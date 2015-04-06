
/**
 * 
 */
package com.armedia.acm.frevvo.config;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.frevvo.model.FrevvoForm;
import com.armedia.acm.frevvo.model.FrevvoFormConstants;
import com.armedia.acm.frevvo.model.FrevvoUploadedFiles;
import com.armedia.acm.frevvo.model.Strings;
import com.armedia.acm.objectonverter.AcmMarshaller;
import com.armedia.acm.objectonverter.AcmUnmarshaller;
import com.armedia.acm.objectonverter.ObjectConverter;
import com.armedia.acm.pluginmanager.service.AcmPluginManager;
import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.EcmFile;

import org.json.JSONArray;
import org.mule.api.MuleException;
import org.mule.api.client.MuleClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.plugins.ecm.model.AcmMultipartFile;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.plugins.objectassociation.dao.ObjectAssociationDao;
import com.armedia.acm.plugins.objectassociation.model.ObjectAssociation;
import com.armedia.acm.services.authenticationtoken.service.AuthenticationTokenService;
import com.armedia.acm.services.functionalaccess.service.FunctionalAccessService;
import com.armedia.acm.services.search.model.SearchConstants;
import com.armedia.acm.services.search.service.SearchResults;
import com.armedia.acm.services.users.dao.ldap.UserActionDao;
import com.armedia.acm.services.users.dao.ldap.UserDao;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.service.ldap.AcmUserActionExecutor;

/**
 * @author riste.tutureski
 *
 */
public abstract class FrevvoFormAbstractService implements FrevvoFormService{

	private Logger LOG = LoggerFactory.getLogger(FrevvoFormAbstractService.class);
	
	private Map<String, Object> properties;
	private HttpServletRequest request;
	private Authentication authentication;
	private AuthenticationTokenService authenticationTokenService;
	private UserDao userDao;
	private UserActionDao userActionDao;
	private EcmFileService ecmFileService;
    private String servletContextPath;
    private String userIpAddress;
    private EcmFileDao ecmFileDao;
    private AcmUserActionExecutor userActionExecutor;
    private MuleClient muleClient;
    private ObjectAssociationDao objectAssociationDao;
    private FunctionalAccessService functionalAccessService;
    private SearchResults searchResults;
	private AcmPluginManager acmPluginManager;

    @Override
	public Object init() {
		Object result = "";
		
		String mode = getRequest().getParameter("mode");
		String xmlId = getRequest().getParameter("xmlId");
		
		if (("edit".equals(mode) || "reinvestigate".equals(mode)) && null != xmlId && !"".equals(xmlId))
		{
			try{
				Long id = Long.parseLong(xmlId);
				result = getEcmFileService().download(id);				
			}
			catch(Exception e)
			{
				LOG.warn("EcmFile with id=" + xmlId + " is not found while edit mode. Empty Frevvo form will be shown.");
			}
		}
		
		return result;
	}

	@Override
	public Map<String, Object> getProperties() {
		return properties;
	}

	@Override
	public void setProperties(Map<String, Object> properties) {
		this.properties = properties;
	}

	@Override
	public HttpServletRequest getRequest() {
		return request;
	}

	@Override
	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	@Override
	public Authentication getAuthentication() {
		return authentication;
	}

	@Override
	public void setAuthentication(Authentication authentication) {
		this.authentication = authentication;
	}

	@Override
	public AuthenticationTokenService getAuthenticationTokenService() {
		return authenticationTokenService;
	}

	@Override
	public void setAuthenticationTokenService(
			AuthenticationTokenService authenticationTokenService) {
		this.authenticationTokenService = authenticationTokenService;		
	}

	@Override
	public UserDao getUserDao() {
		return userDao;
	}

	@Override
	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

    public String findFolderId(AcmContainer container, String objectType, Long id)
    {
        // hopefully the container has it, but sometimes the container isn't set on the parent object
        if ( container != null )
        {
            return container.getFolder().getCmisFolderId();
        }

        AcmContainer found = null;
        try
        {
            found = getEcmFileService().getOrCreateContainer(objectType, id);
            return found.getFolder().getCmisFolderId();
        }
        catch (AcmCreateObjectFailedException | AcmUserActionFailedException e)
        {
            LOG.error("Can not find or create a CMIS folder for '" + objectType + "', id '" + id + "'", e);
            return null;
        }

    }

    @Override
	public UserActionDao getUserActionDao() {
		return userActionDao;
	}

	@Override
	public void setUserActionDao(UserActionDao userActionDao) {
		this.userActionDao = userActionDao;
	}
	
