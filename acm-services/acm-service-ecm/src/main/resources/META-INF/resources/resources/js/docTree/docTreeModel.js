/**
 * DocTree.Model
 *
 * @author jwu
 */
DocTree.Model = DocTree.Model || {
    create : function(args) {
        this.cacheTree = new Acm.Model.CacheFifo();
        this.cacheFolder = new Acm.Model.CacheFifo();

        Acm.Dispatcher.addEventListener(DocTree.Controller.VIEW_CHANGED_PARENT          ,this.onViewChangedParent);
        Acm.Dispatcher.addEventListener(DocTree.Controller.VIEW_ADDED_FOLDER            ,this.onViewAddedFolder);
        Acm.Dispatcher.addEventListener(DocTree.Controller.VIEW_ADDED_DOCUMENT          ,this.onViewAddedDocument);
        Acm.Dispatcher.addEventListener(DocTree.Controller.VIEW_RENAMED_FOLDRE          ,this.onViewRenamedFolder);
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


    ,_objIType: null
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
        var key = this.getObjType() + "." + this.getObjType();
        key += "." + Acm.goodValue(folderId, 0);    //for root folder, folderId is 0 or undefined
        key += "." + Acm.goodValue(pageId, 0);
        key += "." + Acm.goodValue(setting.sortBy);
        key += "." + Acm.goodValue(setting.sortDirection);
        key += "." + Acm.goodValue(setting.maxRows, 0);
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


        ,_setting: {
//            objType: null
//            ,objId: 0
//            ,category: "Document"
            maxRows: 4
            ,sortBy: null
            ,sortDirection: null
            //,folderId: 0
            //,start: 0
            //,totalChildren: 0
        }
        ,getSetting: function() {
            return this._setting;
        }
    }

};

