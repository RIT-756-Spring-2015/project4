/**
 * 
 */
package edu.rit.teamwin;

import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import components.data.DB;
import components.data.IComponentsData;

import edu.rit.teamwin.business.LaboratoryAppointmentManager;
import edu.rit.teamwin.stream.LAMEntityXMLConverter;
import edu.rit.teamwin.stream.LAMExceptionXMLConverter;

/**
 * 
 * @author Alex Aiezza
 * @author Sagar Barbhaya
 * @author Salil Rajadhyaksha
 *
 */
@ApplicationPath ( "webresources" )
public class ApplicationConfig extends Application
{
    @Override
    public Set<Class<?>> getClasses()
    {
        final Set<Class<?>> classes = new java.util.HashSet<>();
        classes.add( LAMEntityXMLConverter.AppointmentMessageBody.class );
        classes.add( LAMEntityXMLConverter.ListMessageBody.class );
        classes.add( LAMExceptionXMLConverter.class );
        return classes;
    }

    @Override
    public Set<Object> getSingletons()
    {
        final Set<Object> singletons = new java.util.HashSet<>();
        addRestResourceSingletons( singletons );
        return singletons;
    }

    private void addRestResourceSingletons( final Set<Object> singletons )
    {
        final IComponentsData dataLayer = new DB();
        singletons.add( dataLayer );
        final LaboratoryAppointmentManager businessLayer = new LaboratoryAppointmentManager(
                dataLayer );
        singletons.add( businessLayer );
        singletons.add( new LaboratoryAppointmentService( businessLayer ) );
    }
}
