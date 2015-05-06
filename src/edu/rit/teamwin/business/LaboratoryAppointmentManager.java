/**
 * 
 */
package edu.rit.teamwin.business;

import java.sql.Date;
import java.sql.Time;
import java.util.List;

import components.data.Appointment;
import components.data.DB;
import components.data.IComponentsData;
import components.data.PSC;
import components.data.Patient;
import components.data.Phlebotomist;

import edu.rit.teamwin.LaboratoryAppointmentService;

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
        return null;
    }

    public Appointment getAppointment( final String appointmentId )
    {
        return null;
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
        final List<Object> results = lam.dataLayer.getData( "Appointment", "" );

        for ( final Object app : results )
        {
            System.out.println( ( (Appointment) app ).getId() );
        }

    }

}
