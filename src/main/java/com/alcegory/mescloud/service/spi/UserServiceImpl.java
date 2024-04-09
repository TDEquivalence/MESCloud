package com.alcegory.mescloud.service.spi;

import com.alcegory.mescloud.model.converter.UserConverter;
import com.alcegory.mescloud.model.dto.UserConfigDto;
import com.alcegory.mescloud.model.dto.UserDto;
import com.alcegory.mescloud.model.entity.UserEntity;
import com.alcegory.mescloud.model.filter.Filter;
import com.alcegory.mescloud.repository.UserRepository;
import com.alcegory.mescloud.repository.UserRoleRepository;
import com.alcegory.mescloud.security.exception.UserNotFoundException;
import com.alcegory.mescloud.security.mapper.EntityDtoMapper;
import com.alcegory.mescloud.security.model.SectionRoleEntity;
import com.alcegory.mescloud.security.model.auth.AuthenticationResponse;
import com.alcegory.mescloud.security.service.RoleService;
import com.alcegory.mescloud.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.management.relation.RoleNotFoundException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.alcegory.mescloud.security.constant.UserServiceImpConstant.USER_NOT_FOUND_BY_USERNAME;

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

    public UserDto updateUser(UserDto userDto) throws UserNotFoundException, RoleNotFoundException {
        UserEntity dbUserEntity = userRepository.findUserByUsername(userDto.getUsername());

        if (dbUserEntity == null) {
            throw new UserNotFoundException(USER_NOT_FOUND_BY_USERNAME);
        }

        dbUserEntity.setFirstName(userDto.getFirstName());
        dbUserEntity.setLastName(userDto.getLastName());
        dbUserEntity.setEmail(userDto.getEmail());
        dbUserEntity.setPassword(passwordEncoder.encode(userDto.getPassword()));
        dbUserEntity.setUpdatedAt(new Date());

        userRepository.save(dbUserEntity);
        updateSectionRole(dbUserEntity, userDto);
        UserDto userDtoResponse = mapper.convertToDto(dbUserEntity);
        userDtoResponse.setSectionRole(userDto.getSectionRole());
        return userDtoResponse;
    }

    public void updateSectionRole(UserEntity user, UserDto userDto) throws RoleNotFoundException {
        Optional<SectionRoleEntity> sectionRoleOptional = roleService.findByName(userDto.getSectionRole());

        if (sectionRoleOptional.isEmpty()) {
            throw new RoleNotFoundException("Role not found: " + userDto.getSectionRole());
        }

        SectionRoleEntity sectionRole = sectionRoleOptional.get();
        userRoleRepository.updateUserRole(user.getId(), sectionRole.getId(), 1L);
        userDto.setSectionRole(sectionRole.getName());
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

