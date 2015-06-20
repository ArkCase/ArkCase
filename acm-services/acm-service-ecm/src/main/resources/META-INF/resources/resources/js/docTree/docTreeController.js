/**
 * DocTree.Controller
 *
 * @author jwu
 */
DocTree.Controller = DocTree.Controller || {
    create : function(args) {
        var name = Acm.goodValue(args.name, "doctree");
        this.VIEW_CHANGED_PARENT           = name + "-view-changed-parent";
        this.VIEW_CHANGED_TREE             = name + "-view-changed-tree";
        this.MODEL_RETRIEVED_FOLDERLIST    = name + "-model-retrieved-folder-list";
        this.VIEW_CHANGED_VERSION          = name + "-view-changed-version";
        this.MODEL_SET_ACTIVE_VERSION      = name + "-model-set-active-version";
        this.VIEW_SENT_EMAIL               = name + "-view-sent-email";


        //-------


        this.VIEW_ADDED_DOCUMENT    = name + "-view-added-document";
        this.MODEL_ADDED_DOCUMENT   = name + "-model-added-document";
    }
    ,onInitialized: function() {
    }

    ,viewChangedParent: function(objType, objId) {
        Acm.Dispatcher.fireEvent(DocTree.Controller.VIEW_CHANGED_PARENT, objType, objId);
    }
    ,viewChangedTree: function() {
        Acm.Dispatcher.fireEvent(DocTree.Controller.VIEW_CHANGED_TREE);
    }
    ,modelRetrievedFolderList: function(folderList, objType, objId, folderId, pageId, callerData) {
        Acm.Dispatcher.fireEvent(DocTree.Controller.MODEL_RETRIEVED_FOLDERLIST, folderList, objType, objId, folderId, pageId, callerData);
    }
    ,viewChangedVersion: function(fileId, version, cacheKey, node) {
        Acm.Dispatcher.fireEvent(DocTree.Controller.VIEW_CHANGED_VERSION, fileId, version, cacheKey, node);
    }
    ,modelSetActiveVersion: function(version, fileId, cacheKey, callerData) {
        Acm.Dispatcher.fireEvent(DocTree.Controller.MODEL_SET_ACTIVE_VERSION, version, fileId, cacheKey, callerData);
    }
    ,viewSentEmail: function(emailNotifications) {
        Acm.Dispatcher.fireEvent(DocTree.Controller.VIEW_SENT_EMAIL, emailNotifications);
    }

    //----------------


    ,viewAddedDocument: function(node, parentId, name) {
        Acm.Dispatcher.fireEvent(DocTree.Controller.VIEW_ADDED_DOCUMENT, node, parentId, name);
    }
    ,modelAddedDocument: function(node, parentId, document) {
        Acm.Dispatcher.fireEvent(DocTree.Controller.MODEL_ADDED_DOCUMENT, node, parentId, document);
    }
};

