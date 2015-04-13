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
        this.MODEL_UPLOADED_FILE           = name + "-model-uploaded-file";
        this.MODEL_RETRIEVED_FOLDERLIST    = name + "-model-retrieved-folder-list";
        this.VIEW_ADDED_FOLDER             = name + "-view-added-folder";
        this.MODEL_CREATED_FOLDER          = name + "-model-created-folder";
        this.VIEW_REMOVED_FOLDER           = name + "-view-removed-folder";
        this.MODEL_DELETED_FOLDER          = name + "-model-deleted-folder";
        //-------

        this.VIEW_RENAMED_FOLDER    = name + "-view-renamed-folder";
        this.VIEW_RENAMED_DOCUMENT  = name + "-view-renamed-document";

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
    ,modelUploadedFile: function(uploadInfo, folderNode) {
        Acm.Dispatcher.fireEvent(DocTree.Controller.MODEL_UPLOADED_FILE, uploadInfo, folderNode);
    }
    ,modelRetrievedFolderList: function(folderList, objType, objId, folderId, pageId, callerData) {
        Acm.Dispatcher.fireEvent(DocTree.Controller.MODEL_RETRIEVED_FOLDERLIST, folderList, objType, objId, folderId, pageId, callerData);
    }
    ,viewAddedFolder: function(parentId, folderName, cacheKey, folderNode) {
        Acm.Dispatcher.fireEvent(DocTree.Controller.VIEW_ADDED_FOLDER, parentId, folderName, cacheKey, folderNode);
    }
    ,modelCreatedFolder: function(createdFolder, parentId, folderName, cacheKey, callerData) {
        Acm.Dispatcher.fireEvent(DocTree.Controller.MODEL_CREATED_FOLDER, createdFolder, parentId, folderName, cacheKey, callerData);
    }
    ,viewRemovedFolder: function(folderId, cacheKey, folderNode) {
        Acm.Dispatcher.fireEvent(DocTree.Controller.VIEW_REMOVED_FOLDER, folderId, cacheKey, folderNode);
    }
    ,modelDeletedFolder: function(deletedInfo, folderId, cacheKey, callerData) {
        Acm.Dispatcher.fireEvent(DocTree.Controller.MODEL_DELETED_FOLDER, deletedInfo, folderId, cacheKey, callerData);
    }

    //----------------


    ,viewAddedDocument: function(node, parentId, name) {
        Acm.Dispatcher.fireEvent(DocTree.Controller.VIEW_ADDED_DOCUMENT, node, parentId, name);
    }
    ,modelAddedDocument: function(node, parentId, document) {
        Acm.Dispatcher.fireEvent(DocTree.Controller.MODEL_ADDED_DOCUMENT, node, parentId, document);
    }
    ,viewRenamedFolder: function(node, id, parentId, name) {
        Acm.Dispatcher.fireEvent(DocTree.Controller.VIEW_RENAMED_FOLDER, node, id, parentId, name);
    }
    ,viewRenamedDocument: function(node, id, parentId, name) {
        Acm.Dispatcher.fireEvent(DocTree.Controller.VIEW_RENAMED_DOCUMENT, node, id, parentId, name);
    }
};

