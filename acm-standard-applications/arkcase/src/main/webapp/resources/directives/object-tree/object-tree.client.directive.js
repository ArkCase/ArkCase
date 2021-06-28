'use strict';

/**
 * @ngdoc directive
 * @name global.directive:objectTree
 * @restrict E
 *
 * @description
 *
 * {@link /acm-standard-applications/arkcase/src/main/webapp/resources/directives/object-tree/object-tree.client.directive.js directives/object-tree/object-tree.client.directive.js}
 *
 * The objectTree directive renders a FancyTree to browse ArkCase objects with support of paging, filter and sort
 *
 * @param {Expression} tree-config Configuration for tree
 * @param {Expression} tree-data Data structure used to render the top level tree nodes of the current page
 * @param {Function} on-reset Callback function to reset tree to initial state.
 * @param {Function} on-select Callback function in response to selected tree item.
 * @param {Function} on-load Callback function to load list of objects.
 * @param {Object} tree-control Tree API functions exposed to user. Following is the list:
 * @param {Function} tree-control.setTitle Set title of a tree node
 * @param {Function} tree-control.select Select a tree node with specified key
 *
 * @example
 <example>
 <file name="index.html">
 <object-tree tree-config="treeConfig" tree-data="treeData" on-reset="onReset" on-load="onLoad" on-select="onSelect">
 </object-tree>
 </file>
 <file name="app.js">
 angular.module('ngAppDemo', []).controller('ngAppDemoController', function($scope, $log) {
 $scope.treeConfig = {
 "pageSize": 2,
 "filters": [
 {
 "desc": "All Open Cases",
 "name": "all-open-cases",
 "value": "fq=-status_lcs:COMPLETE AND -status_lcs:DELETE AND -status_lcs:CLOSED",
 "default": true
 },
 {
 "desc": "All I've Created",
 "name": "my-created-cases",
 "value": "fq=creator_lcs:${user}"
 }
 ],
 "sorters": [
 {
 "desc": "Sort Date Asc",
 "name": "sort-date-asc",
 "value": "create_date_tdt ASC"
 },
 {
 "desc": "Sort By Case Name",
 "name": "sort-by-name-asc",
 "value": "name ASC"
 },
 {
 "desc": "(No Sort)",
 "name": "",
 "value": "",
 "default": true
 }
 ],
 "nodeTypes": [
 {
 "type": "prev",
 "icon": "fa fa-arrow-up"
 },
 {
 "type": "next",
 "icon": "fa fa-arrow-down"
 },
 {
 "type": "p/CASE_FILE",
 "icon": "fa fa-folder",
 "components": [
 "details",
 "people",
 "documents"
 ]
 },
 {
 "type": "p/CASE_FILE/det",
 "label": "Details",
 "components": [
 "details"
 ]
 },
 {
 "type": "p/CASE_FILE/ppl",
 "label": "People",
 "components": [
 "people"
 ]
 },
 {
 "type": "p/CASE_FILE/doc",
 "label": "Documents",
 "components": [
 "documents"
 ]
 }
 ]
 };

 var page1 = [
 {nodeId: 101, nodeType: "CASE_FILE", nodeTitle: 'Case 101', nodeToolTip: 'Case 101'},
 {nodeId: 102, nodeType: "CASE_FILE", nodeTitle: 'Case 102', nodeToolTip: 'Case 102'}
 ];
 var page2 = [
 {nodeId: 201, nodeType: "CASE_FILE", nodeTitle: 'Case 201', nodeToolTip: 'Case 201'},
 {nodeId: 202, nodeType: "CASE_FILE", nodeTitle: 'Case 202', nodeToolTip: 'Case 202'}
 ];
 var page3 = [
 {nodeId: 301, nodeType: "CASE_FILE", nodeTitle: 'Case 301', nodeToolTip: 'Case 301'}
 ];

 $scope.treeData = page1;

 $scope.onLoad = function(start, n, sort, filters){
 //query list of objects according to the parameters. Only consider 'start' here:
 if (0 == start) {
 return page1;
 } else if (2 == start) {
 return page2;
 } else if (4 == start) {
 return page3;
 } else {
 return [];
 }
 };

 $scope.onSelect = function(selectedObject){
 $log.debug(selectedObject)
 };
 });
 </file>
 </example>
 */
