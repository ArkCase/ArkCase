/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package org.mule.module.cmis;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.armedia.mule.cmis.kerberos.KerberosAuthenticationProvider;
import com.armedia.mule.cmis.kerberos.KerberosHttpInvoker;

import org.alfresco.cmis.client.AlfrescoDocument;
import org.apache.chemistry.opencmis.client.api.ChangeEvent;
import org.apache.chemistry.opencmis.client.api.ChangeEvents;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.FileableCmisObject;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.client.api.ObjectId;
import org.apache.chemistry.opencmis.client.api.ObjectType;
import org.apache.chemistry.opencmis.client.api.OperationContext;
import org.apache.chemistry.opencmis.client.api.Policy;
import org.apache.chemistry.opencmis.client.api.QueryResult;
import org.apache.chemistry.opencmis.client.api.Relationship;
import org.apache.chemistry.opencmis.client.api.Repository;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.runtime.ChangeEventsImpl;
import org.apache.chemistry.opencmis.client.runtime.ObjectIdImpl;
import org.apache.chemistry.opencmis.client.runtime.OperationContextImpl;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.data.Ace;
import org.apache.chemistry.opencmis.commons.data.Acl;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.data.RepositoryInfo;
import org.apache.chemistry.opencmis.commons.enums.AclPropagation;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.apache.chemistry.opencmis.commons.enums.UnfileObject;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;
import org.apache.chemistry.opencmis.commons.exceptions.CmisContentAlreadyExistsException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.ContentStreamImpl;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;
import org.mule.module.cmis.exception.CMISConnectorConnectionException;

/**
 * Implementation of {@link CMISFacade} that use Apache Chemistry Project.
 */
public class ChemistryCMISFacade implements CMISFacade 
{
	public static Logger log = Logger.getLogger(ChemistryCMISFacade.class);
	
	private static final String KERBEROS_USERNAME_PREFIX = "KERBEROS/";
	
    private Session session;
    private Map<String, String> connectionParameters;
    private String baseURL = null;

    public ChemistryCMISFacade(String username,
                               String password,
                               String repositoryId,
                               String baseURL,
                               boolean useAtomPub,
                               String connectionTimeout,
                               String useAlfrescoExtension,
                               String cxfPortProvider)
    {
    	this.baseURL = baseURL;
    	
        this.connectionParameters = 
        	paramMap(username, password, repositoryId, baseURL, useAtomPub,
                    connectionTimeout, useAlfrescoExtension, cxfPortProvider);
    } // End ChemistryCMISFacade Constructor

    
    public List<Repository> repositories() 
    {
        return SessionFactoryImpl.newInstance().getRepositories(connectionParameters);
    } // End repositories
    

    public RepositoryInfo repositoryInfo() 
    {
    	RepositoryInfo repoInfo = null;
    	
    	Session session = this.getSession ( this.connectionParameters );
    	if ( session != null )
    	{
    		repoInfo = session.getRepositoryInfo();
    	}
    	
        return repoInfo;
    } // End repositoryInfo
    

    public ChangeEvents changelog(String changeLogToken, boolean includeProperties) 
    {
        boolean hasMore = false;
        String token = changeLogToken;
        ChangeEvents returnEvents = null;
        
        Session session = this.getSession(this.connectionParameters);
        if ( session != null )
        {
	        List<ChangeEvent> changeEvents = new ArrayList<ChangeEvent>();
	        long totalNumItems = 0;
	        // follow the pages
	        do {
	            ChangeEvents events = session.getContentChanges(token, includeProperties, 50);
	            totalNumItems += events.getTotalNumItems();
	
	            changeEvents.addAll(events.getChangeEvents());
	            if (events.getHasMoreItems()) {
	                String t = events.getLatestChangeLogToken();
	                if (t != null && !t.equals(token)) {
	                    hasMore = true;
	                    token = t;
	                }
	            }
	        } while (hasMore);
	        
	        returnEvents = new ChangeEventsImpl(token, changeEvents, false, totalNumItems);
        }

        return returnEvents;
    } // End changelog
    

    public CmisObject getObjectById(String objectId) 
    {
    	CmisObject returnObj = null;
    	
    	Session session = this.getSession ( this.connectionParameters );
    	if ( session != null )
    	{
	    	returnObj = session.getObject(session.createObjectId(objectId), createOperationContext(null, null));
    	}
        
        return returnObj;
    } // End getObjectById
    

    public CmisObject getObjectByPath(String path) 
    {
    	CmisObject returnObj = null;
    	
    	Session session = this.getSession ( this.connectionParameters );
    	if ( session != null )
    	{
	        returnObj = session.getObjectByPath(path, createOperationContext(null, null));
    	}
        
        return returnObj;
    } // End getObjectByPath
    

