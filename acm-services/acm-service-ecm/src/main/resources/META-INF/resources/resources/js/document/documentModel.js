/**
 * AcmDocument.Model
 *
 * @author jwu
 */
AcmDocument.Model = AcmDocument.Model || {
    create : function() {
        if (AcmDocument.Service.create)              {AcmDocument.Service.create();}
        if (AcmDocument.Model.Lookup.create)         {AcmDocument.Model.Lookup.create();}
        if (AcmDocument.Model.Detail.create)         {AcmDocument.Model.Detail.create();}
        if (AcmDocument.Model.DocViewer.create)      {AcmDocument.Model.DocViewer.create();}
        if (AcmDocument.Model.Notes.create)          {AcmDocument.Model.Notes.create();}
        if (AcmDocument.Model.Participants.create)    {AcmDocument.Model.Participants.create();}
        if (AcmDocument.Model.Tags.create)            {AcmDocument.Model.Tags.create();}
        if (AcmDocument.Model.VersionHistory.create)  {AcmDocument.Model.VersionHistory.create();}
        if (AcmDocument.Model.EventHistory.create)   {AcmDocument.Model.EventHistory.create();}
    }
    ,onInitialized: function() {
        if (AcmDocument.Service.onInitialized)              {AcmDocument.Service.onInitialized();}
        if (AcmDocument.Model.Lookup.onInitialized)         {AcmDocument.Model.Lookup.onInitialized();}
        if (AcmDocument.Model.Detail.onInitialized)         {AcmDocument.Model.Detail.onInitialized();}
        if (AcmDocument.Model.DocViewer.onInitialized)      {AcmDocument.Model.DocViewer.onInitialized();}
        if (AcmDocument.Model.Notes.onInitialized)          {AcmDocument.Model.Notes.onInitialized();}
        if (AcmDocument.Model.Participants.onInitialized)    {AcmDocument.Model.Participants.onInitialized();}
        if (AcmDocument.Model.Tags.onInitialized)            {AcmDocument.Model.Tags.onInitialized();}
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



    ,DOC_TYPE_FILE              : "FILE"
    ,DOC_TYPE_DOCUMENT          : "DOCUMENT"

    ,getDocumentId : function() {
        return ObjNav.Model.getObjectId();
    }
    ,getDocument: function() {
        var objId = ObjNav.Model.getObjectId();
        return ObjNav.Model.Detail.getCacheObject(AcmDocument.Model.DOC_TYPE_DOCUMENT, objId);
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


        ,onViewAddedNote: function(note) {
            AcmDocument.Service.Notes.addNote(note);
        }
        ,onViewUpdatedNote: function(note) {
            AcmDocument.Service.Notes.updateNote(note);
        }
        ,onViewDeletedNote: function(noteId) {
            AcmDocument.Service.Notes.deleteNote(noteId);
        }

        ,validateNotes: function(data) {
            if (Acm.isEmpty(data)) {
                return false;
            }
            if (!Acm.isArray(data)) {
                return false;
            }
            return true;
        }
        ,validateNote: function(data) {
            if (Acm.isEmpty(data)) {
                return false;
            }
            if (Acm.isEmpty(data.id)) {
                return false;
            }
            if (Acm.isEmpty(data.parentId)) {
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

    ,Tags: {
        create : function() {
        }
        ,onInitialized: function() {
        }
    }

    ,Participants: {
        create : function() {
        }
        ,onInitialized: function() {
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

