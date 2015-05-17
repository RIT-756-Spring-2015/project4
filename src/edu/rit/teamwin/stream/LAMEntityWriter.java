package edu.rit.teamwin.stream;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;
import components.data.Appointment;

/**
 * <p>
 * This class marshals the XML output of an appointment object.
 * </p>
 * 
 * @author Alex Aiezza
 * @author Sagar Barbhaya
 * @author Salil Rajadhyaksha
 *
 */
public abstract class LAMEntityWriter <T> implements MessageBodyWriter<T>
{

    @Override
    public long getSize( T type0, Class<?> type, Type type1, Annotation [] antns, MediaType mt )
    {
        /*
         * As of JAX-RS 2.0, the method has been deprecated and the value
         * returned by the method is ignored by a JAX-RS runtime. All
         * MessageBodyWriter implementations are advised to return -1 from the
         * method if the size of the payload is unknown or even just to much of
         * a bother to calculate.
         */

        return -1;
    }

    @Provider
    @Produces ( MediaType.APPLICATION_XML )
    public static class AppointmentWriter extends LAMEntityWriter<Appointment>
    {
        @Override
        public boolean isWriteable( Class<?> type, Type type1, Annotation [] antns, MediaType mt )
        {
            return Appointment.class.isAssignableFrom( type );
        }

        @Override
        public void writeTo(
                Appointment appointment,
                Class<?> type,
                Type type1,
                Annotation [] antns,
                MediaType mt,
                MultivaluedMap<String, Object> mm,
                OutputStream out ) throws IOException, WebApplicationException
        {
            final XStream xstream = new XStream( new PureJavaReflectionProvider() );

            // TODO switch to using an xstream converter (http://xstream.codehaus.org/converter-tutorial.html)
            xstream.alias( "appointment", Appointment.class );
            xstream.aliasAttribute( "date", "apptdate" );
            xstream.aliasAttribute( "id", "id" );
            xstream.aliasAttribute( "time", "appttime" );

            xstream.toXML( appointment, out );
        }
    }

    @Provider
    @Produces ( MediaType.APPLICATION_XML )
    public static class AppointmentsWriter extends LAMEntityWriter<List<Appointment>>
    {
        @Override
        public boolean isWriteable( Class<?> type, Type type1, Annotation [] antns, MediaType mt )
        {
            return List.class.isAssignableFrom( type );
        }

        @Override
        public void writeTo(
                List<Appointment> appointment,
                Class<?> type,
                Type type1,
                Annotation [] antns,
                MediaType mt,
                MultivaluedMap<String, Object> mm,
                OutputStream out ) throws IOException, WebApplicationException
        {
            final XStream xstream = new XStream( new PureJavaReflectionProvider() );

            xstream.alias( "AppointmentsList", List.class );
            xstream.alias( "appointment", Appointment.class );
            xstream.toXML( appointment, out );
        }
    }
}
