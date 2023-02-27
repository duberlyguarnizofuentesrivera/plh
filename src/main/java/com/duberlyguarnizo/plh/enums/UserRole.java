package com.duberlyguarnizo.plh.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum UserRole {
    ADMIN("Administrador"),
    SUPERVISOR("Supervisor"),
    DISPATCHER("Despachador"),
    TRANSPORTER("Transportista"),
    CLIENT("Cliente");
    public final String label;

    UserRole(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return this.label;
    }
}
