package at.jakobhuemer.sociallogintestspring.controller.interceptors;


import at.jakobhuemer.sociallogintestspring.repository.UserRepository;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EnableWebMvc
@Configuration
public class InterceptorConfiguration implements WebMvcConfigurer {

    private final UserRepository userRepository;

    public InterceptorConfiguration( UserRepository userRepository ) {
        this.userRepository = userRepository;
    }

    @Override
    public void addInterceptors( InterceptorRegistry registry ) {
        registry.addInterceptor( new TokenAuthenticationInterceptor( userRepository ) );
    }
}
