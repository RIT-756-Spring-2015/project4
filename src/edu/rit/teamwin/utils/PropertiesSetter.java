package edu.rit.teamwin.utils;


import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Set properties from a properties file.
 *
 * @author Alex Aiezza
 */
public class PropertiesSetter
{
    //@formatter:off
    /**
     * <p>
     * <strong>Property File:</strong> {@value}
     * <!-- tt>${com.sun.aas.instanceRoot
     * }/applications/LAMSAppointment/resources/project4.properties</tt -->
     * </p>
     */
    //@formatter:on
    private static final String PROPERTIES_FILE = "project4.properties";

    public PropertiesSetter() throws IOException
    {
        final InputStream inputStream = getClass().getClassLoader().getResourceAsStream(
            PROPERTIES_FILE );

        final Properties p = new Properties();
        p.load( inputStream );

        for ( final String name : p.stringPropertyNames() )
        {
            final String value = p.getProperty( name );
            System.setProperty( name, value );
        }
    }
}