    public ObjectId createDocumentById(String objectId,
                                       String filename,
                                       Object content,
                                       String mimeType,
                                       org.mule.module.cmis.VersioningState versioningState,
                                       String objectType,
                                       Map<String, String> properties)
    {
    	ObjectId returnId = null;
    	
    	if ( content == null )
    	{
    		log.error ( "No document content was specified in the payload." );
    		return null;
    	}
    	else if ( filename == null )
    	{
    		log.error ( "No filename was specified in the request." );
    		return null;
    	}
    	else if ( mimeType == null )
    	{
    		log.error ( "No file mime type was specified in the request." );
    		return null;
    	}
    	else if ( objectType == null )
    	{
    		log.error ( "No object type was specified in the request." );
    		return null;
    	}

    	Session session = this.getSession ( this.connectionParameters );
    	if ( session != null )
    	{
	        Validate.notEmpty(objectId, "objectId is empty");
	
        	log.debug ( 
		        "Preparing to create a document with file name \"" + filename + "\" in the folder with ID \"" + 
		        objectId + "\"." );
	        returnId =
	        	createDocument(
	                session.getObject(session.createObjectId(objectId)),
	                				  filename, 
	                				  content, 
	                				  mimeType, 
	                				  versioningState, 
	                				  objectType, 
	                				  properties);
	        log.debug ( "The ID of the repository node after document creation is \"" + returnId.getId() + "\"." );
    	}
    	
    	return returnId;
    } // End createDocumentById
    
    
    public ObjectId createDocumentByIdFromContent(String objectId,
										          String filename,
										          Object content,
										          String mimeType,
										          org.mule.module.cmis.VersioningState versioningState,
										          String objectType,
										          Map<String, String> properties) 
    {
    	ObjectId returnId = null;
    	
    	if ( content == null )
    	{
    		log.error ( "No document content was specified in the payload." );
    		return null;
    	}
    	else if ( filename == null )
    	{
    		log.error ( "No filename was specified in the request." );
    		return null;
    	}
    	else if ( mimeType == null )
    	{
    		log.error ( "No file mime type was specified in the request." );
    		return null;
    	}
    	else if ( objectType == null )
    	{
    		log.error ( "No object type was specified in the request." );
    		return null;
    	}

    	Session session = this.getSession ( this.connectionParameters );
    	if ( session != null )
    	{
			Validate.notEmpty(objectId, "objectId is empty");
			
			log.debug ( 
	        	"Preparing to create a document with file name \"" + filename + "\" in the folder with ID \"" + 
	        	objectId + "\"." );
			returnId = createDocument(session.getObject(session.createObjectId(objectId)),
									  filename, 
									  content, 
									  mimeType, 
									  versioningState, 
									  objectType, 
									  properties);
			log.debug ( "The ID of the repository node after document creation is \"" + returnId.getId() + "\"." );
    	}
    	
    	return returnId;
	} // End createDocumentByIdFromContent
    

    public ObjectId createDocumentByPath(String folderPath,
                                         String filename,
                                         Object content,
                                         String mimeType,
                                         org.mule.module.cmis.VersioningState versioningState,
                                         String objectType,
                                         Map<String, String> properties,
                                         boolean force) 
    {
    	ObjectId returnId = null;
    	
    	if ( content == null )
    	{
    		log.error ( "No document content was specified in the payload." );
    		return null;
    	}
    	else if ( filename == null )
    	{
    		log.error ( "No filename was specified in the request." );
    		return null;
    	}
    	else if ( mimeType == null )
    	{
    		log.error ( "No file mime type was specified in the request." );
    		return null;
    	}
    	else if ( objectType == null )
    	{
    		log.error ( "No object type was specified in the request." );
    		return null;
    	}

    	Session session = this.getSession ( this.connectionParameters );
    	if ( session != null )
    	{
	        Validate.notEmpty(folderPath, "folderPath is empty");
	        
        	log.debug ( 
        		"Preparing to create a document with file name \"" + filename + "\" in folder \"" + 
        	    folderPath + "\"." );
	        returnId = 
	        	createDocument(force ? getOrCreateFolderByPath(folderPath) : session.getObjectByPath(folderPath),
	        				   filename, 
	        				   content, 
	        				   mimeType, 
	        				   versioningState, 
	        				   objectType, 
	        				   properties);
	        log.debug ( "The ID of the repository node after document creation is \"" + returnId.getId() + "\"." );
    	}
    	
    	return returnId;
    } // End createDocumentByPath
    
    
    public ObjectId createDocumentByPathFromContent(String folderPath,
										            String filename,
										            Object content,
										            String mimeType,
										            org.mule.module.cmis.VersioningState versioningState,
										            String objectType,
										            Map<String, String> properties,
										            boolean force) 
    {
    	ObjectId returnId = null;
    	
    	if ( content == null )
    	{
    		log.error ( "No document content was specified in the payload." );
    		return null;
    	}
    	else if ( filename == null )
    	{
    		log.error ( "No filename was specified in the request." );
    		return null;
    	}
    	else if ( mimeType == null )
    	{
    		log.error ( "No file mime type was specified in the request." );
    		return null;
    	}
    	else if ( objectType == null )
    	{
    		log.error ( "No object type was specified in the request." );
    		return null;
    	}

    	Session session = this.getSession ( this.connectionParameters );
    	if ( session != null )
    	{
			Validate.notEmpty(folderPath, "folderPath is empty");
			
			log.debug ( 
	        	"Preparing to create a document with file name \"" + filename + "\" in folder \"" + 
	            folderPath + "\"." );
			returnId = 
				createDocument(force ? getOrCreateFolderByPath(folderPath) : session.getObjectByPath(folderPath),
							   filename, 
							   content, 
							   mimeType, 
							   versioningState, 
							   objectType, 
							   properties);
			log.debug ( "The ID of the repository node after document creation is \"" + returnId.getId() + "\"." );
    	}
    	
    	return returnId;
    } // End createDocumentByPathFromContent
    

