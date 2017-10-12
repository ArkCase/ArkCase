package com.armedia.acm.plugins.person.service;

import static org.hamcrest.CoreMatchers.is;
import com.armedia.acm.plugins.addressable.model.ContactMethod;
import com.armedia.acm.plugins.addressable.model.PostalAddress;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileVersion;
import com.armedia.acm.plugins.ecm.service.EcmFileToSolrTransformer;
import com.armedia.acm.plugins.person.model.Identification;
import com.armedia.acm.plugins.person.model.Organization;
import com.armedia.acm.plugins.person.model.Person;
import com.armedia.acm.plugins.person.model.PersonOrganizationAssociation;
import com.armedia.acm.services.dataaccess.service.SearchAccessControlFields;
import com.armedia.acm.services.participants.model.AcmAssignedObject;
import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
import com.armedia.acm.services.search.model.solr.SolrBaseDocument;
import com.armedia.acm.services.search.model.solr.SolrDocument;
import com.armedia.acm.services.tag.model.AcmAssociatedTag;
import com.armedia.acm.services.tag.model.AcmTag;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmUser;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

/**
 * Created by dimitar.stefanovski on 10/12/2017.
 */
public class OrganizationToSolrTransformerTest extends EasyMockSupport
{
    private Organization in;


    private AcmUser creator;
    private AcmUser modifier;
    private SearchAccessControlFields mockSearchAccessControlFields;
    private UserDao mockUserDao;
    private OrganizationToSolrTransformer unit;

    @Before
    public void setUp() throws Exception
    {
        in = new Organization();
        creator = new AcmUser();
        creator.setUserId("creator");
        creator.setFirstName("Create");
        creator.setLastName("Creator");

        modifier = new AcmUser();
        modifier.setUserId("modifier");
        modifier.setFirstName("Modify");
        modifier.setLastName("Modifier");

        setupOrganization(in);
        mockSearchAccessControlFields = createMock(SearchAccessControlFields.class);
        mockUserDao = createMock(UserDao.class);

        unit = new OrganizationToSolrTransformer();
        unit.setSearchAccessControlFields(mockSearchAccessControlFields);
        unit.setUserDao(mockUserDao);
    }

    private void setupOrganization(Organization in)
    {
        ZonedDateTime nowUTC = ZonedDateTime.now(ZoneOffset.UTC);
        Date now = Date.from(nowUTC.toInstant());

        String userId = "user";

        in.setOrganizationId(222L);
        in.setOrganizationType("Non-profit");
        in.setOrganizationValue("Organization50");
        in.setCreated(now);
        in.setCreator("creator");
        in.setObjectType("ORGANIZATION");
        in.setModified(now);
        in.setModifier("modifier");

        Identification identification = new Identification();
            identification.setIdentificationID(222L);
            identification.setIdentificationType("DUNS");
            identification.setIdentificationNumber("123");
            identification.setCreated(now);
            identification.setCreator("creator");
            identification.setModified(now);
            identification.setModifier("modifier");

        List<Identification> identifications = new ArrayList<>();
            identifications.add(identification);

        in.setIdentifications(identifications);

        PostalAddress postalAddress = new PostalAddress();
            postalAddress.setId(223L);
            postalAddress.setCreated(now);
            postalAddress.setCreator(userId);
            postalAddress.setModified(now);
            postalAddress.setModifier(userId);
            postalAddress.setType("Home");
            postalAddress.setStreetAddress("Address1");
            postalAddress.setCity("Skopje");
            postalAddress.setState("Karposh");
            postalAddress.setContactMethods(new ArrayList<ContactMethod>());
            postalAddress.setCountry("MK");
            postalAddress.setStatus("Active");

        List<PostalAddress> postalAddresses = new ArrayList<>();
            postalAddresses.add(postalAddress);

        in.setAddresses(postalAddresses);

        /*PostalAddress postalAddressPerson = new PostalAddress();
            postalAddressPerson.setId(6L);
            postalAddressPerson.setCreated(now);
            postalAddressPerson.setCreator(userId);
            postalAddressPerson.setModified(now);
            postalAddressPerson.setModifier(userId);
            postalAddressPerson.setType("Home");
            postalAddressPerson.setStreetAddress("address 1");
            postalAddressPerson.setCity("Skopje");
            postalAddressPerson.setState("Karposh");
            postalAddressPerson.setContactMethods(new ArrayList<ContactMethod>());
            postalAddressPerson.setCountry("MK");
            postalAddressPerson.setStatus("Active");

        List<PostalAddress> postalAddressesPerson = new ArrayList<>();
            postalAddresses.add(postalAddressPerson);*/

        Person person = new Person();
            person.setId(131L);
            person.setStatus("ACTIVE");
            person.setGivenName("Test5");
            person.setFamilyName("Test5");
            person.setCreated(now);
            person.setCreator(userId);
            person.setModified(now);
            person.setModifier(userId);
            person.setAddresses(postalAddresses);

        PersonOrganizationAssociation personOrganizationAssociation = new PersonOrganizationAssociation();
            personOrganizationAssociation.setId(225L);
    }

