package com.armedia.acm.plugins.category.service;

import static com.armedia.acm.plugins.category.model.CategoryStatus.ACTIVATED;
import static com.armedia.acm.plugins.category.model.CategoryStatus.DEACTIVATED;
import static com.armedia.acm.plugins.category.model.CategoryStatus.DELETED;
import static org.easymock.EasyMock.expect;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.powermock.api.easymock.PowerMock.mockStatic;
import static org.powermock.api.easymock.PowerMock.replay;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUpdateObjectFailedException;
import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.plugins.category.model.Category;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.annotation.Mock;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.Date;
import java.util.List;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/spring-library-category.xml", "/spring/spring-library-data-source.xml",
        "/spring/spring-library-user-service.xml", "/spring/spring-library-context-holder.xml", "/spring/spring-library-acm-encryption.xml",
        "/spring/spring-library-property-file-manager.xml", "/spring/spring-library-object-lock.xml", "/spring/spring-library-search.xml",
        "/spring/spring-library-category-test.xml" })
@TransactionConfiguration(defaultRollback = true)
@Transactional
@PrepareForTest(LoggerFactory.class)
@PowerMockIgnore({ "javax.management.*", "javax.net.ssl.*" })
@Ignore
public class CategoryServiceIT
{
    @Autowired
    private CategoryService categoryService;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private AuditPropertyEntityAdapter auditAdapter;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock
    private Logger mockedLogger;

    // private Category parent;

    private Long parentId;
    private Long childId;
    private Long grandChildId;

    // private Category child;

    // private Category grandChild;

    @Before
    public void setUp() throws Exception
    {
        assertNotNull(categoryService);
        assertNotNull(entityManager);
        assertNotNull(auditAdapter);

        mockStatic(LoggerFactory.class);
        expect(LoggerFactory.getLogger(CategoryServiceImpl.class)).andReturn(mockedLogger);
        replay(LoggerFactory.class);

        auditAdapter.setUserId("creator");

        Date created = new Date();
        Category parent = new Category();
        parent.setName("parent");
        parent.setDescription("parent");
        parent.setCreator("creator");
        parent.setCreated(created);
        parent.setModifier("creator");
        parent.setModified(created);
        parent.setStatus(DEACTIVATED);
        parent = categoryService.create(parent);
        parentId = parent.getId();

        Category child = new Category();
        child.setName("child");
        child.setDescription("child");
        child.setCreator("creator");
        child.setCreated(created);
        child.setModifier("creator");
        child.setModified(created);
        child.setStatus(DEACTIVATED);
        child.setParent(parent);
        child = categoryService.create(child);
        childId = child.getId();

        Category grandChild = new Category();
        grandChild.setName("grandChild");
        grandChild.setDescription("grandChild");
        grandChild.setCreator("creator");
        grandChild.setCreated(created);
        grandChild.setModifier("creator");
        grandChild.setModified(created);
        grandChild.setParent(child);
        grandChild.setStatus(DEACTIVATED);
        grandChild = categoryService.create(grandChild);
        grandChildId = grandChild.getId();

    }

    // @AfterTransaction
    public void cleanUp() throws Exception
    {
        entityManager.remove(categoryService.get(parentId));
        entityManager.remove(categoryService.get(childId));
        entityManager.remove(categoryService.get(grandChildId));
    }

    @Test
    public void testGetNonExistingCategory() throws Exception
    {
        Long id = 0l;
        expectedException.expect(AcmObjectNotFoundException.class);
        expectedException.expectMessage(String.format("Category with id %n not found.", id));

        categoryService.get(id);
    }

    @Test
    public void testGetCategory() throws Exception
    {
        Category retreived = categoryService.get(parentId);
        assertNotNull(retreived);
        assertThat(retreived.getId(), is(parentId));
    }

    @Test
    public void testCreateCategoryInvalidArgument() throws Exception
    {
        expectedException.expect(AcmCreateObjectFailedException.class);
        expectedException.expectMessage("Argument was 'null'.");

        categoryService.create(null);
    }

    @Test
    public void testCreateCategoryWithNameCollision() throws Exception
    {
        Category coliding = new Category();
        coliding.setName("parent");

        expectedException.expect(AcmCreateObjectFailedException.class);
        expectedException.expectMessage(String.format("Category with [%s] name already exists on this level.", coliding.getName()));

        categoryService.create(coliding);
    }

    @Test
    public void testCreateCategory() throws Exception
    {
        Category noCollision = new Category();
        noCollision.setName("no collision");

        Category created = categoryService.create(noCollision);
        assertNotNull(created);
        assertThat(created.getName(), is("no collision"));
        assertNotNull(created.getId());
    }

    @Test
    public void testUpdateCategortyNullArgument() throws Exception
    {
        expectedException.expect(AcmUpdateObjectFailedException.class);
        expectedException.expectMessage("Argument was 'null' or withoud an 'id'.");

        categoryService.update(null);
    }

    @Test
    public void testUpdateCategotyNonExistingCategoryArgument() throws Exception
    {
        Category nonExisting = new Category();

        expectedException.expect(AcmUpdateObjectFailedException.class);
        expectedException.expectMessage("Argument was 'null' or withoud an 'id'.");
        try
        {
            categoryService.update(nonExisting);
        } finally
        {

        }
    }

