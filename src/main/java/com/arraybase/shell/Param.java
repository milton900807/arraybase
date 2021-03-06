package com.arraybase.shell;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for parameters of Command-marked methods.
 * This annotation is of particular usefullness, because Java 5 Reflection doesn't have access
 * to declared parameter names (there's simply no such information stored in classfile).
 * You must at least provide name attribute, others being optional.
 * @author ASG
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Param {
    /**
     * Parameter name.
     * Should (1) reflect the original Java parameter name, (2) be short and descriptive to the user.
     * Recommendations: "number-of-nodes", "user-login", "coefficients".
     * @return The name ascribed to annotated method parameter.
     */
    String name();

    /**
     * One-sentence description of the parameter.
     * It is recommended that you always set it.
     * @return "Short description attribute" of the annotated parameter.
     */
    String description() default "";

}