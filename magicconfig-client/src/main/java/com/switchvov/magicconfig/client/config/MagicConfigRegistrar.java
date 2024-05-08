package com.switchvov.magicconfig.client.config;

import com.switchvov.magicconfig.client.value.SpringValueProcessor;
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
        registerClass(registry, PropertySourcesProcessor.class);
        registerClass(registry, SpringValueProcessor.class);
    }

    private static void registerClass(BeanDefinitionRegistry registry, Class<?> clazz) {
        log.debug(" ===>[MagicConfig] register class:{}", clazz.getCanonicalName());
        Optional<String> first = Arrays.stream(registry.getBeanDefinitionNames())
                .filter(x -> clazz.getName().equals(x))
                .findFirst();
        if (first.isPresent()) {
            log.debug(" ===>[MagicConfig] class:{} already registered", clazz.getCanonicalName());
            return;
        }
        AbstractBeanDefinition definition = BeanDefinitionBuilder
                .genericBeanDefinition(clazz).getBeanDefinition();
        registry.registerBeanDefinition(clazz.getName(), definition);
    }
}
