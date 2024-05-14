package com.switchvov.magicconfig.server;

import com.switchvov.magicconfig.server.model.Configs;
import com.switchvov.magicconfig.server.dal.ConfigMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @author switch
 * @since 2024/4/27
 */
@RestController
public class MagicConfigController {
    private static final Map<String, Long> VERSIONS = new HashMap<>();

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
        params.forEach((k, v) -> insertOrUpdate(new Configs(app, env, ns, k, v)));
        VERSIONS.put(app + "-" + env + "-" + ns, System.currentTimeMillis());
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
    public long version(
            @RequestParam("app") String app,
            @RequestParam("env") String env,
            @RequestParam("ns") String ns
    ) {
        return VERSIONS.getOrDefault(app + "-" + env + "-" + ns, -1L);
    }

    @GetMapping("/status")
    public boolean status() {
        return locks.getLocked().get();
    }
}
