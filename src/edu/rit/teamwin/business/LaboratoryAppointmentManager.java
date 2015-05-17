package edu.rit.teamwin.business;

import static java.lang.String.format;
import static java.lang.System.getProperty;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_TIME;

import java.sql.Time;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import components.data.Appointment;
import components.data.AppointmentLabTest;
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
import edu.rit.teamwin.exceptions.PhlebotomistNotAvailableException;
import edu.rit.teamwin.exceptions.PhlebotomistTravelAvailabilityException;
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

    public static final String APPOINTMENT_TABLE            = "Appointment";

    public static final String APPOINTMENT_LAB_TEST_TABLE   = "AppointmentLabTest";

    public static final String DIAGNOSIS_TABLE              = "Diagnosis";

    public static final String LABTEST_TABLE                = "LabTest";

    public static final String PATIENT_SERVICE_CENTER_TABLE = "PSC";

    public static final String PATIENT_TABLE                = "Patient";

    public static final String PHLEBOTOMIST_TABLE           = "Phlebotomist";

    public static final String PHYSICIAN_TABLE              = "Physician";

    public static final String NO_FILTER                    = "";

    public static final int    MAX_APPOINTMENT_ID           = 0xFFFFF;

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
        /* Start it up! */
        LOG.info( "Business Layer Created" );
    }

    public boolean initializeDatabase()
    {
        return dataLayer.initialLoad( "LAMS" );
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
        final List<T> items = this.<T> getData( table, format( "%s='%s'", keyName, key ) );

        if ( items.size() <= 0 )
        {
            throw new ItemNotFoundException( table, format( "%s='%s'", keyName, key ) );
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

    public Appointment setupAppointment( final Appointment appointment )
            throws MaximumAppointmentCapacityReachedException, AppointmentNotValidException,
            ItemNotFoundException
    {

        validateAppointment( appointment );
        dataLayer.addData( appointment );

        return appointment;
    }

    public Appointment createAppointment(
            final LocalDateTime date,
            final Patient patient,
            final PSC psc,
            final Phlebotomist phlebotomist,
            final AppointmentLabTest... labTests )
            throws MaximumAppointmentCapacityReachedException, AppointmentNotValidException,
            ItemNotFoundException
    {
        return createAppointment( generateID(), date, patient, psc, phlebotomist, labTests );
    }

    public Appointment createAppointment(
            final String appointmentId,
            final LocalDateTime date,
            final Patient patient,
            final PSC psc,
            final Phlebotomist phlebotomist,
            final AppointmentLabTest... labTests )
            throws MaximumAppointmentCapacityReachedException, AppointmentNotValidException,
            ItemNotFoundException
    {
        // Create the appointment object
        final Appointment appointment = new Appointment( appointmentId, java.sql.Date.valueOf( date
                .toLocalDate() ), Time.valueOf( date.toLocalTime() ) );
        appointment.setPatientid( getItemByKey( PATIENT_TABLE, "id", patient.getId() ) );
        appointment.setPhlebid( getItemByKey( PHLEBOTOMIST_TABLE, "id", phlebotomist.getId() ) );
        appointment.setPscid( getItemByKey( PATIENT_SERVICE_CENTER_TABLE, "id", psc.getId() ) );

        final List<AppointmentLabTest> labTestsList = new ArrayList<AppointmentLabTest>(
                labTests.length );

        for ( final AppointmentLabTest test : labTests )
        {
            // See if lab test exists
            getItemByKey( LABTEST_TABLE, "id", test.getLabTest().getId() );
            // See if diagnosis code exists
            getItemByKey( DIAGNOSIS_TABLE, "code", test.getDiagnosis().getCode() );
            labTestsList.add( new AppointmentLabTest( appointment.getId(), test.getLabTest()
                    .getId(), test.getDiagnosis().getCode() ) );
        }
        appointment.setAppointmentLabTestCollection( labTestsList );

        return appointment;
    }

    public void updateAppointment(
            final Appointment oldAppointment,
            final Appointment updatedAppointment ) throws AppointmentNotValidException,
            ItemNotFoundException
    {
        assert ( oldAppointment.getId().equals( updatedAppointment.getId() ) );
        assert ( oldAppointment.getPatientid().getId().equals( updatedAppointment.getPatientid()
                .getId() ) );

        // see if the updatedAppointment will schedule
        validateAppointment( updatedAppointment );
        dataLayer.updateData( updatedAppointment );
    }

    public void validateAppointment( final Appointment appointment )
            throws AppointmentNotValidException, ItemNotFoundException
    {
        // APPOINTMENT VALIDITY CHECKS!

        /* Make sure appointment time is between time of open and time of close */
        final LocalTime apptTime = appointment.getAppttime().toLocalTime();

        if ( apptTime.isBefore( OPEN_TIME ) || apptTime.isAfter( CLOSE_TIME ) )
        {
            throw new AppointmentNotDuringBusinessHoursException( appointment );
        }

        /* Check if requested phlebotomist is not in another appointment */
        final Phlebotomist requestedPhlebotomist = getItemByKey( PHLEBOTOMIST_TABLE, "id",
            appointment.getPhlebid().getKey() );

        final List<Appointment> phlebsAppointments = getData( APPOINTMENT_TABLE,
            format( "phlebid='%s'", requestedPhlebotomist.getId() ) );

        final Map<Appointment, AppointmentNotValidException> conflictingAppointments = phlebsAppointments
                .stream()
                .map( app -> new SimpleEntry<Appointment, AppointmentNotValidException>( app, null ) )
                .filter(
                    app -> {
                        /*
                         * Check if the appointment Ids being compared are the
                         * same. If so, that appointment is being updated!
                         */
                        if ( appointment.getId().equals( app.getKey().getId() ) )
                            return false;

                        if ( appointment.getApptdate().toLocalDate()
                                .equals( app.getKey().getApptdate().toLocalDate() ) )
                        {
                            /*
                             * Check if requested phlebotomist is not within 30
                             * minutes of requested PSC (for travel time)
                             */
                            final boolean differentPSC = !app.getKey().getPscid()
                                    .equals( appointment.getPscid() );

                            final LocalTime appStart = app.getKey().getAppttime().toLocalTime();
                            final LocalTime appEnd = app.getKey().getAppttime().toLocalTime()
                                    .plus( APPOINTMENT_DURATION );
                            final LocalTime appointmentStart = apptTime;
                            final LocalTime appointmentEnd = apptTime.plus( APPOINTMENT_DURATION );

                            if ( appointmentEnd.isBefore( appStart.plusSeconds( 1 ) ) )
                            {
                                if ( differentPSC &&
                                        Duration.between( appointmentEnd, appStart ).compareTo(
                                            PHLEBOTOMIST_TRAVEL_DURATION ) < 0 )
                                {
                                    app.setValue( new PhlebotomistTravelAvailabilityException(
                                            appointment, app.getKey() ) );
                                    return true;
                                }
                            } else if ( appointmentStart.isAfter( appEnd.minusSeconds( 1 ) ) )
                            {
                                if ( differentPSC &&
                                        Duration.between( appEnd, appointmentStart ).compareTo(
                                            PHLEBOTOMIST_TRAVEL_DURATION ) < 0 )
                                {
                                    app.setValue( new PhlebotomistTravelAvailabilityException(
                                            appointment, app.getKey() ) );
                                    return true;
                                }
                            } else
                            {
                                app.setValue( new PhlebotomistNotAvailableException( appointment,
                                        app.getKey() ) );
                                return true;
                            }
                        }
                        return false;
                    } )
                .collect( Collectors.toMap( pair -> pair.getKey(), pair -> pair.getValue() ) );

        if ( conflictingAppointments.size() > 0 )
        {
            final List<Appointment> apps = conflictingAppointments.keySet().stream()
                    .collect( Collectors.toList() );
            throw conflictingAppointments.get( apps.get( 0 ) );
        }
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

        /* Get all appointments */
        System.out.println( "  Appointments" );
        System.out.println( lam.<Appointment> getData( APPOINTMENT_TABLE, NO_FILTER ).stream()
                .map( app -> app.getId() ).collect( Collectors.toList() ) );

        printLine();

        /* Get all PSCs */
        System.out.println( "  PSCs" );
        System.out.println( lam.<PSC> getData( PATIENT_SERVICE_CENTER_TABLE, NO_FILTER ).stream()
                .map( psc -> psc.getId() ).collect( Collectors.toList() ) );

        printLine();

        /* Get all Phlebotomists */
        System.out.println( "  Phlebotomists" );
        System.out.println( lam.<Phlebotomist> getData( PHLEBOTOMIST_TABLE, NO_FILTER ).stream()
                .map( phleb -> phleb.getId() ).collect( Collectors.toList() ) );

        printLine();

        /* Try to setup a new appointment before open */
        try
        {
            final Appointment appointment = lam.createAppointment(
                LocalDateTime.parse( "2004-02-01T07:30:00" ),
                lam.getItemByKey( PATIENT_TABLE, "id", "230" ),
                lam.getItemByKey( PATIENT_SERVICE_CENTER_TABLE, "id", "510" ),
                lam.getItemByKey( PHLEBOTOMIST_TABLE, "id", "110" ) );

            lam.setupAppointment( appointment );
            System.out.println( appointment );

        } catch ( MaximumAppointmentCapacityReachedException | AppointmentNotValidException e )
        {
            lam.LOG.error( e.getMessage() );
        }

        printLine();

        /* Try to setup a new appointment after close */
        try
        {
            final Appointment appointment = lam.createAppointment(
                LocalDateTime.parse( "2004-02-01T17:30:00" ),
                lam.getItemByKey( PATIENT_TABLE, "id", "230" ),
                lam.getItemByKey( PATIENT_SERVICE_CENTER_TABLE, "id", "510" ),
                lam.getItemByKey( PHLEBOTOMIST_TABLE, "id", "110" ) );

            lam.setupAppointment( appointment );
            System.out.println( appointment );

        } catch ( MaximumAppointmentCapacityReachedException | AppointmentNotValidException e )
        {
            lam.LOG.error( e.getMessage() );
        }

        printLine();

        /*
         * Try to setup a new appointment with a phlebotomist who is busy
         * already
         */
        try
        {
            final Appointment appointment = lam.createAppointment(
                LocalDateTime.parse( "2004-02-01T13:10:00" ),
                lam.getItemByKey( PATIENT_TABLE, "id", "230" ),
                lam.getItemByKey( PATIENT_SERVICE_CENTER_TABLE, "id", "510" ),
                lam.getItemByKey( PHLEBOTOMIST_TABLE, "id", "110" ) );

            lam.setupAppointment( appointment );
            System.out.println( appointment );

        } catch ( MaximumAppointmentCapacityReachedException | AppointmentNotValidException e )
        {
            lam.LOG.error( e.getMessage() );
        }

        printLine();

        /*
         * Try to setup a new appointment with a phlebotomist who will not make
         * it in time to the requested PSC
         */
        try
        {
            final Appointment appointment = lam.createAppointment(
                LocalDateTime.parse( "2004-02-01T13:30:00" ),
                lam.getItemByKey( PATIENT_TABLE, "id", "230" ),
                lam.getItemByKey( PATIENT_SERVICE_CENTER_TABLE, "id", "520" ),
                lam.getItemByKey( PHLEBOTOMIST_TABLE, "id", "110" ) );

            lam.setupAppointment( appointment );
            System.out.println( appointment );

        } catch ( MaximumAppointmentCapacityReachedException | AppointmentNotValidException e )
        {
            lam.LOG.error( e.getMessage() );
        }

        printLine();

        /*
         * Try to setup a new appointment with a phlebotomist who will make it
         * in time to the requested PSC because he/she is already at it
         */
        try
        {
            final Appointment appointment = lam.createAppointment(
                LocalDateTime.parse( "2004-02-01T13:15:00" ),
                lam.getItemByKey( PATIENT_TABLE, "id", "230" ),
                lam.getItemByKey( PATIENT_SERVICE_CENTER_TABLE, "id", "510" ),
                lam.getItemByKey( PHLEBOTOMIST_TABLE, "id", "110" ) );

            lam.setupAppointment( appointment );
            System.out.println( appointment );

        } catch ( MaximumAppointmentCapacityReachedException | AppointmentNotValidException e )
        {
            lam.LOG.error( e.getMessage() );
        }

        printLine();

        /*
         * Try to setup a new appointment with a phlebotomist who will make it
         * in time to the requested PSC because he/she will have enough travel
         * time
         */
        try
        {
            final Appointment appointment = lam.createAppointment(
                LocalDateTime.parse( "2004-02-01T14:00:00" ),
                lam.getItemByKey( PATIENT_TABLE, "id", "230" ),
                lam.getItemByKey( PATIENT_SERVICE_CENTER_TABLE, "id", "520" ),
                lam.getItemByKey( PHLEBOTOMIST_TABLE, "id", "110" ) );

            lam.setupAppointment( appointment );
            System.out.println( appointment );

        } catch ( MaximumAppointmentCapacityReachedException | AppointmentNotValidException e )
        {
            lam.LOG.error( e.getMessage() );
        }

        printLine();

        /* Get all appointments */
        System.out.println( "  Appointments" );
        System.out.println( lam.<Appointment> getData( APPOINTMENT_TABLE, NO_FILTER ).stream()
                .map( app -> app.getId() ).collect( Collectors.toList() ) );

        printLine();

        try
        {
            // Get appointment 710
            System.out.println( lam.<Appointment> getItemByKey( APPOINTMENT_TABLE, "id", "710" ) );
            printLine();
            // Get appointment 703 ( And fail on purpose )
            System.out.println( lam.<Appointment> getItemByKey( APPOINTMENT_TABLE, "id", "703" ) );

        } catch ( ItemNotFoundException e )
        {
            lam.LOG.error( e.getMessage() );
        }

        printLine();

        // Try to update appointment 710 with a new phlebotomist
        final Appointment app = lam.getItemByKey( APPOINTMENT_TABLE, "id", "710" );
        final Appointment updatedApp = (Appointment) app.clone();
        final Phlebotomist newPhleb = lam.getItemByKey( PHLEBOTOMIST_TABLE, "id", "120" );

        updatedApp.setPhlebid( newPhleb );
        updatedApp.setAppttime( Time.valueOf( "13:45:00" ) );
        try
        {
            lam.updateAppointment( app, updatedApp );
        } catch ( AppointmentNotValidException e )
        {
            lam.LOG.error( e.getMessage() );
            System.out.println();
        }

        System.out.println( lam.<Appointment> getItemByKey( APPOINTMENT_TABLE, "id", "710" ) );

        printLine();

    }

    private final static void printLine()
    {
        for ( int i = 0; i <= 70; i++ )
            System.out.print( i >= 70 ? "\n" : "-" );
    }
}
