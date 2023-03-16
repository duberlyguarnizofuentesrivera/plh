package com.duberlyguarnizo.plh.user;

import com.duberlyguarnizo.plh.enums.UserRole;
import com.duberlyguarnizo.plh.enums.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    static Specification<User> firstNameContains(String name) {
        if (name == null) {
            return null;
        } else {
            return (user, query, cb) -> cb.like(cb.lower(user.get("firstName")), "%" + name.toLowerCase() + "%");
        }
    }

    static Specification<User> lastNameContains(String lastName) {
        if (lastName == null) {
            return null;
        } else {
            return (user, query, cb) -> cb.like(cb.lower(user.get("lastName")), "%" + lastName.toLowerCase() + "%");
        }
    }

    static Specification<User> hasRole(UserRole role) {
        if (role == null) {
            return null;
        } else {
            return (user, query, cb) -> cb.equal(user.get("role"), role);
        }
    }

    static Specification<User> hasStatus(UserStatus status) {
        if (status == null) {
            return null;
        } else {
            return (user, query, cb) -> cb.equal(user.get("status"), status);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    Optional<User> findByUsernameIgnoreCase(String username);

    Page<User> findByStatus(UserStatus systemUserStatus, Pageable pageable);

    Page<User> findByRole(UserRole systemUserRole, Pageable pageable);

    Page<User> findByStatusAndRole(UserStatus status, UserRole role, Pageable pageable);

    Page<User> findByFirstNameContainsIgnoreCaseOrLastNameContainsIgnoreCase(String search, String search2, Pageable pageable);

    Page<User> findByRoleAndStatusAndFirstNameContainsIgnoreCaseOrLastNameNotContainsIgnoreCase(UserRole role, UserStatus status, String firstName, String lastName, Pageable pageable);

    Page<User> findByStatusAndFirstNameContainsIgnoreCaseOrLastNameContainsIgnoreCase(UserStatus statusValue, String search, String search1, Pageable pageable);

    Page<User> findByRoleAndFirstNameContainsIgnoreCaseOrLastNameContainsIgnoreCase(UserRole roleValue, String search, String search1, Pageable pageable);
}
