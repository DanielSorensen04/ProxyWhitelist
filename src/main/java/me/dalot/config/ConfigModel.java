package me.dalot.config;

import lombok.Getter;
import lombok.Setter;

public class ConfigModel {

    @Getter
    @Setter
    private boolean enabled;
    @Getter
    private String message;

    @Override
    public String toString() {
        return "Panel{" +
                "enabled='" + enabled + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
