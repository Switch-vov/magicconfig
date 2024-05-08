package com.switchvov.magicconfig.client.value;

import com.switchvov.magicconfig.client.util.PlaceholderHelper;
import com.switchvov.magicutils.FieldUtils;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.Objects;

/**
 * process spring value.
 * 1. 扫描所有的spring value，保存起来
 * 2. 在配置变更时，更新所有的spring value
 *
 * @author switch
 * @since 2024/5/8
 */
@Slf4j
public class SpringValueProcessor implements BeanPostProcessor, BeanFactoryAware, ApplicationListener<EnvironmentChangeEvent> {
    private static final PlaceholderHelper HELPER = PlaceholderHelper.getInstance();
    private static final MultiValueMap<String, SpringValue> VALUE_HOLDER = new LinkedMultiValueMap<>();

    @Setter
    private BeanFactory beanFactory;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        FieldUtils.findFieldByAnnotated(bean.getClass(), Value.class).forEach(field -> {
            Value value = field.getAnnotation(Value.class);
            HELPER.extractPlaceholderKeys(value.value()).forEach(key -> {
                SpringValue springValue = new SpringValue(bean, beanName, key, value.value(), field);
                log.info(" ===>[MagicConfig] add value holder:{}", springValue);
                VALUE_HOLDER.add(key, springValue);
            });
        });
        return bean;
    }

    @Override
    public void onApplicationEvent(EnvironmentChangeEvent event) {
        log.info(" ===>[MagicConfig] update spring value for keys:{}", event.getKeys());
        event.getKeys().forEach(key -> {
            log.info(" ===>[MagicConfig] update spring value:{}", key);
            List<SpringValue> springValues = VALUE_HOLDER.get(key);
            if (Objects.isNull(springValues) || springValues.isEmpty()) {
                return;
            }
            springValues.forEach(springValue -> {
                log.info(" ===>[MagicConfig] update spring value:{} for key:{}", springValue, key);
                try {
                    Object value = HELPER.resolvePropertyValue((ConfigurableBeanFactory) beanFactory,
                            springValue.getBeanName(), springValue.getPlaceholder());
                    log.info(" ===>[MagicConfig] update value:{} for holder:{}", value, springValue.getPlaceholder());
                    springValue.getField().setAccessible(true);
                    springValue.getField().set(springValue.getBean(), value);
                } catch (Exception e) {
                    log.error(" ===>[MagicConfig] update spring value error", e);
                }
            });
        });
    }
}
