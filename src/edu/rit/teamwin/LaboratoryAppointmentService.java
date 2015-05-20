package edu.rit.teamwin;

import static edu.rit.teamwin.business.LaboratoryAppointmentManager.APPOINTMENT_LAB_TEST_TABLE;
import static edu.rit.teamwin.business.LaboratoryAppointmentManager.APPOINTMENT_TABLE;
import static edu.rit.teamwin.business.LaboratoryAppointmentManager.DIAGNOSIS_TABLE;
import static edu.rit.teamwin.business.LaboratoryAppointmentManager.LABTEST_TABLE;
import static edu.rit.teamwin.business.LaboratoryAppointmentManager.NO_FILTER;
import static edu.rit.teamwin.business.LaboratoryAppointmentManager.PATIENT_SERVICE_CENTER_TABLE;
import static edu.rit.teamwin.business.LaboratoryAppointmentManager.PATIENT_TABLE;
import static edu.rit.teamwin.business.LaboratoryAppointmentManager.PHLEBOTOMIST_TABLE;
import static edu.rit.teamwin.business.LaboratoryAppointmentManager.PHYSICIAN_TABLE;
import static java.lang.String.format;
import static java.lang.System.getProperty;

import java.time.LocalDateTime;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import components.data.Appointment;
import components.data.AppointmentLabTest;
import components.data.Diagnosis;
import components.data.LabTest;
import components.data.PSC;
import components.data.Patient;
import components.data.Phlebotomist;
import components.data.Physician;

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

    public LaboratoryAppointmentService( final LaboratoryAppointmentManager lam )
    {
        LAM = lam;
        LOG.info( "Service Layer Created" );
    }

    private Response buildResponse( final Object entity )
    {
        final ResponseBuilder response = Response.ok( entity );
        response.header( "Access-Control-Allow-Origin", "*" );
        response.header( "Access-Control-Allow-Methods", "GET, POST, PUT, OPTIONS" );
        response.header( "Access-Control-Allow-Headers", "accept, content-type" );
        final Response resp = response.build();

        return resp;
    }

    @Path ( "init" )
    @GET
    public String init()
    {
        LOG.debug( "Initializing Database" );
        return "<response>" + LAM.initializeDatabase() + "</response>";
    }

    @Path ( "refresh" )
    @GET
    public String refresh()
    {
        LOG.debug( "Refreshing Database" );
        return "<response>" + LAM.refreshDatabase() + "</response>";
    }

    @Path ( "Services" )
    @GET
    public Response getServices()
    {
        LOG.debug( "Services called" );
        System.setProperty( "baseUri", context.getBaseUri().toString() );
        return buildResponse( format( getProperty( "default.xml" ),
            getProperty( "services.get.xml" ) ) );
    }

    @Path ( "Appointments" )
    @GET
    public Response getAppointments()
    {
        LOG.info( "GET Appointments called" );

        return buildResponse( LAM.<Appointment> getData( APPOINTMENT_TABLE, NO_FILTER ) );
    }

    @Path ( "Appointments" )
    @OPTIONS
    public Response optionsAppointments()
    {
        LOG.info( "OPTIONS Appointments called" );

        return buildResponse( "" );
    }

    @Path ( "Appointments/{appointment}" )
    @GET
    public Response getAppointment( @PathParam ( "appointment" ) final String appointmentId )
            throws ItemNotFoundException
    {
        LOG.info( format( "GET Appointment %s called", appointmentId ) );

        return buildResponse( LAM.<Appointment> getItemByKey( APPOINTMENT_TABLE, "id",
            appointmentId ) );
    }

    @Path ( "Appointments" )
    @POST
    @Consumes ( MediaType.APPLICATION_XML )
    public Response createAppointment( final Appointment appointment )
            throws MaximumAppointmentCapacityReachedException, AppointmentNotValidException,
            ItemNotFoundException
    {
        LOG.info( "POST Appointments called" );

        final AppointmentLabTest [] labTests = new AppointmentLabTest [appointment
                .getAppointmentLabTestCollection().size()];

        final Appointment app = LAM.createAppointment( LocalDateTime.of( appointment.getApptdate()
                .toLocalDate(), appointment.getAppttime().toLocalTime() ), appointment
                .getPatientid(), appointment.getPscid(), appointment.getPhlebid(), appointment
                .getAppointmentLabTestCollection().toArray( labTests ) );

        LAM.setupAppointment( app );

        return buildResponse( format( getProperty( "default.xml" ), "<uri>" + context.getBaseUri() +
                "LAMSAppointment/Appointments/" + app.getId() + "</uri>" ) );
    }

    @Path ( "Appointments/{appointment}" )
    @PUT
    @Consumes ( MediaType.APPLICATION_XML )
    public Response updateAppointment(
            @PathParam ( "appointment" ) final String appointmentId,
            final Appointment appointment ) throws AppointmentNotValidException,
            ItemNotFoundException, MaximumAppointmentCapacityReachedException
    {
        LOG.info( format( "PUT Appointments %s called", appointmentId ) );

        final AppointmentLabTest [] labTests = new AppointmentLabTest [appointment
                .getAppointmentLabTestCollection().size()];

        final Appointment app = LAM.createAppointment( appointmentId, LocalDateTime.of( appointment
                .getApptdate().toLocalDate(), appointment.getAppttime().toLocalTime() ),
            appointment.getPatientid(), appointment.getPscid(), appointment.getPhlebid(),
            appointment.getAppointmentLabTestCollection().toArray( labTests ) );

        final Appointment oldAppointment = LAM
                .getItemByKey( APPOINTMENT_TABLE, "id", appointmentId );

        LAM.updateAppointment( oldAppointment, app );

        return buildResponse( format( getProperty( "default.xml" ), "<uri>" + context.getBaseUri() +
                "LAMSAppointment/Appointments/" + app.getId() + "</uri>" ) );
    }
    
    @Path ( "Appointments/{a}" )
    @OPTIONS
    public Response optionsAppointment()
    {
        LOG.info( "OPTIONS Appointment called" );

        return buildResponse( "<response/>" );
    }

    @Path ( "Patients" )
    @GET
    public Response getPatients()
    {
        LOG.info( "GET Patients called" );

        return buildResponse( LAM.<Patient> getData( PATIENT_TABLE, NO_FILTER ) );
    }

    @Path ( "Patients/{patient}" )
    @GET
    public Response getPatient( @PathParam ( "patient" ) final String patientId )
            throws ItemNotFoundException
    {
        LOG.info( format( "GET Patient %s called", patientId ) );

        return buildResponse( LAM.<Patient> getData( PATIENT_TABLE, format( "id='%s'", patientId ) ) );
    }

    @Path ( "Physicians" )
    @GET
    public Response getPhysicians()
    {
        LOG.info( "GET Physicians called" );

        return buildResponse( LAM.<Physician> getData( PHYSICIAN_TABLE, NO_FILTER ) );
    }

    @Path ( "Physicians/{physician}" )
    @GET
    public Response getPhysician( @PathParam ( "physician" ) final String physicianId )
            throws ItemNotFoundException
    {
        LOG.info( format( "GET Physician %s called", physicianId ) );

        return buildResponse( LAM.<Physician> getData( PHYSICIAN_TABLE,
            format( "id='%s'", physicianId ) ) );
    }

    @Path ( "Phlebotomists" )
    @GET
    public Response getPhlebotomists()
    {
        LOG.info( "GET Phlebotomists called" );

        return buildResponse( LAM.<Phlebotomist> getData( PHLEBOTOMIST_TABLE, NO_FILTER ) );
    }

    @Path ( "Phlebotomists/{phlebotomist}" )
    @GET
    public Response getPhlebotomist( @PathParam ( "phlebotomist" ) final String phlebotomistId )
            throws ItemNotFoundException
    {
        LOG.info( format( "GET Phlebotomist %s called", phlebotomistId ) );

        return buildResponse( LAM.<Phlebotomist> getData( PHLEBOTOMIST_TABLE,
            format( "id='%s'", phlebotomistId ) ) );
    }

    @Path ( "PSCs" )
    @GET
    public Response getPSCs()
    {
        LOG.info( "GET PSCs called" );

        return buildResponse( LAM.<PSC> getData( PATIENT_SERVICE_CENTER_TABLE, NO_FILTER ) );
    }

    @Path ( "PSCs/{psc}" )
    @GET
    public Response getPSC( @PathParam ( "psc" ) final String pscId ) throws ItemNotFoundException
    {
        LOG.info( format( "GET PSC %s called", pscId ) );


        return buildResponse( LAM.<PSC> getData( PATIENT_SERVICE_CENTER_TABLE,
            format( "id='%s'", pscId ) ) );
    }

    @Path ( "AppointmentLabTestPKs" )
    @GET
    public Response getAppointmentLabTestPKs()
    {
        LOG.info( "GET AppointmentLabTestPKs called" );

        return buildResponse( LAM.<AppointmentLabTest> getData( APPOINTMENT_LAB_TEST_TABLE,
            NO_FILTER ) );
    }

    @Path ( "AppointmentLabTestPKs/{appointmentId}" )
    @GET
    public Response getAppointmentLabTestPK(
            @PathParam ( "appointmentId" ) final String appointmentId )
            throws ItemNotFoundException
    {
        LOG.info( format( "GET AppointmentLabTestPK %s called", appointmentId ) );

        return buildResponse( LAM.<AppointmentLabTest> getData( APPOINTMENT_LAB_TEST_TABLE,
            format( "apptid='%s'", appointmentId ) ) );
    }

    @Path ( "LabTests" )
    @GET
    public Response getLabTests()
    {
        LOG.info( "GET LabTests called" );

        return buildResponse( LAM.<LabTest> getData( LABTEST_TABLE, NO_FILTER ) );
    }

    @Path ( "LabTests/{labTest}" )
    @GET
    public Response getLabTest( @PathParam ( "labTest" ) final String labTestId )
            throws ItemNotFoundException
    {
        LOG.info( format( "GET LabTest %s called", labTestId ) );

        return buildResponse( LAM.<LabTest> getData( LABTEST_TABLE, format( "id='%s'", labTestId ) ) );
    }

    @Path ( "Diagnoses" )
    @GET
    public Response getDiagnoses()
    {
        LOG.info( "GET Diagnoses called" );

        return buildResponse( LAM.<Diagnosis> getData( DIAGNOSIS_TABLE, NO_FILTER ) );
    }

    @Path ( "Diagnoses/{diagnosis}" )
    @GET
    public Response getDiagnosis( @PathParam ( "diagnosis" ) final String diagnosisCode )
            throws ItemNotFoundException
    {
        LOG.info( format( "GET Diagnosis %s called", diagnosisCode ) );

        return buildResponse( LAM.<Diagnosis> getData( DIAGNOSIS_TABLE,
            format( "code='%s'", diagnosisCode ) ) );
    }
}
