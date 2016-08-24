/**
 * 
 */
package com.armedia.acm.frevvo.model;

/**
 * @author riste.tutureski
 *
 */
public interface FrevvoFormConstants
{

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

    public final static String READER_PARTICIPANT_TYPE = "reader";

    /**
     * Frevvo file types
     */
    public final static String PDF = "pdf";
    public final static String XML = "xml";

    /**
     * Frevvo form parameter keys
     */
    public static final String FREVVO_FORMS_SERVICE = "frevvo.forms.service";
    public static final String SERVICE = "frevvo.service.baseUrl";
    public static final String SERVICE_EXTERNAL = "frevvo.service.external.baseUrl";
    public static final String REDIRECT = "frevvo.browser.redirect.baseUrl";
    public static final String INTERNAL_PROTOCOL = "frevvo.protocol.internal";
    public static final String INTERNAL_HOST = "frevvo.host.internal";
    public static final String INTERNAL_PORT = "frevvo.port.internal";
    public static final String PROTOCOL = "frevvo.protocol";
    public static final String HOST = "frevvo.host";
    public static final String PORT = "frevvo.port";
    public static final String URI = "frevvo.uri";
    public static final String TIMEZONE = "frevvo.timezone";
    public static final String LOCALE = "frevvo.locale";
    public static final String TENANT = "frevvo.tenant";
    public static final String ADMIN_USER = "frevvo.admin.user";
    public static final String ADMIN_PASSWORD = "frevvo.admin.password";
    public static final String DESIGNER_USER = "frevvo.designer.user";
    public static final String PLAIN_FORM_APPLICATION_IDS = "frevvo.plain.form.application.ids";

    /**
     * XML Root element
     */
    public static final String ROOT_START = "<root>";
    public static final String ROOT_END = "</root>";

    /**
     * XML Node name and attribute where we can find the element key. The key is in format "form_<KEY>" (for example:
     * "form_complaint"). The "form_<KEY>" should be unique identifier
     */
    public static final String ELEMENT_KEY_NODE_NAME = "fd:extId";
    public static final String ELEMENT_KEY_ATTRIBUTE_NAME = "value";

    /**
     * Form Key prefix
     */
    public static final String ELEMENT_KEY_PREFIX = "form_";

    public static final String PLAIN = "plain";

}
