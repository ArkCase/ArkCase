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

public interface CMISFacade {

    /**
     * @return all repositories that are available at the endpoint.
     */
    List<Repository> repositories();

    /**
     * Returns information about the CMIS repository, the optional capabilities it supports and its
     * Access Control information if applicable.
     *
     * @return a {@link RepositoryInfo} object
     */
    RepositoryInfo repositoryInfo();

    /**
     * Gets repository changes.
     *
     * @param changeLogToken    the change log token to start from or {@code null}
     * @param includeProperties indicates if changed properties should be included in
     *                          the result
     * @return the changelog events
     */
    ChangeEvents changelog(String changeLogToken, boolean includeProperties);

    /**
     * Returns a CMIS object from the repository and puts it into the cache.
     *
     * @param objectId the object id
     * @return a {@link CmisObject} instance
     */
    CmisObject getObjectById(String objectId);

    /**
     * Returns a CMIS object from the repository and puts it into the cache.
     *
     * @param path path of the object to retrieve
     * @return a {@link CmisObject} instance
     */
    CmisObject getObjectByPath(String path);

    /**
     * Creates a new document in the repository where the content comes directly from the payload and
     * the target folder node is specified by a repository path.
     *
     * @param folderPath      Folder in the repository that will hold the document
     * @param filename        name of the file
     * @param content         file content as specified in the payload
     * @param mimeType        stream content-type
     * @param versioningState An enumeration specifying what the versioing state of the newly-created object MUST be. If the repository does not support versioning, the repository MUST ignore the versioningState parameter.  Valid values are:
     *                        o none:  The document MUST be created as a non-versionable document.
     *                        o checkedout: The document MUST be created in the checked-out state.
     *                        o major (default): The document MUST be created as a major version
     *                        o minor: The document MUST be created as a minor version.
     * @param objectType      the type of the object
     * @param properties      the properties optional document properties to set
     * @param force           if should folder structure must be created when there
     *                        are missing intermediate folders
     * @return the object id {@link ObjectId} of the created
     */
    ObjectId createDocumentByPath(String folderPath,
                                  String filename,
                                  Object content,
                                  String mimeType,
                                  VersioningState versioningState,
                                  String objectType,
                                  Map<String, String> properties, 
                                  boolean force);
    
    
    /**
     * Creates a new document in the repository where the content is specified as the value of the "content"
     * parameter and the target folder node is specified by a repository path.
     *
     * @param folderPath      Folder in the repository that will hold the document
     * @param filename        Name of the file
     * @param content         File content
     * @param mimeType        Stream content-type
     * @param versioningState An enumeration specifying what the versioing state of the newly-created object MUST be. If the repository does not support versioning, the repository MUST ignore the versioningState parameter.
     * @param objectType      The type of the object.
     * @param properties      the properties optional document properties to set
     * @param force           if should folder structure must be created when there
     *                        are missing intermediate folders
     * @return the {@link ObjectId} of the created
     */
    ObjectId createDocumentByPathFromContent(String folderPath,
			                                 String filename,
			                                 Object content,
			                                 String mimeType,
			                                 VersioningState versioningState,
			                                 String objectType,
			                                 Map<String, String> properties,
			                                 boolean force);

    /**
     * Creates a new folder in the repository if it doesn't already exist
     *
     * @param folderPath      Path to the folder
     */
    CmisObject getOrCreateFolderByPath(String folderPath);
    
    /**
     * Creates a folder. Note that this is not recusive creation. You just create
     * one folder
     *
     * @param folderName     folder name (eg: "my documents")
     * @param parentObjectId Parent folder for the folder being created (eg: repository.rootFolder)
     * @return the object id {@link ObjectId} of the created
     */
    ObjectId createFolder(String folderName, String parentObjectId);