	@Override
	public EcmFileService getEcmFileService()
    {
        return ecmFileService;
    }
	
	@Override
	public void setEcmFileService(EcmFileService ecmFileService)
    {
        this.ecmFileService = ecmFileService;
    }
	
	@Override
	public String getServletContextPath()
    {
        return servletContextPath;
    }

	@Override
    public void setServletContextPath(String servletContextPath)
    {
        this.servletContextPath = servletContextPath;
    }
	
	public Object convertFromXMLToObject(String xml, Class<?> c) 
	{
		AcmUnmarshaller unmarshaller = ObjectConverter.createXMLUnmarshaller();
		Object obj = unmarshaller.unmarshall(xml, c);
			
		return obj;
	}
	
	public String convertFromObjectToXML(Object obj) 
	{
		AcmMarshaller marshaller = ObjectConverter.createXMLMarshaller();
		String xml = marshaller.marshal(obj);
		
		return xml;
	}
	
	protected void updateXML(String xml, String formName, Long id, Authentication auth)
	{
		ObjectAssociation association = getObjectAssociationDao().findChildOfType(formName.toUpperCase(), id, formName.toLowerCase() + "_xml");
		
		if (association != null)
		{
			EcmFile ecmFile = getEcmFileDao().find(association.getTargetId());
			AcmMultipartFile file = new AcmMultipartFile();
			file.setInputStream(new ByteArrayInputStream(xml.getBytes()));
			
			try 
			{
				getEcmFileService().update(ecmFile, file, auth);
			} 
			catch (AcmCreateObjectFailedException e) 
			{
				LOG.error("Failed to update XML file.", e);
			}    				
		}
	}
	
	public void updateXMLAttachment(MultiValueMap<String, MultipartFile> attachments, String formName, Object form) throws Exception
	{
		String updatedXmlFile = convertFromObjectToXML(form);
        if (updatedXmlFile != null && attachments.containsKey("form_" + formName))
        {
        	List<MultipartFile> files = attachments.get("form_" + formName);
        	if (files != null && files.size() == 1)
        	{
        		MultipartFile originalXml = files.get(0);
        		InputStream updatedXmlContent = new ByteArrayInputStream(updatedXmlFile.getBytes());
        		AcmMultipartFile updatedXml = new AcmMultipartFile(originalXml.getName(), originalXml.getOriginalFilename(), originalXml.getContentType(), originalXml.isEmpty(), originalXml.getSize(), originalXml.getBytes(), updatedXmlContent, false);
        		
        		// Remove old XML file
        		attachments.remove("form_" + formName);
        		
        		// Add updated XML file
        		attachments.add("form_" + formName, updatedXml);
        	}
        }
	}
	
	public FrevvoUploadedFiles saveAttachments(
            MultiValueMap<String, MultipartFile> attachments,
            String targetCmisFolderId,
            String parentObjectType,
            Long parentObjectId) throws AcmCreateObjectFailedException, AcmUserActionFailedException
	{
        FrevvoUploadedFiles retval = new FrevvoUploadedFiles();

		if ( attachments != null )
		{
			for ( Map.Entry<String, List<MultipartFile>> entry : attachments.entrySet() )
			{
				String mode = getRequest().getParameter("mode");
				String xmlId = getRequest().getParameter("xmlId");
				String pdfId = getRequest().getParameter("pdfId");
				
                if ( entry.getKey().startsWith("form_"))
                {
                    // form xml...
                    List<MultipartFile> xml = entry.getValue();
                    if ( xml != null && xml.size() == 1 )
                    {
                        MultipartFile xmlAttachment = xml.get(0);
                        
                        EcmFile formXml = null;
                        
                        // Update XML form if the mode is "edit", otherwise create new
                        if ("edit".equals(mode) && null != xmlId && !"".equals(xmlId))
                        {
                        	EcmFile file = null;
                        	try{
                        		Long id = Long.parseLong(xmlId);
                        		file = getEcmFileDao().find(id);
                        	}
                        	catch(Exception e)
                        	{
                        		LOG.warn("The file with id=" + xmlId + " is not found. The update will not proceed.");
                        	}
                        	
                        	formXml = getEcmFileService().update(
                        			file,
                        			xmlAttachment,
                        			getAuthentication());
                        }
                        else
                        {
                        	formXml = uploadFile(
                                getFormName() + "_xml",
                                targetCmisFolderId,
                                parentObjectType,
                                parentObjectId,
                                xmlAttachment);
                        }
                        retval.setFormXml(formXml);
                    }
                }
                else if ( !entry.getKey().equals("UploadFiles" ) )
                {
                    // form pdf
                    List<MultipartFile> pdf = entry.getValue();
                    if ( pdf != null && pdf.size() == 1 )
                    {
                        MultipartFile pdfAttachment = pdf.get(0);
                        EcmFile pdfRendition = null;
                        
                        // Update PDF form if the mode is "edit", otherwise create new
                        if ("edit".equals(mode) && null != pdfId && !"".equals(pdfId))
                        {
                        	EcmFile file = null;
                        	try{
                        		Long id = Long.parseLong(pdfId);
                        		file = getEcmFileDao().find(id);
                        	}
                        	catch(Exception e)
                        	{
                        		LOG.warn("The file with id=" + pdfId + " is not found. The update will not proceed.");
                        	}
                        	pdfRendition = getEcmFileService().update(
                        			file,
                        			pdfAttachment,
                        			getAuthentication());
                        }
                        else
                        {
                        	pdfRendition = uploadFile(
	                                getFormName(),
	                                targetCmisFolderId,
	                                parentObjectType,
	                                parentObjectId,
	                                pdfAttachment);
                        }
                        retval.setPdfRendition(pdfRendition);
                    }
                }
                else
                {
                    // this must be the other uploaded files
                    final List<MultipartFile> attachmentsList = entry.getValue();

                    if (attachmentsList != null && !attachmentsList.isEmpty() )
                    {
                        for (final MultipartFile attachment : attachmentsList)
                        {
                            EcmFile uploaded = uploadFile(
                                    "attachment",
                                    targetCmisFolderId,
                                    parentObjectType,
                                    parentObjectId,
                                    attachment);
                            retval.getUploadedFiles().add(uploaded);
                        }
                    }
                }

			}
		}

        return retval;
	}