angular.module('directives').directive('objectTree', [ '$q', '$translate', 'UtilService', 'Acm.StoreService', 'Helper.ObjectBrowserService', 'Authentication',
    function($q, $translate, Util, Store, HelperObjectBrowserService, Authentication) {
    var Tree = {
        create: function(treeArgs) {
            Tree.Info.create({
                name: "ObjectTree"
            });

            var treeArgsToUse = this._getDefaultTreeArgs();
            _.merge(treeArgsToUse, treeArgs);
            Tree.jqDivTree.fancytree(treeArgsToUse);
            Tree.tree = Tree.jqDivTree.fancytree('getTree');
        },
        select: function(args, activate) {
            var treeInfo = Tree.Info.getTreeInfo();
            var pageStart = Util.goodValue(args.pageStart, treeInfo.start);
            var nodeType = args.nodeType;
            var nodeId = args.nodeId;
            var subKey = args.subKey;

            var key = Tree.Key.getKeyByObjWithPage(treeInfo.start, nodeType, nodeId);
            if (!Util.isEmpty(subKey)) {
                key += Tree.Key.KEY_SEPARATOR;
                key += subKey;
            }
            treeInfo.key = key;

            if (activate) {
                var node = Tree.tree.getNodeByKey(key);
                if (node) {
                    node.setActive();
                    node.setFocus();
                }
            }
        },
        selectComponent: function(nodeType, nodeId, component) {
            var key = Tree.Key.getKeyByObj(nodeType, nodeId);
            var node = Tree.tree.getNodeByKey(key);
            if (node) {
                var subKey = null;
                if (node.expanded) {
                    subKey = Tree.getSubKeyByComponent(component);
                }
                Tree.select({
                    pageStart: null,
                    nodeType: nodeType,
                    nodeId: nodeId,
                    subKey: subKey
                }, true);

            }
        },
        setTitle: function(nodeType, nodeId, nodeTitle, nodeToolTip) {
            var key = Tree.Key.getKeyByObj(nodeType, nodeId);
            var node = Tree.tree.getNodeByKey(key);
            if (node) {
                node.setTitle(nodeTitle);
                Tree.fixNodeIcon(node);
            }
        }

        ,
        refreshTree: function(key) {
            //this.tree.reload().done(function () {
            this.tree.reload(Tree.getSource()).done(function() {
                if (!Util.isEmpty(key)) {
                    var parts = key.split(Tree.Key.KEY_SEPARATOR);
                    if (parts && 1 < parts.length) {
                        var promiseClosestParentExpanded = null;
                        var parentNode = null;
                        var parentKey = parts[0];
                        //exclude page ID, so start from 1; expand parents only, not include self, so length-1
                        for (var i = 1; i < parts.length - 1; i++) {
                            parentKey += Tree.Key.KEY_SEPARATOR + parts[i];
                            parentNode = Tree.tree.getNodeByKey(parentKey);
                            if (parentNode) {
                                promiseClosestParentExpanded = parentNode.setExpanded(true);
                            }
                        }
                    }

                    Tree.tree.activateKey(key);
                }
            });
        }

        ,
        refresh: function() {
            Tree.onReset()();
            Tree.reset();

            var treeInfo = Tree.Info.getTreeInfo();
            treeInfo.sorter = treeInfo.defaultSorter;
            treeInfo.filter = treeInfo.defaultFilter;

            Tree.onLoad()(treeInfo.start, treeInfo.n, treeInfo.sorter, treeInfo.filter, treeInfo.searchQuery);
        }

        ,
        expandAll: function() {
            Tree.tree.visit(function(node) {
                node.setExpanded();
            });
        }

        ,
        collapseAll: function() {
            Tree.tree.visit(function(node) {
                node.setExpanded(false);
            });
        }

        ,
        _previousKey: null,
        _activeKey: null,
        getPreviousKey: function() {
            return this._previousKey;
        },
        getActiveKey: function() {
            return this._activeKey;
        },
        setActiveKey: function(activeKey) {
            this._previousKey = this._activeKey;
            this._activeKey = activeKey;
        }

        ,
        _nodeType: null,
        getNodeType: function() {
            return this._nodeType;
        },
        setNodeType: function(nodeType) {
            this._nodeType = nodeType;
        }

        ,
        _nodeId: 0,
        getNodeId: function() {
            return this._nodeId;
        },
        setNodeId: function(nodeId) {
            this._nodeId = nodeId;
        }

        ,
        reset: function() {
            this._previousKey = null;
            this._activeKey = null;
            this._nodeType = null;
            this._nodeId = 0;

            Tree.Info.reset();
        },
        fixNodeIcon: function(node) {
            var key = node.key;
            var nodeType = Tree.Key.getNodeTypeByKey(key);
            var nodeIcon = Tree.getIconByKey(key);
            if (nodeIcon) {
                var span = node.span;
                var $spanIcon = $(span.children[1]);
                $spanIcon.removeClass("fancytree-icon");
                $spanIcon.html("<i class='" + nodeIcon + " " + node.data.nodeStatusColor + " ' title='" + node.data.nodeStatus + "'></i>");
            }
        },
        getIconByKey: function(key) {
            var icon = null;
            if (!Util.isEmpty(key)) {
                var nodeType = Tree.Key.getNodeTypeByKey(key);
                var nodeTypes = Util.goodMapValue(Tree, "treeConfig.nodeTypes", []);
                for (var i = 0; i < nodeTypes.length; i++) {
                    if (nodeType == nodeTypes[i].type) {
                        icon = nodeTypes[i].icon;
                        break;
                    }
                }
            }
            return icon;
        },
        getComponentsByKey: function(key) {
            var components = null;
            if (!Util.isEmpty(key)) {
                var nodeType = Tree.Key.getNodeTypeByKey(key);
                var nodeTypes = Util.goodMapValue(Tree, "treeConfig.nodeTypes", []);
                for (var i = 0; i < nodeTypes.length; i++) {
                    if (nodeType == nodeTypes[i].type) {
                        components = nodeTypes[i].components;
                        break;
                    }
                }
            }
            return components;
        },
        getLeadComponentByKey: function(key) {
            var leadComponent = null;
            if (!Util.isEmpty(key)) {
                var nodeType = Tree.Key.getNodeTypeByKey(key);
                var nodeTypes = Util.goodMapValue(Tree, "treeConfig.nodeTypes", []);
                for (var i = 0; i < nodeTypes.length; i++) {
                    if (nodeType == nodeTypes[i].type) {
                        leadComponent = nodeTypes[i].leadComponent;
                        break;
                    }
                }
            }
            return leadComponent;
        },
        getSubKeyByComponent: function(component) {
            var nt = null;
            if (!Util.isEmpty(component)) {
                var nodeTypes = Util.goodMapValue(Tree, "treeConfig.nodeTypes", []);
                for (var i = 0; i < nodeTypes.length; i++) {
                    var aComponent = Util.goodMapValue(nodeTypes[i], "components[0]", null);

                    if ("main" == component) {
                        if (nodeTypes[i].components) {
                            if (1 < nodeTypes[i].components.length) {
                                nt = nodeTypes[i].type;
                                break;
                            }
                        }
                    } else if (aComponent == component) {
                        if (1 == nodeTypes[i].components.length) {
                            nt = nodeTypes[i].type;
                            break;
                        }
                    }
                }
            }
            var subKey = null;
            if (nt) {
                var arr = nt.split(Tree.Key.KEY_SEPARATOR);
                if (3 == arr.length) {
                    subKey = arr[2];
                }
            }
            return subKey;
        },
        getHiddenByKey: function(key) {
            var hidden = false;
            if (!Util.isEmpty(key)) {
                var nodeType = Tree.Key.getNodeTypeByKey(key);
                var nodeTypes = Util.goodMapValue(Tree, "treeConfig.nodeTypes", []);
                for (var i = 0; i < nodeTypes.length; i++) {
                    if (nodeType == nodeTypes[i].type) {
                        hidden = nodeTypes[i].hidden ? true : false;
                        break;
                    }
                }
            }
            return hidden;
        }

        ,
        _getDefaultTreeArgs: function(treeArgs) {
            return {
                source: Tree.getSource(),
                lazyLoad: Tree.lazyLoad,
                activate: function(event, data) {
                    Tree.onActivate(data.node);
                    },
                beforeActivate: function(event, data) {//todo: use to check dirty data
                    return true;
                    },
                renderNode: function(event, data) {
                    Tree.fixNodeIcon(data.node);
                }


            }
        },
        onActivate: function(node) {
            Tree.setActiveKey(node.key);

            if (Tree.Key.getKeyPrevPage() == node.key) {
                Tree.setNodeId(0);
                Tree.setNodeType(null);

                var treeInfo = Tree.Info.getTreeInfo();
                if (0 < treeInfo.start) {
                    treeInfo.start -= treeInfo.n;
                    if (0 > treeInfo.start) {
                        treeInfo.start = 0;
                    }
                }
                Tree.onLoad()(treeInfo.start, treeInfo.n, treeInfo.sorter, treeInfo.filter, treeInfo.searchQuery);

            } else if (Tree.Key.getKeyNextPage() == node.key) {
                Tree.setNodeId(0);
                Tree.setNodeType(null);

                var treeInfo = Tree.Info.getTreeInfo();
                if (0 > treeInfo.total) { //should never get to this condition
                    treeInfo.start = 0;
                } else if ((treeInfo.total - treeInfo.n) > treeInfo.start) {
                    treeInfo.start += treeInfo.n;
                }
                Tree.onLoad()(treeInfo.start, treeInfo.n, treeInfo.sorter, treeInfo.filter, treeInfo.searchQuery);

            } else {
                var activeKey = Tree.getActiveKey();
                var nodeId = Tree.Key.getNodeIdByKey(activeKey);
                var nodeTypePath = Tree.Key.getNodeTypeByKey(activeKey);

                var previousKey = Tree.getPreviousKey();
                var previousNodeId = Tree.Key.getNodeIdByKey(previousKey);
                var previousNodeTypePath = Tree.Key.getNodeTypeByKey(previousKey);

                if (nodeId != previousNodeId || nodeTypePath != previousNodeTypePath) {
                    Tree.onSelect()(node.data);
                }
            }
        },
        getSource: function() {
            var treeData = Util.goodValue(Tree.treeData, {
                    total: 0,
                    docs: []
                });
                var treeInfo = Tree.Info.getTreeInfo();
                treeInfo.pageSize = Util.goodMapValue(Tree, "treeConfig.pageSize", Tree.Info.DEFAULT_PAGE_SIZE);
                treeInfo.total = treeData.total;

                var objList = Util.goodMapValue(treeData, "docs", []);
                if (Util.isArrayEmpty(objList)) {
                    return [];
                }

                var builder = Util.FancyTreeBuilder.reset();

                if (0 < treeInfo.start) {
                    builder.addLeaf({
                        key: Tree.Key.NODE_TYPE_PART_PREV_PAGE,
                        title: treeInfo.start + $translate.instant("common.directive.objectTree.btnPrev.title"),
                        tooltip: $translate.instant("common.directive.objectTree.btnPrev.toolTip"),
                        tooltipLabel: "common.directive.objectTree.btnPrev.toolTip",
                        expanded: false,
                        folder: false
                    });
                }

                _.each(objList, function(obj) {
                    var nodeId = obj.nodeId;
                    var nodeType = obj.nodeType;
                    var nodeStatus = obj.nodeStatus;
                    var nodeStatusColor = obj.nodeStatusColor;
                    var nodeTitleLabel = obj.nodeTitleLabel;
                    var nodeTitle;
                    if(!Util.isEmpty(treeData.configTitleList) && !Util.isEmpty(treeData.configTitleList[obj.nodeType])){
                        var configurationTitle = treeData.configTitleList[obj.nodeType].title;
                        if(configurationTitle === "objectId")
                        {
                            nodeTitle = obj.nodeId;
                        }
                        else if(configurationTitle === "titleTitle")
                        {
                            nodeTitle = obj.nodeTitle;
                        }
                        else if(configurationTitle === "objectIdTitle")
                        {
                            nodeTitle = obj.nodeId + " - "+ obj.nodeTitle;
                        }
                        else if(configurationTitle === "titleObjectId")
                        {
                            nodeTitle = obj.nodeTitle + " - " + obj.nodeId;
                        }
                        else if (configurationTitle === 'numberTitle')
                        {
                            nodeTitle = obj.nodeNumber + " - " + obj.nodeTitle;
                        }
                    }
                    else
                    {
                        nodeTitle = nodeTitleLabel ? $translate.instant(nodeTitleLabel) : obj.nodeTitle;
                    }
                    var nodeToolTipLabel = obj.nodeToolTipLabel;
                    var nodeToolTip = nodeToolTipLabel ? $translate.instant(nodeToolTipLabel) : obj.nodeToolTip;
                    if (nodeId && nodeType) {
                        var objKey = Tree.Key.getKeyByObjWithPage(treeInfo.start, nodeType, nodeId);
                        var components = Tree.getComponentsByKey(objKey);
                        var leadComponent = Tree.getLeadComponentByKey(objKey);
                        var hidden = Tree.getHiddenByKey(objKey);
                        builder.addLeaf({
                            key: objKey,
                            title: nodeTitle,
                            label: nodeTitleLabel,
                            tooltip: nodeToolTip,
                            tooltipLabel: nodeToolTipLabel,
                            expanded: false,
                            folder: true,
                            lazy: true,
                            cache: false,
                            components: components,
                            leadComponent: leadComponent,
                            nodeType: nodeType,
                            nodeId: nodeId,
                            nodeStatus: nodeStatus,
                            nodeStatusColor: nodeStatusColor,
                            hidden: hidden
                        });
                    }
                });
                builder.makeLast();

                if ((0 > treeInfo.total) //unknown size
                    || (treeInfo.total - treeInfo.n > treeInfo.start)) { //more page
                    var title = (0 > treeInfo.total) ? $translate.instant("common.directive.objectTree.btnNext.titleUnknownSize") : (treeInfo.total - treeInfo.start - treeInfo.n) + $translate.instant("common.directive.objectTree.btnNext.title");

                    builder.addLeafLast({
                        key: Tree.Key.NODE_TYPE_PART_NEXT_PAGE,
                        title: title,
                        tooltip: $translate.instant("common.directive.objectTree.btnNext.toolTip"),
                        tooltipLabel: "common.directive.objectTree.btnNext.toolTip",
                        expanded: false,
                        folder: false
                    });
                }
                return builder.getTree();
            }
            ,
        lazyLoad: function(event, data) {
            var builder = Util.FancyTreeBuilder.reset();
            var nodeTypes = Util.goodMapValue(Tree, "treeConfig.nodeTypes", []);
            var key = data.node.key;
            var nodeId = Tree.Key.getNodeIdByKey(key);
            var userPrivileges = Tree.scope.userPrivileges;

            for(var i = 0; i < nodeTypes.length; i++){
                if(nodeTypes[i].requiredPrivileges ){
                    for (var j = 0; j < nodeTypes[i].requiredPrivileges.length; j++){
                        if (userPrivileges && _.indexOf(userPrivileges, nodeTypes[i].requiredPrivileges[j]) === -1) {
                            nodeTypes.splice(i,1);
                            break;
                        }
                    }
                }
            }
            var nodeTypePath = Tree.Key.getNodeTypeByKey(key);
            var arr = nodeTypePath.split(Tree.Key.KEY_SEPARATOR);
            if (Util.isArray(arr) && 2 == arr.length) {
                var nodeDataType = arr[1];
                _.each(nodeTypes, function(nodeType) {
                    var type = Util.goodValue(nodeType.type);
                    var label = Util.goodValue(nodeType.label);
                    var title = $translate.instant(label);
                    var components = Util.goodArray(nodeType.components);
                    var leadComponent = nodeType.leadComponent;
                    var hidden = nodeType.hidden ? true : false;
                    if (0 == type.indexOf(nodeTypePath) && !hidden) {
                        var realLabel = label.substring(0, label.indexOf("."));
                        if (HelperObjectBrowserService.isNodeDisabled(realLabel, title))
                            return true;
                        var lastSep = type.lastIndexOf(Tree.Key.KEY_SEPARATOR);
                        if (nodeTypePath.length == lastSep) {
                            var subPart = type.substring(lastSep);
                            builder.addLeaf({
                                key: key + subPart,
                                label: label,
                                title: title,
                                components: components,
                                leadComponent: leadComponent,
                                nodeType: nodeDataType,
                                nodeId: nodeId
                            });
                        }
                    }
                });
            }

            data.result = builder.getTree();
        }

        ,
        Key: {
            KEY_SEPARATOR: "/",
            TYPE_ID_SEPARATOR: ".",
            NODE_TYPE_PART_PREV_PAGE: "prev",
            NODE_TYPE_PART_NEXT_PAGE: "next",
            NODE_TYPE_PART_PAGE: "p",
            NODE_TYPE_PART_ERROR: "err"

            ,
            getNodeTypeByKey: function(key) {
                var nt = "";
                if (!Util.isEmpty(key)) {
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
            },
            getNodeIdByKey: function(key) {
                var id = "";
                if (!Util.isEmpty(key)) {
                    var arr = key.split(this.KEY_SEPARATOR);
                    if (1 < arr.length) {
                        var idPart = arr[1];
                        var typeAndId = idPart.split(this.TYPE_ID_SEPARATOR);
                        if (1 < typeAndId.length) {
                            id = typeAndId[1];
                        }
                    }
                }
                return id;
            },
            getPageIdByKey: function(key) {
                var pageId = "";
                if (!Util.isEmpty(key)) {
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
            },
            getLastKeyPart: function(key) {
                var part = "";
                if (!Util.isEmpty(key)) {
                    var arr = key.split(this.KEY_SEPARATOR);
                    if (0 < arr.length) {
                        part = arr[arr.length - 1];
                    }
                }
                return part;
            }

            //keyParts format: [{type: "t", id: "123"}, ....]
            //Integer ID works as well: [{type: "t", id: 123}, ....]
            ,
            makeKey: function(keyParts) {
                var key = "";
                if (Util.isArray(keyParts)) {
                    for (var i = 0; i < keyParts.length; i++) {
                        if (keyParts[i].type) {
                            if (!Util.isEmpty(key)) {
                                key += this.KEY_SEPARATOR;
                            }
                            key += keyParts[i].type;

                            if (!Util.isEmpty(keyParts[i].id)) {
                                key += this.TYPE_ID_SEPARATOR;
                                key += keyParts[i].id;
                            }
                        }
                    } //for i
                }
                return key;
            }
            //typeParts is string array: ["t1","t2", ....]
            ,
            makeNodeType: function(typeParts) {
                var nodeType = "";
                if (Util.isArray(typeParts)) {
                    for (var i = 0; i < typeParts.length; i++) {
                        if (!Util.isEmpty(nodeType)) {
                            nodeType += this.KEY_SEPARATOR;
                        }
                        nodeType += typeParts[i];
                    } //for i
                }
                return nodeType;
            }

            ,
            getKeyPrevPage: function() {
                return this.NODE_TYPE_PART_PREV_PAGE;
            },
            getKeyNextPage: function() {
                return this.NODE_TYPE_PART_NEXT_PAGE;
            }

            ,
            getKeyByObj: function(objNodeType, objNodeId) {
                var pageId = Tree.Info.getPageId();
                return this.getKeyByObjWithPage(pageId, objNodeType, objNodeId);
            },
            getKeyByObjWithPage: function(pageId, objNodeType, objNodeId) {
                var subKey = objNodeType + this.TYPE_ID_SEPARATOR + objNodeId;

                return this.getKeyBySubWithPage(pageId, subKey);
            },
            getKeyBySubWithPage: function(pageId, subKey) {
                return this.NODE_TYPE_PART_PAGE + this.TYPE_ID_SEPARATOR + pageId + this.KEY_SEPARATOR + subKey;
            }
        } //Key

        ,
        Info: {
            create: function(args) {
                this.setName(args.name);
                this.readTreeInfo();
            },
            DEFAULT_PAGE_SIZE: 32,
            _treeInfo: {
                name: null
            //, start: 0
            //, n: 32
            //, total: -1
            //, sorter: ""
            //, filter: ""
            //, q: null
            //, searchQuery: null
            //, key: null
            //, nodeId: 0
            //, nodeType: null
            },
            reset: function() {
                this._treeInfo.name = null;
                this._treeInfo.start = 0;
                this._treeInfo.n = this.DEFAULT_PAGE_SIZE;
                this._treeInfo.total = -1;
                this._treeInfo.sorter = "";
                this._treeInfo.filter = "";
                this._treeInfo.q = null;
                this._treeInfo.searchQuery = null;
                this._treeInfo.key = null;
                this._treeInfo.nodeId = 0;
                this._treeInfo.nodeType = null;
            },
            getTreeInfo: function() {
                return this._treeInfo;
            },
            getPageId: function() {
                return this._treeInfo.start;
            },
            getName: function() {
                return this._treeInfo.name;
            },
            setName: function(name) {
                this._treeInfo.name = name;
            },
            getSearchQuery: function() {
                return this._treeInfo.searchQuery;
            },
            setSearchQuery: function(searchQuery) {
                this._treeInfo.searchQuery = searchQuery;
            }

            ,
            validateTreeInfo: function(data) {
                if (Util.isEmpty(data)) {
                    return false;
                }
                if (Util.isEmpty(data.name)) {
                    return false;
                }
                if (0 < Util.goodValue(data.nodeId, 0)) {
                    if (Util.isEmpty(data.nodeType)) {
                        return false;
                    }
                    //                    if (!Util.isEmpty(data.key)) {
                    //                        return false;
                    //                    }
                }

                return true;
            },
            readTreeInfo: function() {
                this._initTreeInfo = new Store.SessionData("AcmObjectTreeInfo");

                var ti = this.getTreeInfo();
                var tiInit = this._initTreeInfo.get();
                if (this.validateTreeInfo(tiInit)) {
                    if (ti.name == Util.goodValue(tiInit.name)) {
                        //ti.name    = Util.goodValue(tiInit.name);
                        ti.start = Util.goodValue(tiInit.start, 0);
                        ti.n = Util.goodValue(tiInit.n, 50);
                        ti.sort = Util.goodValue(tiInit.sort, null);
                        ti.filter = Util.goodValue(tiInit.filter, null);
                        ti.q = Util.goodValue(tiInit.q, null);
                        ti.searchQuery = Util.goodValue(tiInit.searchQuery, null);
                        ti.key = Util.goodValue(tiInit.key, null);
                        ti.nodeId = Util.goodValue(tiInit.nodeId, 0);
                        ti.nodeType = Util.goodValue(tiInit.nodeType, 0);

                        this._initTreeInfo.set(null);
                    }
                }

                if (0 == ti.nodeId && null == ti.key) {
                    //
                    // todo: to activate a tree node at initialization
                    //
                }
            },
            sameResultSet: function(treeInfo) {
                if (!Util.compare(this._treeInfo.name, treeInfo.name)) {
                    return false;
                }

                if (0 < this._treeInfo.nodeId) {
                    if (!Util.compare(this._treeInfo.nodeId, treeInfo.nodeId)) {
                        return false;
                    }
                    if (!Util.compare(this._treeInfo.nodeType, treeInfo.nodeType)) {
                        return false;
                    }
                }

                if (!Util.compare(this._treeInfo.start, treeInfo.start)) {
                    return false;
                }
                if (!Util.compare(this._treeInfo.sorter, treeInfo.sorter)) {
                    return false;
                }
                if (!Util.compare(this._treeInfo.filter, treeInfo.filter)) {
                    return false;
                }
                if (!Util.compare(this._treeInfo.n, treeInfo.n)) {
                    return false;
                }

                return true;
            }
        }
    // Config

    }; //Tree

    return {
        restrict: 'E',
        templateUrl: "directives/object-tree/object-tree.client.view.html",
        scope: {
            treeConfig: '=',
            treeData: '=',
            onReset: '&',
            onLoad: '&',
            onSelect: '&',
            treeControl: '=',
            userPrivileges: '=?'
        }

        ,
        link: function(scope, element, attrs) {
            Tree.reset();
            Tree.scope = scope;
            Tree.jqDivTree = $(element).find(".tree");
            Tree.onReset = scope.onReset;
            Tree.onLoad = scope.onLoad;
            Tree.onSelect = scope.onSelect;
            Tree.treeConfig = null; //scope.treeConfig;
            Tree.treeData = null; //scope.treeData;
            scope.treeControl = {
                    setTitle: Tree.setTitle,
                select: Tree.select,
                selectComponent: Tree.selectComponent,
                refresh: Tree.refresh,
                expandAll: Tree.expandAll,
                collapseAll: Tree.collapseAll
            };

            Tree.create();

            var treeInfo = Tree.Info.getTreeInfo();

            scope.$watchGroup([ 'treeConfig', 'treeData' ], function(newValues, oldValues, scope) {
                var treeConfig = newValues[0];
                var treeData = newValues[1];
                var isNewConfig = (treeConfig != Tree.treeConfig);
                var isNewData = (treeData != Tree.treeData);
                Tree.treeConfig = treeConfig;
                Tree.treeData = treeData;

                if (isNewConfig && treeConfig) {
                    var treeInfo = Tree.Info.getTreeInfo();
                    var oldPageSize = treeInfo.pageSize;
                    var oldFilter = treeInfo.filter;
                    var oldSorter = treeInfo.sorter;
                    var oldSearchQuery = treeInfo.searchQuery;

                    treeInfo.pageSize = Util.goodValue(treeConfig.pageSize, Tree.Info.DEFAULT_PAGE_SIZE);
                    Tree.scope.filters = Util.goodArray(treeConfig.filters);
                    treeInfo.defaultFilter = Util.goodMapValue(_.find(Tree.scope.filters, {
                        'default': true
                    }), "name");
                    treeInfo.filter = treeInfo.defaultFilter;
                    Tree.scope.nameFilter = Util.goodMapValue(_.find(Tree.scope.filters, {
                        'default': true
                    }), "desc");
                    Tree.scope.onSelectFilter = function(filterName) {
                        Tree.scope.nameFilter = Util.goodMapValue(_.find(Tree.scope.filters, function(filters) {
                            return filters.name == filterName
                        }), "desc");
                        var treeInfo = Tree.Info.getTreeInfo();
                        if (!Util.compare(treeInfo.filter, filterName)) {
                            Tree.setNodeId(0);
                            Tree.setNodeType(null);
                            treeInfo.start = 0;
                            treeInfo.filter = filterName;
                            Tree.onLoad()(treeInfo.start, treeInfo.n, treeInfo.sorter, treeInfo.filter, treeInfo.searchQuery);
                        }
                    };

                    Tree.scope.sorters = Util.goodArray(treeConfig.sorters);
                    treeInfo.defaultSorter = Util.goodMapValue(_.find(Tree.scope.sorters, {
                        'default': true
                    }), "name");
                    treeInfo.sorter = treeInfo.defaultSorter;
                    Tree.scope.nameSort = Util.goodMapValue(_.find(Tree.scope.sorters, {
                        'default': true
                    }), "desc");
                    Tree.scope.onSelectSort = function(sorterName) {
                        Tree.scope.nameSort = Tree.scope.nameSort = Util.goodMapValue(_.find(Tree.scope.sorters, function(sorters) {
                            return sorters.name == sorterName
                        }), "desc");
                        var treeInfo = Tree.Info.getTreeInfo();
                        if (!Util.compare(treeInfo.sorter, sorterName)) {
                            Tree.setNodeId(0);
                            Tree.setNodeType(null);
                            treeInfo.start = 0;
                            treeInfo.sorter = sorterName;
                            Tree.onLoad()(treeInfo.start, treeInfo.n, treeInfo.sorter, treeInfo.filter, treeInfo.searchQuery);
                        }
                    };

                    var searchQuery = Util.goodArray(treeConfig.searchQuery);
                    treeInfo.searchQuery = searchQuery;
                    Tree.scope.searchQuery = searchQuery;

                    if (oldPageSize != treeInfo.pageSize || !Util.compare(oldFilter, treeInfo.filter) || !Util.compare(oldSorter, treeInfo.sorter) || !Util.compare(oldSearchQuery, treeInfo.searchQuery)) {
                        Tree.onLoad()(treeInfo.start, treeInfo.n, treeInfo.sorter, treeInfo.filter, treeInfo.searchQuery);
                    }
                }

                if (isNewData && treeConfig && Util.goodMapValue(treeData, "docs", false)) {
                    var treeInfo = Tree.Info.getTreeInfo();
                    Tree.refreshTree(treeInfo.key);
                }
            });

            scope.onClickSearch = function() {
                var searchQuery = Tree.scope.searchQuery;
                var treeInfo = Tree.Info.getTreeInfo();
                if (!Util.compare(treeInfo.searchQuery, searchQuery)) {
                    Tree.setNodeId(0);
                    Tree.setNodeType(null);
                    treeInfo.start = 0;
                    treeInfo.searchQuery = searchQuery;
                    Tree.onLoad()(treeInfo.start, treeInfo.n, treeInfo.sorter, treeInfo.filter, treeInfo.searchQuery);
                }
            };

            scope.onClickRefresh = function() {
                Tree.refresh();
            };

            scope.keyUp = function(event) {
                if (event.keyCode == 13) {
                    scope.onClickSearch();
                }
            };

            scope.$bus.subscribe('$translateChangeSuccess', function(data) {
                Tree.tree.visit(function(node) {
                    var label = node.data.label;
                    if (label) {
                        node.setTitle($translate.instant(label));
                        Tree.fixNodeIcon(node);
                    }
                });
            });
        }
    };
} ]);