    public CmisObject getOrCreateFolderByPath(String folderPath) 
    {
    	CmisObject returnObj = null;
    	Session session = this.getSession ( this.connectionParameters );
    	
    	if ( session != null )
    	{
    		Validate.notEmpty(folderPath, "folderPath is empty");
    		
	        try 
	        {
	        	returnObj = session.getObjectByPath(folderPath);
	        } 
	        catch (CmisObjectNotFoundException e) 
	        {
	            return createFolderStructure(folderPath);
	        }
    	}
        
        return returnObj;
    } // End getOrCreateFolderByPath

    
    /**
     * For each folder in the given folder path, creates it if necessary.
     * Notice: this implementation checks that the folder exists, and if not creates it.
     * This is not efficient, it would be better to try to just try to create it
     * and catch {@link CmisContentAlreadyExistsException}, but currently that exception
     * is not being thrown - it seems like a server's bug
     */
    private CmisObject createFolderStructure(String folderPath) 
    {
        String[] folderNames = StringUtils.split(folderPath, "/");
        String currentObjectId = getObjectByPath("/").getId();
        String currentPath = "/";
        for (String folder : folderNames) {
            currentPath = currentPath + folder + "/";
            
            CmisObject currentObject = null;
            
            try 
            {
            	currentObject = getObjectByPath(currentPath);
            }
            catch (CmisObjectNotFoundException ex) 
            {
            	log.debug("Path not found: " + currentPath);
            }
            
            currentObjectId = currentObject != null
                    ? currentObject.getId()
                    : createFolder(folder, currentObjectId).getId();
        }
        return getObjectById(currentObjectId);
    }

    /**
     * create a document
     */
    protected ObjectId createDocument(
            CmisObject folder,
            String filename,
            Object content,
            String mimeType,
            org.mule.module.cmis.VersioningState versioningState,
            String objectType,
            Map<String, String> extraProperties) 
    {
    	ObjectId returnId = null;
    	
    	Session session = this.getSession ( this.connectionParameters );
    	
    	if ( session != null )
    	{
	        Validate.notNull(folder, "folder is null");
	        Validate.notEmpty(filename, "filename is empty");
	        Validate.notNull(content, "content is null");
	        Validate.notEmpty(mimeType, "did you mean application/octet-stream?");
	        Validate.notNull(versioningState, "versionState is null");
	        VersioningState vs = null;
	        try 
	        {
	            vs = VersioningState.valueOf(versioningState.name());
	        } 
	        catch (IllegalArgumentException e) 
	        {
	            throw new IllegalArgumentException(String.format(
	                    "Illegal value for versioningState. Given `%s' could be: ",
	                    versioningState, Arrays.toString(VersioningState.values())), e);
	        }
	
	        Map<String, Object> properties = new HashMap<String, Object>();
	        properties.put(PropertyIds.OBJECT_TYPE_ID, objectType);
	        properties.put(PropertyIds.NAME, filename);
	        if (extraProperties != null) 
	        {
	            properties.putAll( this.translateInboundProperties ( extraProperties ) );
	        }
	        returnId = session.createDocument(properties,
	                session.createObjectId(folder.getId()),
	                createContentStream(filename, mimeType, content), vs);
    	}
    	
    	return returnId;
    } // End createDocument

    
    public static ContentStream createContentStream(String filename,
                                                    String mimeType,
                                                    Object content) {
        ContentStreamImpl ret;

        if (content instanceof String) {
            ret = new ContentStreamImpl(filename, mimeType, (String) content);
        } else {
			ret = new ContentStreamImpl();
			ret.setFileName(filename);
			ret.setMimeType(mimeType);
			if (content instanceof InputStream) {
				ret.setStream((InputStream) content);
			} else if (content instanceof byte[]) {
				ret.setStream(new ByteArrayInputStream((byte[]) content));
			} else if (content instanceof Document) {
				ret = (ContentStreamImpl) ((Document) content).getContentStream();
			} else {
				throw new IllegalArgumentException(
						"The content must be one of the following: Document, InputStream or Byte array. The received type is not a valid one for generating a content stream: "
								+ content.getClass());
			}
        }

        return ret;
    }

    
    public ObjectId createFolder(String folderName, String parentObjectId) 
    {
    	ObjectId returnId = null;
	
		Session session = this.getSession ( this.connectionParameters );
		
		if ( session != null )
		{
	        Map<String, Object> properties = new HashMap<String, Object>();
	        properties.put(PropertyIds.NAME, folderName);
	        properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:folder");
	        try 
	        {
	        	returnId = session.createFolder(properties, session.getObject(
	                    session.createObjectId(parentObjectId)));
	        } 
	        catch (CmisContentAlreadyExistsException e) 
	        {
	            CmisObject object = session.getObject(session.createObjectId(parentObjectId));
	            if (!(object instanceof Folder)) 
	            {
	                throw new IllegalArgumentException(parentObjectId + " is not a folder");
	            }
	            Folder folder = (Folder) object;
	            for (CmisObject o : folder.getChildren()) {
	                if (o.getName().equals(folderName)) {
	                    return session.createObjectId(o.getId());
	                }
	            }
	        }
		}
		
		return returnId;
    } // End createFolder
    

