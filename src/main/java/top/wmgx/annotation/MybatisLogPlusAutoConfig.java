package top.wmgx.annotation;

import org.springframework.context.annotation.Import;
import top.wmgx.MybatisLogPlus;

import java.lang.annotation.*;

/**
 * @author wmgx
 * @create 2021-02-28-16:13
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import({MybatisLogPlus.class})
public @interface MybatisLogPlusAutoConfig {
}
