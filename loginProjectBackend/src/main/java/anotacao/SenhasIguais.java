package anotacao;

import br.com.login_project.config.SenhasIguaisValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = SenhasIguaisValidator.class)
public @interface SenhasIguais {
    String message() default "Senhas não coincidem";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
