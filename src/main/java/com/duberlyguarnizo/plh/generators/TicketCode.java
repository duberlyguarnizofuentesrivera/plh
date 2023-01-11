package com.duberlyguarnizo.plh.generators;

import org.hibernate.annotations.ValueGenerationType;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@ValueGenerationType(generatedBy = TicketCodeGenerator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface TicketCode {
}
