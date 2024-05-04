package com.switchvov.magicconfig.client.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.switchvov.magicconfig.client.config.ConfigMeta;
import com.switchvov.magicutils.HttpUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * default impl for magic repository.
 *
 * @author switch
 * @since 2024/5/4
 */
@Slf4j
public class MagicRepositoryImpl implements MagicRepository {
    private static final ScheduledExecutorService heartbeatExecutor = Executors.newScheduledThreadPool(1);
    private static final Map<String, Map<String, String>> configMap = new HashMap<>();
    private static final Map<String, Long> versionMap = new HashMap<>();

    private final ConfigMeta meta;
    private final List<MagicRepositoryChangeListener> listeners;

    public MagicRepositoryImpl(ConfigMeta meta) {
        this.meta = meta;
        this.listeners = new ArrayList<>();
        heartbeatExecutor.scheduleWithFixedDelay(this::heartbeat, 1000, 5000, TimeUnit.MILLISECONDS);
    }

    @Override
    public Map<String, String> getConfig() {
        return configMap.computeIfAbsent(meta.genKey(), this::findAll);
    }

    private Map<String, String> findAll() {
        return findAll(meta.genKey());
    }

    private Map<String, String> findAll(String key) {
        String listPath = meta.listPath();
        log.debug(" ===>[MagicConfig] list all configs of [{}] from magic config server.", key);
        List<Configs> configs = HttpUtils.httpGet(listPath, new TypeReference<>() {
        });
        Map<String, String> resultMap = new HashMap<>();
        configs.forEach(c -> resultMap.put(c.getPkey(), c.getPval()));
        return resultMap;
    }

    @Override
    public void addListener(MagicRepositoryChangeListener listener) {
        listeners.add(listener);
    }

    private void heartbeat() {
        String versionPath = meta.versionPath();
        Long version = HttpUtils.httpGet(versionPath, new TypeReference<>() {
        });
        String key = meta.genKey();
        Long oldVersion = versionMap.getOrDefault(key, -1L);
        if (version <= oldVersion) {
            log.debug(" ===>[MagicConfig] config not need updated. oldVersion:{}, version:{}", oldVersion, version);
            return;
        }
        log.debug(" ===>[MagicConfig] config updated. oldVersion:{}, version:{}", oldVersion, version);
        Map<String, String> newConfigs = findAll();
        configMap.put(key, newConfigs);
        listeners.forEach(l -> l.onChange(new MagicRepositoryChangeEvent(meta, newConfigs)));
        versionMap.put(key, version);
    }
}
