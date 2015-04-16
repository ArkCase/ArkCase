/**
 * AcmDocument.Model
 *
 * @author jwu
 */
AcmDocument.Model = AcmDocument.Model || {
    create : function() {
        if (AcmDocument.Service.create)              {AcmDocument.Service.create();}
        if (AcmDocument.Model.MicroData.create)      {AcmDocument.Model.MicroData.create();}
        if (AcmDocument.Model.Lookup.create)         {AcmDocument.Model.Lookup.create();}
        if (AcmDocument.Model.Detail.create)         {AcmDocument.Model.Detail.create();}
        if (AcmDocument.Model.DocViewer.create)      {AcmDocument.Model.DocViewer.create();}
        if (AcmDocument.Model.Notes.create)          {AcmDocument.Model.Notes.create();}
        if (AcmDocument.Model.Participants.create)    {AcmDocument.Model.Participants.create();}
        if (AcmDocument.Model.AssociatedTags.create)            {AcmDocument.Model.AssociatedTags.create();}
        if (AcmDocument.Model.VersionHistory.create)  {AcmDocument.Model.VersionHistory.create();}
        if (AcmDocument.Model.EventHistory.create)   {AcmDocument.Model.EventHistory.create();}
    }
    ,onInitialized: function() {
        if (AcmDocument.Service.onInitialized)              {AcmDocument.Service.onInitialized();}
        if (AcmDocument.Model.MicroData.create)              {AcmDocument.Model.MicroData.create();}
        if (AcmDocument.Model.Lookup.onInitialized)         {AcmDocument.Model.Lookup.onInitialized();}
        if (AcmDocument.Model.Detail.onInitialized)         {AcmDocument.Model.Detail.onInitialized();}
        if (AcmDocument.Model.DocViewer.onInitialized)      {AcmDocument.Model.DocViewer.onInitialized();}
        if (AcmDocument.Model.Notes.onInitialized)          {AcmDocument.Model.Notes.onInitialized();}
        if (AcmDocument.Model.Participants.onInitialized)    {AcmDocument.Model.Participants.onInitialized();}
        if (AcmDocument.Model.AssociatedTags.onInitialized)            {AcmDocument.Model.AssociatedTags.onInitialized();}
        if (AcmDocument.Model.VersionHistory.onInitialized)  {AcmDocument.Model.VersionHistory.onInitialized();}
        if (AcmDocument.Model.EventHistory.onInitialized)   {AcmDocument.Model.EventHistory.onInitialized();}
    }

    ,interface: {
        apiListObjects: function() {
            //return "/api/latest/plugin/search/CASE_FILE";
        }
        ,apiRetrieveObject: function(nodeType, objId) {
            //return "/api/latest/plugin/casefile/byId/" + objId;
        }
        ,apiSaveObject: function(nodeType, objId) {
            //return "/api/latest/plugin/casefile/";
        }
        ,nodeId: function(objSolr) {
            return objSolr.object_id_s;
            //return parseInt(objSolr.object_id_s);
        }
        ,nodeType: function(objSolr) {
            return AcmDocument.Model.DOC_TYPE_DOCUMENT;
        }
        ,nodeTitle: function(objSolr) {
            return Acm.goodValue(objSolr.title_parseable) + " (" + Acm.goodValue(objSolr.name) + ")";
        }
        ,nodeToolTip: function(objSolr) {
            return Acm.goodValue(objSolr.title_parseable);
        }
        ,objToSolr: function(objData) {
            var solr = {};
            solr.author = objData.creator;
            solr.author_s = objData.creator;
            solr.create_tdt = objData.created;
            solr.last_modified_tdt = objData.modified;
            solr.modifier_s = objData.modifier;
            solr.name = objData.caseNumber;
            solr.object_id_s = objData.id;
            solr.object_type_s = AcmDocument.Model.DOC_TYPE_DOCUMENT;
            solr.owner_s = objData.creator;
            solr.status_s = objData.status;
            solr.title_parseable = objData.title;
            return solr;
        }
        ,validateObjData: function(data) {
            return AcmDocument.Model.Detail.validateDocument(data);
        }
        ,nodeTypeMap: function() {
            return AcmDocument.Model.Tree.Key.nodeTypeMap;
        }
    }



    ,DOC_TYPE_DOCUMENT          : "FILE"
    ,DOC_TYPE_DOCUMENT_SM       : "file"



    ,getDocumentId : function() {
        return ObjNav.Model.getObjectId();
    }
    ,getDocument: function() {
        var objId = ObjNav.Model.getObjectId();
        return ObjNav.Model.Detail.getCacheObject(AcmDocument.Model.DOC_TYPE_DOCUMENT, objId);
    }

    ,MicroData: {
        create : function() {
            this.documentId   = Acm.Object.MicroData.get("objId");
        }
        ,onInitialized: function() {
        }
    }


    ,Detail: {
        create : function() {

        }
        ,onInitialized: function() {
        }
        ,getCacheDocument: function(documentId) {
            if (0 >= documentId) {
                return null;
            }
            return ObjNav.Model.Detail.getCacheObject(AcmDocument.Model.DOC_TYPE_DOCUMENT, documentId);
        }
        ,putCacheDocument: function(documentId, document) {
            ObjNav.Model.Detail.putCacheObject(AcmDocument.Model.DOC_TYPE_DOCUMENT, documentId, document);
        }
        ,validateDocument: function(data) {
            /*if (Acm.isEmpty(data)) {
                return false;
            }
            if (Acm.isEmpty(data.id) || Acm.isEmpty(data.caseNumber)) {
                return false;
            }
            if (!Acm.isArray(data.childObjects)) {
                return false;
            }
            if (!Acm.isArray(data.milestones)) {
                return false;
            }
            if (!Acm.isArray(data.participants)) {
                return false;
            }
            if (!Acm.isArray(data.personAssociations)) {
                return false;
            }
            if (!Acm.isArray(data.references)) {
                return false;
            }*/
            return true;
        }
        ,validateParentObject: function(data) {
            /*if (Acm.isEmpty(data)) {
             return false;
             }
             if (Acm.isEmpty(data.id) || Acm.isEmpty(data.caseNumber)) {
             return false;
             }
             if (!Acm.isArray(data.childObjects)) {
             return false;
             }
             if (!Acm.isArray(data.milestones)) {
             return false;
             }
             if (!Acm.isArray(data.participants)) {
             return false;
             }
             if (!Acm.isArray(data.personAssociations)) {
             return false;
             }
             if (!Acm.isArray(data.references)) {
             return false;
             }*/
            return true;
        }
    }



    ,Notes: {
        create : function() {
            this.cacheNoteList = new Acm.Model.CacheFifo(4);

            Acm.Dispatcher.addEventListener(AcmDocument.Controller.VIEW_ADDED_NOTE     , this.onViewAddedNote);
            Acm.Dispatcher.addEventListener(AcmDocument.Controller.VIEW_UPDATED_NOTE   , this.onViewUpdatedNote);
            Acm.Dispatcher.addEventListener(AcmDocument.Controller.VIEW_DELETED_NOTE   , this.onViewDeletedNote);
        }
        ,onInitialized: function() {
        }


        ,onViewAddedNote: function(note,documentId) {
            AcmDocument.Service.Notes.addNote(note,documentId);
        }
        ,onViewUpdatedNote: function(note,documentId) {
            AcmDocument.Service.Notes.updateNote(note,documentId);
        }
        ,onViewDeletedNote: function(noteId,documentId) {
            AcmDocument.Service.Notes.deleteNote(noteId,documentId);
        }

        ,validateNotes: function(data) {
            if (Acm.isEmpty(data)) {
                return false;
            }
            if (Acm.isNotArray(data)) {
                return false;
            }
            return true;
        }
        ,validateNote: function(data) {
            if (Acm.isEmpty(data)) {
                return false;
            }
            if (Acm.isEmpty(data.parentId)) {
                return false;
            }
            return true;
        }
        ,validateDeletedNote: function (data) {
            if (Acm.isEmpty(data)) {
                return false;
            }
            if (Acm.isEmpty(data.deletedNoteId)) {
                return false;
            }
            return true;
        }
    }

    ,DocViewer: {
        create : function() {
        }
        ,onInitialized: function() {
        }
    }

    ,EventHistory: {
        create : function() {
        }
        ,onInitialized: function() {
        }
    }

    ,AssociatedTags: {
        create : function() {
            this.cacheAssociatedTags = new Acm.Model.CacheFifo();
            Acm.Dispatcher.addEventListener(AcmDocument.Controller.VIEW_REMOVED_ASSOCIATED_TAG     , this.onViewRemovedAssociatedTag);
        }
        ,onInitialized: function() {
            AcmDocument.Service.AssociatedTags.retrieveAssociatedTags(AcmDocument.Model.MicroData.documentId);
        }

        ,onViewRemovedAssociatedTag: function(documentId,tagId){
            AcmDocument.Service.AssociatedTags.removeAssociatedTag(documentId,tagId);
        }

        ,validateRemovedAssociatedTag: function(data){
            if (Acm.isEmpty(data)) {
                return false;
            }
            if (Acm.isEmpty(data.deletedAssociatedTagId)) {
                return false;
            }
            if (Acm.isEmpty(data.tagId)) {
                return false;
            }
            return true;
        }
        ,validateAssociatedTags: function(data){
            if (Acm.isEmpty(data)) {
                return false;
            }
            if (Acm.isNotArray(data)) {
                return false;
            }
            return true;
        }

        ,validateAssociatedTag: function(data) {
            if (Acm.isEmpty(data)) {
                return false;
            }
            if (Acm.isEmpty(data.tagText)) {
                return false;
            }
            if (Acm.isEmpty(data.tagDescription)) {
                return false;
            }
            if (Acm.isEmpty(data.tagName)) {
                return false;
            }
            return true;
        }
    }

    ,Participants: {
        create : function() {
            this.cacheParticipants = new Acm.Model.CacheFifo();
            Acm.Dispatcher.addEventListener(AcmDocument.Controller.VIEW_REMOVED_PARTICIPANT     , this.onViewRemovedParticipant);
            Acm.Dispatcher.addEventListener(AcmDocument.Controller.VIEW_CHANGED_PARTICIPANT_ROLE     , this.onViewChangedParticipantRole);
            Acm.Dispatcher.addEventListener(AcmDocument.Controller.VIEW_ADDED_NEW_PARTICIPANT     , this.onViewAddedNewParticipant);

        }
        ,onInitialized: function() {
            AcmDocument.Service.Participants.retrieveParticipants(AcmDocument.Model.MicroData.documentId);
        }

        ,onViewRemovedParticipant: function(participantId, userId, participantType,documentId){
            AcmDocument.Service.Participants.removeParticipant(participantId, userId, participantType,documentId);
        }
        ,onViewChangedParticipantRole: function(participantType, participantId, documentId){
            AcmDocument.Service.Participants.changeParticipantRole(participantType, participantId, documentId);
        }
        ,onViewAddedNewParticipant: function(userId, participantType, documentId){
            AcmDocument.Service.Participants.addNewParticipant(userId, participantType, documentId);
        }

        ,validateRemovedParticipant: function(data){
            if (Acm.isEmpty(data)) {
                return false;
            }
            if (Acm.isEmpty(data.deletedParticipant)) {
                return false;
            }
            return true;
        }
        ,validateParticipants: function(data){
            if (Acm.isEmpty(data)) {
                return false;
            }
            if (Acm.isNotArray(data)) {
                return false;
            }
            return true;
        }

        ,validateParticipant: function(data) {
            if (Acm.isEmpty(data)) {
                return false;
            }
            if (Acm.isEmpty(data.id)) {
                return false;
            }
            if (Acm.isEmpty(data.objectType)) {
                return false;
            }
            if (Acm.isEmpty(data.objectId)) {
                return false;
            }
            if (Acm.isEmpty(data.participantType)) {
                return false;
            }
            if (Acm.isEmpty(data.participantLdapId)) {
                return false;
            }
            return true;
        }
    }

    ,VersionHistory: {
        create : function() {
        }
        ,onInitialized: function() {
        }
    }

    ,Lookup: {
        create: function() {

        }
        ,onInitialized: function() {

        }
    }

};

