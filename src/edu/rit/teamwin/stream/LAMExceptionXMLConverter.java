package edu.rit.teamwin.stream;

import static java.lang.String.format;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.ext.ExceptionMapper;

/**
 * <p>
 * This class marshals &amp; unmarshals XML to objects and objects to XML.
 * </p>
 * 
 * @author Alex Aiezza
 * @author Sagar Barbhaya
 * @author Salil Rajadhyaksha
 *
 */
public class LAMExceptionXMLConverter implements ExceptionMapper<Exception>
{
    private static final String ERROR = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n<AppointmentList>\n<error>\n%s\n</error></AppointmentList>\n";

    @Override
    public Response toResponse( final Exception exception )
    {
        final ResponseBuilder responseBuilder = Response.serverError();
        responseBuilder.header( "Access-Control-Allow-Origin", "*" );

        final String resp = format( ERROR, exception.getMessage() );
        responseBuilder.entity( resp );

        return responseBuilder.build();
    }

}
