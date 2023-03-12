package com.duberlyguarnizo.plh.address;

import org.mapstruct.*;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface AddressMapper {
    Address toEntity(AddressDto addressDto);

    AddressDto toDto(Address address);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Address partialUpdate(AddressDto addressDto, @MappingTarget Address address);

    Address toEntity(AddressBasicDto addressBasicDto);

    AddressBasicDto toBasicDto(Address address);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Address partialUpdate(AddressBasicDto addressBasicDto, @MappingTarget Address address);


    default Set<Long> pickUpAddressesToPickUpAddressIds(Set<Address> pickUpAddresses) {
        return pickUpAddresses.stream().map(Address::getId).collect(Collectors.toSet());
    }

}