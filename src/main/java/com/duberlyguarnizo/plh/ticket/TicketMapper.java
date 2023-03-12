package com.duberlyguarnizo.plh.ticket;

import com.duberlyguarnizo.plh.shipment.Shipment;
import org.mapstruct.*;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface TicketMapper {
    @InheritInverseConfiguration(name = "toDto")
    Ticket toEntity(TicketDto ticketDto);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "clientId", source = "client.id")
    @Mapping(target = "shipmentIds", expression = "java(shipmentsToShipmentIds(ticket.getShipments()))")
    TicketDto toDto(Ticket ticket);

    @InheritConfiguration(name = "toEntity")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Ticket partialUpdate(TicketDto ticketDto, @MappingTarget Ticket ticket);

    @Mapping(source = "clientId", target = "client.id")
    Ticket toEntity(TicketBasicDto ticketBasicDto);

    @Mapping(source = "client.id", target = "clientId")
    TicketBasicDto toBasicDto(Ticket ticket);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "clientId", target = "client.id")
    Ticket partialUpdate(TicketBasicDto ticketBasicDto, @MappingTarget Ticket ticket);


    @InheritInverseConfiguration(name = "toDetailDto")
    Ticket toEntity(TicketDetailDto ticketDetailDto);

    @Mapping(target = "shipmentIds", expression = "java(shipmentsToShipmentIds(ticket.getShipments()))")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "clientId", source = "client.id")
    TicketDetailDto toDetailDto(Ticket ticket);

    @InheritConfiguration(name = "toEntity")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Ticket partialUpdate(TicketDetailDto ticketDetailDto, @MappingTarget Ticket ticket);

    default List<Long> shipmentsToShipmentIds(List<Shipment> shipments) {
        return shipments.stream().map(Shipment::getId).toList();
    }
}