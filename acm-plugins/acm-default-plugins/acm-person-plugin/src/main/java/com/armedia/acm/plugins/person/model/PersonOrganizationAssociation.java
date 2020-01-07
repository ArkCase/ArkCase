package com.armedia.acm.plugins.person.model;

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

import com.armedia.acm.data.AcmEntity;
import com.armedia.acm.data.converter.BooleanToStringConverter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.voodoodyne.jackson.jsog.JSOGGenerator;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by nebojsha on 5/22/17.
 */
@Entity
@Table(name = "acm_person_org_assoc")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "cm_class_name", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("com.armedia.acm.plugins.person.model.PersonOrganizationAssociation")
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "className", defaultImpl = PersonOrganizationAssociation.class)
@JsonIdentityInfo(generator = JSOGGenerator.class)
public class PersonOrganizationAssociation implements Serializable, AcmEntity
{
    private static final long serialVersionUID = 7413755227864370548L;
    private transient final Logger log = LogManager.getLogger(getClass());

    @Id
    @TableGenerator(name = "acm_person_org_assoc_gen", table = "acm_person_org_assoc_id", pkColumnName = "cm_seq_name", valueColumnName = "cm_seq_num", pkColumnValue = "acm_person_org_assoc", initialValue = 100, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "acm_person_org_assoc_gen")
    @Column(name = "cm_id")
    private Long id;

    @ManyToOne(cascade = { CascadeType.REFRESH, CascadeType.DETACH, CascadeType.PERSIST }, optional = false)
    @JoinColumn(name = "cm_person_id", nullable = false)
    private Person person;

    @ManyToOne(cascade = { CascadeType.REFRESH, CascadeType.DETACH, CascadeType.PERSIST }, optional = false)
    @JoinColumn(name = "cm_organization_id", nullable = false)
    private Organization organization;

    @Column(name = "cm_description")
    private String description;

    @Column(name = "cm_person_to_org_assoc_type")
    private String personToOrganizationAssociationType;

    @Column(name = "cm_org_to_person_assoc_type")
    private String organizationToPersonAssociationType;

    @Column(name = "cm_primary_contact")
    @Convert(converter = BooleanToStringConverter.class)
    private boolean primaryContact = Boolean.FALSE;

    @Column(name = "cm_default_organization")
    @Convert(converter = BooleanToStringConverter.class)
    private boolean defaultOrganization = Boolean.FALSE;

    @Column(name = "cm_created", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @Column(name = "cm_creator", updatable = false)
    private String creator;

    @Column(name = "cm_modified", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date modified;

    @Column(name = "cm_modifier")
    private String modifier;

    @Column(name = "cm_class_name")
    private String className = this.getClass().getName();

    @Column(name = "cm_object_type", updatable = false)
    private String objectType = PersonOrganizationConstants.PERSON_ORGANIZATION_ASSOCIATION_OBJECT_TYPE;

    public String getPersonToOrganizationAssociationType()
    {
        return personToOrganizationAssociationType;
    }

    public void setPersonToOrganizationAssociationType(String personToOrganizationAssociationType)
    {
        this.personToOrganizationAssociationType = personToOrganizationAssociationType;
    }

    public String getOrganizationToPersonAssociationType()
    {
        return organizationToPersonAssociationType;
    }

    public void setOrganizationToPersonAssociationType(String organizationToPersonAssociationType)
    {
        this.organizationToPersonAssociationType = organizationToPersonAssociationType;
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Person getPerson()
    {
        return person;
    }

    public void setPerson(Person person)
    {
        this.person = person;
    }

    public Organization getOrganization()
    {
        return organization;
    }

    public void setOrganization(Organization organization)
    {
        this.organization = organization;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    @Override
    public Date getCreated()
    {
        return created;
    }

    @Override
    public void setCreated(Date created)
    {
        this.created = created;
    }

    @Override
    public String getCreator()
    {
        return creator;
    }

    @Override
    public void setCreator(String creator)
    {
        this.creator = creator;
    }

    @Override
    public Date getModified()
    {
        return modified;
    }

    @Override
    public void setModified(Date modified)
    {
        this.modified = modified;
    }

    @Override
    public String getModifier()
    {
        return modifier;
    }

    @Override
    public void setModifier(String modifier)
    {
        this.modifier = modifier;
    }

    public String getClassName()
    {
        return className;
    }

    public void setClassName(String className)
    {
        this.className = className;
    }

    public String getObjectType()
    {
        return objectType;
    }

    public boolean isPrimaryContact()
    {
        return primaryContact;
    }

    public void setPrimaryContact(boolean primaryContact)
    {
        this.primaryContact = primaryContact;
    }

    public boolean isDefaultOrganization()
    {
        return defaultOrganization;
    }

    public void setDefaultOrganization(boolean defaultOrganization)
    {
        this.defaultOrganization = defaultOrganization;
    }
}
