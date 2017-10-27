package annot;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 是否创建实体对象.
 * <p>在方法前加上此注解，则服务调用Servlet会给方法传入实体对象</p>
 * @author chenmin
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface BuildEntity {
	String[] value() default {};
}