    private EcmFile uploadFile(String fileType,
                            String targetCmisFolderId,
                            String parentObjectType,
                            Long parentObjectId,
                            MultipartFile attachment)
            throws AcmCreateObjectFailedException, AcmUserActionFailedException
    {
        try
        {
            AcmMultipartFile file = new AcmMultipartFile(
                attachment.getName(),
                attachment.getOriginalFilename(),
                attachment.getContentType(),
                attachment.isEmpty(),
                attachment.getSize(),
                attachment.getBytes(),
                attachment.getInputStream(),
                true);

            EcmFile uploaded = getEcmFileService().upload(
                fileType,
                file,
                getAuthentication(),
                targetCmisFolderId,
                parentObjectType,
                parentObjectId);

            return uploaded;
        }
        catch (IOException e)
        {
            LOG.error("Could not upload file: " + e.getMessage(), e);
            throw new AcmCreateObjectFailedException("file", e.getMessage(), e);
        }
    }

    public String cleanXML(String xml)
	{
		if (xml != null){
			String changedXML = xml.replaceAll("(?s)<rta_label.*?<\\/rta_label>", "");
			changedXML = changedXML.replaceAll("<[^<>]*?\\/>", "");
			changedXML = changedXML.replaceAll("(?s)<[^\\/<>]*?>[\n\r\t ]*?<\\/[^<>]*?>", "");
			
			if (!xml.equals(changedXML)){
				return cleanXML(changedXML);
			}
		}
		return xml;
	}
	
	public List<String> convertToList(String source, String delimiter){
		if (source != null && !"".equals(source)) {
			String[] sourceArray = source.split(delimiter, -1);
			return new LinkedList<String>(Arrays.asList(sourceArray)); 
		}
		
		return null;
	}
	
	public FrevvoForm populateEditInformation(FrevvoForm form, AcmContainer container, String formName)
	{		
		Long containerId = null;
		Long folderId = null;
		
		if (container != null)
		{
			containerId = container.getId();
			
			if (container.getFolder() != null)
			{
				folderId = container.getFolder().getId();
			}
		}
		
		// Set edit mode
		form.setMode(FrevvoFormConstants.EDIT);
		
		if (containerId != null && folderId != null)
		{
			// Set xml id
			EcmFile ecmFileXML = getEcmFileDao().findForContainerFolderAndFileType(containerId, folderId, formName + "_xml");
			
			if (ecmFileXML != null && ecmFileXML.getId() != null)
			{
				form.setXmlId(ecmFileXML.getId().toString());
			}
			
			// Set pdf id
			EcmFile ecmFilePDF = getEcmFileDao().findForContainerFolderAndFileType(containerId, folderId, formName);
			
			if (ecmFilePDF != null && ecmFilePDF.getId() != null)
			{
				form.setPdfId(ecmFilePDF.getId().toString());
			}
		}
		
		return form;
	}
	
