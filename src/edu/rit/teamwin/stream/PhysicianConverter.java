package edu.rit.teamwin.stream;

import static java.lang.String.format;

import java.util.Collections;
import java.util.List;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import components.data.Patient;
import components.data.Physician;

/**
 * 
 * @author Alex Aiezza
 * @author Sagar Barbhaya
 * @author Salil Rajadhyaksha
 *
 */
public class PhysicianConverter implements Converter
{
    private final String uri;

    public PhysicianConverter( final String uri )
    {
        this.uri = uri + "LAMSAppointment/Physicians/%s";
    }

    @Override
    public boolean canConvert( @SuppressWarnings ( "rawtypes" ) Class type )
    {
        return Physician.class.isAssignableFrom( type );
    }

    @Override
    public void marshal( Object source, HierarchicalStreamWriter writer, MarshallingContext context )
    {
        final Physician physician = (Physician) source;

        writer.startNode( "physician" );
        writer.addAttribute( "id", physician.getId() );

        writer.startNode( "uri" );
        writer.setValue( format( uri, physician.getId() ) );
        writer.endNode();

        writer.startNode( "name" );
        writer.setValue( physician.getName() );
        writer.endNode();

        writer.startNode( "patients" );
        List<Patient> patients = Collections.checkedList( physician.getPatientCollection(),
            Patient.class );
        context.convertAnother( patients );
        writer.endNode();

        writer.endNode();
    }

    @Override
    public Object unmarshal( HierarchicalStreamReader reader, UnmarshallingContext context )
    {
        return null;
    }
}
