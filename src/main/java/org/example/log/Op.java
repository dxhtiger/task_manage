// org.example.log.Op.java
package org.example.log;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Op {
    String value();           // 操作名，例如：创建任务 / 删除任务
    boolean saveArgs() default true;
}
