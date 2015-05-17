package edu.rit.teamwin.stream;

import java.util.List;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class ListConverter implements Converter
{

    @Override
    public boolean canConvert( @SuppressWarnings ( "rawtypes" ) Class type )
    {
        return List.class.isAssignableFrom( type );
    }

    @Override
    public void marshal( Object source, HierarchicalStreamWriter writer, MarshallingContext context )
    {
        final List<?> list = (List<?>) source;
        list.forEach( element -> context.convertAnother( element ) );
    }

    @Override
    public Object unmarshal( HierarchicalStreamReader reader, UnmarshallingContext context )
    {
        // TODO Auto-generated method stub
        return null;
    }


}
