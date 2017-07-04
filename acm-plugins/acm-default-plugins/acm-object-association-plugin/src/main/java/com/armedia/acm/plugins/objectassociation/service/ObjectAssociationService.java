package com.armedia.acm.plugins.objectassociation.service;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.plugins.objectassociation.model.AcmChildObjectEntity;
import com.armedia.acm.plugins.objectassociation.model.ObjectAssociation;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


public interface ObjectAssociationService
{
    void addReference(Long id, String number, String type, String title, String status, Long parentId, String parentType) throws Exception;

    @Transactional
    void delete(Long id);

    AcmAbstractDao<AcmChildObjectEntity> getDaoForChildObjectEntity(String objectType);

    ObjectAssociation saveObjectAssociation(ObjectAssociation oa);

    List<ObjectAssociation> findByParentTypeAndId(String type, Long id);


    /**
     * List Associations for given object id and type.
     * Results are combined with target documents.
     *
     * @param auth       Authentication
     * @param parentId   id of the owner of associations
     * @param parentType type of the owner of associations
     * @param targetType id of the target of associations
     * @param start      which row to start
     * @param limit      number of rows to retrieve
     * @return solr response
     */
    String getAssociations(Authentication auth, Long parentId, String parentType, String targetType, int start, int limit) throws AcmObjectNotFoundException;

    /**
     * saves object association
     *
     * @param objectAssociation Object association
     * @param auth              Authentication
     * @return saved association
     */
    ObjectAssociation saveAssociation(ObjectAssociation objectAssociation, Authentication auth);

    /**
     * Removes object association
     *
     * @param id   id of the association
     * @param auth Authentication
     */
    void deleteAssociation(Long id, Authentication auth);

    /**
     * return object association for given id
     *
     * @param id   id of the association
     * @param auth Authentication
     * @return object association
     */
    ObjectAssociation getAssociation(Long id, Authentication auth);
}
