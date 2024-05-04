package com.switchvov.magicconfig.client.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author switch
 * @since 2024/5/4
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConfigMeta {
    private String app;
    private String env;
    private String ns;
    private String configServer;

    public String genKey() {
        return getApp() + "_" + getEnv() + "_" + getNs();
    }

    public String listPath() {
        return path("list");
    }

    public String versionPath() {
        return path("version");
    }

    private String path(String path) {
        return getConfigServer() + "/" + path + "?app=" + getApp() + "&env=" + getEnv() + "&ns=" + getNs();
    }
}
