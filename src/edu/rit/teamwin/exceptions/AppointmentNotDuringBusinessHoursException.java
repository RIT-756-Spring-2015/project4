package edu.rit.teamwin.exceptions;

import static java.lang.String.format;

import components.data.Appointment;

import edu.rit.teamwin.business.LaboratoryAppointmentManager;

/**
 * <p>
 * This exception is thrown in the by
 * {@link LaboratoryAppointmentManager#validateAppointment(components.data.Appointment)
 * LaboratoryAppointmentManager.validateAppointment} when a given appointment
 * trying to be scheduled does not occur during business hours.
 * </p>
 * 
 * @author Alex Aiezza
 * @author Sagar Barbhaya
 * @author Salil Rajadhyaksha
 *
 */
@SuppressWarnings ( "serial" )
public class AppointmentNotDuringBusinessHoursException extends AppointmentNotValidException
{
    public AppointmentNotDuringBusinessHoursException( final Appointment appointment )
    {
        super( appointment, format(
            "Appointment time '%s' is not between the hours of OPEN and CLOSE",
            appointment.getAppttime() ) );
    }
}
