package at.jakobhuemer.sociallogintestspring.tools;

import at.jakobhuemer.sociallogintestspring.service.UserService;
import at.jakobhuemer.sociallogintestspring.settings.Settings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LoadTokensOnStartup implements CommandLineRunner {


    @Autowired
    private UserService userService;


    @Override
    public void run( String... args ) throws Exception {

        List<String> tokens = userService.getAllTokens();
        System.out.println( "Loading tokens: " + tokens.toString() );
        TokenGenerator.get().load( tokens.toArray( new String[ 0 ] ) );
    }
}
