package com.switchvov.magicconfig.client.config;

import com.switchvov.magicconfig.client.repository.MagicRepository;
import com.switchvov.magicconfig.client.repository.MagicRepositoryChangeListener;
import org.springframework.context.ApplicationContext;

import java.util.Map;

/**
 * magic config service.
 *
 * @author switch
 * @since 2024/5/3
 */
public interface MagicConfigService extends MagicRepositoryChangeListener {
    String[] getPropertyNames();

    String getProperty(String name);

    static MagicConfigService getDefault(ApplicationContext context, ConfigMeta meta) {
        MagicRepository repository = MagicRepository.getDefault(context, meta);
        Map<String, String> config = repository.getConfig();
        MagicConfigService configService = new MagicConfigServiceImpl(context, config);
        repository.addListener(configService);
        return configService;
    }
}
