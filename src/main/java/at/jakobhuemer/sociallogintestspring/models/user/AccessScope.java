package at.jakobhuemer.sociallogintestspring.models.user;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@Getter
public enum AccessScope {
    MANAGE_POSTS(1),
    MANAGE_USERS(2),
    WRITE_POSTS(4),
    EDIT_OWN_POSTS(8),
    READ_USERS(16),
    TWITCH_SEND_MESSAGES(32);

    private final int value;

    AccessScope( int level) {
        this.value = level;
    }


    public static AccessScope from( String s ) {
        for( AccessScope scope : AccessScope.values() ) {
            if( scope.name().equalsIgnoreCase( s ) ) {
                return scope;
            }
        }
        return null;
    }


    public static AccessScope from( int i ) {
        for( AccessScope scope : AccessScope.values() ) {
            if( scope.value == i ) {
                return scope;
            }
        }
        return null;
    }

    public static AccessScope[] from( long l ) {
        List<AccessScope> returnScopes = new ArrayList<>();
        AccessScope[] allScopes = AccessScope.values();

        Arrays.sort( allScopes, Comparator.comparingInt( AccessScope::getValue ) );

        String levels = padStart( Long.toBinaryString( l ), '0', allScopes.length );

        for( int i = 0; i < allScopes.length; i++ ) {
            AccessScope scope = allScopes[ i ];
            if( levels.charAt( i ) == '1' ) {
                returnScopes.add( scope );
            }
        }
        return returnScopes.toArray( new AccessScope[ 0 ] );
    }

    private static String padStart( String s, char filler, int length ) {
        StringBuilder sb = new StringBuilder( length );
        for( int i = 0; i < length - s.length(); i++ ) {
            sb.insert( 0, filler );
        }
        return sb.toString();
    }
}
