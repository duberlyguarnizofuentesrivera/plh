package com.duberlyguarnizo.plh.enums;

public enum EventType {
    NEW_CLIENT("Nuevo cliente creado"),
    NEW_RECEIVER("Nuevo destinatario creado"),
    NEW_TICKET("Nuevo ticket creado"),
    NEW_USER("Nuevo usuario creado"),
    SHIPMENT_STATUS_CHANGE("Actualización de envío"),
    SHIPMENT_PROBLEM_CHANGE("Envío con problemas"),
    SHIPMENT_DELETED("Envío eliminado"),
    TICKET_PAYMENT_CHANGE("Actualización de pagos"),
    TICKET_STATUS_CHANGE("Actualización de estado de Ticket"),
    TICKET_DELETED("Ticket eliminado"),
    USER_DELETED("Usuario eliminado"),
    USER_ROLE_CHANGE("Cambio de rol de usuario"),
    USER_STATUS_CHANGE("Cambio de estado de usuario"),
    USER_PROFILE_CHANGE("Cambio en detalles de usuario");
    public final String label;

    EventType(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return this.label;
    }
}
