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
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
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
import components.data.DB;
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

    public LaboratoryAppointmentService()
    {
        LAM = new LaboratoryAppointmentManager( new DB() );
        LOG.info( "Service Layer Created" );
    }

    @Path ( "init" )
    @GET
    public String init()
    {
        LOG.debug( "Initializing Database" );
        return "" + LAM.initializeDatabase();
    }

    @Path ( "Services" )
    @GET
    public String getServices()
    {
        LOG.debug( "Services called" );
        System.setProperty( "baseUri", context.getBaseUri().toString() );
        return format( getProperty( "default.xml" ), getProperty( "services.get.xml" ) );
    }

    @Path ( "Appointments" )
    @GET
    public Response getAppointments()
    {
        LOG.info( "GET Appointments called" );
        
        final ResponseBuilder response = Response.ok();
        response.header( "Access-Control-Allow-Origin", "*" );
        response.entity( LAM.<Appointment> getData( APPOINTMENT_TABLE, NO_FILTER ) );

        return response.build();
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
            throws MaximumAppointmentCapacityReachedException
    {
        LOG.info( "POST Appointments called" );

        try
        {
            final AppointmentLabTest [] labTests = new AppointmentLabTest [appointment
                    .getAppointmentLabTestCollection().size()];

            final Appointment app = LAM.createAppointment( LocalDateTime.of( appointment
                    .getApptdate().toLocalDate(), appointment.getAppttime().toLocalTime() ),
                appointment.getPatientid(), appointment.getPscid(), appointment.getPhlebid(),
                appointment.getAppointmentLabTestCollection().toArray( labTests ) );

            LAM.setupAppointment( app );

            return format( getProperty( "default.xml" ), "<uri>" + context.getBaseUri() +
                    "LAMSAppointment/Appointments/" + app.getId() + "</uri>" );
        } catch ( AppointmentNotValidException | ItemNotFoundException e )
        {
            return format( getProperty( "default.xml" ), "<error>" + e.getMessage() + "</error>" );
        }
    }

    @Path ( "Appointments/{appointment}" )
    @PUT
    @Consumes ( MediaType.APPLICATION_XML )
    public String updateAppointment(
            @PathParam ( "appointment" ) final String appointmentId,
            final Appointment appointment ) throws AppointmentNotValidException,
            ItemNotFoundException, MaximumAppointmentCapacityReachedException
    {
        LOG.info( format( "PUT Appointments %s called", appointmentId ) );

        try
        {
            final AppointmentLabTest [] labTests = new AppointmentLabTest [appointment
                    .getAppointmentLabTestCollection().size()];

            final Appointment app = LAM.createAppointment( appointmentId, LocalDateTime.of(
                appointment.getApptdate().toLocalDate(), appointment.getAppttime().toLocalTime() ),
                appointment.getPatientid(), appointment.getPscid(), appointment.getPhlebid(),
                appointment.getAppointmentLabTestCollection().toArray( labTests ) );

            final Appointment oldAppointment = LAM.getItemByKey( APPOINTMENT_TABLE, "id",
                appointmentId );

            LAM.updateAppointment( oldAppointment, app );

            return format( getProperty( "default.xml" ), "<uri>" + context.getBaseUri() +
                    "LAMSAppointment/Appointments/" + app.getId() + "</uri>" );
        } catch ( AppointmentNotValidException | ItemNotFoundException e )
        {
            return format( getProperty( "default.xml" ), "<error>" + e.getMessage() + "</error>" );
        }
    }

    @Path ( "Patients" )
    @GET
    public List<Patient> getPatients()
    {
        LOG.info( "GET Patients called" );

        return LAM.<Patient> getData( PATIENT_TABLE, NO_FILTER );
    }

    @Path ( "Patients/{patient}" )
    @GET
    public List<Patient> getPatient( @PathParam ( "patient" ) final String patientId )
            throws ItemNotFoundException
    {
        LOG.info( format( "GET Patient %s called", patientId ) );

        return LAM.<Patient> getData( PATIENT_TABLE, format( "id='%s'", patientId ) );
    }

    @Path ( "Physicians" )
    @GET
    public List<Physician> getPhysicians()
    {
        LOG.info( "GET Physicians called" );

        return LAM.<Physician> getData( PHYSICIAN_TABLE, NO_FILTER );
    }

    @Path ( "Physicians/{physician}" )
    @GET
    public List<Physician> getPhysician( @PathParam ( "physician" ) final String physicianId )
            throws ItemNotFoundException
    {
        LOG.info( format( "GET Physician %s called", physicianId ) );

        return LAM.<Physician> getData( PHYSICIAN_TABLE, format( "id='%s'", physicianId ) );
    }

    @Path ( "Phlebotomists" )
    @GET
    public List<Phlebotomist> getPhlebotomists()
    {
        LOG.info( "GET Phlebotomists called" );

        return LAM.<Phlebotomist> getData( PHLEBOTOMIST_TABLE, NO_FILTER );
    }

    @Path ( "Phlebotomists/{phlebotomist}" )
    @GET
    public List<Phlebotomist> getPhlebotomist(
            @PathParam ( "phlebotomist" ) final String phlebotomistId )
            throws ItemNotFoundException
    {
        LOG.info( format( "GET Phlebotomist %s called", phlebotomistId ) );

        return LAM.<Phlebotomist> getData( PHLEBOTOMIST_TABLE, format( "id='%s'", phlebotomistId ) );
    }

    @Path ( "PSCs" )
    @GET
    public List<PSC> getPSCs()
    {
        LOG.info( "GET PSCs called" );

        return LAM.<PSC> getData( PATIENT_SERVICE_CENTER_TABLE, NO_FILTER );
    }

    @Path ( "PSCs/{psc}" )
    @GET
    public List<PSC> getPSC( @PathParam ( "psc" ) final String pscId ) throws ItemNotFoundException
    {
        LOG.info( format( "GET PSC %s called", pscId ) );

        return LAM.<PSC> getData( PATIENT_SERVICE_CENTER_TABLE, format( "id='%s'", pscId ) );
    }

    @Path ( "AppointmentLabTestPKs" )
    @GET
    public List<AppointmentLabTest> getAppointmentLabTestPKs()
    {
        LOG.info( "GET AppointmentLabTestPKs called" );

        return LAM.<AppointmentLabTest> getData( APPOINTMENT_LAB_TEST_TABLE, NO_FILTER );
    }

    @Path ( "AppointmentLabTestPKs/{appointmentId}" )
    @GET
    public List<AppointmentLabTest> getAppointmentLabTestPK(
            @PathParam ( "appointmentId" ) final String appointmentId )
            throws ItemNotFoundException
    {
        LOG.info( format( "GET AppointmentLabTestPK %s called", appointmentId ) );

        return LAM.<AppointmentLabTest> getData( APPOINTMENT_LAB_TEST_TABLE,
            format( "apptid='%s'", appointmentId ) );
    }

    @Path ( "LabTests" )
    @GET
    public List<LabTest> getLabTests()
    {
        LOG.info( "GET LabTests called" );

        return LAM.<LabTest> getData( LABTEST_TABLE, NO_FILTER );
    }

    @Path ( "LabTests/{labTest}" )
    @GET
    public List<LabTest> getLabTest( @PathParam ( "labTest" ) final String labTestId )
            throws ItemNotFoundException
    {
        LOG.info( format( "GET LabTest %s called", labTestId ) );

        return LAM.<LabTest> getData( LABTEST_TABLE, format( "id='%s'", labTestId ) );
    }

    @Path ( "Diagnoses" )
    @GET
    public List<Diagnosis> getDiagnoses()
    {
        LOG.info( "GET Diagnoses called" );

        return LAM.<Diagnosis> getData( DIAGNOSIS_TABLE, NO_FILTER );
    }

    @Path ( "Diagnoses/{diagnosis}" )
    @GET
    public List<Diagnosis> getDiagnosis( @PathParam ( "diagnosis" ) final String diagnosisCode )
            throws ItemNotFoundException
    {
        LOG.info( format( "GET Diagnosis %s called", diagnosisCode ) );

        return LAM.<Diagnosis> getData( DIAGNOSIS_TABLE, format( "code='%s'", diagnosisCode ) );
    }
}
