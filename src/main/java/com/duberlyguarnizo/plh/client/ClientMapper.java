package com.duberlyguarnizo.plh.client;

import com.duberlyguarnizo.plh.address.Address;
import org.mapstruct.*;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface ClientMapper {

    Client toEntity(ClientDto clientDto);

    @Mapping(target = "pickUpAddressIds", expression = "java(pickUpAddressesToPickUpAddressIds(client.getPickUpAddresses()))")
    ClientDto toDto(Client client);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Client partialUpdate(ClientDto clientDto, @MappingTarget Client client);

    Client toEntity(ClientBasicDto clientBasicDto);

    ClientBasicDto toBasicDto(Client client);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Client partialUpdate(ClientBasicDto clientBasicDto, @MappingTarget Client client);


    Client toEntity(ClientDetailDto clientDetailDto);

    @Mapping(target = "pickUpAddressIds", expression = "java(pickUpAddressesToPickUpAddressIds(client.getPickUpAddresses()))")
    ClientDetailDto toDetailDto(Client client);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Client partialUpdate(ClientDetailDto clientDetailDto, @MappingTarget Client client);

    default Set<Long> pickUpAddressesToPickUpAddressIds(Set<Address> pickUpAddresses) {
        return pickUpAddresses.stream().map(Address::getId).collect(Collectors.toSet());
    }


}