    public ObjectType getTypeDefinition(String typeId) 
    {
    	ObjectType returnTypeDef = null;
    	
    	Session session = this.getSession ( this.connectionParameters );
		
		if ( session != null )
		{
	        Validate.notEmpty(typeId, "typeId is empty");
	        returnTypeDef = session.getTypeDefinition(typeId);
		}
        
        return returnTypeDef;
    } // End getTypeDefinition
    

    public ItemIterable<Document> getCheckoutDocs(String filter,
                                                  String orderBy) 
    {
    	ItemIterable<Document> docList = null;
    	
    	Session session = this.getSession ( this.connectionParameters );
		
		if ( session != null )
		{
			docList = session.getCheckedOutDocs(createOperationContext(filter, orderBy));
		}
		
		return docList;
    } // End getCheckoutDocs

    
    public ItemIterable<QueryResult> query(String statement,
                                           Boolean searchAllVersions, 
                                           String filter,
                                           String orderBy) 
    {
    	ItemIterable<QueryResult> resultList = null;
    	
    	Session session = this.getSession ( this.connectionParameters );
		
		if ( session != null )
		{
	        Validate.notEmpty(statement, "statement is empty");
	        Validate.notNull(searchAllVersions, "searchAllVersions is empty");
	        
        	log.debug ( "Preparing to execute the CMIS query \"" + statement + "\".");
	        OperationContext ctx = createOperationContext(filter, orderBy);
	        resultList = session.query(statement, searchAllVersions, ctx);
	        log.debug ( "The result list contains " + resultList.getTotalNumItems() + " items.");
	        
	        for ( QueryResult currentResult : resultList )
	        {
	        	log.debug ( 
	        		"Object with ID \"" + currentResult.getPropertyByQueryName ( "cmis:objectId" ) + 
	        		"\" is in the result set." );
	        }
		}
		
		return resultList;
    } // End query
    

    public List<Folder> getParentFolders(CmisObject cmisObject, String objectId) 
    {
        validateObjectOrId(cmisObject, objectId);
        validateRedundantIdentifier(cmisObject, objectId);
        CmisObject target = getCmisObject(cmisObject, objectId);

        if (target != null && target instanceof FileableCmisObject) 
        {
            return ((FileableCmisObject) target).getParents();
        }
        else
        {
        	log.error ( "Unable to obtain the object reference, so no parent references could be obtained." );
        }
        return null;
    } // End getParentFolders

    
    public Object folder(Folder folder, String folderId,
                         NavigationOptions get, Integer depth,
                         String filter, String orderBy) {
        validateObjectOrId(folder, folderId);
        validateRedundantIdentifier(folder, folderId);

        Folder target = getCmisObject(folder, folderId, Folder.class);
        Object ret = null;

        if (target != null) 
        {
            if (get.equals(NavigationOptions.DESCENDANTS) || get.equals(NavigationOptions.TREE)) {
                Validate.notNull(depth, "depth is null");
            }

            if (get.equals(NavigationOptions.PARENT)) {
                ret = target.getFolderParent();
            } else {
                OperationContext ctx = createOperationContext(filter, orderBy);
                if (get.equals(NavigationOptions.CHILDREN)) {
                    ret = target.getChildren(ctx);
                } else if (get.equals(NavigationOptions.DESCENDANTS)) {
                    ret = target.getDescendants(depth, ctx);
                } else if (get.equals(NavigationOptions.TREE)) {
                    ret = target.getFolderTree(depth, ctx);
                }
            }
        }

        return ret;
    }

    public ContentStream getContentStream(CmisObject cmisObject, String objectId) {
        validateObjectOrId(cmisObject, objectId);
        validateRedundantIdentifier(cmisObject, objectId);

        CmisObject target = getCmisObject(cmisObject, objectId);

        if (target != null && target instanceof Document) 
        {
            return ((Document) target).getContentStream();
        }
        else
        {
        	log.error ( "Unable to obtain the object reference in order to obtain the content of the object." );
        }
        return null;
    }

