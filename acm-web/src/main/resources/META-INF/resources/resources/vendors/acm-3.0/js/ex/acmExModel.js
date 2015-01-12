/**
 * AcmEx.Model
 *
 * @author jwu
 */
AcmEx.Model = {
    create : function() {
        if (AcmEx.Model.Tree.create)              {AcmEx.Model.Tree.create();}
    }
    ,onInitialize : function() {
        if (AcmEx.Model.Tree.onInitialized)       {AcmEx.Model.Tree.onInitialized();}
    }

    ,Tree: {
        create : function() {
            if (AcmEx.Model.Tree.Config.create)    {AcmEx.Model.Tree.Config.create();}
            if (AcmEx.Model.Tree.Key.create)       {AcmEx.Model.Tree.Key.create();}
        }
        ,onInitialized: function() {
            if (AcmEx.Model.Tree.Config.onInitialized)    {AcmEx.Model.Tree.Config.onInitialized();}
            if (AcmEx.Model.Tree.Key.onInitialized)       {AcmEx.Model.Tree.Key.onInitialized();}
        }

        ,Config: {
            create: function() {
                this.readTreeInfo();
            }
            ,onInitialized: function() {
            }

            ,_treeInfo: {
                name            : ""
                ,start          : 0
                ,n              : 50
                ,total          : -1
                ,sort           : null
                ,filter         : null
                ,q              : null
                ,key            : null
                ,objId          : 0
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
                    return Acm.isEmpty(data.key);
                }
                if (Acm.isNotEmpty(data.key)) {
                    return 0 == Acm.goodValue(data.objId, 0);
                }
                return true;
            }
            ,readTreeInfo: function() {
                this._initTreeInfo = new Acm.Model.SessionData("AcmTreeInfo");

                var ti = this.getTreeInfo();
                var tiInit = this._initTreeInfo.get();
                if (this.validateTreeInfo(tiInit)) {
                    if (this.getName() == Acm.goodValue(tiInit.name)) {
                        ti.start  = Acm.goodValue(tiInit.start, 0);
                        ti.n      = Acm.goodValue(tiInit.n, 50);
                        ti.sort   = Acm.goodValue(tiInit.sort, null);
                        ti.filter = Acm.goodValue(tiInit.filter, null);
                        ti.q      = Acm.goodValue(tiInit.q, null);
                        ti.key    = Acm.goodValue(tiInit.key, null);
                        ti.objId  = Acm.goodValue(tiInit.objId, 0);

                        this._initTreeInfo.set(null);
                    }
                }

                if (0 == ti.objId && null == ti.key) {
                    var objId = Acm.Object.MicroData.get("objId");
                    if (Acm.isNotEmpty(objId)) {
                        ti.objId = objId;
                    }
                }
            }
            ,sameResultSet: function(treeInfo) {
                if (!Acm.compare(this._treeInfo.name, treeInfo.name)) {
                    return false;
                }

                if (0 < this._treeInfo.objId) {
                    return Acm.compare(this._treeInfo.objId, treeInfo.objId);
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
            create: function() {
            }
            ,onInitialized: function() {
            }

            ,KEY_SEPARATOR               : "/"
            ,TYPE_ID_SEPARATOR           : "."
            ,NODE_TYPE_PART_PREV_PAGE    : "prevPage"
            ,NODE_TYPE_PART_NEXT_PAGE    : "nextPage"
            ,NODE_TYPE_PART_PAGE         : "p"

//            ,NODE_TYPE_PART_OBJECT       : "c"


            ,_nodeTypeMap: [
                 {nodeType: "prevPage" ,icon: "i-arrow-up"   ,tabIds: ["tabBlank"]}
                ,{nodeType: "nextPage" ,icon: "i-arrow-down" ,tabIds: ["tabBlank"]}
                ,{nodeType: "p"        ,icon: ""             ,tabIds: ["tabBlank"]}
            ]
            ,setNodeTypeMap: function(nodeTypeMap) {
                this._nodeTypeMap = nodeTypeMap;
            }

            ,getTabIdsByKey: function(key) {
                var nodeType = this.getNodeTypeByKey(key);
                //var tabIds = ["tabBlank"];
                var tabIds = [];
                for (var i = 0; i < this._nodeTypeMap.length; i++) {
                    if (nodeType == this._nodeTypeMap[i].nodeType) {
                        tabIds = this._nodeTypeMap[i].tabIds;
                        break;
                    }
                }
                return tabIds;
            }
            ,getIconByKey: function(key) {
                var nodeType = this.getNodeTypeByKey(key);
                var icon = null;
                for (var i = 0; i < this._nodeTypeMap.length; i++) {
                    if (nodeType == this._nodeTypeMap[i].nodeType) {
                        icon = this._nodeTypeMap[i].icon;
                        break;
                    }
                }
                return icon;
            }
            ,getTabIds: function() {
                var tabIds = [];
                for (var i = 0; i < this._nodeTypeMap.length; i++) {
                    var tabIdsThis = this._nodeTypeMap[i].tabIds;
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
        }
    }

}