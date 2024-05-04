package com.switchvov.magicconfig.client.config;

import lombok.Data;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;

/**
 * magic property sources processor.
 *
 * @author switch
 * @since 2024/5/3
 */
@Data
public class PropertySourcesProcessor implements BeanFactoryPostProcessor, EnvironmentAware,
        PriorityOrdered, ApplicationContextAware {
    private static final String MAGIC_PROPERTY_SOURCES = "MagicPropertySources";
    private static final String MAGIC_PROPERTY_SOURCE = "MagicPropertySource";

    private Environment environment;
    private ApplicationContext applicationContext;

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory factory) throws BeansException {
        ConfigurableEnvironment ENV = (ConfigurableEnvironment) environment;
        if (ENV.getPropertySources().contains(MAGIC_PROPERTY_SOURCES)) {
            return;
        }
        // 通过 http 请求，去 magicconfig-server 获取配置
        String app = ENV.getProperty("magicconfig.app", "app1");
        String env = ENV.getProperty("magicconfig.env", "dev");
        String ns = ENV.getProperty("magicconfig.ns", "public");
        String configServer = ENV.getProperty("magicconfig.configServer", "http://localhost:9129");
        ConfigMeta configMeta = new ConfigMeta(app, env, ns, configServer);

        MagicConfigService configService = MagicConfigService.getDefault(applicationContext, configMeta);
        MagicPropertySource propertySource = new MagicPropertySource(MAGIC_PROPERTY_SOURCE, configService);
        CompositePropertySource composite = new CompositePropertySource(MAGIC_PROPERTY_SOURCES);
        composite.addPropertySource(propertySource);
        ENV.getPropertySources().addFirst(composite);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

}