	public String getDocUriParameter(String parameterName)
	{		
		String docUriParameters = getRequest().getParameter(FrevvoFormConstants.DOC_URI_PARAMETERS_HOLDER_NAME);
		
		if (docUriParameters != null && parameterName != null)
		{
			String[] parameters = docUriParameters.split(FrevvoFormConstants.DOC_URI_PARAMETERS_DELIMITER);
			
			if (parameters != null)
			{
				for (String parameterNameValuePair : parameters)
				{
					String[] parameterArray = parameterNameValuePair.split(FrevvoFormConstants.DOC_URI_PARAMETER_DELIMITER);
					
					if (parameterArray != null && parameterArray.length > 0)
					{
						if (parameterName.equals(parameterArray[0]))
						{
							if (parameterArray.length > 1)
							{
								return parameterArray[1];
							}
							else
							{
								return "";
							}
						}
					}
				}
			}
		}
		
		return "";
	}
	
	public String getDocUriParameters()
	{		
		return getRequest().getParameter(FrevvoFormConstants.DOC_URI_PARAMETERS_HOLDER_NAME);
	}
	
	/**
	 * This method will return map that need Frevvo form for participant section
	 * 
	 * @param participantTypes
	 * @param formName
	 * @param approvePrivilege
	 * @return
	 */
	public Map<String, Map<String, Strings>> getParticipants(List<String> participantTypes, String formName, String approvePrivilege)
	{
		Map<String, String> groups = null;
		
		if (participantTypes != null && participantTypes.size() > 0)
		{
			Map<String, Map<String, Strings>> participantsOptions = new HashMap<>();
			for (String participantType : participantTypes)
			{
				String type = "";
				String[] participantTypeArray = participantType.split("=");
				if (participantTypeArray != null && participantTypeArray.length == 2)
				{
					type = participantTypeArray[0];
					String privilege = (String) getProperties().get(formName + "." + type.replace(" ", "_") + ".privilege");
					
					try
					{
						List<String> rolesForPrivilege = getAcmPluginManager().getRolesForPrivilege(privilege);
						Map<String, List<String>> rolesToGroups = getFunctionalAccessService().getApplicationRolesToGroups();
						
						if (privilege.equals(approvePrivilege))
						{
							if (groups == null)
							{
								groups = getGroups(rolesForPrivilege, rolesToGroups, 0, 1000, "name ASC", getAuthentication());
							}
							
							if (groups != null)
							{
								Map<String, Strings> optionsMap = new HashMap<>();
								if (!"owning group".equals(type))
								{
									optionsMap = getGroupUsersMap(groups, rolesForPrivilege, rolesToGroups);
								}
								else
								{
									optionsMap = getGroupsMap(groups);
								}
								participantsOptions.put(type, optionsMap);
							}
						}
						else
						{
							Map<String, Strings> optionsMap = getUsersMap(FrevvoFormConstants.DEFAULT_KEY, null, rolesForPrivilege, rolesToGroups);
							participantsOptions.put(type, optionsMap);
						}
					}
					catch(Exception e)
					{
						LOG.warn("Cannot find users with privilege = " + type + ". Continue and not break the execution - normal behavior when configuration has some wrong data.");
					}
				}
			}
			
			return participantsOptions;
		}
		
		return null;
	}
	
	/**
	 * This method will return information which users belong in which group. In other words, this is map
	 * between group and users. '*' is special key (which means all users)
	 * 
	 * @param groups
	 * @param rolesForPrivilege
	 * @param rolesToGroups
	 * @return
	 */
	private Map<String, Strings> getGroupUsersMap(Map<String, String> groups, List<String> rolesForPrivilege, Map<String, List<String>> rolesToGroups)
	{
		Map<String, Strings> optionsMap = new HashMap<>();
		
		// Update optionsMap for each group
		for (Entry<String, String> entry : groups.entrySet())
		{
			String groupId = entry.getKey();
			
			optionsMap = getUsersMap(groupId, optionsMap, rolesForPrivilege, rolesToGroups);
		}
		
		return optionsMap;
	}
	
