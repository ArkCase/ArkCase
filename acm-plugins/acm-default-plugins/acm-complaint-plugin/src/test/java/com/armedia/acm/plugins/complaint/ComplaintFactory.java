package com.armedia.acm.plugins.complaint;

import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.plugins.objectassociation.model.ObjectAssociation;
import com.armedia.acm.plugins.person.model.Person;
import com.armedia.acm.plugins.person.model.PersonAssociation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by armdev on 4/8/14.
 */
public class ComplaintFactory
{
    private Logger log = LoggerFactory.getLogger(getClass());
    
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

        complaint.setOriginator(new PersonAssociation());
        PersonAssociation pa = complaint.getOriginator();
        
        Person p = new Person();
        p.setFamilyName("Person");
        p.setGivenName("ACM");
        p.setStatus("testStatus");
        p.setCreator("ann-acm");
        p.setModifier("ann-acm");

        pa.setPerson(p);
        pa.setPersonDescription("Simple Description");
        pa.setPersonType("Subject");
        pa.setCreator("originatorCreator");
        pa.setModifier("originatorModifier");

        complaint.setOriginator(pa);
        
        PersonAssociation personAssoc = new PersonAssociation();
        
        personAssoc.setPerson(p);
        personAssoc.setPersonType("Complainant");
        personAssoc.setPersonDescription("Short Description");
        
        List <PersonAssociation> listPersonAssoc = complaint.getPersonAssociations();
        listPersonAssoc.add(pa);

        ObjectAssociation oa = new ObjectAssociation();
        oa.setTargetId(12345L);
        oa.setTargetType("DOCUMENT");
        oa.setTargetName("Test Name");
        oa.setCreator("tester");
        oa.setModifier("testModifier");

        complaint.addChildObject(oa);
        complaint.setPersonAssociations(listPersonAssoc);

        return complaint;
    }
}
