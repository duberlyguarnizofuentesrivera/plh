package com.duberlyguarnizo.plh.user;

import com.duberlyguarnizo.plh.enums.UserRole;
import com.duberlyguarnizo.plh.enums.UserStatus;
import com.duberlyguarnizo.plh.util.CurrentUserAuditorAware;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.duberlyguarnizo.plh.user.UserRepository.*;
import static org.springframework.data.jpa.domain.Specification.where;

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
    public Page<UserBasicDto> getWithFilters(String search, String status, String role, PageRequest paging) {
        UserRole roleValue;
        UserStatus userStatusValue;
        try {
            roleValue = UserRole.valueOf(role);
        } catch (Exception e) {
            roleValue = null;
        }
        try {
            userStatusValue = UserStatus.valueOf(status);
        } catch (Exception e) {
            userStatusValue = null;
        }
        if (paging == null) {
            paging = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "firstName"));
        }
        return repository.findAll(where(hasRole(roleValue))
                .and(hasStatus(userStatusValue))
                .and(firstNameContains(search).or(lastNameContains(search))), paging).map(mapper::toBasicDto);
    }

    public Optional<UserDto> getById(Long id) {
        Optional<User> user = repository.findById(id);
        return user.map(mapper::toDto);
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


    public boolean update(UserRegisterDto userRegisterDto) {
        Optional<User> user = repository.findById(userRegisterDto.id());
        if (user.isPresent()) { //user does exist, so we update
            User updateUser = user.get();
            mapper.partialUpdate(userRegisterDto, updateUser);
            //update password if needed
            String password = userRegisterDto.password();
            if (password != null && !password.isEmpty()) {
                //if password was on body of request, change it to encoded value
                updateUser.setPassword(encoder.encode(password)); //secure password
            }
            log.info("UserService - Update: User " + updateUser.getUsername().toUpperCase() + " updated successfully!");
            repository.save(updateUser);
            return true;
        } else {
            log.warn("UserService Update: Attempt to update user " + userRegisterDto.username().toUpperCase() + " failed!... no such id: {}", userRegisterDto.id());
            return false;
        }
    }

    public boolean deleteByUserName(String username) {
        final boolean[] result = {true};
        Optional<User> user = repository.findByUsernameIgnoreCase(username);
        user.ifPresentOrElse(value -> repository.deleteById(value.getId()), () -> result[0] = false);
        return result[0];
    }

    public boolean deleteById(Long id) {
        Optional<User> user = repository.findById(id);
        if (user.isPresent()) {
            repository.deleteById(id);
            return true;
        } else {
            log.error("UseService: deleteById(): Error deleting user with id: {}.", id);
            return false;
        }
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
