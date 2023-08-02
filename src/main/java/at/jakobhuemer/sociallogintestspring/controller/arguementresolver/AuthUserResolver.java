package at.jakobhuemer.sociallogintestspring.controller.arguementresolver;

import at.jakobhuemer.sociallogintestspring.models.user.AuthUser;
import at.jakobhuemer.sociallogintestspring.models.user.User;
import at.jakobhuemer.sociallogintestspring.service.UserService;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Optional;

public class AuthUserResolver implements HandlerMethodArgumentResolver {

    UserService userService;

    public AuthUserResolver( UserService userService ) {
        this.userService = userService;
    }

    @Override
    public boolean supportsParameter( MethodParameter parameter ) {
        return parameter.getParameterAnnotation( AuthUser.class ) != null;
    }

    @Override
    public Object resolveArgument( MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory ) throws Exception {
        return (User) webRequest.getAttribute( "authUser", NativeWebRequest.SCOPE_REQUEST );
    }

}
