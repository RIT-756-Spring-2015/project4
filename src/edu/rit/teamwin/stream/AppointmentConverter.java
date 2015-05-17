package edu.rit.teamwin.stream;

import static java.lang.String.format;

import java.util.Collections;
import java.util.List;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import components.data.Appointment;
import components.data.AppointmentLabTest;


/**
 * @author Alex Aiezza
 *
 */
public class AppointmentConverter implements Converter
{

    private final String uri;

    public AppointmentConverter( final String uri )
    {
        this.uri = uri + "Appointments/%s";
    }

    @Override
    public boolean canConvert( @SuppressWarnings ( "rawtypes" ) Class type )
    {
        return type.equals( Appointment.class );
    }

    @Override
    public void marshal( Object source, HierarchicalStreamWriter writer, MarshallingContext context )
    {
        final Appointment appointment = (Appointment) source;

        writer.addAttribute( "date", appointment.getApptdate().toString() );
        writer.addAttribute( "id", appointment.getId() );
        writer.addAttribute( "time", appointment.getAppttime().toString() );

        writer.startNode( "uri" );
        writer.setValue( format( uri, appointment.getId() ) );
        writer.endNode();

        writer.startNode( "patient" );
        context.convertAnother( appointment.getPatientid() );
        writer.endNode();

        writer.startNode( "phlebotomist" );
        context.convertAnother( appointment.getPhlebid() );
        writer.endNode();

        writer.startNode( "allLabTests" );
        List<AppointmentLabTest> allLabTests = Collections.checkedList(
            appointment.getAppointmentLabTestCollection(), AppointmentLabTest.class );
        context.convertAnother( allLabTests );
        writer.endNode();

    }

    @Override
    public Object unmarshal( HierarchicalStreamReader reader, UnmarshallingContext context )
    {
        // TODO Auto-generated method stub
        return null;
    }


}
