package edu.rit.teamwin.stream;

import static java.lang.String.format;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import components.data.Appointment;
import components.data.AppointmentLabTest;
import components.data.Diagnosis;
import components.data.LabTest;
import components.data.PSC;
import components.data.Patient;
import components.data.Phlebotomist;
import components.data.Physician;


/**
 * @author Alex Aiezza
 * @author Sagar Barbhaya
 * @author Salil Rajadhyaksha
 *
 */
public class AppointmentConverter implements Converter
{

    private final String uri;

    public AppointmentConverter( final String uri )
    {
        this.uri = uri + "LAMSAppointment/Appointments/%s";
    }

    @Override
    public boolean canConvert( @SuppressWarnings ( "rawtypes" ) Class type )
    {
        return Appointment.class.isAssignableFrom( type );
    }

    @Override
    public void marshal( Object source, HierarchicalStreamWriter writer, MarshallingContext context )
    {
        final Appointment appointment = (Appointment) source;

        writer.startNode( "appointment" );
        writer.addAttribute( "date", appointment.getApptdate().toString() );
        writer.addAttribute( "id", appointment.getId() );
        writer.addAttribute( "time", appointment.getAppttime().toString() );

        writer.startNode( "uri" );
        writer.setValue( format( uri, appointment.getId() ) );
        writer.endNode();

        context.convertAnother( appointment.getPatientid() );
        context.convertAnother( appointment.getPhlebid() );
        context.convertAnother( appointment.getPscid() );

        writer.startNode( "allLabTests" );
        List<AppointmentLabTest> allLabTests = Collections.checkedList(
            appointment.getAppointmentLabTestCollection(), AppointmentLabTest.class );
        context.convertAnother( allLabTests );
        writer.endNode();

        writer.endNode();
    }

    @Override
    public Object unmarshal( HierarchicalStreamReader reader, UnmarshallingContext context )
    {
        final Appointment appointment = new Appointment();

        reader.moveDown();
        appointment.setApptdate( Date.valueOf( reader.getValue() ) );
        reader.moveUp();
        reader.moveDown();
        appointment.setAppttime( Time.valueOf( LocalTime.parse( reader.getValue() ) ) );
        reader.moveUp();
        reader.moveDown();
        final Patient patient = new Patient( reader.getValue() );
        reader.moveUp();
        reader.moveDown();
        patient.setPhysician( new Physician( reader.getValue() ) );
        appointment.setPatientid( patient );
        reader.moveUp();
        reader.moveDown();
        appointment.setPscid( new PSC( reader.getValue() ) );
        reader.moveUp();
        reader.moveDown();
        appointment.setPhlebid( new Phlebotomist( reader.getValue() ) );
        reader.moveUp();
        reader.moveDown();
        final List<AppointmentLabTest> labTests = new ArrayList<AppointmentLabTest>();
        while ( reader.hasMoreChildren() )
        {
            reader.moveDown();
            final AppointmentLabTest alt = new AppointmentLabTest();
            alt.setLabTest( new LabTest( reader.getAttribute( "id" ) ) );
            alt.setDiagnosis( new Diagnosis( reader.getAttribute( "dxcode" ) ) );
            labTests.add( alt );
            reader.moveUp();
        }
        reader.moveUp();

        appointment.setAppointmentLabTestCollection( labTests );

        return appointment;
    }
}
