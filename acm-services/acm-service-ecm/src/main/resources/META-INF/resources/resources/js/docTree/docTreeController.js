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
        this.MODEL_UPLOADED_FILES          = name + "-model-uploaded-files";
        this.MODEL_REPLACED_FILE           = name + "-model-replaced-file";
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
        this.VIEW_MOVED_FILE               = name + "-view-moved-file";
        this.MODEL_MOVED_FILE              = name + "-model-moved-file";
        this.VIEW_COPIED_FILE              = name + "-view-copied-file";
        this.MODEL_COPIED_FILE             = name + "-model-copied-file";
        this.VIEW_MOVED_FOLDER             = name + "-view-moved-folder";
        this.MODEL_MOVED_FOLDER            = name + "-model-moved-folder";
        this.VIEW_COPIED_FOLDER            = name + "-view-copied-folder";
        this.MODEL_COPIED_FOLDER           = name + "-model-copied-folder";
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
    ,modelUploadedFiles: function(uploadInfo, callerData) {
        Acm.Dispatcher.fireEvent(DocTree.Controller.MODEL_UPLOADED_FILES, uploadInfo, callerData);
    }
    ,modelReplacedFile: function(replaceInfo, fileId, callerData) {
        Acm.Dispatcher.fireEvent(DocTree.Controller.MODEL_REPLACED_FILE, replaceInfo, fileId, callerData);
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
    ,viewMovedFile: function(fileId, folderId, frCacheKey, toCacheKey, node) {
        Acm.Dispatcher.fireEvent(DocTree.Controller.VIEW_MOVED_FILE, fileId, folderId, frCacheKey, toCacheKey, node);
    }
    ,modelMovedFile: function(moveFileInfo, objType, objId, folderId, fileId, frCacheKey, toCacheKey, callerData) {
        Acm.Dispatcher.fireEvent(DocTree.Controller.MODEL_MOVED_FILE, moveFileInfo, objType, objId, folderId, fileId, frCacheKey, toCacheKey, callerData);
    }
    ,viewCopiedFile: function(fileId, folderId, toCacheKey, node) {
        Acm.Dispatcher.fireEvent(DocTree.Controller.VIEW_COPIED_FILE, fileId, folderId, toCacheKey, node);
    }
    ,modelCopiedFile: function(copyFileInfo, objType, objId, folderId, fileId, toCacheKey, callerData) {
        Acm.Dispatcher.fireEvent(DocTree.Controller.MODEL_COPIED_FILE, copyFileInfo, objType, objId, folderId, fileId, toCacheKey, callerData);
    }
    ,viewMovedFolder: function(subFolderId, folderId, frCacheKey, toCacheKey, node) {
        Acm.Dispatcher.fireEvent(DocTree.Controller.VIEW_MOVED_FOLDER, subFolderId, folderId, frCacheKey, toCacheKey, node);
    }
    ,modelMovedFolder: function(moveFolderInfo, subFolderId, folderId, frCacheKey, toCacheKey, callerData) {
        Acm.Dispatcher.fireEvent(DocTree.Controller.MODEL_MOVED_FOLDER, moveFolderInfo, subFolderId, folderId, frCacheKey, toCacheKey, callerData);
    }
    ,viewCopiedFolder: function(subFolderId, folderId, toCacheKey, node) {
        Acm.Dispatcher.fireEvent(DocTree.Controller.VIEW_COPIED_FOLDER, subFolderId, folderId, toCacheKey, node);
    }
    ,modelCopiedFolder: function(copyFolderInfo, subFolderId, folderId, toCacheKey, callerData) {
        Acm.Dispatcher.fireEvent(DocTree.Controller.MODEL_COPIED_FOLDER, copyFolderInfo, subFolderId, folderId, toCacheKey, callerData);
    }
    ,viewChangedVersion: function(fileId, version, cacheKey, node) {
        Acm.Dispatcher.fireEvent(DocTree.Controller.VIEW_CHANGED_VERSION, fileId, version, cacheKey, node);
    }
    ,modelSetActiveVersion: function(version, fileId, cacheKey, callerData) {
        Acm.Dispatcher.fireEvent(DocTree.Controller.MODEL_SET_ACTIVE_VERSION, version, fileId, cacheKey, callerData);
    }
    ,viewSentEmail: function(emailData) {
        Acm.Dispatcher.fireEvent(DocTree.Controller.VIEW_SENT_EMAIL, emailData);
    }

    //----------------


    ,viewAddedDocument: function(node, parentId, name) {
        Acm.Dispatcher.fireEvent(DocTree.Controller.VIEW_ADDED_DOCUMENT, node, parentId, name);
    }
    ,modelAddedDocument: function(node, parentId, document) {
        Acm.Dispatcher.fireEvent(DocTree.Controller.MODEL_ADDED_DOCUMENT, node, parentId, document);
    }
};

