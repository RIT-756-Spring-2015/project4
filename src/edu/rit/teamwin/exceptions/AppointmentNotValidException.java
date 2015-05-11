package edu.rit.teamwin.exceptions;

import static java.lang.String.format;
import edu.rit.teamwin.business.LaboratoryAppointmentManager;

/**
 * <p>
 * This exception is thrown in the by
 * {@link LaboratoryAppointmentManager#validateAppointment(components.data.Appointment)
 * LaboratoryAppointmentManager.validateAppoint}(
 * {@link components.data.Appointment appointment} ) when a given appointment Id
 * cannot be found in the database.
 * </p>
 * 
 * @author Alex Aiezza
 * @author Sagar Barbhaya
 * @author Salil Rajadhyaksha
 *
 */
@SuppressWarnings ( "serial" )
public abstract class AppointmentNotValidException extends Exception
{
    public AppointmentNotValidException( final String problemWithAppointment )
    {
        super( format( "Appointment Not Valid: '%s'", problemWithAppointment ) );
    }

}
