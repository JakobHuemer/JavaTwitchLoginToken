package at.jakobhuemer.sociallogintestspring.models.user;

import lombok.Getter;
import lombok.Setter;

@Getter
public enum AuthorityLevel {
    RESTRICTED( 0 ),
    USER( 1 ),
    MODERATOR( 2 ),
    ADMIN( 3 ),
    SUPER( 4 );

    private final int level;

    AuthorityLevel( int level ) {
        this.level = level;
    }

    public static AuthorityLevel from( String s ) {
        switch( s.toUpperCase() ) {
            case "MOD", "MODERATOR" -> {
                return MODERATOR;
            }
            case "ADMIN" -> {
                return ADMIN;
            }
            case "SUPER" -> {
                return SUPER;
            }
            case "RESTRICTED" -> {
                return RESTRICTED;
            }
            default -> {
                return null;
            }
        }
    }
}