    /**
     * Creates a new document in the repository where the content comes directly from the payload and
     * the target folder node is specified by an object ID.
     *
     * @param folderId        Folder Object Id
     * @param filename        name of the file
     * @param content         file content as specified in the payload
     * @param mimeType        stream content-type
     * @param versioningState An enumeration specifying what the versioing state of the newly-created object MUST be. If the repository does not support versioning, the repository MUST ignore the versioningState parameter.  Valid values are:
     *                        o none:  The document MUST be created as a non-versionable document.
     *                        o checkedout: The document MUST be created in the checked-out state.
     *                        o major (default): The document MUST be created as a major version
     *                        o minor: The document MUST be created as a minor version.
     * @param objectType      the type of the object
     * @param properties      the properties optional document properties to set
     * @return the object id {@link ObjectId} of the created
     */
    ObjectId createDocumentById(String folderId,
                                String filename,
                                Object content,
                                String mimeType,
                                VersioningState versioningState,
                                String objectType,
                                Map<String, String> properties);
    
    
    /**
     * Creates a new document in the repository where the content comes directly from the payload and
     * the target folder node is specified by an object ID.
     *
     * @param folderId        Folder Object Id
     * @param filename        name of the file
     * @param content         file content
     * @param mimeType        stream content-type
     * @param versioningState An enumeration specifying what the versioing state of the newly-created object MUST be. If the repository does not support versioning, the repository MUST ignore the versioningState parameter.  Valid values are:
     *                        o none:  The document MUST be created as a non-versionable document.
     *                        o checkedout: The document MUST be created in the checked-out state.
     *                        o major (default): The document MUST be created as a major version
     *                        o minor: The document MUST be created as a minor version.
     * @param objectType      the type of the object
     * @param properties      the properties optional document properties to set
     * @return the object id {@link ObjectId} of the created
     */
    ObjectId createDocumentByIdFromContent(String folderId,
			                               String filename,
			                               Object content,
			                               String mimeType,
			                               VersioningState versioningState,
			                               String objectType,
			                               Map<String, String> properties);

    /**
     * Returns the type definition of the given type id.
     *
     * @param typeId Object type Id
     * @return type of object
     */
    ObjectType getTypeDefinition(String typeId);

    /**
     * Retrieve list of checked out documents.
     *
     * @param filter  comma-separated list of properties to filter
     * @param orderBy comma-separated list of query names and the ascending modifier
     *                "ASC" or the descending modifier "DESC" for each query name
     * @return list of documents
     */
    ItemIterable<Document> getCheckoutDocs(String filter, String orderBy);

    /**
     * Sends a query to the repository
     *
     * @param statement         the query statement (CMIS query language)
     * @param searchAllVersions specifies if the latest and non-latest versions
     *                          of document objects should be included
     * @param filter            comma-separated list of properties to filter
     * @param orderBy           comma-separated list of query names and the ascending modifier
     *                          "ASC" or the descending modifier "DESC" for each query name
     * @return an iterable of {@link QueryResult}
     */
    ItemIterable<QueryResult> query(String statement, Boolean searchAllVersions,
                                    String filter, String orderBy);

    /**
     * Retrieves the parent folders of a fileable cmis object
     *
     * @param cmisObject the object whose parent folders are needed. can be null if "objectId" is set.
     * @param objectId   id of the object whose parent folders are needed. can be null if "object" is set.
     * @return a list of the object's parent folders.
     */
    List<Folder> getParentFolders(CmisObject cmisObject, String objectId);

    /**
     * Navigates the folder structure.
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
    Object folder(Folder folder, String folderId, NavigationOptions get,
                  Integer depth, String filter, String orderBy);

    /**
     * Retrieves the content stream of a Document.
     *
     * @param cmisObject The document from which to get the stream. Can be null if "objectId" is set.
     * @param objectId   Id of the document from which to get the stream. Can be null if "object" is set.
     * @return The content stream of the document.
     */
    ContentStream getContentStream(CmisObject cmisObject, String objectId);

    /**
     * Moves a fileable cmis object from one location to another. Take into account that a fileable
     * object may be filled in several locations. Thats why you must specify a source folder.
     *
     * @param cmisObject     The object to move. Can be null if "objectId" is set.
     * @param objectId       The object's id. Can be null if "cmisObject" is set.
     * @param sourceFolderId Id of the source folder
     * @param targetFolderId Id of the target folder
     * @return The object moved (FileableCmisObject)
     */
    FileableCmisObject moveObject(FileableCmisObject cmisObject,
                                  String objectId,
                                  String sourceFolderId,
                                  String targetFolderId);

    /**
     * Update an object's properties
     *
     * @param cmisObject Object to be updated. Can be null if "objectId" is set.
     * @param objectId   The object's id. Can be null if "cmisObject" is set.
     * @param properties The properties to update
     * @return The updated object (a repository might have created a new object)
     */
    CmisObject updateObjectProperties(CmisObject cmisObject,
                                      String objectId,
                                      Map<String, String> properties);

    /**
     * Remove an object
     *
     * @param cmisObject  The object to be deleted. Can be null if "objectId" is set.
     * @param objectId    The object's id. Can be null if "cmisObject" is set.
     * @param allVersions If true, deletes all version history of the object. Defaults to "false".
     */
    void delete(CmisObject cmisObject, String objectId, boolean allVersions);

