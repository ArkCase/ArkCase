package com.armedia.acm.plugins.casefile.service;

import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.objectassociation.model.ObjectAssociation;
import com.armedia.acm.plugins.person.model.PersonAssociation;
import com.armedia.acm.service.milestone.model.AcmMilestone;
import com.armedia.acm.services.participants.model.AcmParticipant;
import org.easymock.EasyMock;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/spring/spring-library-merge-case-test.xml"})
public class MergeCaseServiceImplTest extends EasyMockSupport {

    private SaveCaseService saveCaseService;

    @Autowired
    private MergeCaseServiceImpl mergeCaseService;
    private Authentication auth;
    private String ipAddress;
    private CaseFileDao caseFileDao;
    private Long sourceId;
    private Long targetId;

    @Before
    public void setUp() {
        auth = createMock(Authentication.class);
        ipAddress = "127.0.0.1";
        saveCaseService = createMock(SaveCaseService.class);
        caseFileDao = createMock(CaseFileDao.class);
        mergeCaseService.setSaveCaseService(saveCaseService);
        mergeCaseService.setCaseFileDao(caseFileDao);
        sourceId = 1L;
        targetId = 2L;
    }

    @Test
    public void testMergeCases() throws Exception {
        assertNotNull(mergeCaseService);

        CaseFile sourceCaseFile = new CaseFile();
        CaseFile targetCaseFile = new CaseFile();
        fillSourceDammyData(sourceCaseFile);
        fillTargetDammyData(targetCaseFile);

        EasyMock.expect(saveCaseService.saveCase(sourceCaseFile, auth, ipAddress)).andReturn(sourceCaseFile);
        EasyMock.expect(saveCaseService.saveCase(targetCaseFile, auth, ipAddress)).andReturn(targetCaseFile);

        EasyMock.expect(caseFileDao.find(sourceId)).andReturn(sourceCaseFile);
        EasyMock.expect(caseFileDao.find(targetId)).andReturn(targetCaseFile);

        replayAll();

        targetCaseFile = mergeCaseService.mergeCases(auth, ipAddress, sourceId, targetId);

        assertEquals(3, targetCaseFile.getApprovers().size());
        assertEquals(3, targetCaseFile.getParticipants().size());
        assertEquals(5, targetCaseFile.getChildObjects().size());
        assertEquals(3, targetCaseFile.getPersonAssociations().size());
        assertEquals(3, targetCaseFile.getMilestones().size());
        assertEquals("Target" + MergeCaseService.MERGE_TEXT_SEPPARATOR + "Source", targetCaseFile.getTitle());
        assertEquals("Target Details" + MergeCaseService.MERGE_TEXT_SEPPARATOR + "Source Details", targetCaseFile.getDetails());
    }

    private void fillTargetDammyData(CaseFile caseFile) {
        caseFile.setId(targetId);

        //add approvers
        caseFile.setApprovers(Arrays.asList("appr1", "appr2"));
        //add participants
        List<AcmParticipant> participants = new ArrayList<>();
        AcmParticipant p1 = new AcmParticipant();
        p1.setId(1l);
        participants.add(p1);
        AcmParticipant p2 = new AcmParticipant();
        p2.setId(3l);
        participants.add(p2);
        caseFile.setParticipants(participants);

        //add person associations
        List<PersonAssociation> personAssociations = new ArrayList<>();
        PersonAssociation pa1 = new PersonAssociation();
        pa1.setId(1l);
        personAssociations.add(pa1);
        PersonAssociation pa2 = new PersonAssociation();
        pa2.setId(3l);
        personAssociations.add(pa2);
        caseFile.setPersonAssociations(personAssociations);

        //add milestones
        List<AcmMilestone> milistones = new ArrayList<>();
        AcmMilestone ms1 = new AcmMilestone();
        ms1.setId(1l);
        milistones.add(ms1);
        AcmMilestone ms2 = new AcmMilestone();
        ms2.setId(3l);
        milistones.add(ms2);
        caseFile.setMilestones(milistones);

        //add childObjects
        ObjectAssociation co1 = new ObjectAssociation();
        ObjectAssociation co2 = new ObjectAssociation();
        co1.setAssociationId(1L);
        co2.setAssociationId(3L);
        caseFile.addChildObject(co1);
        caseFile.addChildObject(co2);

        caseFile.setTitle("Target");
        caseFile.setDetails("Target Details");

    }

    private void fillSourceDammyData(CaseFile caseFile) {
        caseFile.setId(sourceId);

        caseFile.setApprovers(Arrays.asList("appr1", "appr3"));

        //add participants
        List<AcmParticipant> participants = new ArrayList<>();
        AcmParticipant p1 = new AcmParticipant();
        p1.setId(1l);
        participants.add(p1);
        AcmParticipant p2 = new AcmParticipant();
        p2.setId(2l);
        participants.add(p2);
        caseFile.setParticipants(participants);

        //add person associations
        List<PersonAssociation> personAssociations = new ArrayList<>();
        PersonAssociation pa1 = new PersonAssociation();
        pa1.setId(1l);
        personAssociations.add(pa1);
        PersonAssociation pa2 = new PersonAssociation();
        pa2.setId(2l);
        personAssociations.add(pa2);
        caseFile.setPersonAssociations(personAssociations);

        //add milestones
        List<AcmMilestone> milistones = new ArrayList<>();
        AcmMilestone ms1 = new AcmMilestone();
        ms1.setId(1l);
        milistones.add(ms1);
        AcmMilestone ms2 = new AcmMilestone();
        ms2.setId(2l);
        milistones.add(ms2);
        caseFile.setMilestones(milistones);

        //add childObjects
        ObjectAssociation co1 = new ObjectAssociation();
        ObjectAssociation co2 = new ObjectAssociation();
        co1.setAssociationId(1L);
        co2.setAssociationId(2L);
        caseFile.addChildObject(co1);
        caseFile.addChildObject(co2);

        caseFile.setTitle("Source");
        caseFile.setDetails("Source Details");
    }

    public void setMergeCaseService(MergeCaseServiceImpl mergeCaseService) {
        this.mergeCaseService = mergeCaseService;
    }
}