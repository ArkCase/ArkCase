/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package org.mule.module.cmis;

import java.util.List;
import java.util.Map;

import org.apache.chemistry.opencmis.client.api.ChangeEvents;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.FileableCmisObject;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.client.api.ObjectId;
import org.apache.chemistry.opencmis.client.api.ObjectType;
import org.apache.chemistry.opencmis.client.api.Policy;
import org.apache.chemistry.opencmis.client.api.QueryResult;
import org.apache.chemistry.opencmis.client.api.Relationship;
import org.apache.chemistry.opencmis.client.api.Repository;
import org.apache.chemistry.opencmis.commons.data.Ace;
import org.apache.chemistry.opencmis.commons.data.Acl;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.data.RepositoryInfo;
import org.apache.chemistry.opencmis.commons.enums.AclPropagation;
import org.apache.chemistry.opencmis.commons.enums.UnfileObject;
import org.mule.api.ConnectionException;
import org.mule.api.annotations.Connect;
import org.mule.api.annotations.ConnectionIdentifier;
import org.mule.api.annotations.ConnectivityTesting;
import org.mule.api.annotations.Connector;
import org.mule.api.annotations.Disconnect;
import org.mule.api.annotations.InvalidateConnectionOn;
import org.mule.api.annotations.MetaDataSwitch;
import org.mule.api.annotations.Processor;
import org.mule.api.annotations.ValidateConnection;
import org.mule.api.annotations.display.Password;
import org.mule.api.annotations.display.Placement;
import org.mule.api.annotations.param.ConnectionKey;
import org.mule.api.annotations.param.Default;
import org.mule.api.annotations.param.Optional;
import org.mule.module.cmis.exception.CMISConnectorConnectionException;

/**
 * CMIS (Content Management Interoperability Services) is a standard for improving interoperability between ECM systems.
 *
 * @author MuleSoft, Inc.
 */
@Connector(name = "cmis", schemaVersion = "1.1", friendlyName = "CMIS", minMuleVersion = "3.4",
        metaData = MetaDataSwitch.ON, connectivityTesting = ConnectivityTesting.ON)
public class CMISCloudConnector implements CMISFacade {

    /**
     * Reference to a CMISFacade implementation in case you want to
     * use another implementation or initialize the default in a
     * diferent way. Using this option make useless the other
     * attributes.
     */
    private CMISFacade facade;

    private String connectionIdentifier;
    
    // This object will be used to hold the concurrency for the connection manager features
    private final Object threadSafeLock;
    
    public CMISCloudConnector() {
    	threadSafeLock = new Object();
    }

    /**
     * Connects to CMIS
     *
     * @param username CMIS username
     * @param password CMIS password
     * @param repositoryId The identifier for the Repository that this connector instance works with
     * @param baseUrl URL base for the SOAP connector
     * @param endpoint The type of endpoint. Values: SOAP or ATOMPUB
     * @param connectionTimeout The connection time-out specification
     * @param useAlfrescoExtension Specifies whether the Alfresco Object Factory implementation should be utilized, the
     * Alfresco CMIS extension JAR must be included in your Mule application in order for
     * this configuration to work.
     * @param cxfPortProvider Specifies CXF port provider, the CMIS connector includes a default implementation
     * but allows to set a custom implementation by extending org.apache.chemistry.opencmis.client.bindings.spi.webservices.AbstractPortProvider
     */
    @Connect
    public void connect(@Placement(group = "Authentication") @ConnectionKey String username,
                        @Placement(group = "Authentication") @Password String password,
                        @Placement(group = "Repository Information") @ConnectionKey String baseUrl,
                        @Placement(group = "Repository Information") String repositoryId,
                        @Placement(group = "Repository Information") @Optional @Default("ATOM") String endpoint,
                        @Optional @Default("10000") String connectionTimeout,
                        @Optional @Default("false") String useAlfrescoExtension,
                        @Optional @Default("org.apache.chemistry.opencmis.client.bindings.spi.webservices.CXFPortProvider") String cxfPortProvider) throws ConnectionException {
    	
    	synchronized (threadSafeLock) {
    		// Prevent re-initialization
    		if (facade == null) {
		        boolean useAtomPub;
		        if (endpoint == null) {
		            useAtomPub = true;
		        } else if (CMISConnectionType.valueOf(endpoint) == CMISConnectionType.SOAP) {
		            useAtomPub = false;
		        } else if (CMISConnectionType.valueOf(endpoint) == CMISConnectionType.ATOM) {
		            useAtomPub = true;
		        } else {
		            throw new IllegalStateException("unknown endpoint type " + endpoint + ". Please use SOAP or ATOMPUB");
		        }
		
		        setConnectionIdentifier(username + "@" + baseUrl);
		
		        this.facade =
		            CMISFacadeAdaptor.adapt(
		                    new ChemistryCMISFacade(
		                            username,
		                            password,
		                            repositoryId,
		                            baseUrl,
		                            useAtomPub,
		                            connectionTimeout,
		                            useAlfrescoExtension,
		                            cxfPortProvider));
		        
		        // Force a call to an operation in order to create the client and force authentication
		        repositoryInfo();
    		}
    	}
    }