    /**
     * Deletes a folder and all subfolders.
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
    List<String> deleteTree(CmisObject folder, String folderId, boolean allversions,
                            UnfileObject unfile, boolean continueOnFailure);

    /**
     * Returns the relationships if they have been fetched for an object.
     *
     * @param cmisObject the object whose relationships are needed
     * @param objectId   The object's id. Can be null if "cmisObject" is set.
     * @return list of {@link Relationship} the object's relationships
     */
    List<Relationship> getObjectRelationships(CmisObject cmisObject, String objectId);

    /**
     * Returns the ACL if it has been fetched for an object.
     *
     * @param cmisObject the object whose Acl is needed
     * @param objectId   The object's id. Can be null if "cmisObject" is set.
     * @return the object's Acl {@link Acl}
     */
    Acl getAcl(CmisObject cmisObject, String objectId);

    /**
     * Set the permissions associated with an object.
     * <p/>
     * {@code <cmis:get-acl objectId="workspace://SpacesStore/64b078f5-3024-403b-b133-fa87d0060f28" />}
     *
     * @param cmisObject     the object whose Acl is intended to change.
     * @param objectId       The object's id. Can be null if "cmisObject" is set.
     * @param addAces        added access control entities
     * @param removeAces     removed access control entities
     * @param aclPropagation wheter to propagate changes or not. can be  REPOSITORYDETERMINED | OBJECTONLY | PROPAGATE
     * @return the new access control list
     */
    Acl applyAcl(CmisObject cmisObject, String objectId, List<Ace> addAces,
                 List<Ace> removeAces, AclPropagation aclPropagation);

    /**
     * Retrieve an object's version history
     *
     * @param document   the document whose versions are to be retrieved
     * @param documentId Id of the document whose versions are to be retrieved
     * @param filter     comma-separated list of properties to filter (only for CHILDREN or DESCENDANTS navigation)
     * @param orderBy    comma-separated list of query names and the ascending modifier
     *                   "ASC" or the descending modifier "DESC" for each query name (only for CHILDREN or DESCENDANTS navigation)
     * @return versions of the document.
     */
    List<Document> getAllVersions(CmisObject document, String documentId,
                                  String filter, String orderBy);


    /**
     * Checks out the document and returns the object id of the PWC (private working copy).
     *
     * @param document   The document to be checked out. Can be null if "documentId" is set.
     * @param documentId Id of the document to be checked out. Can be null if "document" is set.
     * @return PWC ObjectId
     */
    ObjectId checkOut(CmisObject document, String documentId);

    /**
     * If applied to a PWC (private working copy) of the document, the check out
     * will be reversed. Otherwise, an exception will be thrown.
     *
     * @param document   The checked out document. Can be null if "documentId" is set.
     * @param documentId Id of the checked out document. Can be null if "document" is set.
     */
    void cancelCheckOut(CmisObject document, String documentId);

    /**
     * If applied to a PWC (private working copy) it performs a check in.
     * Otherwise, an exception will be thrown.
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
    ObjectId checkIn(CmisObject document, String documentId,
                     Object content, String filename,
                     String mimeType, boolean major, String checkinComment, Map<String, String> properties);

    /**
     * Get the policies that are applied to an object.
     *
     * @param cmisObject The document from which to get the stream. Can be null if "objectId" is set.
     * @param objectId   Id of the document from which to get the stream. Can be null if "object" is set.
     * @return List of applied policies
     */
    List<Policy> getAppliedPolicies(CmisObject cmisObject, String objectId);

    /**
     * Applies policies to this object.
     *
     * @param cmisObject The document from which to get the stream. Can be null if "objectId" is set.
     * @param objectId   Id of the document from which to get the stream. Can be null if "object" is set.
     * @param policyIds  Policy ID's to apply
     */
    void applyPolicy(CmisObject cmisObject, String objectId, List<ObjectId> policyIds);
    
    /**
     * Apply the specified aspect and set some properties (optional) for the aspect.
     *
     * @param objectId   The object's id.
     * @param aspectName The name of the aspect.
     * @param properties The properties to set. Can be null.
     * @return The updated object (a repository might have created a new object)
     */
    void applyAspect(String objectId,
                     String aspectName,
                     Map<String, String> properties);
    
    
    /**
     * Creates a parent/child relationships between two nodes in the repository of the 
     * specified relationship object type.
     *
     * @param parentObjectId The ID of the parent (or source) object in the relationship.
     * @param childObjectId The ID of the child (or target) object in the relationship.
     * @param relationshipType The name of the relationship type that should be associated with the objects.
     * @return The {@link ObjectId} that is the result of the relationship
     */    
    ObjectId createRelationship(String parentObjectId, String childObjectId, String relationshipType);
}