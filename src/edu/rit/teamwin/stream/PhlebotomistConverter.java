package edu.rit.teamwin.stream;

import static java.lang.String.format;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import components.data.Phlebotomist;

/**
 * 
 * @author Alex Aiezza
 * @author Sagar Barbhaya
 * @author Salil Rajadhyaksha
 *
 */
public class PhlebotomistConverter implements Converter
{
    private final String uri;

    public PhlebotomistConverter( final String uri )
    {
        this.uri = uri + "LAMSAppointment/Phlebotomists/%s";
    }

    @Override
    public boolean canConvert( @SuppressWarnings ( "rawtypes" ) Class type )
    {
        return Phlebotomist.class.isAssignableFrom( type );
    }

    @Override
    public void marshal( Object source, HierarchicalStreamWriter writer, MarshallingContext context )
    {
        final Phlebotomist phlebotomist = (Phlebotomist) source;

        writer.startNode( "phlebotomist" );
        writer.addAttribute( "id", phlebotomist.getId() );

        writer.startNode( "uri" );
        writer.setValue( format( uri, phlebotomist.getId() ) );
        writer.endNode();

        writer.startNode( "name" );
        writer.setValue( phlebotomist.getName() );
        writer.endNode();

        writer.endNode();
    }

    @Override
    public Object unmarshal( HierarchicalStreamReader reader, UnmarshallingContext context )
    {
        return null;
    }
}
