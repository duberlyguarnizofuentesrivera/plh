package com.duberlyguarnizo.plh.user;

import com.duberlyguarnizo.plh.enums.EntityType;
import com.duberlyguarnizo.plh.enums.EventType;
import com.duberlyguarnizo.plh.enums.UserRole;
import com.duberlyguarnizo.plh.enums.UserStatus;
import com.duberlyguarnizo.plh.event.Event;
import com.duberlyguarnizo.plh.event.EventRepository;
import com.duberlyguarnizo.plh.util.CurrentUserAuditorAware;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.duberlyguarnizo.plh.user.UserRepository.*;
import static org.springframework.data.jpa.domain.Specification.where;

@Service
@Slf4j
public class UserService {
    private final UserRepository repository;
    private final EventRepository eventRepository;
    private final PasswordEncoder encoder;
    private final UserMapper mapper = Mappers.getMapper(UserMapper.class);
    private final CurrentUserAuditorAware auditorAware;

    @Autowired
    public UserService(UserRepository repository, EventRepository eventRepository, PasswordEncoder encoder, CurrentUserAuditorAware auditorAware) {
        this.repository = repository;
        this.eventRepository = eventRepository;
        this.encoder = encoder;
        this.auditorAware = auditorAware;
    }

    //-------CRUD methods-----------------------------------------
    public Page<UserBasicDto> getWithFilters(String search, String status, String role, PageRequest paging) throws IllegalArgumentException {
        //TODO: implement search string to be firstName + Lastname
        UserRole roleValue;
        UserStatus userStatusValue;
        try {
            roleValue = UserRole.valueOf(role);
        } catch (Exception e) {
            if ("all".equals(role)) {
                roleValue = null;
            } else {
                throw new IllegalArgumentException();
            }
        }
        try {
            userStatusValue = UserStatus.valueOf(status);
        } catch (Exception e) {
            if ("all".equals(status)) {
                userStatusValue = null;
            } else {
                throw new IllegalArgumentException();
            }
        }
        if (paging == null) {
            paging = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "firstName"));
        }
        if (!search.isEmpty()) {
            log.warn("userService: getWithFilters(): search string is not empty");
            return repository.findAll(where(hasRole(roleValue))
                    .and(hasStatus(userStatusValue))
                    .and(firstNameContains(search).or(lastNameContains(search))), paging).map(mapper::toBasicDto);
        } else {
            log.warn("userService: getWithFilters(): search string is empty");

            var result = repository.findAll(where(hasRole(roleValue))
                    .and(hasStatus(userStatusValue)), paging);
            return result.map(mapper::toBasicDto);
        }

    }

    public Optional<UserDto> getById(Long id) {
        Optional<User> user = repository.findById(id);
        return user.map(mapper::toDto);
    }

    @PreAuthorize("isAuthenticated()")
    public boolean save(UserDetailDto userDetailDto) {
        Optional<User> user = repository.findByUsernameIgnoreCase(userDetailDto.getUsername());
        Event event;
        if (user.isEmpty()) { //user doesn't exist
            User newUser = mapper.toEntity(userDetailDto);
            newUser.setPassword(encoder.encode(userDetailDto.getPassword())); //secure password
            User result = repository.save(newUser);
            event = Event.builder()
                    .entityId(result.getId())
                    .entityType(EntityType.ENTITY_USER)
                    .eventType(EventType.NEW_USER)
                    .build();
            eventRepository.save(event);
            log.info("UserService - Save: User " + newUser.getUsername().toUpperCase() + " created successfully!");
            return true;
        }
        log.warn("UserService: Attempt to save user " + userDetailDto.getUsername().toUpperCase() + " failed!");
        return false;
    }


    public boolean update(UserDetailDto userDetailDto) {
        Optional<User> currentUser = auditorAware.getCurrentAuditor();
        Optional<User> user = repository.findById(userDetailDto.getId());
        if (user.isPresent()) { //user does exist, so we update
            User updateUser = user.get();
            mapper.partialUpdate(userDetailDto, updateUser);
            //update password if needed
            if (currentUser.isPresent()) {
                var currentExistingUser = currentUser.get();
                if (currentExistingUser.getRole() == UserRole.ADMIN) {
                    changePassword(userDetailDto, updateUser);
                } else {
                    //If not admin, only change password for current user
                    if (currentExistingUser.getUsername().equals(userDetailDto.getUsername()) && verifyCurrentUserPassword(userDetailDto.getPassword())) {
                        changePassword(userDetailDto, updateUser);
                    }
                }
                eventRepository.save(Event.builder()
                        .entityId(updateUser.getId())
                        .entityType(EntityType.ENTITY_USER)
                        .eventType(EventType.USER_PROFILE_CHANGE)
                        .build());
            }

            log.info("UserService - Update: User " + updateUser.getUsername().toUpperCase() + " updated successfully!");
            repository.save(updateUser);
            return true;
        } else {
            log.warn("UserService Update: Attempt to update user " + userDetailDto.getUsername().toUpperCase() + " failed!... no such id: {}", userDetailDto.getId());
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
            String userNames = user.get().getFirstName() + " " + user.get().getLastName();
            repository.deleteById(id);
            eventRepository.save(Event.builder()
                    .entityType(EntityType.DELETED_ENTITY)
                    .eventType(EventType.USER_DELETED)
                    .referer(userNames)
                    .build());
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


    //-------------Utility methods--------------------------------
    private void changePassword(UserDetailDto userDetailDto, User updateUser) {
        String password = userDetailDto.getPassword();
        if (password != null && !password.isEmpty()) {
            //if password was on body of request, change it to encoded value
            updateUser.setPassword(encoder.encode(password)); //secure password
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


}
