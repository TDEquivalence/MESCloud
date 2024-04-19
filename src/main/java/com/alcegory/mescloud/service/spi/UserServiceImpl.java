package com.alcegory.mescloud.service.spi;

import com.alcegory.mescloud.exception.DeleteUserException;
import com.alcegory.mescloud.exception.ForbiddenAccessException;
import com.alcegory.mescloud.exception.UserUpdateException;
import com.alcegory.mescloud.model.converter.UserConverter;
import com.alcegory.mescloud.model.dto.SectionRoleMapping;
import com.alcegory.mescloud.model.dto.UserConfigDto;
import com.alcegory.mescloud.model.dto.UserDto;
import com.alcegory.mescloud.model.entity.UserEntity;
import com.alcegory.mescloud.model.filter.Filter;
import com.alcegory.mescloud.repository.UserRepository;
import com.alcegory.mescloud.security.mapper.EntityDtoMapper;
import com.alcegory.mescloud.security.model.SectionRoleEntity;
import com.alcegory.mescloud.security.model.UserRoleEntity;
import com.alcegory.mescloud.security.model.auth.AuthenticationResponse;
import com.alcegory.mescloud.security.repository.UserRoleRepository;
import com.alcegory.mescloud.security.service.RoleService;
import com.alcegory.mescloud.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.management.relation.RoleNotFoundException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.alcegory.mescloud.security.model.Authority.ADMIN_DELETE;
import static com.alcegory.mescloud.security.utility.AuthorityUtil.checkUserAndAuthority;

@Slf4j
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final EntityDtoMapper mapper;
    private final UserConverter userConverter;
    private final RoleService sectionRoleService;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDto getUserDtoById(Long id) {
        UserEntity userEntity = userRepository.findUserById(id);
        return mapper.convertToDto(userEntity);
    }

    @Override
    public UserEntity getUserById(Long id) {
        return userRepository.findUserById(id);
    }

    @Override
    public List<UserDto> getDtoUsers() {
        List<UserEntity> userEntityList = userRepository.findAll();
        return mapper.convertToDto(userEntityList);
    }

    @Override
    public List<UserEntity> getUsers() {
        return userRepository.findAll();
    }

    @Override
    public List<UserDto> getDtoUsers(Filter filter) {
        List<UserEntity> userEntityList = userRepository.getFilteredUsers(filter);
        return mapper.convertToDto(userEntityList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserDto updateUser(UserDto userDto) throws UserUpdateException {
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
            userResponseDto.setSectionRoles(userDto.getSectionRoles());
            return userResponseDto;
        } catch (Exception ex) {
            log.error("An error occurred while updating user with username: " + userDto.getUsername(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new UserUpdateException("Failed to update user: " + ex.getMessage(), ex);
        }
    }

    private void updateUserData(UserEntity dbUserEntity, UserDto userDto) {
        if (userDto.getFirstName() != null) {
            dbUserEntity.setFirstName(userDto.getFirstName());
        }
        if (userDto.getLastName() != null) {
            dbUserEntity.setLastName(userDto.getLastName());
        }
        if (userDto.getRole() != null) {
            dbUserEntity.setRole(userDto.getRole());
        }
        if (userDto.getEmail() != null) {
            dbUserEntity.setEmail(userDto.getEmail());
        }
        setPassword(dbUserEntity, userDto);
        dbUserEntity.setUpdatedAt(new Date());
    }

    private void setPassword(UserEntity dbUserEntity, UserDto userDto) {
        String password = userDto.getPassword();
        if (password != null && !password.isEmpty()) {
            dbUserEntity.setPassword(passwordEncoder.encode(password));
        }
    }

    private void updateSectionRole(UserEntity user, UserDto userDto) throws RoleNotFoundException {
        List<SectionRoleMapping> sectionRoles = userDto.getSectionRoles();

        if (sectionRoles != null && !sectionRoles.isEmpty()) {
            for (SectionRoleMapping sectionRoleMapping : sectionRoles) {
                SectionRoleEntity sectionRole = sectionRoleService.findByName(sectionRoleMapping.getSectionRole())
                        .orElseThrow(() -> new RoleNotFoundException("Role not found: " + sectionRoleMapping.getSectionRole()));

                long sectionId = sectionRoleMapping.getSectionId();

                UserRoleEntity existingUserRole = userRoleRepository.findByUserIdAndSectionId(user.getId(), sectionId);
                if (existingUserRole != null) {
                    userRoleRepository.delete(existingUserRole);
                    log.info("User role deleted successfully. User ID: {}, Role ID: {}, Section ID: {}", user.getId(), existingUserRole.getRoleId(), sectionId);
                }

                UserRoleEntity newUserRole = new UserRoleEntity();
                newUserRole.setUserId(user.getId());
                newUserRole.setRoleId(sectionRole.getId());
                newUserRole.setSectionId(sectionId);
                userRoleRepository.save(newUserRole);
                log.info("New user role created. User ID: {}, Role ID: {}, Section ID: {}", user.getId(), sectionRole.getId(), sectionId);
            }
        }
    }

    @Override
    public UserConfigDto getUserConfigByAuth(Authentication authentication) {
        UserEntity user = (UserEntity) authentication.getPrincipal();
        if (user == null) {
            return null;
        }

        return processGetUserConfigByAuth(user);
    }

    @Override
    public UserConfigDto getUserConfigByAuth(UserEntity user) {
        if (user == null) {
            return null;
        }

        return processGetUserConfigByAuth(user);
    }

    private UserConfigDto processGetUserConfigByAuth(UserEntity user) {
        UserEntity userEntity = userRepository.findUserByUsername(user.getUsername());
        return userConverter.convertToDtoWithRelatedEntities(userEntity);
    }

    @Override
    public UserEntity getUserByAuth(AuthenticationResponse authenticateRequest) {
        if (authenticateRequest == null || authenticateRequest.getUsername() == null) {
            return null;
        }

        return userRepository.findUserByUsername(authenticateRequest.getUsername());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(UserDto user, Authentication authentication) {
        try {
            checkUserAndAuthority(authentication, ADMIN_DELETE);
            UserEntity userEntity = mapper.convertToEntity(user);
            deleteUserRolesByUserId(userEntity.getId());
            userRepository.delete(userEntity);
        } catch (ForbiddenAccessException e) {
            log.error("User is not authorized to delete: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to delete user: {}", e.getMessage());
            throw new DeleteUserException("Failed to delete user", e);
        }
    }

    private void deleteUserRolesByUserId(Long userId) {
        List<UserRoleEntity> userRoles = userRoleRepository.findByUserId(userId);
        userRoleRepository.deleteAll(userRoles);
    }

    @Override
    public Optional<UserEntity> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}

