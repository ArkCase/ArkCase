package com.armedia.acm.plugins.audit.service;


import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.pluginmanager.model.AcmPlugin;
import com.armedia.acm.plugins.audit.dao.AuditLookupDao;
import com.armedia.acm.plugins.audit.model.AcmAuditLookup;
import com.armedia.acm.plugins.dashboard.dao.DashboardDao;
import com.armedia.acm.plugins.dashboard.exception.AcmDashboardException;
import com.armedia.acm.services.users.model.AcmUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import java.sql.SQLException;
import java.util.Map;

/**
 * Created by marjan.stefanoski on 26.06.2015.
 */
public class InsertEventTypeNames implements ApplicationContextAware {



    private AcmPlugin pluginEventType;
    private AuditLookupDao auditLookupDao;
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

        try {
            updateEventTypeNamesInTheDb();
        } catch ( SQLException e ) {
            if(log.isErrorEnabled()) {
              log.error("Audit msgs was not inserted successfully"+e.getMessage(),e);
            }

        } catch (AcmDashboardException e) {
            e.printStackTrace();
        } catch (AcmObjectNotFoundException e) {
            e.printStackTrace();
        }
    }


    public void updateEventTypeNamesInTheDb() throws SQLException, AcmObjectNotFoundException, AcmDashboardException {
        Map<String, Object> props = getPluginEventType().getPluginProperties();
        boolean isForUpdate = false;
        if (props.containsKey(AuditConstants.AUDIT_UPDATE)){
            String forUpdate = (String) props.get(AuditConstants.AUDIT_UPDATE);
            isForUpdate = new Boolean(forUpdate).booleanValue();
        }
        if(isForUpdate){
            try {
                getAuditLookupDao().deleteAllAuditsFormLookupTabel();
            } catch ( NoResultException e ){
                if(log.isInfoEnabled()){
                    log.info("No Data into acm_audit_event_type_lu table found "+e.getMessage(),e);
                }
            } catch ( PersistenceException e ){
                if(log.isInfoEnabled()){
                    log.info("No Data into acm_audit_event_type_lu table found "+e.getMessage(),e);
                }
            }
            insertNewDataIntoDb();
            if(log.isInfoEnabled()){
                log.info("New Audit Data inserted into acm_audit_event_type_lu table");
            }
        } else {
            if( log.isInfoEnabled() ){
                log.info("No new data for inserting into acm_audit_event_type_lu table");
            }
        }
    }

    private void insertNewDataIntoDb(){
        Map<String,Object> props = getPluginEventType().getPluginProperties();
        long i = 0;
        for ( Map.Entry<String,Object> entry: props.entrySet() ){
            if( entry.getKey().contains(AuditConstants.EVENT_TYPE) ){
                String key = entry.getKey().split(AuditConstants.EVENT_TYPE)[1];
                String value = (String) entry.getValue();
                if ("".equals(value)){
                    value = key;
                }
                AcmAuditLookup  auditLookup = new AcmAuditLookup();
                auditLookup.setAuditBuisinessName(value);
                auditLookup.setAuditStatus(AuditConstants.AUDIT_STATUS_ACTIVE);
                auditLookup.setOrder(new Long(++i));
                auditLookup.setAuditEventName(key);
                auditLookup.setCreator(AuditConstants.ARK_AUDIT_USER);
                auditLookup.setModifier(AuditConstants.ARK_AUDIT_USER);
                try {
                    getAuditLookupDao().save(auditLookup);
                } catch (PersistenceException e) {
                    if(log.isInfoEnabled()){
                        log.info("No Data into acm_audit_event_type_lu table found "+e.getMessage(),e);
                    }
                    throw e;
                } catch (Exception e){
                    if(log.isInfoEnabled()){
                        log.info("No Data into acm_audit_event_type_lu table found "+e.getMessage(),e);
                    }
                    throw e;
                }
            }
        }
    }

    public AcmPlugin getPluginEventType() {
        return pluginEventType;
    }

    public void setPluginEventType(AcmPlugin pluginEventType) {
        this.pluginEventType = pluginEventType;
    }

    public AuditLookupDao getAuditLookupDao() {
        return auditLookupDao;
    }

    public void setAuditLookupDao(AuditLookupDao auditLookupDao) {
        this.auditLookupDao = auditLookupDao;
    }
}
