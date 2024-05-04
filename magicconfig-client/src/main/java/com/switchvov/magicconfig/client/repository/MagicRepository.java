package com.switchvov.magicconfig.client.repository;

import com.switchvov.magicconfig.client.config.ConfigMeta;

import java.util.Map;

/**
 * interface to get config from remote.
 *
 * @author switch
 * @since 2024/5/4
 */
public interface MagicRepository {
    Map<String, String> getConfig();

    void addListener(MagicRepositoryChangeListener listener);

    static MagicRepository getDefault(ConfigMeta meta) {
        return new MagicRepositoryImpl(meta);
    }
}