	/**
	 * This method will return updated map (add users to the map 'source') for given key (which can be group id or special key '*')
	 * 
	 * @param key
	 * @param source
	 * @param rolesForPrivilege
	 * @param rolesToGroups
	 * @return
	 */
	private Map<String, Strings> getUsersMap(String key, Map<String, Strings> source, List<String> rolesForPrivilege, Map<String, List<String>> rolesToGroups)
	{
		if (source == null)
		{
			source = new HashMap<String, Strings>();
		}
		
		String group = null;
		if (!FrevvoFormConstants.DEFAULT_KEY.equals(key))
		{
			group = key;
		}
		
		Set<AcmUser> usersSet = getFunctionalAccessService().getUsersByRolesAndGroups(rolesForPrivilege, rolesToGroups, group, null);
        
		List<AcmUser> users = new ArrayList<>(usersSet);
        
        if (users != null && users.size() > 0) {
        	Strings options = new Strings();
        	for (int i = 0; i < users.size(); i++) {
        		options.add(users.get(i).getUserId() + "=" + users.get(i).getFullName());
        	}
        	
        	source.put(key, options);
        }
		
		return source;
	}
	
	/**
	 * This method will return all groups in map that need Frevvo with special key "*", which means all groups
	 * 
	 * @param groups
	 * @return
	 */
	private Map<String, Strings> getGroupsMap(Map<String, String> groups)
	{
		Map<String, Strings> optionsMap = new HashMap<>();
		Strings options = new Strings();
		
		if (groups != null)
		{
			for (Entry<String, String> entry : groups.entrySet())
			{
				String groupId = entry.getKey();
				String groupName = entry.getValue();
		        
		        options.add(groupId + "=" + groupName);
			}
		}
		
		optionsMap.put(FrevvoFormConstants.DEFAULT_KEY, options);
		
		return optionsMap;
	}
	
	/**
	 * This method will return the groups in id-name format
	 * 
	 * @param roles
	 * @param rolesToGroups
	 * @param startRow
	 * @param maxRows
	 * @param sort
	 * @param auth
	 * @return
	 * @throws MuleException
	 */
	private Map<String, String> getGroups(List<String> roles, Map<String, List<String>> rolesToGroups, int startRow, int maxRows, String sort, Authentication auth) throws MuleException
	{
		Map<String, String> groups = new HashMap<>();
		
		String groupsFromSolr = getFunctionalAccessService().getGroupsByPrivilege(roles, rolesToGroups, startRow, maxRows, sort, auth);
		
		if (groupsFromSolr != null)
		{
			JSONArray docs = getSearchResults().getDocuments(groupsFromSolr);
			
			if (docs != null)
			{
				for (int i = 0; i < docs.length(); i++)
				{
					String key = getSearchResults().extractString(docs.getJSONObject(i), SearchConstants.PROPERTY_OBJECT_ID_S);
					String value = getSearchResults().extractString(docs.getJSONObject(i), SearchConstants.PROPERTY_NAME);
					
					groups.put(key, value);
				}
			}
		}
		
		return groups;
	}

    @Override
    public String getUserIpAddress()
    {
        return userIpAddress;
    }

    @Override
    public void setUserIpAddress(String userIpAddress)
    {
        this.userIpAddress = userIpAddress;
    }

	/**
	 * @return the ecmFileDao
	 */
	public EcmFileDao getEcmFileDao() {
		return ecmFileDao;
	}

	/**
	 * @param ecmFileDao the ecmFileDao to set
	 */
	public void setEcmFileDao(EcmFileDao ecmFileDao) {
		this.ecmFileDao = ecmFileDao;
	}

	/**
	 * @return the userActionExecutor
	 */
	public AcmUserActionExecutor getUserActionExecutor() {
		return userActionExecutor;
	}

	/**
	 * @param userActionExecutor the userActionExecutor to set
	 */
	public void setUserActionExecutor(AcmUserActionExecutor userActionExecutor) {
		this.userActionExecutor = userActionExecutor;
	}

	public MuleClient getMuleClient() {
		return muleClient;
	}

	public void setMuleClient(MuleClient muleClient) {
		this.muleClient = muleClient;
	}	
	
	public ObjectAssociationDao getObjectAssociationDao() {
		return objectAssociationDao;
	}

	public void setObjectAssociationDao(ObjectAssociationDao objectAssociationDao) {
		this.objectAssociationDao = objectAssociationDao;
	}

	public FunctionalAccessService getFunctionalAccessService() {
		return functionalAccessService;
	}

	public void setFunctionalAccessService(
			FunctionalAccessService functionalAccessService) {
		this.functionalAccessService = functionalAccessService;
	}

	public SearchResults getSearchResults() {
		return searchResults;
	}

	public void setSearchResults(SearchResults searchResults) {
		this.searchResults = searchResults;
	}

	public AcmPluginManager getAcmPluginManager() {
		return acmPluginManager;
	}

	public void setAcmPluginManager(AcmPluginManager acmPluginManager) {
		this.acmPluginManager = acmPluginManager;
	}
	
}
