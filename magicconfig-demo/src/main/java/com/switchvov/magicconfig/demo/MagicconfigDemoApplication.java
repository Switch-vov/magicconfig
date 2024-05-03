package com.switchvov.magicconfig.demo;

import com.switchvov.magicconfig.client.annotation.EnableMagicConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import java.util.Arrays;

@SpringBootApplication
@EnableConfigurationProperties({MagicDemoConfig.class})
@EnableMagicConfig
@Slf4j
public class MagicconfigDemoApplication {
    @Value("${magic.a}")
    private String a;

    @Autowired
    private MagicDemoConfig magicDemoConfig;

    @Autowired
    private Environment environment;

    public static void main(String[] args) {
        SpringApplication.run(MagicconfigDemoApplication.class, args);
    }

    @Bean
    public ApplicationRunner runner() {
        log.info(Arrays.toString(environment.getActiveProfiles()));
        return args -> {
            log.info("from value a:{}", a);
            log.info("from properties a:{}", magicDemoConfig.getA());
        };
    }
}
