package com.alcegory.mescloud.service.management;

import com.alcegory.mescloud.api.mqtt.MqttClient;
import com.alcegory.mescloud.constant.MqttDTOConstants;
import com.alcegory.mescloud.exception.ActiveProductionOrderException;
import com.alcegory.mescloud.exception.MesMqttException;
import com.alcegory.mescloud.model.converter.ProductionOrderConverter;
import com.alcegory.mescloud.model.dto.equipment.CountingEquipmentDto;
import com.alcegory.mescloud.model.dto.production.ProductionInstructionDto;
import com.alcegory.mescloud.model.dto.production.ProductionOrderDto;
import com.alcegory.mescloud.model.dto.production.ProductionOrderMqttDto;
import com.alcegory.mescloud.model.entity.equipment.CountingEquipmentEntity;
import com.alcegory.mescloud.model.entity.production.ProductionInstructionEntity;
import com.alcegory.mescloud.model.entity.production.ProductionOrderEntity;
import com.alcegory.mescloud.model.request.RequestProductionOrderDto;
import com.alcegory.mescloud.protocol.MesMqttSettings;
import com.alcegory.mescloud.security.service.UserRoleService;
import com.alcegory.mescloud.service.equipment.CountingEquipmentService;
import com.alcegory.mescloud.service.production.ProductionOrderService;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.alcegory.mescloud.security.model.SectionAuthority.OPERATOR_CREATE;
import static com.alcegory.mescloud.security.model.SectionAuthority.OPERATOR_UPDATE;

@Service
@Log
@AllArgsConstructor
public class ProductionOrderManagementServiceImpl implements ProductionOrderManagementService {

    private final CountingEquipmentService countingEquipmentService;
    private final ProductionOrderService productionOrderService;
    private final UserRoleService userRoleService;

    private final MqttClient mqttClient;
    private final MesMqttSettings mqttSettings;

    private final ProductionOrderConverter converter;

    @Override
    @Transactional
    public Optional<ProductionOrderDto> create(String companyPrefix, String sectionPrefix, long sectionId,
                                               RequestProductionOrderDto productionOrder, Authentication authentication) {
        userRoleService.checkSectionAuthority(authentication, sectionId, OPERATOR_CREATE);
        return create(companyPrefix, sectionPrefix, productionOrder);
    }

    private Optional<ProductionOrderDto> create(String companyPrefix, String sectionPrefix,
                                                RequestProductionOrderDto productionOrder) {
        Optional<CountingEquipmentEntity> countingEquipmentOpt = countingEquipmentService.findEntityById(productionOrder.getEquipmentId());

        if (countingEquipmentOpt.isEmpty()) {
            log.warning(() -> String.format("Unable to create Production Order - no Equipment found with id [%s]",
                    productionOrder.getEquipmentId()));
            return Optional.empty();
        }

        if (productionOrderService.hasEquipmentActiveProductionOrder(productionOrder.getEquipmentId())) {
            throw new ActiveProductionOrderException(String.format("Unable to create Production Order - " +
                    "Equipment with id [%s] still has an " +
                    "uncompleted production order", productionOrder.getEquipmentId()));
        }

        ProductionOrderEntity productionOrderEntity = createProductionOrderEntity(productionOrder, sectionPrefix);
        CountingEquipmentEntity countingEquipmentEntity = countingEquipmentOpt.get();
        updateProductionOrderEntity(productionOrderEntity, countingEquipmentEntity);

        try {
            publishToPlc(companyPrefix, sectionPrefix, productionOrderEntity);
        } catch (MesMqttException e) {
            log.severe("Unable to publish Production Order over MQTT.");
            return Optional.empty();
        }

        return Optional.of(converter.toDto(productionOrderService.save(productionOrderEntity)));
    }

    private ProductionOrderEntity createProductionOrderEntity(RequestProductionOrderDto productionOrder, String sectionPrefix) {
        ProductionOrderEntity productionOrderEntity = converter.toEntity(productionOrder);
        productionOrderEntity.setCreatedAt(new Date());
        productionOrderEntity.setCompleted(false);

        String sectionPrefixUpper = sectionPrefix.toUpperCase();
        productionOrderEntity.setCode(productionOrderService.generateCode(sectionPrefixUpper));

        List<ProductionInstructionEntity> instructions = converter.toEntityList(productionOrder.getInstructions());
        productionOrderEntity.setProductionInstructions(instructions);
        return productionOrderEntity;
    }

    private void updateProductionOrderEntity(ProductionOrderEntity productionOrderEntity, CountingEquipmentEntity countingEquipmentEntity) {
        productionOrderEntity.setEquipment(countingEquipmentEntity);
        productionOrderEntity.setIms(countingEquipmentEntity.getIms());
        countingEquipmentEntity.setId(productionOrderEntity.getEquipment().getId());
        countingEquipmentService.setOperationStatus(countingEquipmentEntity, CountingEquipmentEntity.OperationStatus.IN_PROGRESS);
    }

    private void publishToPlc(String companyPrefix, String sectionPrefix, ProductionOrderEntity productionOrderEntity) throws MesMqttException {
        ProductionOrderMqttDto productionOrderMqttDto = converter.toMqttDto(productionOrderEntity, true);
        String topic = mqttSettings.getPlcTopicByCompanyAndSection(companyPrefix, sectionPrefix);
        mqttClient.publish(topic, productionOrderMqttDto);
    }

