package de.atennert.connector.pluginloader;

/**
 * Interface for filters that are used to filter classes
 * by their type.
 * 
 * @author Andreas Tennert
 */
public interface IInstanceFilter
{
	/**
	 * @param o class instance to check
	 * @return <code>true</code> if the given instance
	 * 		has the correct type, <code>false</code>
	 */
	boolean isCorrectType(Object o);
}
