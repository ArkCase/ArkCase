package com.armedia.acm.objectdiff.service;

/*-
 * #%L
 * Tool Integrations: Object Diff Util
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.armedia.acm.objectdiff.model.AcmChange;
import com.armedia.acm.objectdiff.model.AcmCollectionElementAdded;
import com.armedia.acm.objectdiff.model.AcmCollectionElementRemoved;
import com.armedia.acm.objectdiff.model.AcmDiff;
import com.armedia.acm.objectdiff.model.AcmObjectReplaced;
import com.armedia.acm.objectdiff.model.AcmValueChanged;
import com.armedia.acm.objectdiff.model.TestAttribute;
import com.armedia.acm.objectdiff.model.TestPerson;
import com.armedia.acm.objectdiff.model.interfaces.AcmChangeDisplayable;

import org.apache.commons.lang3.SerializationUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "/config/spring-library-person-diff-test.xml"
})
public class AcmDiffServiceTest
{
    private TestPerson oldPerson;
    @Autowired
    private AcmDiffService diffService;

    private Date oldDateOfBirth = Date.from(Instant.parse("2000-01-01T00:10:00.00Z"));
    private Date newDateOfBirth = Date.from(Instant.parse("2010-01-01T00:10:00.00Z"));
    private LocalDate oldEmploymentDate = LocalDate.of(2000, 1, 1);
    private LocalDate newEmploymentDate = LocalDate.of(2010, 1, 1);
    private LocalDateTime oldCompleted = LocalDateTime.of(2000, 1, 1, 0, 0);
    private LocalDateTime newCompleted = LocalDateTime.of(2010, 1, 1, 0, 0);
    private LocalTime oldAlarmTime = LocalTime.of(6, 30);
    private LocalTime newAlarmTime = LocalTime.of(7, 30);

    @Before
    public void setUp()
    {
        assertNotNull(diffService);

        oldPerson = new TestPerson();

        oldPerson.setId(1L);
        oldPerson.setLastName("Doe");
        oldPerson.setName("John");
        oldPerson.setToBeIgnored("This field is ignored");
        oldPerson.setDateOfBirth(oldDateOfBirth);
        oldPerson.setEmploymentDate(oldEmploymentDate);
        oldPerson.setCompleted(oldCompleted);
        oldPerson.setAlarmTime(oldAlarmTime);

        List<TestAttribute> attributeList = new ArrayList<>();
        // first attribute
        TestAttribute attribute = new TestAttribute();
        attribute.setId(1L);
        attribute.setValue("Value 1");
        attributeList.add(attribute);

        // second attribute
        attribute = new TestAttribute();
        attribute.setId(2L);
        attribute.setValue("Value 2");
        attributeList.add(attribute);

        // third attribute
        attribute = new TestAttribute();
        attribute.setId(3L);
        attribute.setValue("Value 3");
        attributeList.add(attribute);

        oldPerson.setAttributeList(attributeList);

        // default attribute
        attribute = new TestAttribute();
        attribute.setId(4L);
        attribute.setValue("Value 4");
        attributeList.add(attribute);
        oldPerson.setDefaultAttribute(attribute);
    }

    @Test
    public void compareObjectsNoChanges()
    {
        TestPerson newPerson = SerializationUtils.clone(oldPerson);

        AcmDiff diff = diffService.compareObjects(oldPerson, newPerson);
        assertEquals(0, diff.getChangesAsList().size());
    }

    @Test
    public void compareObjectsCollectionElementRemoved()
    {
        TestPerson newPerson = SerializationUtils.clone(oldPerson);
        newPerson.getAttributeList().remove(0);

        AcmDiff diff = diffService.compareObjects(oldPerson, newPerson);
        assertEquals(1, diff.getChangesAsList().size());
        AcmChange change = diff.getChangesAsList().get(0);
        assertTrue(change.isLeaf());
        assertTrue(change instanceof AcmCollectionElementRemoved);
        AcmChangeDisplayable displayable = (AcmChangeDisplayable) change;
        assertEquals("Value 1", displayable.getOldValue());
        assertEquals(null, displayable.getNewValue());
    }

    @Test
    public void compareObjectsCollectionElementAdded()
    {
        TestPerson newPerson = SerializationUtils.clone(oldPerson);
        TestAttribute testAttribute = new TestAttribute();
        testAttribute.setId(6L);
        testAttribute.setValue("value 6");
        newPerson.getAttributeList().add(testAttribute);

        AcmDiff diff = diffService.compareObjects(oldPerson, newPerson);
        assertEquals(1, diff.getChangesAsList().size());
        AcmChange change = diff.getChangesAsList().get(0);
        assertTrue(change.isLeaf());
        assertTrue(change instanceof AcmCollectionElementAdded);
    }

    @Test
    public void compareObjectsValueChanged()
    {
        TestPerson newPerson = SerializationUtils.clone(oldPerson);
        // change 1
        newPerson.setName("Jane");
        // change 2
        newPerson.setLastName("Doe1");
        // change 3 but should be ignored
        newPerson.setToBeIgnored(null);
        // change 4
        newPerson.setDateOfBirth(newDateOfBirth);
        // change 5
        newPerson.setEmploymentDate(newEmploymentDate);
        // change 5
        newPerson.setCompleted(newCompleted);
        // change 6
        newPerson.setAlarmTime(newAlarmTime);

        AcmDiff diff = diffService.compareObjects(oldPerson, newPerson);
        assertEquals(6, diff.getChangesAsList().size());

        for (AcmChange change : diff.getChangesAsList())
        {
            if ("person.name".equals(change.getPath()))
            {
                assertTrue(change instanceof AcmValueChanged);
                AcmValueChanged valueChanged = (AcmValueChanged) change;
                assertEquals("John", valueChanged.getOldValue());
                assertEquals("Jane", valueChanged.getNewValue());
            }
            else if ("person.lastName".equals(change.getPath()))
            {
                assertTrue(change instanceof AcmValueChanged);
                AcmValueChanged valueChanged = (AcmValueChanged) change;
                assertEquals("Doe", valueChanged.getOldValue());
                assertEquals("Doe1", valueChanged.getNewValue());
            }
            else if ("person.dateOfBirth".equals(change.getPath()))
            {
                assertTrue(change instanceof AcmValueChanged);
                AcmValueChanged valueChanged = (AcmValueChanged) change;
                assertEquals(oldDateOfBirth.toString(), valueChanged.getOldValue());
                assertEquals(newDateOfBirth.toString(), valueChanged.getNewValue());
            }
            else if ("person.employmentDate".equals(change.getPath()))
            {
                assertTrue(change instanceof AcmValueChanged);
                AcmValueChanged valueChanged = (AcmValueChanged) change;
                assertEquals(oldEmploymentDate.toString(), valueChanged.getOldValue());
                assertEquals(newEmploymentDate.toString(), valueChanged.getNewValue());
            }
            else if ("person.completed".equals(change.getPath()))
            {
                assertTrue(change instanceof AcmValueChanged);
                AcmValueChanged valueChanged = (AcmValueChanged) change;
                assertEquals(oldCompleted.toString(), valueChanged.getOldValue());
                assertEquals(newCompleted.toString(), valueChanged.getNewValue());
            }
            else if ("person.alarmTime".equals(change.getPath()))
            {
                assertTrue(change instanceof AcmValueChanged);
                AcmValueChanged valueChanged = (AcmValueChanged) change;
                assertEquals(oldAlarmTime.toString(), valueChanged.getOldValue());
                assertEquals(newAlarmTime.toString(), valueChanged.getNewValue());
            }
        }
    }

    @Test
    public void compareObjectsReplaced()
    {
        TestPerson newPerson = SerializationUtils.clone(oldPerson);
        TestAttribute defaultAttribute = new TestAttribute();
        defaultAttribute.setId(2L);
        defaultAttribute.setValue("changed value");
        newPerson.setDefaultAttribute(defaultAttribute);

        AcmDiff diff = diffService.compareObjects(oldPerson, newPerson);
        assertEquals(1, diff.getChangesAsList().size());
        AcmChange change = diff.getChangesAsList().get(0);
        assertTrue(change instanceof AcmObjectReplaced);

        AcmObjectReplaced acmObjectReplaced = (AcmObjectReplaced) change;
        assertEquals(acmObjectReplaced.getOldObject(), oldPerson.getDefaultAttribute());
        assertEquals(acmObjectReplaced.getNewObject(), defaultAttribute);

        assertEquals("Value 4", acmObjectReplaced.getOldValue());
        assertEquals("changed value", acmObjectReplaced.getNewValue());
    }

    @Test
    public void compareObjectModified()
    {
        TestPerson newPerson = SerializationUtils.clone(oldPerson);
        newPerson.getDefaultAttribute().setValue("Changed Value");

        AcmDiff diff = diffService.compareObjects(oldPerson, newPerson);
        assertEquals(1, diff.getChangesAsList().size());
        AcmChange change = diff.getChangesAsList().get(0);
        assertTrue(change instanceof AcmValueChanged);
        assertEquals("person.defaultAttribute.value", change.getPath());
    }

    @Test
    public void compareCollectionElementModified()
    {
        TestPerson newPerson = SerializationUtils.clone(oldPerson);
        newPerson.getAttributeList().get(1).setValue("Changed value");

        AcmDiff diff = diffService.compareObjects(oldPerson, newPerson);
        assertEquals(1, diff.getChangesAsList().size());
        AcmChange change = diff.getChangesAsList().get(0);
        assertTrue(change instanceof AcmValueChanged);
        assertEquals("person.attributeList.attribute.value", change.getPath());
    }

    @Test
    public void compareRootObjectsReplaced()
    {
        TestPerson newPerson = SerializationUtils.clone(oldPerson);
        newPerson.setId(2L);
        newPerson.setName("Jane");
        AcmDiff diff = diffService.compareObjects(oldPerson, newPerson);
        assertEquals(1, diff.getChangesAsList().size());
        AcmChange change = diff.getChangesAsList().get(0);
        AcmChangeDisplayable displayable = (AcmChangeDisplayable) change;
        assertEquals("Jane Doe", displayable.getNewValue());
        assertEquals("John Doe", displayable.getOldValue());
        assertEquals("person", change.getPath());
    }

    @Test
    public void compareSameObjectReference()
    {
        TestPerson newPerson = SerializationUtils.clone(oldPerson);
        newPerson.getDefaultAttribute().setValue("1231231");
        AcmDiff diff = diffService.compareObjects(oldPerson, newPerson);
        assertEquals(1, diff.getChangesAsList().size());

    }
}
