package org.classysheet.core.api.domain;

import org.classysheet.core.api.domain.naming.EnglishNamingStrategy;
import org.classysheet.core.api.domain.naming.NamingStrategy;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ TYPE })
@Retention(RUNTIME)
public @interface Workbook {

    /**
     * Defaults to the class name without camel case.
     * For example "FooBar" becomes "Foo bar".
     * @return "" if it should use the default name.
     */
    String name() default "";

    /**
     * Defaults to english.
     * @return an implementation of {@link NamingStrategy}
     */
    Class<? extends NamingStrategy> namingStrategyClass() default EnglishNamingStrategy.class;

}