    @Disconnect
    public void disconnect() {
    	synchronized (threadSafeLock) {
    		facade = null;
    	}
    }

    @ValidateConnection
    public boolean isConnected() {
    	synchronized (threadSafeLock) {
    		return facade != null;
		}
    }

    @ConnectionIdentifier
    public String getConnectionIdentifier() {
        return this.connectionIdentifier;
    }

    /**
     * Returns all repositories that are available at the endpoint.
     * <p/>
     * {@sample.xml ../../../doc/cmis-connector.xml.sample cmis:repositories}
     *
     * @return a list of {@link Repository}.
     */
    @Override
    @Processor
    @InvalidateConnectionOn(exception = CMISConnectorConnectionException.class)
    public List<Repository> repositories() {
        return facade.repositories();
    }

    /**
     * Returns information about the CMIS repository, the optional capabilities it supports and its Access Control information if applicable.
     * <p/>
     * {@sample.xml ../../../doc/cmis-connector.xml.sample cmis:repositoryInfo}
     *
     * @return a {@link RepositoryInfo} instance
     */
    @Override
    @Processor
    @InvalidateConnectionOn(exception = CMISConnectorConnectionException.class)
    public RepositoryInfo repositoryInfo() {
        return facade.repositoryInfo();
    }

    /**
     * Gets repository changes.
     * <p/>
     * {@sample.xml ../../../doc/cmis-connector.xml.sample cmis:changelog}
     *
     * @param changeLogToken    The change log token to start from or {@code null}
     * @param includeProperties Indicates if changed properties should be included in
     *                          the result
     * @return a {@link ChangeEvents} instance
     */
    @Override
    @Processor
    @InvalidateConnectionOn(exception = CMISConnectorConnectionException.class)
    public ChangeEvents changelog(@Optional String changeLogToken,
                                  boolean includeProperties) {
        return facade.changelog(changeLogToken, includeProperties);
    }

    /**
     * Returns a CMIS object from the repository and puts it into the cache.
     * <p/>
     * {@sample.xml ../../../doc/cmis-connector.xml.sample cmis:getObjectById}
     *
     * @param objectId The object id
     *
     * @return a {@link CmisObject} instance
     */
    @Override
    @Processor
    @InvalidateConnectionOn(exception = CMISConnectorConnectionException.class)
    public CmisObject getObjectById(String objectId) {
        return facade.getObjectById(objectId);
    }

    /**
     * Returns a CMIS object from the repository and puts it into the cache.
     * <p/>
     * {@sample.xml ../../../doc/cmis-connector.xml.sample cmis:getObjectByPath}
     *
     * @param path Path of the object to retrieve
     * @return a {@link CmisObject} instance
     */
    @Override
    @Processor
    @InvalidateConnectionOn(exception = CMISConnectorConnectionException.class)
    public CmisObject getObjectByPath(String path) {
        return facade.getObjectByPath(path);
    }

