package at.jakobhuemer.sociallogintestspring.controller.interceptors;

import at.jakobhuemer.sociallogintestspring.models.User;
import at.jakobhuemer.sociallogintestspring.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import lombok.extern.slf4j.Slf4j;
import netscape.javascript.JSObject;
import org.apache.http.client.HttpResponseException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.io.PrintWriter;
import java.util.Optional;

@Component
@Slf4j
public class TokenAuthenticationInterceptor implements HandlerInterceptor {

    private UserRepository userRepository;

    public TokenAuthenticationInterceptor( UserRepository userRepository ) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean preHandle( HttpServletRequest request, HttpServletResponse response, Object handler ) throws Exception {

        System.out.println( "CHECKING AUTHORITY!!!" );

        if( !( handler instanceof HandlerMethod ) ) {
            return true;
        }


        HandlerMethod handlerMethod = (HandlerMethod) handler;
        User.AuthLevelRequired authLevelRequired = AnnotationUtils.findAnnotation( handlerMethod.getMethod(), User.AuthLevelRequired.class );

        if( authLevelRequired == null ) {
            System.out.println("NO ACCESS LEVEL REQUIRED");
            return true;
        }


        String reqToken = request.getHeader( "Authorization" );


        if( reqToken == null ) {

            JSONObject missingAuthResponse = new JSONObject();

            missingAuthResponse.put( "status", 400 );
            missingAuthResponse.put( "message", HttpStatus.BAD_REQUEST.getReasonPhrase() );
            missingAuthResponse.put( "details", "Missing Authorization Header" );

            response.setStatus( 400 );
            response.setContentType( "application/json" );
            response.getWriter().write( missingAuthResponse.toString() );
            return false;
        }

        Optional<User> optionalUser = userRepository.findDistinctFirstByToken( reqToken );

        User.AuthorityLevel userLevel;
        if( optionalUser.isPresent() ) {
            userLevel = optionalUser.get().getAuthorityLevel();
        } else {
            JSONObject invalidToken = new JSONObject();

            invalidToken.put( "status", 401 );
            invalidToken.put( "message", HttpStatus.UNAUTHORIZED.getReasonPhrase() );
            invalidToken.put( "details", "Invalid Authorization Token" );

            response.setStatus( 401 );
            response.setContentType( "application/json" );
            response.getWriter().write( invalidToken.toString() );
            return false;
        }



        System.out.println( "USER LEVEL: " + userLevel.getLevel() );

        // check for authority level
        if( userLevel.getLevel() >= authLevelRequired.value().getLevel() ) {

            // ACCESS GRANTED

            return true;
        } else {
            JSONObject authLevelTooLow = new JSONObject();

            authLevelTooLow.put( "status", 401 );
            authLevelTooLow.put( "message", HttpStatus.UNAUTHORIZED.getReasonPhrase() );
            authLevelTooLow.put( "details", "Missing Permissions" );

            response.setStatus( 401 );
            response.setContentType( "application/json" );
            response.getWriter().write( authLevelTooLow.toString() );
            return false;
        }
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
