package com.armedia.acm.plugins.casefile.service;

import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.plugins.casefile.exceptions.AcmCaseFileNotFound;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.objectassociation.model.ObjectAssociation;
import com.armedia.acm.plugins.person.model.PersonAssociation;
import com.armedia.acm.service.milestone.model.AcmMilestone;
import com.armedia.acm.services.participants.model.AcmParticipant;
import org.apache.commons.lang.StringUtils;
import org.mule.api.MuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MergeCaseServiceImpl implements MergeCaseService {
    private final Logger log = LoggerFactory.getLogger(getClass());

    private SaveCaseService saveCaseService;
    private CaseFileDao caseFileDao;

    @Override
    @Transactional
    public CaseFile mergeCases(Authentication auth, String ipAddress, Long sourceId, Long targetId) throws MuleException {

        CaseFile source = caseFileDao.find(sourceId);
        if (source == null)
            throw new AcmCaseFileNotFound("Source Case File with id = " + sourceId + " not found");
        CaseFile target = caseFileDao.find(targetId);
        if (target == null)
            throw new AcmCaseFileNotFound("Target Case File with id = " + targetId + " not found");
        CaseFile[] cases = new CaseFile[]{target, source};
        Set<String> approversSet = new HashSet<>();
        Set<PersonAssociation> personAssociationSet = new HashSet<>();
        Set<AcmMilestone> acmMilestoneSet = new HashSet<>();
        Set<AcmParticipant> acmParticipantSet = new HashSet<>();
        Set<ObjectAssociation> childObjectsSet = new HashSet<>();
        for (int i = 0; i < cases.length; i++) {
            CaseFile cf = cases[i];
            //merge approvers
            if (cf.getApprovers() != null && !cf.getApprovers().isEmpty())
                approversSet.addAll(cf.getApprovers());
            //merge person association
            if (cf.getPersonAssociations() != null && !cf.getPersonAssociations().isEmpty())
                personAssociationSet.addAll(cf.getPersonAssociations());
            //merge milestones
            if (cf.getMilestones() != null && !cf.getMilestones().isEmpty())
                acmMilestoneSet.addAll(cf.getMilestones());
            //merge participants
            if (cf.getParticipants() != null && !cf.getParticipants().isEmpty())
                acmParticipantSet.addAll(cf.getParticipants());
            //merge child objects
            if (cf.getChildObjects() != null && !cf.getChildObjects().isEmpty())
                childObjectsSet.addAll(cf.getChildObjects());
        }

        target.setApprovers(new ArrayList<>(approversSet));
        target.setPersonAssociations(new ArrayList<>(personAssociationSet));
        target.setMilestones(new ArrayList<>(acmMilestoneSet));
        target.setParticipants(new ArrayList<>(acmParticipantSet));
        childObjectsSet.forEach(target::addChildObject);

        //merge title
        String sourceTitle = StringUtils.isEmpty(source.getTitle()) ? "" : source.getTitle();
        target.setTitle(!StringUtils.isEmpty(target.getTitle()) ? target.getTitle() + MERGE_TEXT_SEPPARATOR + sourceTitle : sourceTitle);

        //merge details
        String sourceDetails = StringUtils.isEmpty(source.getDetails()) ? "" : source.getDetails();
        target.setDetails(!StringUtils.isEmpty(target.getDetails()) ? target.getDetails() + MERGE_TEXT_SEPPARATOR + sourceDetails : sourceDetails);

        //set source that is merged
        source.setMergedTo(target);

        saveCaseService.saveCase(source, auth, ipAddress);
        saveCaseService.saveCase(target, auth, ipAddress);

        return target;
    }

    public void setSaveCaseService(SaveCaseService saveCaseService) {
        this.saveCaseService = saveCaseService;
    }

    public void setCaseFileDao(CaseFileDao caseFileDao) {
        this.caseFileDao = caseFileDao;
    }
}
