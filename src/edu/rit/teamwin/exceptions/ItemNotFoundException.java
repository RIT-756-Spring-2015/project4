package edu.rit.teamwin.exceptions;

import static java.lang.String.format;
import edu.rit.teamwin.business.LaboratoryAppointmentManager;

/**
 * <p>
 * This exception is thrown in the by
 * {@link LaboratoryAppointmentManager#getItem(String, String)
 * LaboratoryAppointmentManager.getItem} when a given primary key filter could
 * not be found in a given table.
 * </p>
 * 
 * @author Alex Aiezza
 * @author Sagar Barbhaya
 * @author Salil Rajadhyaksha
 *
 */
@SuppressWarnings ( "serial" )
public class ItemNotFoundException extends Exception
{
    public ItemNotFoundException( final String table, final String filter )
    {
        super( format( "Item from table '%s', cannot be found using filter: '%s'.", table, filter ) );
    }
}