    /**
     * Creates a new document in the repository where the content comes directly from the payload and
     * the target folder node is specified by a repository path.
     * * <p/>
     * {@sample.xml ../../../doc/cmis-connector.xml.sample cmis:createDocumentByPath}
     *
     * @param folderPath      Folder in the repository that will hold the document
     * @param filename        name of the file
     * @param content         file content as specified in the payload
     * @param mimeType        stream content-type
     * @param versioningState An enumeration specifying what the versioning state of the newly-created object MUST be. If the repository does not support versioning, the repository MUST ignore the versioningState parameter.  Valid values are:
     *                        o none:  The document MUST be created as a non-versionable document.
     *                        o checked-out: The document MUST be created in the checked-out state.
     *                        o major (default): The document MUST be created as a major version
     *                        o minor: The document MUST be created as a minor version.
     * @param objectType      the type of the object
     * @param properties      the properties optional document properties to set
     * @param force           if should folder structure must be created when there
     *                        are missing intermediate folders
     * @return the object id {@link ObjectId} of the created
     */
    @Override
    @Processor
    @InvalidateConnectionOn(exception = CMISConnectorConnectionException.class)
    public ObjectId createDocumentByPath(String folderPath,
                                         String filename,
                                         @Optional @Default("#[payload]")  Object content,
                                         String mimeType,
                                         VersioningState versioningState,
                                         String objectType,
                                         @Placement(group = "Properties") @Optional Map<String, String> properties,
                                         @Optional @Default("false") boolean force) {
        return facade.createDocumentByPath(folderPath, filename, content, mimeType, versioningState,
                objectType, properties, force);
    }
    
    
    /**
     * Creates a new document in the repository where the content is specified as the value of the "content"
     * parameter and the target folder node is specified by a repository path.
     * <p/>
     * {@sample.xml ../../../doc/cmis-connector.xml.sample cmis:createDocumentByPathFromContent}
     *
     * @param folderPath      Folder in the repository that will hold the document
     * @param filename        Name of the file
     * @param content         File content
     * @param mimeType        Stream content-type
     * @param versioningState An enumeration specifying what the versioning state of the newly-created object MUST be. If the repository does not support versioning, the repository MUST ignore the versioningState parameter.
     * @param objectType      The type of the object.
     * @param properties      the properties optional document properties to set
     * @param force           if should folder structure must be created when there
     *                        are missing intermediate folders
     * @return the {@link ObjectId} of the created
     */
    @Override
    @Processor
    @InvalidateConnectionOn(exception = CMISConnectorConnectionException.class)
    public ObjectId createDocumentByPathFromContent(String folderPath,
			                                        String filename,
			                                        @Optional @Default("#[payload]") Object content,
			                                        String mimeType,
			                                        VersioningState versioningState,
			                                        String objectType,
			                                        @Optional Map<String, String> properties,
			                                        @Optional @Default("false") boolean force) {
        return facade.createDocumentByPathFromContent(
        		folderPath, 
        		filename, 
        		content, 
        		mimeType, 
        		versioningState,
                objectType, 
                properties, 
                force);
    }

    /**
     * Creates a new folder in the repository if it doesn't already exist.
     *  <p/>
     * {@sample.xml ../../../doc/cmis-connector.xml.sample cmis:getOrCreateFolderByPath}
     * 
     * @param folderPath      Path to the folder
     * 
     * @return the {@link ObjectId} of the created
     */
    @Processor
    @InvalidateConnectionOn(exception = CMISConnectorConnectionException.class)
    public CmisObject getOrCreateFolderByPath(String folderPath) {
    	return facade.getOrCreateFolderByPath(folderPath);
    }
    
    /**
     * Creates a new document in the repository where the content comes directly from the payload and
     * the target folder node is specified by an object ID.
     *  <p/>
     * {@sample.xml ../../../doc/cmis-connector.xml.sample cmis:createDocumentById}
     *
     * @param folderId        Folder Object Id
     * @param filename        name of the file
     * @param content         file content as specified in the payload
     * @param mimeType        stream content-type
     * @param versioningState An enumeration specifying what the versioning state of the newly-created object MUST be. If the repository does not support versioning, the repository MUST ignore the versioningState parameter.  Valid values are:
     *                        o none:  The document MUST be created as a non-versionable document.
     *                        o checkedout: The document MUST be created in the checked-out state.
     *                        o major (default): The document MUST be created as a major version
     *                        o minor: The document MUST be created as a minor version.
     * @param objectType      the type of the object
     * @param properties      the properties optional document properties to set
     * @return the object id {@link ObjectId} of the created
     */
    @Override
    @Processor
    @InvalidateConnectionOn(exception = CMISConnectorConnectionException.class)
    public ObjectId createDocumentById(String folderId,
                                       String filename,
                                       @Optional @Default("#[payload]")  Object content,
                                       String mimeType,
                                       VersioningState versioningState,
                                       String objectType,
                                       @Optional @Default("") @Placement(group = "Properties") Map<String, String> properties) {
        return facade.createDocumentById(folderId, filename, content, mimeType, versioningState,
                objectType, properties);
    }
    
