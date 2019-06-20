package gov.foia.service;

/*-
 * #%L
 * ACM Standard Application: Freedom of Information Act
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software. 
 * 
 * If the software was purchased under a paid ArkCase license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.EcmFile;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Query;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import gov.foia.model.ExemptionCodeDto;
import gov.foia.service.DocumentRedactionEvent.RedactionType;

/**
 * FOIA File related services.
 * <p>
 * Created by Petar Ilin <petar.ilin@armedia.com> on 20.09.2016.
 */
public class FOIAFileService implements ApplicationEventPublisherAware
{
    /**
     * Logger instance.
     */
    private final Logger log = LogManager.getLogger(getClass());

    private ApplicationEventPublisher eventPublisher;

    /**
     * ACM File DAO instance.
     */
    private EcmFileDao ecmFileDao;
    private List<String> exemptionCodes;

    /**
     * Update exemption codes list for FOIA file which are associated toFOIAFile entity as an element collection.
     * <p>
     * NOTE: until we refactor the EcmFile pipelines, it would be difficult to subclass EcmFile entity. Thus, we leave
     * the entity intact (its discriminator value is not changed), and we just modify associated table containing codes
     * 
     * @param fileId
     *            file identifier
     * @param exemptionCodes
     *            list of exemption codes (redaction tags)
     * @param user
     */
    @Transactional
    public void updateExemptionCodes(Long fileId, List<String> exemptionCodes, String user)
    {
        // check if such database record exists
        EcmFile ecmFile = ecmFileDao.find(fileId);
        Objects.requireNonNull(ecmFile, "File not found");

        String selectPersistedExemptionCodesSql = "SELECT fo_exemption_code FROM foia_file_exemption_code WHERE ecm_file_id = ?1 AND ecm_file_version = ?2";
        Query selectPersistedExemptionCodesQuery = ecmFileDao.getEm().createNativeQuery(selectPersistedExemptionCodesSql);
        selectPersistedExemptionCodesQuery.setParameter(1, fileId);
        selectPersistedExemptionCodesQuery.setParameter(2, ecmFile.getActiveVersionTag());
        Stream<?> stream = selectPersistedExemptionCodesQuery.getResultList().stream();
        List<String> currentList = stream.map(o -> String.class.cast(o)).collect(Collectors.toList());
        List<String> removeList = new ArrayList<>(currentList);
        removeList.removeAll(exemptionCodes);

        // delete all existing exemption codes that are not in the new exemption codes list associated with given
        // file id
        for (String exemptionCode : removeList)
        {
            String sql = "DELETE FROM foia_file_exemption_code WHERE ecm_file_id = ?1 AND fo_exemption_code = ?2";
            Query query = ecmFileDao.getEm().createNativeQuery(sql);
            query.setParameter(1, fileId);
            query.setParameter(2, exemptionCode);
            query.executeUpdate();
            DocumentRedactionEvent removedEvent = new DocumentRedactionEvent(ecmFile, user, RedactionType.REMOVED, exemptionCode);
            removedEvent.setSucceeded(true);
            eventPublisher.publishEvent(removedEvent);
        }

        // insert new exemption codes associated with given file id
        exemptionCodes.removeAll(currentList);
        if (!exemptionCodes.isEmpty())
        {
            String insertExemptionCodeSql = "INSERT INTO foia_file_exemption_code (ecm_file_id, ecm_file_version, fo_exemption_code, fo_code_creator, fo_code_created) VALUES (?1,?2,?3,?4,?5)";
            Query insertExemptionCodeQuery = ecmFileDao.getEm().createNativeQuery(insertExemptionCodeSql);
            Date dateCreated = new Date();
            for (String exemptionCode : exemptionCodes)
            {
                insertExemptionCodeQuery.setParameter(1, fileId);
                insertExemptionCodeQuery.setParameter(2, ecmFile.getActiveVersionTag());
                insertExemptionCodeQuery.setParameter(3, exemptionCode);
                insertExemptionCodeQuery.setParameter(4, user);
                insertExemptionCodeQuery.setParameter(5, dateCreated);
                insertExemptionCodeQuery.executeUpdate();
                DocumentRedactionEvent insertedEvent = new DocumentRedactionEvent(ecmFile, user, RedactionType.ADDED, exemptionCode);
                insertedEvent.setSucceeded(true);
                eventPublisher.publishEvent(insertedEvent);
            }
        }
        log.debug("Updated exemption codes [{}] of document [{}]", exemptionCodes, fileId);
    }

    public List<ExemptionCodeDto> getExemptionCodes(Long caseId)
    {

        // select all existing exemption codes associated with given foia request (case file) id
        String sql = "SELECT cont.cm_object_id as object_id, file.cm_file_id as file_id,codes.ecm_file_version as file_version, file.cm_file_name as file_name, codes.fo_exemption_code as exemption_code, codes.fo_code_creator as creator, codes.fo_exemption_statute as exemption_statute "
                + "FROM acm_container as cont JOIN acm_file as file ON cont.cm_container_id = file.cm_container_id "
                + "JOIN foia_file_exemption_code as codes ON file.cm_file_id = codes.ecm_file_id "
                + "WHERE cont.cm_object_id = ?1 AND cont.cm_object_type = 'CASE_FILE'";
        Query query = ecmFileDao.getEm().createNativeQuery(sql, "ExemptionCodeResults");

        query.setParameter(1, caseId);
        List<ExemptionCodeDto> exemptionCodes = query.getResultList();

        log.debug("Exemption codes of foia request (case file) [{}] selected", caseId);
        return exemptionCodes;
    }

    @Transactional
    public Integer updateExemptionCodesStatute(ExemptionCodeDto exemptionData)
    {
        // update exemption statute associated with given foia request (case file) id and exemption code
        String sql = "UPDATE foia_file_exemption_code"
                + " SET fo_exemption_statute = ?1"
                + " WHERE ecm_file_id = ?2 AND fo_exemption_code = ?3";

        Query query = ecmFileDao.getEm().createNativeQuery(sql);

        query.setParameter(1, exemptionData.getExemptionStatute());
        query.setParameter(2, exemptionData.getFileId());
        query.setParameter(3, exemptionData.getExemptionCode());
        return query.executeUpdate();

    }

    /**
     * @param applicationEventPublisher
     *            the eventPublisher to set
     */
    /*
     * (non-Javadoc)
     * @see org.springframework.context.ApplicationEventPublisherAware#setApplicationEventPublisher(org.springframework.
     * context.ApplicationEventPublisher)
     */
    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
    {
        eventPublisher = applicationEventPublisher;
    }

    public EcmFileDao getEcmFileDao()
    {
        return ecmFileDao;
    }

    public void setEcmFileDao(EcmFileDao ecmFileDao)
    {
        this.ecmFileDao = ecmFileDao;
    }
}
