package com.switchvov.magicconfig.demo;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Test demo config.
 *
 * @author switch
 * @since 2024/5/3
 */
@Data
@ConfigurationProperties(prefix = "magic")
public class MagicDemoConfig {
    private String a;
    private String b;
}
