package edu.rit.teamwin.business;

import static java.lang.String.format;
import static java.lang.System.getProperty;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_TIME;

import java.sql.Date;
import java.sql.Time;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
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
import edu.rit.teamwin.exceptions.AppointmentNotDuringBusinessHoursException;
import edu.rit.teamwin.exceptions.AppointmentNotValidException;
import edu.rit.teamwin.exceptions.ItemNotFoundException;
import edu.rit.teamwin.exceptions.MaximumAppointmentCapacityReachedException;
import edu.rit.teamwin.utils.PropertiesSetter;

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
    static
    {
        try
        {
            PropertiesSetter.class.newInstance();
        } catch ( InstantiationException | IllegalAccessException e )
        {
            e.printStackTrace();
        }
    }

    private static final String APPOINTMENT_TABLE            = "Appointment";

    private static final String APPOINTMENT_LAB_TEST_TABLE   = "AppointmentLabTest";

    private static final String DIAGNOSIS_TABLE              = "Diagnosis";

    private static final String LABTEST_TABLE                = "Labtest";

    private static final String PATIENT_SERVICE_CENTER_TABLE = "PSC";

    private static final String PATIENT_TABLE                = "Patient";

    private static final String PHLEBOTOMIST_TABLE           = "Phlebotomist";

    private static final String PHYSICIAN_TABLE              = "Physician";

    private static final String NO_FILTER                    = "";

    public static final int     MAX_APPOINTMENT_ID           = 0xFFFFF;

    public static final LocalTime OPEN_TIME, CLOSE_TIME;

    public static final Duration  APPOINTMENT_DURATION, PHLEBOTOMIST_TRAVEL_DURATION;

    static
    {
        OPEN_TIME = LocalTime.parse( getProperty( "open.localtime" ), ISO_LOCAL_TIME );
        CLOSE_TIME = LocalTime.parse( getProperty( "close.localtime" ), ISO_LOCAL_TIME );
        APPOINTMENT_DURATION = Duration.parse( getProperty( "appointment.duration" ) );
        PHLEBOTOMIST_TRAVEL_DURATION = Duration.parse( getProperty( "phleb.travel.duration" ) );
    }

    private final Log             LOG = LogFactory.getLog( getClass() );

    private final IComponentsData dataLayer;

    /**
     * @param dataLayer
     */
    public LaboratoryAppointmentManager( final IComponentsData dataLayer )
    {
        this.dataLayer = dataLayer;
    }

    /**
     * @param table
     *            The database table to acquire objects from
     * @param filter
     *            The WHERE clause to limit the the objects from the table
     * @return a list of objects requested from the database. List will be empty
     *         if there were no entries in the table.
     */
    @SuppressWarnings ( "unchecked" )
    public <T> List<T> getData( final String table, final String filter )
    {
        final List<Object> results = dataLayer.getData( table, filter );

        final List<T> items = new ArrayList<T>( results.size() );

        results.forEach( item -> items.add( (T) item ) );

        return items;
    }

    public <T> T getItemByKey( final String table, final String keyName, final String key )
            throws ItemNotFoundException
    {
        final List<T> items = this.<T> getData( table, format( "%s=%s", keyName, key ) );

        if ( items.size() <= 0 )
        {
            throw new ItemNotFoundException( table, format( "%s=%s", keyName, key ) );
        }

        if ( items.size() > 1 )
        {
            /*
             * This really should never happen if the database has the given
             * filter as the primary key
             */
            LOG.error( format( "Too many items with the same primary key '%s'... somehow.",
                format( "%s=%s", keyName, key ) ) );
        }

        return items.get( 0 );
    }

    public Appointment setupAppointment(
            final java.util.Date date,
            final Patient patient,
            final PSC psc,
            final Phlebotomist phlebotomist ) throws MaximumAppointmentCapacityReachedException,
            AppointmentNotValidException, ItemNotFoundException
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
            throws AppointmentNotValidException, ItemNotFoundException
    {
        // APPOINTMENT VALIDITY CHECKS!

        /* Make sure appointment time is between time of open and time of close */

        final LocalTime apptTime = appointment.getAppttime().toLocalTime();

        if ( apptTime.isBefore( OPEN_TIME ) || apptTime.isAfter( CLOSE_TIME ) )
        {
            throw new AppointmentNotDuringBusinessHoursException( apptTime );
        }

        /* Check if requested phlebotomist is not in another appointment */
        final Phlebotomist requestedPhlebotomist = getItemByKey( PHLEBOTOMIST_TABLE, "id",
            appointment.getPhlebid().getKey() );

        final List<Appointment> conflictingAppointments = requestedPhlebotomist
                .getAppointmentCollection()
                .stream()
                .filter(
                    app -> {
                        if ( !appointment.getApptdate().toLocalDate()
                                .equals( app.getApptdate().toLocalDate() ) )
                            return true;
                        else
                        {
                            final LocalTime appStart = app.getAppttime().toLocalTime();
                            final LocalTime appEnd = app.getAppttime().toLocalTime()
                                    .plus( APPOINTMENT_DURATION );
                            final LocalTime appointmentStart = apptTime;
                            final LocalTime appointmentEnd = apptTime.plus( APPOINTMENT_DURATION );
                            
//                            if ( appointmentEnd.isBefore( apptTime ) )
                            
                        }
                        return true;
                    } ).collect( Collectors.toList() );


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
        final List<String> appointmentIds = this
                .<Appointment> getData( APPOINTMENT_TABLE, NO_FILTER ).stream()
                .map( app -> app.getId() ).collect( Collectors.toList() );

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
                this.<Appointment> getItemByKey( APPOINTMENT_TABLE, "id",
                    Integer.toHexString( appointmentId ) );
            } catch ( ItemNotFoundException e )
            {
                complete = true;
            }

        } while ( !complete );

        return Integer.toHexString( appointmentId );
    }

    public static void main( String [] args ) throws ItemNotFoundException
    {
        LaboratoryAppointmentManager lam = new LaboratoryAppointmentManager( new DB() );

        // Start it up!
        lam.dataLayer.initialLoad( "LAMS" );

        printLine();

        // Get all appointments
        System.out.println( lam.<Appointment> getData( APPOINTMENT_TABLE, NO_FILTER ).stream()
                .map( app -> app.getId() ).collect( Collectors.toList() ) );

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

        } catch ( MaximumAppointmentCapacityReachedException | AppointmentNotValidException e )
        {
            lam.LOG.error( e.getMessage() );
        }

        printLine();

        // Get all appointments
        System.out.println( lam.<Appointment> getData( APPOINTMENT_TABLE, NO_FILTER ).stream()
                .map( app -> app.getId() ).collect( Collectors.toList() ) );

        printLine();

        try
        {
            // Get appointment 710
            System.out.println( lam.<Appointment> getItemByKey( APPOINTMENT_TABLE, "id", "710" ) );
            printLine();
            // Get appointment 702 ( And fail on purpose )
            System.out.println( lam.<Appointment> getItemByKey( APPOINTMENT_TABLE, "id", "702" ) );

        } catch ( ItemNotFoundException e )
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
