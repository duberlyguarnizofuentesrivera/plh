package com.duberlyguarnizo.plh.enums;

public enum UserStatus {
    ACTIVE("Activo"),
    INACTIVE("Inactivo");
    public final String label;

    UserStatus(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return this.label;
    }
}
