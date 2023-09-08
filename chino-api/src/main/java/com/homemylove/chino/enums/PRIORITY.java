package com.homemylove.chino.enums;

import lombok.Getter;

@Getter
public enum PRIORITY {
    IMPORTANT(256),
    HIGH( 64),
    MEDIUM( 32),
    LOW( 8),
    NOT_IMPORTANT(2);

    private final int value;

     PRIORITY( int value) {
        this.value = value;
    }
}