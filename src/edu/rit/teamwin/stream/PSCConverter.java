package edu.rit.teamwin.stream;

import static java.lang.String.format;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import components.data.PSC;

/**
 * 
 * @author Alex Aiezza
 * @author Sagar Barbhaya
 * @author Salil Rajadhyaksha
 *
 */
public class PSCConverter implements Converter
{
    private final String uri;

    public PSCConverter( final String uri )
    {
        this.uri = uri + "LAMSAppointment/PSCs/%s";
    }

    @Override
    public boolean canConvert( @SuppressWarnings ( "rawtypes" ) Class type )
    {
        return PSC.class.isAssignableFrom( type );
    }

    @Override
    public void marshal( Object source, HierarchicalStreamWriter writer, MarshallingContext context )
    {
        final PSC psc = (PSC) source;

        writer.startNode( "psc" );
        writer.addAttribute( "id", psc.getId() );

        writer.startNode( "uri" );
        writer.setValue( format( uri, psc.getId() ) );
        writer.endNode();

        writer.startNode( "name" );
        writer.setValue( psc.getName() );
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
