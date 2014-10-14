package com.armedia.acm.plugins.dashboard.dao;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.plugins.dashboard.exception.AcmWidgetException;
import com.armedia.acm.plugins.dashboard.model.Dashboard;
import com.armedia.acm.plugins.dashboard.model.widget.*;
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
public class WidgetDao extends AcmAbstractDao<Widget> {

    @PersistenceContext
    private EntityManager entityManager;

    private Logger log = LoggerFactory.getLogger(getClass());

    @Transactional
    public Widget saveWidget(Widget in) {
        Widget existing;
        if(in.getWidgetId()!=null) {
            existing = getEntityManager().find(Widget.class, in.getWidgetId());
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

    public Widget getWidgetByWidgetName(String widgetName) throws AcmObjectNotFoundException {
        Query widgetsByRoles = getEntityManager().createQuery(
                "SELECT widget FROM Widget widget WHERE widget.widgetName=:widgetName");
        widgetsByRoles.setParameter("widgetName", widgetName);
        List<Widget> retval = widgetsByRoles.getResultList();
        if(retval.isEmpty()) {
            throw new AcmObjectNotFoundException("dashboard",null, "Widgets not found for these roles",null);
        }
        return retval.get(0);
    }

    public List<Widget> getAllWidgets() {
        String queryText = "SELECT widget FROM Widget widget";
        Query allWidgets = getEntityManager().createQuery(queryText);
        List<Widget> result = allWidgets.getResultList();
        return result;
    }

    public List<RolesGroupByWidgetDto> getRolesGroupByWidget() throws AcmObjectNotFoundException{
        String queryText = "SELECT widget.widgetName, wrole.roleName " +
                "FROM Widget widget,WidgetRole wrole " +
                "WHERE widget.widgetId=wrole.widgetId " +
                "ORDER BY widget.widgetName";
        Query rolesByWidget = getEntityManager().createQuery(queryText);

        List<Object[]> rolesPerWidget = rolesByWidget.getResultList();

        List<RolesGroupByWidgetDto> result = new ArrayList<RolesGroupByWidgetDto>();
        List<WidgetRoleName> roles = new ArrayList<WidgetRoleName>();
        RolesGroupByWidgetDto rolesPerW = new RolesGroupByWidgetDto();
        String widgetN = null;
        String roleN = null;
        for(Object[] roleWidget : rolesPerWidget){
            widgetN = (String)roleWidget[0];
            roleN = (String)roleWidget[1];
            if(rolesPerW.getWidgetName()==null) {
                //only for the first widget in the list
                 rolesPerW.setWidgetName(widgetN);
                 roles.add(new WidgetRoleName(roleN));
            } else if(widgetN.equals(rolesPerW.getWidgetName())){
                roles.add(new WidgetRoleName(roleN));
            } else {
                //all roles for the widget will be added to dto and
                // the dto will be added to the result list.
                rolesPerW.setWidgetAuthorizedRoles(roles);
                result.add(rolesPerW);
                //create a new dto object
                rolesPerW = new RolesGroupByWidgetDto();
                roles = new ArrayList<WidgetRoleName>();
                rolesPerW.setWidgetName(widgetN);
                roles.add(new WidgetRoleName(roleN));
            }
        }
        //add the last row into the result collection
        rolesPerW.setWidgetAuthorizedRoles(roles);
        result.add(rolesPerW);

        if( result.isEmpty()){
            throw new AcmObjectNotFoundException("dashboard",null, "Roles not found for all widgets",null);
        }
        return result;
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

    @Transactional
    public void deleteAllWidgetRolesByWidgetName(String widgetName) {
        Query deleteAllRolesPerWidget = getEntityManager().createQuery(
                "DELETE FROM WidgetRole wrole WHERE wrole.widgetId IN " +
                "(SELECT widget.widgetId FROM Widget widget " +
                "WHERE widget.widgetName=:widgetName)");

        deleteAllRolesPerWidget.setParameter("widgetName",widgetName);
        deleteAllRolesPerWidget.executeUpdate();
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    protected Class<Widget> getPersistenceClass()
    {
        return Widget.class;
    }
}

