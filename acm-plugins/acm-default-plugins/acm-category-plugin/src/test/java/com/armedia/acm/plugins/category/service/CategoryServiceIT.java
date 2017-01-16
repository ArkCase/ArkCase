package com.armedia.acm.plugins.category.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.plugins.category.model.Category;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.Date;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/spring/spring-library-category.xml", "/spring/spring-library-data-source.xml",
        "/spring/spring-library-user-service.xml", "/spring/spring-library-context-holder.xml", "/spring/spring-library-acm-encryption.xml",
        "/spring/spring-library-property-file-manager.xml", "/spring/spring-library-object-lock.xml", "/spring/spring-library-search.xml",
        "/spring/spring-library-category-test.xml"})
@TransactionConfiguration(defaultRollback = false)
public class CategoryServiceIT
{
    @Autowired
    private CategoryService categoryService;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private AuditPropertyEntityAdapter auditAdapter;

    private Category parent;

    private Long parentId;

    private Category child;

    @Before
    public void setUp() throws Exception
    {

        auditAdapter.setUserId("creator");

        Date created = new Date();
        parent = new Category();
        parent.setName("parent");
        parent.setCreator("creator");
        parent.setCreated(created);
        parent.setModifier("creator");
        parent.setModified(created);
        parent = categoryService.create(parent);
        parentId = parent.getId();

        child = new Category();
        child.setName("child");
        child.setCreator("creator");
        child.setCreated(created);
        child.setModifier("creator");
        child.setModified(created);
        child.setParent(parent);
        categoryService.create(child);
    }

    @Test
    public void testGetChildren() throws Exception
    {
        assertNotNull(categoryService);
        assertNotNull(entityManager);

        Category savedParent = categoryService.get(parentId);
        List<Category> children = savedParent.getChildren();
        assertNotNull(children);
        assertTrue(!children.isEmpty());

    }

}
