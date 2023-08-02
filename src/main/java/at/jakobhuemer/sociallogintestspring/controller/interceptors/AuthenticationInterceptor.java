package at.jakobhuemer.sociallogintestspring.controller.interceptors;

import at.jakobhuemer.sociallogintestspring.exceptions.InterceptorResponseExceptionHandler;
import at.jakobhuemer.sociallogintestspring.exceptions.UserNotFoundException;
import at.jakobhuemer.sociallogintestspring.models.user.AccessScope;
import at.jakobhuemer.sociallogintestspring.models.user.AuthorityLevel;
import at.jakobhuemer.sociallogintestspring.models.user.RequiredPermissions;
import at.jakobhuemer.sociallogintestspring.models.user.User;
import at.jakobhuemer.sociallogintestspring.repository.UserRepository;
import at.jakobhuemer.sociallogintestspring.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.misc.Pair;
import org.apache.http.client.HttpResponseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

@Component
@Slf4j
public class AuthenticationInterceptor implements HandlerInterceptor {

    UserService userService;

    public AuthenticationInterceptor( UserService userService ) {
        this.userService = userService;
    }

    @Override
    public boolean preHandle( HttpServletRequest request, HttpServletResponse response, Object handler ) throws Exception {

        String token = request.getHeader( "Authorization" );

        HandlerMethod handlerMethod = (HandlerMethod) handler;

        RequiredPermissions requiredPermissions = handlerMethod.getMethodAnnotation( RequiredPermissions.class );

        if( requiredPermissions != null ) {

            if( token == null || token.isEmpty() ) {
                return InterceptorResponseExceptionHandler.thr( response, request, HttpStatus.BAD_REQUEST, "Authorization Header missing", new Pair<>( "path", request.getRequestURI() ) );
            }

            List<AccessScope> requiredScopes = Arrays.stream( requiredPermissions.scopes() ).toList();
            AuthorityLevel requiredLevel = requiredPermissions.level();

            // get users level and scopes

            User authUser;
            try {
                authUser = userService.getUserByToken( token );
            } catch( UserNotFoundException e ) {
                return InterceptorResponseExceptionHandler.thr( response, request, org.springframework.http.HttpStatus.UNAUTHORIZED, "Invalid Auth Token", new Pair<>( "path", request.getRequestURI() ) );
            }

            // check level
            AuthorityLevel userLevel = authUser.getAuthorityLevel();

            //restricted can't do anything
            if( userLevel == AuthorityLevel.RESTRICTED ) {
                return InterceptorResponseExceptionHandler.thr( response, request, HttpStatus.FORBIDDEN, "Restricted", new Pair<>( "path", request.getRequestURI() ) );
            }

            // superuser can do everything
            if( userLevel == AuthorityLevel.SUPER ) {
                request.setAttribute( "authUser", authUser );
                return true;
            }

            if( userLevel.getLevel() < requiredLevel.getLevel() ) {
                return InterceptorResponseExceptionHandler.thr( response, request, org.springframework.http.HttpStatus.FORBIDDEN, "Missing Scopes", new Pair<>( "path", request.getRequestURI() ) );
            }

            // check scopes
            List<AccessScope> userScopes = authUser.getAccessScopes();


            if( !new HashSet<>( userScopes ).containsAll( requiredScopes ) ) {
                return InterceptorResponseExceptionHandler.thr( response, request, org.springframework.http.HttpStatus.FORBIDDEN, "Missing Scopes", new Pair<>( "path", request.getRequestURI() ) );
            }

            request.setAttribute( "authUser", authUser );
            return true;
        }
        return true;

    }

    @Override
    public void postHandle( HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView ) throws Exception {
        HandlerInterceptor.super.postHandle( request, response, handler, modelAndView );
    }

    @Override
    public void afterCompletion( HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex ) throws Exception {
        HandlerInterceptor.super.afterCompletion( request, response, handler, ex );
    }


}
