package com.armedia.acm.plugins.category.service;

import com.armedia.acm.plugins.category.dao.CategoryDao;
import com.armedia.acm.plugins.category.model.Category;
import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
import com.armedia.acm.services.search.model.solr.SolrDocument;
import com.armedia.acm.services.search.service.AcmObjectToSolrDocTransformer;
import com.armedia.acm.services.users.dao.ldap.UserDao;
import com.armedia.acm.services.users.model.AcmUser;

import java.util.Date;
import java.util.List;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Feb 13, 2017
 *
 */
public class CategoryToSolrTransformer implements AcmObjectToSolrDocTransformer<Category>
{

    private UserDao userDao;

    private CategoryDao categoryDao;

    /*
     * (non-Javadoc)
     *
     * @see
     * com.armedia.acm.services.search.service.AcmObjectToSolrDocTransformer#getObjectsModifiedSince(java.util.Date,
     * int, int)
     */
    @Override
    public List<Category> getObjectsModifiedSince(Date lastModified, int start, int pageSize)
    {
        return categoryDao.findModifiedSince(lastModified, start, pageSize);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.armedia.acm.services.search.service.AcmObjectToSolrDocTransformer#toSolrAdvancedSearch(java.lang.Object)
     */
    @Override
    public SolrAdvancedSearchDocument toSolrAdvancedSearch(Category in)
    {
        SolrAdvancedSearchDocument solr = new SolrAdvancedSearchDocument();

        solr.setId(in.getId() + "-CATEGORY");
        solr.setObject_id_s(in.getId() + "");
        solr.setObject_type_s("CATEGORY");
        solr.setTitle_parseable(in.getName());
        solr.setTitle_parseable_lcs(in.getName());
        solr.setDescription_no_html_tags_parseable(in.getDescription());
        solr.setName(in.getName());

        solr.setCreate_date_tdt(in.getCreated());
        solr.setCreator_lcs(in.getCreator());
        solr.setModified_date_tdt(in.getModified());
        solr.setModifier_lcs(in.getModifier());

        solr.setStatus_lcs(in.getStatus().name());

        /** Additional properties for full names instead of ID's */
        AcmUser creator = userDao.quietFindByUserId(in.getCreator());
        if (creator != null)
        {
            solr.setAdditionalProperty("creator_full_name_lcs", String.format("%s %s", creator.getFirstName(), creator.getLastName()));
        }

        AcmUser modifier = userDao.quietFindByUserId(in.getModifier());
        if (modifier != null)
        {
            solr.setAdditionalProperty("modifier_full_name_lcs", String.format("%s %s", modifier.getFirstName(), modifier.getLastName()));
        }

        return solr;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.armedia.acm.services.search.service.AcmObjectToSolrDocTransformer#toSolrQuickSearch(java.lang.Object)
     */
    @Override
    public SolrDocument toSolrQuickSearch(Category in)
    {
        SolrDocument solr = new SolrDocument();

        solr.setId(in.getId() + "-CATEGORY");
        solr.setObject_id_s(in.getId() + "");
        solr.setObject_type_s("CATEGORY");
        solr.setTitle_parseable(in.getName());
        solr.setTitle_parseable_lcs(in.getName());
        solr.setDescription_no_html_tags_parseable(in.getDescription());
        solr.setName(in.getName());

        solr.setAuthor(in.getCreator());
        solr.setCreate_tdt(in.getCreated());
        solr.setModifier_s(in.getModifier());
        solr.setLast_modified_tdt(in.getModified());

        solr.setStatus_s(in.getStatus().name());

        return solr;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.armedia.acm.services.search.service.AcmObjectToSolrDocTransformer#toContentFileIndex(java.lang.Object)
     */
    @Override
    public SolrAdvancedSearchDocument toContentFileIndex(Category in)
    {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.armedia.acm.services.search.service.AcmObjectToSolrDocTransformer#isAcmObjectTypeSupported(java.lang.Class)
     */
    @Override
    public boolean isAcmObjectTypeSupported(Class acmObjectType)
    {
        return Category.class.equals(acmObjectType);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.armedia.acm.services.search.service.AcmObjectToSolrDocTransformer#getAcmObjectTypeSupported()
     */
    @Override
    public Class<?> getAcmObjectTypeSupported()
    {
        return Category.class;
    }

    /**
     * @param userDao
     *            the userDao to set
     */
    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }

    /**
     * @param categoryDao
     *            the categoryDao to set
     */
    public void setCategoryDao(CategoryDao categoryDao)
    {
        this.categoryDao = categoryDao;
    }

}
