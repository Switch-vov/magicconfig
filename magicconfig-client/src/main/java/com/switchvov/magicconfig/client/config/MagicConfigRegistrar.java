package com.switchvov.magicconfig.client.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Arrays;
import java.util.Optional;

/**
 * register magic config bean.
 *
 * @author switch
 * @since 2024/5/3
 */
@Slf4j
public class MagicConfigRegistrar implements ImportBeanDefinitionRegistrar {
    @Override
    public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
        log.debug("register PropertySourcesProcessor");
        Optional<String> first = Arrays.stream(registry.getBeanDefinitionNames())
                .filter(x -> PropertySourcesProcessor.class.getName().equals(x))
                .findFirst();
        if (first.isPresent()) {
            log.debug("PropertySourcesProcessor already registered");
            return;
        }
        AbstractBeanDefinition definition = BeanDefinitionBuilder
                .genericBeanDefinition(PropertySourcesProcessor.class).getBeanDefinition();
        registry.registerBeanDefinition(PropertySourcesProcessor.class.getName(), definition);
    }
}
