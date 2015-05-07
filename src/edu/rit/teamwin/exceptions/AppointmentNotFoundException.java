package edu.rit.teamwin.exceptions;

import static java.lang.String.format;
import edu.rit.teamwin.business.LaboratoryAppointmentManager;

/**
 * <p>
 * This exception is thrown in the by
 * {@link LaboratoryAppointmentManager#getAppointment(String)
 * LaboratoryAppointmentManager.getAppointment( appointmentId )} when a given
 * appointment Id cannot be found in the database.
 * </p>
 * 
 * @author Alex Aiezza
 * @author Sagar Barbhaya
 * @author Salil Rajadhyaksha
 *
 */
@SuppressWarnings ( "serial" )
public class AppointmentNotFoundException extends Exception
{
    public AppointmentNotFoundException( final String appointmentId )
    {
        super( format( "Appointment: '%s' cannot be found.", appointmentId ) );
    }

}
