package com.ex.commanddemo.concurrent.annotation;

import java.lang.annotation.*;

/**
 * Created by edison
 * On 2018/5/15 14:22
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
public @interface EventHandler {

    /**
     * The type of event this method handles. This handler will only be considered for invocation if the event message's
     * payload is assignable to this type.
     * <p>
     * Optional. If unspecified, the first parameter of the method defines the type of supported event.
     *
     * @return The type of the event this method handles.
     */
    Class<?> payloadType() default Object.class;
}
