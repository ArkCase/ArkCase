package com.armedia.acm.objectdiff.service;

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

import java.util.ArrayList;
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

    @Before
    public void setUp()
    {
        assertNotNull(diffService);

        oldPerson = new TestPerson();

        oldPerson.setId(1L);
        oldPerson.setLastName("Doe");
        oldPerson.setName("John");
        oldPerson.setToBeIgnored("This field is ignored");

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

        AcmDiff diff = diffService.compareObjects(oldPerson, newPerson);
        assertEquals(2, diff.getChangesAsList().size());

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