package com.alcegory.mescloud.service.export;

import java.util.HashMap;
import java.util.Map;

public abstract class ExcelConstants {

    public static final String SHEET_NAME_PRODUCTION_ORDERS = "Ordens de Produção";
    public static final String SHEET_NAME_COMPOSED = "Produções Compostas";
    public static final String TABLE_NAME_PRODUCTION = "ProductionOrdersTable";
    public static final String TABLE_NAME_COMPOSED = "ComposedProductionOrdersTable";
    public static final String TABLE_STYLE = "TableStyleMedium9";

    private ExcelConstants() {
    }

    public static Map<String, String> translateFieldNamesMapping() {
        Map<String, String> mapping = new HashMap<>();
        mapping.put("batchCode", "Lote Final");
        mapping.put("composedCode", "Produção Composta");
        mapping.put("composedCreatedAt", "Criação da Composta");
        mapping.put("validAmount", "Quantidade");
        mapping.put("isBatchApproved", "Aprovado");
        mapping.put("approvedAt", "Aprovado em");
        mapping.put("amountOfHits", "Hits");
        mapping.put("hitInsertedAt", "Hits inseridos em");
        mapping.put("sampleAmount", "Amostra");
        mapping.put("reliability", "Fiabilidade");
        mapping.put("equipment", "Equipamento");
        mapping.put("productionCode", "Ordem de Produção");
        mapping.put("ims", "IMS");
        mapping.put("productionCreatedAt", "Início de Produção");
        mapping.put("productionCompletedAt", "Conclusão de Produção");
        return mapping;
    }
}
