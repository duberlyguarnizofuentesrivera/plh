package com.duberlyguarnizo.plh.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum PersonType {
    PERSON("Persona Natural"), COMPANY("Empresa");
    public final String label;

    PersonType(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return this.label;
    }
}
