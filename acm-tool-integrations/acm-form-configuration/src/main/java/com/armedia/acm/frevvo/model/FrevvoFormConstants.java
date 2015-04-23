/**
 * 
 */
package com.armedia.acm.frevvo.model;

/**
 * @author riste.tutureski
 *
 */
public interface FrevvoFormConstants {

	/**
	 * Frevvo form modes
	 */
	public final static String EDIT = "edit";
	
	/**
	 * DOC URI paremeters from Frevvo to ArkCase are sent in the following format:
	 * 
	 * name1:value1,name2:value2,name3:value3 ...
	 * 
	 * This is the name of the URL parameter who hold these values
	 */
	public final static String DOC_URI_PARAMETERS_HOLDER_NAME = "docUriParameters";
	
	/**
	 * DOC URI paremeters from Frevvo to ArkCase are sent in the following format:
	 * 
	 * name1:value1,name2:value2,name3:value3 ...
	 * 
	 * Delimiter for each name-value pair is ","
	 */
	public final static String DOC_URI_PARAMETERS_DELIMITER = ",";
	
	/**
	 * DOC URI paremeters from Frevvo to ArkCase are sent in the following format:
	 * 
	 * name1:value1,name2:value2,name3:value3 ...
	 * 
	 * Delimiter for name and value pair is ":"
	 */
	public final static String DOC_URI_PARAMETER_DELIMITER = ":";
	
	/**
	 * Default user key
	 */
	public final static String DEFAULT_USER = "*";
	
	/**
	 * THIS WILL BE REMOVED ONCE WE IMPLEMENT GROUP PICKER ON FREVVO SIDE
	 */
	public final static String OWNING_GROUP = "owning group";
	
}
