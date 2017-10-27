package annot;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 参数值注解.
 * <p>在服务类方法前加上此注解，则服务Servlet将根据参数名从request中取值，并用这些值调用方法.</p>
 * @author chenmin
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Params {
	String[] value() default {};
}
