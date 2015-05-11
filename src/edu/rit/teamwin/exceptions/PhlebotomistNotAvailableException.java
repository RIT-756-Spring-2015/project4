package edu.rit.teamwin.exceptions;

import static java.lang.String.format;

import components.data.Appointment;

import edu.rit.teamwin.business.LaboratoryAppointmentManager;

/**
 * <p>
 * This exception is thrown in the by
 * {@link LaboratoryAppointmentManager#validateAppointment(components.data.Appointment)
 * LaboratoryAppointmentManager.validateAppointment} when a given appointment
 * trying to be scheduled requests a phlebotomist that is already in an
 * appointment.
 * </p>
 * 
 * @author Alex Aiezza
 * @author Sagar Barbhaya
 * @author Salil Rajadhyaksha
 *
 */
@SuppressWarnings ( "serial" )
public class PhlebotomistNotAvailableException extends AppointmentNotValidException
{
    private final Appointment conflictingAppointment;

    public PhlebotomistNotAvailableException(
        final Appointment appointment,
        final Appointment conflictingAppointment )
    {
        super(
                appointment,
                format(
                    "Requested Phlebotomist (%s) is not available at for the requested appointment due to a conflicting appointment",
                    appointment.getPhlebid().getId(), appointment.getPscid().getId() ) );
        this.conflictingAppointment = conflictingAppointment;
    }

    /**
     * @return the conflictingAppointment
     */
    public Appointment getConflictingAppointment()
    {
        return conflictingAppointment;
    }

}
