package com.duberlyguarnizo.plh.client;

import com.duberlyguarnizo.plh.enums.PersonType;
import com.duberlyguarnizo.plh.enums.UserStatus;
import com.duberlyguarnizo.plh.util.PlhException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Slf4j
class ClientServiceTest {

    @Mock
    private ClientRepository repository;

    @InjectMocks
    private ClientMapper mapper = Mappers.getMapper(ClientMapper.class);

    @InjectMocks
    private ClientService service;

    private ClientDetailDto mockClientDetailDto;
    private Client mockClient;

    @BeforeEach
    void setUp() {
        mockClient = Client.builder()
                .id(1L)
                .names("John Doe")
                .email("johndoe@example.com")
                .idNumber("000000001")
                .phone("000000000")
                .pickUpAddresses(Collections.emptySet())
                .type(PersonType.PERSON)
                .status(UserStatus.ACTIVE)
                .contactNames("Juan Barreto")
                .build();
//        log.warn(mockClientDto.toString());
        mockClientDetailDto = mapper.toDetailDto(mockClient);
//        log.warn(mockClientDetailDto.toString());
    }

    // ------------- Update tests ---------------

    @Test
    @DisplayName("ClientService returns false when client does not exist")
    void testUpdateWhenClientDoesNotExist() throws PlhException {
        // Arrange
        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        // Act
        boolean result = service.update(mockClientDetailDto);

        // Assert
        assertFalse(result);
        verify(repository).findById(1L);
        verifyNoMoreInteractions(repository);
    }

    @Test
    @DisplayName("ClientService return true when update is called and clients exists")
    void testUpdateWhenClientExists() throws PlhException {
        // Arrange
        Optional<Client> optionalClient = Optional.of(mockClient);
        when(repository.findById(anyLong())).thenReturn(optionalClient);
        when(repository.save(any(Client.class))).thenReturn(mockClient);

        // Act
        boolean result = service.update(mockClientDetailDto);

        // Assert
        assertTrue(result);
        verify(repository).findById(1L);
        verify(repository).save(any(Client.class));
        verifyNoMoreInteractions(repository);
    }


    @Test
    @DisplayName("ClientService throws exception when repository save() fails")
    void testUpdateWhenExceptionThrown() throws PlhException {
        // Arrange
        Optional<Client> optionalClient = Optional.of(mockClient);
        when(repository.findById(anyLong())).thenReturn(optionalClient);
        when(repository.save(any(Client.class))).thenThrow(new RuntimeException());

        // Act & Assert
        assertThrows(PlhException.class, () -> service.update(mockClientDetailDto));
        verify(repository).findById(1L);
        verifyNoMoreInteractions(repository);
    }

    // -------------- Save tests ---------------

    @Test
    @DisplayName("ClientService returns true when saving and client does not exist")
    void testSaveWhenClientDoesNotExist() throws PlhException {
        // Arrange
        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        // Act
        boolean result = service.save(mockClientDetailDto);

        // Assert
        assertTrue(result);
        verify(repository).findById(1L);
        verify(repository).save(any(Client.class));
    }

    @Test
    @DisplayName("ClientService returns false when saving and client does exist")
    void testSaveWhenClientExists() throws PlhException {
        // Arrange
        when(repository.findById(anyLong())).thenReturn(Optional.of(mockClient));

        // Act
        boolean result = service.save(mockClientDetailDto);

        // Assert
        assertFalse(result);
        verify(repository).findById(1L);
        verifyNoMoreInteractions(repository);
    }

    @Test
    @DisplayName("ClientService throws exception when saving and repository save() fails")
    void testSaveWhenExceptionThrown() throws PlhException {
        // Arrange
        when(repository.findById(anyLong())).thenReturn(Optional.empty());
//        when(mapper.toEntity((ClientDetailDto) any())).thenReturn(mockClient);
        doThrow(new RuntimeException()).when(repository).save(any(Client.class));

        // Act & Assert
        assertThrows(PlhException.class, () -> service.save(mockClientDetailDto));
        verify(repository).findById(1L);
        verify(repository).save(any(Client.class));
        verifyNoMoreInteractions(repository);
    }

    // -------------- Delete tests ---------------