    public FileableCmisObject moveObject(FileableCmisObject cmisObject,
                                         String objectId,
                                         String sourceFolderId,
                                         String targetFolderId) {
        validateObjectOrId(cmisObject, objectId);
        validateRedundantIdentifier(cmisObject, objectId);
        Validate.notEmpty(sourceFolderId, "sourceFolderId is empty");
        Validate.notEmpty(targetFolderId, "targetFolderId is empty");

        FileableCmisObject target = getCmisObject(cmisObject, objectId, FileableCmisObject.class);
        if (target != null) 
        {
            return target.move(new ObjectIdImpl(sourceFolderId), new ObjectIdImpl(targetFolderId));
        }
        else
        {
        	log.error ( "Unable to obtain the object reference in order to perform the object move." );
        }
        return null;
    }

    
    public CmisObject updateObjectProperties(CmisObject cmisObject,
                                             String objectId,
                                             Map<String, String> properties) 
    {
    	CmisObject returnObj = null;
    	
        validateObjectOrId(cmisObject, objectId);
        validateRedundantIdentifier(cmisObject, objectId);
        Validate.notNull(properties, "properties is null");
        
        CmisObject target = getCmisObject(cmisObject, objectId);
        if (target != null) 
        {
        	AlfrescoDocument alfDocument = ( AlfrescoDocument ) target;
        	returnObj = alfDocument.updateProperties ( this.translateInboundProperties ( properties ) );
        }
        else
        {
        	log.error ( "Unable to obtain the object reference in order to update the properties of the object." );
        }
        return returnObj;
    } // End updateObjectProperties
    

    public void delete(CmisObject cmisObject, String objectId, boolean allVersions) {
        validateObjectOrId(cmisObject, objectId);
        validateRedundantIdentifier(cmisObject, objectId);

        CmisObject target = getCmisObject(cmisObject, objectId);
        if (target != null) {
            target.delete(allVersions);
        }
    }

    public List<String> deleteTree(CmisObject folder, String folderId,
                                   boolean allversions, UnfileObject unfile, boolean continueOnFailure) {
        validateObjectOrId(folder, folderId);
        validateRedundantIdentifier(folder, folderId);
        CmisObject target = getCmisObject(folder, folderId);
        if (target != null && target instanceof Folder) {
            return ((Folder) target).deleteTree(allversions, unfile, continueOnFailure);
        }
        return null;
    }

    public List<Relationship> getObjectRelationships(CmisObject cmisObject,
                                                     String objectId) {
        validateObjectOrId(cmisObject, objectId);
        validateRedundantIdentifier(cmisObject, objectId);
        CmisObject target = getCmisObject(cmisObject, objectId);
        if (target != null) {
            return target.getRelationships();
        }
        return null;
    }

    public Acl getAcl(CmisObject cmisObject, String objectId) {
        validateObjectOrId(cmisObject, objectId);
        validateRedundantIdentifier(cmisObject, objectId);
        CmisObject target = getCmisObject(cmisObject, objectId);
        if (target != null) {
            return target.getAcl();
        }
        return null;
    }

    public List<Document> getAllVersions(CmisObject document, String documentId,
                                         String filter, String orderBy) {
        validateObjectOrId(document, documentId);
        validateRedundantIdentifier(document, documentId);
        CmisObject target = getCmisObject(document, documentId);

        if (target instanceof Document) {
            OperationContext ctx = createOperationContext(filter, orderBy);
            return ((Document) target).getAllVersions(ctx);
        }
        return null;
    }


    public ObjectId checkOut(CmisObject document, String documentId) {
        validateObjectOrId(document, documentId);
        validateRedundantIdentifier(document, documentId);
        CmisObject target = getCmisObject(document, documentId);

        if (target != null && target instanceof Document) {
            return ((Document) target).checkOut();
        }
        return null;
    }


    public void cancelCheckOut(CmisObject document, String documentId) {
        validateObjectOrId(document, documentId);
        validateRedundantIdentifier(document, documentId);
        CmisObject target = getCmisObject(document, documentId);
        if (target != null && target instanceof Document) {
            ((Document) target).cancelCheckOut();
        }
    }

    public ObjectId checkIn(CmisObject document, String documentId,
                            Object content, String filename,
                            String mimeType, boolean major,
                            String checkinComment,
                            Map<String, String> properties) {
        validateObjectOrId(document, documentId);
        validateRedundantIdentifier(document, documentId);
        Validate.notEmpty(filename, "filename is empty");
        Validate.notNull(content, "content is null");
        Validate.notEmpty(mimeType, "did you mean application/octet-stream?");
        Validate.notEmpty(checkinComment, "checkinComment is empty");

        CmisObject target = getCmisObject(document, documentId);
        if (target != null && target instanceof Document) {
            Document doc = (Document) target;
            return doc.checkIn(major, coalesceProperties(properties),
                    createContentStream(filename, mimeType, content),
                    checkinComment);
        }
        return null;
    }

    private Map<String, String> coalesceProperties(Map<String, String> properties) {
        return properties != null ? properties : Collections.<String, String>emptyMap();
    }


    public Acl applyAcl(CmisObject cmisObject, String objectId, List<Ace> addAces,
                        List<Ace> removeAces, AclPropagation aclPropagation) {
        validateObjectOrId(cmisObject, objectId);
        validateRedundantIdentifier(cmisObject, objectId);
        CmisObject target = getCmisObject(cmisObject, objectId);
        if (target != null) {
            return target.applyAcl(addAces, removeAces, aclPropagation);
        }
        return null;
    }

