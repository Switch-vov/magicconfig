package com.switchvov.magicconfig.client.config;

import com.switchvov.magicconfig.client.repository.MagicRepositoryChangeEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.ApplicationContext;

import java.util.Map;
import java.util.Objects;

/**
 * magic config service impl.
 *
 * @author switch
 * @since 2024/5/3
 */
@Slf4j
public class MagicConfigServiceImpl implements MagicConfigService {
    ApplicationContext context;
    Map<String, String> config;

    public MagicConfigServiceImpl(ApplicationContext context, Map<String, String> config) {
        this.context = context;
        this.config = config;
    }

    @Override
    public String[] getPropertyNames() {
        return config.keySet().toArray(new String[0]);
    }

    @Override
    public String getProperty(String name) {
        return config.get(name);
    }

    @Override
    public void onChange(MagicRepositoryChangeEvent event) {
        Map<String, String> config = event.getConfig();
        if (Objects.isNull(config) || config.isEmpty()) {
            log.warn(" ===> [MagicConfig] config service unchanged, event:{}", event);
            return;
        }
        log.debug(" ===> [MagicConfig] config service changed, event:{}", event);
        this.config = config;
        log.debug(" ===> [MagicConfig] config service changed, fire an EnvironmentChangeEvent with keys:{}", config.keySet());
        context.publishEvent(new EnvironmentChangeEvent(config.keySet()));
    }
}
