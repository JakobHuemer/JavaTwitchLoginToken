package at.jakobhuemer.sociallogintestspring.controller;

import at.jakobhuemer.sociallogintestspring.dto.UserDTO;
//import at.jakobhuemer.sociallogintestspring.models.post.Post;
import at.jakobhuemer.sociallogintestspring.exceptions.UserNotFoundException;
import at.jakobhuemer.sociallogintestspring.models.user.*;
import at.jakobhuemer.sociallogintestspring.service.CommentService;
//import at.jakobhuemer.sociallogintestspring.services.PostService;
import at.jakobhuemer.sociallogintestspring.service.UserService;
import org.apache.http.HttpException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import at.jakobhuemer.sociallogintestspring.models.user.AccessScope;

import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;


@RestController
@RequestMapping( "/api/v1" )
public class Controller {

    @Autowired
    UserService userService;
//    @Autowired
//    CommentService commentService;

//    public Controller( UserService userService, /*PostService postService,*/ CommentService commentService ) {
//        this.userService = userService;
////        this.postService = postService;
//        this.commentService = commentService;
//    }

    @GetMapping( value = "/login", params = { "code" }, produces = "application/json" )
    public User codeLogin( @RequestParam( "code" ) String code ) throws HttpException {

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

    @GetMapping( value = "/login", params = {}, produces = "application/json" )
    @RequiredPermissions()
    public User tokenLogin( @RequestHeader( "Authorization" ) String bearerToken ) throws HttpException {
        return userService.login( bearerToken );
    }

    @GetMapping( value = "/users", produces = "application/json" )
    @RequiredPermissions( scopes = { AccessScope.READ_USERS } )
    public ResponseEntity<Iterable<UserDTO>> get() {
        return new ResponseEntity<>( userService.getPublicUsers(), HttpStatus.OK );
    }

//    @PostMapping( value = "/post", produces = "application/json" )
//    @RequiredPermissions( scopes = { AccessScopes.WRITE_POSTS })
//    public Post createPost( @RequestHeader( "Authorization" ) String authToken, @RequestParam String content ) {
//        Post post = postService.createPost( authToken, content );
//
//        return post;
//    }

}
