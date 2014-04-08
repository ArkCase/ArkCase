package com.armedia.acm.plugins.complaint;

import com.armedia.acm.plugins.complaint.model.Complaint;
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
        complaint.setComplaintType("TEST");
        complaint.setCreated(new Date());
        complaint.setCreator("tester");
        complaint.setDetails("details");
        complaint.setPriority("testPriority");
        complaint.setModified(new Date());
        complaint.setModifier("testModifier");

        Person p = new Person();
        p.setModifier("testModifier");
        p.setCreator("testCreator");
        p.setCreated(new Date());
        p.setModified(new Date());
        p.setFamilyName("Person");
        p.setGivenName("ACM");
        p.setStatus("testStatus");

        complaint.setOriginator(p);

        return complaint;
    }
}
