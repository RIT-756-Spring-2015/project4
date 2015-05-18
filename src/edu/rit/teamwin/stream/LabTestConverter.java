package edu.rit.teamwin.stream;

import static java.lang.String.format;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import components.data.LabTest;

/**
 * 
 * @author Alex Aiezza
 * @author Sagar Barbhaya
 * @author Salil Rajadhyaksha
 *
 */
public class LabTestConverter implements Converter
{
    private final String uri;

    public LabTestConverter( final String uri )
    {
        this.uri = uri + "LAMSAppointment/LabTests/%s";
    }

    @Override
    public boolean canConvert( @SuppressWarnings ( "rawtypes" ) Class type )
    {
        return type.equals( LabTest.class );
    }

    @Override
    public void marshal( Object source, HierarchicalStreamWriter writer, MarshallingContext context )
    {
        final LabTest labTest = (LabTest) source;

        writer.startNode( "appointmentLabTest" );
        writer.addAttribute( "id", labTest.getId() );

        writer.startNode( "name" );
        writer.setValue( labTest.getName() );
        writer.endNode();

        writer.startNode( "cost" );
        writer.setValue( format( "%.2f", labTest.getCost() ) );
        writer.endNode();

        writer.startNode( "uri" );
        writer.setValue( format( uri, labTest.getId() ) );
        writer.endNode();

        writer.endNode();
    }

    @Override
    public Object unmarshal( HierarchicalStreamReader reader, UnmarshallingContext context )
    {
        return null;
    }

}
