package com.armedia.acm.plugins.ecm.service;

import com.armedia.acm.event.AcmEvent;
import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.services.tag.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * Created by marjan.stefanoski on 31.03.2015.
 */
public class EcmFileAssociatedTagChangeListener implements ApplicationListener<AcmEvent> {

    private static final String OBJECT_TYPE="FILE";

    private EcmFileDao ecmFileDao;

    private transient final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void onApplicationEvent( AcmEvent event ) {
          if ( event instanceof AcmAssociatedTagPersistentEvent ) {
              if( log.isInfoEnabled() ) {
                  log.info("Change in tag association table occurred");
              }
              acmAssociatedTagAddedOrDeleted((AcmAssociatedTagPersistentEvent)event);
          } else if ( event instanceof AcmTagUpdatedEvent || event instanceof AcmTagUpdatedEvent ) {
              if( log.isInfoEnabled() ) {
                  log.info("Change in tag table occurred");
              }
              acmTagUpdatedOrDeleted(event);
          }
    }

    private void acmTagUpdatedOrDeleted(AcmEvent event) {

        AcmEvent acmTagEvent;
        if ( event instanceof AcmTagUpdatedEvent ){
            AcmTagUpdatedEvent updatedEvent = (AcmTagUpdatedEvent) event;
            acmTagEvent = updatedEvent;
        } else {
            AcmTagDeletedEvent deletedEvent = (AcmTagDeletedEvent) event;
            acmTagEvent = deletedEvent;
        }

        if ( acmTagEvent.getSource() != null && acmTagEvent.getUserId() != null ) {
            String modifier = acmTagEvent.getUserId();
            AcmTag tag = (AcmTag) acmTagEvent.getSource();
            List<AcmAssociatedTag> tagList = tag.getAssociatedTags();
            for( AcmAssociatedTag acmAssociatedTag: tagList ) {
                if ( OBJECT_TYPE.equals(acmAssociatedTag.getParentType()) ) {
                    Long fileId = null;
                    if ( acmAssociatedTag.getParentId() != null ) {
                        fileId = acmAssociatedTag.getParentId();
                    }
                    if( log.isInfoEnabled() && fileId !=null )
                        log.info("Fields modifier and modified on EcmFile with fileID: "+fileId+" will be updated" );
                    try {
                        updateFile(fileId,modifier);
                    } catch ( SQLException e ) {
                        if( log.isErrorEnabled() )
                            log.error("SQL Exception occurred while trying to manually update modified and modifier fields for file with fieldId: "+fileId,e);
                    }

                }
            }
        } else {
            if (log.isErrorEnabled() )
                log.error("Event Source and/or userId are null in the event wih objectType: " + acmTagEvent.getObjectType());
        }
    }

    private void acmAssociatedTagAddedOrDeleted(AcmAssociatedTagPersistentEvent event) {

        if( event.getSource() != null && event.getUserId() != null ){
            AcmAssociatedTag acmAssociatedTag = ( AcmAssociatedTag ) event.getSource();
            String modifier = event.getUserId();
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
                log.error("Event Source and/or userId are null in the event wih objectType: " + event.getObjectType());
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
