
/**
 *
 */
package com.armedia.acm.frevvo.config;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.frevvo.model.FrevvoForm;
import com.armedia.acm.frevvo.model.FrevvoFormConstants;
import com.armedia.acm.frevvo.model.FrevvoUploadedFiles;
import com.armedia.acm.frevvo.model.Strings;
import com.armedia.acm.muletools.mulecontextmanager.MuleContextManager;
import com.armedia.acm.objectonverter.AcmMarshaller;
import com.armedia.acm.objectonverter.AcmUnmarshaller;
import com.armedia.acm.objectonverter.DateFormats;
import com.armedia.acm.objectonverter.ObjectConverter;
import com.armedia.acm.pluginmanager.service.AcmPluginManager;
import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.AcmContainerEntity;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.AcmMultipartFile;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileConstants;
import com.armedia.acm.plugins.ecm.service.AcmFolderService;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.plugins.ecm.utils.FolderAndFilesUtils;
import com.armedia.acm.plugins.objectassociation.dao.ObjectAssociationDao;
import com.armedia.acm.services.authenticationtoken.service.AuthenticationTokenService;
import com.armedia.acm.services.functionalaccess.service.FunctionalAccessService;
import com.armedia.acm.services.search.model.SearchConstants;
import com.armedia.acm.services.search.service.SearchResults;
import com.armedia.acm.services.users.dao.ldap.UserActionDao;
import com.armedia.acm.services.users.dao.ldap.UserDao;
import com.armedia.acm.services.users.service.ldap.AcmUserActionExecutor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONObject;
import org.mule.api.MuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author riste.tutureski
 */
public abstract class FrevvoFormAbstractService implements FrevvoFormService
{

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
    private MuleContextManager muleContextManager;
    private ObjectAssociationDao objectAssociationDao;
    private FunctionalAccessService functionalAccessService;
    private SearchResults searchResults;
    private AcmPluginManager acmPluginManager;
    private FolderAndFilesUtils folderAndFilesUtils;
    private AcmFolderService acmFolderService;
    private Gson gson = new GsonBuilder().setDateFormat(DateFormats.FREVVO_DATE_FORMAT).create();

    @Override
    public Object init()
    {
        String result = "";

        String mode = getRequest().getParameter("mode");
        String containerIdString = getRequest().getParameter("containerId");
        String folderIdString = getRequest().getParameter("folderId");

        if (("edit".equals(mode) || "reinvestigate".equals(mode)) && null != containerIdString && !"".equals(containerIdString)
                && null != folderIdString && !"".equals(folderIdString))
        {
            Long xmlId = null;
            try
            {
                String fileType = getFormName().toLowerCase() + "_xml";
                EcmFile ecmFile = getEcmFile(containerIdString, folderIdString, fileType);

                if (ecmFile != null)
                {
                    xmlId = ecmFile.getId();
                    result = getEcmFileService().download(xmlId);
                }
            } catch (Exception e)
            {
                LOG.warn("EcmFile with id={} is not found while edit mode. Empty Frevvo form will be shown.", xmlId);
            }
        }

        return result;
    }

    @Override
    public JSONObject createResponse(Object object)
    {
        String jsonString = getGson().toJson(object);

        LOG.debug("returning JSON: {}", jsonString);
        JSONObject json = new JSONObject(jsonString);

        return json;
    }

    public abstract Object convertToFrevvoForm(Object obj, Object form);

    @Override
    public Map<String, Object> getProperties()
    {
        return properties;
    }

    @Override
    public void setProperties(Map<String, Object> properties)
    {
        this.properties = properties;
    }

    @Override
    public HttpServletRequest getRequest()
    {
        return request;
    }

    @Override
    public void setRequest(HttpServletRequest request)
    {
        this.request = request;
    }

    @Override
    public Authentication getAuthentication()
    {
        return authentication;
    }

    @Override
    public void setAuthentication(Authentication authentication)
    {
        this.authentication = authentication;
    }

    @Override
    public AuthenticationTokenService getAuthenticationTokenService()
    {
        return authenticationTokenService;
    }

    @Override
    public void setAuthenticationTokenService(AuthenticationTokenService authenticationTokenService)
    {
        this.authenticationTokenService = authenticationTokenService;
    }

    @Override
    public UserDao getUserDao()
    {
        return userDao;
    }