    @Test
    public void toContentFileIndex() throws Exception
    {
//        assertNull(in);
    }

    @Test
    public void toSolrAdvancedSearch() throws Exception
    {
        /*mockSearchAccessControlFields.setAccessControlFields(anyObject(SolrBaseDocument.class), anyObject(AcmAssignedObject.class));
        expectLastCall();

        expect(mockUserDao.quietFindByUserId(eq("user"))).andReturn(user).times(2);
        expect(mockUserDao.quietFindByUserId(null)).andReturn(null);

        replayAll();
        SolrAdvancedSearchDocument result = unit.toSolrAdvancedSearch(in);
        verifyAll();

        validateResult(result);*/
    }

    @Test
    public void toSolrQuickSearch() throws Exception
    {
        // given
         mockSearchAccessControlFields.setAccessControlFields(anyObject(SolrBaseDocument.class), anyObject(AcmAssignedObject.class));
        expectLastCall();
        expect(mockUserDao.quietFindByUserId("creator")).andReturn(creator);
        expect(mockUserDao.quietFindByUserId("modifier")).andReturn(modifier);

        replayAll();
        // when
        SolrDocument result = unit.toSolrQuickSearch(in);

        verifyAll();

        // then
        validateResult(result);

    }

    private void validateResult(SolrAdvancedSearchDocument result)
    {
//        assertNotNull(result);
//        assertEquals(result.getEcmFileId(), String.valueOf(in.getVersionSeriesId()));
//        assertEquals("101-FILE", result.getId());
//        assertEquals(String.valueOf(in.getFileId()), result.getObject_id_s());
//        assertEquals(in.getObjectType(), result.getObject_type_s());
//        assertEquals(in.getFileName(), result.getName());
//        assertEquals(in.getFileActiveVersionNameExtension(), result.getExt_s());
//        assertEquals(in.getFileActiveVersionMimeType(), result.getMime_type_s());
//        assertEquals(in.getCreated(), result.getCreate_date_tdt());
//        assertEquals(in.getCreator(), result.getCreator_lcs());
//        assertEquals(in.getModified(), result.getModified_date_tdt());
//        assertEquals(in.getModifier(), result.getModifier_lcs());
//        assertEquals(in.getFileName(), result.getTitle_parseable());
//        assertEquals(in.getFileName(), result.getTitle_parseable_lcs());
//        assertEquals(in.getStatus(), result.getStatus_lcs());
//        assertEquals(in.getFileType(), result.getType_lcs());
//        assertEquals(String.valueOf(in.getParentObjectId()), result.getParent_id_s());
//        assertEquals(in.getParentObjectType(), result.getParent_type_s());
//        assertEquals(9, result.getAdditionalProperties().size());
    }

    private void validateResult(SolrDocument result)
    {
        assertNotNull(result);
        assertThat(result.getId(), is(in.getId().toString()+"-ORGANIZATION"));
        assertThat(result.getObject_id_s(), is(in.getId().toString()));
        assertThat(result.getObject_type_s(), is(in.getObjectType()));
        assertThat(result.getCreate_tdt(), is(in.getCreated()));
        assertThat(result.getAuthor_s(), is(in.getCreator()));
        assertThat(result.getLast_modified_tdt(), is(in.getModified()));
        assertThat(result.getModifier_s(), is(in.getModifier()));
        assertThat(result.getType_s(), is(in.getOrganizationType()));
        assertThat(result.getData_s(), is(in.getOrganizationValue()));
        assertThat(result.getName(), is(in.getOrganizationValue()));
        assertThat(result.getTitle_parseable(), is(in.getOrganizationValue()));
        assertThat(result.getTitle_parseable_lcs(), is(in.getOrganizationValue()));

        assertThat(result.getAdditionalProperties().get("creator_full_name_lcs"), is("Create Creator"));

        assertThat(result.getAdditionalProperties().get("modifier_full_name_lcs"), is("Modify Modifier"));
//        assertEquals(in., result.);
    }

}