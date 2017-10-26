package com.armedia.acm.plugins.person.service;

import static com.armedia.acm.plugins.person.model.OrganizationConstants.PARENT_COMPANY;
import static com.armedia.acm.plugins.person.model.OrganizationConstants.PARTNER_COMPANY;
import static com.armedia.acm.plugins.person.model.OrganizationConstants.SUB_COMPANY;
import static com.armedia.acm.plugins.person.model.PersonOrganizationConstants.ORGANIZATION_OBJECT_TYPE;

import com.armedia.acm.plugins.objectassociation.model.ObjectAssociation;
import com.armedia.acm.plugins.objectassociation.model.ObjectAssociationEvent;
import com.armedia.acm.plugins.objectassociation.model.ObjectAssociationEvent.ObjectAssociationState;
import com.armedia.acm.plugins.objectassociation.service.ObjectAssociationService;
import com.armedia.acm.plugins.person.dao.OrganizationDao;
import com.armedia.acm.plugins.person.model.Organization;

import org.springframework.context.ApplicationListener;

import java.util.List;
import java.util.Optional;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Sep 14, 2017
 *
 */
public class OrganizationAssociationListener implements ApplicationListener<ObjectAssociationEvent>
{

    private OrganizationDao organizationDao;

    private ObjectAssociationService associationService;

    /*
     * (non-Javadoc)
     *
     * @see
     * org.springframework.context.ApplicationListener#onApplicationEvent(org.springframework.context.ApplicationEvent)
     */
    @Override
    public void onApplicationEvent(ObjectAssociationEvent event)
    {
        String associationType = ((ObjectAssociation) event.getSource()).getAssociationType();
        ObjectAssociation oa = ((ObjectAssociation) event.getSource());
        Organization child = null, parent = null;
        if (ObjectAssociationState.DELETE.equals(event.getObjectAssociationState()))
        {
            if (SUB_COMPANY.equalsIgnoreCase(associationType))
            {
                // child is the child in the context of the association, not the child company!
                child = organizationDao.find(oa.getTargetId());
                // parent is the parent in the context of the association, not the parent company!
                parent = organizationDao.find(oa.getParentId());
            } else if (PARENT_COMPANY.equalsIgnoreCase(associationType))
            {
                // child is the child in the context of the association, not the child company!
                child = organizationDao.find(oa.getParentId());
                // parent is the parent in the context of the association, not the parent company!
                parent = organizationDao.find(oa.getTargetId());
            }

            if (child != null && child.getParentOrganization() != null &&
                    child.getParentOrganization().getId() != null && parent != null && parent.getId() != null &&
                    child.getParentOrganization().getId().equals(parent.getId()))
            {
                child.setParentOrganization(null);
                organizationDao.save(child);
            }
        } else if (ObjectAssociationState.NEW.equals(event.getObjectAssociationState())
                || ObjectAssociationState.UPDATE.equals(event.getObjectAssociationState()))
        {
            if (SUB_COMPANY.equalsIgnoreCase(associationType))
            {
                // child is the child in the context of the association, not the child company!
                child = organizationDao.find(oa.getTargetId());
                // parent is the parent in the context of the association, not the parent company!
                parent = organizationDao.find(oa.getParentId());

                if (child != null && child.getParentOrganization() != null && parent != null && parent.getId() != null &&
                        child.getParentOrganization().getId() != null && child.getParentOrganization().getId().equals(parent.getId()))
                {
                    return;
                }
            } else if (PARENT_COMPANY.equalsIgnoreCase(associationType))
            {
                // child is the child in the context of the association, not the child company!
                child = organizationDao.find(oa.getParentId());
                // parent is the parent in the context of the association, not the parent company!
                parent = organizationDao.find(oa.getTargetId());
                if (parent != null && parent.getParentOrganization() != null && parent.getParentOrganization().getId() != null &&
                        child != null && child.getId() != null && parent.getParentOrganization().getId().equals(child.getId()))
                {
                    return;
                }
            } else if (PARTNER_COMPANY.equalsIgnoreCase(associationType)) {
                // child is the child in the context of the association, not the child company!
                child = organizationDao.find(oa.getTargetId());
                // parent is the parent in the context of the association, not the parent company!
                parent = organizationDao.find(oa.getParentId());
                if (parent != null && child != null && parent.getParentOrganization() != null && parent.getParentOrganization().getId() != null
                        && parent.getParentOrganization().getId().equals(child.getId())) {
                    parent.setParentOrganization(null);
                    organizationDao.save(parent);
                }
            }

            if (!PARTNER_COMPANY.equalsIgnoreCase(associationType) && child != null && parent != null && parent.getId() != null &&
                    (child.getParentOrganization() == null || (!parent.getId().equals(child.getParentOrganization().getId()))))
            {
                child.setParentOrganization(parent);
                Organization finalParent = parent;
                organizationDao.save(child);
                List<ObjectAssociation> parentAssociations = associationService.findByParentTypeAndId(ORGANIZATION_OBJECT_TYPE,
                        child.getOrganizationId());
                Optional<ObjectAssociation> illegalParent = parentAssociations.stream()
                        .filter(pa -> !pa.getTargetId().equals(finalParent.getOrganizationId())).findFirst();
                if (illegalParent.isPresent())
                {
                    associationService.delete(illegalParent.get().getAssociationId());
                }

            }
        }

    }

    /**
     * @param organizationDao
     *            the organizationDao to set
     */
    public void setOrganizationDao(OrganizationDao organizationDao)
    {
        this.organizationDao = organizationDao;
    }

    /**
     * @param associationService
     *            the associationService to set
     */
    public void setAssociationService(ObjectAssociationService associationService)
    {
        this.associationService = associationService;
    }

}