    /**
     * Creates a new document in the repository where the content comes directly from the payload and
     * the target folder node is specified by an object ID.
     * *  <p/>
     * {@sample.xml ../../../doc/cmis-connector.xml.sample cmis:createDocumentByIdFromContent}
     *
     * @param folderId        Folder Object Id
     * @param filename        name of the file
     * @param content         file content
     * @param mimeType        stream content-type
     * @param versioningState An enumeration specifying what the versioning state of the newly-created object MUST be. If the repository does not support versioning, the repository MUST ignore the versioningState parameter.  Valid values are:
     *                        o none:  The document MUST be created as a non-versionable document.
     *                        o checkedout: The document MUST be created in the checked-out state.
     *                        o major (default): The document MUST be created as a major version
     *                        o minor: The document MUST be created as a minor version.
     * @param objectType      the type of the object
     * @param properties      the properties optional document properties to set
     * @return the object id {@link ObjectId} of the created
     */
    @Override
    @Processor
    @InvalidateConnectionOn(exception = CMISConnectorConnectionException.class)
    public ObjectId createDocumentByIdFromContent(String folderId,
			                                      String filename,
			                                      @Optional @Default("#[payload]") Object content,
			                                      String mimeType,
			                                      VersioningState versioningState,
			                                      String objectType,
			                                      @Optional Map<String, String> properties) {
        return facade.createDocumentByIdFromContent(folderId, filename, content, mimeType, versioningState, objectType, properties);
    }

    /**
     * Creates a folder. Note that this is not recusive creation. You just create
     * one folder
     * <p/>
     * {@sample.xml ../../../doc/cmis-connector.xml.sample cmis:createFolder}
     *
     * @param folderName     Folder name (eg: "my documents")
     * @param parentObjectId Parent folder for the folder being created (eg: repository.rootFolder)
     * @return the {@link ObjectId} of the created
     */
    @Override
    @Processor
    @InvalidateConnectionOn(exception = CMISConnectorConnectionException.class)
    public ObjectId createFolder(String folderName,
                                 String parentObjectId) {
        return facade.createFolder(folderName, parentObjectId);
    }

    /**
     * Returns the type definition of the given type id.
     * <p/>
     * {@sample.xml ../../../doc/cmis-connector.xml.sample cmis:getTypeDefinition}
     *
     * @param typeId Object type Id
     * @return type of object {@link ObjectType}
     */
    @Override
    @Processor
    @InvalidateConnectionOn(exception = CMISConnectorConnectionException.class)
    public ObjectType getTypeDefinition(String typeId) {
        return facade.getTypeDefinition(typeId);
    }

    /**
     * Retrieve list of checked out documents.
     * <p/>
     * {@sample.xml ../../../doc/cmis-connector.xml.sample cmis:getCheckoutDocs}
     *
     * @param filter  comma-separated list of properties to filter
     * @param orderBy comma-separated list of query names and the ascending modifier
     *                "ASC" or the descending modifier "DESC" for each query name
     * @return list of {@link Document}.
     */
    @Override
    @Processor
    @InvalidateConnectionOn(exception = CMISConnectorConnectionException.class)
    public ItemIterable<Document> getCheckoutDocs(@Optional String filter, @Optional String orderBy) {
        return facade.getCheckoutDocs(filter, orderBy);
    }

