package edu.rit.teamwin.stream;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import components.data.PSC;

public class PSCConverter implements Converter
{
    @Override
    public boolean canConvert( @SuppressWarnings ( "rawtypes" ) Class type )
    {
        return type.equals( PSC.class );
    }

    @Override
    public void marshal( Object source, HierarchicalStreamWriter writer, MarshallingContext context )
    {
        final PSC psc = (PSC) source;

        writer.addAttribute( "id", psc.getId() );

        writer.startNode( "uri" );
        writer.endNode();

        writer.startNode( "name" );
        writer.setValue( psc.getName() );
        writer.endNode();
    }

    @Override
    public Object unmarshal( HierarchicalStreamReader reader, UnmarshallingContext context )
    {
        // TODO Auto-generated method stub
        return null;
    }

}
