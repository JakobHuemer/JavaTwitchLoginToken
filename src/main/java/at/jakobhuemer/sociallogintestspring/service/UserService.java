package at.jakobhuemer.sociallogintestspring.service;

import at.jakobhuemer.sociallogintestspring.dto.UserDTO;
import at.jakobhuemer.sociallogintestspring.exceptions.UserNotFoundException;
import at.jakobhuemer.sociallogintestspring.models.user.AccessScope;
import at.jakobhuemer.sociallogintestspring.models.user.AuthorityLevel;
import at.jakobhuemer.sociallogintestspring.models.user.User;
import at.jakobhuemer.sociallogintestspring.repository.UserRepository;
import at.jakobhuemer.sociallogintestspring.tools.TokenGenerator;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    UserRepository userRepository;

    public UserService( UserRepository userRepository ) {
        this.userRepository = userRepository;
    }

    public User register( String twitchLogin, String accessToken, String refreshToken, Long twitchId ) {
        System.out.println( "REGISTERING NEW USER" );
        // create new user


        User user = User.builder()
                .accessScopes( List.of( AccessScope.EDIT_OWN_POSTS, AccessScope.WRITE_POSTS ) )
                .authorityLevel( AuthorityLevel.USER )
                .twitchLogin( twitchLogin )
                .accessToken( accessToken )
                .refreshToken( refreshToken )
                .twitchId( twitchId )
                .token( TokenGenerator.get().new_() )
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

    public User login( String bearerToken ) {
        System.out.println( "LOGIN EXISTING USER: " + bearerToken );
        Optional<User> userOptional = userRepository.findDistinctFirstByToken( bearerToken );

        if( userOptional.isPresent() ) {
            return userOptional.get();
        } else {
            throw new ResponseStatusException( HttpStatus.UNAUTHORIZED, "Invalid Bearer Token" );
        }
    }

    public List<UserDTO> getPublicUsers() {
        System.out.println( "GETTING USERS" );
        List<UserDTO> users = userRepository.findAll().stream().map( User::toDTO ).toList();

//        users.forEach( userDTO -> {
//            System.out.println(userDTO.login());
//        } );
//
//        userRepository.findAll();
//
//        List<User> users = new ArrayList<>();
//        users.add( User.builder()
//                .token( "token" )
//                .twitchLogin( "twitchLogin" )
//                .twitchId( 123L )
//                .build() );
        return users;
    }

    public List<String> getAllTokens() {
        return userRepository.findAll().stream().map( User::getToken ).toList();
    }

    public List<AccessScope> getUserAccessScopes( String token ) throws UserNotFoundException {
        return userRepository.findDistinctFirstByToken( token ).orElseThrow( () -> new UserNotFoundException( "User with token not found" ) ).getAccessScopes();
    }

    public AuthorityLevel getUserAuthorityLevel( String token ) throws UserNotFoundException {
        return userRepository.findDistinctFirstByToken( token ).orElseThrow( () -> new UserNotFoundException( "User with token not found" ) ).getAuthorityLevel();
    }

    public User getUserById( Long id ) throws UserNotFoundException {
        return userRepository.findById( id ).orElseThrow( () -> new UserNotFoundException( "User with id %d not found".formatted( id ) ) );
    }

    public User getUserByToken( String token ) throws UserNotFoundException {
        return userRepository.findDistinctFirstByToken( token ).orElseThrow( () -> new UserNotFoundException( "User with token %s not found".formatted( token ) ) );
    }

    public boolean addAccessScope( Long id, AccessScope accessScope ) throws UserNotFoundException {

        User user = getUserById( id );
        if( !user.getAccessScopes().contains( accessScope ) ) {
            user.getAccessScopes().add( accessScope );
            userRepository.save( user );
            return true;
        }
        return false;
    }

    public boolean removeAccessScope( Long id, AccessScope accessScope ) throws UserNotFoundException {

        User user = getUserById( id );
        boolean result = user.getAccessScopes().remove( accessScope );
        userRepository.save( user );
        return result;
    }

    public void setAuthorityLevel( Long id, AuthorityLevel level ) throws UserNotFoundException {
        User user = getUserById( id );
        user.setAuthorityLevel( level );
        userRepository.save( user );
    }
}
