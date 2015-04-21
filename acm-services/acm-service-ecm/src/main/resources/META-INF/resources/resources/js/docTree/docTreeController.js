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
        this.VIEW_REMOVED_FILE             = name + "-view-removed-file";
        this.MODEL_DELETED_FILE            = name + "-model-deleted-file";
        this.VIEW_RENAMED_FOLDER           = name + "-view-renamed-folder";
        this.MODEL_RENAMED_FOLDER          = name + "-model-renamed-folder";
        this.VIEW_RENAMED_FILE             = name + "-view-renamed-file";
        this.MODEL_RENAMED_FILE            = name + "-model-renamed-file";
        this.VIEW_CUT_PASTED               = name + "-view-cut-pasted";
        this.MODEL_MOVED_ITEM              = name + "-model-moved-item";
        this.VIEW_COPY_PASTED              = name + "-view-copy-pasted";
        this.MODEL_COPIED_ITEM             = name + "-model-copied-item";


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
    ,viewRemovedFile: function(fileId, cacheKey, fileNode) {
        Acm.Dispatcher.fireEvent(DocTree.Controller.VIEW_REMOVED_FILE, fileId, cacheKey, fileNode);
    }
    ,modelDeletedFile: function(deletedInfo, fileId, cacheKey, callerData) {
        Acm.Dispatcher.fireEvent(DocTree.Controller.MODEL_DELETED_FILE, deletedInfo, fileId, cacheKey, callerData);
    }
    ,viewRenamedFolder: function(name, id, cacheKey, node) {
        Acm.Dispatcher.fireEvent(DocTree.Controller.VIEW_RENAMED_FOLDER, name, id, cacheKey, node);
    }
    ,viewRenamedFile: function(name, id, cacheKey, node) {
        Acm.Dispatcher.fireEvent(DocTree.Controller.VIEW_RENAMED_FILE, name, id, cacheKey, node);
    }
    ,modelRenamedFolder: function(renamedInfo, folderName, folderId, cacheKey, callerData) {
        Acm.Dispatcher.fireEvent(DocTree.Controller.MODEL_RENAMED_FOLDER, renamedInfo, folderName, folderId, cacheKey, callerData);
    }
    ,modelRenamedFile: function(renamedInfo, fileName, fileId, cacheKey, callerData) {
        Acm.Dispatcher.fireEvent(DocTree.Controller.MODEL_RENAMED_FILE, renamedInfo, fileName, fileId, cacheKey, callerData);
    }
    ,viewCutPasted: function(itemId, folderId, frCacheKey, toCacheKey, node) {
        Acm.Dispatcher.fireEvent(DocTree.Controller.VIEW_CUT_PASTED, itemId, folderId, frCacheKey, toCacheKey, node);
    }
    ,modelMovedItem: function(moveItemInfo, objType, objId, folderId, itemId, frCacheKey, toCacheKey, callerData) {
        Acm.Dispatcher.fireEvent(DocTree.Controller.MODEL_MOVED_ITEM, moveItemInfo, objType, objId, folderId, itemId, frCacheKey, toCacheKey, callerData);
    }
    ,viewCopyPasted: function(itemId, folderId, toCacheKey, node) {
        Acm.Dispatcher.fireEvent(DocTree.Controller.VIEW_COPY_PASTED, itemId, folderId, toCacheKey, node);
    }
    ,modelCopiedItem: function(copyItemInfo, objType, objId, folderId, itemId, toCacheKey, callerData) {
        Acm.Dispatcher.fireEvent(DocTree.Controller.MODEL_COPIED_ITEM, copyItemInfo, objType, objId, folderId, itemId, toCacheKey, callerData);
    }
    //----------------


    ,viewAddedDocument: function(node, parentId, name) {
        Acm.Dispatcher.fireEvent(DocTree.Controller.VIEW_ADDED_DOCUMENT, node, parentId, name);
    }
    ,modelAddedDocument: function(node, parentId, document) {
        Acm.Dispatcher.fireEvent(DocTree.Controller.MODEL_ADDED_DOCUMENT, node, parentId, document);
    }
};

