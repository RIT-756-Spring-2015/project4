package edu.rit.teamwin.stream;

import static java.lang.String.format;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import components.data.Diagnosis;

/**
 * 
 * @author Alex Aiezza
 * @author Sagar Barbhaya
 * @author Salil Rajadhyaksha
 *
 */
public class DiagnosisConverter implements Converter
{
    private final String uri;

    public DiagnosisConverter( final String uri )
    {
        this.uri = uri + "LAMSAppointment/Diagnoses/%s";
    }

    @Override
    public boolean canConvert( @SuppressWarnings ( "rawtypes" ) Class type )
    {
        return type.equals( Diagnosis.class );
    }

    @Override
    public void marshal( Object source, HierarchicalStreamWriter writer, MarshallingContext context )
    {
        final Diagnosis diagnosis = (Diagnosis) source;

        writer.startNode( "appointmentLabTest" );

        writer.startNode( "code" );
        writer.setValue( diagnosis.getCode() );
        writer.endNode();

        writer.startNode( "name" );
        writer.setValue( diagnosis.getName() );
        writer.endNode();

        writer.startNode( "uri" );
        writer.setValue( format( uri, diagnosis.getCode() ) );
        writer.endNode();

        writer.endNode();
    }

    @Override
    public Object unmarshal( HierarchicalStreamReader reader, UnmarshallingContext context )
    {
        return null;
    }

}
