package top.wmgx.annotation;

import org.springframework.context.annotation.Import;
import top.wmgx.MybatisPlusLogPlus;

import java.lang.annotation.*;

/**
 * @author wmgx
 * @create 2021-02-28-16:13
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import({MybatisPlusLogPlus.class})
public @interface MybatisPlusLogPlusAutoConfig {
}