    /**
     * Sends a query to the repository
     * <p/>
     * {@sample.xml ../../../doc/cmis-connector.xml.sample cmis:query}
     *
     * @param statement         the query statement (CMIS query language)
     * @param searchAllVersions specifies if the latest and non-latest versions
     *                          of document objects should be included
     * @param filter            comma-separated list of properties to filter
     * @param orderBy           comma-separated list of query names and the ascending modifier
     *                          "ASC" or the descending modifier "DESC" for each query name
     * @return an iterable of {@link QueryResult}
     */
    @Override
    @Processor
    @InvalidateConnectionOn(exception = CMISConnectorConnectionException.class)
    public ItemIterable<QueryResult> query(@Placement(order = 1) String statement,
                                           @Placement(order = 4) Boolean searchAllVersions,
                                           @Placement(order = 2) @Optional String filter,
                                           @Placement(order = 3) @Optional String orderBy) {
        return facade.query(statement, searchAllVersions, filter, orderBy);
    }

    /**
     * Retrieves the parent folders of a fileable cmis object
     * <p/>
     * {@sample.xml ../../../doc/cmis-connector.xml.sample cmis:getParentFolders}
     *
     * @param cmisObject the object whose parent folders are needed. can be null if "objectId" is set.
     * @param objectId   id of the object whose parent folders are needed. can be null if "object" is set.
     * @return a list of the object's parent folders.
     */
    @Override
    @Processor
    @InvalidateConnectionOn(exception = CMISConnectorConnectionException.class)
    public List<Folder> getParentFolders(@Optional CmisObject cmisObject, @Optional String objectId) {
        return facade.getParentFolders(cmisObject, objectId);
    }

    /**
     * Navigates the folder structure.
     * <p/>
     * {@sample.xml ../../../doc/cmis-connector.xml.sample cmis:folder}
     *
     * @param folder   Folder Object. Can be null if "folderId" is set.
     * @param folderId Folder Object id. Can be null if "folder" is set.
     * @param get      NavigationOptions that specifies whether to get the parent folder,
     *                 the list of immediate children or the whole descendants tree
     * @param depth    if "get" value is DESCENDANTS, represents the depth of the
     *                 descendants tree
     * @param filter   comma-separated list of properties to filter (only for CHILDREN or DESCENDANTS navigation)
     * @param orderBy  comma-separated list of query names and the ascending modifier
     *                 "ASC" or the descending modifier "DESC" for each query name (only for CHILDREN or DESCENDANTS navigation)
     * @return the following, depending on the value of "get" parameter:
     *         <ul>
     *         <li>PARENT: returns the parent Folder</li>
     *         <li>CHILDREN: returns a CmisObject ItemIterable with objects contained in the current folder</li>
     *         <li>DESCENDANTS: List<Tree<FileableCmisObject>> representing
     *         the whole descentants tree of the current folder</li>
     *         <li>TREE: List<Tree<FileableCmisObject>> representing the
     *         directory structure under the current folder.
     *         </li>
     *         </ul>
     */
    @Override
    @Processor
    @InvalidateConnectionOn(exception = CMISConnectorConnectionException.class)
    public Object folder(@Placement(order = 2) @Optional Folder folder,
                         @Placement(order = 3) @Optional String folderId,
                         @Placement(order = 1) NavigationOptions get,
                         @Placement(order = 4) @Optional Integer depth,
                         @Placement(order = 5) @Optional String filter,
                         @Placement(order = 6) @Optional String orderBy) {
        return facade.folder(folder, folderId, get, depth, filter, orderBy);
    }

    /**
     * Retrieves the content stream of a Document.
     * <p/>
     * {@sample.xml ../../../doc/cmis-connector.xml.sample cmis:getContentStream}
     *
     * @param cmisObject The document from which to get the stream. Can be null if "objectId" is set.
     * @param objectId   Id of the document from which to get the stream. Can be null if "object" is set.
     * @return The content stream of the document.
     */
    @Override
    @Processor
    @InvalidateConnectionOn(exception = CMISConnectorConnectionException.class)
    public ContentStream getContentStream(@Optional CmisObject cmisObject,
                                          @Optional String objectId) {
        return facade.getContentStream(cmisObject, objectId);
    }

