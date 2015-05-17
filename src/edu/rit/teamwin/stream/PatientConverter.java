package edu.rit.teamwin.stream;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import components.data.Patient;

public class PatientConverter implements Converter
{

    @Override
    public boolean canConvert( @SuppressWarnings ( "rawtypes" ) Class type )
    {
        return type.equals( Patient.class );
    }

    @Override
    public void marshal( Object source, HierarchicalStreamWriter writer, MarshallingContext context )
    {
        final Patient patient = (Patient) source;

        writer.addAttribute( "id", patient.getId() );

        writer.startNode( "uri" );
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

    }

    @Override
    public Object unmarshal( HierarchicalStreamReader reader, UnmarshallingContext context )
    {
        // TODO Auto-generated method stub
        return null;
    }

}
