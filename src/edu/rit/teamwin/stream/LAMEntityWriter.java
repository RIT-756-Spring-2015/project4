package edu.rit.teamwin.stream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
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
public abstract class LAMEntityWriter <T> implements MessageBodyWriter<T>, MessageBodyReader<T>
{

    @Override
    public long getSize(
            T t,
            Class<?> type,
            Type genericType,
            Annotation [] annotations,
            MediaType mediaType )
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
        public boolean isWriteable(
                Class<?> type,
                Type genericType,
                Annotation [] annotations,
                MediaType mediaType )
        {
            return Appointment.class.isAssignableFrom( type );
        }

        @Override
        public void writeTo(
                Appointment appointment,
                Class<?> type,
                Type genericType,
                Annotation [] annotations,
                MediaType mediaType,
                MultivaluedMap<String, Object> httpHeaders,
                OutputStream out ) throws IOException, WebApplicationException
        {
            final XStream xstream = new XStream();
            xstream.registerConverter( new AppointmentConverter( System.getProperty( "baseUri" ) ) );
            xstream.registerConverter( new PatientConverter() );
            xstream.registerConverter( new PhlebotomistConverter() );
            xstream.registerConverter( new ListConverter() );
            xstream.registerConverter( new AppointmentLabTestConverter() );

            xstream.alias( "appointment", Appointment.class );

            xstream.toXML( appointment, out );
        }

        @Override
        public boolean isReadable(
                Class<?> type,
                Type genericType,
                Annotation [] annotations,
                MediaType mediaType )
        {
            return isWriteable( type, genericType, annotations, mediaType );
        }

        @Override
        public Appointment readFrom(
                Class<Appointment> type,
                Type genericType,
                Annotation [] annotations,
                MediaType mediaType,
                MultivaluedMap<String, String> httpHeaders,
                InputStream entityStream ) throws IOException, WebApplicationException
        {
            final XStream xstream = new XStream();
            xstream.registerConverter( new AppointmentConverter( System.getProperty( "baseUri" ) ) );
            xstream.registerConverter( new PatientConverter() );
            xstream.registerConverter( new PhlebotomistConverter() );
            xstream.registerConverter( new ListConverter() );
            xstream.registerConverter( new AppointmentLabTestConverter() );

            xstream.alias( "appointment", Appointment.class );

            return (Appointment) xstream.fromXML( entityStream );
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

        @Override
        public boolean isReadable(
                Class<?> type,
                Type genericType,
                Annotation [] annotations,
                MediaType mediaType )
        {
            return isWriteable( type, genericType, annotations, mediaType );
        }

        @Override
        public List<Appointment> readFrom(
                Class<List<Appointment>> type,
                Type genericType,
                Annotation [] annotations,
                MediaType mediaType,
                MultivaluedMap<String, String> httpHeaders,
                InputStream entityStream ) throws IOException, WebApplicationException
        {
            // TODO Auto-generated method stub
            return null;
        }
    }
}
