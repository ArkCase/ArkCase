package com.armedia.acm.plugins.ecm.service;

import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.services.tag.model.AcmAssociatedTag;
import com.armedia.acm.services.tag.model.AcmAssociatedTagPersistentEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

import java.sql.SQLException;
import java.util.Date;

/**
 * Created by marjan.stefanoski on 31.03.2015.
 */
public class EcmFileTagChangeListener implements ApplicationListener<AcmAssociatedTagPersistentEvent> {

    private static final String OBJECT_TYPE="FILE";

    private EcmFileDao ecmFileDao;

    private transient final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void onApplicationEvent( AcmAssociatedTagPersistentEvent acmAssociatedTagPersistentEvent ) {
        if( acmAssociatedTagPersistentEvent != null ) {
            if( acmAssociatedTagPersistentEvent.getSource() != null && acmAssociatedTagPersistentEvent.getUserId() != null ){
                AcmAssociatedTag acmAssociatedTag = ( AcmAssociatedTag ) acmAssociatedTagPersistentEvent.getSource();
                String modifier = acmAssociatedTagPersistentEvent.getUserId();
                Long fileId = acmAssociatedTag.getParentId();
                if ( OBJECT_TYPE.equals(acmAssociatedTag.getParentType()) ) {
                    if( log.isInfoEnabled() )
                        log.info("Fields modifier and modified on EcmFile with fileID: "+fileId+" will be updated" );
                    try {
                        updateFile( fileId, modifier );
                    } catch ( SQLException e ) {
                        if( log.isErrorEnabled() )
                            log.error("SQL Exception occurred while trying to manually update modified and modifier fields for file with fieldId: "+fileId,e);
                    }
                }
            } else {
                if( log.isErrorEnabled() )
                    log.error("Event Source and/or userId are null in the event wih objectType: "+acmAssociatedTagPersistentEvent.getObjectType());
            }
        }
    }

    private void updateFile(Long fileId, String modifier) throws SQLException {
        EcmFile file = getEcmFileDao().find(fileId);
        file.setModified(new Date());
        file.setModifier(modifier);
        getEcmFileDao().updateEcmFile(file);
    }
    public EcmFileDao getEcmFileDao() {
        return ecmFileDao;
    }

    public void setEcmFileDao(EcmFileDao ecmFileDao) {
        this.ecmFileDao = ecmFileDao;
    }
}
