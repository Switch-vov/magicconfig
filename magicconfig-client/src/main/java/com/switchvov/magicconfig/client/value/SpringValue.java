package com.switchvov.magicconfig.client.value;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Field;

/**
 * spring value.
 *
 * @author switch
 * @since 2024/5/8
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpringValue {
    private Object bean;
    private String beanName;
    private String key;
    private String placeholder;
    private Field field;
}
