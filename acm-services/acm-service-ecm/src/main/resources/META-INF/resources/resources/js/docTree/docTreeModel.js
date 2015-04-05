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
        Acm.Dispatcher.addEventListener(DocTree.Controller.VIEW_ADDED_DOCUMENT          ,this.onViewAddedDocument);
        Acm.Dispatcher.addEventListener(DocTree.Controller.VIEW_RENAMED_FOLDER          ,this.onViewRenamedFolder);
        Acm.Dispatcher.addEventListener(DocTree.Controller.VIEW_RENAMED_DOCUMENT        ,this.onViewRenamedDocument);

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
    }
    ,onViewAddedFolder: function(node, parentId, name) {
        var folder = {title: name};
        DocTree.Service.testService(node, parentId, folder);

    }
    ,onViewAddedDocument: function(node, parentId, name) {
        var folder = {title: name};
        DocTree.Service.testService2(node, parentId, folder);
    }
    ,onViewRenamedFolder: function(node, id, parentId, name) {

    }
    ,onViewRenamedDocument: function(node, id, parentId, name) {
    }


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
    ,validateFolderList: function(data) {
        if (Acm.isEmpty(data)) {
            return false;
        }
        if (Acm.isNotArray(data.children)) {
            return false;
        }
        return true;
    }
    ,validateUploadInfo: function(data) {
        if (Acm.isArrayEmpty(data)) {
            return false;
        }
        for (var i = 0; i < data.length; i++) {
            if (!this.validateUploadInfoItem(data[0])) {
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
//            objType: null
//            ,objId: 0
//            ,category: "Document"
            maxRows: 1000
            ,sortBy: null
            ,sortDirection: null
            //,folderId: 0
            //,start: 0
            //,totalChildren: 0
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