    @Test
    public void testUpdateCategoryColidingName() throws Exception
    {
        Category nonColiding = new Category();
        nonColiding.setName("non coliding");
        nonColiding = categoryService.create(nonColiding);

        Category coliding = new Category();
        coliding.setId(nonColiding.getId());
        coliding.setName("parent");

        expectedException.expect(AcmUpdateObjectFailedException.class);
        expectedException.expectMessage(String.format("Category with [%s] name already exists on this level.", coliding.getName()));

        categoryService.update(coliding);
    }

    @Test
    public void testUpdateCategory() throws Exception
    {
        Category newCategory = new Category();
        newCategory.setName("name");
        newCategory.setDescription("description");
        newCategory = categoryService.create(newCategory);
        assertNotNull(newCategory.getId());
        assertThat(newCategory.getName(), is("name"));
        assertThat(newCategory.getDescription(), is("description"));

        Category updatingCategory = new Category();
        updatingCategory.setId(newCategory.getId());
        updatingCategory.setName("updated name");
        updatingCategory.setDescription("updated description");

        updatingCategory = categoryService.update(updatingCategory);

        assertNotNull(updatingCategory);
        assertNotNull(updatingCategory.getId());
        assertThat(updatingCategory.getName(), is("updated name"));
        assertThat(updatingCategory.getDescription(), is("updated description"));

    }

    @Test
    public void testDeleteCategoryInvalidArgument() throws Exception
    {
        expectedException.expect(AcmObjectNotFoundException.class);
        expectedException.expectMessage("Argument was 'null'.");

        categoryService.delete(null);
    }

    @Test
    public void testDeleteCategory() throws Exception
    {
        Category parent = categoryService.get(parentId);
        parent.setStatus(ACTIVATED);
        parent = categoryService.update(parent);
        Category child = categoryService.get(childId);
        child.setStatus(ACTIVATED);
        child = categoryService.update(child);
        Category grandChild = categoryService.get(grandChildId);
        grandChild.setStatus(ACTIVATED);
        grandChild = categoryService.update(grandChild);

        assertThat(parent.getStatus(), is(ACTIVATED));
        assertThat(child.getStatus(), is(ACTIVATED));
        assertThat(grandChild.getStatus(), is(ACTIVATED));

        child = categoryService.delete(child.getId());

        assertThat(parent.getStatus(), is(ACTIVATED));
        assertThat(child.getStatus(), is(DELETED));
        assertThat(grandChild.getStatus(), is(DELETED));
    }

    @Test
    public void testActivateCategoryNullArgument() throws Exception
    {
        expectedException.expect(AcmObjectNotFoundException.class);
        expectedException.expectMessage("Argument was 'null' or withoud an 'id'.");

        categoryService.activate(null, false);
    }

    @Test
    public void testActivateCategoryNonExistingCategoryArgument() throws Exception
    {
        Category nonExisting = new Category();

        expectedException.expect(AcmObjectNotFoundException.class);
        expectedException.expectMessage("Argument was 'null' or withoud an 'id'.");
        try
        {
            categoryService.activate(nonExisting.getId(), false);
        } finally
        {

        }
    }

    @Test
    public void testActivateCategory() throws Exception
    {

        Category parent = categoryService.get(parentId);
        // parent = entityManager.merge(parent);
        Category child = categoryService.get(childId);
        // child = entityManager.merge(child);
        Category grandChild = categoryService.get(grandChildId);
        // grandChild = entityManager.merge(grandChild);

        assertThat(parent.getStatus(), is(DEACTIVATED));
        assertThat(child.getStatus(), is(DEACTIVATED));
        assertThat(grandChild.getStatus(), is(DEACTIVATED));

        categoryService.activate(child.getId(), false);

        assertThat(parent.getStatus(), is(ACTIVATED));
        assertThat(child.getStatus(), is(ACTIVATED));
        assertThat(grandChild.getStatus(), is(DEACTIVATED));

    }

    @Test
    public void testDectivateCategoryNullArgument() throws Exception
    {
        expectedException.expect(AcmObjectNotFoundException.class);
        expectedException.expectMessage("Argument was 'null' or withoud an 'id'.");

        categoryService.deactivate(null);
    }

    @Test
    public void testDectivateCategoryNonExistingCategoryArgument() throws Exception
    {
        Category nonExisting = new Category();

        expectedException.expect(AcmObjectNotFoundException.class);
        expectedException.expectMessage("Argument was 'null' or withoud an 'id'.");
        try
        {
            categoryService.deactivate(nonExisting.getId());
        } finally
        {

        }
    }

    @Test
    @Ignore
    public void testDectivateCategory() throws Exception
    {
        Category parent = categoryService.get(parentId);
        Category child = categoryService.get(childId);
        Category grandChild = categoryService.get(grandChildId);

        assertThat(parent.getStatus(), is(DEACTIVATED));
        assertThat(child.getStatus(), is(DEACTIVATED));
        assertThat(grandChild.getStatus(), is(DEACTIVATED));

        categoryService.deactivate(child.getId());

        assertThat(parent.getStatus(), is(ACTIVATED));
        assertThat(child.getStatus(), is(ACTIVATED));
        assertThat(grandChild.getStatus(), is(DEACTIVATED));

    }

    @Test
    @Ignore
    public void testGetChildren() throws Exception
    {
        Category parent = categoryService.get(parentId);
        Category child = categoryService.get(childId);

        List<Category> children = parent.getChildren();
        assertNotNull(children);
        assertTrue(!children.isEmpty());
        assertThat(children.get(0), is(child));
    }

}
