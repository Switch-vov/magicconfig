package com.switchvov.magicconfig.client.config;

import java.util.Map;

/**
 * magic config service impl.
 *
 * @author switch
 * @since 2024/5/3
 */
public class MagicConfigServiceImpl implements MagicConfigService {
    Map<String, String> config;

    public MagicConfigServiceImpl(Map<String, String> config) {
        this.config = config;
    }

    @Override
    public String[] getPropertyNames() {
        return this.config.keySet().toArray(new String[0]);
    }

    @Override
    public String getProperty(String name) {
        return this.config.get(name);
    }
}
