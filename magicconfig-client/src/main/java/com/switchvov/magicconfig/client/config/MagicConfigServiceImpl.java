package com.switchvov.magicconfig.client.config;

import com.switchvov.magicconfig.client.repository.MagicRepositoryChangeEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.ApplicationContext;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

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
        Set<String> keys = calcChangedKeys(this.config, event.getConfig());
        if (keys.isEmpty()) {
            log.warn(" ===> [MagicConfig] calcChangedKeys return empty, ignore update.");
            return;
        }
        Map<String, String> config = event.getConfig();
        if (Objects.isNull(config) || config.isEmpty()) {
            log.warn(" ===> [MagicConfig] config service unchanged, event:{}", event);
            return;
        }
        log.debug(" ===> [MagicConfig] config service changed, event:{}", event);
        this.config = config;
        log.debug(" ===> [MagicConfig] config service changed, fire an EnvironmentChangeEvent with keys:{}", keys);
        context.publishEvent(new EnvironmentChangeEvent(keys));
    }

    private Set<String> calcChangedKeys(Map<String, String> oldConfigs, Map<String, String> newConfigs) {
        if (Objects.isNull(oldConfigs) || oldConfigs.isEmpty()) {
            return newConfigs.keySet();
        }
        if (Objects.isNull(newConfigs) || newConfigs.isEmpty()) {
            return oldConfigs.keySet();
        }
        Set<String> news = newConfigs.keySet().stream()
                .filter(key -> !newConfigs.getOrDefault(key, "").equals(oldConfigs.getOrDefault(key, "")))
                .collect(Collectors.toSet());
        newConfigs.keySet().stream().filter(key -> !newConfigs.containsKey(key)).forEach(news::add);
        return news;
    }
}
