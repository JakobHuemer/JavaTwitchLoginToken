package at.jakobhuemer.sociallogintestspring.controller;


import at.jakobhuemer.sociallogintestspring.exceptions.UserNotFoundException;
import at.jakobhuemer.sociallogintestspring.models.user.*;
import at.jakobhuemer.sociallogintestspring.service.UserService;
import org.apache.http.client.HttpResponseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping( "/api/v1/users" )
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping( value = "/{id}/scopes" )
    @RequiredPermissions( scopes = { AccessScope.MANAGE_USERS }, level = AuthorityLevel.ADMIN )
    public HashMap<String, Object> modManageUserScopes( @RequestBody HashMap<String, Object> payload, @PathVariable Long id ) {
        return editUserScopes( payload, id );
    }


    @PostMapping( value = "/me/scopes" )
    @RequiredPermissions
    public HashMap<String, Object> userManageOwnScopes( @RequestBody HashMap<String, Object> payload, @AuthUser User authUser ) {
        return editUserScopes( payload, authUser.getId() );
    }


    private HashMap<String, Object> editUserScopes( HashMap<String, Object> payload, Long id ) {

        List<AccessScope> addedScopes = new ArrayList<>();
        List<AccessScope> removedScopes = new ArrayList<>();


        payload.forEach( ( key, val ) -> {
            if( !( val instanceof List<?> ) ) {
                return;
            }

            ( (List<?>) val ).forEach( scopeString -> {
                if( !( scopeString instanceof String ) ) {
                    throw new ResponseStatusException( HttpStatus.BAD_REQUEST, "Scopes must be Strings" );
                } else {

                    AccessScope scope = AccessScope.from( (String) scopeString );

                    if( scope == null ) {
                        return;
                    }

                    try {
                        if( key.equalsIgnoreCase( "add" ) ) {
                            if( userService.addAccessScope( id, scope ) ) addedScopes.add( scope );

                        } else if( key.equalsIgnoreCase( "remove" ) ) {
                            if( userService.removeAccessScope( id, scope ) ) removedScopes.add( scope );

                        }

                    } catch( UserNotFoundException e ) {
                        throw new ResponseStatusException( HttpStatus.INTERNAL_SERVER_ERROR, "Could not find user id" );
                    }

                }
            } );

        } );

        // return hashmap with added and removed scopes
        HashMap<String, Object> response = new HashMap<>();
        response.put( "added", addedScopes.toArray( new AccessScope[ 0 ] ) );
        response.put( "removed", removedScopes.toArray( new AccessScope[ 0 ] ) );
        response.put( "user_id", id );
        return response;
    }


    @PostMapping( value = "/{id}/authority-level" )
    @RequiredPermissions( scopes = { AccessScope.MANAGE_USERS }, level = AuthorityLevel.MODERATOR )
    private void editUserAuthLevel( @RequestBody HashMap<String, Object> payload, @PathVariable Long id, @AuthUser User exUser ) {

        System.out.println( payload );

        if( payload.get( "level" ) instanceof String ) {
            AuthorityLevel levelToSet = AuthorityLevel.from( (String) payload.get( "level" ) );
            if( levelToSet == null ) {

                return;
            }

            User opUser;
            try {
                opUser = userService.getUserById( id );
            } catch( UserNotFoundException e ) {
                throw new ResponseStatusException( HttpStatus.NOT_FOUND, "Invalid userid %d".formatted( id ) );
            }

            if( opUser.getToken().equals( exUser.getToken() ) ) {
                throw new ResponseStatusException( HttpStatus.FORBIDDEN, "Cannot edit own level" );
            }

            if( ( exUser.getAuthorityLevel().getLevel() > opUser.getAuthorityLevel().getLevel() && exUser.getAuthorityLevel().getLevel() > levelToSet.getLevel() ) ||
                    exUser.getAuthorityLevel() == AuthorityLevel.SUPER ) {
                try {
                    userService.setAuthorityLevel( opUser.getId(), levelToSet );
                } catch( UserNotFoundException e ) {
                    throw new ResponseStatusException( HttpStatus.INTERNAL_SERVER_ERROR, "Could not set AuthLevel from user %s to %s".formatted( opUser.getId(), levelToSet.name() ) );
                }

            } else {
                throw new ResponseStatusException( HttpStatus.FORBIDDEN, "User can only manage users under own level and levels under own level" );
            }
        }
    }

}