package com.switchvov.magicconfig.client.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.switchvov.magicconfig.client.config.ConfigMeta;
import com.switchvov.magicutils.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * default impl for magic repository.
 *
 * @author switch
 * @since 2024/5/4
 */
@Slf4j
public class MagicRepositoryImpl implements MagicRepository {
    private static final ExecutorService heartbeatExecutor = Executors.newFixedThreadPool(1);
    private static final Map<String, Map<String, String>> configMap = new HashMap<>();
    private static final Map<String, Long> versionMap = new HashMap<>();

    private final ApplicationContext context;
    private final ConfigMeta meta;
    private final List<MagicRepositoryChangeListener> listeners;
    private final HttpUtils.HttpInvoker invoker;

    public MagicRepositoryImpl(ApplicationContext context, ConfigMeta meta) {
        this.context = context;
        this.meta = meta;
        this.listeners = new ArrayList<>();
        this.invoker = new HttpUtils.OkHttpInvoker();
        Environment env = context.getEnvironment();
        int timeout = Integer.parseInt(env.getProperty("magicconfig.client.poll.timeout", "20000"));
        int maxIdleConnections = Integer.parseInt(env.getProperty("magicconfig.client.poll.maxconn", "128"));
        int keepAliveDuration = Integer.parseInt(env.getProperty("magicconfig.client.poll.keepalive", "300"));
        ((HttpUtils.OkHttpInvoker) this.invoker).init(timeout, maxIdleConnections, keepAliveDuration);
        heartbeatExecutor.execute(this::heartbeat);
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
        List<Configs> configs = HttpUtils.httpGet(invoker::get, listPath, new TypeReference<>() {
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
        while (true) {
            String versionPath = meta.versionPath();
            Long version = -1L;
            try {
                version = HttpUtils.httpGet(invoker::get, versionPath, new TypeReference<>() {
                });
            } catch (Exception e) {
                log.debug("poll version timeout");
            }
            String key = meta.genKey();
            Long oldVersion = versionMap.getOrDefault(key, -1L);
            if (version <= oldVersion) {
                log.debug(" ===>[MagicConfig] config not need updated. oldVersion:{}, version:{}", oldVersion, version);
                continue;
            }
            log.debug(" ===>[MagicConfig] config updated. oldVersion:{}, version:{}", oldVersion, version);
            Map<String, String> newConfigs = findAll();
            configMap.put(key, newConfigs);
            listeners.forEach(l -> l.onChange(new MagicRepositoryChangeEvent(meta, newConfigs)));
            versionMap.put(key, version);
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (Exception e) {
                log.debug("poll version sleep error", e);
            }
        }
    }
}
