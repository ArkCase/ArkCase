package com.armedia.acm.plugins.complaint;

import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.plugins.objectassociation.model.ObjectAssociation;
import com.armedia.acm.plugins.person.model.Person;

import java.util.Date;
import java.util.UUID;

/**
 * Created by armdev on 4/8/14.
 */
public class ComplaintFactory
{
    public Complaint complaint()
    {
        Complaint complaint = new Complaint();
        complaint.setComplaintNumber(UUID.randomUUID().toString());
        complaint.setComplaintTitle("testTitle");
        complaint.setComplaintType("Local");
        complaint.setCreated(new Date());
        complaint.setCreator("tester");
        complaint.setDetails("details");
        complaint.setPriority("Expedite");
        complaint.setModified(new Date());
        complaint.setModifier("testModifier");

        Person p = complaint.getOriginator();
        p.setFamilyName("Person");
        p.setGivenName("ACM");
        p.setStatus("testStatus");

        complaint.setOriginator(p);

        ObjectAssociation oa = new ObjectAssociation();
        oa.setTargetId(12345L);
        oa.setTargetType("DOCUMENT");
        oa.setTargetName("Test Name");
        oa.setCreator("tester");
        oa.setModifier("testModifier");

        complaint.addChildObject(oa);

        return complaint;
    }
}
