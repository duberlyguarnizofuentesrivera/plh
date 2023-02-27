package com.duberlyguarnizo.plh.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
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
