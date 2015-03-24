/**
 * DocTree.Model
 *
 * @author jwu
 */
DocTree.Model = DocTree.Model || {
    create : function(args) {
        this.cacheTree = new Acm.Model.CacheFifo();


        Acm.Dispatcher.addEventListener(DocTree.Controller.VIEW_ADDED_FOLDER            ,this.onViewAddedFolder);
        Acm.Dispatcher.addEventListener(DocTree.Controller.VIEW_ADDED_DOCUMENT          ,this.onViewAddedDocument);
        Acm.Dispatcher.addEventListener(DocTree.Controller.VIEW_RENAMED_FOLDRE          ,this.onViewRenamedFolder);
        Acm.Dispatcher.addEventListener(DocTree.Controller.VIEW_RENAMED_DOCUMENT        ,this.onViewRenamedDocument);

        if (DocTree.Service.create)              {DocTree.Service.create(args);}
    }
    ,onInitialized: function() {
        if (DocTree.Service.onInitialized)              {DocTree.Service.onInitialized();}
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

};

