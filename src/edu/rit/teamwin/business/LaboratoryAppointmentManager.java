package edu.rit.teamwin.business;

import static java.lang.String.format;

import java.sql.Date;
import java.sql.Time;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import components.data.Appointment;
import components.data.DB;
import components.data.IComponentsData;
import components.data.PSC;
import components.data.Patient;
import components.data.Phlebotomist;

import edu.rit.teamwin.LaboratoryAppointmentService;
import edu.rit.teamwin.exceptions.AppointmentNotFoundException;
import edu.rit.teamwin.exceptions.AppointmentNotValidException;
import edu.rit.teamwin.exceptions.MaximumAppointmentCapacityReachedException;

/**
 * <p>
 * This class represents the <strong>BUSINESS LAYER</strong>. An instance of
 * this class will be directly utilized by the
 * {@link LaboratoryAppointmentService Service Layer}.
 * </p>
 * 
 * @author Alex Aiezza
 * @author Sagar Barbhaya
 * @author Salil Rajadhyaksha
 *
 */
public class LaboratoryAppointmentManager
{
    private static final String   APPOINTMENT_TABLE            = "Appointment";

    private static final String   APPOINTMENT_LAB_TEST_TABLE   = "AppointmentLabTest";

    private static final String   DIAGNOSIS_TABLE              = "Diagnosis";

    private static final String   LABTEST_TABLE                = "Labtest";

    private static final String   PATIENT_SERVICE_CENTER_TABLE = "PSC";

    private static final String   PATIENT_TABLE                = "Patient";

    private static final String   PHLEBOTOMIST_TABLE           = "Phlebotomist";

    private static final String   PHYSICIAN_TABLE              = "Physician";

    private static final String   NO_FILTER                    = "";

    public static final int       MAX_APPOINTMENT_ID           = 0xFFFFF;

    private final Log             LOG                          = LogFactory.getLog( getClass() );

    private final IComponentsData dataLayer;

    /**
     * @param dataLayer
     */
    public LaboratoryAppointmentManager( final IComponentsData dataLayer )
    {
        this.dataLayer = dataLayer;
    }

    /**
     * @return a list of all appointments
     */
    public List<Appointment> getAppointments()
    {
        final List<Object> results = dataLayer.getData( APPOINTMENT_TABLE, NO_FILTER );

        final List<Appointment> appointments = new ArrayList<Appointment>( results.size() );

        results.forEach( app -> appointments.add( (Appointment) app ) );

        return appointments;
    }

    public Appointment getAppointment( final String appointmentId )
            throws AppointmentNotFoundException
    {
        final List<Object> results = dataLayer.getData( APPOINTMENT_TABLE,
            format( "id='%s'", appointmentId ) );

        if ( results.size() <= 0 )
        {
            throw new AppointmentNotFoundException( appointmentId );
        }

        if ( results.size() > 1 )
        {
            /*
             * This really should never happen if the database has Id as the
             * primary key
             */
            LOG.error( format( "Too many appointments with the same Id '%s'... somehow.",
                appointmentId ) );
        }

        final Appointment appointment = (Appointment) results.get( 0 );

        return appointment;
    }

    public Appointment setupAppointment(
            final java.util.Date date,
            final Patient patient,
            final PSC psc,
            final Phlebotomist phlebotomist ) throws MaximumAppointmentCapacityReachedException
    {
        // Create the appointment object
        final Appointment appointment = new Appointment( generateID(), new java.sql.Date(
                date.getTime() ), new Time( date.getTime() ) );
        appointment.setPatientid( patient );
        appointment.setPhlebid( phlebotomist );
        appointment.setPscid( psc );

        // TODO make sure these throw exceptions if something goes wrong
        if ( validateAppointment( appointment ) )
        {
            dataLayer.addData( appointment );
        }

        return appointment;

    }

    public boolean updateAppointment(
            final Appointment oldAppointment,
            final Appointment updatedAppointment )
    {
        assert ( oldAppointment.getId().equals( updatedAppointment.getId() ) );
        // see if the updatedAppointment will schedule
        // if so, we're good!
        // if not, reschedule the old appointment and return false
        return false;
    }

    public boolean validateAppointment( final Appointment appointment )
            throws AppointmentNotValidException
    {
        // APPOINTMENT VALIDITY CHECKS!

        /* Check if requested phlebotomist is not in another appointment */

        /*
         * Check if requested phlebotomist is not within 30 minutes of requested
         * PSC (for travel time) (And 15 minutes for the appointment)
         */

        return true;
    }

    public boolean deleteAppointment( final Appointment appointment )
    {
        // Delete the appointment
        return false;
    }

    /**
     * @return a generated string with a maximum size of 5
     * @throws MaximumAppointmentCapacityReachedException
     */
    private String generateID() throws MaximumAppointmentCapacityReachedException
    {
        // Check if id already exists
        final List<String> appointmentIds = getAppointments().stream().map( app -> app.getId() )
                .collect( Collectors.toList() );

        final Integer firstAppointmentId = Integer.decode( "0x".concat( appointmentIds.get( 0 ) ) );
        Integer appointmentId = firstAppointmentId;

        boolean complete = false;
        do
        {
            appointmentId = appointmentId < MAX_APPOINTMENT_ID ? appointmentId + 1 : 0;

            if ( appointmentId.equals( firstAppointmentId ) )
            {
                throw new MaximumAppointmentCapacityReachedException();
            }

            try
            {
                getAppointment( Integer.toHexString( appointmentId ) );
            } catch ( AppointmentNotFoundException e )
            {
                complete = true;
            }

        } while ( !complete );

        return Integer.toHexString( appointmentId );
    }

    public static void main( String [] args )
    {
        LaboratoryAppointmentManager lam = new LaboratoryAppointmentManager( new DB() );

        // Start it up!
        lam.dataLayer.initialLoad( "LAMS" );

        printLine();

        // Get all appointments
        System.out.println( lam.getAppointments().stream().map( app -> app.getId() )
                .collect( Collectors.toList() ) );

        printLine();

        // Try to setup a new appointment
        try
        {
            final Appointment appointment = lam
                    .setupAppointment( Date.from( Instant.now() ), ( (Patient) lam.dataLayer
                            .getData( PATIENT_TABLE, NO_FILTER ).get( 0 ) ), ( (PSC) lam.dataLayer
                            .getData( PATIENT_SERVICE_CENTER_TABLE, NO_FILTER ).get( 0 ) ),
                        ( (Phlebotomist) lam.dataLayer.getData( PHLEBOTOMIST_TABLE, NO_FILTER )
                                .get( 0 ) ) );

            System.out.println( appointment );

        } catch ( MaximumAppointmentCapacityReachedException e )
        {
            lam.LOG.error( e.getMessage() );
        }

        printLine();

        // Get all appointments
        System.out.println( lam.getAppointments().stream().map( app -> app.getId() )
                .collect( Collectors.toList() ) );

        printLine();

        try
        {
            // Get appointment 710
            System.out.println( lam.getAppointment( "710" ) );
            printLine();
            // Get appointment 702 ( And fail on purpose )
            System.out.println( lam.getAppointment( "702" ) );

        } catch ( AppointmentNotFoundException e )
        {
            lam.LOG.error( e.getMessage() );
        }

        printLine();

    }

    private final static void printLine()
    {
        for ( int i = 0; i <= 70; i++ )
            System.out.print( i >= 70 ? "\n" : "-" );
    }
}
