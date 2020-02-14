package com.ruijie.cpu.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "bg",ignoreUnknownFields = false)
public class BgProperties {
    private String ip;
    private String port;
    private String call;
}