    /**
     * Moves a fileable cmis object from one location to another. Take into account that a fileable
     * object may be filled in several locations. Thats why you must specify a source folder.
     * <p/>
     * {@sample.xml ../../../doc/cmis-connector.xml.sample cmis:moveObject}
     *
     * @param cmisObject     The object to move. Can be null if "objectId" is set.
     * @param objectId       The object's id. Can be null if "cmisObject" is set.
     * @param sourceFolderId Id of the source folder
     * @param targetFolderId Id of the target folder
     * @return The object moved (FileableCmisObject)
     */
    @Override
    @Processor
    @InvalidateConnectionOn(exception = CMISConnectorConnectionException.class)
    public FileableCmisObject moveObject(@Placement(order = 3) @Optional FileableCmisObject cmisObject,
                                         @Placement(order = 4) @Optional String objectId,
                                         @Placement(order = 1) String sourceFolderId,
                                         @Placement(order = 2) String targetFolderId) {
        return facade.moveObject(cmisObject, objectId, sourceFolderId, targetFolderId);
    }

    /**
     * Update an object's properties
     * <p/>
     * {@sample.xml ../../../doc/cmis-connector.xml.sample cmis:updateObjectProperties}
     *
     * @param cmisObject Object to be updated. Can be null if "objectId" is set.
     * @param objectId   The object's id. Can be null if "cmisObject" is set.
     * @param properties The properties to update
     * @return The updated object (a repository might have created a new object)
     */
    @Override
    @Processor
    @InvalidateConnectionOn(exception = CMISConnectorConnectionException.class)
    public CmisObject updateObjectProperties(@Optional CmisObject cmisObject,
                                             @Optional String objectId,
                                             @Placement(group = "Properties") Map<String, String> properties) {
        return facade.updateObjectProperties(cmisObject, objectId, properties);
    }


    /**
     * Returns the relationships if they have been fetched for an object.
     * <p/>
     * {@sample.xml ../../../doc/cmis-connector.xml.sample cmis:getObjectRelationships}
     *
     * @param cmisObject the object whose relationships are needed
     * @param objectId   the id of the object
     * @return list of the object's relationships
     */
    @Override
    @Processor
    @InvalidateConnectionOn(exception = CMISConnectorConnectionException.class)
    public List<Relationship> getObjectRelationships(@Optional CmisObject cmisObject,
                                                     @Optional String objectId) {
        return facade.getObjectRelationships(cmisObject, objectId);
    }

    /**
     * Returns the ACL if it has been fetched for an object.
     * <p/>
     * {@sample.xml ../../../doc/cmis-connector.xml.sample cmis:getAcl}
     *
     * @param cmisObject the object whose Acl is needed
     * @param objectId   the id of the object
     * @return the object's Acl
     */
    @Override
    @Processor
    @InvalidateConnectionOn(exception = CMISConnectorConnectionException.class)
    public Acl getAcl(@Optional CmisObject cmisObject, @Optional String objectId) {
        return facade.getAcl(cmisObject, objectId);
    }

    /**
     * Retrieve an object's version history
     * <p/>
     * {@sample.xml ../../../doc/cmis-connector.xml.sample cmis:getAllVersions}
     *
     * @param document   the document whose versions are to be retrieved
     * @param documentId Id of the document whose versions are to be retrieved
     * @param filter     comma-separated list of properties to filter (only for CHILDREN or DESCENDANTS navigation)
     * @param orderBy    comma-separated list of query names and the ascending modifier
     *                   "ASC" or the descending modifier "DESC" for each query name (only for CHILDREN or DESCENDANTS navigation)
     * @return versions of the document.
     */
    @Override
    @Processor
    @InvalidateConnectionOn(exception = CMISConnectorConnectionException.class)
    public List<Document> getAllVersions(@Optional CmisObject document,
                                         @Optional String documentId,
                                         @Optional String filter,
                                         @Optional String orderBy) {
        return facade.getAllVersions(document, documentId, filter, orderBy);
    }

    /**
     * Checks out the document and returns the object id of the PWC (private working copy).
     * <p/>
     * {@sample.xml ../../../doc/cmis-connector.xml.sample cmis:checkOut}
     *
     * @param document   The document to be checked out. Can be null if "documentId" is set.
     * @param documentId Id of the document to be checked out. Can be null if "document" is set.
     * @return PWC ObjectId
     */
    @Override
    @Processor
    @InvalidateConnectionOn(exception = CMISConnectorConnectionException.class)
    public ObjectId checkOut(@Optional CmisObject document,
                             @Optional String documentId) {
        return facade.checkOut(document, documentId);
    }

