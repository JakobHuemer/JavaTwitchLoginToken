package at.jakobhuemer.sociallogintestspring.controller;

import at.jakobhuemer.sociallogintestspring.dto.UserDTO;
import at.jakobhuemer.sociallogintestspring.models.User;
import at.jakobhuemer.sociallogintestspring.services.UserService;
import org.apache.http.HttpException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping( "/api/v1" )
public class Controller {

    UserService userService;

    public Controller( UserService userService ) {
        this.userService = userService;
    }

    @GetMapping( value = "/login", params = { "code" }, produces = "application/json" )
    public User login( @RequestParam( "code" ) String code ) throws HttpException {


        Object[] accessData;
        try {
            accessData = User.processCode( code );
        } catch( IOException e ) {
            throw new ResponseStatusException( HttpStatus.BAD_REQUEST, "Code is not valid!" );
        }

        try {
            return userService.login( (String) accessData[ 0 ], (String) accessData[ 1 ], (String) accessData[ 2 ], (Long) accessData[ 3 ] );
        } catch( ArrayIndexOutOfBoundsException e ) {
            throw new ResponseStatusException( HttpStatus.INTERNAL_SERVER_ERROR, "WTF xD hahahhhahaha einfoch index out of bound im controller wtf xD lmao xD", new RuntimeException() );
        }
    }

    @GetMapping( value = "/users", produces = "application/json" )
    @User.AuthLevelRequired( User.AuthorityLevel.USER )
    public ResponseEntity<Iterable<UserDTO>> get() {
        return new ResponseEntity<>( userService.getPublicUsers(), HttpStatus.OK );
    }

}
