package edu.rit.teamwin;

import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import components.data.DB;

import edu.rit.teamwin.business.LaboratoryAppointmentManager;

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
public class LaboratoryAppointmentService
{
    private final Log                          LOG = LogFactory.getLog( getClass() );

    @Context
    private UriInfo                            context;

    private final LaboratoryAppointmentManager LAM;

    public LaboratoryAppointmentService()
    {
        LAM = new LaboratoryAppointmentManager( new DB() );

        LOG.info( "Service Created" );
    }


}