    /**
     * If applied to a PWC (private working copy) of the document, the check out
     * will be reversed. Otherwise, an exception will be thrown.
     * <p/>
     * {@sample.xml ../../../doc/cmis-connector.xml.sample cmis:cancelCheckOut}
     *
     * @param document   The checked out document. Can be null if "documentId" is set.
     * @param documentId Id of the checked out document. Can be null if "document" is set.
     */
    @Override
    @Processor
    @InvalidateConnectionOn(exception = CMISConnectorConnectionException.class)
    public void cancelCheckOut(@Optional CmisObject document,
                               @Optional String documentId) {
        facade.cancelCheckOut(document, documentId);
    }

    /**
     * If applied to a PWC (private working copy) it performs a check in.
     * Otherwise, an exception will be thrown.
     * <p/>
     * {@sample.xml ../../../doc/cmis-connector.xml.sample cmis:checkIn}
     *
     * @param document       The document to check-in. Can be null if "documentId" is set.
     * @param documentId     Id of the document to check-in. Can be null if "document" is set.
     * @param content        File content (no byte array or input stream for now)
     * @param filename       Name of the file
     * @param mimeType       Stream content-type
     * @param major          whether it is major
     * @param checkinComment Check-in comment
     * @param properties     custom properties
     * @return the {@link ObjectId} of the checkedin document
     */
    @Override
    @Processor
    @InvalidateConnectionOn(exception = CMISConnectorConnectionException.class)
    public ObjectId checkIn(@Optional CmisObject document,
                            @Optional String documentId,
                            @Optional @Default("#[payload]") Object content,
                            String filename,
                            String mimeType,
                            boolean major,
                            String checkinComment,
                            @Placement(group = "Properties") @Optional Map<String, String> properties) {
        return facade.checkIn(document, documentId, content, filename, mimeType, major, checkinComment, properties);
    }


    /**
     * Set the permissions associated with an object.
     * <p/>
     * {@sample.xml ../../../doc/cmis-connector.xml.sample cmis:applyAcl}
     *
     * @param cmisObject     the object whose Acl is intended to change.
     * @param objectId       the id of the object
     * @param addAces        added access control entities
     * @param removeAces     removed access control entities
     * @param aclPropagation wheter to propagate changes or not. can be  REPOSITORYDETERMINED | OBJECTONLY | PROPAGATE
     * @return the new access control list
     */
    @Override
    @Processor
    @InvalidateConnectionOn(exception = CMISConnectorConnectionException.class)
    public Acl applyAcl(@Optional CmisObject cmisObject,
                        @Optional String objectId,
                        @Placement(group = "Add Aces") List<Ace> addAces,
                        @Placement(group = "Remove Aces") List<Ace> removeAces,
                        @Placement(order = 1) AclPropagation aclPropagation) {
        return facade.applyAcl(cmisObject, objectId, addAces, removeAces, aclPropagation);
    }

    /**
     * Get the policies that are applied to an object.
     * <p/>
     * {@sample.xml ../../../doc/cmis-connector.xml.sample cmis:getAppliedPolicies}
     *
     * @param cmisObject The document from which to get the stream. Can be null if "objectId" is set.
     * @param objectId   Id of the document from which to get the stream. Can be null if "object" is set.
     * @return List of applied policies
     */
    @Override
    @Processor
    @InvalidateConnectionOn(exception = CMISConnectorConnectionException.class)
    public List<Policy> getAppliedPolicies(@Optional @Default("#[payload]") CmisObject cmisObject,
                                           @Optional String objectId) {
        return facade.getAppliedPolicies(cmisObject, objectId);
    }