    public List<Policy> getAppliedPolicies(CmisObject cmisObject, String objectId) {
        validateObjectOrId(cmisObject, objectId);
        validateRedundantIdentifier(cmisObject, objectId);
        CmisObject target = getCmisObject(cmisObject, objectId);
        if (target != null) {
            return target.getPolicies();
        }
        return null;
    }

    public void applyPolicy(CmisObject cmisObject, String objectId, List<ObjectId> policyIds) {
        validateObjectOrId(cmisObject, objectId);
        validateRedundantIdentifier(cmisObject, objectId);
        Validate.notNull(policyIds);
        CmisObject target = getCmisObject(cmisObject, objectId);
        if (target != null) {
            target.applyPolicy(policyIds.toArray(new ObjectId[policyIds.size()]));
        }
    }
    
    public void applyAspect ( String objectId,
						      String aspectName,
						      Map<String, String> properties ) 
    {
		validateObjectOrId(null, objectId);
		
		CmisObject target = getCmisObject ( null, objectId );
		AlfrescoDocument alfDocument = ( AlfrescoDocument ) target;
		if ( ( alfDocument != null ) && ( !alfDocument.hasAspect ( "P:" + aspectName ) ) ) 
		{
			alfDocument.addAspect ( "P:" + aspectName );
			if ( properties != null )
			{
				alfDocument.updateProperties ( this.translateInboundProperties ( properties ) );
			}
		}
    } // End applyAspect
    
    
    public ObjectId createRelationship ( String parentObjectId, 
						             String childObjectId, 
						             String relationshipType)
    {
    	if (StringUtils.isEmpty(parentObjectId)) {
    		log.error("No value was specified for the required attribute \"parentObjectId\". No relationship could be created.");
    		return null;
    	}
    	
    	if (StringUtils.isEmpty(childObjectId)) {
    		log.error("No value was specified for the required attribute \"childObjectId\". No relationship could be created.");
        	return null;
    	}
    	
    	if (StringUtils.isEmpty(relationshipType)) {
    		log.error("No value was specified for the required attribute \"relationshipType\". No relationship could be created." );
        	return null;
    	}
    	
    	// Get a handle to the session object.
    	Session session = this.getSession ( this.connectionParameters );
		
		if ( session == null ) {
			log.error("Unable to obtain a repository session, so no relationship could be created.");
			return null;
		}
		
		// We were able to obtain a session handle. Make sure that the source and target objects exists.
		try
		{
			CmisObject parentObj = this.getObjectById ( parentObjectId );
			
			if ( parentObj == null )
			{
				log.error ("The parent object with ID \"" + parentObjectId + "\" doesn't exists in the repository. No relationship will be created." );
				return null;
			}
		}
		catch ( Exception objEx )
		{
			log.error("An error occurred while attempting to determine if an the parent object with ID \"" + parentObjectId + "\" exists in the repository. " + objEx.getMessage () );
			return null;
		}
		
		try
		{
			CmisObject childObj = this.getObjectById ( childObjectId );
			
			if ( childObj == null )
			{
				log.error("The child object with ID \"" + childObjectId + "\" doesn't exists in the repository. No relationship will be created." );
				return null;
			}
		}
		catch ( Exception objEx )
		{
			log.error ("An error occurred while attempting to determine if an the child object with ID \"" + childObjectId + "\" exists in the repository. " + objEx.getMessage () );
			return null;
		}
		
		// Set-up the paramters in preparation for the "createRelationship" call.
		Map<String, Serializable> relProps = new HashMap<String, Serializable>();
		relProps.put("cmis:sourceId", parentObjectId);
		relProps.put("cmis:targetId", childObjectId);
		relProps.put("cmis:objectTypeId", "R:" + relationshipType);
		
		try
		{
			return session.createRelationship(relProps, null, null, null);
		}
		catch ( Exception relEx )
		{
			log.error ("An error occurred while attempting to create a relationship between the parent object with ID \"" + parentObjectId + "\" and the child object with ID \"" + childObjectId + "\". " + relEx.getMessage () );
			return null;
		}
    } 


    /**
     * Validates that either a CmisObject or it's ID has been provided.
     */
    private static void validateObjectOrId(CmisObject object, String objectId) {
        if (object == null && StringUtils.isBlank(objectId)) {
            throw new IllegalArgumentException("Both the cmis object and it's ID are not set");
        }
    }

    /**
     * Validates that and object's ID is the one provided, in case both are not null or blank.
     */
    private static void validateRedundantIdentifier(CmisObject object, String objectId) {
        if (object != null && StringUtils.isNotBlank(objectId) && !object.getId().equals(objectId)) {
            throw new IllegalArgumentException("The id provided does not match the object's ID");
        }
    }

    private CmisObject getCmisObject(CmisObject object, String objectId) {
        return getCmisObject(object, objectId, CmisObject.class);
    }

