package at.jakobhuemer.sociallogintestspring;

import at.jakobhuemer.sociallogintestspring.models.user.User;
import at.jakobhuemer.sociallogintestspring.repository.UserRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@SpringBootApplication
public class SocialLoginTestSpringApplication {


    public static void main( String[] args ) {
        SpringApplication.run( SocialLoginTestSpringApplication.class, args );
    }

}
