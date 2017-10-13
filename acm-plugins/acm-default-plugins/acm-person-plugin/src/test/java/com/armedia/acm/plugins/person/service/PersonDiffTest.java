package com.armedia.acm.plugins.person.service;

import com.armedia.acm.objectdiff.model.AcmDiff;
import com.armedia.acm.objectdiff.service.AcmDiffService;
import com.armedia.acm.plugins.addressable.model.ContactMethod;
import com.armedia.acm.plugins.addressable.model.PostalAddress;
import com.armedia.acm.plugins.person.model.Identification;
import com.armedia.acm.plugins.person.model.Person;
import com.armedia.acm.plugins.person.model.PersonAlias;
import org.apache.commons.lang3.SerializationUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "/spring/spring-library-person-diff-test.xml"
})
public class PersonDiffTest
{

    @Autowired
    private AcmDiffService acmDiffService;
    private Person oldPerson;
    private ContactMethod defaultUrl;
    private ContactMethod defaultEmail;
    private ContactMethod defaultPhone;
    private PostalAddress defaultAddress;
    private Identification defaultIdentification;
    private PersonAlias defaultAlias;


    @Before
    public void setUp()
    {

        defaultUrl = new ContactMethod();
        defaultUrl.setId(1l);
        defaultUrl.setValue("defaultUrl");
        defaultUrl.setType("url");

        defaultEmail = new ContactMethod();
        defaultEmail.setId(2l);
        defaultEmail.setValue("defaultEmail");
        defaultEmail.setType("email");

        defaultPhone = new ContactMethod();
        defaultPhone.setId(3l);
        defaultPhone.setValue("defaultPhone");
        defaultPhone.setType("phone");

        defaultAddress = new PostalAddress();
        defaultAddress.setId(4l);
        defaultAddress.setCity("City");
        defaultAddress.setState("State");
        defaultAddress.setStreetAddress("Street123");
        defaultAddress.setZip("1000");

        defaultIdentification = new Identification();
        defaultIdentification.setIdentificationID(1l);
        defaultIdentification.setIdentificationNumber("12345");
        defaultIdentification.setIdentificationIssuer("Issuer");
        defaultIdentification.setIdentificationType("ID CARD");

        defaultAlias = new PersonAlias();
        defaultAlias.setId(1l);
        defaultAlias.setAliasType("type");
        defaultAlias.setAliasValue("value");

        oldPerson = new Person();
        oldPerson.setDefaultUrl(defaultUrl);
        oldPerson.setDefaultEmail(defaultEmail);
        oldPerson.setDefaultPhone(defaultPhone);
        oldPerson.getContactMethods().add(defaultUrl);
        oldPerson.getContactMethods().add(defaultEmail);
        oldPerson.getContactMethods().add(defaultPhone);

        oldPerson.setDefaultAddress(defaultAddress);
        oldPerson.getAddresses().add(defaultAddress);

        oldPerson.setDefaultIdentification(defaultIdentification);
        oldPerson.getIdentifications().add(defaultIdentification);

        oldPerson.setDefaultAlias(defaultAlias);
        oldPerson.getPersonAliases().add(defaultAlias);

        oldPerson.setFamilyName("family name");
        oldPerson.setGivenName("given name");
        oldPerson.setId(1l);
        oldPerson.setTitle("Title");
        oldPerson.setModified(new Date(System.currentTimeMillis()));
    }

    @Test
    public void compareTestNewAddress() throws Exception
    {
        oldPerson.setAddresses(null);
        oldPerson.setDefaultAddress(null);
        Person newPerson = SerializationUtils.clone(oldPerson);
        newPerson.setModified(new Date(System.currentTimeMillis()));
        List<PostalAddress> addresses = new ArrayList<>();
        PostalAddress address = new PostalAddress();
        address.setId(1l);
        address.setState("state");
        address.setCity("city");
        addresses.add(address);
        newPerson.setAddresses(addresses);
        newPerson.setDefaultAddress(address);

        AcmDiff diff = acmDiffService.compareObjects(oldPerson, newPerson);
        assertEquals(2, diff.getChangesAsList().size());
    }

    @Test
    public void compareTest() throws Exception
    {
        Person newPerson = SerializationUtils.clone(oldPerson);
        newPerson.setModified(new Date(System.currentTimeMillis()));

        newPerson.getIdentifications().get(0).setIdentificationNumber("11");
        newPerson.getIdentifications().get(0).setIdentificationYearIssued(new Date());
        newPerson.getIdentifications().get(0).setIdentificationType("blabla card");
        newPerson.setDefaultAlias(null);
        newPerson.setTitle(null);

        newPerson.getContactMethods().remove(0);
        newPerson.getContactMethods().get(0).setValue("test");
        ContactMethod newPhone = new ContactMethod();
        newPhone.setId(4l);
        newPhone.setValue("defaultEmail");
        newPhone.setType("phone");
        newPerson.getContactMethods().add(newPhone);

        AcmDiff diff = acmDiffService.compareObjects(oldPerson, newPerson);


        assertEquals("person", diff.getChangesAsTree().getPath());
    }
}