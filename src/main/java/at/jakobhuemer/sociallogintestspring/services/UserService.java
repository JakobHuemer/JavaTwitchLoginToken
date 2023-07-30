package at.jakobhuemer.sociallogintestspring.services;

import at.jakobhuemer.sociallogintestspring.dto.UserDTO;
import at.jakobhuemer.sociallogintestspring.models.User;
import at.jakobhuemer.sociallogintestspring.repository.UserRepository;
import at.jakobhuemer.sociallogintestspring.tools.TokenGenerator;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    UserRepository userRepository;

    public UserService( UserRepository userRepository ) {
        this.userRepository = userRepository;
    }

    public User register( String twitchLogin, String accessToken, String refreshToken, Long twitchId ) {
        System.out.println("REGISTERING NEW USER");
        // create new user


        User user = new User.Builder()
                .setToken( TokenGenerator.get().new_() )
                .setAccessToken( accessToken )
                .setRefreshToken( refreshToken )
                .setTwitchLogin( twitchLogin )
                .setTwitchId( twitchId )
                .build();

        // save user
        user = userRepository.save( user );
        return user;
    }

    public User login( String twitchLogin, String accessToken, String refreshToken, Long twitchId ) {
        System.out.println( "LOGIN EXISTING USER: " + twitchLogin );
        Optional<User> userOptional = userRepository.findDistinctFirstByTwitchId( twitchId );

        if( userOptional.isPresent() ) {
            User user = userOptional.get();
            user.setAccessToken( accessToken );
            user.setRefreshToken( refreshToken );
            userRepository.save( user );
            return user;
        } else {
            return register( twitchLogin, accessToken, refreshToken, twitchId );
        }
    }

    public List<UserDTO> getPublicUsers() {
        return userRepository.findAll().stream().map( User::toDTO ).toList();
    }
}
