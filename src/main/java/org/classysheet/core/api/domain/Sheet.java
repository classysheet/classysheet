package org.classysheet.core.api.domain;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ TYPE })
@Retention(RUNTIME)
public @interface Sheet {

    /**
     * Defaults to the class name without camel case and with suffix "s".
     * For example "FooBar" becomes "Foo bars".
     * @return "" if it should use the default name.
     */
    String name() default "";

}
