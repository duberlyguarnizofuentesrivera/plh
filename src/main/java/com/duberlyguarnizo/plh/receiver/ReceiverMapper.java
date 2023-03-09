package com.duberlyguarnizo.plh.receiver;

import com.duberlyguarnizo.plh.address.Address;
import org.mapstruct.*;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface ReceiverMapper {

    Receiver toEntity(ReceiverDto receiverDto);

    @Mapping(target = "addressIds", expression = "java(addressesToAddressIds(receiver.getAddresses()))")
    ReceiverDto toDto(Receiver receiver);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Receiver partialUpdate(ReceiverDto receiverDto, @MappingTarget Receiver receiver);

    Receiver toEntity(ReceiverBasicDto receiverBasicDto);


    ReceiverBasicDto toBasicDto(Receiver receiver);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Receiver partialUpdate(ReceiverBasicDto receiverBasicDto, @MappingTarget Receiver receiver);


    Receiver toEntity(ReceiverDetailDto receiverDetailDto);

    @Mapping(target = "addressIds", expression = "java(addressesToAddressIds(receiver.getAddresses()))")
    ReceiverDetailDto toDetailDto(Receiver receiver);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Receiver partialUpdate1(ReceiverDetailDto receiverDetailDto, @MappingTarget Receiver receiver);

    default Set<Long> addressesToAddressIds(Set<Address> addresses) {
        return addresses.stream().map(Address::getId).collect(Collectors.toSet());
    }

    default Set<Long> addressesToAddressIds1(Set<Address> addresses) {
        return addresses.stream().map(Address::getId).collect(Collectors.toSet());
    }
}