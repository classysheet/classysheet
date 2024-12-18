package org.classysheet.core.api.domain;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ FIELD })
@Retention(RUNTIME)
public @interface Column {

    /**
     * Defaults to the field name without camel case and with starting capital.
     * For example "fooBar" becomes "Foo bar".
     * @return "" if it should use the default name.
     */
    String name() default "";

}