    @Test
    @DisplayName("ClientService returns true when deleting an existing client")
    void testDeleteWhenClientExists() throws PlhException {
        // Arrange
        Optional<Client> optionalClient = Optional.of(mockClient);
        when(repository.findById(anyLong())).thenReturn(optionalClient);
        doNothing().when(repository).deleteById(anyLong());

        // Act
        boolean result = service.delete(1L);

        // Assert
        assertTrue(result);
        verify(repository).findById(1L);
        verify(repository).deleteById(1L);
        verifyNoMoreInteractions(repository);
    }

    @Test
    @DisplayName("ClientService returns false when deleting a non-existing client")
    void testDeleteWhenClientDoesNotExist() throws PlhException {
        // Arrange
        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        // Act
        boolean result = service.delete(1L);

        // Assert
        assertFalse(result);
        verify(repository).findById(1L);
        verifyNoMoreInteractions(repository);
    }

    @Test
    @DisplayName("ClientService throws exception when deleting and repository delete() fails")
    void testDeleteWhenExceptionThrown() throws PlhException {
        // Arrange
        Optional<Client> optionalClient = Optional.of(mockClient);
        when(repository.findById(anyLong())).thenReturn(optionalClient);
        doThrow(new RuntimeException()).when(repository).deleteById(anyLong());

        // Act & Assert
        assertThrows(PlhException.class, () -> service.delete(1L));
        verify(repository).findById(1L);
        verify(repository).deleteById(1L);
        verifyNoMoreInteractions(repository);
    }

    // -------------- getClientById tests ---------------

    @Test
    void testGetClientByIdWhenClientExists() throws PlhException {
        // Arrange
        Optional<Client> optionalClient = Optional.of(mockClient);
        when(repository.findById(anyLong())).thenReturn(optionalClient);

        // Act
        var result = service.getClientById(1L);

        // Assert
        assertTrue(result.isPresent());
        verify(repository).findById(1L);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void testGetClientByIdWhenClientDoesNotExist() throws PlhException {
        // Arrange
        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        // Act
        var result = service.getClientById(1L);

        // Assert
        assertFalse(result.isPresent());
        verify(repository).findById(1L);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void testGetClientByIdWhenExceptionThrown() throws PlhException {
        // Arrange
        when(repository.findById(anyLong())).thenThrow(new RuntimeException());

        // Act & Assert
        assertThrows(PlhException.class, () -> service.getClientById(1L));
        verify(repository).findById(1L);
        verifyNoMoreInteractions(repository);
    }

    // -------------- getAll tests ---------------

    @Test
    void testGetAllWithValidArguments() throws PlhException {
        // Arrange
        List<Client> clients = new ArrayList<>();
        clients.add(mockClient);
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<Client> queryResultPage = new PageImpl<>(clients);

        when(repository.findAll((Specification<Client>) any(), any(PageRequest.class))).thenReturn(queryResultPage);

        // Act
        var result = service.getAll("John", "PERSON", "ACTIVE", LocalDate.now(),
                LocalDate.now(), pageRequest);

        // Assert
        assertNotNull(result);
        verify(repository).findAll((Specification<Client>) any(), any(PageRequest.class));
        verifyNoMoreInteractions(repository);
    }


    @Test
    void testGetAllWithTypeArgumentNotAllowed() throws PlhException {
        // Arrange

        // Act & Assert
        LocalDate date = LocalDate.now();
        assertThrows(PlhException.class, () -> service.getAll("John", "INVALID_TYPE", "ACTIVE", date,
                date, null));
        verifyNoMoreInteractions(repository);
    }

    @Test
    void testGetAllWithStatusArgumentNotAllowed() throws PlhException {
        // Arrange

        // Act & Assert
        LocalDate date = LocalDate.now();
        assertThrows(PlhException.class, () -> service.getAll("John", "COMPANY", "INVALID_STATUS", date,
                date, null));
        verifyNoMoreInteractions(repository);
    }

    @Test
    void testGetAllWithStatusAndTypeEqualsAll() throws PlhException {
        // Act & Assert
        LocalDate date = LocalDate.now();
        when(repository.findAll((Specification<Client>) any(), any(PageRequest.class))).thenReturn(null);
        assertDoesNotThrow(() -> service.getAll("", "all", "all", date,
                date, null));
        verify(repository).findAll((Specification<Client>) any(), any(PageRequest.class));
    }
}