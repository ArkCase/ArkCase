/**
 * ObjNav.Model
 *
 * @author jwu
 */
ObjNav.Model = {
    create : function(args) {
        this.interface = args.modelInterface;

        if (ObjNav.Model.Tree.create)        {ObjNav.Model.Tree.create(args);}
        if (ObjNav.Model.List.create)        {ObjNav.Model.List.create(args);}
        if (ObjNav.Model.Detail.create)      {ObjNav.Model.Detail.create(args);}
        if (ObjNav.Service.create)           {ObjNav.Service.create(args);}
    }
    ,onInitialized: function() {
        if (ObjNav.Model.Tree.onInitialized)    {ObjNav.Model.Tree.onInitialized();}
        if (ObjNav.Model.List.onInitialized)    {ObjNav.Model.List.onInitialized();}
        if (ObjNav.Model.Detail.onInitialized)  {ObjNav.Model.Detail.onInitialized();}
        if (ObjNav.Service.onInitialized)       {ObjNav.Service.onInitialized();}

        var treeInfo = ObjNav.Model.Tree.Config.getTreeInfo();
        ObjNav.Model.retrieveData(treeInfo);
    }

    //to fix later
    ,onTopbarViewSetAsnData: function(asnData) {
        if (ObjNav.Model.Tree.Config.validateTreeInfo(asnData)) {
            if ("/plugin/casefile" == asnData.name) {
                var treeInfo = ObjNav.Model.Tree.Config.getTreeInfo();
                var sameResultSet = ObjNav.Model.Tree.Config.sameResultSet(asnData);
                ObjNav.Model.Tree.Config.readTreeInfo();

                if (!sameResultSet) {
                    CaseFile.Model.retrieveData(treeInfo);
                }
                return true;
            }
        }
        return false;
    }

    ,retrieveData: function(treeInfo) {
        if (0 < treeInfo.objId) { //single caseFile
            ObjNav.Model.setObjectId(treeInfo.objId);
            ObjNav.Model.setObjectType(treeInfo.objType);
            ObjNav.Service.Detail.retrieveObject(treeInfo.objType, treeInfo.objId);

        } else {
            ObjNav.Service.List.retrieveObjectList(treeInfo);
        }
    }

    ,_objectType: null
    ,getObjectType: function() {
        return this._objectType;
    }
    ,setObjectType: function(objectType) {
        this._objectType = objectType;
    }

    ,_objectId: 0
    ,getObjectId : function() {
        return this._objectId;
    }
    ,setObjectId : function(objectId) {
        this._objectId = objectId;
    }

    ,Tree: {
        create : function(args) {
            if (ObjNav.Model.Tree.Config.create)    {ObjNav.Model.Tree.Config.create(args);}
            if (ObjNav.Model.Tree.Key.create)       {ObjNav.Model.Tree.Key.create(args);}
        }
        ,onInitialized: function() {
            if (ObjNav.Model.Tree.Config.onInitialized)    {ObjNav.Model.Tree.Config.onInitialized();}
            if (ObjNav.Model.Tree.Key.onInitialized)       {ObjNav.Model.Tree.Key.onInitialized();}
        }

        ,Config: {
            create: function(args) {
                this.setName(args.name);
                this.readTreeInfo();
            }
            ,onInitialized: function() {
            }

            ,_treeInfo: {
                name            : null
                ,start          : 0
                ,n              : 50
                ,total          : -1
                ,sort           : null
                ,filter         : null
                ,q              : null
                ,key            : null
                ,objId          : 0
                ,objType        : null
            }
            ,getTreeInfo: function() {
                return this._treeInfo;
            }
            ,getPageId: function() {
                return this._treeInfo.start;
            }
            ,getName: function() {
                return this._treeInfo.name;
            }
            ,setName: function(name) {
                this._treeInfo.name = name;
            }

            ,validateTreeInfo: function(data) {
                if (Acm.isEmpty(data)) {
                    return false;
                }
                if (Acm.isEmpty(data.name)) {
                    return false;
                }
                if (0 < Acm.goodValue(data.objId, 0)) {
                    if (Acm.isEmpty(data.objType)) {
                        return false;
                    }
//                    if (Acm.isNotEmpty(data.key)) {
//                        return false;
//                    }
                }

                return true;
            }
            ,readTreeInfo: function() {
                this._initTreeInfo = new Acm.Model.SessionData("AcmTreeInfo");

                var ti = this.getTreeInfo();
                var tiInit = this._initTreeInfo.get();
                if (this.validateTreeInfo(tiInit)) {
                    if (ti.name == Acm.goodValue(tiInit.name)) {
                        //ti.name    = Acm.goodValue(tiInit.name);
                        ti.start   = Acm.goodValue(tiInit.start, 0);
                        ti.n       = Acm.goodValue(tiInit.n, 50);
                        ti.sort    = Acm.goodValue(tiInit.sort, null);
                        ti.filter  = Acm.goodValue(tiInit.filter, null);
                        ti.q       = Acm.goodValue(tiInit.q, null);
                        ti.key     = Acm.goodValue(tiInit.key, null);
                        ti.objId   = Acm.goodValue(tiInit.objId, 0);
                        ti.objType = Acm.goodValue(tiInit.objType, 0);

                        this._initTreeInfo.set(null);
                    }
                }

                if (0 == ti.objId && null == ti.key) {
                    var objId = Acm.Object.MicroData.get("objId");
                    var objType = Acm.Object.MicroData.get("objType");
                    if (Acm.isNotEmpty(objId)) {
                        ti.objId = objId;
                        ti.objType = objType;
                    }
                }
            }
            ,sameResultSet: function(treeInfo) {
                if (!Acm.compare(this._treeInfo.name, treeInfo.name)) {
                    return false;
                }

                if (0 < this._treeInfo.objId) {
                    if (!Acm.compare(this._treeInfo.objId, treeInfo.objId)) {
                        return false;
                    }
                    if (!Acm.compare(this._treeInfo.objType, treeInfo.objType)) {
                        return false;
                    }
                }

                if (!Acm.compare(this._treeInfo.start, treeInfo.start)) {
                    return false;
                }
                if (!Acm.compare(this._treeInfo.sort, treeInfo.sort)) {
                    return false;
                }
                if (!Acm.compare(this._treeInfo.filter, treeInfo.filter)) {
                    return false;
                }
                if (!Acm.compare(this._treeInfo.n, treeInfo.n)) {
                    return false;
                }

                return true;
            }
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
            //,NODE_TYPE_PART_OBJECT       : "o"


            ,getTabIdsByKey: function(key) {
                var nodeTypeMap = ObjNav.Model.interface.nodeTypeMap();
                var tabIds = [];
                //var tabIds = ["tabBlank"];
                if (Acm.isNotEmpty(key)) {
                    var nodeType = this.getNodeTypeByKey(key);
                    for (var i = 0; i < nodeTypeMap.length; i++) {
                        if (nodeType == nodeTypeMap[i].nodeType) {
                            tabIds = nodeTypeMap[i].tabIds;
                            break;
                        }
                    }
                }
                return tabIds;
            }
            ,getIconByKey: function(key) {
                var nodeTypeMap = ObjNav.Model.interface.nodeTypeMap();
                var icon = null;
                if (Acm.isNotEmpty(key)) {
                    var nodeType = this.getNodeTypeByKey(key);
                    for (var i = 0; i < nodeTypeMap.length; i++) {
                        if (nodeType == nodeTypeMap[i].nodeType) {
                            icon = nodeTypeMap[i].icon;
                            break;
                        }
                    }
                }
                return icon;
            }
            ,getTabIds: function() {
                var nodeTypeMap = ObjNav.Model.interface.nodeTypeMap();
                var tabIds = [];
                for (var i = 0; i < nodeTypeMap.length; i++) {
                    var tabIdsThis = nodeTypeMap[i].tabIds;
                    for (var j = 0; j < tabIdsThis.length; j++) {
                        var tabId = tabIdsThis[j];
                        if (!Acm.isItemInArray(tabId, tabIds)) {
                            tabIds.push(tabId);
                        }
                    }
                }
                return tabIds;
            }
            ,getNodeTypeByKey: function(key) {
                var nt = "";
                if (Acm.isNotEmpty(key)) {
                    var arr = key.split(this.KEY_SEPARATOR);
                    for (var i = 0; i < arr.length; i++) {
                        var typeAndId = arr[i].split(this.TYPE_ID_SEPARATOR);
                        if (0 < i) {
                            nt += this.KEY_SEPARATOR;
                        }
                        nt += typeAndId[0];
                    }
                }
                return nt;
            }
            ,getNodeIdByKey: function(key) {
                var id = "";
                if (Acm.isNotEmpty(key)) {
                    var arr = key.split(this.KEY_SEPARATOR);
                    var lastPart = arr[arr.length - 1];
                    var typeAndId = lastPart.split(this.TYPE_ID_SEPARATOR);
                    if (1 < typeAndId.length) {
                        id = typeAndId[1];
                    }
                }
                return id;
            }

            ,getObjIdByKey: function(key) {
                var objId = "";
                if (Acm.isNotEmpty(key)) {
                    var arr = key.split(this.KEY_SEPARATOR);
                    if (1 < arr.length) {
                        var objPart = arr[1];
                        var typeAndId = objPart.split(this.TYPE_ID_SEPARATOR);
                        if (1 < typeAndId.length) {
                            objId = typeAndId[1];
                        }
                    }
                }
                return objId;
            }
            ,getObjTypeByKey: function(key) {
                var nt = "";
                if (Acm.isNotEmpty(key)) {
                    var arr = key.split(this.KEY_SEPARATOR);
                    if (1 < arr.length) {
                        var objPart = arr[1];
                        var typeAndId = objPart.split(this.TYPE_ID_SEPARATOR);
                        if (1 < typeAndId.length) {
                            nt = typeAndId[0];
                        }
                    }
                }
                return nt;
                //return this.nodeTypeToObjType(nt);
            }
            ,getPageIdByKey: function(key) {
                var pageId = "";
                if (Acm.isNotEmpty(key)) {
                    var arr = key.split(this.KEY_SEPARATOR);
                    if (0 < arr.length) {
                        var pagePart = arr[0];
                        var typeAndId = pagePart.split(this.TYPE_ID_SEPARATOR);
                        if (1 < typeAndId.length) {
                            pageId = typeAndId[1];
                        }
                    }
                }
                return pageId;
            }

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
            //typeParts is string array: ["t1","t2", ....]
            ,makeNodeType: function(typeParts) {
                var nodeType = "";
                if (Acm.isArray(typeParts)) {
                    for (var i = 0; i < typeParts.length; i++) {
                        if (Acm.isNotEmpty(nodeType)) {
                            nodeType += this.KEY_SEPARATOR;
                        }
                        nodeType += typeParts[i];
                    } //for i
                }
                return nodeType;
            }

            ,getKeyPrevPage: function() {
                return this.NODE_TYPE_PART_PREV_PAGE;
            }
            ,getKeyNextPage: function() {
                return this.NODE_TYPE_PART_NEXT_PAGE;
            }

            ,getKeyByObj: function(objNoteType, objId) {
                var pageId = ObjNav.Model.Tree.Config.getPageId();
                return this.getKeyByObjWithPage(pageId, objNoteType, objId);
            }
            ,getKeyByObjWithPage: function(pageId, objNoteType, objId) {
                var subKey = objNoteType
                        + this.TYPE_ID_SEPARATOR
                        + objId
                    ;
                return this.getKeyBySubWithPage(pageId, subKey);
            }
            ,getKeyBySubWithPage: function(pageId, subKey) {
                return this.NODE_TYPE_PART_PAGE
                    + this.TYPE_ID_SEPARATOR
                    + pageId
                    + this.KEY_SEPARATOR
                    + subKey
                    ;
            }
        }
    }


    ,List: {
        create : function(args) {
            this.cachePage = new Acm.Model.CacheFifo();

            Acm.Dispatcher.addEventListener(ObjNav.Controller.VIEW_CLICKED_PREV_PAGE      ,this.onViewPrevPageClicked);
            Acm.Dispatcher.addEventListener(ObjNav.Controller.VIEW_CLICKED_NEXT_PAGE      ,this.onViewNextPageClicked);
            Acm.Dispatcher.addEventListener(ObjNav.Controller.VIEW_CHANGED_TREE_FILTER    ,this.onViewChangedTreeFilter);
            Acm.Dispatcher.addEventListener(ObjNav.Controller.VIEW_CHANGED_TREE_SORT      ,this.onViewChangedTreeSort);
        }
        ,onInitialized: function() {
        }

        ,onViewPrevPageClicked: function() {
            ObjNav.Model.setObjectId(0);
            ObjNav.Model.setObjectType(null);

            var treeInfo = ObjNav.Model.Tree.Config.getTreeInfo();
            if (0 < treeInfo.start) {
                treeInfo.start -= treeInfo.n;
                if (0 > treeInfo.start) {
                    treeInfo.start = 0;
                }
            }
            ObjNav.Service.List.retrieveObjectList(treeInfo);
        }
        ,onViewNextPageClicked: function() {
            ObjNav.Model.setObjectId(0);
            ObjNav.Model.setObjectType(null);

            var treeInfo = ObjNav.Model.Tree.Config.getTreeInfo();
            if (0 > treeInfo.total) {       //should never get to this condition
                treeInfo.start = 0;
            } else if ((treeInfo.total - treeInfo.n) > treeInfo.start) {
                treeInfo.start += treeInfo.n;
            }
            ObjNav.Service.List.retrieveObjectList(treeInfo);
        }
        ,onViewChangedTreeFilter: function(filter) {
            var treeInfo = ObjNav.Model.Tree.Config.getTreeInfo();
            if (!Acm.compare(treeInfo.filter, filter)) {
                ObjNav.Model.setObjectId(0);
                ObjNav.Model.setObjectType(null);
                treeInfo.start = 0;
                treeInfo.filter = filter;
                ObjNav.Service.List.retrieveObjectList(treeInfo);
            }
        }
        ,onViewChangedTreeSort: function(sort) {
            var treeInfo = ObjNav.Model.Tree.Config.getTreeInfo();
            if (!Acm.compare(treeInfo.sort, sort)) {
                ObjNav.Model.setObjectId(0);
                ObjNav.Model.setObjectType(null);
                treeInfo.start = 0;
                treeInfo.sort = sort;
                ObjNav.Service.List.retrieveObjectList(treeInfo);
            }
        }

        ,getSolrObject: function(nodeType, nodeId) {
            var solr = null;
            var treeInfo = ObjNav.Model.Tree.Config.getTreeInfo();
            var objList = ObjNav.Model.List.cachePage.get(treeInfo.start);
            if (this.validateObjList(objList)) {
                for (var i = 0; i < objList.length; i++) {
                    if (nodeId == ObjNav.Model.interface.nodeId(objList[i]) && nodeType == ObjNav.Model.interface.nodeType(objList[i])) {
                        solr = objList[i];
                        break;
                    }
                }
            }
            return solr;
        }

        ,validateObjList: function(data) {
            if (Acm.isNotArray(data)) {
                return false;
            }

            return true;
        }
        ,validateObjSolr: function(data) {
            if (Acm.isEmpty(data)) {
                return false;
            }

            return true;
        }

    }

    ,Detail: {
        create : function(args) {
            this.cacheObject = new Acm.Model.CacheFifo();

            Acm.Dispatcher.addEventListener(ObjNav.Controller.VIEW_SELECTED_OBJECT          ,this.onViewSelectedObject);
        }
        ,onInitialized: function() {
        }

        ,onViewSelectedObject: function(objType, objId) {
            ObjNav.Model.setObjectId(objId);
            ObjNav.Model.setObjectType(objType);
            var objData = ObjNav.Model.Detail.getCacheObject(objType, objId);
            if (!objData) {
                ObjNav.Service.Detail.retrieveObject(objType, objId);
            }
        }

        ,getCacheObject: function(objType, objId) {
            var key = objType + ObjNav.Model.Tree.Key.TYPE_ID_SEPARATOR + objId;
            return ObjNav.Model.Detail.cacheObject.get(key);
        }
        ,putCacheObject: function(objType, objId, objData) {
            var key = objType + ObjNav.Model.Tree.Key.TYPE_ID_SEPARATOR + objId;
            return ObjNav.Model.Detail.cacheObject.put(key, objData);
        }
        ,validateObjData: function(data) {
            if (Acm.isEmpty(data)) {
                return false;
            }

            return true;
        }
    }
};

