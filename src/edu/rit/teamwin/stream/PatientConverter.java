package edu.rit.teamwin.stream;

import static java.lang.String.format;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import components.data.Patient;

/**
 * 
 * @author Alex Aiezza
 * @author Sagar Barbhaya
 * @author Salil Rajadhyaksha
 *
 */
public class PatientConverter implements Converter
{
    private final String uri;

    public PatientConverter( final String uri )
    {
        this.uri = uri + "LAMSAppointment/Patients/%s";
    }

    @Override
    public boolean canConvert( @SuppressWarnings ( "rawtypes" ) Class type )
    {
        return Patient.class.isAssignableFrom( type );
    }

    @Override
    public void marshal( Object source, HierarchicalStreamWriter writer, MarshallingContext context )
    {
        final Patient patient = (Patient) source;

        writer.startNode( "patient" );
        writer.addAttribute( "id", patient.getId() );

        writer.startNode( "uri" );
        writer.setValue( format( uri, patient.getId() ) );
        writer.endNode();

        writer.startNode( "name" );
        writer.setValue( patient.getName() );
        writer.endNode();

        writer.startNode( "address" );
        writer.setValue( patient.getAddress() );
        writer.endNode();

        writer.startNode( "insurance" );
        writer.setValue( patient.getInsurance() + "" );
        writer.endNode();

        writer.startNode( "dob" );
        writer.setValue( patient.getDateofbirth().toString() );
        writer.endNode();

        writer.endNode();
    }

    @Override
    public Object unmarshal( HierarchicalStreamReader reader, UnmarshallingContext context )
    {
        return null;
    }

}
