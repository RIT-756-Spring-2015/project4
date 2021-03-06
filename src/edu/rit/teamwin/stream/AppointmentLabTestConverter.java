package edu.rit.teamwin.stream;

import static java.lang.String.format;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import components.data.AppointmentLabTest;

/**
 * 
 * @author Alex Aiezza
 * @author Sagar Barbhaya
 * @author Salil Rajadhyaksha
 *
 */
public class AppointmentLabTestConverter implements Converter
{
    private final String uri;

    public AppointmentLabTestConverter( final String uri )
    {
        this.uri = uri + "LAMSAppointment/AppointmentLabTestPKs/%s";
    }

    @Override
    public boolean canConvert( @SuppressWarnings ( "rawtypes" ) Class type )
    {
        return type.equals( AppointmentLabTest.class );
    }

    @Override
    public void marshal( Object source, HierarchicalStreamWriter writer, MarshallingContext context )
    {
        final AppointmentLabTest alt = (AppointmentLabTest) source;

        writer.startNode( "appointmentLabTest" );
        writer.addAttribute( "appointmentId", alt.getAppointment().getId() );
        writer.addAttribute( "dxcode", alt.getDiagnosis().getCode() );
        writer.addAttribute( "labTestId", alt.getLabTest().getId() );

        writer.startNode( "uri" );
        writer.setValue( format( uri, alt.getKey().getApptid() ) );
        writer.endNode();

        writer.endNode();
    }

    @Override
    public Object unmarshal( HierarchicalStreamReader reader, UnmarshallingContext context )
    {
        // TODO Auto-generated method stub
        return null;
    }

}