    /**
     * Returns the object if it is not null. Otherwise, get the object by ID and
     * returns it if types match. Returns null if types don't match.
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    private <T> T getCmisObject(T object, String objectId, Class<T> clazz) 
    {
        if (object != null) 
        {
            return object;
        } 
        else 
        {
            CmisObject obj = getObjectById(objectId);
            if ((obj != null) && clazz.isAssignableFrom(obj.getClass())) 
            {
                return (T) obj;
            }
            return null;
        }
    }


    private static OperationContext createOperationContext(String filter,
                                                           String orderBy) {
        OperationContext ctx = new OperationContextImpl();
        ctx.setIncludeAcls(true);
        ctx.setIncludePolicies(true);
        if (StringUtils.isNotBlank(filter) || StringUtils.isNotBlank(orderBy)) {
            if (StringUtils.isNotBlank(filter)) {
                ctx.setFilterString(filter);
            }
            if (StringUtils.isNotBlank(orderBy)) {
                ctx.setOrderBy(orderBy);
            }
        }
        return ctx;
    }

    private static Map<String, String> paramMap(String username,
                                                String password,
                                                String repositoryId,
                                                String baseURL,
                                                boolean useAtomPub,
                                                String connectionTimeout,
                                                String useAlfrescoExtension,
                                                String cxfPortProvider)
    {
    	if ( ( username == null ) || (username.trim().length() <= 0 ) )
    	{
    		log.error ( 
    			"The \"username\" attribute of the \"config\" element for the repository connector configuration is " +
    		    "empty or missing. This configuration is required in order to provide repository connection " +
    			"parameters to the connector. The connector is currently non-functional." );
    		return null;
    	}
    	else if ( ( password == null ) || (password.trim().length() <= 0 ) )
    	{
    		log.error ( 
    			"The \"password\" attribute of the \"config\" element for the repository connector configuration is " +
    		    "empty or missing. This configuration is required in order to provide repository connection " +
    			"parameters to the connector. The connector is currently non-functional." );
    		return null;
    	}
    	else if ( ( baseURL == null ) || (baseURL.trim().length() <= 0 ) )
    	{
    		log.error ( 
    			"The \"baseURL\" attribute of the \"config\" element for the repository connector configuration is " +
    		    "empty or missing. This configuration is required in order to provide repository connection " +
    			"parameters to the connector. The connector is currently non-functional." );
    		return null;
    	}

        Map<String, String> parameters = new HashMap<String, String>();

        // user credentials
        if (username.trim().startsWith(KERBEROS_USERNAME_PREFIX))
        {
            parameters.put(SessionParameter.USER, username.trim().substring(KERBEROS_USERNAME_PREFIX.length()));
            parameters.put(SessionParameter.HTTP_INVOKER_CLASS, KerberosHttpInvoker.class.getName());
            parameters.put(SessionParameter.AUTHENTICATION_PROVIDER_CLASS, KerberosAuthenticationProvider.class.getName());
        }
        else
        {
            parameters.put(SessionParameter.USER, username.trim());
        }

        parameters.put(SessionParameter.PASSWORD, password.trim());

        // connection settings... we prefer SOAP over ATOMPUB because some rare
        // behaviurs with the ChangeEvents.getLatestChangeLogToken().
        if (!useAtomPub) 
        {
            parameters.put(SessionParameter.BINDING_TYPE, BindingType.WEBSERVICES.value());
            parameters.put(SessionParameter.WEBSERVICES_ACL_SERVICE, baseURL + "ACLService?wsdl");
            parameters.put(SessionParameter.WEBSERVICES_DISCOVERY_SERVICE, baseURL + "DiscoveryService?wsdl");
            parameters.put(SessionParameter.WEBSERVICES_MULTIFILING_SERVICE, baseURL + "MultiFilingService?wsdl");
            parameters.put(SessionParameter.WEBSERVICES_NAVIGATION_SERVICE, baseURL + "NavigationService?wsdl");
            parameters.put(SessionParameter.WEBSERVICES_OBJECT_SERVICE, baseURL + "ObjectService?wsdl");
            parameters.put(SessionParameter.WEBSERVICES_POLICY_SERVICE, baseURL + "PolicyService?wsdl");
            parameters.put(SessionParameter.WEBSERVICES_RELATIONSHIP_SERVICE, baseURL + "RelationshipService?wsdl");
            parameters.put(SessionParameter.WEBSERVICES_REPOSITORY_SERVICE, baseURL + "RepositoryService?wsdl");
            parameters.put(SessionParameter.WEBSERVICES_VERSIONING_SERVICE, baseURL + "VersioningService?wsdl");
            parameters.put(SessionParameter.WEBSERVICES_PORT_PROVIDER_CLASS, cxfPortProvider);
        }
        else 
        {
            parameters.put(SessionParameter.BINDING_TYPE, BindingType.ATOMPUB.value());
            parameters.put(SessionParameter.ATOMPUB_URL, baseURL.trim());
        }
        
        // Session locale
        parameters.put(SessionParameter.LOCALE_ISO3166_COUNTRY, "");
        parameters.put(SessionParameter.LOCALE_ISO639_LANGUAGE, "en");
        
        if ( connectionTimeout != null )
        {
        	parameters.put(SessionParameter.CONNECT_TIMEOUT, connectionTimeout);
        }

        if ( repositoryId != null )
        {
        	parameters.put(SessionParameter.REPOSITORY_ID, repositoryId.trim());
        }
        else
        {
        	// No repository ID was specified. Go try an get the first ID in the repository list from the server.
        	String repoID = getRepositoryID ( parameters, baseURL );
        	if ( repoID != null )
        	{
        		parameters.put(SessionParameter.REPOSITORY_ID, repoID);
        	}
        }

        // Determine if the use of the Alfresco extension for OpenCMIS was requested in the configuration.
        if ( Boolean.parseBoolean ( useAlfrescoExtension.trim().toLowerCase () ) )
        {
        	// Have the Alfresco Extended CMIS factory used.
        	parameters.put(SessionParameter.OBJECT_FACTORY_CLASS, "org.alfresco.cmis.client.impl.AlfrescoObjectFactoryImpl");
        	log.debug ( "The Alfresco Object Factor CMIS extension has been included in the session parameters." );
        }

        return parameters;
    }
    
    
    public static String getRepositoryID ( Map<String, String> parameters, String baseURL )
    {
    	String repoID = null;
    	
    	try
    	{
    		log.debug ( "Attempting to dynamically obtain the repository ID." );
    		List<Repository> repositoryList = SessionFactoryImpl.newInstance().getRepositories(parameters);
    		
    		if ( repositoryList.size() <= 0 )
    		{
    			log.error ( 
                	"No repositories were returned at the CMIS server URL \"" + baseURL + "\". " +
                	"The connector is currently non-functional." );
    		}
    		else
    		{
    			// Get the first repo in the list.
    			Repository firstRepo = repositoryList.get ( 0 );
    			
    			// Extract the ID of this repo, and add it to the parameters list.
    			repoID = firstRepo.getId();
    			
    			log.debug ( "The repository ID that will be used is " + repoID + "." );
    		}
    	}
    	catch ( Exception repoIDEx )
    	{
    		log.error ( 
        		"An error occurred while attempting to dynamically obtain a repository ID. " +
        		"The connector is currently non-functional. " + repoIDEx.getMessage() );
    	}
    	
    	return repoID;
    } // end getRepositoryID

    
    private Session getSession(Map<String, String> parameters) 
    {
    	Session repoSession = this.session;
    	
    	if ( parameters == null )
    	{
    		throw new CMISConnectorConnectionException("Repository sessions cannot be obtained through the connector because the connector configuration " +
                    "is missing or incorrectly specified in the mule application configuration file.");
    	}
    	else if ( parameters.get(SessionParameter.REPOSITORY_ID) == null )
    	{
    		// There must have been a problem dynamically obtaining the repository ID.  
    		// Try again.
    		String repoID = getRepositoryID ( parameters, this.baseURL );
    		
    		if ( repoID != null )
    		{
    			parameters.put(SessionParameter.REPOSITORY_ID, repoID);
    		}
    		else
    		{
                throw new CMISConnectorConnectionException("Repository sessions cannot be obtained through the connector because the repository ID is missing " +
                        "from the connector configuration.");
    		}
        	return null;
    	}
    	
    	if ( repoSession == null )
    	{
	        Validate.notNull(parameters);
	        try
	        {
	        	repoSession = SessionFactoryImpl.newInstance().createSession ( parameters );
	        	this.session = repoSession;
	        }
	        catch ( Exception sessionEx )
	        {
                throw new CMISConnectorConnectionException("An error occurred while attempting to obtain a new repository session",
                        sessionEx);
	        }
    	}
    	
    	return repoSession;
    } // End getSession
    
    
    //******************************************************************************
    // Method: translateInboundProperties
    // Description: Translates an inbound set of String based properties to Object 
    //   properties. In situations where a multi-value property is signified
    //   by an "M:" prefix to the property name, then the property value is assumed to be
    //   a list of values and is converted to an Array object this added to the output map.
    //******************************************************************************
    private Map<String,Object> translateInboundProperties ( Map<String,String> inboundProperties )
    {
    	Map<String,Object> returnMap = new HashMap<String,Object> ();
    	
    	if ( inboundProperties == null )
    	{
    		returnMap = null;
    	}
    	else if ( inboundProperties.size() > 0 )
    	{
    		Iterator<String> keySetItr = inboundProperties.keySet().iterator();
    		while ( keySetItr.hasNext() )
    		{
    			String currentKey = keySetItr.next ();
    			String currentVal = inboundProperties.get ( currentKey );
    			
    			// Don't waste our time with empty properties.
    			if ( currentVal != null )
    			{
	    			// Determine if this is a multi-valued property.
	    			if ( currentKey.toLowerCase().startsWith("m:") )
	    			{
	    				// This is a multi-valued property. Each value is separated by a ','.
	    				String [] valArray = currentVal.split ( "," );
	    				
	    				ArrayList<String> propArray = new ArrayList<String> ( Arrays.asList ( valArray ) );
	    				returnMap.put ( currentKey.substring(2), propArray );
	    			}
	    			else
	    			{
	    				// Just add the property into the return list as is.
	    				returnMap.put ( currentKey, currentVal );
	    			}
    			}
    		}
    	}
    	
    	return returnMap;
    } // End translateInboundProperties
}
