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
	 * This key is used in the maps for participants information that Frevvo need to show in the dropdowns.
	 * Frevvo participant information should be in the following format:
	 * 
	 * Map<type, Map<key, List<option>>
	 * 
	 * Where:
	 * - type is the type of the participant: owning group, assignee, follower ... etc ...
	 * - key is the '*' (DEFAULT_KEY from below) for showing all users or groups in the dropdowns or 'group id' that keep specific
	 * users provided 'group id'
	 * - option is the string in the format 'value=label' for users and groups
	 * 
	 */
	public final static String DEFAULT_KEY = "*";
	
	public final static String GROUP = "group";
	public final static String USER = "user";
	public final static String GROUP_USER = "group-user";
	
}
