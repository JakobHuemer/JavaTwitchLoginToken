package at.jakobhuemer.sociallogintestspring.tools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TokenGenerator {
    private static TokenGenerator instance = null;
    private final int TOKEN_LENGTH = 32;
    private final char[] choices = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_".toCharArray();

    private final List<String> tokenList = new ArrayList<String>();

    private TokenGenerator() {
    }

    public static TokenGenerator get() {
        if( instance == null ) {
            instance = new TokenGenerator();
        }
        return instance;
    }

    public String new_() {
        StringBuilder token = new StringBuilder();

        do {
            // generate token with length of TOKEN_LENGTH
            for( int i = 0; i < TOKEN_LENGTH; i++ ) {
                token.append( choices[ (int) ( Math.random() * choices.length ) ] );
            }
        } while( tokenList.contains( token.toString() ) );

        tokenList.add( token.toString() );
        return token.toString();
    }

    public void load( String[] tokens ) {
        Collections.addAll( tokenList, tokens );
    }
}
