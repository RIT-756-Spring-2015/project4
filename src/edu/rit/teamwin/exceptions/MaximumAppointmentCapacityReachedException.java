package edu.rit.teamwin.exceptions;

import static java.lang.String.format;
import edu.rit.teamwin.business.LaboratoryAppointmentManager;

/**
 * <p>
 * This exception is thrown in the by
 * {@link LaboratoryAppointmentManager#generateId()
 * LaboratoryAppointmentManager.generateId} when the database cannot take any
 * more new appointments.
 * </p>
 * 
 * @author Alex Aiezza
 * @author Sagar Barbhaya
 * @author Salil Rajadhyaksha
 *
 */
@SuppressWarnings ( "serial" )
public class MaximumAppointmentCapacityReachedException extends Exception
{
    public MaximumAppointmentCapacityReachedException()
    {
        super( format( "The maximum capacity for appointments (%d) has been reached.",
            LaboratoryAppointmentManager.MAX_APPOINTMENT_ID ) );
    }
}
