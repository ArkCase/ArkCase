package com.armedia.acm.data;

import com.armedia.acm.data.annotations.Encrypted;
import org.eclipse.persistence.descriptors.DescriptorEvent;
import org.eclipse.persistence.descriptors.DescriptorEventAdapter;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.queries.SQLCall;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class EncryptColumnEntityAdapter extends DescriptorEventAdapter {
    private final Logger log = LoggerFactory.getLogger(getClass());

    private Map<Object, Boolean> entityCache = new ConcurrentHashMap<>();
    private Map<Object, List<String>> cachedEntitiesWithEncryptedColumns = new ConcurrentHashMap<>();
    private Boolean encryptionEnabled;
    private String encryptionProperties;
    private String encryptionDBFunction;
    private String encryptionPassphrase;

    @Override
    public void aboutToInsert(DescriptorEvent event) {
        super.aboutToInsert(event);

        if (!checkPropertiesSet())
            return;
        if (!checkDBSupportedForEncryption(event))
            return;

        if (encryptionEnabled) {
            if (entityCache.containsKey(event.getObject()) && entityCache.get(event.getObject()) == Boolean.FALSE) {
                //this entity doesn't have fields to be encrypted
                return;
            } else {
                List<String> fieldsToBeEncrypted = getEncryptedFieldNames(event.getObject());
                entityCache.put(event.getObject(), Boolean.valueOf(fieldsToBeEncrypted.isEmpty()));
                if (!fieldsToBeEncrypted.isEmpty()) {
                    cachedEntitiesWithEncryptedColumns.put(event.getObject(), fieldsToBeEncrypted);
                }
            }
            addEncryptionFunctionToQuery(event, cachedEntitiesWithEncryptedColumns.get(event.getObject()));
        }
    }

    @Override
    public void aboutToUpdate(DescriptorEvent event) {
        super.aboutToUpdate(event);
        if (!checkPropertiesSet())
            return;
        if (!checkDBSupportedForEncryption(event))
            return;

        if (encryptionEnabled) {
            if (entityCache.containsKey(event.getObject()) && entityCache.get(event.getObject()) == Boolean.FALSE) {
                //this entity doesn't have fields to be encrypted
                return;
            } else {
                List<String> fieldsToBeEncrypted = getEncryptedFieldNames(event.getObject());
                entityCache.put(event.getObject(), Boolean.valueOf(fieldsToBeEncrypted.isEmpty()));
                if (!fieldsToBeEncrypted.isEmpty()) {
                    cachedEntitiesWithEncryptedColumns.put(event.getObject(), fieldsToBeEncrypted);
                }
            }
            addEncryptionFunctionToQuery(event, cachedEntitiesWithEncryptedColumns.get(event.getObject()));
        }
    }

    private boolean checkDBSupportedForEncryption(DescriptorEvent event) {
        //if we want to support additional db just add || event.getSession().getDatasourcePlatform().isMySQL() to the isSupportedDB
        boolean isSupportedDB = event.getSession().getDatasourcePlatform().isPostgreSQL();
        if (!isSupportedDB)
            log.debug("{} database platform is not supported for encryption", event.getSession().getDatasourcePlatform().getClass().getName());
        return isSupportedDB;
    }

    private boolean checkPropertiesSet() {
        //add additional properties here to log if not set
        if (encryptionEnabled == null)
            return false;
        boolean checked = true;
        if (encryptionProperties == null) {
            log.warn("Encryption enabled but encryption algorithm not set.");
            checked = false;
        }
        if (encryptionDBFunction == null) {
            log.warn("Encryption enabled but encryption db function not set.");
            checked = false;
        }
        return checked;
    }

    private List<String> getEncryptedFieldNames(Object entity) {
        Objects.requireNonNull(entity, "Entity must not be null");
        List<String> encryptedFieldNames = new LinkedList<>();
        for (Field field : entity.getClass().getDeclaredFields()) {
            if (field.getAnnotation(Encrypted.class) != null) {
                encryptedFieldNames.add(field.getName());
            }
        }
        return encryptedFieldNames;
    }

    private void addEncryptionFunctionToQuery(DescriptorEvent event, List<String> fieldNames) {
        SQLCall call = (SQLCall) event.getClassDescriptor().getQueryManager().getInsertCall();

        String sql = event.getQuery().getSQLString();

        AbstractRecord record = event.getQuery().getQueryMechanism().getModifyRow();

        StringBuilder sqlBuilder = new StringBuilder(sql.substring(0, sql.indexOf("?")));
        boolean first = true;
        for (Object fieldName : record.keySet()) {
            DatabaseField field = (DatabaseField) fieldName;

            if (first) {
                first = false;
            } else {
                sqlBuilder.append(", ");
            }

            if (fieldNames.contains(field.getName())) {
                log.debug("{field.getName()} is eligible for encryption");
                sqlBuilder.append(encryptionDBFunction);
                sqlBuilder.append("(?, '");
                sqlBuilder.append(encryptionPassphrase);
                sqlBuilder.append("', '");
                sqlBuilder.append(encryptionProperties);
                sqlBuilder.append("')");
            } else {
                sqlBuilder.append("?");
            }

        }
        sqlBuilder.append(")");

        log.debug("SQL: {}", sql);
        log.debug("New SQL: {}", sqlBuilder.toString());

        call.setSQLString(sqlBuilder.toString());
    }

    public void setEncryptionEnabled(Boolean encryptionEnabled) {
        this.encryptionEnabled = encryptionEnabled;
    }

    public Boolean getEncryptionEnabled() {
        return this.encryptionEnabled;
    }

    public void setEncryptionProperties(String encryptionProperties) {
        this.encryptionProperties = encryptionProperties;
    }

    public void setEncryptionDBFunction(String encryptionDBFunction) {
        this.encryptionDBFunction = encryptionDBFunction;
    }

    public void setEncryptionPassphrase(String encryptionPassphrase) {
        this.encryptionPassphrase = encryptionPassphrase;
    }

    public String getEncryptionProperties() {
        return encryptionProperties;
    }

    public String getEncryptionDBFunction() {
        return encryptionDBFunction;
    }

    public String getEncryptionPassphrase() {
        return encryptionPassphrase;
    }
}