    /**
     * Applies policies to this object.
     * <p/>
     * {@sample.xml ../../../doc/cmis-connector.xml.sample cmis:applyPolicy}
     *
     * @param cmisObject The document from which to get the stream. Can be null if "objectId" is set.
     * @param objectId   Id of the document from which to get the stream. Can be null if "object" is set.
     * @param policyIds  Policy ID's to apply
     */
    @Override
    @Processor
    @InvalidateConnectionOn(exception = CMISConnectorConnectionException.class)
    public void applyPolicy(@Optional CmisObject cmisObject,
                            @Optional String objectId,
                            @Placement(group = "Policy Ids") List<ObjectId> policyIds) {
        facade.applyPolicy(cmisObject, objectId, policyIds);
    }

    /**
     * Remove an object
     * <p/>
     * {@sample.xml ../../../doc/cmis-connector.xml.sample cmis:delete}
     *
     * @param cmisObject  The object to be deleted. Can be null if "objectId" is set.
     * @param objectId    The object's id. Can be null if "cmisObject" is set.
     * @param allVersions If true, deletes all version history of the object. Defaults to "false".
     */
    @Override
    @Processor
    @InvalidateConnectionOn(exception = CMISConnectorConnectionException.class)
    public void delete(@Optional CmisObject cmisObject,
                       @Optional String objectId,
                       @Optional @Default("false") boolean allVersions) {
        facade.delete(cmisObject, objectId, allVersions);
    }

    /**
     * Deletes a folder and all subfolders.
     * <p/>
     * {@sample.xml ../../../doc/cmis-connector.xml.sample cmis:deleteTree}
     *
     * @param folder            Folder Object. Can be null if "folderId" is set.
     * @param folderId          Folder Object id. Can be null if "folder" is set.
     * @param allversions       If true, then delete all versions of the document.
     *                          If false, delete only the document object specified.
     * @param unfile            Specifies how the repository must process file-able child-
     *                          or descendant-objects.
     * @param continueOnFailure Specified whether to continue attempting to perform
     *                          this operation even if deletion of a child- or descendant-object
     *                          in the specified folder cannot be deleted or not.
     * @return a list of object ids which failed to be deleted.
     */
    @Override
    @Processor
    @InvalidateConnectionOn(exception = CMISConnectorConnectionException.class)
    public List<String> deleteTree(@Placement(order = 1) @Optional CmisObject folder,
                                   @Placement(order = 2) @Optional String folderId,
                                   @Placement(order = 4) boolean allversions,
                                   @Placement(order = 3) @Optional UnfileObject unfile,
                                   @Placement(order = 5) boolean continueOnFailure) {
        return facade.deleteTree(folder, folderId, allversions, unfile, continueOnFailure);
    }
    
    /**
    * Apply and aspect to an object and set some properties of that aspect.
    * <p/>
    * {@sample.xml ../../../doc/cmis-connector.xml.sample cmis:applyAspect}
    *
    * @param objectId   The object's id.
    * @param aspectName The name of the aspect to be applied to the object.
    * @param properties The properties to set.
    */
    @Override
    @Processor
    @InvalidateConnectionOn(exception = CMISConnectorConnectionException.class)
    public void applyAspect(String objectId,
					        String aspectName,
					        @Optional @Default("#[payload]") Map<String, String> properties) 
    {
        facade.applyAspect(objectId, aspectName, properties);
    }
    
    /**
     * Creates a parent/child relationships between two nodes in the repository of the 
     * specified relationship object type.
     * <p/>
     * {@sample.xml ../../../doc/cmis-connector.xml.sample cmis:createRelationship}
     *
     * @param parentObjectId The ID of the parent (or source) object in the relationship.
     * @param childObjectId The ID of the child (or target) object in the relationship.
     * @param relationshipType The name of the relationship type that should be associated with the objects.
     * @return The {@link ObjectId} that is the result of the relationship
     */
    @Override
    @Processor
    @InvalidateConnectionOn(exception = CMISConnectorConnectionException.class)
    public ObjectId createRelationship ( String parentObjectId, 
    		                         String childObjectId, 
    		                         String relationshipType )
    {
    	return facade.createRelationship(parentObjectId, childObjectId, relationshipType);
    }
    
    public CMISFacade getFacade() {
        return facade;
    }

    public void setFacade(CMISFacade facade) {
        this.facade = facade;
    }

    public void setConnectionIdentifier(String connectionIdentifier) {
        this.connectionIdentifier = connectionIdentifier;
    }
}
