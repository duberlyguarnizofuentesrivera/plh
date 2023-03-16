package com.duberlyguarnizo.plh.enums;

public enum EntityType {
    ENTITY_USER("Usuario"),
    ENTITY_TICKET("Ticket"),
    ENTITY_SHIPMENT("Envío"),
    ENTITY_CLIENT("Cliente"),
    ENTITY_RECEIVER("Destinatario"),
    DELETED_ENTITY("Borrado");

    public final String label;

    EntityType(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return this.label;
    }
}
