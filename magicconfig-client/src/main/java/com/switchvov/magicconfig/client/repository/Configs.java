package com.switchvov.magicconfig.client.repository;

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
public class Configs {
    private String app;
    private String env;
    private String ns;
    private String pkey;
    private String pval;
}
