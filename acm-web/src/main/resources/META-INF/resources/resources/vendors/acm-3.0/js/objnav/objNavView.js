/**
 * ObjNav.View
 *
 * @author jwu
 */
ObjNav.View = {
    create : function(args) {
        if (ObjNav.View.Navigator.create)          {ObjNav.View.Navigator.create(args);}
        if (ObjNav.View.Content.create)            {ObjNav.View.Content.create(args);}
    }
    ,onInitialized: function() {
        if (ObjNav.View.Navigator.onInitialized)   {ObjNav.View.Navigator.onInitialized();}
        if (ObjNav.View.Content.onInitialized)     {ObjNav.View.Content.onInitialized();}
    }

    ,Navigator: {
        create : function(args) {
            this.$tree = (args.$tree)? args.$tree : $("#tree");
            this.getContextMenu = args.treeArgs.getContextMenu;
            this._createTree(args.treeArgs);

            if (args.treeFilter) {
                this.$ulFilter = (args.$ulFilter)? args.$ulFilter : $("#ulFilter");
                this.Filter.buildFilter(this.$ulFilter
                    , args.treeFilter
                    , function(value) {
                        ObjNav.Controller.viewChangedTreeFilter(value);
                    }
                );
            }
            if (args.treeSort) {
                this.$ulSort = (args.$ulSort)? args.$ulSort : $("#ulSort");
                this.Sorter.buildSort(this.$ulSort
                    , args.treeSort
                    , function(value) {
                        ObjNav.Controller.viewChangedTreeSort(value);
                    }
                );
            }


            Acm.Dispatcher.addEventListener(ObjNav.Controller.MODEL_RETRIEVED_OBJECT_LIST, this.onModelRetrievedObjectList);
            Acm.Dispatcher.addEventListener(ObjNav.Controller.MODEL_RETRIEVED_OBJECT_LIST_ERROR, this.onModelRetrievedObjectListError);

            if ("undefined" != typeof Topbar) {
                //Acm.Dispatcher.addEventListener(Topbar.Controller.Asn.VIEW_SET_ASN_DATA       , this.onTopbarViewSetAsnData, Acm.Dispatcher.PRIORITY_HIGH);
            }
        }
        ,onInitialized: function() {
        }


        ,onModelRetrievedObjectList: function(key) {
            ObjNav.View.Navigator.refreshTree(key);
        }
        ,onModelRetrievedObjectListError: function(error) {
            Acm.Dialog.error(error.errMsg);
            //ObjNav.View.Navigator.refreshTree(null);
        }
        ,onTopbarViewSetAsnData: function(asnData) {
            //fix me
//            if (ObjNav.Model.Tree.Config.validateTreeInfo(asnData)) {
//                if (0 == asnData.name.indexOf("/plugin/casefile")) {
//                    var treeInfo = ObjNav.Model.Tree.Config.getTreeInfo();
//                    if (ObjNav.Model.Tree.Config.sameResultSet(asnData)) {
//                        if (asnData.key) {
//                            var key = ObjNav.Model.Tree.Key.getKeyBySubWithPage(asnData.start, asnData.key);
//                            ObjNav.Object.Tree.refreshTree(key);
//                        }
//                        return true;
//                    }
//                }
//            }
            return false;
        }
        ,onTreeNodeActivated: function(node) {
            if (ObjNav.Model.Tree.Key.getKeyPrevPage() == node.key) {
                ObjNav.Controller.viewClickedPrevPage();
            } else if (ObjNav.Model.Tree.Key.getKeyNextPage() == node.key) {
                ObjNav.Controller.viewClickedNextPage();
            } else {
                var objId = ObjNav.Model.Tree.Key.getObjIdByKey(node.key);
                var objType = ObjNav.Model.Tree.Key.getObjTypeByKey(node.key);

                var previousKey = ObjNav.View.Navigator.getPreviousKey();
                var previousObjId = ObjNav.Model.Tree.Key.getObjIdByKey(previousKey);
                var previousObjType = ObjNav.Model.Tree.Key.getObjTypeByKey(previousKey);

                if (objId != previousObjId || objType != previousObjType) {
                    ObjNav.Controller.viewSelectedObject(objType, objId);
                }
            }

            ObjNav.Controller.viewSelectedTreeNode(node.key);
        }

        ,_getDefaultTreeArgs: function() {
            return {
                activate: function(event, data) {
                    ObjNav.View.Navigator.onTreeNodeActivated(data.node);
                }
                ,source: function() {
                    var builder = AcmEx.FancyTreeBuilder.reset();

                    var treeInfo = ObjNav.Model.Tree.Config.getTreeInfo();
                    var objList = ObjNav.Model.List.cachePage.get(treeInfo.start);
                    if (Acm.isArrayEmpty(objList)) {
                        return builder.getTree();
                    }

                    if (0 < treeInfo.start) {
                        builder.addLeaf({key: ObjNav.Model.Tree.Key.NODE_TYPE_PART_PREV_PAGE
                            ,title: treeInfo.start + " records above..."
                            ,tooltip: "Review previous records"
                            ,expanded: false
                            ,folder: false
                        });
                    }

                    for (var i = 0; i < objList.length; i++) {
                        var obj = objList[i];
                        var nodeId      = ObjNav.Model.interface.nodeId(obj);
                        var nodeType    = ObjNav.Model.interface.nodeType(obj);
                        var nodeTitle   = ObjNav.Model.interface.nodeTitle(obj);
                        var nodeToolTip = ObjNav.Model.interface.nodeToolTip(obj);
                        if (nodeId && nodeType) {
                            var objKey = ObjNav.Model.Tree.Key.getKeyByObjWithPage(treeInfo.start, nodeType, nodeId);
                            builder.addLeaf({key: objKey
                                ,title          : nodeTitle
                                ,tooltip        : nodeToolTip
                                ,expanded: false
                                ,folder: true
                                ,lazy: true
                                ,cache: false
                            });
                        }
                    } //end for i
                    builder.makeLast();

                    if ((0 > treeInfo.total)                                    //unknown size
                        || (treeInfo.total - treeInfo.n > treeInfo.start)) {    //more page
                        var title = (0 > treeInfo.total)? "More records..."
                            : (treeInfo.total - treeInfo.start - treeInfo.n) + " more records...";
                        builder.addLeafLast({key: ObjNav.Model.Tree.Key.NODE_TYPE_PART_NEXT_PAGE
                            ,title: title
                            ,tooltip: "Load more records"
                            ,expanded: false
                            ,folder: false
                        });
                    }

                    return builder.getTree();
                }
            };
        }

        ,_createTree: function(treeArgs) {
            var treeArgsToUse = this._getDefaultTreeArgs();
            for (var arg in treeArgs) {
                treeArgsToUse[arg] = treeArgs[arg];
            }

            this.useFancyTree(this.$tree, treeArgsToUse);


            if (ObjNav.View.Navigator.getContextMenu) {
//                ObjNav.View.Navigator.$tree.contextmenu({
//                    //delegate: "span.fancytree-title"
//                    delegate: ".fancytree-title"
//                    ,beforeOpen: function(event, ui) {
//                        var node = $.ui.fancytree.getNode(ui.target);
//                        //node.setFocus();
//                        node.setActive();
//                        ObjNav.View.Navigator.$tree.contextmenu("replaceMenu", ObjNav.View.Navigator.getContextMenu(node));
//
//                    }
//                    ,select: function(event, ui) {
//                        var node = $.ui.fancytree.getNode(ui.target);
//                        alert("select " + ui.cmd + " on " + node);
//                    }
//                });
            }
        }

        ,useFancyTree: function($tree, arg) {
            this.activateOrig = null;
            if (arg.activate) {
                this.activateOrig = arg.activate;
            }
            arg.activate = function(event, data) {
                var activeKey = ObjNav.View.Navigator.getActiveKey();
                ObjNav.View.Navigator.setPreviousKey(activeKey);
                ObjNav.View.Navigator.setActiveKey(data.node.key);
                if (ObjNav.View.Navigator.activateOrig) {
                    ObjNav.View.Navigator.activateOrig(event, data);
                }
            };

            this.beforeActivateOrig = null;
            if (arg.beforeActivate) {
                this.beforeActivateOrig = arg.beforeActivate;
            }
            arg.beforeActivate = function(event, data) {
                if (App.Object.Dirty.isDirty()) {
                    var node = data.node;
                    var key = node.key;
                    if (key == ObjNav.View.Navigator.getActiveKey()) {
                        if (ObjNav.View.Navigator.beforeActivateOrig) {
                            return ObjNav.View.Navigator.beforeActivateOrig(event, data);
                        } else {
                            return true;
                        }
                    } else {
                        var reason = App.Object.Dirty.getFirst();
                        Acm.Dialog.alert("Need to save data first: " + reason);
                        return false;
                    }
                }
            };

            //dblclick
            //focus

            if (!arg.renderNode) {
                arg.renderNode = function(event, data) {
                    ObjNav.View.Navigator.fixNodeIcon(data.node);
                };
            }

            if (!arg.loadError) {
                arg.loadError = function(event, data) {
                    var error = data.error;
                    if (error.status && error.statusText) {
                        data.details = "Error status: " + error.statusText + "[" + error.status + "]";
                    } else {
                        data.details = "Error: " + error;
                    }
                    //data.message = "Custom error: " + data.message;
                };
            }

            $tree.fancytree(arg);

            this.tree = $tree.fancytree("getTree");
        }

        ,fixNodeIcon: function(node) {
            var key = node.key;
            var nodeType = ObjNav.Model.Tree.Key.getNodeTypeByKey(key);
            var acmIcon = ObjNav.Model.Tree.Key.getIconByKey(key);
            if (acmIcon) {
                var span = node.span;
                var $spanIcon = $(span.children[1]);
                $spanIcon.removeClass("fancytree-icon");
//                $spanIcon.html("<i class='i " + acmIcon + "'></i>");
                $spanIcon.html("<i class='" + acmIcon + "'></i>");
            }
        }
        ,setTitle: function(key, title) {
            var node = this.tree.getNodeByKey(key);
            if (node) {
                node.setTitle(title);
                this.fixNodeIcon(node);
            }
        }
        ,updateObjNode: function(nodeType, nodeId) {
            var objSolr = ObjNav.Model.List.getSolrObject(nodeType, nodeId);
            if (ObjNav.Model.List.validateObjSolr(objSolr)) {
                var nodeTitle = ObjNav.Model.interface.nodeTitle(objSolr);
                var key = ObjNav.Model.Tree.Key.getKeyByObj(nodeType, nodeId);
                ObjNav.View.Navigator.setTitle(key, nodeTitle);
            }
        }

        ,_previousKey: null
        ,getPreviousKey: function() {
            return this._previousKey;
        }
        ,setPreviousKey: function(previousKey) {
            this._previousKey = previousKey;
        }
        ,getPreviousObjId: function() {
            var objId = ObjNav.Model.Tree.Key.getObjIdByKey(this._previousKey);
            return objId;
        }
        ,getPreviousObjType: function() {
            var objType = ObjNav.Model.Tree.Key.getObjTypeByKey(this._previousKey);
            return objType;
        }
        ,_activeKey: null
        ,getActiveKey: function() {
            return this._activeKey;
        }
        ,setActiveKey: function(activeKey) {
            this._activeKey = activeKey;
        }
        ,getActiveObjId: function() {
            var objId = ObjNav.Model.Tree.Key.getObjIdByKey(this._activeKey);
            return objId;
        }
        ,getActiveObjType: function() {
            var objType = ObjNav.Model.Tree.Key.getObjTypeByKey(this._activeKey);
            return objType;
        }

        ,refreshTree: function(key) {
            this.tree.reload().done(function(){
                if (Acm.isNotEmpty(key)) {
                    var parts = key.split(ObjNav.Model.Tree.Key.KEY_SEPARATOR);
                    if (parts && 1 < parts.length) {
                        var parentKey = parts[0];
                        //exclude page ID, so start from 1; expand parents only, not include self, so length-1
                        for (var i = 1; i < parts.length-1; i++) {
                            parentKey += ObjNav.Model.Tree.Key.KEY_SEPARATOR + parts[i];
                            var node = ObjNav.View.Navigator.tree.getNodeByKey(parentKey);
                            if (node) {
                                if (!node.isExpanded()) {
                                    node.setExpanded(true);
                                }
                            }
                        }
                    }

                    ObjNav.View.Navigator.tree.activateKey(key);
                }
            });
        }
        ,activeTreeNode: function(key) {
            this.tree.activateKey(key);
        }
        ,expandAllTreeNode: function(key) {
            var thisNode = this.tree.getNodeByKey(key);
            if (thisNode) {
                thisNode.setExpanded(true);
                thisNode.visit(function(node) {
                    node.setExpanded(true);
                });
            }
        }
        ,collapseAllTreeNode: function(key) {
            var thisNode = this.tree.getNodeByKey(key);
            if (thisNode) {
                thisNode.setExpanded(false);
                thisNode.visit(function(node) {
                    node.setExpanded(false);
                });
            }
        }
        ,toggleAllTreeNode: function(key) {
            var thisNode = this.tree.getNodeByKey(key);
            if (thisNode) {
                thisNode.toggleExpanded();
                thisNode.visit(function(node) {
                    node.toggleExpanded();
                });
            }
        }

        ,Filter: {
            //defaultFilter: null
            buildFilter: function($ulFilter, treeFilter, onFilterChanged) {
                var html = "";
                if (this.validateFilter(treeFilter)) {
                    for (var i = 0; i < treeFilter.length; i++) {
                        if (treeFilter[i].default) {
                            //this.defaultFilter = Acm.goodValue(treeFilter[i].name);

                            var treeInfo = ObjNav.Model.Tree.Config.getTreeInfo();
                            treeInfo.filter = Acm.goodValue(treeFilter[i].name);
                        }
                        html += "<li value='" + Acm.goodValue(treeFilter[i].name)
                            +  "'><a href='#'>" + Acm.goodValue(treeFilter[i].desc) + "</a></li>";
                    }
                }

                if (Acm.isNotEmpty(html)) {
                    $ulFilter.html(html);
                    $ulFilter.find("li").on("click", function(e) {
                        var value = $(this).attr("value");
                        onFilterChanged(value);
                    });
                }
            }
            ,validateFilter: function(data) {
                if (Acm.isEmpty(data)) {
                    return false;
                }
                if (!Acm.isArray(data)) {
                    return false;
                }
                return true;
            }
        }

        ,Sorter: {
            //defaultSort: null
            buildSort: function($ulSort, treeSort, onSortChanged) {
                var html = "";
                if (this.validateSort(treeSort)) {
                    for (var i = 0; i < treeSort.length; i++) {
                        if (treeSort[i].default) {
                            //this.defaultSort = Acm.goodValue(treeSort[i].name);

                            var treeInfo = ObjNav.Model.Tree.Config.getTreeInfo();
                            treeInfo.sort = Acm.goodValue(treeSort[i].name);
                        }
                        html += "<li value='" + Acm.goodValue(treeSort[i].name)
                            +  "'><a href='#'>" + Acm.goodValue(treeSort[i].desc) + "</a></li>";
                    }
                }

                if (Acm.isNotEmpty(html)) {
                    $ulSort.html(html);
                    $ulSort.find("li").on("click", function(e) {
                        var value = $(this).attr("value");
                        onSortChanged(value);
                    });
                }
            }
            ,validateSort: function(data) {
                if (Acm.isEmpty(data)) {
                    return false;
                }
                if (!Acm.isArray(data)) {
                    return false;
                }
                return true;
            }
        }
    }

    ,Content: {
        create : function(args) {
            this.$tabTop          = $("#tabTop");
            this.$tabTopBlank     = $("#tabTopBlank");

            Acm.Dispatcher.addEventListener(ObjNav.Controller.VIEW_SELECTED_TREE_NODE       ,this.onViewSelectedTreeNode);
            Acm.Dispatcher.addEventListener(ObjNav.Controller.VIEW_SELECTED_OBJECT          ,this.onViewSelectedObject);
        }
        ,onInitialized: function() {
        }


        ,onViewSelectedTreeNode: function(key) {
            ObjNav.View.Content.showPanel(key);
        }
        ,onViewSelectedObject: function(objType, objId) {
            ObjNav.View.Content.showTopPanel(0 < objId);
        }

        ,showTopPanel: function(show) {
            Acm.Object.show(this.$tabTop, show);
            Acm.Object.show(this.$tabTopBlank, !show);
        }
        ,showPanel: function(key) {
            var tabIds = ObjNav.Model.Tree.Key.getTabIds();
            var tabIdsToShow = ObjNav.Model.Tree.Key.getTabIdsByKey(key);
            for (var i = 0; i < tabIds.length; i++) {
                var show = Acm.isItemInArray(tabIds[i], tabIdsToShow);
                Acm.Object.show($("#" + tabIds[i]), show);
            }
        }
    }

};

