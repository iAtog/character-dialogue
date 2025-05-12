package me.iatog.characterdialogue.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface NPCAdapterInfo {
    String pluginName();
    String version() default "1.0.0";
}
