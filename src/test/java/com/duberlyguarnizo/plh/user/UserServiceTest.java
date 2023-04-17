package com.duberlyguarnizo.plh.user;


import com.duberlyguarnizo.plh.enums.UserRole;
import com.duberlyguarnizo.plh.enums.UserStatus;
import com.duberlyguarnizo.plh.util.PlhException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    User user1, user2, user3;
    @Mock
    private UserRepository repository;
    @Mock
    private UserMapper mapper;
    @Autowired
    private UserService service;

    // Set up the mock specifications to return true for any User

    @BeforeEach
    public void setUp() {
//        MockitoAnnotations.openMocks(this)
        user1 = User.builder()
                .id(1L)
                .username("jhondoe")
                .password("password")
                .email("jhondoe@yahoo.com")
                .firstName("John")
                .lastName("Doe")
                .idNumber("12345678")
                .phone("12345678")
                .status(UserStatus.ACTIVE)
                .role(UserRole.ADMIN)
                .build();
        user2 = User.builder()
                .id(2L)
                .username("marydoe")
                .password("password")
                .email("marydoe@yahoo.com")
                .firstName("Mary")
                .lastName("Doe")
                .idNumber("87645321")
                .phone("987654321")
                .status(UserStatus.INACTIVE)
                .role(UserRole.SUPERVISOR)
                .build();
        user3 = User.builder()
                .id(3L)
                .username("petersmith")
                .password("password")
                .email("peter.smith@gmail.com")
                .firstName("Peter")
                .lastName("Smith")
                .idNumber("78465321")
                .phone("963852741")
                .phone2("028748879")
                .status(UserStatus.ACTIVE)
                .role(UserRole.TRANSPORTER)
                .build();

    }


    @Test
    @DisplayName("UserService: getWithFilters throws exception with invalid arguments")
    void testGetWithFiltersWithInvalidParameters() {
        // Arrange
        PageRequest pageRequest = PageRequest.of(0, 10);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> service.getWithFilters("", "ACTIVE", null, pageRequest));
        assertThrows(IllegalArgumentException.class, () -> service.getWithFilters("", null, "InvalidRole", pageRequest));
        assertThrows(IllegalArgumentException.class, () -> service.getWithFilters("", "ACTIVE", "InvalidRole", null));
    }

    @Test
    void testGetByIdWithExceptionThrown() {
        assertThrows(PlhException.class, () -> service.getById(null));
        verify(repository, never()).save(any());
    }

    @Test
    void testGetByIdWithExistingClient() {
        var resultUser = Optional.of(user1);
        when(repository.findById(anyLong())).thenReturn(resultUser);
        var queryResult = repository.findById(1L);
        assertEquals(resultUser, queryResult);
        verify(repository).findById(anyLong());
    }

    @Test
    void testGetByIdWithNonExistingClient() {
        Optional<User> resultUser = Optional.empty();
        when(repository.findById(anyLong())).thenReturn(resultUser);
        var queryResult = service.getById(2L);
        assertEquals(Optional.empty(), queryResult);
    }

}