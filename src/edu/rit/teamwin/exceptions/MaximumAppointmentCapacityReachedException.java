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
 * <p>
 * The MaximumAppointmentCapacityReachedException does not refer to a physical
 * limitation of the data source in use, but rather the limitation we decided to
 * impart on our Appointment ID generation where all IDs are actually
 * hexadecimal values with a max digit length. In this case, the data layer
 * given to us for the assignment has the Appointment Id as a varchar of length
 * 5. Thus, the maximum value would be 5 hexadecimal digits long or put plainly,
 * {@link LaboratoryAppointmentManager#MAX_APPOINTMENT_ID FFFFF}.
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
