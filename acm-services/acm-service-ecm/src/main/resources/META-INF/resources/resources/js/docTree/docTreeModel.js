/**
 * DocTree.Model
 *
 * @author jwu
 */
DocTree.Model = DocTree.Model || {
    create : function(args) {
        this.cacheTree = new Acm.Model.CacheFifo();
        this.cacheFolderList = new Acm.Model.CacheFifo();

        Acm.Dispatcher.addEventListener(DocTree.Controller.VIEW_CHANGED_PARENT          ,this.onViewChangedParent);
        Acm.Dispatcher.addEventListener(DocTree.Controller.VIEW_ADDED_FOLDER            ,this.onViewAddedFolder);
        Acm.Dispatcher.addEventListener(DocTree.Controller.VIEW_REMOVED_FOLDER          ,this.onViewRemovedFolder);
        Acm.Dispatcher.addEventListener(DocTree.Controller.VIEW_REMOVED_FILE            ,this.onViewRemovedFile);
        Acm.Dispatcher.addEventListener(DocTree.Controller.VIEW_RENAMED_FOLDER          ,this.onViewRenamedFolder);
        Acm.Dispatcher.addEventListener(DocTree.Controller.VIEW_RENAMED_FILE            ,this.onViewRenamedFile);
        Acm.Dispatcher.addEventListener(DocTree.Controller.VIEW_MOVED_FILE              ,this.onViewMovedFile);
        Acm.Dispatcher.addEventListener(DocTree.Controller.VIEW_COPIED_FILE             ,this.onViewCopiedFile);
        Acm.Dispatcher.addEventListener(DocTree.Controller.VIEW_MOVED_FOLDER            ,this.onViewMovedFolder);
        Acm.Dispatcher.addEventListener(DocTree.Controller.VIEW_COPIED_FOLDER           ,this.onViewCopiedFolder);
        Acm.Dispatcher.addEventListener(DocTree.Controller.VIEW_CHANGED_VERSION         ,this.onViewChangedVersion);

        //---------
        Acm.Dispatcher.addEventListener(DocTree.Controller.VIEW_ADDED_DOCUMENT          ,this.onViewAddedDocument);

        if (DocTree.Model.Config.create)           {DocTree.Model.Config.create(args);}
        if (DocTree.Model.Key.create)              {DocTree.Model.Key.create(args);}
        if (DocTree.Service.create)                {DocTree.Service.create(args);}
    }
    ,onInitialized: function() {
        if (DocTree.Model.Config.onInitialized)    {DocTree.Model.Config.onInitialized();}
        if (DocTree.Model.Key.onInitialized)       {DocTree.Model.Key.onInitialized();}
        if (DocTree.Service.onInitialized)         {DocTree.Service.onInitialized();}
    }

    ,onViewChangedParent: function(objType, objId) {
        //if not in cache
        //DocTree.Service.retrieveTopFolder(parentType, parentId);
        //
        var z = 1;
    }
    ,onViewAddedFolder: function(parentId, folderName, cacheKey, folderNode) {
        DocTree.Service.createFolder(parentId, folderName, cacheKey, folderNode);
    }
    ,onViewRemovedFolder: function(folderId, cacheKey, folderNode) {
        DocTree.Service.deleteFolder(folderId, cacheKey, folderNode);
    }
    ,onViewRemovedFile: function(fileId, cacheKey, folderNode) {
        DocTree.Service.deleteFile(fileId, cacheKey, folderNode);
    }
    ,onViewRenamedFolder: function(name, id, cacheKey, node) {
        DocTree.Service.renameFolder(name, id, cacheKey, node);
    }
    ,onViewRenamedFile: function(name, id, cacheKey, node) {
        DocTree.Service.renameFile(name, id, cacheKey, node);
    }
    ,onViewMovedFile: function(fileId, folderId, frCacheKey, toCacheKey, node) {
        DocTree.Service.moveFile(DocTree.Model.getObjType(), DocTree.Model.getObjId(), folderId, fileId, frCacheKey, toCacheKey, node);
    }
    ,onViewCopiedFile: function(fileId, folderId, toCacheKey, node) {
        DocTree.Service.copyFile(DocTree.Model.getObjType(), DocTree.Model.getObjId(), folderId, fileId, toCacheKey, node);
    }
    ,onViewMovedFolder: function(subFolderId, folderId, frCacheKey, toCacheKey, node) {
        DocTree.Service.moveFolder(subFolderId, folderId, frCacheKey, toCacheKey, node);
    }
    ,onViewCopiedFolder: function(subFolderId, folderId, toCacheKey, node) {
        DocTree.Service.copyFolder(subFolderId, folderId, toCacheKey, node);
    }
    ,onViewChangedVersion: function(fileId, version, node) {
        DocTree.Service.setActiveVersion(fileId, version, node);
    }

    //---------------
    ,onViewAddedDocument: function(node, parentId, name) {
        var folder = {title: name};
        DocTree.Service.testService2(node, parentId, folder);
    }

    ,NODE_TYPE_PREV: "prev"
    ,NODE_TYPE_NEXT: "next"
    ,NODE_TYPE_FILE: "file"
    ,NODE_TYPE_FOLDER: "folder"

    ,_objType: null
    ,getObjType: function() {
        return this._objType;
    }
    ,setObjType: function(objType) {
        this._objType = objType;
    }
    ,_objId: null
    ,getObjId: function() {
        return this._objId;
    }
    ,setObjId: function(objId) {
        this._objId = objId;
    }

    ,getCacheKey: function(folderId, pageId) {
        var setting = DocTree.Model.Config.getSetting();
        var key = this.getObjType() + "." + this.getObjId();
        key += "." + Acm.goodValue(folderId, 0);    //for root folder, folderId is 0 or undefined
        key += "." + Acm.goodValue(pageId, 0);
        key += "." + DocTree.Model.Config.getSortBy();
        key += "." + DocTree.Model.Config.getSortDirection();
        key += "." + DocTree.Model.Config.getMaxRows();
        return key;
    }

    ,findFolderItemIdx: function(objectId, folderList) {
        var found = -1;
        if (DocTree.Model.validateFolderList(folderList)) {
            for (var i = 0; i < folderList.children.length; i++) {
                if (Acm.goodValue(folderList.children[i].objectId) == objectId) {
                    found = i;
                    break;
                }
            }
        }
        return found;
    }
    ,validateFolderList: function(data) {
        if (Acm.isEmpty(data)) {
            return false;
        }
        if (Acm.isNotArray(data.children)) {
            return false;
        }
        return true;
    }
    ,validateCreateInfo: function(data) {
        if (Acm.isEmpty(data)) {
            return false;
        }
        if (Acm.isEmpty(data.id)) {
            return false;
        }
        if (0 == data.id) {
            return false;
        }
        if (Acm.isEmpty(data.parentFolderId)) {
            return false;
        }
        return true;
    }
    ,validateDeletedFolder: function(data) {
        if (Acm.isEmpty(data)) {
            return false;
        }
        if (Acm.isEmpty(data.deletedFolderId)) {
            return false;
        }
        return true;
    }
    ,validateDeletedFile: function(data) {
        if (Acm.isEmpty(data)) {
            return false;
        }
        if (Acm.isEmpty(data.deletedFileId)) {
            return false;
        }
        return true;
    }
    ,validateRenamedFolder: function(data) {
        if (Acm.isEmpty(data)) {
            return false;
        }
//to be determined
//        if (Acm.isEmpty(data.deletedFolderId)) {
//            return false;
//        }
        return true;
    }
    ,validateRenamedFile: function(data) {
        if (Acm.isEmpty(data)) {
            return false;
        }
        if (Acm.isEmpty(data.fileId)) {
            return false;
        }
        if (Acm.isEmpty(data.fileName)) {
            return false;
        }
        return true;
    }
    ,validateMoveFileInfo: function(data) {
        if (Acm.isEmpty(data)) {
            return false;
        }
        if (Acm.isEmpty(data.fileId)) {
            return false;
        }
        if (Acm.isEmpty(data.folder)) {
            return false;
        }
        if (Acm.isEmpty(data.folder.id)) {
            return false;
        }
        return true;
    }
    ,validateCopyFileInfo: function(data) {
        if (Acm.isEmpty(data)) {
            return false;
        }
        if (Acm.isEmpty(data.fileId)) {
            return false;
        }
        if (Acm.isEmpty(data.folder)) {
            return false;
        }
        if (Acm.isEmpty(data.folder.id)) {
            return false;
        }
        return true;
    }
    ,validateMoveFolderInfo: function(data) {
        if (Acm.isEmpty(data)) {
            return false;
        }
        if (Acm.isEmpty(data.id)) {
            return false;
        }
        return true;
    }
    ,validateCopyFolderInfo: function(data) {
        if (Acm.isEmpty(data)) {
            return false;
        }
        if (Acm.isEmpty(data.fileId)) {
            return false;
        }
        if (Acm.isEmpty(data.folder)) {
            return false;
        }
        if (Acm.isEmpty(data.folder.id)) {
            return false;
        }
        return true;
    }
    ,validateUploadInfo: function(data) {
        if (Acm.isArrayEmpty(data)) {
            return false;
        }
        for (var i = 0; i < data.length; i++) {
            if (!this.validateUploadInfoItem(data[i])) {
                return false;
            }
        }
        return true;
    }
    ,validateUploadInfoItem: function(data) {
        if (Acm.isEmpty(data)) {
            return false;
        }
        if (Acm.isEmpty(data.fileId)) {
            return false;
        }
        if (Acm.isEmpty(data.folder)) {
            return false;
        }
        if (Acm.isNotArray(data.versions)) {
            return false;
        }
        if (Acm.isNotArray(data.tags)) {
            return false;
        }
        return true;
    }

    ,Key: {
        create: function(args) {
        }
        ,onInitialized: function() {
        }

        ,KEY_SEPARATOR               : "/"
        ,TYPE_ID_SEPARATOR           : "."
        ,NODE_TYPE_PART_PREV_PAGE    : "prevPage"
        ,NODE_TYPE_PART_NEXT_PAGE    : "nextPage"
        ,NODE_TYPE_PART_PAGE         : "p"


        //keyParts format: [{type: "t", id: "123"}, ....]
        //Integer ID works as well: [{type: "t", id: 123}, ....]
        ,makeKey: function(keyParts) {
            var key = "";
            if (Acm.isArray(keyParts)) {
                for (var i = 0; i < keyParts.length; i++) {
                    if (keyParts[i].type) {
                        if (Acm.isNotEmpty(key)) {
                            key += this.KEY_SEPARATOR;
                        }
                        key += keyParts[i].type;

                        if (Acm.isNotEmpty(keyParts[i].id)) {
                            key += this.TYPE_ID_SEPARATOR;
                            key += keyParts[i].id;
                        }
                    }
                } //for i
            }
            return key;
        }


    }

    ,Config: {
        create: function(args) {
        }
        ,onInitialized: function() {
        }

        ,DEFAULT_MAX_ROWS: 1000
        ,DEFAULT_SORT_BY: "name"
        ,DEFAULT_SORT_DIRECTION: "ASC"
        ,_setting: {
            maxRows: 16
            ,sortBy: null
            ,sortDirection: null
//            ,objType: null
//            ,objId: 0
//            ,category: "Document"
//            ,folderId: 0
//            ,start: 0
//            ,totalChildren: 0
        }
        ,getSetting: function() {
            return this._setting;
        }
        ,getMaxRows: function() {
            return Acm.goodValue(this._setting.maxRows, this.DEFAULT_MAX_ROWS);
        }
        ,getSortBy: function() {
            return Acm.goodValue(this._setting.sortBy, this.DEFAULT_SORT_BY);
        }
        ,getSortDirection: function() {
            return Acm.goodValue(this._setting.sortDirection, this.DEFAULT_SORT_DIRECTION);
        }
    }

};

