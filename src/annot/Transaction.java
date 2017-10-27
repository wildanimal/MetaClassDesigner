package annot;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 事务标记注解.
 * <pre>在服务类方法前标记此注解.则服务Servlet会使用数据库事务支持.</pre>
 * @author chenmin
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Transaction {
	boolean readOnly() default false;
}
