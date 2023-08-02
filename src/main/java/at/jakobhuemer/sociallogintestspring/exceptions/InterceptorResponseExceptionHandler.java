package at.jakobhuemer.sociallogintestspring.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.antlr.v4.runtime.misc.Pair;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.time.Instant;

public class InterceptorResponseExceptionHandler {

    @SafeVarargs
    public static boolean thr( HttpServletResponse response, HttpServletRequest request, HttpStatus httpStatus, String details, Pair<String, Object>... extras ) throws IOException {


        StringBuilder responseString = new StringBuilder( """
                {
                """ );


        responseString.append( """
                    "timestamp": %s,
                """.formatted( getTimeStamp() ) );

        responseString.append( """
                    "status": %d,
                """.formatted( httpStatus.value() ) );

        responseString.append( """
                    "message": "%s",
                """.formatted( httpStatus.getReasonPhrase() ) );

        responseString.append( """
                \t"details": "%s\"""".formatted( details ) );

        for( Pair<String, Object> extra : extras ) {
            if( extra.b instanceof String ) {
                responseString.append( """
                        ,
                            "%s": "%s\"""".formatted( extra.a, extra.b ) );
            } else if( extra.b instanceof Long ) {
                responseString.append( """
                        ,
                            "%s": %d""".formatted( extra.a, extra.b ) );
            }
        }

        responseString.append( """
                                
                }""" );

        response.setStatus( httpStatus.value() );
        response.setContentType( "application/json" );
        response.getWriter().write( responseString.toString() );

        return false;
    }

    public static boolean thr( HttpServletResponse response, HttpServletRequest request, HttpStatus httpStatus, Pair<String, Object>... extras ) throws IOException {


        StringBuilder responseString = new StringBuilder( """
                {
                """ );


        responseString.append( """
                    "timestamp": %s,
                """.formatted( getTimeStamp() ) );

        responseString.append( """
                    "status": %d,
                """.formatted( httpStatus.value() ) );

        responseString.append( """
                    "message": "%s\"""".formatted( httpStatus.getReasonPhrase() ) );

        for( Pair<String, Object> extra : extras ) {
            if( extra.b instanceof String ) {
                responseString.append( """
                        ,
                            "%s": "%s\"""".formatted( extra.a, extra.b ) );
            } else if( extra.b instanceof Long ) {
                responseString.append( """
                        ,
                            "%s": %d""".formatted( extra.a, extra.b ) );
            }
        }

        responseString.append( """
                                
                }""" );

        response.setStatus( httpStatus.value() );
        response.setContentType( "application/json" );
        response.getWriter().write( responseString.toString() );

        return false;
    }

    public static boolean thr( HttpServletResponse response, HttpServletRequest request, HttpStatus httpStatus, String details ) throws IOException {


        StringBuilder responseString = new StringBuilder( """
                {
                """ );


        responseString.append( """
                    "timestamp": %s,
                """.formatted( getTimeStamp() ) );

        responseString.append( """
                    "status": %d,
                """.formatted( httpStatus.value() ) );

        responseString.append( """
                    "message": "%s",
                """.formatted( httpStatus.getReasonPhrase() ) );

        responseString.append( """
                \t"details": "%s\"""".formatted( details ) );

        responseString.append( """
                                
                }""" );

        response.setStatus( httpStatus.value() );
        response.setContentType( "application/json" );
        response.getWriter().write( responseString.toString() );

        return false;
    }

    public static boolean thr( HttpServletResponse response, HttpServletRequest request, HttpStatus httpStatus) throws IOException {


        StringBuilder responseString = new StringBuilder( """
                {
                """ );


        responseString.append( """
                    "timestamp": %s,
                """.formatted( getTimeStamp() ) );

        responseString.append( """
                    "status": %d,
                """.formatted( httpStatus.value() ) );

        responseString.append( """
                    "message": "%s\"""".formatted( httpStatus.getReasonPhrase() ) );

        responseString.append( """
                                
                }""" );

        response.setStatus( httpStatus.value() );
        response.setContentType( "application/json" );
        response.getWriter().write( responseString.toString() );

        return false;
    }


    private static String getTimeStamp() {
        return String.valueOf(Instant.now().toEpochMilli());
    }

}
