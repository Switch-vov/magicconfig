package com.switchvov.magicconfig.client.config;

/**
 * magic config service.
 *
 * @author switch
 * @since 2024/5/3
 */
public interface MagicConfigService {
    String[] getPropertyNames();

    String getProperty(String name);
}
