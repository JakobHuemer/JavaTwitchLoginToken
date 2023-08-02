package at.jakobhuemer.sociallogintestspring.controller;


import at.jakobhuemer.sociallogintestspring.controller.arguementresolver.AuthUserResolver;
import at.jakobhuemer.sociallogintestspring.controller.interceptors.AuthenticationInterceptor;
import at.jakobhuemer.sociallogintestspring.service.UserService;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@EnableWebMvc
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {


    UserService userService;

    public WebMvcConfig( UserService userService ) {
        this.userService = userService;
    }

    @Override
    public void addInterceptors( InterceptorRegistry registry ) {
        registry.addInterceptor( new AuthenticationInterceptor( userService) );
    }

    @Override
    public void addArgumentResolvers( List<HandlerMethodArgumentResolver> resolvers ) {
        resolvers.add( new AuthUserResolver( userService ) );
    }
}
