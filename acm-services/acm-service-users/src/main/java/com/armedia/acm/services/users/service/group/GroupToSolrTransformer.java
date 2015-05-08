package com.armedia.acm.services.users.service.group;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
import com.armedia.acm.services.search.model.solr.SolrDocument;
import com.armedia.acm.services.search.service.AcmObjectToSolrDocTransformer;
import com.armedia.acm.services.users.dao.group.AcmGroupDao;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.group.AcmGroup;

/**
 * @author riste.tutureski
 *
 */
public class GroupToSolrTransformer implements AcmObjectToSolrDocTransformer<AcmGroup> 
{
	private final Logger LOG = LoggerFactory.getLogger(getClass());
	
	private AcmGroupDao groupDao;
	
	@Override
	public List<AcmGroup> getObjectsModifiedSince(Date lastModified, int start, int pageSize) 
	{
		return getGroupDao().findModifiedSince(lastModified, start, pageSize);
	}

	@Override
	public SolrAdvancedSearchDocument toSolrAdvancedSearch(AcmGroup in) 
	{
		LOG.info("Creating Solr advnced search document for Group.");
		
		SolrAdvancedSearchDocument solr = new SolrAdvancedSearchDocument();
		
		solr.setId(in.getName() + "-GROUP");
		solr.setObject_id_s(in.getName());
		solr.setObject_type_s("GROUP");
		solr.setTitle_parseable(in.getName());
		solr.setName(in.getName());
		solr.setDescription_parseable(in.getDescription());
		solr.setObject_sub_type_s(in.getType());
		
		solr.setCreate_date_tdt(in.getCreated());
        solr.setCreator_lcs(in.getCreator());
        solr.setModified_date_tdt(in.getModified());
        solr.setModifier_lcs(in.getModifier());
        
        solr.setStatus_lcs(in.getStatus());
        
        if (in.getParentGroup() != null)
        {
        	solr.setParent_id_s(in.getParentGroup().getName());
        	solr.setParent_type_s("GROUP");
        }
        
        if (in.getSupervisor() != null)
        {
        	solr.setSupervisor_id_s(in.getSupervisor().getUserId());        	
        }
        solr = addSubGroupIds(in, solr);
        solr = addMemberIds(in, solr);
		
		return solr;
	}

	@Override
	public SolrDocument toSolrQuickSearch(AcmGroup in) 
	{
		LOG.info("Creating Solr quick search document for Group.");
		
		SolrDocument solr = new SolrDocument();
		
		solr.setId(in.getName() + "-GROUP");
		solr.setObject_id_s(in.getName());
		solr.setObject_type_s("GROUP");
		solr.setName(in.getName());
        
        solr.setAuthor(in.getCreator());
        solr.setCreate_tdt(in.getCreated());
        solr.setModifier_s(in.getModifier());
        solr.setLast_modified_tdt(in.getModified());
        
        solr.setTitle_parseable(in.getName());
        solr.setStatus_s(in.getStatus());

		return solr;
	}

    @Override
    public SolrAdvancedSearchDocument toContentFileIndex(AcmGroup in) {
        //No implementation needed
        return null;
    }

    @Override
	public boolean isAcmObjectTypeSupported(Class acmObjectType) 
	{
		boolean objectNotNull = acmObjectType != null;
		String ourClassName = AcmGroup.class.getName();
		String theirClassName = acmObjectType.getName();
		boolean classNames = theirClassName.equals(ourClassName);
		boolean isSupported = objectNotNull && classNames;
		
		return isSupported;
	}
	
	private SolrAdvancedSearchDocument addSubGroupIds(AcmGroup in, SolrAdvancedSearchDocument solr)
	{
		if (in.getChildGroups() != null && in.getChildGroups().size() > 0)
        {
        	List<String> subGroupIds = new ArrayList<String>();
        	for (AcmGroup subGroup : in.getChildGroups())
        	{
        		if (!subGroupIds.contains(subGroup.getName()))
        		{
        			subGroupIds.add(subGroup.getName());
        		}
        	}
        	
        	solr.setChild_id_ss(subGroupIds);
        }
		
		return solr;
	}
	
	private SolrAdvancedSearchDocument addMemberIds(AcmGroup in, SolrAdvancedSearchDocument solr)
	{
		if (in.getMembers() != null && in.getMembers().size() > 0)
        {
        	List<String> membersIds = new ArrayList<String>();
        	for (AcmUser user : in.getMembers())
        	{
        		membersIds.add(user.getUserId());
        	}
        	
        	solr.setMember_id_ss(membersIds);
        }
		
		return solr;
	}

	public AcmGroupDao getGroupDao() {
		return groupDao;
	}

	public void setGroupDao(AcmGroupDao groupDao) {
		this.groupDao = groupDao;
	}

}