    @Override
    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }

    public String findFolderId(AcmContainer container, String objectType, Long id)
    {
        // hopefully the container has it, but sometimes the container isn't set on the parent object
        if (container != null)
        {
            return container.getFolder().getCmisFolderId();
        }

        AcmContainer found = null;
        try
        {
            found = getEcmFileService().getOrCreateContainer(objectType, id);
            return found.getFolder().getCmisFolderId();
        } catch (AcmCreateObjectFailedException | AcmUserActionFailedException e)
        {
            LOG.error("Can not find or create a CMIS folder for '{}', id '{}'", objectType, id, e);
            return null;
        }

    }

    public String findCmisFolderId(Long folderId, AcmContainer container, String objectType, Long objectId)
    {
        String cmisFolderId = null;
        if (folderId != null)
        {
            try
            {
                AcmFolder folder = getAcmFolderService().findById(folderId);
                cmisFolderId = folder.getCmisFolderId();
            } catch (Exception e)
            {
                LOG.warn("Cannot find folder for provided folderId={}. Will try to find the attachment folder or to create one.", folderId);
            }
        }

        if (cmisFolderId == null)
        {
            return findFolderIdForAttachments(container, objectType, objectId);
        }

        return cmisFolderId;
    }

    public String findFolderIdForAttachments(AcmContainer container, String objectType, Long id)
    {
        // hopefully the container has it, but sometimes the container isn't set on the parent object
        if (container != null)
        {
            // the atttachment folder does not have a not-null constraint :-( but the folder id is not-null in the db.
            return container.getAttachmentFolder() == null ? container.getFolder().getCmisFolderId()
                    : container.getAttachmentFolder().getCmisFolderId();
        }

        AcmContainer found = null;
        try
        {
            found = getEcmFileService().getOrCreateContainer(objectType, id);
            return found.getAttachmentFolder().getCmisFolderId();
        } catch (AcmCreateObjectFailedException | AcmUserActionFailedException e)
        {
            LOG.error("Can not find or create a CMIS folder for '{}', id '{}'", objectType, id, e);
            return null;
        }

    }

    @Override
    public UserActionDao getUserActionDao()
    {
        return userActionDao;
    }

    @Override
    public void setUserActionDao(UserActionDao userActionDao)
    {
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

    public void updateXML(AcmContainerEntity entity, Authentication auth, Class<?> c)
    {
        if (entity != null)
        {
            EcmFile ecmFile = getEcmFile(entity, getFormName().toLowerCase());

            // if there is no ecmFile then we have to XML file to update so we can stop here.

            if (ecmFile != null)
            {
                Object form = getExistingForm(ecmFile.getId(), c);
                form = convertToFrevvoForm(entity, form);

                if (form != null)
                {
                    String xml = convertFromObjectToXML(form);
                    updateXML(xml, ecmFile, auth);
                }
            }
        }
    }

    protected void updateXML(String xml, EcmFile ecmFile, Authentication auth)
    {
        if (xml != null && ecmFile != null)
        {
            AcmMultipartFile file = new AcmMultipartFile();
            file.setInputStream(new ByteArrayInputStream(xml.getBytes()));

            try
            {
                getEcmFileService().update(ecmFile, file, auth);
            } catch (AcmCreateObjectFailedException e)
            {
                LOG.error("Failed to update XML file.", e);
            }
        }
    }

    public Object getExistingForm(Long id, Class<?> c)
    {
        Object retval = null;

        if (id != null)
        {
            String existingXml = "";
            try
            {
                // Taking existing XML for the form
                existingXml = getEcmFileService().download(id);

                // Creating Frevvo form from the existing XML
                retval = convertFromXMLToObject(cleanXML(existingXml), c);
            } catch (MuleException e)
            {
                LOG.error("Cannot download file with id={}", id);
            }
        }

        return retval;
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
                AcmMultipartFile updatedXml = new AcmMultipartFile(originalXml.getName(), originalXml.getOriginalFilename(),
                        originalXml.getContentType(), originalXml.isEmpty(), originalXml.getSize(), originalXml.getBytes(),
                        updatedXmlContent, false);

                // Remove old XML file
                attachments.remove("form_" + formName);

                // Add updated XML file
                attachments.add("form_" + formName, updatedXml);
            }
        }
    }

    public MultiValueMap<String, MultipartFile> updateFileName(String newName, String type,
            MultiValueMap<String, MultipartFile> attachments) throws IOException
    {
        String key = findAttachmentKey(type, attachments);

        if (key != null)
        {
            List<MultipartFile> files = attachments.get(key);
            if (files != null && files.size() == 1)
            {
                MultipartFile originalFile = files.get(0);
                String fullFileName = newName + "." + type.toLowerCase();
                AcmMultipartFile updatedFile = new AcmMultipartFile(fullFileName, fullFileName, originalFile.getContentType(),
                        originalFile.isEmpty(), originalFile.getSize(), originalFile.getBytes(), originalFile.getInputStream(), false);

                // Remove old file
                attachments.remove(key);

                // Add updated file
                attachments.add(newName, updatedFile);
            }
        }

        return attachments;
    }

    private String findAttachmentKey(String type, MultiValueMap<String, MultipartFile> attachments)
    {
        if (attachments != null)
        {
            for (Map.Entry<String, List<MultipartFile>> entry : attachments.entrySet())
            {
                if (FrevvoFormConstants.PDF.equalsIgnoreCase(type))
                {
                    if (!entry.getKey().startsWith("form_") && !entry.getKey().equals("UploadFiles"))
                    {
                        // Then this file is PDF
                        return entry.getKey();
                    }
                } else if (FrevvoFormConstants.XML.equalsIgnoreCase(type))
                {
                    if (entry.getKey().startsWith("form_"))
                    {
                        // Then this file is XML
                        return entry.getKey();
                    }
                }
            }
        }

        return null;
    }

    public FrevvoUploadedFiles saveAttachments(MultiValueMap<String, MultipartFile> attachments, String targetCmisFolderId,
            String parentObjectType, Long parentObjectId) throws AcmCreateObjectFailedException, AcmUserActionFailedException
    {
        return saveAttachments(getAttachmentFileType(""), attachments, targetCmisFolderId, parentObjectType, parentObjectId);
    }

    public FrevvoUploadedFiles saveAttachments(String fileType, MultiValueMap<String, MultipartFile> attachments, String targetCmisFolderId,
            String parentObjectType, Long parentObjectId) throws AcmCreateObjectFailedException, AcmUserActionFailedException
    {
        FrevvoUploadedFiles retval = new FrevvoUploadedFiles();

        if (attachments != null)
        {
            for (Map.Entry<String, List<MultipartFile>> entry : attachments.entrySet())
            {
                String mode = getRequest().getParameter("mode");
                String containerIdString = getRequest().getParameter("containerId");
                String folderIdString = getRequest().getParameter("folderId");

                if (entry.getKey().startsWith("form_"))
                {
                    // form xml...
                    List<MultipartFile> xml = entry.getValue();
                    if (xml != null && xml.size() == 1)
                    {
                        MultipartFile xmlAttachment = xml.get(0);

                        EcmFile formXml = null;

                        // Update XML form if the mode is "edit", otherwise create new
                        if ("edit".equals(mode) && null != containerIdString && !"".equals(containerIdString) && null != folderIdString
                                && !"".equals(folderIdString))
                        {
                            EcmFile file = getEcmFile(containerIdString, folderIdString, getFormName().toLowerCase() + "_xml");

                            formXml = getEcmFileService().update(file, xmlAttachment, getAuthentication());
                        } else
                        {
                            formXml = uploadFile(getFormName() + "_xml", targetCmisFolderId, parentObjectType, parentObjectId,
                                    xmlAttachment);
                        }
                        retval.setFormXml(formXml);
                    }
                } else if (!entry.getKey().equals("UploadFiles"))
                {
                    // form pdf
                    List<MultipartFile> pdf = entry.getValue();
                    if (pdf != null && pdf.size() == 1)
                    {
                        MultipartFile pdfAttachment = pdf.get(0);
                        EcmFile pdfRendition = null;
                        String _fileType = getFormName();

                        if (pdfAttachment != null && pdfAttachment.getContentType() != null
                                && pdfAttachment.getContentType().contains(EcmFileConstants.MIME_TYPE_PNG)
                                && pdfAttachment.getContentType().contains(EcmFileConstants.MIME_TYPE_FREVVO_SIGNATURE_KEY))
                        {
                            _fileType += "_signature";
                        }

                        // Update PDF form if the mode is "edit", otherwise create new
                        if ("edit".equals(mode) && null != containerIdString && !"".equals(containerIdString) && null != folderIdString
                                && !"".equals(folderIdString))
                        {
                            EcmFile file = getEcmFile(containerIdString, folderIdString, _fileType.toLowerCase());
                            file.setFileName(pdfAttachment.getName());
                            getEcmFileDao().save(file);

                            pdfRendition = getEcmFileService().update(file, pdfAttachment, getAuthentication());
                        } else
                        {
                            pdfRendition = uploadFile(_fileType, targetCmisFolderId, parentObjectType, parentObjectId, pdfAttachment);
                        }
                        retval.setPdfRendition(pdfRendition);
                    }
                } else
                {
                    // this must be the other uploaded files
                    final List<MultipartFile> attachmentsList = entry.getValue();

                    if (attachmentsList != null && !attachmentsList.isEmpty())
                    {
                        for (final MultipartFile attachment : attachmentsList)
                        {
                            EcmFile uploaded = uploadFile(fileType, targetCmisFolderId, parentObjectType, parentObjectId, attachment);
                            retval.getUploadedFiles().add(uploaded);
                        }
                    }
                }

            }
        }

        return retval;
    }

    private EcmFile uploadFile(String fileType, String targetCmisFolderId, String parentObjectType, Long parentObjectId,
            MultipartFile attachment) throws AcmCreateObjectFailedException, AcmUserActionFailedException
    {
        try
        {
            AcmMultipartFile file = new AcmMultipartFile(attachment.getName(), attachment.getOriginalFilename(),
                    attachment.getContentType(), attachment.isEmpty(), attachment.getSize(), attachment.getBytes(),
                    attachment.getInputStream(), true);

            EcmFile uploaded = getEcmFileService().upload(attachment.getOriginalFilename(), fileType, file, getAuthentication(),
                    targetCmisFolderId, parentObjectType, parentObjectId);

            return uploaded;
        } catch (IOException e)
        {
            LOG.error("Could not upload file: {}", e.getMessage(), e);
            throw new AcmCreateObjectFailedException("file", e.getMessage(), e);
        }
    }

    public EcmFile getEcmFile(AcmContainerEntity entity, String formName)
    {
        EcmFile ecmFile = null;
        if (entity != null)
        {
            // First find the XML that is already in the system and create Frevvo form
            Long containerId = entity.getContainer().getId();
            Long folderId = entity.getContainer().getAttachmentFolder().getId();
            String fileType = formName + "_xml";

            ecmFile = getEcmFileDao().findForContainerAttachmentFolderAndFileType(containerId, folderId, fileType);
        }

        return ecmFile;
    }

    private EcmFile getEcmFile(String containerIdString, String folderIdString, String fileType)
    {
        EcmFile ecmFile = null;

        try
        {
            Long containerId = Long.parseLong(containerIdString);
            Long folderId = Long.parseLong(folderIdString);

            ecmFile = getEcmFileDao().findForContainerAttachmentFolderAndFileType(containerId, folderId, fileType);
        } catch (Exception e)
        {
            LOG.warn("EcmFile with for containerId={} and folderId={} is not found.", containerIdString, folderIdString);
        }

        return ecmFile;
    }

    public String cleanXML(String xml)
    {
        if (xml != null)
        {
            String changedXML = xml.replaceAll("(?s)<rta_label.*?<\\/rta_label>", "");
            changedXML = changedXML.replaceAll("<[^<>]*?\\/>", "");
            changedXML = changedXML.replaceAll("(?s)<[^\\/<>]*?>[\n\r\t ]*?<\\/[^<>]*?>", "");

            if (!xml.equals(changedXML))
            {
                return cleanXML(changedXML);
            }
        }
        return xml;
    }

    public List<String> convertToList(String source, String delimiter)
    {
        if (source != null && !"".equals(source))
        {
            String[] sourceArray = source.split(delimiter, -1);
            return new LinkedList<>(Arrays.asList(sourceArray));
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
        form.setContainerId(containerId);
        form.setFolderId(folderId);

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
                            } else
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
     * @param owningGroupType
     * @param formName
     * @return
     */
    public Strings getOwningGroups(String owningGroupType, String formName)
    {
        if (owningGroupType != null)
        {

            String privilege = (String) getProperties().get(formName + "." + owningGroupType.replace(" ", "_") + ".privilege");

            try
            {
                List<String> rolesForPrivilege = getAcmPluginManager().getRolesForPrivilege(privilege);
                Map<String, List<String>> rolesToGroups = getFunctionalAccessService().getApplicationRolesToGroups();

                Map<String, String> groups = getGroups(rolesForPrivilege, rolesToGroups, 0, 1000, "name ASC", getAuthentication());

                return getGroupsList(groups);
            } catch (Exception e)
            {
                LOG.error("Cannot find groups with privilege = {}.", privilege, e);
            }
        }

        return null;
    }

    /**
     * This method will return all groups in list that need Frevvo
     *
     * @param groups
     * @return
     */
    private Strings getGroupsList(Map<String, String> groups)
    {
        Strings options = new Strings();

        if (groups != null)
        {
            groups.forEach((groupId, groupName) -> options.add(String.format("%1$s=%2$s", groupId, groupName)));
        }

        return options;
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
    private Map<String, String> getGroups(List<String> roles, Map<String, List<String>> rolesToGroups, int startRow, int maxRows,
            String sort, Authentication auth) throws MuleException
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

    public Map<String, String> getParticipantsPrivilegeTypes(List<String> participantTypes, String formName)
    {
        Map<String, String> retval = new HashMap<>();

        if (participantTypes != null)
        {
            for (String participantType : participantTypes)
            {
                String[] participantTypeArray = participantType.split("=");
                if (participantTypeArray != null && participantTypeArray.length == 2)
                {
                    String key = participantTypeArray[0];
                    String value = (String) getProperties().get(formName + "." + key.replace(" ", "_") + ".privilege.type");

                    retval.put(key, value);
                }
            }
        }

        return retval;
    }

    public String getAttachmentFileType(Object form)
    {
        return "attachment";
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
    public EcmFileDao getEcmFileDao()
    {
        return ecmFileDao;
    }

    /**
     * @param ecmFileDao the ecmFileDao to set
     */
    public void setEcmFileDao(EcmFileDao ecmFileDao)
    {
        this.ecmFileDao = ecmFileDao;
    }

    /**
     * @return the userActionExecutor
     */
    public AcmUserActionExecutor getUserActionExecutor()
    {
        return userActionExecutor;
    }

    /**
     * @param userActionExecutor the userActionExecutor to set
     */
    public void setUserActionExecutor(AcmUserActionExecutor userActionExecutor)
    {
        this.userActionExecutor = userActionExecutor;
    }

    public MuleContextManager getMuleContextManager()
    {
        return muleContextManager;
    }

    public void setMuleContextManager(MuleContextManager muleContextManager)
    {
        this.muleContextManager = muleContextManager;
    }

    public ObjectAssociationDao getObjectAssociationDao()
    {
        return objectAssociationDao;
    }

    public void setObjectAssociationDao(ObjectAssociationDao objectAssociationDao)
    {
        this.objectAssociationDao = objectAssociationDao;
    }

    public FunctionalAccessService getFunctionalAccessService()
    {
        return functionalAccessService;
    }

    public void setFunctionalAccessService(FunctionalAccessService functionalAccessService)
    {
        this.functionalAccessService = functionalAccessService;
    }

    public SearchResults getSearchResults()
    {
        return searchResults;
    }

    public void setSearchResults(SearchResults searchResults)
    {
        this.searchResults = searchResults;
    }

    public AcmPluginManager getAcmPluginManager()
    {
        return acmPluginManager;
    }

    public void setAcmPluginManager(AcmPluginManager acmPluginManager)
    {
        this.acmPluginManager = acmPluginManager;
    }

    public Gson getGson()
    {
        return gson;
    }

    public void setGson(Gson gson)
    {
        this.gson = gson;
    }

    public FolderAndFilesUtils getFolderAndFilesUtils()
    {
        return folderAndFilesUtils;
    }

    public void setFolderAndFilesUtils(FolderAndFilesUtils folderAndFilesUtils)
    {
        this.folderAndFilesUtils = folderAndFilesUtils;
    }

    public AcmFolderService getAcmFolderService()
    {
        return acmFolderService;
    }

    public void setAcmFolderService(AcmFolderService acmFolderService)
    {
        this.acmFolderService = acmFolderService;
    }

}
