package com.duberlyguarnizo.plh.enums;

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
