package com.alcegory.mescloud.service.spi;

import com.alcegory.mescloud.exception.UserUpdateException;
import com.alcegory.mescloud.model.converter.UserConverter;
import com.alcegory.mescloud.model.dto.UserConfigDto;
import com.alcegory.mescloud.model.dto.UserDto;
import com.alcegory.mescloud.model.entity.UserEntity;
import com.alcegory.mescloud.model.filter.Filter;
import com.alcegory.mescloud.repository.UserRepository;
import com.alcegory.mescloud.repository.UserRoleRepository;
import com.alcegory.mescloud.security.mapper.EntityDtoMapper;
import com.alcegory.mescloud.security.model.SectionRoleEntity;
import com.alcegory.mescloud.security.model.UserRoleEntity;
import com.alcegory.mescloud.security.model.auth.AuthenticationResponse;
import com.alcegory.mescloud.security.service.RoleService;
import com.alcegory.mescloud.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.management.relation.RoleNotFoundException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final EntityDtoMapper mapper;
    private final UserConverter userConverter;
    private final RoleService roleService;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserDto getUserById(Long id) {
        UserEntity userEntity = userRepository.findUserById(id);
        return mapper.convertToDto(userEntity);
    }

    public List<UserDto> getFilteredUsers() {
        List<UserEntity> userEntityList = userRepository.findAll();
        return mapper.convertToDto(userEntityList);
    }

    public List<UserDto> getFilteredUsers(Filter filter) {
        List<UserEntity> userEntityList = userRepository.getFilteredUsers(filter);
        return mapper.convertToDto(userEntityList);
    }

    @Transactional(rollbackFor = Exception.class)
    public UserDto updateUser(UserDto userDto) throws UserUpdateException, RoleNotFoundException {
        if (userDto == null) {
            throw new IllegalArgumentException("UserDto cannot be null.");
        }

        UserEntity dbUserEntity = userRepository.findUserByUsername(userDto.getUsername());

        if (dbUserEntity == null) {
            throw new UserUpdateException("User not found with username: " + userDto.getUsername());
        }

        try {
            updateUserData(dbUserEntity, userDto);
            updateSectionRole(dbUserEntity, userDto);

            userRepository.save(dbUserEntity);

            UserDto userResponseDto = mapper.convertToDto(dbUserEntity);
            userResponseDto.setSectionRole(userDto.getSectionRole());
            return userResponseDto;
        } catch (Exception ex) {
            log.error("An error occurred while updating user with username: " + userDto.getUsername(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new UserUpdateException("Failed to update user: " + ex.getMessage(), ex);
        }
    }

    private void updateUserData(UserEntity dbUserEntity, UserDto userDto) {
        dbUserEntity.setFirstName(userDto.getFirstName());
        dbUserEntity.setLastName(userDto.getLastName());
        dbUserEntity.setRole(userDto.getRole());
        dbUserEntity.setEmail(userDto.getEmail());
        setPassword(dbUserEntity, userDto);
        dbUserEntity.setUpdatedAt(new Date());
    }

    private void setPassword(UserEntity dbUserEntity, UserDto userDto) {
        String password = userDto.getPassword();
        if (password != null && !password.isEmpty()) {
            dbUserEntity.setPassword(passwordEncoder.encode(password));
        }
    }

    public void updateSectionRole(UserEntity user, UserDto userDto) throws RoleNotFoundException {
        //TODO: receiving sectionsID
        long sectionId = 1L; // Assuming sectionId is always 1L

        Optional<SectionRoleEntity> sectionRoleOptional = roleService.findByName(userDto.getSectionRole());

        if (sectionRoleOptional.isEmpty()) {
            throw new RoleNotFoundException("Role not found: " + userDto.getSectionRole());
        }

        SectionRoleEntity sectionRole = sectionRoleOptional.get();
        Optional<UserRoleEntity> existingUserRoleOptional =
                Optional.ofNullable(userRoleRepository.findByUserIdAndSectionId(user.getId(), sectionId));

        if (existingUserRoleOptional.isPresent()) {
            UserRoleEntity existingUserRole = existingUserRoleOptional.get();
            userRoleRepository.delete(existingUserRole); // Delete existing user role
            log.info("User role deleted successfully. User ID: {}, Role ID: {}, Section ID: {}", user.getId(), existingUserRole.getRoleId(), sectionId);
        }

        // Create new user role
        UserRoleEntity newUserRole = new UserRoleEntity();
        newUserRole.setUserId(user.getId());
        newUserRole.setRoleId(sectionRole.getId());
        newUserRole.setSectionId(sectionId);
        userRoleRepository.save(newUserRole);
        log.info("New user role created. User ID: {}, Role ID: {}, Section ID: {}", user.getId(), sectionRole.getId(), sectionId);
    }


    public UserConfigDto getUserConfigByAuth(AuthenticationResponse authenticateRequest) {
        if (authenticateRequest == null || authenticateRequest.getUsername() == null) {
            return null;
        }

        UserEntity userEntity = userRepository.findUserByUsername(authenticateRequest.getUsername());
        return userConverter.convertToDtoWithRelatedEntities(userEntity);
    }

    public UserEntity getUserByAuth(AuthenticationResponse authenticateRequest) {
        if (authenticateRequest == null || authenticateRequest.getUsername() == null) {
            return null;
        }

        return userRepository.findUserByUsername(authenticateRequest.getUsername());
    }

    @Override
    public void deleteUser(UserDto user) {
        UserEntity userEntity = mapper.convertToEntity(user);
        userRepository.delete(userEntity);
    }
}

