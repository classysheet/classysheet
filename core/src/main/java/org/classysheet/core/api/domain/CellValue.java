package org.classysheet.core.api.domain;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ FIELD })
@Retention(RUNTIME)
public @interface CellValue {

    /**
     * Defaults to the enum value name in lower case, with underscores as spaces, and with a starting capital.
     * For example "FOO_BAR" becomes "Foo bar".
     * @return "" if it should use the default name.
     */
    String name() default "";

}
