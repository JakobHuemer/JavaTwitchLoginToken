package at.jakobhuemer.sociallogintestspring.models.user;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention( java.lang.annotation.RetentionPolicy.RUNTIME )
@Target( java.lang.annotation.ElementType.METHOD )
public @interface RequiredPermissions {
    AccessScope[] scopes() default {};
    AuthorityLevel level() default AuthorityLevel.USER;
}
