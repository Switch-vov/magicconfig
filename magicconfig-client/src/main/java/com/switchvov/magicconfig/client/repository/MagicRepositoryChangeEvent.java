package com.switchvov.magicconfig.client.repository;

import com.switchvov.magicconfig.client.config.ConfigMeta;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @author switch
 * @since 2024/5/4
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MagicRepositoryChangeEvent {
    private ConfigMeta meta;
    private Map<String, String> config;
}
