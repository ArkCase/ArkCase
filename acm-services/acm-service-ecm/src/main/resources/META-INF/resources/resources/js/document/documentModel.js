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

    ,DOC_TYPE_DOCUMENT          : "FILE"
    ,DOC_TYPE_DOCUMENT_SM       : "file"

    ,MicroData: {
        create : function() {
            this.documentId   = Acm.Object.MicroData.get("objId");
        }
        ,onInitialized: function() {
        }
    }


    ,Detail: {
        create : function() {
            this.cacheDocumentDetail = new Acm.Model.CacheFifo();
        }
        ,onInitialized: function() {
            AcmDocument.Service.Detail.retrieveDocumentDetail(AcmDocument.Model.MicroData.documentId);
        }
        ,validateDocumentDetail: function(data) {
            if (Acm.isEmpty(data)) {
                return false;
            }
            if (Acm.isEmpty(data.title_t)) {
                return false;
            }
            if (Acm.isEmpty(data.create_tdt)) {
                return false;
            }
            if (Acm.isEmpty(data.type_s)) {
                return false;
            }
            if (Acm.isEmpty(data.author)) {
                return false;
            }
            if (Acm.isEmpty(data.status_s)) {
                return false;
            }
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
            this.cacheAllTags        = new Acm.Model.CacheFifo();
            Acm.Dispatcher.addEventListener(AcmDocument.Controller.VIEW_REMOVED_ASSOCIATED_TAG      , this.onViewRemovedAssociatedTag);
            Acm.Dispatcher.addEventListener(AcmDocument.Controller.VIEW_CREATED_NEW_TAG             , this.onViewCreatedNewTag);
            Acm.Dispatcher.addEventListener(AcmDocument.Controller.MODEL_CREATED_NEW_TAG            , this.onModelCreatedNewTag);
            Acm.Dispatcher.addEventListener(AcmDocument.Controller.VIEW_ASSOCIATED_NEW_TAG          , this.onViewAssociatedNewTag);
        }
        ,onInitialized: function() {
            AcmDocument.Service.AssociatedTags.retrieveAllTags(AcmDocument.Model.MicroData.documentId);
            AcmDocument.Service.AssociatedTags.retrieveAssociatedTags(AcmDocument.Model.MicroData.documentId);
        }
        ,onViewCreatedNewTag: function(newTagName, newTagDesc, newTagText){
            var documentId = Acm.goodValue(AcmDocument.Model.MicroData.documentId);
            AcmDocument.Service.AssociatedTags.createNewTag(newTagName, newTagDesc, newTagText, documentId);
        }

        //need this for now to associate newly created tag.
        ,onModelCreatedNewTag: function(newTag){
            if(newTag.hasError){
                App.View.MessageBoard.show(newTag.errorMsg);
            }
            else if(AcmDocument.Model.AssociatedTags.validateTag(newTag)){
                var tagId = Acm.goodValue(newTag.id);
                var documentId = Acm.goodValue(AcmDocument.Model.MicroData.documentId);
                AcmDocument.Service.AssociatedTags.associateNewTag(documentId, tagId);
            }
        }
        ,onViewAssociatedNewTag: function(tagId){
            var documentId = Acm.goodValue(AcmDocument.Model.MicroData.documentId);
            AcmDocument.Service.AssociatedTags.associateNewTag(documentId,tagId);
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
        ,validateTags: function(data){
            if (Acm.isEmpty(data)) {
                return false;
            }
            if (Acm.isNotArray(data)) {
                return false;
            }
            return true;
        }

        ,validateNewTagAssociation: function(data){
            if (Acm.isEmpty(data)) {
                return false;
            }
            if (Acm.isEmpty(data.creator)) {
                return false;
            }
            if (Acm.isEmpty(data.parentId)) {
                return false;
            }
            if (Acm.isEmpty(data.parentType)) {
                return false;
            }
            if (Acm.isEmpty(data.tagId)) {
                return false;
            }
            return true;
        }

        ,validateTag: function(data) {
            if (Acm.isEmpty(data)) {
                return false;
            }
            if (Acm.isEmpty(data.id)) {
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
            if (Acm.isEmpty(data.deletedParticipantId)) {
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

