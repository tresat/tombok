package lombok;

import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Defines a tranformation that will add a static initializer block to
 * "announce" via Std Out when a class is first loaded, and an initializer block
 * to "announce" whenever an instance is created.
 */
@Target(ElementType.TYPE) 
@Retention(RetentionPolicy.SOURCE)
public @interface Announcer {}
