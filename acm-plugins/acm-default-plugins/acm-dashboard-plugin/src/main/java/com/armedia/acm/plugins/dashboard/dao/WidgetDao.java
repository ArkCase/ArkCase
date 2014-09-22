package com.armedia.acm.plugins.dashboard.dao;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.plugins.dashboard.model.widget.Widget;
import com.armedia.acm.plugins.dashboard.model.widget.WidgetRole;
import com.armedia.acm.plugins.dashboard.model.widget.WidgetRolePrimaryKey;
import com.armedia.acm.services.users.model.AcmRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by marjan.stefanoski on 9/19/2014.
 */
public class WidgetDao {

    @PersistenceContext
    private EntityManager entityManager;

    private Logger log = LoggerFactory.getLogger(getClass());

    @Transactional
    public Widget saveWidget(Widget in) {
        Widget existing;
        if(in.getWidgetId()!=null) {
            existing  = getEntityManager().find(Widget.class, in.getWidgetId());
        } else {
            try {
                existing = getWidgetByWidgetName(in.getWidgetName());
            } catch (AcmObjectNotFoundException e) {
                existing = null;
            }
        }
        if ( existing == null ) {
            in = getEntityManager().merge(in);
            getEntityManager().flush();
        } else {
            in = existing;
        }
        return in;
    }

    @Transactional
    public WidgetRole saveWidgetRole(WidgetRole widgetRole) {
        WidgetRolePrimaryKey key = new WidgetRolePrimaryKey();
        key.setRoleName(widgetRole.getRoleName());
        key.setWidgetId(widgetRole.getWidgetId());
        WidgetRole existing = getEntityManager().find(WidgetRole.class, key);

        if ( existing == null ) {
            getEntityManager().persist(widgetRole);
            getEntityManager().flush();
            return widgetRole;
        }
        getEntityManager().persist(existing);
        return existing;
    }

    public Widget getWidgetByWidgetName(String widgetName) throws AcmObjectNotFoundException{
        Query widgetsByRoles = getEntityManager().createQuery(
                "SELECT widget FROM Widget widget WHERE widget.widgetName=:widgetName");
        widgetsByRoles.setParameter("widgetName", widgetName);
        List<Widget> retval = widgetsByRoles.getResultList();
        if( retval.isEmpty()){
            throw new AcmObjectNotFoundException("dashboard",null, "Widgets not found for these roles",null);
        }
        return retval.get(0);
    }

    public List<Widget> getAllWidgets() {
        return null;
    }

    public List<Widget> getAllWidgetsByRoles(List<AcmRole> roles) throws AcmObjectNotFoundException {
        List<String> roleNames = new ArrayList<String>();
        for(AcmRole role : roles){
            roleNames.add(role.getRoleName());
        }
        Query widgetsByRoles = getEntityManager().createQuery(
                "SELECT widget FROM Widget widget, WidgetRole widgetRole " +
                "WHERE widget.widgetId = widgetRole.widgetId " +
                "AND widgetRole.roleName IN :roleNames ");
        widgetsByRoles.setParameter("roleNames", roleNames);
        List<Widget> retval = widgetsByRoles.getResultList();
        if( retval.isEmpty()){
            throw new AcmObjectNotFoundException("dashboard",null, "Widgets not found for these roles",null);
        }
        return retval;
    }

    public WidgetRole addWidgetToARole(Widget widget, AcmRole role){
              WidgetRole widgetRole = new WidgetRole();
              widgetRole.setRoleName(role.getRoleName());
              widgetRole.setWidgetId(widget.getWidgetId());
        return saveWidgetRole(widgetRole);
    }

    public void deleteWidgetRole(WidgetRole widgetRole){

        getEntityManager().remove(widgetRole);
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
}

