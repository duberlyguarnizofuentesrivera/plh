package com.duberlyguarnizo.plh.util;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PlhException extends RuntimeException {
    public PlhException(String message) {
        super(message);
        log.error("PlhException: " + message, this);
        super.printStackTrace();
    }
}
