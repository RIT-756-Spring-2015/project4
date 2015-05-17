package edu.rit.teamwin;

import static edu.rit.teamwin.business.LaboratoryAppointmentManager.APPOINTMENT_TABLE;
import static edu.rit.teamwin.business.LaboratoryAppointmentManager.NO_FILTER;
import static java.lang.String.format;
import static java.lang.System.getProperty;

import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import components.data.Appointment;
import components.data.DB;

import edu.rit.teamwin.business.LaboratoryAppointmentManager;
import edu.rit.teamwin.exceptions.AppointmentNotValidException;
import edu.rit.teamwin.exceptions.ItemNotFoundException;
import edu.rit.teamwin.exceptions.MaximumAppointmentCapacityReachedException;
import edu.rit.teamwin.utils.PropertiesSetter;

/**
 * <p>
 * This class is the <strong>SERVICE LAYER</strong>. An instance of this class
 * will run and serve up RESTful endpoints.
 * </p>
 * 
 * @author Alex Aiezza
 * @author Sagar Barbhaya
 * @author Salil Rajadhyaksha
 *
 */
@Path ( "LAMSAppointment" )
@Produces ( MediaType.APPLICATION_XML )
public class LaboratoryAppointmentService
{
    private final Log                          LOG = LogFactory.getLog( getClass() );

    @Context
    private UriInfo                            context;

    private final LaboratoryAppointmentManager LAM;

    {
        try
        {
            PropertiesSetter.class.newInstance();
        } catch ( final Exception e )
        {
            LOG.error( e.getMessage() );
        }
    }

    public LaboratoryAppointmentService()
    {
        LAM = new LaboratoryAppointmentManager( new DB() );
        LOG.info( "Service Layer Created" );
    }

    @Path ( "Services" )
    @GET
    public String getServices()
    {
        LOG.debug( "Services called" );
        return format( getProperty( "default.xml" ), getProperty( "services.get.xml" ) );
    }

    @Path ( "Appointments" )
    @GET
    public List<Appointment> getAppointments()
    {
        LOG.info( "GET Appointments called" );

        return LAM.<Appointment> getData( APPOINTMENT_TABLE, NO_FILTER );
    }

    @Path ( "Appointments/{appointment}" )
    @GET
    public Appointment getAppointment( @PathParam ( "appointment" ) final String appointmentId )
            throws ItemNotFoundException
    {
        LOG.info( format( "GET Appointment %s called", appointmentId ) );

        return LAM.<Appointment> getItemByKey( APPOINTMENT_TABLE, "id", appointmentId );
    }

    @Path ( "Appointments" )
    @POST
    @Consumes ( MediaType.APPLICATION_XML )
    public String createAppointment( final Appointment appointment )
            throws MaximumAppointmentCapacityReachedException, AppointmentNotValidException,
            ItemNotFoundException
    {
        LOG.info( "POST Appointments called" );

        final Appointment app = LAM.setupAppointment( appointment.getApptdate(),
            appointment.getPatientid(), appointment.getPscid(), appointment.getPhlebid() );

        return format( "Link to newly created appointment: %s", app.getId() );
    }

    @Path ( "Appointments/{appointment}" )
    @PUT
    @Consumes ( MediaType.APPLICATION_XML )
    public String updateAppointment(
            @PathParam ( "appointment" ) final String appointmentId,
            final Appointment appointment ) throws AppointmentNotValidException,
            ItemNotFoundException
    {
        LOG.info( format( "PUT Appointments %s called", appointmentId ) );

        final Appointment oldAppointment = LAM
                .getItemByKey( APPOINTMENT_TABLE, "id", appointmentId );

        LAM.updateAppointment( oldAppointment, appointment );
        return format( "Link to newly created appointment: %s", appointment.getId() );
    }
}