    @Override
    @Transactional
    public Optional<ProductionOrderDto> complete(String companyPrefix, String sectionPrefix, long sectionId, long equipmentId,
                                                 Authentication authentication) {
        userRoleService.checkSectionAuthority(authentication, sectionId, OPERATOR_UPDATE);
        log.info(() -> String.format("Complete process Production Order started for equipmentId [%s]:", equipmentId));

        Optional<CountingEquipmentEntity> countingEquipmentOpt = countingEquipmentService.findEntityById(equipmentId);
        if (countingEquipmentOpt.isEmpty()) {
            log.warning(() -> String.format("Unable to find Equipment with id [%s]", equipmentId));
            return Optional.empty();
        }

        CountingEquipmentDto countingEquipmentDto = countingEquipmentService.setOperationStatus(countingEquipmentOpt.get(),
                CountingEquipmentEntity.OperationStatus.PENDING);
        log.info(() -> String.format("Change status to PENDING for Equipment with code [%s]", countingEquipmentDto.getCode()));

        Optional<ProductionOrderEntity> productionOrderEntityOpt = productionOrderService.findActiveByEquipmentId(equipmentId);
        if (productionOrderEntityOpt.isEmpty()) {
            log.warning(() -> String.format("No active Production Order found for Equipment with id [%s]", equipmentId));
            return Optional.empty();
        }

        publishProductionOrderCompletion(companyPrefix, sectionPrefix, countingEquipmentOpt.get(), productionOrderEntityOpt.get());
        log.info(() -> String.format("Production Order Conclusion already publish for equipmentId [%s]:", equipmentId));

        ProductionOrderEntity productionOrder = productionOrderEntityOpt.get();
        ProductionOrderDto productionOrderDto = converter.toDto(productionOrder);
        productionOrderDto.setCompletedAt(new Date());
        return Optional.of(productionOrderDto);
    }

    public void publishProductionOrderCompletion(String companyPrefix, String sectionPrefix,
                                                 CountingEquipmentEntity countingEquipment, ProductionOrderEntity productionOrder) {
        try {
            publishProductionOrderCompletionToPLC(companyPrefix, sectionPrefix, countingEquipment, productionOrder);
        } catch (MesMqttException e) {
            log.severe(() -> String.format("Unable to publish Order Completion to PLC for equipment [%s]",
                    countingEquipment.getCode()));
        }
    }

    private void publishProductionOrderCompletionToPLC(String companyPrefix, String sectionPrefix,
                                                       CountingEquipmentEntity countingEquipment, ProductionOrderEntity productionOrder)
            throws MesMqttException {

        ProductionOrderMqttDto productionOrderMqttDto = new ProductionOrderMqttDto();
        productionOrderMqttDto.setJsonType(MqttDTOConstants.PRODUCTION_ORDER_CONCLUSION_DTO_NAME);
        productionOrderMqttDto.setEquipmentEnabled(false);
        productionOrderMqttDto.setProductionOrderCode(productionOrder.getCode());
        productionOrderMqttDto.setTargetAmount(0);
        productionOrderMqttDto.setEquipmentCode(countingEquipment.getCode());

        String topic = mqttSettings.getPlcTopicByCompanyAndSection(companyPrefix, sectionPrefix);
        mqttClient.publish(topic, productionOrderMqttDto);
    }

    @Override
    @Transactional
    public ProductionOrderDto editProductionOrder(ProductionOrderDto requestProductionOrder, Authentication authentication,
                                                  long sectionId) {
        userRoleService.checkSectionAuthority(authentication, sectionId, OPERATOR_UPDATE);

        if (requestProductionOrder == null) {
            log.warning("Null request Production Order received for editing production order.");
            return null;
        }

        Optional<ProductionOrderEntity> persistedProductionOrderOpt = productionOrderService.findById(requestProductionOrder.getId());

        if (persistedProductionOrderOpt.isEmpty()) {
            log.warning("No production order found for ID: " + requestProductionOrder.getId());
            return null;
        }

        ProductionOrderEntity productionOrderToUpdate = persistedProductionOrderOpt.get();
        updateProductionInstructions(requestProductionOrder, productionOrderToUpdate);
        ProductionOrderEntity persistedProductionOrder = productionOrderService.save(productionOrderToUpdate);
        return converter.toDto(persistedProductionOrder);
    }

    private void updateProductionInstructions(ProductionOrderDto requestProductionOrder, ProductionOrderEntity productionOrderToUpdate) {
        List<ProductionInstructionDto> requestInstructions = requestProductionOrder.getInstructions();
        List<ProductionInstructionEntity> existingInstructions = productionOrderToUpdate.getProductionInstructions();

        if (requestInstructions == null || existingInstructions == null) {
            return;
        }

        for (ProductionInstructionDto requestInstruction : requestInstructions) {
            for (ProductionInstructionEntity existingInstruction : existingInstructions) {
                if (requestInstruction.getName().equals(existingInstruction.getName())) {
                    existingInstruction.setValue(requestInstruction.getValue());
                    break;
                }
            }
        }
    }
}
