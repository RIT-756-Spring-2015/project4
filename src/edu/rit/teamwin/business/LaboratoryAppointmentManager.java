/**
 * 
 */
package edu.rit.teamwin.business;

import java.util.List;

import components.data.Appointment;
import components.data.DB;
import components.data.IComponentsData;

import edu.rit.teamwin.service.LaboratoryAppointmentService;

/**
 * <p>
 * This class represents the <strong>BUSINESS LAYER</strong>. An instance of
 * this class will be directly utilized by the
 * {@link LaboratoryAppointmentService Service Layer}.
 * </p>
 * 
 * @author Alex Aiezza
 * @author Sagar Barbhaya
 * @author Salil Rajadhyaksha
 *
 */
public class LaboratoryAppointmentManager
{

    private final IComponentsData dataLayer;

    /**
     * @param dataLayer
     */
    public LaboratoryAppointmentManager( final IComponentsData dataLayer )
    {
        this.dataLayer = dataLayer;
    }

    public static void main( String [] args )
    {
        LaboratoryAppointmentManager lam = new LaboratoryAppointmentManager( new DB() );

        lam.dataLayer.initialLoad( "LAMS" );
        final List<Object> results = lam.dataLayer.getData( "Appointment", "" );

        for ( final Object app : results )
        {
            System.out.println( ( (Appointment) app ).getId() );
        }

    }

}
