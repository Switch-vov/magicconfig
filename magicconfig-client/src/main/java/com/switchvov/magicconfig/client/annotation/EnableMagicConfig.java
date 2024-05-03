package com.switchvov.magicconfig.client.annotation;

import com.switchvov.magicconfig.client.config.MagicConfigRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Magic config client entrypoint.
 *
 * @author switch
 * @since 2024/5/3
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@Import({MagicConfigRegistrar.class})
public @interface EnableMagicConfig {
}
