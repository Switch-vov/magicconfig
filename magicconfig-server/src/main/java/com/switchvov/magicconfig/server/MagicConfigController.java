package com.switchvov.magicconfig.server;

import com.switchvov.magicconfig.server.model.Configs;
import com.switchvov.magicconfig.server.dal.ConfigMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @author switch
 * @since 2024/4/27
 */
@Slf4j
@RestController
public class MagicConfigController {
    private static final Map<String, Long> VERSIONS = new HashMap<>();
    private static final MultiValueMap<String, DeferredResult<Long>> RESULT_MULTI_MAP = new LinkedMultiValueMap<>();

    private final ConfigMapper mapper;
    private final DistributedLocks locks;

    public MagicConfigController(
            @Autowired ConfigMapper configMapper,
            @Autowired DistributedLocks locks
    ) {
        this.mapper = configMapper;
        this.locks = locks;
    }

    @GetMapping("/list")
    public List<Configs> list(
            @RequestParam("app") String app,
            @RequestParam("env") String env,
            @RequestParam("ns") String ns
    ) {
        return mapper.list(app, env, ns);
    }

    @PostMapping("/update")
    public List<Configs> update(
            @RequestParam("app") String app,
            @RequestParam("env") String env,
            @RequestParam("ns") String ns,
            @RequestBody Map<String, String> params
    ) {
        String key = genKey(app, env, ns);
        params.forEach((k, v) -> insertOrUpdate(new Configs(app, env, ns, k, v)));
        long value = System.currentTimeMillis();
        VERSIONS.put(key, value);
        log.debug("poll in defer version {} setResult", key);
        Optional.ofNullable(RESULT_MULTI_MAP.get(key))
                .ifPresent(results -> results.forEach(result -> result.setResult(value)));
        return mapper.list(app, env, ns);
    }

    private void insertOrUpdate(Configs configs) {
        Configs conf = mapper.select(configs.getApp(), configs.getEnv(), configs.getNs(), configs.getPkey());
        if (Objects.isNull(conf)) {
            mapper.insert(configs);
            return;
        }
        mapper.update(configs);
    }

    @GetMapping("/version")
    public DeferredResult<Long> version(
            @RequestParam("app") String app,
            @RequestParam("env") String env,
            @RequestParam("ns") String ns
    ) {
        String key = genKey(app, env, ns);
        log.debug("poll in defer version {}", key);
        DeferredResult<Long> result = new DeferredResult<>();
        result.onCompletion(() -> {
            log.debug("poll in defer version {} onCompletion", key);
            RESULT_MULTI_MAP.remove(key);
        });
        result.onTimeout(() -> {
            log.debug("poll in defer version {} onTimeout", key);
            RESULT_MULTI_MAP.remove(key);
        });
        RESULT_MULTI_MAP.add(key, result);
        log.debug("return defer for {}", key);
        return result;
    }

    @GetMapping("/status")
    public boolean status() {
        return locks.getLocked().get();
    }

    private String genKey(String app, String env, String ns) {
        return app + "_" + env + "_" + ns;
    }
}
