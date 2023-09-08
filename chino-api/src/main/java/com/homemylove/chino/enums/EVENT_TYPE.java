package com.homemylove.chino.enums;

import lombok.Getter;

@Getter
public enum EVENT_TYPE {
    POKE("poke");

    private final String type;
    private EVENT_TYPE(String type){
        this.type = type;
    }

}
