package com.armedia.acm.plugins.complaint;

import com.armedia.acm.plugins.addressable.model.ContactMethod;
import com.armedia.acm.plugins.addressable.model.PostalAddress;
import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.plugins.objectassociation.model.ObjectAssociation;
import com.armedia.acm.plugins.person.model.Organization;
import com.armedia.acm.plugins.person.model.Person;
import com.armedia.acm.plugins.person.model.PersonAssociation;

import java.util.ArrayList;
import java.util.Calendar;

import com.armedia.acm.services.participants.model.AcmParticipant;
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
        complaint.setDetails("details");
        complaint.setPriority("Expedite");
        complaint.setTag("noTag");
        complaint.setFrequency("once");
        
        PostalAddress location = new PostalAddress();
        location.setStreetAddress("testAddress");
        location.setCity("testCity");
        location.setState("testState");
        location.setZip("12345");
        location.setType("home");
        
        complaint.setLocation(location);
        
        Calendar  cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DATE, 3);
        
        Date dueDate = cal.getTime();
        complaint.setDueDate(dueDate);
        
        log.debug("the due date is set to: " + dueDate);

        complaint.setOriginator(new PersonAssociation());
        PersonAssociation pa = complaint.getOriginator();
        
        Person p = new Person();
        p.setFamilyName("Person");
        p.setGivenName("ACM");
        p.setStatus("testStatus");

        ContactMethod cm = new ContactMethod();
        cm.setType("Phone Number");
        cm.setValue("703-555-1212");

        List<ContactMethod> cms = new ArrayList<>();
        cms.add(cm);
        p.setContactMethods(cms);

        Organization org = new Organization();
        org.setOrganizationType("Corporation");
        org.setOrganizationValue("The League of Extraordinary Gentlemen");

        List<Person> ps = new ArrayList<>();
        ps.add(p);
        List<Organization> orgs = new ArrayList<>();
        orgs.add(org);
        p.setOrganizations(orgs);

        pa.setPerson(p);
        pa.setPersonDescription("Simple Description");
        pa.setPersonType("Subject");

        complaint.setOriginator(pa);
        
        PersonAssociation personAssoc = new PersonAssociation();

        Person p2 = new Person();
        p2.setFamilyName("Person 2");
        p2.setGivenName("ACM");
        p2.setStatus("testStatus");
        
        personAssoc.setPerson(p2);
        personAssoc.setPersonType("Witness");
        personAssoc.setPersonDescription("Short Description");

        List <PersonAssociation> listPersonAssoc = complaint.getPersonAssociations();
        listPersonAssoc.add(personAssoc);

        ObjectAssociation oa = new ObjectAssociation();
        oa.setTargetId(12345L);
        oa.setTargetType("DOCUMENT");
        oa.setTargetName("Test Name");

        complaint.addChildObject(oa);
        complaint.setPersonAssociations(listPersonAssoc);

        AcmParticipant assignee = new AcmParticipant();
        assignee.setParticipantType("assignee");
        assignee.setParticipantLdapId("ann-acm");

        complaint.getParticipants().add(assignee);

        return complaint;
    }
}
