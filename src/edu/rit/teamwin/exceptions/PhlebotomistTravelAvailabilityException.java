package edu.rit.teamwin.exceptions;

import static java.lang.String.format;

import components.data.Appointment;

import edu.rit.teamwin.business.LaboratoryAppointmentManager;

/**
 * <p>
 * This exception is thrown in the by
 * {@link LaboratoryAppointmentManager#validateAppointment(components.data.Appointment)
 * LaboratoryAppointmentManager.validateAppointment} when a given appointment
 * trying to be scheduled requests a phlebotomist that will not make it from
 * his/her current PSC to the requested PSC.
 * </p>
 * 
 * @author Alex Aiezza
 * @author Sagar Barbhaya
 * @author Salil Rajadhyaksha
 *
 */
@SuppressWarnings ( "serial" )
public class PhlebotomistTravelAvailabilityException extends AppointmentNotValidException
{
    private final Appointment conflictingAppointment;

    public PhlebotomistTravelAvailabilityException(
        final Appointment appointment,
        final Appointment conflictingAppointment )
    {
        super(
                appointment,
                format(
                    "Requested Phlebotomist (%s) is not available at PSC (%s) for the requested appointment because he/she will be coming from PSC (%s) and won't have enough time to travel",
                    appointment.getPhlebid().getId(), appointment.getPscid().getId(),
                    conflictingAppointment.getPscid().getId() ) );

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
