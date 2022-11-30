package no.fintlabs.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Constraint(validatedBy = UniqueKeysInstanceElementMetadataPostValidator.class)
public @interface UniqueKeys {

    String DUPLICATE_KEYS = "duplicateKeys";

    String message() default "contains duplicate keys: {" + DUPLICATE_KEYS + "}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
