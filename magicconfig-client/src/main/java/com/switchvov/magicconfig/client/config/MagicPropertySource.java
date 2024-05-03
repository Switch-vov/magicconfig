package com.switchvov.magicconfig.client.config;

import org.springframework.core.env.EnumerablePropertySource;

/**
 * magic property source.
 *
 * @author switch
 * @since 2024/5/3
 */
public class MagicPropertySource extends EnumerablePropertySource<MagicConfigService> {

    public MagicPropertySource(String name, MagicConfigService source) {
        super(name, source);
    }

    @Override
    public String[] getPropertyNames() {
        return source.getPropertyNames();
    }

    @Override
    public Object getProperty(String name) {
        return source.getProperty(name);
    }
}
