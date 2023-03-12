package com.duberlyguarnizo.plh.user;

import com.duberlyguarnizo.plh.enums.UserRole;
import com.duberlyguarnizo.plh.enums.UserStatus;
import com.duberlyguarnizo.plh.util.CurrentUserAuditorAware;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class UserService {
    private final UserRepository repository;
    private final PasswordEncoder encoder;
    private final UserMapper mapper = Mappers.getMapper(UserMapper.class);
    private final CurrentUserAuditorAware auditorAware;

    @Autowired
    public UserService(UserRepository repository, PasswordEncoder encoder, CurrentUserAuditorAware auditorAware) {
        this.repository = repository;
        this.encoder = encoder;
        this.auditorAware = auditorAware;
    }

    //-------CRUD methods-----------------------------------------
    public Page<UserBasicDto> getAll(Integer page, Integer size) {
        Page<User> userList = repository.findAll(PageRequest.of(page - 1, size));
        return userList.map(mapper::toBasicDto);
    }

    public Optional<UserBasicDto> getById(Long id) {
        Optional<User> user = repository.findById(id);
        return user.map(mapper::toBasicDto);
    }

    public Page<UserBasicDto> getByRole(UserRole role, Integer page, Integer size) {
        Page<User> userList = repository.findByRole(role, PageRequest.of(page - 1, size));
        return userList.map(mapper::toBasicDto);
    }

    public Page<UserBasicDto> getByStatus(UserStatus status, Integer page, Integer size) {
        Page<User> userList = repository.findByStatus(status, PageRequest.of(page - 1, size));
        return userList.map(mapper::toBasicDto);
    }

    public boolean save(UserRegisterDto userRegisterDto) {
        Optional<User> user = repository.findByUsernameIgnoreCase(userRegisterDto.username());
        if (user.isEmpty()) { //user doesn't exist
            User newUser = mapper.toRegisterEntity(userRegisterDto);
            newUser.setPassword(encoder.encode(userRegisterDto.password())); //secure password
            repository.save(newUser);
            log.info("UserService - Save: User " + newUser.getUsername().toUpperCase() + " created successfully!");
            return true;
        }
        log.warn("UserService: Attempt to save user " + userRegisterDto.username().toUpperCase() + " failed!");
        return false;
    }

    public boolean update(UserRegisterDto userRegisterDto, boolean createIfNotFound) {
        Optional<User> user = repository.findByUsernameIgnoreCase(userRegisterDto.username());
        if (user.isPresent()) { //user does exist, so we update
            User updateUser = user.get();
            updateUser = mapper.partialRegisterUpdate(userRegisterDto, updateUser);
            String password = userRegisterDto.password();
            if (password != null && !password.isEmpty()) {
                //if password was on body of request, change it to encoded value
                updateUser.setPassword(encoder.encode(password)); //secure password
            }
            log.info("UserService - Update: User " + updateUser.getUsername().toUpperCase() + " updated successfully!");
            repository.save(updateUser);
            return true;
        } else {
            if (createIfNotFound) {
                save(userRegisterDto);//if not exists, save the DTO
                log.info("UserService - Update: User " + userRegisterDto.username().toUpperCase() + " not found, can't update. Trying to create instead.");
                return true;
            }
        }
        log.warn("UserService Update: Attempt to update user " + userRegisterDto.username().toUpperCase() + " failed!");
        return false;
    }

    public boolean deleteByUserName(String username) {
        final boolean[] result = {true};
        Optional<User> user = repository.findByUsernameIgnoreCase(username);
        user.ifPresentOrElse(value -> repository.deleteById(value.getId()), () -> result[0] = false);
        return result[0];
    }

    //-----End of CRUD ---------------------------------------
    public Optional<UserDto> getByUsername(String username) {
        Optional<User> user = repository.findByUsernameIgnoreCase(username);
        if (user.isPresent()) {
            User resultUser = user.get();
            UserDto resultUserDto = mapper.toDto(resultUser);
            return Optional.of(resultUserDto);
        }
        return Optional.empty();
    }

    public Optional<UserBasicDto> getCurrentUser() {
        Optional<User> currentUser = auditorAware.getCurrentAuditor();
        if (currentUser.isEmpty()) {
            log.warn("User Service - GetCurrentUser: Current auditor requested, but no user currently logged in!");
            return Optional.empty();
        } else {
            return Optional.of(mapper.toBasicDto(currentUser.get()));
        }
    }

    public Page<UserBasicDto> getByStatusAndRole(UserStatus userStatus, UserRole userRole, Integer page, Integer size) {
        Page<User> result = repository.findByStatusAndRole(userStatus, userRole, PageRequest.of(page - 1, size));
        return result.map(mapper::toBasicDto);
    }


    public Page<UserBasicDto> getWithFilters(String userStatus, String userRole, String search, Integer page, Integer size) {
        UserRole roleValue;
        UserStatus statusValue;
        Page<User> result;
        statusValue = "all".equals(userStatus) ? null : UserStatus.valueOf(userStatus);
        roleValue = "all".equals(userRole) ? null : UserRole.valueOf(userRole);
        if (statusValue == null && roleValue == null) {
            log.warn("case: rol and status null");
            if (search.isEmpty()) {
                result = repository.findAll(PageRequest.of(page - 1, size));
            } else {
                result = repository.findByFirstNameContainsIgnoreCaseOrLastNameContainsIgnoreCase(search, search, PageRequest.of(page - 1, size));
            }
        } else {
            result = iterateFilters(search, page, size, roleValue, statusValue);
        }
        return result.map(mapper::toBasicDto);
    }


    public boolean verifyCurrentUserPassword(String oldPassword) {
        Optional<User> currentUser = auditorAware.getCurrentAuditor();
        if (currentUser.isEmpty()) {
            log.warn("User Service - VerifyPassword: Current auditor requested, but no user currently logged in!");
            return false;
        } else {
            return encoder.matches(oldPassword, currentUser.get().getPassword());
        }
    }

    public boolean changeCurrentUserPassword(String newPassword) {
        Optional<User> currentUser = auditorAware.getCurrentAuditor();
        return changePasswordForAnyUser(newPassword, currentUser);
    }


    public boolean changeOtherUserPassword(String userName, String newPassword) {
        Optional<User> userToChangePassword = repository.findByUsernameIgnoreCase(userName);
        return changePasswordForAnyUser(newPassword, userToChangePassword);
    }

    //-------------Utility methods--------------------------------
    private Page<User> iterateFilters(String search, Integer page, Integer size, UserRole roleValue, UserStatus statusValue) {
        Page<User> result;
        if (statusValue != null && roleValue != null) {
            if (search.isEmpty()) {
                result = repository.findByStatusAndRole(statusValue, roleValue, PageRequest.of(page - 1, size));
            } else {
                result = repository.findByRoleAndStatusAndFirstNameContainsIgnoreCaseOrLastNameNotContainsIgnoreCase(roleValue, statusValue, search, search, PageRequest.of(page - 1, size));
            }
        } else if (statusValue != null) {
            if (search.isEmpty()) {
                result = repository.findByStatus(statusValue, PageRequest.of(page - 1, size));
            } else {
                result = repository.findByStatusAndFirstNameContainsIgnoreCaseOrLastNameContainsIgnoreCase(statusValue, search, search, PageRequest.of(page - 1, size));
            }
        } else if (roleValue != null) {
            if (search.isEmpty()) {
                result = repository.findByRole(roleValue, PageRequest.of(page - 1, size));
            } else {
                result = repository.findByRoleAndFirstNameContainsIgnoreCaseOrLastNameContainsIgnoreCase(roleValue, search, search, PageRequest.of(page - 1, size));
            }
        } else {
            throw new IllegalArgumentException("Both statusValue and roleValue cannot be null.");
        }
        return result;
    }

    private boolean changePasswordForAnyUser(String newPassword, Optional<User> currentUser) {
        if (currentUser.isEmpty()) {
            log.warn("User Service - ChangePassword: Current auditor requested, but no user currently logged in!");
            return false;
        } else {

            currentUser.get().setPassword(encoder.encode(newPassword));
            repository.save(currentUser.get());
            log.info("User Service - ChangePassword: User " + currentUser.get().getUsername().toUpperCase() + " password changed successfully!");
            return true;
        }
    }

    public boolean setStatus(String username) {
        if (username.isEmpty()) {
            return false;
        } else {
            Optional<User> user = repository.findByUsernameIgnoreCase(username);
            if (user.isPresent()) {
                User currentUser = user.get();
                currentUser.setStatus(currentUser.getStatus() == UserStatus.INACTIVE ? UserStatus.ACTIVE : UserStatus.INACTIVE);
                repository.save(currentUser);
                log.info("User Service - setStatus: User " + currentUser.getUsername().toUpperCase() + " status changed successfully!");
                return true;
            } else {
                //no user with username
                return false;
            }
        }
    }
}
