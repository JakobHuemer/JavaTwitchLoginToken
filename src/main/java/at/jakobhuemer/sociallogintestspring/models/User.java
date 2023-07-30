package at.jakobhuemer.sociallogintestspring.models;

import at.jakobhuemer.sociallogintestspring.dto.UserDTO;
import at.jakobhuemer.sociallogintestspring.settings.Settings;
import jakarta.persistence.*;
import lombok.*;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;

@Entity( name = "User" )
@Table(
        name = "\"users\"",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "access_token_unique",
                        columnNames = "access_token"
                ),
                @UniqueConstraint(
                        name = "twitch_id_unique",
                        columnNames = "twitch_id"
                )
        }
)
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class User {

    @Id
    @SequenceGenerator(
            name = "id_sequence",
            sequenceName = "id_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "id_sequence"
    )
    private Long id;

    @Column(
            name = "twitch_id",
            columnDefinition = "INT"
    )
    private Long twitchId;

    @Column(
            name = "token",
            columnDefinition = "TEXT"
    )
    private String token;

    @Column(
            name = "twitch_code",
            columnDefinition = "TEXT"
    )
    private String twitchCode;

    @Column(
            name = "refresh_token",
            columnDefinition = "TEXT"
    )
    private String refreshToken;

    @Column(
            name = "access_token",
            columnDefinition = "TEXT"
    )
    private String accessToken;

    @Column(
            name = "twitch_login",
            columnDefinition = "TEXT",
            nullable = false
    )
    private String twitchLogin;

    @Column(
            name = "auth_level",
            columnDefinition = "INT",
            nullable = true
    )
    private AuthorityLevel authorityLevel = AuthorityLevel.USER;


    public User( User.Builder builder ) {
        this.token = builder.token;
        this.twitchCode = builder.twitchCode;
        this.refreshToken = builder.refreshToken;
        this.accessToken = builder.accessToken;
        this.twitchLogin = builder.twitchLogin;
        this.authorityLevel = builder.authorityLevel;
        this.twitchId = builder.twitchId;
    }


    /**
     * @param code The code that is provided by the frontend
     * @return {@code String[]}
     * <p>
     * 0: twitch_login
     * </p>
     * <p>
     * 1: user_access_token
     * </p>
     * <p>
     * 2: user_refresh_token
     * </p>
     * @throws RuntimeException if anything goes wrong
     * @Requests <p>
     * GET https://id.twitch.tv/oauth2/token
     * </p>
     * <p>
     * GET https://id.twitch.tv/oauth2/validate
     * </p>
     */
    public static Object[] processCode( String code ) throws IOException {
//        this.twitchCode = code;

        HttpClient httpClient = HttpClientBuilder.create().build();

        String url = "https://id.twitch.tv/oauth2/token";

        HttpPost request = new HttpPost( url );

        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
        pairs.add( new BasicNameValuePair( "client_id", Settings.get().CLIENT_ID ) );
        pairs.add( new BasicNameValuePair( "client_secret", Settings.get().CLIENT_SECRET ) );
        pairs.add( new BasicNameValuePair( "code", code ) );
        pairs.add( new BasicNameValuePair( "grant_type", "authorization_code" ) );
        pairs.add( new BasicNameValuePair( "redirect_uri", Settings.get().TWITCH_OAUTH_REDIRECT_URI ) );


//        System.out.println( "url: " + url );
//        System.out.println( "client_id: " + Settings.get().CLIENT_ID );
//        System.out.println( "client_secret: " + Settings.get().CLIENT_SECRET );
//        System.out.println( "code: " + code );
//        System.out.println( "grant_type: authorization_code" );
//        System.out.println( "redirect_uri: " + Settings.get().TWITCH_OAUTH_REDIRECT_URI );

        HttpResponse response;
        request.setEntity( new UrlEncodedFormEntity( pairs ) );
        response = httpClient.execute( request );


        JSONObject jsonObject;
        jsonObject = new JSONObject( new BasicResponseHandler().handleResponse( response ) );
        String refreshToken = jsonObject.getString( "refresh_token" );
        String accessToken = jsonObject.getString( "access_token" );


        HttpGet reqValidate = new HttpGet( "https://id.twitch.tv/oauth2/validate" );
        HttpResponse validateResponse;
//        reqValidate.setHeader( "Authorization", "OAuth oz0o2yk194bmlv6jug3la9e7d2wv7x" );
        reqValidate.setHeader( "Authorization", "OAuth " + accessToken );

        validateResponse = httpClient.execute( reqValidate );


        JSONObject validateJson;
        validateJson = new JSONObject( new BasicResponseHandler().handleResponse( validateResponse ) );



        String twitchLogin = validateJson.getString( "login" );
        Long twitchId = validateJson.getLong( "user_id" );
        System.out.println("------------------------------------------------------------------------------------------------------------------");
        System.out.println("TWITCHLOGIN: " + twitchLogin);
        System.out.println("------------------------------------------------------------------------------------------------------------------");


        return new Object[]{ twitchLogin, accessToken, refreshToken, twitchId };
    }


    /**
     * updates the refresh- and access_token of the user
     */

    public void refreshToken() throws IOException {

        Object[] accessData = new String[ 3 ];

        if( this.refreshToken == null ) {
            if( !this.twitchCode.startsWith( "old: " ) ) {
                accessData = User.processCode( this.twitchCode );
                this.twitchLogin = (String) accessData[ 0 ];
                this.accessToken = (String) accessData[ 1 ];
                this.refreshToken = (String) accessData[ 2 ];
            } else {
                System.out.println( "NO REFRESH TOKEN SET!" );
                throw new RuntimeException();
            }
        }

        HttpClient httpClient = HttpClientBuilder.create().build();

        String url = "https://id.twitch.tv/oauth2/token";

        HttpPost request = new HttpPost( url );

        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
        pairs.add( new BasicNameValuePair( "refresh_token", this.refreshToken ) );
        pairs.add( new BasicNameValuePair( "client_id", Settings.get().CLIENT_ID ) );
        pairs.add( new BasicNameValuePair( "client_secret", Settings.get().CLIENT_SECRET ) );
        pairs.add( new BasicNameValuePair( "grant_type", "refresh_token" ) );

        HttpResponse response;

        try {
            request.setEntity( new UrlEncodedFormEntity( pairs ) );
            response = httpClient.execute( request );
        } catch( IOException e ) {
            throw new RuntimeException( e );
        }

        JSONObject jsonObject;

        try {
            jsonObject = new JSONObject( new BasicResponseHandler().handleResponse( response ) );
        } catch( IOException e ) {
            throw new RuntimeException( e );
        }

        try {
            this.refreshToken = jsonObject.getString( "refresh_token" );
            this.accessToken = jsonObject.getString( "access_token" );
        } catch( JSONException e ) {
            throw new RuntimeException();
        }
    }

    public UserDTO toDTO() {
        return new UserDTO( this.id, this.twitchId, this.twitchLogin, this.authorityLevel );
    }

    // Builder
    public static class Builder {

        private String twitchLogin = null;
        private Long twitchId = null;
        private String twitchCode = null;
        private String refreshToken = null;
        private String accessToken = null;
        private String token = null;
        private AuthorityLevel authorityLevel = AuthorityLevel.USER;

        public Builder() {

        }

        public Builder setAuthorityLevel( AuthorityLevel authorityLevel ) {
            this.authorityLevel = authorityLevel;
            return this;
        }

        public Builder setTwitchId( Long twitchId ) {
            this.twitchId = twitchId;
            return this;
        }

        public Builder setAuthorityLevel( String authorityString ) {
            this.authorityLevel = switch( authorityString.toUpperCase() ) {
                case "MOD" -> AuthorityLevel.MOD;
                case "ADMIN" -> AuthorityLevel.ADMIN;
                default -> AuthorityLevel.USER;
            };
            return this;
        }


        public Builder setTwitchLogin( String twitchLogin ) {
            this.twitchLogin = twitchLogin;
            return this;
        }

        public Builder setTwitchCode( String twitchCode ) {
            this.twitchCode = twitchCode;
            return this;
        }

        public Builder setRefreshToken( String refreshToken ) {
            this.refreshToken = refreshToken;
            return this;
        }

        public Builder setToken( String token ) {
            this.token = token;
            return this;
        }

        public Builder setAccessToken( String accessToken ) {
            this.accessToken = accessToken;
            return this;
        }


        public User build() {
            return new User( this );
        }
    }

    public static enum AuthorityLevel {
        RESTRICTED(-1), USER( 0 ), MOD( 1 ), ADMIN( 2 );

        private final int level;

        AuthorityLevel( int level ) {
            this.level = level;
        }

        public int getLevel() {
            return level;
        }
    }


    @Retention( RetentionPolicy.RUNTIME )
    @Target( ElementType.METHOD )
    public @interface AuthLevelRequired {
        AuthorityLevel value();

    }


}
