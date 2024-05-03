package com.switchvov.magicconfig.client.config;

import lombok.Data;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;

import java.util.HashMap;
import java.util.Map;

/**
 * magic property sources processor.
 *
 * @author switch
 * @since 2024/5/3
 */
@Data
public class PropertySourcesProcessor implements BeanFactoryPostProcessor, EnvironmentAware, PriorityOrdered {
    private static final String MAGIC_PROPERTY_SOURCES = "MagicPropertySources";
    private static final String MAGIC_PROPERTY_SOURCE = "MagicPropertySource";

    private Environment environment;

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory factory) throws BeansException {
        ConfigurableEnvironment env = (ConfigurableEnvironment) environment;
        if (env.getPropertySources().contains(MAGIC_PROPERTY_SOURCES)) {
            return;
        }
        // TODO: 通过 http 请求，去 magicconfig-server 获取配置
        Map<String, String> config = Map.of("magic.a", "dev500", "magic.b", "b600", "magic.c", "c700");
        MagicConfigService configService = new MagicConfigServiceImpl(config);
        MagicPropertySource propertySource = new MagicPropertySource(MAGIC_PROPERTY_SOURCE, configService);
        CompositePropertySource composite = new CompositePropertySource(MAGIC_PROPERTY_SOURCES);
        composite.addPropertySource(propertySource);
        env.getPropertySources().addFirst(composite);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
