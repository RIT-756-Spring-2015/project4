package edu.rit.teamwin.business;

import static java.lang.String.format;

import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

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
        final List<Object> results = dataLayer.getData( APPOINTMENT_TABLE, "" );

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

    public boolean setupAppointment(
            final Date date,
            final Time time,
            final Patient patient,
            final PSC psc,
            final Phlebotomist phlebotomist )
    {
        // Create the appointment object
        final Appointment appointment = new Appointment( generateID(), date, time );
        appointment.setPatientid( patient );
        appointment.setPhlebid( phlebotomist );
        appointment.setPscid( psc );

        return validateAppointment( appointment );

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
    {
        // APPOINTMENT VALIDITY CHECKS!

        /* Check if requested phlebotomist is not in another appointment */

        /*
         * Check if requested phlebotomist is not within 30 minutes of requested
         * PSC (for travel time) (And 15 minutes for the appointment)
         */

        return false;
    }

    public boolean deleteAppointment( final Appointment appointment )
    {
        // Delete the appointment
        return false;
    }

    /**
     * @return a generated string with a maximum size of 5
     */
    private String generateID()
    {
        // Check if id already exists
        final StringBuilder out = new StringBuilder();
        // yada yada

        return out.toString();
    }

    public static void main( String [] args )
    {
        LaboratoryAppointmentManager lam = new LaboratoryAppointmentManager( new DB() );

        lam.dataLayer.initialLoad( "LAMS" );

        // Get all appointments
        lam.getAppointments().forEach( app -> System.out.println( app.getId() ) );

        for ( int i = 0; i <= 50; i++ )
            System.out.print( i >= 50 ? "\n" : "-" );

        try
        {
            // Get appointment 710
            System.out.println( lam.getAppointment( "710" ) );

            for ( int i = 0; i <= 50; i++ )
                System.out.print( i >= 50 ? "\n" : "-" );

            // Get appointment 710
            System.out.println( lam.getAppointment( "701" ) );
        } catch ( AppointmentNotFoundException e )
        {
            lam.LOG.error( e.getMessage() );
        }

        for ( int i = 0; i <= 50; i++ )
            System.out.print( i >= 50 ? "\n" : "-" );

    }
}
