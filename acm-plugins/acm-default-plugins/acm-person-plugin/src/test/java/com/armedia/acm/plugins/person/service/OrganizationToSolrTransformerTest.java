package com.armedia.acm.plugins.person.service;

/*-
 * #%L
 * ACM Default Plugin: Person
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

import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.ACM_PARTICIPANTS_LCS;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.CREATOR_FULL_NAME_LCS;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.MODIFIER_FULL_NAME_LCS;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.STATUS_LCS;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.TITLE_PARSEABLE;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.TITLE_PARSEABLE_LCS;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.TYPE_LCS;
import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import com.armedia.acm.plugins.addressable.model.PostalAddress;
import com.armedia.acm.plugins.person.model.Identification;
import com.armedia.acm.plugins.person.model.Organization;
import com.armedia.acm.plugins.person.model.Person;
import com.armedia.acm.plugins.person.model.PersonOrganizationAssociation;
import com.armedia.acm.services.dataaccess.service.SearchAccessControlFields;
import com.armedia.acm.services.participants.model.AcmAssignedObject;
import com.armedia.acm.services.participants.model.AcmParticipant;
import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
import com.armedia.acm.services.search.model.solr.SolrBaseDocument;
import com.armedia.acm.services.search.model.solr.SolrContentDocument;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmUser;

import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
        setupCreator(creator);

        modifier = new AcmUser();
        setupModifier(modifier);

        setupOrganization(in);
        mockSearchAccessControlFields = createMock(SearchAccessControlFields.class);
        mockUserDao = createMock(UserDao.class);

        unit = new OrganizationToSolrTransformer();
        unit.setSearchAccessControlFields(mockSearchAccessControlFields);
        unit.setUserDao(mockUserDao);
    }

    private void setupModifier(AcmUser modifier)
    {
        modifier.setUserId("modifier");
        modifier.setFirstName("Modify");
        modifier.setLastName("Modifier");
    }

    private void setupCreator(AcmUser creator)
    {
        creator.setUserId("creator");
        creator.setFirstName("Create");
        creator.setLastName("Creator");
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

        /* Set Identifications */
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

        /* Set Postal addresses */
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
        postalAddress.setContactMethods(new ArrayList<>());
        postalAddress.setCountry("MK");
        postalAddress.setStatus("Active");

        List<PostalAddress> postalAddresses = new ArrayList<>();
        postalAddresses.add(postalAddress);

        in.setAddresses(postalAddresses);

        /* Set Participants */
        AcmParticipant acmParticipant = new AcmParticipant();
        acmParticipant.setId(222L);
        acmParticipant.setObjectType("objectTpye");
        acmParticipant.setObjectId(223L);
        acmParticipant.setParticipantType("participantType");
        acmParticipant.setParticipantLdapId("ldapType");

        List<AcmParticipant> acmParticipants = new ArrayList<>();
        acmParticipants.add(acmParticipant);

        in.setParticipants(acmParticipants);

    }

    private void setupOrganizationPersonAssociationPrimaryContactWithGivenName(Organization in)
    {
        ZonedDateTime nowUTC = ZonedDateTime.now(ZoneOffset.UTC);
        Date now = Date.from(nowUTC.toInstant());
        String userId = "user";
        this.in.setPersonAssociations(new ArrayList<>());

        /* Set PersonAssociations */
        Person person = new Person();
        person.setId(131L);
        person.setStatus("ACTIVE");
        person.setGivenName("Test5");
        person.setFamilyName("");
        person.setCreated(now);
        person.setCreator(userId);
        person.setModified(now);
        person.setModifier(userId);

        PersonOrganizationAssociation personOrganizationAssociation = new PersonOrganizationAssociation();
        personOrganizationAssociation.setId(225L);
        personOrganizationAssociation.setPerson(person);
        personOrganizationAssociation.setDescription("description");
        personOrganizationAssociation.setPrimaryContact(true);

        List<PersonOrganizationAssociation> personAssociations = new ArrayList<>();
        personAssociations.add(personOrganizationAssociation);

        this.in.setPersonAssociations(personAssociations);
    }

    private void setupOrganizationPersonAssociationPrimaryContactWithFamilyName(Organization in)
    {
        ZonedDateTime nowUTC = ZonedDateTime.now(ZoneOffset.UTC);
        Date now = Date.from(nowUTC.toInstant());
        String userId = "user";
        this.in.setPersonAssociations(new ArrayList<>());

        /* Set PersonAssociations */
        Person person = new Person();
        person.setId(131L);
        person.setStatus("ACTIVE");
        person.setGivenName("");
        person.setFamilyName("Test6");
        person.setCreated(now);
        person.setCreator(userId);
        person.setModified(now);
        person.setModifier(userId);

        PersonOrganizationAssociation personOrganizationAssociation = new PersonOrganizationAssociation();
        personOrganizationAssociation.setId(225L);
        personOrganizationAssociation.setPerson(person);
        personOrganizationAssociation.setDescription("description");
        personOrganizationAssociation.setPrimaryContact(true);

        List<PersonOrganizationAssociation> personAssociations = new ArrayList<>();
        personAssociations.add(personOrganizationAssociation);

        this.in.setPersonAssociations(personAssociations);
    }

    private void setupOrganizationPersonAssociationPrimaryContactWithFullName(Organization in)
    {
        ZonedDateTime nowUTC = ZonedDateTime.now(ZoneOffset.UTC);
        Date now = Date.from(nowUTC.toInstant());
        String userId = "user";
        this.in.setPersonAssociations(new ArrayList<>());

        /* Set PersonAssociations */
        Person person = new Person();
        person.setId(131L);
        person.setStatus("ACTIVE");
        person.setGivenName("Test5");
        person.setFamilyName("Test6");
        person.setCreated(now);
        person.setCreator(userId);
        person.setModified(now);
        person.setModifier(userId);

        PersonOrganizationAssociation personOrganizationAssociation = new PersonOrganizationAssociation();
        personOrganizationAssociation.setId(225L);
        personOrganizationAssociation.setPerson(person);
        personOrganizationAssociation.setDescription("description");
        personOrganizationAssociation.setPrimaryContact(true);

        List<PersonOrganizationAssociation> personAssociations = new ArrayList<>();
        personAssociations.add(personOrganizationAssociation);

        this.in.setPersonAssociations(personAssociations);
    }

    @Test
    public void toContentFileIndex() throws Exception
    {
        // given

        // when
        SolrContentDocument result = unit.toContentFileIndex(in);

        // then
        assertNull(result);
    }

    @Test
    public void toSolrAdvancedSearch() throws Exception
    {
        // given
        mockSearchAccessControlFields.setAccessControlFields(anyObject(SolrBaseDocument.class), anyObject(AcmAssignedObject.class));
        expectLastCall();
        expect(mockUserDao.quietFindByUserId("creator")).andReturn(creator);
        expect(mockUserDao.quietFindByUserId("modifier")).andReturn(modifier);
        replayAll();

        // when
        SolrAdvancedSearchDocument result = unit.toSolrAdvancedSearch(in);
        verifyAll();

        // then
        validateResult(result);
    }

    @Test
    public void toSolrAdvancedSearch_primaryContactWithGivenName() throws Exception
    {
        // given
        setupOrganizationPersonAssociationPrimaryContactWithGivenName(in);
        mockSearchAccessControlFields.setAccessControlFields(anyObject(SolrBaseDocument.class), anyObject(AcmAssignedObject.class));
        expectLastCall();
        expect(mockUserDao.quietFindByUserId("creator")).andReturn(creator);
        expect(mockUserDao.quietFindByUserId("modifier")).andReturn(modifier);
        replayAll();

        // when
        SolrAdvancedSearchDocument result = unit.toSolrAdvancedSearch(in);
        verifyAll();

        // then
        validateResultToSolrAdvancedSerach_primaryContactWithGivenName(result);
    }

    @Test
    public void toSolrAdvancedSearch_primaryContactWithFamilyName() throws Exception
    {
        // given
        setupOrganizationPersonAssociationPrimaryContactWithFamilyName(in);
        mockSearchAccessControlFields.setAccessControlFields(anyObject(SolrBaseDocument.class), anyObject(AcmAssignedObject.class));
        expectLastCall();
        expect(mockUserDao.quietFindByUserId("creator")).andReturn(creator);
        expect(mockUserDao.quietFindByUserId("modifier")).andReturn(modifier);
        replayAll();

        // when
        SolrAdvancedSearchDocument result = unit.toSolrAdvancedSearch(in);
        verifyAll();

        // then
        validateResultToSolrAdvancedSerach_primaryContactWithFamilyName(result);
    }

    @Test
    public void toSolrAdvancedSearch_primaryContactWithFullName() throws Exception
    {
        // given
        setupOrganizationPersonAssociationPrimaryContactWithFullName(in);
        mockSearchAccessControlFields.setAccessControlFields(anyObject(SolrBaseDocument.class), anyObject(AcmAssignedObject.class));
        expectLastCall();
        expect(mockUserDao.quietFindByUserId("creator")).andReturn(creator);
        expect(mockUserDao.quietFindByUserId("modifier")).andReturn(modifier);
        replayAll();

        // when
        SolrAdvancedSearchDocument result = unit.toSolrAdvancedSearch(in);
        verifyAll();

        // then
        validateResultToSolrAdvancedSerach_primaryContactWithFullName(result);
    }

    private void validateResultToSolrAdvancedSerach_primaryContactWithGivenName(SolrAdvancedSearchDocument result)
    {
        assertThat(result.getAdditionalProperties().get("primary_contact_s"), is("Test5"));
        assertNull(result.getAdditionalProperties().get("default_phone_s"));
        assertNull(result.getAdditionalProperties().get("default_location_s"));
        assertNull(result.getAdditionalProperties().get("default_identification_s"));
    }

    private void validateResultToSolrAdvancedSerach_primaryContactWithFamilyName(SolrAdvancedSearchDocument result)
    {
        assertThat(result.getAdditionalProperties().get("primary_contact_s"), is("Test6"));
        assertNull(result.getAdditionalProperties().get("default_phone_s"));
        assertNull(result.getAdditionalProperties().get("default_location_s"));
        assertNull(result.getAdditionalProperties().get("default_identification_s"));
    }

    private void validateResultToSolrAdvancedSerach_primaryContactWithFullName(SolrAdvancedSearchDocument result)
    {
        assertThat(result.getAdditionalProperties().get("primary_contact_s"), is("Test5 Test6"));
        assertNull(result.getAdditionalProperties().get("default_phone_s"));
        assertNull(result.getAdditionalProperties().get("default_location_s"));
        assertNull(result.getAdditionalProperties().get("default_identification_s"));
    }

    private void validateResult(SolrAdvancedSearchDocument result)
    {
        assertNotNull(result);
        assertThat(result.getId(), is(in.getId().toString() + "-ORGANIZATION"));
        assertThat(result.getObject_id_s(), is(in.getId().toString()));
        assertThat(result.getObject_type_s(), is(in.getObjectType()));
        assertThat(result.getCreate_date_tdt(), is(in.getCreated()));
        assertThat(result.getCreator_lcs(), is(in.getCreator()));
        assertThat(result.getModified_date_tdt(), is(in.getModified()));
        assertThat(result.getModifier_lcs(), is(in.getModifier()));
        assertThat(result.getAdditionalProperties().get(TYPE_LCS), is(in.getOrganizationType()));
        assertThat(result.getName(), is(in.getOrganizationValue()));
        assertThat(result.getAdditionalProperties().get(TITLE_PARSEABLE), is(in.getOrganizationValue()));
        assertThat(result.getAdditionalProperties().get(TITLE_PARSEABLE_LCS), is(in.getOrganizationValue()));
        assertThat(result.getAdditionalProperties().get(STATUS_LCS), is(in.getStatus()));
        assertThat(result.getAdditionalProperties().get(CREATOR_FULL_NAME_LCS), is("Create Creator"));
        assertThat(result.getAdditionalProperties().get(MODIFIER_FULL_NAME_LCS), is("Modify Modifier"));
        assertThat(result.getAdditionalProperties().get(ACM_PARTICIPANTS_LCS),
                is("[{\"ldapId\":\"ldapType\", \"type\":\"participantType\"}]"));
    }
}
