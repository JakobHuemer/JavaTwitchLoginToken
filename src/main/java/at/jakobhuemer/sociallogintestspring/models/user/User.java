package at.jakobhuemer.sociallogintestspring.models.user;

import at.jakobhuemer.sociallogintestspring.dto.UserDTO;
//import at.jakobhuemer.sociallogintestspring.models.post.Post;
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
@Builder(access = AccessLevel.PUBLIC)
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
            name = "twitch_id"
    )
    private Long twitchId;

    @Column(
            name = "token",
            columnDefinition = "TEXT"
    )
    private String token;

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
            columnDefinition = "TEXT"
    )
    private String twitchLogin;

    @Column(
            name = "auth_level"
    )
    private AuthorityLevel authorityLevel = AuthorityLevel.USER;

    @Column(
            name = "access_scopes"
    )
    private List<AccessScope> accessScopes;

//    @OneToMany(
//            mappedBy = "author",
//            cascade = CascadeType.ALL,
//            orphanRemoval = true
//    )
//    // q: what does mappedBy do?
//    private List<Post> postList;


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
            throw new RuntimeException( "refreshToken is null" );
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
        return new UserDTO( this.id, this.twitchId, this.twitchLogin, this.authorityLevel, this.accessScopes );
    }

}
