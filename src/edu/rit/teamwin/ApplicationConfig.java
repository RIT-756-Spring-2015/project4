/**
 * 
 */
package edu.rit.teamwin;

import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import edu.rit.teamwin.stream.LAMEntityXMLConverter;

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
        Set<Class<?>> resources = new java.util.HashSet<>();
        addRestResourceClasses( resources );
        return resources;
    }

    private void addRestResourceClasses( Set<Class<?>> resources )
    {
        resources.add( LaboratoryAppointmentService.class );
        resources.add( LAMEntityXMLConverter.AppointmentMessageBody.class );
        resources.add( LAMEntityXMLConverter.ListMessageBody.class );
    }
}
