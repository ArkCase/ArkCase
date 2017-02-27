'use strict';

/**
 * @ngdoc directive
 * @name global.directive:docTree
 * @restrict E
 *
 * @description
 *
 * {@link https://***REMOVED***/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/directives/doc-tree/doc-tree.client.directive.js directives/doc-tree/doc-tree.client.directive.js}
 *
 * The docTree directive renders a FancyTree to browse ArkCase objects with support of paging, filter and sort
 *
 * @param {String} object-type Object type of document container object
 * @param {Number} object-id Object ID of document container object
 * @param {Number} file-types List of file types and form types the tree can upload
 * @param {Array} correspondence-forms List of correspondence form types the tree can upload
 * @param {Function} upload-form (Optional)Function used to upload Frevvo form
 * @param {Object} tree-config Tree configuration used to add to default configuration. Default doc tree configuration
 * is saved in config.json file of common module.
 * @param {Object} object-info Metadata of document container object
 * @param {Boolean} read-only Value "true" would disable all functions that can modify document. Default value is "false"
 * @param {Function} on-allow-cmd (Optional)Callback function before a command is shown on Menu, to allow doc tree
 * consummer to have a chance to modify menu items. Return "invisible" to remove the command from menu;
 * "disable" to disable the command; Anything else or undefined means the command will be show as normal.
 * @param {Function} on-pre-cmd (Optional)Callback function before a command is executed to give doc tree
 * consumer to run additional code before the command is executed or to override implemented command.
 * Return "fasle" prevents the command execution; "true" or "undefined" to continue the command.
 * It also accepts promise as return. In that case, promise resolution of "false" prevents command execution.
 * @param {Function} on-post-cmd (Optional)Callback function after a command is executed to give doc tree
 * consumer to run additional code after the command is executed.
 * @param {Object} tree-control Tree API functions exposed to user. Following is the list:
 * @param {Function} treeControl.refreshTree Refresh the tree
 * @param {Function} treeControl.getSelectedNodes Get list of selected tree nodes
 *
 *
 * @example
 <example>
 <file name="index.html">
 <doc-tree object-type="objectType" object-id="objectId" tree-control="treeControl"
 upload-form="uploadForm" tree-config="treeConfig" object-info="objectInfo">
 </doc-tree>
 </file>
 <file name="app.js">
 angular.module('ngAppDemo', []).controller('ngAppDemoController', function($scope, $log) {
    $scope.objectType = "CASE_FILE";
    $scope.objectId = 123;
    $scope.uploadForm = function() {
        $log.info("Upload form");
    };
    $scope.treeConfig = {};
    $scope.treeConfig.fileTypes = [
      {
        "type": "mr",
        "label": "Medical Release"
      },
      {
        "type": "gr",
        "label": "General Release"
      },
      {
        "type": "ev",
        "label": "eDelivery"
      },
      {
        "type": "sig",
        "label": "SF86 Signature"
      },
      {
        "type": "noi",
        "label": "Notice of Investigation"
      },
      {
        "type": "wir",
        "label": "Witness Interview Request"
      },
      {
        "type": "Other",
        "label": "Other"
      }
    ];
});
 </file>
 </example>
 */
angular.module('directives').directive('docTree', ['$q', '$translate', '$modal', '$filter', '$log', '$injector'
    , 'Acm.StoreService', 'UtilService', 'Util.DateService', 'ConfigService', 'Profile.UserInfoService'
    , 'EcmService'
    , function ($q, $translate, $modal, $filter, $log, $injector
        , Store, Util, UtilDateService, ConfigService, UserInfoService
        , Ecm
    ) {
        var cacheTree = new Store.CacheFifo();
        var cacheFolderList = new Store.CacheFifo();

        var DocTree = {
            CLIPBOARD: null

            , NODE_TYPE_PREV: "prev"
            , NODE_TYPE_NEXT: "next"
            , NODE_TYPE_FILE: "file"
            , NODE_TYPE_FOLDER: "folder"

            , jqTree: null
            , tree: null

            , cacheTree: cacheTree
            , cacheFolderList: cacheFolderList

            , _objType: null
            , getObjType: function () {
                return this._objType;
            }
            , setObjType: function (objType) {
                this._objType = objType;
            }
            , _objId: null
            , getObjId: function () {
                return this._objId;
            }
            , setObjId: function (objId) {
                this._objId = objId;
            }

            , _getDefaultTreeArgs: function () {
                return {
                    extensions: ["table", "gridnav", "edit", "dnd"]
                    , checkbox: true
                    , selectMode: 2

                    , table: {
                        indentation: 10,      // indent 20px per node level
                        nodeColumnIdx: 2,     // render the node title into the 3rd column
                        checkboxColumnIdx: 0  // render the checkboxes into the 1st column
                    }
                    , gridnav: {
                        autofocusInput: false,
                        handleCursorKeys: true
                    }
                    , renderColumns: function (event, data) {
                        var node = data.node;
                        var $tdList = $(node.tr).find(">td");
                        // (index #0 is rendered by fancytree by adding the checkbox)
                        // (index #2 is rendered by fancytree)

                        if (DocTree.isFileNode(node)) {
                            var columnDefs = Util.goodArray(DocTree.treeConfig.columnDefs);
                            for (var i = 0; i < columnDefs.length; i++) {
                                var columnDef = columnDefs[i];
                                var colIdx = Util.goodValue(columnDef.index, -1);
                                if (0 <= colIdx) {
                                    var element = $tdList.eq(colIdx).get();
                                    var renderer = DocTree.Column.findRenderer(columnDef.name);
                                    if (renderer) {
                                        renderer(element, node, columnDef, DocTree.readOnly);
                                    }
                                }
                            }

                        } else if (DocTree.isFolderNode(node)) {
                            ;
                        } else {  //not file, not folder
                            $tdList.eq(0).text("");
                        }
                    }

                    , renderNode: function (event, data) {
                        var node = data.node;
                        var acmIcon = null;
                        var nodeType = Util.goodValue(node.data.objectType);
                        if (DocTree.NODE_TYPE_PREV == nodeType) {
                            acmIcon = "<i class='i i-arrow-up'></i>"; //"i-notice icon"
                        } else if (DocTree.NODE_TYPE_NEXT == nodeType) {
                            acmIcon = "<i class='i i-arrow-down'></i>";
                        }
                        if (acmIcon) {
                            var span = node.span;
                            var $spanIcon = $(span.children[1]);
                            $spanIcon.removeClass("fancytree-icon");
                            $spanIcon.html(acmIcon);
                        }
                    }
                    , click: DocTree.onClick
                    , dblclick: DocTree.onDblClick
                    , keydown: DocTree.Command.onKeyDown
                    , source: DocTree.Source.source()
                    , lazyLoad: DocTree.Source.lazyLoad
                    , edit: {
                        triggerStart: ["f2", "shift+click", "mac+enter"]
                        , beforeEdit: function (event, data) {
                            // Return false to prevent edit mode
                            if (DocTree.readOnly) {
                                return false;
                            }
                            if (DocTree.isTopNode(data.node) || DocTree.isSpecialNode(data.node)) {
                                return false;
                            }
                            if (data.node.isLoading()) {
                                return false;
                            }
                            DocTree.editSetting.isEditing = true;
                        }
                        , edit: function (event, data) {
                            data.input.select();
                        }
                        , beforeClose: function (event, data) {
                            // Return false to prevent cancel/save (data.input is available)
                        }
                        , save: function (event, data) {
                            var parent = data.node.getParent();
                            if (parent) {
                                var name = data.input.val();

                                if (DocTree.findSiblingNodeByName(data.node, name)) {
                                    data.node.remove();
                                    return false;
                                }

                                var isNew = data.isNew;
                                if (isNew) {
                                    //User renames folder right after create one, data.isNew should be false, but is still true
                                    //When folder is created first time, key starts with "_"
                                    var key = data.node.key + "";
                                    isNew = key.startsWith("_");
                                }

                                if (isNew) {
                                    if (DocTree.isFolderNode(data.node)) {
                                        DocTree.Op.createFolder(data.node, name);
                                    } else {
                                        //create new document node
                                    }
                                    DocTree.editSetting.lastNewTitle = name;

                                } else {
                                    if (DocTree.isFolderNode(data.node)) {
                                        DocTree.Op.renameFolder(data.node, name);
                                    } else if (DocTree.isFileNode(data.node)) {
                                        DocTree.Op.renameFile(data.node, name);
                                    }
                                }
                            }


                            return true;        // We return true, so ext-edit will set the current user input as title
                        }
                        , close: function (event, data) {
                            // Editor was removed
                            if (data.save) {
                                var fileName = data.node.title;
                                DocTree.markNodePending(data.node, fileName);
                            }
                            DocTree.editSetting.isEditing = false;
                        }
                    }
                    , dnd: {
                        //autoExpandMS: 400,
                        autoExpandMS: 1600000,
                        focusOnClick: true,
                        preventVoidMoves: true,       // Prevent dropping nodes 'before self', etc.
                        preventRecursiveMoves: true,  // Prevent dropping nodes on own descendants
                        dragStart: function (node, data) {
                            if (DocTree.readOnly) {
                                return false;
                            }
                            if (DocTree.isTopNode(data.node) || DocTree.isSpecialNode(data.node)) {
                                return false;
                            }
                            if (DocTree.editSetting.isEditing) {
                                return false;
                            }
                            return true;
                        }
                        , dragEnter: function (node, data) {
                            if (node == data.otherNode) {
                                return ["before", "after"];     //Cannot drop to oneself
                            } else if (DocTree.isTopNode(data.node)) {
                                if (node == data.otherNode.parent) {
                                    return false;
                                } else {
                                    return ["over"];
                                }
                            } else if (node == data.otherNode.parent) {
                                return ["before", "after"];     //Drop over ones own parent doesn't make sense
                            } else if (DocTree.NODE_TYPE_PREV == data.node.data.objectType) {
                                return ["after"];
                            } else if (DocTree.NODE_TYPE_NEXT == data.node.data.objectType) {
                                return ["before"];
                            } else if (DocTree.isFolderNode(data.node)) {
                                return true;
                            } else {
                                return ["before", "after"];  // Don't allow dropping *over* a document node (would create a child)
                            }
                        }
                        , dragDrop: function (node, data) {
                            if (DocTree.readOnly) {
                                return;
                            }

                            if (("before" != data.hitMode && "after" != data.hitMode) && DocTree.isFolderNode(node)) {
                                DocTree.expandNode(node).done(function () {
                                    if (DocTree.isFolderNode(data.otherNode)) {
                                        DocTree.Op.moveFolder(data.otherNode, node, data.hitMode);
                                    } else if (DocTree.isFileNode(data.otherNode)) {
                                        DocTree.Op.moveFile(data.otherNode, node, data.hitMode);
                                    }
                                });
                            } else {
                                if (DocTree.isFolderNode(data.otherNode)) {
                                    DocTree.Op.moveFolder(data.otherNode, node, data.hitMode);
                                } else if (DocTree.isFileNode(data.otherNode)) {
                                    DocTree.Op.moveFile(data.otherNode, node, data.hitMode);
                                }
                            }
                        }
                    }

                };
            } //end _getDefaultTreeArgs

            //, create: function (treeArgs) {
            , create: function (jqTree) {
                var treeArgsToUse = this._getDefaultTreeArgs();
                //_.merge(treeArgsToUse, treeArgs);

                jqTree.fancytree(treeArgsToUse)
                    .on("command", DocTree.Command.onCommand)
                    .on("mouseenter", ".fancytree-node", function (event) {
                        var node = $.ui.fancytree.getNode(event);
                        if (node) {
                            if (DocTree.isSpecialNode(node)) {
                                DocTree.Paging.alertPaging(node);
                            }
                        }
                    })
                    .on("mouseleave", ".fancytree-node", function (event) {
                        var node = $.ui.fancytree.getNode(event);
                        if (node) {
                            if (DocTree.isSpecialNode(node)) {
                                DocTree.Paging.relievePaging();
                            }
                        }
                    });

                var tree = jqTree.fancytree("getTree");
                var jqTreeBody = DocTree.jqTree.find("tbody");
                DocTree.ExternalDnd.startExternalDnd(jqTreeBody);

                //todo: move to column renderer
                jqTreeBody.on("change", "select.docversion", DocTree.onChangeVersion);
                jqTreeBody.on("dblclick", "select.docversion", DocTree.onDblClickVersion);

                var jqTreeHead = jqTree.find("thead");
                jqTreeHead.find("input:checkbox").on("click", function (e) {
                    DocTree.onClickBtnChkAllDocument(e, this);
                });

                return tree;
            }
            , refreshDocTree: function () {
                var jqTreeBody = DocTree.jqTree.find("tbody");
                DocTree.Menu.useContextMenu(jqTreeBody);
            }


            , isTopNode: function (node) {
                if (node) {
                    if (node.data.root) { //not fancy tree root node, which is the invisible parent of the top node
                        return true;
                    }
                }
                return false;
            }
            , isFolderNode: function (node) {
                if (node) {
                    if (node.folder) {
                        return true;
                    }
                }
                return false;
            }
            , isFileNode: function (node) {
                if (node) {
                    if (node.data) {
                        if (DocTree.NODE_TYPE_FILE == Util.goodValue(node.data.objectType)) {   //if (!node.isFolder()) {
                            return true;
                        }
                    }
                }
                return false;
            }
            , isSpecialNode: function (node) {
                if (node) {
                    if (node.data) {
                        if (DocTree.NODE_TYPE_FILE != Util.goodValue(node.data.objectType) && !node.folder) {
                            return true;
                        }
                    }
                }
                return false;
            }
            , getCacheKeyByNode: function (folderNode) {
                var pageId = Util.goodValue(folderNode.data.startRow, 0);
                var folderId = folderNode.data.objectId;
                var cacheKey = DocTree.getCacheKey(DocTree.isTopNode(folderNode) ? 0 : folderId, pageId);
                return cacheKey;
            }
            , getCacheKey: function (folderId, pageId) {
                var setting = DocTree.Config.getSetting();
                var key = this.getObjType() + "." + this.getObjId();
                key += "." + Util.goodValue(folderId, 0);    //for root folder, folderId is 0 or undefined
                key += "." + Util.goodValue(pageId, 0);
                key += "." + DocTree.Config.getSortBy();
                key += "." + DocTree.Config.getSortDirection();
                key += "." + DocTree.Config.getMaxRows();
                return key;
            }

            , getTopNode: function () {
                var topNode = null;
                if (DocTree.tree) {
                    var rootNode = DocTree.tree.getRootNode();
                    if (rootNode) {
                        topNode = rootNode.children[0];
                    }
                }
                return topNode;
            }
            , expandNodesByNames: function (names, src) {
                var dfdAll = $.Deferred();
                DocTree._expandFirstNodeByName(DocTree.getTopNode(), names, dfdAll, src);
                return dfdAll.promise();
            }
            , _expandFirstNodeByName: function (node, names, dfdAll, src) {
                var dfd = $.Deferred();

                if (Util.isEmpty(node) || Util.isArrayEmpty(names)) {
                    dfdAll.resolve(src);


                } else {
                    if (node.title == names[0]) {
                        DocTree.expandNode(node).done(function () {
                            names.shift();
                            if (Util.isArrayEmpty(names)) {
                                node = null;
                            } else {
                                node = DocTree.findChildNodeByName(node, names[0]);
                            }
                            DocTree._expandFirstNodeByName(node, names, dfdAll, src);
                        });
                    } else {
                        dfdAll.reject();
                    }
                }

                return dfd.promise;
            }
            , expandNode: function (node) {
                var dfd = $.Deferred();
                if (node.lazy && !node.children) {
                    node.setExpanded(true).always(function () {
                        dfd.resolve(node);
                    });
                } else {
                    dfd.resolve(node);
                }
                return dfd.promise();
            }
            , expandTopNode: function () {
                var $promise = $.when();
                var node = DocTree.jqTree.fancytree("getRootNode");
                if (node) {
                    var topNode = node.children[0];
                    $promise = this.expandNode(topNode);
//            if (!topNode.children) {
//                topNode.setExpanded(true);
//            }
                }
                return $promise;
            }
            /**
             * @description Recursive function that will iterate through the tree starting from a given node,
             * and replicate the tree structure and it's types of children in an array.
             * Used in DocTree.refreshTree() together with DocTree.expandAfterRefresh()
             *
             * NOTE - the node from which the iteration is started, must be used in DocTree.expandAfterRefresh()
             *
             * @param node The node from which the iteration will start
             * @param nodesStatusBeforeRefresh The array in which the nodes status will be saved
             */
            , saveNodesStatus: function (node, nodesStatusBeforeRefresh) {
                if (node.children) {
                    for (var i = 0; i < node.children.length; i++) {
                        if (DocTree.isFolderNode(node.children[i])) {
                            if (node.children[i].expanded) {
                                var folderExpanded = [];
                                nodesStatusBeforeRefresh.push(folderExpanded);
                                DocTree.saveNodesStatus(node.children[i], folderExpanded);
                            } else {
                                nodesStatusBeforeRefresh.push('folderNotExpanded');
                            }
                        } else {
                            nodesStatusBeforeRefresh.push('file');
                        }
                    }
                }
            }
            /**
             * @description Recursive function which will expand the folders in the tree based on their value
             * in the nodesStatusBeforeRefresh array
             * Used in DocTree.refreshtree() together with DocTree.saveNodesStatus()
             *
             * NOTE - the children param. must be the chhildren array from the node used as starting node in DocTree.saveNodesStatus()
             *
             * @param children The children of the first node
             * @param nodesStatusBeforeRefresh The array in which the tree structure and it's types of
             * children are replicated
             */
            , expandAfterRefresh: function (children, nodesStatusBeforeRefresh) {
                nodesStatusBeforeRefresh.forEach(function (item, index) {
                    if (angular.isArray(item)) {
                        DocTree.expandNode(children[index]).then(function (data) {
                            DocTree.expandAfterRefresh(data.children, nodesStatusBeforeRefresh[index]);
                        });
                    }
                });
            }
            /**
             * @description Refresh tree and try to keep current open branches
             */
            , refreshTree: function () {
                var objType = DocTree.getObjType();
                var objId = DocTree.getObjId();
                if (!Util.isEmpty(objType) && !Util.isEmpty(objId)) {
                    //remove tree cache for current obj
                    DocTree.cacheTree.remove(objType + "." + objId);
                    //remove individual folder cache for current obj
                    var keys = DocTree.cacheFolderList.keys();
                    _.each(keys, function (key) {
                        // cache key has following format :
                        // ojType.objId.folderId. (...)
                        if (!Util.isEmpty(key)) {
                            var tokens = key.split(".");
                            if (1 < tokens.length) {
                                var keyObjId = tokens[1];
                                if (Util.compare(objId, keyObjId)) {
                                    DocTree.cacheFolderList.remove(key);
                                }
                            }
                        }
                    });
                    //var cacheFolderList = DocTree.cacheFolderList.cache;
                    //if (!Util.isEmpty(cacheFolderList)) {
                    //    for (var cacheKey in cacheFolderList) {
                    //        if (cacheFolderList.hasOwnProperty(cacheKey)) {
                    //            var cacheKeySplit = cacheKey.split(".");
                    //            if (Util.isArray(cacheKeySplit)) {
                    //                // cache keys have following format :
                    //                // CASE_FILE.1258.0.0.name.ASC.16
                    //                // ojType.objId.folderId.pageId.soryBy.sortDirection.maxSize
                    //                var cacheKeyObjId = cacheKeySplit[1];
                    //                if (!Util.isEmpty(cacheKeyObjId)) {
                    //                    if (Util.goodValue(cacheKeyObjId) == Util.goodValue(objId)) {
                    //                        DocTree.cacheFolderList.remove(cacheKey);
                    //                    }
                    //                }
                    //            }
                    //        }
                    //    }
                    //}
                }

                var nodesStatusBeforeRefresh = [];// Array in which the tree structure will be replicated before the reload is started
                var rootNode = DocTree.getTopNode();// We want the iteration to start from the root node of the tree
                DocTree.saveNodesStatus(rootNode, nodesStatusBeforeRefresh);

                DocTree.tree.reload(DocTree.Source.source()).then(function () {
                    if (rootNode.expanded) {
                        DocTree.expandTopNode().then(function () {
                            var rootNode = DocTree.getTopNode();
                            DocTree.expandAfterRefresh(rootNode.children, nodesStatusBeforeRefresh);
                        })
                    }
                });
            }
            /**
             * @description Refresh a tree node.
             *
             * @param node (Optional)Tree node to refresh. If not specified, current active node is refreshed
             */
            , refreshNode: function (node) {
                node = node || DocTree.tree.getActiveNode();
                if (node) {
                    node.render(true);
                }
            }

            /**
             * @description Update data of a tree node
             *
             * @param node Tree node to update
             * @param value New value
             * @param field A field name
             * //param fieldModel (Optional)A field name for model
             */
            //, updateNodeData: function (node, value, field, fieldModel) {
            , updateNodeData: function (node, value, field) {
                if (Validator.validateNode(node)) {
                    node.data[field] = value;

                    var folderNode = node.getParent();
                    var cacheKey = DocTree.getCacheKeyByNode(folderNode);
                    var folderList = DocTree.cacheFolderList.get(cacheKey);
                    if (Validator.validateFolderList(folderList)) {
                        var found = _.find(folderList.children, function (child) {
                            return (child.objectId == node.data.objectId);
                        });
                        if (found) {
                            found[field] = value;
                        }
                    }
                    DocTree.cacheFolderList.put(cacheKey, folderList);
                }
            }

            , switchObject: function (activeObjType, activeObjId) {
                if (!DocTree.tree) {
                    return;
                }

                var dict = null;
                var topNode = DocTree.getTopNode();
                if (topNode) {
                    var previousObjType = DocTree.getObjType();
                    var previousObjId = DocTree.getObjId();
                    if (previousObjType != activeObjType || previousObjId != activeObjId) {
                        var dictTree = DocTree.tree.toDict();
                        if (!Util.isArrayEmpty(dictTree)) {
                            dict = dictTree[0];
                            if (dict && dict.data && dict.data.containerObjectType == previousObjType && dict.data.containerObjectId == previousObjId) {
                                DocTree.cacheTree.put(previousObjType + "." + previousObjId, dict);
                            }
                        }
                    }
                }

                DocTree.setObjType(activeObjType);
                DocTree.setObjId(activeObjId);
                dict = DocTree.cacheTree.get(activeObjType + "." + activeObjId);
                if (dict && topNode) {
                    topNode.removeChildren();
                    topNode.resetLazy();
                    topNode.fromDict(dict);
                } else {
                    DocTree.tree.reload(DocTree.Source.source());
                }
                Util.deferred(DocTree.Controller.viewChangedTree);
            }
            , markNodePending: function (node, fileName) {
                if (Validator.validateFancyTreeNode(node)) {
                    $(node.span).addClass("pending");
                    if (!node.folder) {
                        if(fileName){
                            node.title = $translate.instant("common.directive.docTree.waitUploading") + fileName;
                        }else {
                            node.title = $translate.instant("common.directive.docTree.waitUploading") + node.data.name;
                        }
                        node.renderTitle();
                    }
                    node.setStatus("loading");
                }
            }
            , markNodeOk: function (node) {
                if (Validator.validateFancyTreeNode(node)) {
                    $(node.span).removeClass("pending");
                    if (!node.folder) {
                        node.title = node.title.replace($translate.instant("common.directive.docTree.waitUploading"), '');
                        node.renderTitle();
                    }
                    node.setStatus("ok");
                }
            }
            , markNodeError: function (node) {
                if (Validator.validateFancyTreeNode(node)) {
                    $(node.span).addClass("pending");
                    node.title = $translate.instant("common.directive.docTree.error.nodeTitle");
                    node.renderTitle();
                    //node.setStatus("error");
                    node.setStatus("ok");
                }
            }

            , getNodePathNames: function (node) {
                var names = [];
                if (Validator.validateNode(node)) {
                    var n = node;
                    while (n) {
                        names.unshift(Util.goodValue(n.title));
                        n = n.parent;
                    }
                    names.shift(); //remove the hidden Root node
                }
                return names;
            }
            , findChildNodeByName: function (parentNode, name) {
                var found = null;
                if (Validator.validateFancyTreeNode(parentNode)) {
                    if (!Util.isArrayEmpty(parentNode.children)) {
                        for (var i = 0; i < parentNode.children.length; i++) {
                            if (parentNode.children[i].title == name) {
                                found = parentNode.children[i];
                                break;
                            }
                        }
                    }
                }
                return found;
            }
            , findSiblingNodeByName: function (node, name) {
                var found = null;
                var parentNode = node.getParent();
                if (Validator.validateFancyTreeNode(parentNode)) {
                    if (!Util.isArrayEmpty(parentNode.children)) {
                        for (var i = 0; i < parentNode.children.length; i++) {
                            if (parentNode.children[i].title == name) {
                                if (node.key != parentNode.children[i]) {   //cannot be self
                                    found = parentNode.children[i];
                                    break;
                                }
                            }
                        }
                    }
                }
                return found;
            }
            , findChildNodeById: function (parentNode, id) {
                var found = null;
                for (var j = parentNode.children.length - 1; 0 <= j; j--) {
                    if (parentNode.children[j].data.objectId == id) {
                        found = parentNode.children[j];
                        break;
                    }
                }
                return found;
            }
            , findNodeByPathNames: function (names) {
                var found = null;
                if (!Util.isArrayEmpty(names)) {
                    var node = DocTree.tree.getRootNode();
                    for (var i = 0; i < names.length; i++) {
                        found = this.findChildNodeByName(node, names[i]);
                        if (found) {
                            node = found;
                        } else {
                            break;
                        }
                    }
                }
                return found;
            }

            , _doDownload: function (node) {
                var url = "api/latest/plugin/ecm/download?ecmFileId=" + node.data.objectId;
                DocTree.jqFormDownloadDoc.attr("action", url);
                this.$input = $('<input>').attr({
                    id: 'fileId',
                    name: 'ecmFileId'
                });
                this.$input.val(node.data.objectId).appendTo(this.jqFormDownloadDoc);
                DocTree.jqFormDownloadDoc.submit();
                // empty jqFormDownloadDoc because everytime first input is selected and same file is downloaded
                this.jqFormDownloadDoc.empty();
            }

            // Find oldest parent in the array(not include top node).
            // In inNodes array, Parent nodes need to be before child nodes.
            , _findOldestParent: function (node, inNodes) {
                var found = null;
                if (Validator.validateNode(node) && Validator.validateNodes(inNodes)) {
                    for (var i = 0; i < inNodes.length; i++) {
                        if (!DocTree.isTopNode(inNodes[i])) {
                            var parent = node.parent;
                            while (parent && !DocTree.isTopNode(parent)) {
                                if (parent.data.objectId == inNodes[i].data.objectId) {
                                    found = parent;
                                    break;
                                }
                                parent = parent.parent;
                            }
                        }
                    }
                }
                return found;
            }
            // ignore children
            , getTopMostNodes: function (nodes) {
                var topMostNodes = [];
                for (var i = 0; i < nodes.length; i++) {
                    if (!DocTree.isTopNode(nodes[i])) {
                        var parent = DocTree._findOldestParent(nodes[i], nodes);
                        if (!parent) {
                            topMostNodes.push(nodes[i]);
                        }
                    }
                }
                return topMostNodes;
            }

            , onDblClick: function (event, data) {
                if (DocTree.readOnly) {
                    return;
                }
                var tree = $(this).fancytree("getTree"),
                    node = tree.getActiveNode();
                if (!DocTree.editSetting.isEditing) {
                    if (DocTree.isFileNode(node)) {
                        $(this).trigger("command", {cmd: "open"});
                    }
                }
                //return false;
            }
            , onClick: function (event, data) {
                if (DocTree.isSpecialNode(data.node)) {
                    DocTree.Paging.doPaging(data.node);
                }
                return true;
            }


            , Source: {
                source: function () {
                    var src = [];

                    var containerObjectType = DocTree.getObjType();
                    var containerObjectId = DocTree.getObjId();
                    if (!Util.isEmpty(containerObjectType) && !Util.isEmpty(containerObjectId)) {
                        src = Util.FancyTreeBuilder
                            .reset()
                            .addBranchLast({
                                key: containerObjectType + "." + containerObjectId
                                , title: "/"
                                , tooltip: "root"
                                , expanded: false
                                , folder: true
                                , lazy: true
                                , cache: false
                                , objectId: 0
                                , root: true
                                , startRow: 0
                                , containerObjectType: containerObjectType
                                , containerObjectId: containerObjectId
                                , totalChildren: -1
                            })
                            .getTree();
                    }
                    return src;
                }

                , _makeChildNodes: function (folderList) {
                    var builder = Util.FancyTreeBuilder.reset();
                    if (Validator.validateFolderList(folderList)) {
                        var startRow = Util.goodValue(folderList.startRow, 0);
                        var maxRows = Util.goodValue(folderList.maxRows, 0);
                        var totalChildren = Util.goodValue(folderList.totalChildren, -1);
                        var folderId = Util.goodValue(folderList.folderId, 0);

                        if (0 < startRow) {
                            builder.addLeaf({
                                key: folderId + ".prev"
                                , title: startRow + $translate.instant("common.directive.docTree.prevItems")
                                , tooltip: $translate.instant("common.directive.docTree.prevItemsTooltip")
                                , expanded: false
                                , folder: false
                                , objectType: DocTree.NODE_TYPE_PREV
                            });
                        }

                        for (var i = 0; i < folderList.children.length; i++) {
                            var child = folderList.children[i];
                            if (DocTree.NODE_TYPE_FOLDER == Util.goodValue(child.objectType)) {
                                var nodeData = DocTree.Source.getDefaultFolderNode();
                                DocTree._folderDataToNodeData(child, nodeData);
                                builder.addLeaf(nodeData);

                            }
                            if (DocTree.NODE_TYPE_FILE == Util.goodValue(child.objectType)) {
                                var nodeData = {};
                                DocTree._fileDataToNodeData(child, nodeData);
                                nodeData.folder = false;
                                builder.addLeaf(nodeData);
                            }
                        }

                        if ((0 > totalChildren) || (totalChildren - maxRows > startRow)) {//unknown size or more page
                            var title = (0 > totalChildren) ? $translate.instant("common.directive.docTree.morItemsBegin") : (totalChildren - startRow - maxRows) + $translate.instant("common.directive.docTree.moreItems");
                            builder.addLeafLast({
                                key: Util.goodValue(folderId, 0) + ".next"
                                , title: title
                                , tooltip: $translate.instant("common.directive.docTree.moreItemsTooltip")
                                , expanded: false
                                , folder: false
                                , objectType: DocTree.NODE_TYPE_NEXT
                            });
                        }
                    }
                    return builder.getTree();
                }
                , getDefaultFolderNode: function () {
                    var nodeData = {};
                    nodeData.expanded = false;
                    nodeData.folder = true;
                    nodeData.lazy = true;
                    nodeData.cache = false;
                    nodeData.totalChildren = -1;
                    nodeData.children = [];
                    return nodeData;
                }
                , lazyLoad: function (event, data) {
                    var folderNode = data.node;
                    var folderId = Util.goodValue(folderNode.data.objectId, 0);
                    if (0 >= folderId && !DocTree.isTopNode(folderNode)) {
                        data.result = [];
                        return;
                    }

                    var cacheKey = DocTree.getCacheKeyByNode(folderNode);
                    var folderList = DocTree.cacheFolderList.get(cacheKey);
                    if (Validator.validateFolderList(folderList)) {
                        data.result = DocTree.Source._makeChildNodes(folderList);

                    } else {
                        data.result = DocTree.Op.retrieveFolderList(folderNode
                            , function (folderList) {
                                folderNode.data.startRow = Util.goodValue(folderList.startRow, 0);
                                folderNode.data.totalChildren = Util.goodValue(folderList.totalChildren, -1);
                                var rc = DocTree.Source._makeChildNodes(folderList);
                                return rc;
                            }
                        );
                    }
                }
            } //end Source


            , CustomData: {
                _items: []

                , getItems: function () {
                    return this._items;
                }
                , setItems: function (items) {
                    this._items = items;
                }
                , addItem: function (item) {
                    var find = _.find(this._items, function (anItem) {
                        return anItem == item;
                    });
                    if (!find) {
                        this._items.push(item);
                    }
                }
                , addData: function (data) {
                    var arr = [];

                    if (Util.isEmpty(data)) {
                        return;
                    } else if (Util.isArray(data)) {
                        arr = data;
                    } else if ("string" == typeof data) {
                        arr = [data];
                    }

                    var that = this;
                    _.each(arr, function (item) {
                        that.addItem(item);
                    });
                }

            }

            , Column: {
                _renderers: []

                , getRenderers: function () {
                    return this._renderers;
                }
                , setRenderers: function (renderers) {
                    this._renderers = renderers;
                }

                , addRenderers: function (renderers) {
                    for (var i = 0; i < renderers.length; i++) {
                        DocTree.CustomData.addData(renderers[i].model);
                        DocTree.Column.addRenderer(renderers[i].name, renderers[i].renderer);
                    }
                }
                , addRenderer: function (name, renderer) {
                    if (Util.isEmpty(renderer)) {
                        this.removeRenderer(name);
                        return;
                    }

                    var find = _.find(this._renderers, {name: name});
                    if (find) {
                        find.renderer = renderer;
                    } else {
                        this._renderers.push({name: name, renderer: renderer});
                    }
                }

                , removeRenderer: function (name) {
                    var i = this._findRendererIdx(name);
                    this._removeRendererByIdx(i);
                }
                , _removeRendererByIdx: function (i) {
                    if (0 <= i) {
                        this._renderers.splice(i, 1);
                    }
                }
                , _findRendererIdx: function (name) {
                    for (var i = 0; i < this._renderers.length; i++) {
                        var item = this._renderers[i];
                        if (item.name == name) {
                            return i;
                        }
                    }
                    return -1;
                }
                , findRenderer: function (name) {
                    var renderer = null;
                    var find = _.find(this._renderers, {name: name});
                    if (find) {
                        renderer = find.renderer;
                    }
                    return renderer;
                }

                , getCoreRenderers: function () {
                    return [
                        {
                            name: "checkbox",
                            renderer: function (element, node, columnDef, isReadOnly) {
                                ;
                            }
                        }
                        , {
                            name: "title",
                            renderer: function (element, node, columnDef, isReadOnly) {
                                ;
                            }
                        }
                        , {
                            name: "ext",
                            renderer: function (element, node, columnDef, isReadOnly) {
                                $(element).text(node.data.ext);
                            }
                        }
                        , {
                            name: "type",
                            renderer: function (element, node, columnDef, isReadOnly) {
                                var filter = $filter('capitalizeFirst');
                                var typeColumn = (DocTree.getDocumentTypeDisplayLabel(node.data.type));
                                var filteredType = filter(typeColumn);

                                $(element).text(filteredType); // document type is mapped (afdp-1249)
                            }
                        }
                        , {
                            name: "created",
                            renderer: function (element, node, columnDef, isReadOnly) {
                                var versionDate = UtilDateService.getDatePart(node.data.created);
                                $(element).text(versionDate);
                            }
                        }
                        , {
                            name: "modified",
                            renderer: function (element, node, columnDef, isReadOnly) {
                                var versionDate = UtilDateService.getDatePart(node.data.modified);
                                $(element).text(versionDate);
                            }
                        }
                        , {
                            name: "author",
                            renderer: function (element, node, columnDef, isReadOnly) {
                                var versionUser = Util.goodValue(node.data.creator);
                                if (versionUser) {
                                    //UserInfoService.getUserInfoByIdQuietly(versionUser).then(function (userInfo) {
                                    UserInfoService.getUserInfoById(versionUser).then(function (userInfo) {
                                        $(element).text(Util.goodMapValue(userInfo, "fullName"));
                                    })
                                }
                            }
                        }
                        , {
                            name: "version",
                            renderer: function (element, node, columnDef, isReadOnly) {
                                if (isReadOnly) {
                                    $(element).text(node.data.version);

                                } else {
                                    var $td = $("<td/>");
                                    var $span = $("<span/>").appendTo($td);
                                    var $select = $("<select/>")
                                        .addClass('docversion inline')
                                        .appendTo($span);

                                    if (Util.isArray(node.data.versionList)) {
                                        for (var v = 0; v < node.data.versionList.length; v++) {
                                            var versionTag = node.data.versionList[v].versionTag;
                                            var $option = $("<option/>")
                                                .val(versionTag)
                                                .text(versionTag)
                                                .appendTo($select);

                                            if (Util.goodValue(node.data.version) == versionTag) {
                                                $option.attr("selected", true);

                                                //versionDate = UtilDateService.getDatePart(node.data.versionList[v].created);
                                                //versionUser = Util.goodValue(node.data.versionList[v].creator);
                                            }
                                        }
                                    }
                                    $(element).replaceWith($td);
                                }
                            }
                        }
                        , {
                            name: "status",
                            renderer: function (element, node, columnDef, isReadOnly) {
                                $(element).text(node.data.status);
                            }
                        }
                    ];
                }
            }

            , Command: {
                _handlers: []

                , getHandlers: function () {
                    return this._handlers;
                }
                , setHandlers: function (handlers) {
                    this._handlers = handlers;
                }

                , addHandlers: function (handlers) {
                    for (var i = 0; i < handlers.length; i++) {
                        this.addHandler(handlers[i]);
                    }
                }

                , addHandler: function (handler) {
                    var name = handler.name;
                    if (null === handler.execute) {
                        this.removeHandler(name);
                        return;
                    }

                    var find = _.find(this._handlers, {name: name});
                    if (find) {
                        find.prevHandler = _.clone(find);
                        _.each(handler, function (value, key) {
                            if (!Util.isEmpty(handler[key])) {
                                find[key] = handler[key];
                            }
                        });

                    } else {
                        this._handlers.push(handler);
                    }
                }

                , removeHandler: function (name) {
                    var i = this._findHandlerIdx(name);
                    this._removeHandlerByIdx(i);
                }
                , _removeHandlerByIdx: function (i) {
                    if (0 <= i) {
                        this._handlers.splice(i, 1);
                    }
                }
                , _findHandlerIdx: function (name) {
                    for (var i = 0; i < this._handlers.length; i++) {
                        var handler = this._handlers[i];
                        if (handler.name == name) {
                            return i;
                        }
                    }
                    return -1;
                }
                , findHandler: function (name) {
                    return _.find(this._handlers, {name: name});
                }

                , executeSubmitFiles: function (nodes, args) {
                    if (!DocTree.uploadSetting) {
                        $log.warn("Warning: No files are selected");
                        return;
                    }

                    var files = args.files;
                    var promiseUploadFile = DocTree.doSubmitFormUploadFile(files);
                    $q.when(promiseUploadFile).then(function (data) {
                        args.data = data;
                        DocTree.uploadSetting = null;
                    });
                    return promiseUploadFile;
                }

                , getCoreHandlers: function () {
                    return [
                        {
                            name: "moveUp",
                            execute: function (nodes, args) {
                                var node = nodes[0];
                                if (node) {
                                    var refNode = node.getPrevSibling();
                                    if (refNode) {
                                        node.moveTo(refNode, "before");
                                        node.setActive();
                                    }
                                }
                            }
                        }
                        , {
                            name: "moveDown",
                            execute: function (nodes, args) {
                                var node = nodes[0];
                                if (node) {
                                    var refNode = node.getNextSibling();
                                    if (refNode) {
                                        node.moveTo(refNode, "after");
                                        node.setActive();
                                    }
                                }
                            }
                        }
                        , {
                            name: "indent",
                            execute: function (nodes, args) {
                                var node = nodes[0];
                                if (node) {
                                    var refNode = node.getPrevSibling();
                                    if (refNode) {
                                        node.moveTo(refNode, "child");
                                        refNode.setExpanded();
                                        node.setActive();
                                    }
                                }
                            }
                        }
                        , {
                            name: "outdent",
                            execute: function (nodes, args) {
                                var node = nodes[0];
                                if (!node.isTopLevel()) {
                                    node.moveTo(node.getParent(), "after");
                                    node.setActive();
                                }
                            }
                        }
                        , {
                            name: "rename",
                            execute: function (nodes, args) {
                                var node = nodes[0];
                                node.editStart();
                            }
                        }
                        , {
                            name: "remove",
                            execute: function (nodes, args) {
                                DocTree.Op.batchRemove(nodes);
                            }
                        }
                        , {
                            name: "addChild",
                            execute: function (nodes, args) {
                                var node = nodes[0];
                                node.editCreateNode("child", "");
                            }
                        }
                        , {
                            name: "addSibling",
                            handler: {
                                execute: function (nodes, args) {
                                    var node = nodes[0];
                                    node.editCreateNode("after", "");
                                }
                            }
                        }
                        , {
                            name: "newFolder",
                            execute: function (nodes, args) {
                                if (!DocTree.editSetting.isEditing) {
                                    var node = nodes[0];
                                    var nodeData = DocTree.Source.getDefaultFolderNode();
                                    nodeData.title = "New Folder";
                                    if (DocTree.isFileNode(node)) {
                                        node = node.getParent();
                                    }
                                    node.editCreateNode("child", nodeData);
                                }
                            }
                        }
                        , {
                            name: "newDocument",
                            execute: function (nodes, args) {
                                if (!DocTree.editSetting.isEditing) {
                                    var node = nodes[0];
                                    DocTree.uploadSetting = {
                                        uploadToFolderNode: node
                                        //, uploadFileType: fileType
                                        , uploadFileNew: true
                                        , deferUploadFile: $q.defer()
                                    };
                                    DocTree.uploadFile();
                                }
                            }
                        }
                        , {
                            name: "cut",
                            execute: function (nodes, args) {
                                var batch = 1 < nodes.length;
                                if (batch) {
                                    DocTree.checkNodes(nodes, false);
                                }
                                DocTree.CLIPBOARD = {mode: "cut", batch: batch, data: nodes};
                            }
                        }
                        , {
                            name: "copy",
                            execute: function (nodes, args) {
                                var batch = 1 < nodes.length;
                                if (batch) {
                                    DocTree.checkNodes(nodes, false);
                                }
                                var clones = [];
                                for (var i = 0; i < nodes.length; i++) {
                                    var clone = nodes[i].toDict(false, function (n) {
                                        delete n.key;
                                    });
                                    clones.push(clone);
                                }
                                DocTree.CLIPBOARD = {mode: "copy", batch: batch, data: clones, src: nodes};
                            }
                        }
                        , {
                            name: "clear",
                            execute: function (nodes, args) {
                                DocTree.CLIPBOARD = null;
                            }
                        }
                        , {
                            name: "paste",
                            execute: function (nodes, args) {
                                var node = nodes[0];
                                DocTree.expandNode(node).done(function () {
                                    var mode = DocTree.isFolderNode(node) ? "child" : "after";
                                    if (DocTree.CLIPBOARD.mode === "cut") {
                                        DocTree.Op.batchMove(DocTree.CLIPBOARD.data, node, mode);
                                    } else if (DocTree.CLIPBOARD.mode === "copy") {
                                        DocTree.Op.batchCopy(DocTree.CLIPBOARD.src, DocTree.CLIPBOARD.data, node, mode);
                                    }
                                });
                            }
                        }
                        , {
                            name: "download",
                            execute: function (nodes, args) {
                                var node = nodes[0];
                                DocTree._doDownload(node);
                            }
                        }
                        , {
                            name: "replace",
                            execute: function (nodes, args) {
                                var selectFiles = DocTree.Command.findHandler("selectReplacement/");
                                selectFiles.execute(nodes, args);

                                $q.when(DocTree.uploadSetting.deferSelectFile.promise).then(function (files) {
                                    args = args || {};
                                    args.files = files;
                                    var replaceFiles = DocTree.Command.findHandler("replaceFiles/");
                                    DocTree.Command.handleCommand(replaceFiles, nodes, args);
                                });
                            }
                        }
                        , {
                            name: "open",
                            execute: function (nodes, args) {
                                var node = nodes[0];

                                // Any documents which are checked in doctree will be opened in the viewer simultaneously
                                // in addition to the document which is directly opened (double-clicked)
                                var selectedIdsList = "";
                                _.forEach(nodes, function (value) {
                                    if (value.data && value.data.objectId && value.data.objectType) {
                                        if (value.data.objectType.toLowerCase() != "folder") {
                                            selectedIdsList += value.data.objectId + ",";
                                        }
                                    }
                                });
                                //$(".fancytree-selected:not('.fancytree-folder')").find(".btn-group a").each(function () {
                                //    selectedIdsList += this.innerText.trim() + ",";
                                //});

                                // removes trailing comma from the id list
                                if (selectedIdsList.length > 0)
                                    selectedIdsList = selectedIdsList.substring(0, selectedIdsList.length - 1);

                                // Opens the snowbound viewer and loads the selected document(s) into it
                                var baseUrl = window.location.href.split('!')[0];
                                var urlArgs = node.data.objectId + "/" + node.parent.data.containerObjectId + "/" +
                                    node.parent.data.containerObjectType + "/" + node.data.name + "/" + selectedIdsList;
                                window.open(baseUrl + '!/viewer/' + urlArgs);
                            }
                        }
                        , {
                            name: "edit",
                            execute: function (nodes, args) {
                            }
                        }
                        , {
                            name: "editForm",
                            execute: function (nodes, args) {
                                var node = nodes[0];
                                DocTree.editForm(node);
                            }
                        }
                        , {
                            name: "print",
                            execute: function (nodes, args) {
                            }
                        }
                        , {
                            name: "form/",
                            getArgs: function (data) {
                                return {fileType: data.cmd.substring(this.name.length)};
                            },
                            execute: function (nodes, args) {
                                var node = nodes[0];
                                DocTree.uploadForm(node, args.fileType);
                            }
                        }
                        , {
                            name: "file/",
                            getArgs: function (data) {
                                return {fileType: data.cmd.substring(this.name.length)};
                            },
                            execute: function (nodes, args) {
                                var selectFiles = DocTree.Command.findHandler("selectFiles/");
                                selectFiles.execute(nodes, args);

                                $q.when(DocTree.uploadSetting.deferSelectFile.promise).then(function (files) {
                                    args = args || {};
                                    args.files = files;
                                    var submitFiles = DocTree.Command.findHandler("submitFiles/");
                                    DocTree.Command.handleCommand(submitFiles, nodes, args);
                                });
                            }
                        }
                        , {
                            name: "submitFiles/",
                            execute: function (nodes, args) {
                                return DocTree.Command.executeSubmitFiles(nodes, args);
                            }
                        }
                        , {
                            name: "replaceFiles/",
                            execute: function (nodes, args) {
                                return DocTree.Command.executeSubmitFiles(nodes, args);
                            }
                        }
                        , {
                            name: "selectFiles/",
                            execute: function (nodes, args) {
                                if (DocTree.uploadSetting) {
                                    $log.warn("Warning: Trying to upload file before previous upload");
                                }
                                var node = nodes[0];
                                DocTree.uploadSetting = {
                                    uploadToFolderNode: node
                                    , uploadFileType: args.fileType
                                    , uploadFileNew: true
                                    , deferUploadFile: $q.defer()
                                    , deferSelectFile: $q.defer()
                                };

                                DocTree.uploadFile();

                                return DocTree.uploadSetting.deferSelectFile.promise;
                            }
                        }
                        , {
                            name: "selectReplacement/",
                            execute: function (nodes, args) {
                                if (DocTree.uploadSetting) {
                                    $log.warn("Warning: Trying to upload file before previous upload");
                                }
                                var node = nodes[0];
                                var fileType = Util.goodValue(node.data.type);
                                if (!Util.isEmpty(fileType)) {
                                    DocTree.uploadSetting = {
                                        replaceFileNode: node
                                        , uploadToFolderNode: node.parent
                                        , uploadFileType: fileType
                                        , uploadFileNew: false
                                        , deferUploadFile: $q.defer()
                                        , deferSelectFile: $q.defer()
                                    };

                                    DocTree.replaceFile();
                                }
                                return DocTree.uploadSetting.deferSelectFile.promise;
                            }
                        }

                    ];
                }

                , onCommand: function (event, data) {
                    var tree = DocTree.tree;
                    var selNodes = tree.getSelectedNodes();
                    var node = tree.getActiveNode();
                    var nodes = selNodes;
                    if (Util.isArrayEmpty(selNodes)) {
                        nodes = [node];
                    }

                    var tokens = data.cmd.split("/");
                    var name = tokens[0];
                    if (1 < tokens.length) {
                        name += "/";
                    }

                    var handler = DocTree.Command.findHandler(name);
                    if (handler) {
                        var args;
                        if (handler.getArgs) {
                            args = handler.getArgs(data);
                        }

                        if ("file/" == name) {
                            handler.execute(nodes, args);
                        } else {
                            DocTree.Command.handleCommand(handler, nodes, args);
                        }
                    }
                }

                //
                // Prevent command process if onPreCmd returns "false";
                // continue when onPreCmd returns "true", "undefined" or anything else
                //
                , handleCommand: function (handler, nodes, args) {
                    var rcPre = true;
                    var rcExe = true;

                    if (handler.onPreCmd) {
                        rcPre = handler.onPreCmd(nodes, args);
                    }
                    if (false !== rcPre) {
                        $q.all([rcPre]).then(function (preCmdData) {
                            if (false !== preCmdData[0]) {
                                if (handler.execute) {
                                    rcExe = handler.execute(nodes, args);
                                }
                                $q.all([rcExe]).then(function () {
                                    if (handler.onPostCmd) {
                                        handler.onPostCmd(nodes, args);
                                    }
                                });
                            }
                        });
                    }
                }

                , onKeyDown: function (event, data) {
                    var cmd = null;
                    switch ($.ui.fancytree.eventToString(event)) {
                        case "ctrl+shift+n":
                        case "meta+shift+n": // mac: cmd+shift+n
                            cmd = "addChild";
                            break;
                        case "ctrl+c":
                        case "meta+c": // mac
                            cmd = "copy";
                            break;
                        case "ctrl+v":
                        case "meta+v": // mac
                            cmd = "paste";
                            break;
                        case "ctrl+x":
                        case "meta+x": // mac
                            cmd = "cut";
                            break;
                        case "ctrl+n":
                        case "meta+n": // mac
                            cmd = "addSibling";
                            break;
                        case "del":
                        case "meta+backspace": // mac
                            cmd = "remove";
                            break;
                        // case "f2":  // already triggered by ext-edit pluging
                        //   cmd = "rename";
                        //   break;
                        case "ctrl+up":
                            cmd = "moveUp";
                            break;
                        case "ctrl+down":
                            cmd = "moveDown";
                            break;
                        case "ctrl+right":
                        case "ctrl+shift+right": // mac
                            cmd = "indent";
                            break;
                        case "ctrl+left":
                        case "ctrl+shift+left": // mac
                            cmd = "outdent";
                        case "ctrl+p":
                        case "meta+p": // mac
                            cmd = "print";
                            break;
                    }
                    if (cmd) {
                        $(this).trigger("command", {cmd: cmd});
                        // event.preventDefault();
                        // event.stopPropagation();
                        return false;
                    }
                }
                , getCommandObject: function (cmd) {
                    return {cmd: cmd};
                }
                , trigger: function (cmd) {
                    DocTree.jqTree.trigger("command", {cmd: cmd});
                }
            } //end Command


            , Menu: {
                useContextMenu: function ($s) {
                    $s.contextmenu({
                        menu: []
                        //,delegate: "span.fancytree-node"
                        , delegate: "tr"
                        , beforeOpen: function (event, ui) {
                            var dfd = new $.Deferred();
                            var node = $.ui.fancytree.getNode(ui.target);
                            if (DocTree.isSpecialNode(node)) {
                                return false;
                            }

                            var menuResource = null;
                            var selNodes = DocTree.getSelectedNodes();
                            var batchMode = !Util.isArrayEmpty(selNodes);
                            var nodes = (batchMode) ? selNodes : [node];
                            if (batchMode) {
                                menuResource = DocTree.Menu.getBatchResource(nodes);
                            } else if ("RECORD" == Util.goodValue(node.data.status)) {
                                menuResource = DocTree.Menu.getRecordResource(node);
                            } else {
                                menuResource = DocTree.Menu.getBasicResource(node);
                            }
                            var menu = DocTree.Menu.makeContextMenu(menuResource, nodes);

                            $q.when(menu).then(function (menuResult) {
                                $s.contextmenu("replaceMenu", menuResult);

                                if (!batchMode) {
                                    $s.contextmenu("enableEntry", "paste", !!DocTree.CLIPBOARD);
                                    node.setActive();
                                }

                                dfd.resolve(); // Notify about finished response
                            });

                            // Return a promise to delay opening until an async response becomes available
                            ui.result = dfd.promise();
                        }
                        , select: function (event, ui) {
                            var uploadFile = Util.goodMapValue(ui.item.data(), "uploadFile", false);
                            var uploadFileLabel = Util.goodMapValue(ui.item.data(), "label", "");
                            $(this).trigger("command", {
                                cmd: ui.cmd,
                                uploadFile: uploadFile,
                                label: uploadFileLabel
                            });
                        }
                    });
                }

                , getBatchResource: function (nodes) {
                    var menuResource = null;
                    if (Validator.validateNodes(nodes)) {
                        var countFolder = 0;
                        var countFile = 0;
                        var hasFileRecord = false;
                        for (var i = 0; i < nodes.length; i++) {
                            if (DocTree.isFolderNode(nodes[i])) {
                                countFolder++;
                            } else if (DocTree.isFileNode(nodes[i])) {
                                countFile++;
                                if (nodes[i].data.status == "RECORD") {
                                    hasFileRecord = true;
                                }
                            }
                        }

                        if (countFile > 0 && countFolder > 0) { // files and folders menu
                            menuResource = (hasFileRecord ? "menu.batch.filesOrFoldersRecord" : "menu.batch.filesAndFolders");
                        } else if (countFolder > 0) {       // folders only menu
                            menuResource = "menu.batch.folders";
                        } else if (countFile > 0) {        // files only menu
                            menuResource = (hasFileRecord ? "menu.record.file" : "menu.batch.files");
                        }

                    }

                    return menuResource;
                }
                , getRecordResource: function (node) {
                    var menuResource = null;
                    if (node) {
                        if (DocTree.isTopNode(node)) {
                            menuResource = "menu.record.root";
                        } else if (DocTree.isFolderNode(node)) {
                            menuResource = "menu.record.folder";
                        } else if (DocTree.isFileNode(node)) {
                            menuResource = "menu.record.file";
                        }
                    }
                    return menuResource;
                }
                , getBasicResource: function (node) {
                    var menuResource = null;
                    if (node) {
                        if (DocTree.isTopNode(node)) {
                            menuResource = "menu.basic.root";
                        } else if (DocTree.isFolderNode(node)) {
                            menuResource = "menu.basic.folder";
                        } else if (DocTree.isFileNode(node)) {
                            menuResource = "menu.basic.file";
                        }
                    }
                    return menuResource;
                }
                , makeContextMenu: function (menuResource, nodes) {
                    var promiseArray = [];
                    var menuDeferred = $q.defer();
                    var menu;
                    if (menuResource) {
                        menu = Util.goodMapValue(DocTree.treeConfig, menuResource, []);
                        menu = _.clone(menu);
                        var menuFileTypes = _.find(menu, {"cmd": "subMenuFileTypes"});
                        if (menuFileTypes) {
                            menuFileTypes.children = this.makeSubMenu(DocTree.fileTypes);
                        }
                        var menuCorrespondenceForms = _.find(menu, {cmd: "subMenuCorrespondenceForms"});
                        if (_.isEmpty(DocTree.correspondenceForms) && menuCorrespondenceForms) {
                            menuCorrespondenceForms.invisible = true;
                        } else if (menuCorrespondenceForms) {
                            menuCorrespondenceForms.children = this.makeSubMenu(DocTree.correspondenceForms);
                        }
                        //} else {
                        //    var menu0 = [Util.goodMapValue(DocTree.treeConfig, "noop")];
                        //    menu = [{
                        //        title: $translate.instant("common.directive.docTree.menu.noop"),
                        //        cmd: "noop",
                        //        uiIcon: ""
                        //    }];
                    }

                    //Check to see if there is a global handling, if there is, it would override specific handler
                    var globalHandler = DocTree.Command.findHandler("global");
                    var onAllowCmdGlobal = Util.goodMapValue(globalHandler, "onAllowCmd", null);
                    var allowGlobal;
                    if (onAllowCmdGlobal) {
                        allowGlobal = onAllowCmdGlobal(nodes);
                    }

                    _.each(menu, function (item) {
                        var allow = true;
                        if (undefined !== allowGlobal) {
                            allow = allowGlobal;

                        } else {
                            if (item.cmd) {
                                var found = DocTree.Command.findHandler(item.cmd);
                                var onAllowCmd = Util.goodMapValue(found, "onAllowCmd", null);
                                if (onAllowCmd) {
                                    allow = onAllowCmd(nodes);
                                }
                            }
                        }

                        promiseArray.push(allow);
                        $q.when(allow).then(function (allowResult) {
                            if ("invisible" == allowResult) {
                                item.invisible = true;
                            } else if ("disable" == allowResult) {
                                item.disabled = true;
                            } else {
                                if (item.disabledExpression) {
                                    item.disabled = eval(item.disabledExpression);
                                } else {
                                    item.disabled = false;
                                }
                            }
                        });
                    });

                    $q.all(promiseArray).then(function () {
                        menu = _.filter(menu, function (item) {
                            return !item.invisible;
                        });

                        //Under readOnly mode, disable all non-readOnly cmd
                        if (DocTree.readOnly) {
                            _.each(menu, function (item) {
                                var readOnly = Util.goodMapValue(item.data, "readOnly", false);
                                if (readOnly) {
                                    item.disabled = false;
                                }
                            });

                        }

                        menuDeferred.resolve(menu);
                    });


                    return menuDeferred.promise;
                }


                // To create a menu like this:
                //        var menu = [
                //            {title: "Electronic Communication", cmd: "form/electronicCommunicationFormUrl"}
                //            ,{title: "Report of Investigation", cmd: "form/roiFormUrl"}
                //            ,{title: "Medical Release", cmd: "file/mr"}
                //            ,{title: "General Release", cmd: "file/gr"}
                //            ,{title: "eDelivery", cmd: "file/ev"}
                //            ,{title: "SF86 Signature", cmd: "file/sig"}
                //            ,{title: "Notice of Investigation", cmd: "file/noi"}
                //            ,{title: "Witness Interview Request", cmd: "file/wir"}
                //            ,{title: "Other", cmd: "file/other"}
                //        ];
                , makeSubMenu: function (subTypes) {
                    var menu = [], item;
                    if (subTypes) {
                        if (Util.isArray(subTypes)) {
                            for (var i = 0; i < subTypes.length; i++) {
                                item = {};
                                if (!Util.isEmpty(subTypes[i].label)) {
                                    item.title = subTypes[i].label;
                                }
                                if (!Util.isEmpty(subTypes[i].type)) {
                                    if (!Util.isEmpty(subTypes[i].form)) {
                                        item.cmd = "form/" + subTypes[i].type;
                                    }
                                    else {
                                        item.cmd = "file/" + subTypes[i].type;
                                        item.data = {};
                                        item.data.uploadFile = true;
                                    }
                                }
                                else if (!Util.isEmpty(subTypes[i].templateFilename)) {
                                    item.cmd = "template/" + subTypes[i].templateFilename;
                                    item.data = {};
                                    item.data.label = subTypes[i].label;
                                }
                                menu.push(item);
                            }
                        }
                    }
                    return menu;
                }
            }

            , Paging: {
                _triggerNode: null
                , alertPaging: function (node) {
                    DocTree.Paging._triggerNode = node;
                    setTimeout(function () {
                        var node = DocTree.Paging._triggerNode;
                        DocTree.Paging.doPaging(node);
                    }, 2500);
                }
                , relievePaging: function () {
                    DocTree.Paging._triggerNode = null;
                }
                , doPaging: function (node) {
                    if (!node) {
                        return;
                    }
                    var parent = node.getParent();
                    if (!parent) {
                        return;
                    }

                    if (DocTree.NODE_TYPE_PREV == node.data.objectType) {
                        var startRow = Util.goodValue(parent.data.startRow, 0) - Util.goodValue(parent.data.maxRows, DocTree.Config.getMaxRows());
                        if (0 > startRow) {
                            startRow = 0;
                        }
                        parent.data.startRow = startRow;
                        parent.resetLazy();
                        parent.setExpanded(true);
                    } else if (DocTree.NODE_TYPE_NEXT == node.data.objectType) {
                        var startRow = Util.goodValue(parent.data.startRow, 0) + Util.goodValue(parent.data.maxRows, DocTree.Config.getMaxRows());
                        var totalChildren = Util.goodValue(parent.data.totalChildren, -1);
                        if (0 <= totalChildren) {   // -1 is a special value for unknown totalChildren; keep increasing in this case
                            if (totalChildren <= startRow) {
                                startRow = totalChildren - 1;
                                if (0 > startRow) {
                                    startRow = 0;
                                }
                            }
                        }
                        parent.data.startRow = startRow;
                        parent.resetLazy();
                        parent.setExpanded(true);
                    }
                }
            }

            , Dnd: {} //end Dnd


            , ExternalDnd: {
                startExternalDnd: function ($treeBody) {
                    $treeBody.on("dragenter", "tr", this.onDragEnter);
                    $treeBody.on("dragleave", "tr", this.onDragLeave);
                    $treeBody.on("dragover", "tr", this.onDragOver);
                    $treeBody.on("drop", "tr", this.onDragDrop);

                    $(document).on('dragenter', function (e) {
                        e.stopPropagation();
                        e.preventDefault();
                    });
                    $(document).on('dragover', function (e) {
                        e.stopPropagation();
                        e.preventDefault();
                    });
                    $(document).on('drop', function (e) {
                        e.stopPropagation();
                        e.preventDefault();
                    });
                }

                , onDragEnter: function (e) {
                    e.stopPropagation();
                    e.preventDefault();
                    $(this).addClass("dragover");
                }
                , onDragOver: function (e) {
                    e.stopPropagation();
                    e.preventDefault();
                    $(this).addClass("dragover");
                }
                , onDragLeave: function (e) {
                    e.stopPropagation();
                    e.preventDefault();
                    $(this).removeClass("dragover");
                }
                , onDragDrop: function (e) {
                    //e.stopPropagation();
                    e.preventDefault();
                    $(this).removeClass("dragover");

                    if (DocTree.readOnly) {
                        return;
                    }

                    var node = $.ui.fancytree.getNode(e);
                    var files = e.originalEvent.dataTransfer.files;
                    if (files instanceof FileList) {
                        if (DocTree.isFolderNode(node)) {
                            DialogDnd.openModal("folder"
                                , _.filter(DocTree.fileTypes, function (fileType) {
                                    return Util.isEmpty(fileType.form);
                                })
                                , function (result) {
                                    var op = result.op;
                                    var fileType = result.fileType.type;
                                    if (DialogDnd.OpTypes.OP_UPLOAD_TO_FOLDER == op && !Util.isEmpty(fileType)) {
                                        DocTree.uploadSetting = {
                                            uploadToFolderNode: node
                                            , uploadFileType: fileType
                                            , uploadFileNew: true
                                            , deferUploadFile: $q.defer()
                                        };
                                        var args = {files: files};
                                        var submitFiles = DocTree.Command.findHandler("submitFiles/");
                                        DocTree.Command.handleCommand(submitFiles, [node], args);
                                    }
                                }
                            );


                        } else if (DocTree.isFileNode(node)) {
                            DialogDnd.openModal("file"
                                , _.filter(DocTree.fileTypes, function (fileType) {
                                    return Util.isEmpty(fileType.form);
                                })
                                , function (result) {
                                    var op = result.op;
                                    var fileType = result.fileType.type;
                                    if (DialogDnd.OpTypes.OP_REPLACE == op) {
                                        DocTree.uploadSetting = {
                                            replaceFileNode: node
                                            , uploadToFolderNode: node.parent
                                            , uploadFileType: Util.goodValue(node.data.type)
                                            , uploadFileNew: false
                                            , deferUploadFile: $q.defer()
                                        };
                                        var args = {files: files};
                                        var replaceFiles = DocTree.Command.findHandler("replaceFiles/");
                                        DocTree.Command.handleCommand(replaceFiles, [node], args);

                                    } else if (DialogDnd.OpTypes.OP_UPLOAD_TO_PARENT == op && !Util.isEmpty(fileType)) {
                                        DocTree.uploadSetting = {
                                            uploadToFolderNode: node.parent
                                            , uploadFileType: fileType
                                            , uploadFileNew: true
                                            , deferUploadFile: $q.defer()
                                        };
                                        var args = {files: files};
                                        var submitFiles = DocTree.Command.findHandler("submitFiles/");
                                        DocTree.Command.handleCommand(submitFiles, [node], args);
                                    }
                                }
                            );
                        }
                    }

                }
            } //end ExternalDnd

            , Op: {
                retrieveFolderList: function (folderNode, callbackSuccess) {
                    var dfd = $.Deferred();
                    if (!DocTree.isFolderNode(folderNode)) {
                        dfd.reject();

                    } else {
                        var param = {};
                        param.objType = DocTree.getObjType();
                        param.objId = DocTree.getObjId();
                        var folderId = Util.goodValue(folderNode.data.objectId, 0);
                        if (DocTree.isTopNode(folderNode)) {
                            folderId = 0;
                        }
                        if (0 < folderId) {
                            param.folderId = folderId;
                        }
                        var pageId = Util.goodValue(folderNode.data.startRow, 0);
                        var pageSize = Util.goodValue(DocTree.Config.getMaxRows(), 0);
                        param.start = pageId;
                        param.n = pageSize;
                        var setting = DocTree.Config.getSetting();
                        if (!Util.isEmpty(setting.sortBy) && !Util.isEmpty(setting.sortDirection)) {
                            param.sortBy = setting.sortBy;
                            param.sortDir = setting.sortDirection;
                        }

                        Util.serviceCall({
                            service: Ecm.retrieveFolderList
                            , param: param
                            , onSuccess: function (data) {
                                if (Validator.validateFolderList(data)) {
                                    var folderList = data;
                                    var setting = DocTree.Config.getSetting();
                                    setting.maxRows = Util.goodValue(folderList.maxRows, 0);
                                    setting.sortBy = Util.goodValue(folderList.sortBy);
                                    setting.sortDirection = Util.goodValue(folderList.sortDirection);

                                    var cacheKey = DocTree.getCacheKey(folderId, pageId);
                                    DocTree.cacheFolderList.put(cacheKey, folderList);
                                    return folderList;
                                }
                            }
                        }).then(
                            function (folderList) {
                                if (folderList) {
                                    folderNode.data.objectId = Util.goodValue(folderList.folderId, 0);
                                    folderNode.data.totalChildren = Util.goodValue(folderList.totalChildren, 0);
                                    folderNode.renderTitle();
                                    DocTree.markNodeOk(folderNode);
                                    var rc = callbackSuccess(folderList);
                                    dfd.resolve(rc);
                                } else {
                                    DocTree.markNodeError(folderNode);
                                    dfd.reject();
                                }
                            }
                            , function (errorData) {
                                DocTree.markNodeError(folderNode);
                                dfd.reject();
                            }
                        );
                    }

                    return dfd.promise();
                }
                , createFolder: function (newNode, folderName) {
                    var dfd = $.Deferred();
                    var parent = newNode.getParent();
                    if (!DocTree.isFolderNode(parent)) {
                        dfd.reject();

                    } else {
                        if (!newNode) {
                            var nodeData = DocTree.Source.getDefaultFolderNode();
                            nodeData.title = folderName;
                            newNode = parent.addChildren(nodeData);
                            newNode.setActive();
                            DocTree.markNodePending(newNode);
                        }

                        var cacheKey = DocTree.getCacheKeyByNode(parent);
                        var parentId = parent.data.objectId;
                        Util.serviceCall({
                            service: Ecm.createFolder
                            , param: {
                                parentId: parentId
                                , folderName: folderName
                            }
                            , data: {}
                            , onSuccess: function (data) {
                                if (Validator.validateCreateInfo(data)) {
                                    if (data.parentFolder.id == parentId) {
                                        var createInfo = data;
                                        var folderList = DocTree.cacheFolderList.get(cacheKey);
                                        if (Validator.validateFolderList(folderList)) {
                                            var createdFolder = DocTree.folderToSolrData(createInfo);
                                            folderList.children.push(createdFolder);
                                            folderList.totalChildren++;
                                            DocTree.cacheFolderList.put(cacheKey, folderList);

                                            DocTree._folderDataToNodeData(createdFolder, newNode);
                                            return newNode;
                                        }
                                    }
                                }
                            }
                        }).then(
                            function (newNode) {
                                DocTree.markNodeOk(newNode);
                                newNode.renderTitle();
                                dfd.resolve(newNode);

                            }
                            , function (errorData) {
                                DocTree.markNodeError(newNode);
                                dfd.reject();
                            }
                        );
                    }
                    return dfd.promise();
                }
                , uploadFiles: function (formData, folderNode, names, fileType) {
                    var dfd = $.Deferred();
                    if (!DocTree.isFolderNode(folderNode)) {
                        dfd.reject();

                    } else {
                        var promiseAddNodes = DocTree._addingFileNodes(folderNode, names, fileType);
                        var cacheKey = DocTree.getCacheKeyByNode(folderNode);
                        var promiseUploadFiles = Util.serviceCall({
                            service: Ecm.uploadFiles
                            , data: formData
                            , onSuccess: function (data) {
                                if (Validator.validateUploadInfo(data)) {
                                    var uploadInfo = data;

                                    var folderList = DocTree.cacheFolderList.get(cacheKey);
                                    if (Validator.validateFolderList(folderList)) {
                                        var uploadedFiles = [];
                                        for (var i = 0; i < uploadInfo.length; i++) {
                                            var uploadedFile = DocTree.fileToSolrData(uploadInfo[i]);
                                            uploadedFile.originalName = names[i];
                                            uploadedFiles.push(uploadedFile);
                                            folderList.children.push(uploadedFile);
                                            folderList.totalChildren++;
                                        }
                                        DocTree.cacheFolderList.put(cacheKey, folderList);
                                        return uploadedFiles;
                                    }
                                }
                            }
                        });


                        $q.all([promiseUploadFiles, promiseAddNodes]).then(
                            function (successData) {
                                var uploadedFiles = successData[0];
                                var fileNodes = successData[1];
                                if (!Util.isArrayEmpty(uploadedFiles) && Validator.validateFancyTreeNodes(fileNodes)) {
                                    for (var i = 0; i < uploadedFiles.length; i++) {
                                        var uploadedFile = uploadedFiles[i];
                                        var type = Util.goodValue(uploadedFile.type);
                                        var originalName = Util.goodValue(uploadedFile.originalName);
                                        var fileNode = DocTree._matchFileNode(type, originalName, fileNodes);
                                        if (fileNode) {
                                            DocTree._fileDataToNodeData(uploadedFile, fileNode);
                                            fileNode.renderTitle();
                                            fileNode.setStatus("ok");
                                        }
                                    } //end for
                                    dfd.resolve({files: uploadedFiles, nodes: fileNodes});
                                }
                            }
                            , function (errorData) {
                                DocTree.refreshTree();
                                dfd.reject();
                            }
                        );
                    }
                    return dfd.promise();
                }
                , replaceFile: function (formData, fileNode, name) {
                    var dfd = $.Deferred();
                    if (!DocTree.isFileNode(fileNode)) {
                        dfd.reject();

                    } else {
                        DocTree.markNodePending(fileNode);

                        var folderNode = fileNode.getParent();
                        var fileId = fileNode.data.objectId;
                        var cacheKey = DocTree.getCacheKeyByNode(folderNode);
                        Util.serviceCall({
                            service: Ecm.replaceFile
                            , param: {
                                fileId: fileId
                            }
                            , data: formData
                            , onSuccess: function (data) {
                                if (Validator.validateReplaceInfo(data)) {
                                    var replaceInfo = data;

                                    //i think file will have different id because it is new file
                                    if (replaceInfo.fileId == fileId) {
                                        var folderList = DocTree.cacheFolderList.get(cacheKey);
                                        if (Validator.validateFolderList(folderList)) {
                                            var replaced = DocTree.findFolderItemIdx(fileId, folderList);
                                            if (0 <= replaced) {
                                                folderList.children[replaced].ext = Util.goodValue(replaceInfo.fileActiveVersionNameExtension);
                                                folderList.children[replaced].modified = Util.goodValue(replaceInfo.modified);
                                                folderList.children[replaced].version = Util.goodValue(replaceInfo.activeVersionTag);

                                                folderList.children[replaced].versionList = [];
                                                if (Util.isArray(replaceInfo.versions)) {
                                                    for (var i = 0; i < replaceInfo.versions.length; i++) {
                                                        var ver = {};
                                                        ver.ext = replaceInfo.versions[i].versionFileNameExtension;
                                                        ver.versionTag = replaceInfo.versions[i].versionTag;
                                                        ver.created = replaceInfo.versions[i].created;
                                                        ver.modified = replaceInfo.versions[i].modified;
                                                        ver.creator = replaceInfo.versions[i].creator;
                                                        folderList.children[replaced].versionList.push(ver);
                                                    }
                                                }
                                                DocTree.cacheFolderList.put(cacheKey, folderList);
                                                return (folderList.children[replaced]);
                                            }
                                        }
                                    }
                                }
                            }
                        }).then(
                            function (replacedFile) {
                                if (replacedFile && fileNode) {
                                    fileNode.data.ext = replacedFile.ext;
                                    fileNode.data.modified = replacedFile.modified;
                                    fileNode.data.version = replacedFile.version;
                                    fileNode.data.versionList = replacedFile.versionList;
                                    fileNode.renderTitle();
                                    fileNode.setStatus("ok");
                                }
                                dfd.resolve({files: [replacedFile], nodes: [fileNode]});
                            }
                            , function (errorData) {
                                DocTree.markNodeError(fileNode);
                                dfd.reject(errorData);
                            }
                        );
                    }
                    return dfd.promise();
                }
                , copyFolder: function (srcNode, frNode, toNode, mode) {
                    var dfd = $.Deferred();

                    //var toFolderNode = DocTree.isFolderNode(toNode)? toNode : toNode.parent;
                    var toFolderNode = toNode;
                    if (DocTree.isFileNode(toNode) || "after" == mode || "before" == mode) {
                        toFolderNode = toNode.parent;
                    }

                    if (!toFolderNode) {
                        dfd.reject();

                    } else if (!DocTree.isFolderNode(srcNode)) {
                        dfd.reject();

                    } else {
                        var newNode = null;
                        if (DocTree.isFolderNode(toNode)) {
                            newNode = toNode.addChildren(frNode);
                        } else {
                            //toNode = node.addNode(frNode, "after")
                            newNode = toNode.addNode(frNode, mode)
                        }
                        newNode.setActive();

//todo: copy to same parent, need to rename a "fn" to "fn (n)"
//                if (frNode.parent == toFolderNode) {
//                    //copy to another folder name
//
//                } else {}

                        DocTree.markNodePending(newNode);
                        var subFolderId = frNode.data.objectId;
                        var toFolderId = toFolderNode.data.objectId;
                        var toCacheKey = DocTree.getCacheKeyByNode(toFolderNode);
                        var frCacheKey = DocTree.getCacheKeyByNode(srcNode.parent);

                        Util.serviceCall({
                            service: Ecm.copyFolder
                            , param: {
                                subFolderId: subFolderId
                                , folderId: toFolderId
                                , objType: DocTree.getObjTyupe()
                                , objId: DocTree.getObjId()
                            }
                            , data: {}
                            , onSuccess: function (data) {
                                if (Validator.validateCopyFolderInfo(data)) {
                                    var copyFolderInfo = data;
                                    if (copyFolderInfo.originalFolderId == subFolderId && copyFolderInfo.newFolder.parentFolderId == toFolderId) {
                                        var frFolderList = DocTree.cacheFolderList.get(frCacheKey);
                                        var toFolderList = DocTree.cacheFolderList.get(toCacheKey);
                                        if (Validator.validateFolderList(frFolderList) && Validator.validateFolderList(toFolderList)) {
                                            var idx = DocTree.findFolderItemIdx(subFolderId, frFolderList);
                                            if (0 <= idx) {
                                                var folderData = DocTree.folderToSolrData(frFolderList.children[idx]);
                                                folderData.objectId = copyFolderInfo.newFolder.id;
                                                folderData.folderId = copyFolderInfo.newFolder.parentFolderId;
                                                folderData.modified = Util.goodValue(copyFolderInfo.newFolder.modified);
                                                folderData.modifier = Util.goodValue(copyFolderInfo.newFolder.modifier);
                                                toFolderList.children.push(folderData);
                                                toFolderList.totalChildren++;
                                                DocTree.cacheFolderList.put(toCacheKey, toFolderList);
                                                return folderData;
                                            }
                                        }
                                    }
                                }
                            }
                        }).then(
                            function (copyFolderInfo) {
                                DocTree._folderDataToNodeData(copyFolderInfo, newNode);
                                DocTree.markNodeOk(newNode);
                                newNode.setExpanded(false);
                                newNode.resetLazy();
                                newNode.renderTitle();
                                dfd.resolve(copyFolderInfo);
                            }
                            , function (errorData) {
                                DocTree.markNodeError(newNode);
                                dfd.reject();
                            }
                        );
                    }
                    return dfd.promise();

                }
                , copyFile: function (srcNode, frNode, toNode, mode) {
                    var dfd = $.Deferred();

                    //var toFolderNode = DocTree.isFolderNode(toNode)? toNode : toNode.parent;
                    var toFolderNode = toNode;
                    if (DocTree.isFileNode(toNode) || "after" == mode || "before" == mode) {
                        toFolderNode = toNode.parent;
                    }

                    if (!toFolderNode) {
                        dfd.reject();

                    } else if (!DocTree.isFileNode(srcNode)) {
                        dfd.reject();

                    } else {
                        var newNode = null;
                        if (DocTree.isFolderNode(toNode)) {
                            newNode = toNode.addChildren(frNode);
                        } else {
                            //toNode = node.addNode(frNode, "after")
                            newNode = toNode.addNode(frNode, mode)
                        }
                        newNode.setActive();

//todo: copy to same parent, need to rename a "fn" to "fn (n)"
//                if (frNode.parent == toFolderNode) {
//                    //copy to another folder name
//
//                } else {}

                        DocTree.markNodePending(newNode);
                        var fileId = frNode.data.objectId;
                        var toFolderId = toFolderNode.data.objectId;
                        var toCacheKey = DocTree.getCacheKeyByNode(toFolderNode);
                        var frCacheKey = DocTree.getCacheKeyByNode(srcNode.parent);

                        Util.serviceCall({
                            service: Ecm.copyFile
                            , param: {
                                objType: DocTree.getObjType()
                                , objId: DocTree.getObjId()
                            }
                            , data: {
                                id: fileId
                                , folderId: toFolderId
                            }
                            , onSuccess: function (data) {
                                if (Validator.validateCopyFileInfo(data)) {
                                    var copyFileInfo = data;
                                    if (copyFileInfo.originalId == fileId && copyFileInfo.newFile.folder.id == toFolderId) {
                                        var toFolderList = DocTree.cacheFolderList.get(toCacheKey);
                                        if (Validator.validateFolderList(toFolderList)) {
                                            var fileData = DocTree.fileToSolrData(copyFileInfo.newFile);
                                            toFolderList.children.push(fileData);
                                            toFolderList.totalChildren++;
                                            DocTree.cacheFolderList.put(toCacheKey, toFolderList);
                                            return fileData;
                                        }
                                    }
                                }
                            }
                        }).then(
                            function (copyFileInfo) {
                                DocTree._fileDataToNodeData(copyFileInfo, newNode);
                                DocTree.markNodeOk(newNode);
                                newNode.renderTitle();
                                dfd.resolve(copyFileInfo);
                            }
                            , function (errorData) {
                                DocTree.markNodeError(newNode);
                                dfd.reject();
                            }
                        );
                    }
                    return dfd.promise();
                }
                , _findNodesById: function (id, inNodes) {
                    var find = null;
                    for (var i = 0; i < inNodes.length; i++) {
                        if (inNodes[i].data.objectId == id) {
                            find = inNodes[i];
                            break;
                        }
                    }
                    return find;
                }
                , batchCopy: function (srcNodes, frNodes, toNode, mode) {
                    var dfd = $.Deferred();
                    if (Util.isArrayEmpty(srcNodes) || Util.isArrayEmpty(frNodes)) {
                        dfd.resolve();

                    } else {
                        var srcNodesToCopy = DocTree.getTopMostNodes(srcNodes);
                        var frNodesToCopy = [];
                        for (var i = 0; i < srcNodesToCopy.length; i++) {
                            var find = DocTree.Op._findNodesById(srcNodesToCopy[i].data.objectId, frNodes);
                            frNodesToCopy.push(find);
                        }

                        var requests = [];
                        for (var i = 0; i < srcNodesToCopy.length; i++) {
                            if (DocTree.isFolderNode(srcNodesToCopy[i])) {
                                requests.push(DocTree.Op.copyFolder(srcNodesToCopy[i], frNodesToCopy[i], toNode, mode));
                            } else if (DocTree.isFileNode(srcNodesToCopy[i])) {
                                requests.push(DocTree.Op.copyFile(srcNodesToCopy[i], frNodesToCopy[i], toNode, mode));
                            }
                        }

                        $q.all(requests).then(
                            function (data) {
                                if (DocTree.CLIPBOARD && DocTree.CLIPBOARD.src && DocTree.CLIPBOARD.batch) {
                                    DocTree.checkNodes(DocTree.CLIPBOARD.src, true);
                                }
                                dfd.resolve();
                            }
                            , function (data) {
                                dfd.reject();
                            }
                        );
                    }
                    return dfd.promise();
                }
                , moveFolder: function (frNode, toNode, mode) {
                    var dfd = $.Deferred();
                    var toFolderNode = toNode;
                    if (DocTree.isFileNode(toNode) || "after" == mode || "before" == mode) {
                        toFolderNode = toNode.parent;
                    }

                    if (!toFolderNode) {
                        dfd.reject();

                    } else if (!DocTree.isFolderNode(frNode)) {
                        dfd.reject();

                    } else if (frNode.parent == toFolderNode) {
                        frNode.moveTo(toNode, mode);
                        frNode.setActive();
                        dfd.resolve();

                    } else if ((frNode.parent == toFolderNode.parent) && ("before" == mode || "after" == mode)) {
                        frNode.moveTo(toNode, mode);
                        frNode.setActive();
                        dfd.resolve();

                    } else {
                        var toFolderId = toFolderNode.data.objectId;
                        var toCacheKey = DocTree.getCacheKeyByNode(toFolderNode);

                        var subFolderId = frNode.data.objectId;
                        var frFolderNode = frNode.parent;
                        var frFolderId = frFolderNode.data.objectId;
                        var frCacheKey = DocTree.getCacheKeyByNode(frFolderNode);

                        frNode.moveTo(toNode, mode);
                        frNode.setActive();

                        Util.serviceCall({
                            service: Ecm.moveFolder
                            , param: {
                                subFolderId: subFolderId
                                , folderId: toFolderId
                            }
                            , data: {}
                            , onSuccess: function (data) {
                                if (Validator.validateMoveFolderInfo(data)) {
                                    if (data.id == subFolderId) {
                                        var moveFolderInfo = data;

                                        var frFolderList = DocTree.cacheFolderList.get(frCacheKey);
                                        var toFolderList = DocTree.cacheFolderList.get(toCacheKey);
                                        if (Validator.validateFolderList(frFolderList) && Validator.validateFolderList(toFolderList)) {
                                            var idx = DocTree.findFolderItemIdx(subFolderId, frFolderList);
                                            if (0 <= idx) {
                                                toFolderList.children.push(frFolderList.children[idx]);
                                                toFolderList.totalChildren++;
                                                DocTree.cacheFolderList.put(toCacheKey, toFolderList);

                                                frFolderList.children.splice(idx, 1);
                                                frFolderList.totalChildren--;
                                                DocTree.cacheFolderList.put(frCacheKey, frFolderList);
                                            }
                                        }
                                        return moveFolderInfo;
                                    }
                                }
                            }
                        }).then(
                            function (moveFolderInfo) {
                                DocTree.markNodeOk(frNode);
                                dfd.resolve(moveFolderInfo);
                            }
                            , function (errorData) {
                                DocTree.markNodeError(frNode);
                                dfd.reject();
                            }
                        );
                    }
                    return dfd.promise();
                }
                , moveFile: function (frNode, toNode, mode) {
                    var dfd = $.Deferred();
                    var toFolderNode = toNode;
                    if (DocTree.isFileNode(toNode) || "after" == mode || "before" == mode) {
                        toFolderNode = toNode.parent;
                    }

                    if (!toFolderNode) {
                        dfd.reject();

                    } else if (!DocTree.isFileNode(frNode)) {
                        dfd.reject();

                    } else if (frNode.parent == toFolderNode) {
                        frNode.moveTo(toNode, mode);
                        frNode.setActive();
                        dfd.resolve();

                    } else {
                        var fileId = frNode.data.objectId;
                        var toFolderId = toFolderNode.data.objectId;
                        var toCacheKey = DocTree.getCacheKeyByNode(toFolderNode);

                        var frFolderNode = frNode.parent;
                        //var frFolderId = frFolderNode.data.objectId;
                        var frCacheKey = DocTree.getCacheKeyByNode(frFolderNode);

                        frNode.moveTo(toNode, mode);
                        frNode.setActive();

                        DocTree.markNodePending(frNode);

                        Util.serviceCall({
                            service: Ecm.moveFile
                            , param: {
                                objType: DocTree.getObjType()
                                , objId: DocTree.getObjId()
                            }
                            , data: {
                                "id": fileId
                                , "folderId": toFolderId
                            }
                            , onSuccess: function (data) {
                                if (Validator.validateMoveFileInfo(data)) {
                                    if (data.fileId == fileId) {
                                        var moveFileInfo = data;

                                        var frFolderList = DocTree.cacheFolderList.get(frCacheKey);
                                        var toFolderList = DocTree.cacheFolderList.get(toCacheKey);
                                        if (Validator.validateFolderList(frFolderList) && Validator.validateFolderList(toFolderList)) {
                                            var idx = DocTree.findFolderItemIdx(fileId, frFolderList);
                                            if (0 <= idx) {
                                                toFolderList.children.push(frFolderList.children[idx]);
                                                toFolderList.totalChildren++;
                                                DocTree.cacheFolderList.put(toCacheKey, toFolderList);

                                                frFolderList.children.splice(idx, 1);
                                                frFolderList.totalChildren--;
                                                DocTree.cacheFolderList.put(frCacheKey, frFolderList);
                                            }
                                        }
                                        return moveFileInfo;
                                    }
                                }
                            }
                        }).then(
                            function (moveFileInfo) {
                                DocTree.markNodeOk(frNode);
                                dfd.resolve(moveFileInfo);
                            }
                            , function (errorData) {
                                DocTree.markNodeError(frNode);
                                dfd.reject();
                            }
                        );
                    }

                    return dfd.promise();
                }
                , batchMove: function (frNodes, toNode, mode) {
                    var dfd = $.Deferred();
                    if (Util.isArrayEmpty(frNodes)) {
                        dfd.resolve();

                    } else {
                        var moveNodes = DocTree.getTopMostNodes(frNodes);
                        var requests = [];
                        for (var i = 0; i < moveNodes.length; i++) {
                            if (DocTree.isFolderNode(moveNodes[i])) {
                                requests.push(DocTree.Op.moveFolder(moveNodes[i], toNode, mode));
                            } else if (DocTree.isFileNode(moveNodes[i])) {
                                requests.push(DocTree.Op.moveFile(moveNodes[i], toNode, mode));
                            }
                        }

                        $q.all(requests).then(
                            function () {
                                if (DocTree.CLIPBOARD && DocTree.CLIPBOARD.data && DocTree.CLIPBOARD.batch) {
                                    DocTree.checkNodes(DocTree.CLIPBOARD.data, true);
                                }
                                dfd.resolve();
                            }
                            , function () {
                                dfd.reject();
                            }
                        );
                    }
                    return dfd.promise();
                }
                , deleteFolder: function (node) {
                    var dfd = $.Deferred();
                    if (!DocTree.isFolderNode(node)) {
                        dfd.reject();

                    } else {
                        var parent = node.parent;
                        if (!Validator.validateNode(parent)) {
                            dfd.reject();

                        } else {
                            var cacheKey = DocTree.getCacheKeyByNode(parent);
                            var refNode = node.getNextSibling() || node.getPrevSibling() || node.getParent();
                            node.remove();
                            if (refNode) {
                                refNode.setActive();
                            }

                            var folderId = node.data.objectId;
                            Util.serviceCall({
                                service: Ecm.deleteFolder
                                , param: {
                                    folderId: folderId
                                }
                                , data: {}
                                , onSuccess: function (data) {
                                    if (Validator.validateDeletedFolder(data)) {
                                        if (data.deletedFolderId == folderId) {
                                            var folderList = DocTree.cacheFolderList.get(cacheKey);
                                            if (Validator.validateFolderList(folderList)) {
                                                var deleted = DocTree.findFolderItemIdx(folderId, folderList);
                                                if (0 <= deleted) {
                                                    folderList.children.splice(deleted, 1);
                                                    folderList.totalChildren--;
                                                    DocTree.cacheFolderList.put(cacheKey, folderList);
                                                    return data.deletedFolderId;
                                                }
                                            }
                                        }
                                    }
                                }
                            }).then(
                                function (deletedFolderId) {
                                    dfd.resolve(deletedFolderId);
                                }
                                , function (errorData) {
                                    DocTree.markNodeError(node);
                                    dfd.reject();
                                }
                            );
                        }
                    }
                    return dfd.promise();
                }
                , deleteFile: function (node) {
                    var dfd = $.Deferred();
                    if (!DocTree.isFileNode(node)) {
                        dfd.reject();

                    } else {
                        var parent = node.parent;
                        if (!Validator.validateNode(parent)) {
                            dfd.reject();

                        } else {
                            var cacheKey = DocTree.getCacheKeyByNode(parent);
                            var refNode = node.getNextSibling() || node.getPrevSibling() || node.getParent();
                            node.remove();
                            if (refNode) {
                                refNode.setActive();
                            }

                            var fileId = node.data.objectId;
                            Util.serviceCall({
                                service: Ecm.deleteFile
                                , param: {
                                    fileId: fileId
                                }
                                , data: {}
                                , onSuccess: function (data) {
                                    if (Validator.validateDeletedFile(data)) {
                                        if (data.deletedFileId == fileId) {
                                            var folderList = DocTree.cacheFolderList.get(cacheKey);
                                            if (Validator.validateFolderList(folderList)) {
                                                var deleted = DocTree.findFolderItemIdx(fileId, folderList);
                                                if (0 <= deleted) {
                                                    folderList.children.splice(deleted, 1);
                                                    folderList.totalChildren--;
                                                    DocTree.cacheFolderList.put(cacheKey, folderList);
                                                    return data.deletedFileId;
                                                }
                                            }
                                        }
                                    }
                                }
                            }).then(
                                function (deletedFileId) {
                                    dfd.resolve(deletedFileId);
                                }
                                , function (errorData) {
                                    DocTree.markNodeError(node);
                                    dfd.reject();
                                }
                            );
                        }
                    }
                    return dfd.promise();
                }
                , batchRemove: function (nodes) {
                    var dfd = $.Deferred();
                    if (Util.isArrayEmpty(nodes)) {
                        dfd.resolve();

                    } else {
                        var removeNodes = DocTree.getTopMostNodes(nodes);
                        var requests = [];
                        for (var i = 0; i < removeNodes.length; i++) {
                            if (DocTree.isFolderNode(removeNodes[i])) {
                                requests.push(DocTree.Op.deleteFolder(removeNodes[i]));
                            } else if (DocTree.isFileNode(removeNodes[i])) {
                                requests.push(DocTree.Op.deleteFile(removeNodes[i]));
                            }
                        }

                        $q.all(requests).then(
                            function () {
                                dfd.resolve();
                            }
                            , function () {
                                dfd.reject();
                            }
                        );
                    }
                    return dfd.promise();
                }
                , renameFolder: function (node, folderName) {
                    var dfd = $.Deferred();
                    if (!DocTree.isFolderNode(node)) {
                        dfd.reject();

                    } else {
                        var parent = node.getParent();
                        if (!DocTree.isFolderNode(parent)) {
                            dfd.reject();

                        } else {
                            var folderId = node.data.objectId;
                            var cacheKey = DocTree.getCacheKeyByNode(parent);
                            Util.serviceCall({
                                service: Ecm.renameFolder
                                , param: {
                                    folderId: folderId
                                    , folderName: folderName
                                }
                                , data: {}
                                , onSuccess: function (data) {
                                    if (Validator.validateRenamedFolder(data)) {
                                        if (data.id == folderId) {
                                            var renamedInfo = data;
                                            var folderList = DocTree.cacheFolderList.get(cacheKey);
                                            var idx = DocTree.findFolderItemIdx(folderId, folderList);
                                            if (0 <= idx) {
                                                folderList.children[idx].name = Util.goodValue(renamedInfo.name);
                                                DocTree.cacheFolderList.put(cacheKey, folderList);
                                                return renamedInfo;
                                            }
                                        }
                                    }
                                }
                            }).then(
                                function (renamedInfo) {
                                    DocTree.markNodeOk(node);
                                    dfd.resolve(renamedInfo);
                                }
                                , function (errorData) {
                                    DocTree.markNodeError(node);
                                    dfd.reject();
                                }
                            );
                        }
                    }
                    return dfd.promise();
                }
                , renameFile: function (node, fileName) {
                    var dfd = $.Deferred();
                    if (!DocTree.isFileNode(node)) {
                        dfd.reject();

                    } else {
                        var parent = node.getParent();
                        if (!DocTree.isFolderNode(parent)) {
                            dfd.reject();

                        } else {
                            var cacheKey = DocTree.getCacheKeyByNode(parent);
                            var fileId = node.data.objectId;

                            Util.serviceCall({
                                service: Ecm.renameFile
                                , param: {
                                    fileId: fileId,
                                    name: fileName
                                }
                                , data: {}
                                , onSuccess: function (data) {
                                    if (Validator.validateRenamedFile(data)) {
                                        if (data.fileId == fileId) {
                                            var renamedInfo = data;
                                            var folderList = DocTree.cacheFolderList.get(cacheKey);
                                            var idx = DocTree.findFolderItemIdx(fileId, folderList);
                                            if (0 <= idx) {
                                                folderList.children[idx].name = Util.goodValue(renamedInfo.fileName);
                                                DocTree.cacheFolderList.put(cacheKey, folderList);
                                                return renamedInfo;
                                            }
                                        }
                                    }
                                }
                            }).then(
                                function (renamedInfo) {
                                    node.title = renamedInfo.fileName;
                                    node.tooltip = node.title;
                                    DocTree.markNodeOk(node);
                                    dfd.resolve(renamedInfo);
                                }
                                , function (errorData) {
                                    DocTree.markNodeError(node);
                                    dfd.reject();
                                }
                            );
                        }
                    }
                    return dfd.promise();
                }
                , setActiveVersion: function (fileNode, version) {
                    var dfd = $.Deferred();
                    if (!DocTree.isFileNode(fileNode)) {
                        dfd.reject();

                    } else {
                        var parent = fileNode.getParent();
                        if (!DocTree.isFolderNode(parent)) {
                            dfd.reject();

                        } else {
                            DocTree.markNodePending(fileNode);
                            var fileId = fileNode.data.objectId;
                            var cacheKey = DocTree.getCacheKeyByNode(parent);

                            Util.serviceCall({
                                service: Ecm.setActiveVersion
                                , param: {
                                    fileId: fileId
                                    , version: version
                                }
                                , data: {}
                                , onSuccess: function (data) {
                                    if (Validator.validateActiveVersion(data)) {
                                        if (data.fileId == fileId) {
                                            var activeVersion = data;
                                            var folderList = DocTree.cacheFolderList.get(cacheKey);
                                            if (Validator.validateFolderList(folderList)) {
                                                var idx = DocTree.findFolderItemIdx(fileId, folderList);
                                                if (0 <= idx) {
                                                    folderList.children[idx].activeVersionTag = Util.goodValue(activeVersion.activeVersionTag);
                                                    folderList.children[idx].ext = Util.goodValue(activeVersion.fileActiveVersionNameExtension);
                                                    DocTree.cacheFolderList.put(cacheKey, folderList);
                                                    return activeVersion;
                                                }
                                            }
                                        }
                                    }
                                }
                            }).then(
                                function (activeVersion) {
                                    fileNode.data.activeVertionTag = Util.goodValue(activeVersion);
                                    fileNode.data.version = Util.goodValue(activeVersion.activeVersionTag);
                                    fileNode.data.ext = Util.goodValue(activeVersion.fileActiveVersionNameExtension);
                                    DocTree.markNodeOk(fileNode);
                                    fileNode.renderTitle();
                                    dfd.resolve();
                                }
                                , function (errorData) {
                                    DocTree.markNodeError(fileNode);
                                    dfd.reject();
                                }
                            );
                        }
                    }
                    return dfd.promise();
                }

            } // end Op


            //
            // Build a table template:
            //
            // '<table id="treeDoc" class="table table-striped th-sortable table-hover">'
            //+ '<thead>'
            //+ '<tr>'
            //+ '<th id="selectDoc" width2="6%"><input type="checkbox"/></th>'
            //+ '<th id="docID" width2="4%" >ID</th>'
            //+ '<th id="docTitle" width="35%">Title</th>'
            //+ '<th id="docType" width="12%">Type</th>'
            //+ '<th id="docCreated" width="10%">Created</th>'
            //+ '<th id="docAuthor" width="16%">Author</th>'
            //+ '<th id="docVersion" width="6%">Version</th>'
            //+ '<th id="docStatus" width="8%">Status</th>'
            //+ '</tr>'
            //+ '</thead>'
            //+ '<tbody>'
            //+ '<tr>'
            //+ '<td headers="selectDoc"></td>'
            //+ '<td headers="docID"></td>'
            //+ '<td headers="docTitle"></td>'
            //+ '<td headers="docType"></td>'
            //+ '<td headers="docCreated"></td>'
            //+ '<td headers="docAuthor"></td>'
            //+ '<td headers="docVersion"></td>'
            //+ '<td headers="docStatus"></td>'
            //+ '</tr>'
            //+ '</tbody>'
            //+ '</table>'
            , makeTable: function (element, columnDefs) {
                var jqTable = $("<table/>")
                    .addClass("table table-striped th-sortable table-hover")
                    .appendTo($(element));
                var jqThead = $("<thead/>").appendTo(jqTable);
                var jqTrHead = $("<tr/>")
                    .appendTo(jqThead);
                var jqTbody = $("<tbody/>").appendTo(jqTable);
                var jqTrBody = $("<tr/>")
                    .appendTo(jqTbody);

                var jqTh, jqTd;
                _.each(columnDefs, function (columnDef) {
                    var name = columnDef.name;
                    var field = Util.goodValue(columnDef.field, name);
                    var cellTemplate = columnDef.cellTemplate;
                    var displayName = $translate.instant(columnDef.displayName);
                    var headTemplate = columnDef.headTemplate;
                    if ("checkbox" == name) {
                        headTemplate = "<input type='checkbox'/>";
                    }
                    var width = Util.goodValue(columnDef.width, "10%");
                    jqTh = $("<th/>")
                        .attr("width", width)
                        //.text(displayName)
                        .appendTo(jqTrHead);
                    if (headTemplate) {
                        $(headTemplate).appendTo(jqTh);
                    } else {
                        jqTh.text(displayName);
                    }

                    jqTd = $("<td/>")
                        .appendTo(jqTrBody);
                });

                return jqTable;
            }
            , makeUploadDocForm: function (jqTree) {
                this.jqFormUploadDoc = $("<form/>")
                    .attr("id", "formUploadDoc")
                    .attr("style", "display:none;")
                    .appendTo(jqTree);
                this.jqFileInput = $("<input/>")
                    .attr("type", "file")
                    .attr("id", "file")
                    .attr("name", "files[]")
                    .attr("multiple", "")
                    .appendTo(this.jqFormUploadDoc);

                this.jqFileInput.on("change", function (e) {
                    DocTree.jqFormUploadDoc.submit();
                });
                this.jqFormUploadDoc.submit(function (e) {
                    DocTree.onSubmitFormUploadFile(e, this);
                });
            }
            , makeDownloadDocForm: function (jqTree) {
                this.jqFormDownloadDoc = $("<form/>")
                    .attr("id", "formDownloadDoc")
                    .attr("action", "#")
                    .attr("style", "display:none;")
                    .appendTo(jqTree);
            }

            , uploadForm: function (node, formType) {
                DocTree.uploadSetting = {
                    uploadToFolderNode: node
                    , uploadFileType: formType
                    , deferUploadFile: $q.defer()
                };

                var url = DocTree.doUploadForm(formType, node.data.objectId);
                if (url) {
                    Ui.dlgOpenWindow(url, "", 1060, $(window).height() - 30, DocTree.onLoadingFrevvoForm);
                }
            }
            , editForm: function (node) {
                DocTree.uploadSetting = {
                    uploadToFolderNode: node
                    , uploadFileType: node.data.type
                    , deferUploadFile: $q.defer()
                };

                var url = DocTree.doUploadForm(node.data.type, node.parent.data.objectId, '', true);
                if (url) {
                    Ui.dlgOpenWindow(url, "", 1060, $(window).height() - 30, DocTree.onLoadingFrevvoForm);
                }
            }
            , uploadFile: function () {
                DocTree.jqFileInput.attr("multiple", '');
                DocTree.jqFileInput.click();
            }
            , replaceFile: function () {
                DocTree.jqFileInput.removeAttr("multiple");
                DocTree.jqFileInput.click();
            }

            , _addFileNode: function (folderNode, name, type) {
                var fileNode = folderNode.addChildren({
                    "title": name,
                    "name": name,
                    "ext": "",
                    "type": type
                });
                DocTree.markNodePending(fileNode);
                return fileNode;
            }
            , _addingFileNodes: function (folderNode, names, type) {
                var deferred = $.Deferred();
                DocTree.expandNode(folderNode).done(function () {
                    var fileNodes = [];
                    for (var i = 0; i < names.length; i++) {
                        var fileNode = DocTree._addFileNode(folderNode, names[i], type);
                        fileNodes.push(fileNode);
                    }
                    deferred.resolve(fileNodes);
                });
                return deferred.promise();
            }

            , onFailedAddingFileNode: function () {
                var z = 1;
            }
            , onLoadingFrevvoForm: function () {
                if (!DocTree.uploadSetting) {
                    return;
                }

                var folderNode = DocTree.uploadSetting.uploadToFolderNode;
                var fileType = DocTree.uploadSetting.uploadFileType;
                var names = [fileType + " form"];
                var promiseAddNode = DocTree._addingFileNodes(folderNode, names, fileType);

                setTimeout(function () {
                    var promiseRetrieveLatest = DocTree.Op.retrieveFolderList(folderNode
                        , function (folderListLatest) {
                            //jwu: simulate frevvo data for testing, please keep
                            //var mock = {};
                            //var i = folderListLatest.children.length - 1;
                            //mock.objectId = folderListLatest.children[i].objectId + 1001;
                            //mock.objectType = folderListLatest.children[i].objectType;
                            //mock.created = folderListLatest.children[i].created;
                            //mock.creator = folderListLatest.children[i].creator;
                            //mock.modified = folderListLatest.children[i].modified;
                            //mock.modifier = folderListLatest.children[i].modifier;
                            //mock.name = "Mock";
                            //mock.type = fileType;
                            //mock.status = folderListLatest.children[i].status;
                            //mock.category = folderListLatest.children[i].category;
                            //mock.version = "1.1";
                            //mock.versionList = [{versionTag: "1.0"}, {versionTag: "1.1"}];
                            //folderListLatest.children.push(mock);
                            //folderListLatest.totalChildren++;
                            //mock = {};
                            //i = folderListLatest.children.length - 1;
                            //mock.objectId = folderListLatest.children[i].objectId + 1002;
                            //mock.objectType = folderListLatest.children[i].objectType;
                            //mock.created = folderListLatest.children[i].created;
                            //mock.creator = folderListLatest.children[i].creator;
                            //mock.modified = folderListLatest.children[i].modified;
                            //mock.modifier = folderListLatest.children[i].modifier;
                            //mock.name = "Mock2";
                            //mock.type = fileType;
                            //mock.status = folderListLatest.children[i].status;
                            //mock.category = folderListLatest.children[i].category;
                            //mock.version = "1.2";
                            //mock.versionList = [{versionTag: "1.0"}, {versionTag: "1.1"}, {versionTag: "1.2"}];
                            //folderListLatest.children.push(mock);
                            //folderListLatest.totalChildren++;

                            var uploadedFiles = null;
                            if (Validator.validateFolderList(folderListLatest)) {
                                var newChildren = [];
                                for (var i = folderListLatest.children.length - 1; 0 <= i; i--) {
                                    if (folderListLatest.children[i].type == fileType) {
                                        if (!DocTree.findChildNodeById(folderNode, folderListLatest.children[i].objectId)) { //not found in the tree node, must be newly created
                                            newChildren.push(folderListLatest.children[i]);
                                        }
                                    }
                                }
                                if (!Util.isArrayEmpty(newChildren)) {
                                    //var cacheKey = DocTree.getCacheKey(folderId, pageId);
                                    var cacheKey = DocTree.getCacheKeyByNode(folderNode);
                                    var folderList = DocTree.cacheFolderList.get(cacheKey);
                                    if (Validator.validateFolderList(folderList)) {
                                        uploadedFiles = [];
                                        for (var i = 0; i < newChildren.length; i++) {
                                            var uploadedFile = DocTree.fileToSolrData(newChildren[i]);
                                            uploadedFiles.push(uploadedFile);
                                            //folderList.children.push(uploadedFile);
                                            //folderList.totalChildren++;
                                        }
                                    } //end if validateFolderList
                                } //end if (!Util.isArrayEmpty(newChildren))
                            }
                            //DocTree.refreshTree();
                            return uploadedFiles;
                        }
                    );

                    $.when(promiseRetrieveLatest, promiseAddNode).done(function (uploadedFiles, fileNodes) {
                        if (!Util.isArrayEmpty(uploadedFiles) && Validator.validateFancyTreeNodes(fileNodes)) {
                            for (var i = 0; i < uploadedFiles.length; i++) {
                                var uploadedFile = uploadedFiles[i];
                                var emptyNode = null;
                                if (0 == i) {
                                    emptyNode = DocTree._findEmptyNode(folderNode, fileType);
                                    if (emptyNode) {
                                        DocTree._fileDataToNodeData(uploadedFile, emptyNode);
                                        emptyNode.renderTitle();
                                        emptyNode.setStatus("ok");
                                    }
                                }


                                if (!emptyNode) {
                                    var fileNode = folderNode.addChildren({"title": Util.goodValue(uploadedFile.name)});
                                    DocTree._fileDataToNodeData(uploadedFile, fileNode);
                                    fileNode.renderTitle();
                                }
                            }
                        } else { // most likely the Frevvo form was canceled or closed, remove previously created nodes (identified with spinner icon)
                            for (var i = 0; i < fileNodes.length; i++) {
                                folderNode.removeChild(fileNodes[i]);
                            }
                        }
                    });
                }, 5000);

            }
            , _folderDataToNodeData: function (folderData, nodeData) {
                if (folderData && nodeData) {
                    if (!nodeData.data) {
                        nodeData.data = {};
                    }
                    nodeData.key = Util.goodValue(folderData.objectId, 0);
                    nodeData.title = Util.goodValue(folderData.name);
                    nodeData.tooltip = Util.goodValue(folderData.name);
                    nodeData.data.name = Util.goodValue(folderData.name);
                    nodeData.data.objectId = Util.goodValue(folderData.objectId, 0);
                    nodeData.data.objectType = Util.goodValue(folderData.objectType);
                    nodeData.data.created = Util.goodValue(folderData.created);
                    nodeData.data.creator = Util.goodValue(folderData.creator);
                    nodeData.data.modified = Util.goodValue(folderData.modified);
                    nodeData.data.status = Util.goodValue(folderData.status);

                }
                return nodeData;
            }
            , _fileDataToNodeData: function (fileData, nodeData) {
                if (fileData && nodeData) {
                    if (!nodeData.data) {
                        nodeData.data = {};
                    }
                    nodeData.key = Util.goodValue(fileData.objectId, 0);
                    nodeData.title = Util.goodValue(fileData.name);
                    nodeData.tooltip = Util.goodValue(fileData.name);
                    nodeData.data.name = Util.goodValue(fileData.name);
                    nodeData.data.ext = Util.goodValue(fileData.ext);
                    nodeData.data.type = Util.goodValue(fileData.type);
                    nodeData.data.objectId = Util.goodValue(fileData.objectId, 0);
                    nodeData.data.objectType = Util.goodValue(fileData.objectType);
                    nodeData.data.created = Util.goodValue(fileData.created);
                    nodeData.data.creator = Util.goodValue(fileData.creator);
                    nodeData.data.modified = Util.goodValue(fileData.modified);
                    nodeData.data.status = Util.goodValue(fileData.status);
                    nodeData.data.category = Util.goodValue(fileData.category);
                    nodeData.data.version = Util.goodValue(fileData.version);
                    nodeData.data.lock = Util.goodValue(fileData.lock);
                    nodeData.data.modifier = Util.goodValue(fileData.modifier);
                    if (Util.isArray(fileData.versionList)) {
                        nodeData.data.versionList = [];
                        for (var i = 0; i < fileData.versionList.length; i++) {
                            var version = {};
                            version.versionTag = Util.goodValue(fileData.versionList[i].versionTag);
                            version.created = Util.goodValue(fileData.versionList[i].created);
                            version.creator = Util.goodValue(fileData.versionList[i].creator);
                            version.modified = Util.goodValue(fileData.versionList[i].modified);
                            version.modifier = Util.goodValue(fileData.versionList[i].modifier);
                            nodeData.data.versionList.push(version);

                            if (!Util.isEmpty(version.versionTag) && version.versionTag == nodeData.data.version) {
                                nodeData.data.versionDate = Util.goodValue(version.created);
                                nodeData.data.versionUser = Util.goodValue(version.creator);
                            }
                        }
                    }

                    _.each(DocTree.CustomData.getItems(), function (item) {
                        nodeData.data[item] = Util.goodValue(fileData[item]);
                    });
                }
                return nodeData;
            }
            , _findEmptyNode: function (folderNode, fileType) {
                var node = null;
                for (var i = folderNode.children.length - 1; 0 <= i; i--) {
                    if (fileType == folderNode.children[i].data.type) {
                        if (Util.isEmpty(folderNode.children[i].data.objectId)) {
                            node = folderNode.children[i];
                            break;
                        }
                    }
                }
                return node;
            }
            , onSubmitFormUploadFile: function (event, ctrl) {
                event.preventDefault();

                if (DocTree.uploadSetting) {
                    var files = DocTree.jqFileInput[0].files;
                    DocTree.uploadSetting.deferSelectFile.resolve(files);
                }
            }
            //, doSubmitFormUploadFile: function (files, doRefresh) {
            , doSubmitFormUploadFile: function (files) {
                if (!DocTree.uploadSetting) {
                    return Util.errorPromise("upload file error");
                }

                //var refresh = Util.goodValue(doRefresh, true);

                var dfd = $.Deferred();
                var folderNode = DocTree.uploadSetting.uploadToFolderNode;
                var fileType = DocTree.uploadSetting.uploadFileType;
                var fd = new FormData();
                fd.append("parentObjectType", DocTree.getObjType());
                fd.append("parentObjectId", DocTree.getObjId());
                if (!DocTree.isTopNode(folderNode)) {
                    fd.append("folderId", folderNode.data.objectId);
                }
                fd.append("fileType", fileType);
                fd.append("category", "Document");
                var names = [];
                for (var i = 0; i < files.length; i++) {
                    names.push(files[i].name);
                    fd.append("files[]", files[i]);
                    if (0 == i && !DocTree.uploadSetting.uploadFileNew) {    //for replace operation, only take one file
                        break;
                    }
                }

                var cacheKey = DocTree.getCacheKeyByNode(folderNode);
                if (DocTree.uploadSetting.uploadFileNew) {
                    DocTree.Op.uploadFiles(fd, folderNode, names, fileType)
                        .then(function (data) {
                                _.each(data.nodes, function (node) {
                                    DocTree.markNodeOk(node)
                                });
                                dfd.resolve(data);
                            },
                            function (error) {
                                dfd.reject(error);
                            }
                        );
                } else {
                    var replaceNode = DocTree.uploadSetting.replaceFileNode;
                    DocTree.Op.replaceFile(fd, replaceNode, names[0])
                        .then(function (data) {
                                _.each(data.nodes, function (node) {
                                    DocTree.markNodeOk(node)
                                });
                                dfd.resolve(data);
                            },
                            function (error) {
                                dfd.reject(error);
                            }
                        );
                }
                return dfd.promise();
            }
            , _matchFileNode: function (type, name, fileNodes) {
                var fileNode = null;
                for (var i = 0; i < fileNodes.length; i++) {
                    var nameNode = fileNodes[i].data.name;
                    if (nameNode == name && fileNodes[i].data.type == type) {
                        fileNode = fileNodes[i];
                        break;
                    }
                }
                return fileNode;
            }
            , checkNodes: function (nodes, check) {
                if (!Util.isArrayEmpty(nodes)) {
                    for (var i = 0; i < nodes.length; i++) {
                        nodes[i].setSelected(check);
                    }
                }
            }

            , getSelectedNodes: function () {
                var nodes = null;
                if (DocTree.tree) {
                    nodes = DocTree.tree.getSelectedNodes();
                }
                return nodes;
            }
            , getEffectiveNodes: function () {
                var nodes = null;
                if (this.tree) {
                    var selNodes = this.tree.getSelectedNodes();
                    var node = this.tree.getActiveNode();
                    nodes = (!Util.isArrayEmpty(selNodes)) ? selNodes : ((!Util.isEmpty(node)) ? [node] : []);
                }
                return nodes;
            }

            , editSetting: {
                isEditing: false        //node enters into editing mode
            }

            , getDocumentTypeDisplayLabel: function (documentType) { // looks up the display label for the given document type (afdp-1249)
                var labelMappings = DocTree.fileTypes;
                if (documentType && Util.isArray(labelMappings)) {
                    documentType = documentType.trim().toLowerCase();
                    for (var i = 0; i < labelMappings.length; i++) {
                        if (labelMappings[i]["type"] && labelMappings[i]["type"].trim().toLowerCase() == documentType) {
                            return labelMappings[i]["label"];
                        }
                    }
                }
                return documentType; // label could not be found, the raw document type will be displayed
            }


            , onViewChangedParent: function (objType, objId) {
                DocTree.switchObject(objType, objId);
            }

            , onChangeVersion: function (event) {
                var node = DocTree.tree.getActiveNode();
                if (node) {
                    var parent = node.parent;
                    if (parent) {
                        var cacheKey = DocTree.getCacheKeyByNode(parent);

                        var verSelected = Ui.getSelectValue($(this));
                        var verCurrent = Util.goodValue(node.data.version, "0");
                        if (verSelected != verCurrent) {
                            if (verSelected < verCurrent) {
                                Ui.dlgConfirm($translate.instant("common.directive.docTree.confirmVersion")
                                    , function (result) {
                                        if (result) {
                                            DocTree.Op.setActiveVersion(node, verSelected);
                                        } else {
                                            node.renderTitle();
                                        }
                                    }
                                );
                            } else {
                                DocTree.Op.setActiveVersion(node, verSelected);
                            }
                        }
                    } //end if (parent)
                }
            }

            //
            // This prevent going to detail page when user checking version drop down too fast
            //
            , onDblClickVersion: function (event, data) {
                event.stopPropagation();
            }

            , onClickBtnChkAllDocument: function (event, ctrl) {
                var checked = $(ctrl).is(":checked");
                DocTree.tree.visit(function (node) {
                    node.setSelected(checked);
                });
            }


            , Key: {
                KEY_SEPARATOR: "/"
                , TYPE_ID_SEPARATOR: "."
                , NODE_TYPE_PART_PREV_PAGE: "prevPage"
                , NODE_TYPE_PART_NEXT_PAGE: "nextPage"
                , NODE_TYPE_PART_PAGE: "p"


                //keyParts format: [{type: "t", id: "123"}, ....]
                //Integer ID works as well: [{type: "t", id: 123}, ....]
                , makeKey: function (keyParts) {
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
            }

            , Config: {
                DEFAULT_MAX_ROWS: 1000
                , DEFAULT_SORT_BY: "name"
                , DEFAULT_SORT_DIRECTION: "ASC"
                , _setting: {
                    maxRows: 16
                    , sortBy: null
                    , sortDirection: null
                }
                , getSetting: function () {
                    return this._setting;
                }
                , getMaxRows: function () {
                    return Util.goodValue(this._setting.maxRows, this.DEFAULT_MAX_ROWS);
                }
                , getSortBy: function () {
                    return Util.goodValue(this._setting.sortBy, this.DEFAULT_SORT_BY);
                }
                , getSortDirection: function () {
                    return Util.goodValue(this._setting.sortDirection, this.DEFAULT_SORT_DIRECTION);
                }
            }


            , findFolderItemIdx: function (objectId, folderList) {
                var found = -1;
                if (Validator.validateFolderList(folderList)) {
                    for (var i = 0; i < folderList.children.length; i++) {
                        if (Util.goodValue(folderList.children[i].objectId) == objectId) {
                            found = i;
                            break;
                        }
                    }
                }
                return found;
            }

            , fileToSolrData: function (fileData) {
                var solrData = {};
                solrData.objectType = "file";
                if (!Util.isEmpty(fileData.fileId, 0)) {
                    solrData.objectId = fileData.fileId;
                } else if (!Util.isEmpty(fileData.objectId, 0)) {
                    solrData.objectId = fileData.objectId;
                }
                solrData.created = Util.goodValue(fileData.created);
                solrData.creator = Util.goodValue(fileData.creator);
                solrData.modified = Util.goodValue(fileData.modified);
                solrData.modifier = Util.goodValue(fileData.modifier);

                if (!Util.isEmpty(fileData.fileName)) {
                    solrData.name = fileData.fileName;
                } else if (!Util.isEmpty(fileData.name)) {
                    solrData.name = fileData.name;
                }

                if (!Util.isEmpty(fileData.fileActiveVersionNameExtension)) {
                    solrData.ext = fileData.fileActiveVersionNameExtension;
                } else if (!Util.isEmpty(fileData.ext)) {
                    solrData.ext = fileData.ext;
                }

                if (!Util.isEmpty(fileData.fileType)) {
                    solrData.type = fileData.fileType;
                } else if (!Util.isEmpty(fileData.type)) {
                    solrData.type = fileData.type;
                }
                solrData.status = Util.goodValue(fileData.status);
                solrData.category = Util.goodValue(fileData.category);
                if (!Util.isEmpty(fileData.activeVersionTag)) {
                    solrData.version = fileData.activeVersionTag;
                } else if (!Util.isEmpty(fileData.version)) {
                    solrData.version = fileData.version;
                }

                if (Util.isArray(fileData.versions)) {
                    solrData.versionList = [];
                    for (var i = 0; i < fileData.versions.length; i++) {
                        var version = {};
                        version.versionTag = Util.goodValue(fileData.versions[i].versionTag);
                        version.created = Util.goodValue(fileData.versions[i].created);
                        version.creator = Util.goodValue(fileData.versions[i].creator);
                        version.modified = Util.goodValue(fileData.versions[i].modified);
                        version.modifier = Util.goodValue(fileData.versions[i].modifier);
                        solrData.versionList.push(version);
                    }
                }
                if (Util.isArray(fileData.versionList)) {
                    solrData.versionList = [];
                    for (var i = 0; i < fileData.versionList.length; i++) {
                        var version = {};
                        version.versionTag = Util.goodValue(fileData.versionList[i].versionTag);
                        version.created = Util.goodValue(fileData.versionList[i].created);
                        version.creator = Util.goodValue(fileData.versionList[i].creator);
                        version.modified = Util.goodValue(fileData.versionList[i].modified);
                        version.modifier = Util.goodValue(fileData.versionList[i].modifier);
                        solrData.versionList.push(version);
                    }
                }
                return solrData;
            }
            , folderToSolrData: function (folderData) {
                var solrData = {};
                solrData.objectType = "folder";
                if (!Util.isEmpty(folderData.id, 0)) {
                    solrData.objectId = folderData.id;
                } else if (!Util.isEmpty(folderData.objectId, 0)) {
                    solrData.objectId = folderData.objectId;
                }
                solrData.created = Util.goodValue(folderData.created);
                solrData.creator = Util.goodValue(folderData.creator);
                solrData.modified = Util.goodValue(folderData.modified);
                solrData.modifier = Util.goodValue(folderData.modifier);
                solrData.name = Util.goodValue(folderData.name);
                solrData.ext = Util.goodValue(folderData.ext);
                solrData.status = Util.goodValue(folderData.status);
                if (!Util.isEmpty(folderData.parentFolderId, 0)) {
                    solrData.folderId = Util.goodValue(folderData.parentFolderId, 0);
                } else if (!Util.isEmpty(folderData.folderId, 0)) {
                    solrData.folderId = Util.goodValue(folderData.folderId, 0);
                }
                return solrData;
            }

            , onFileTypesChanged: function (fileTypes) {
                DocTree.fileTypes = fileTypes;
                var jqTreeBody = DocTree.jqTree.find("tbody");
                DocTree.Menu.useContextMenu(jqTreeBody, false);
            }
        };  //end DocTree

        var DialogDnd = {
            OpTypes: {
                OP_NOOP: ""
                , OP_REPLACE: "Replace"
                , OP_UPLOAD_TO_PARENT: "UploadToParent"
                , OP_UPLOAD_TO_FOLDER: "UploadToFolder"
            }

            , openModal: function (nodeType, fileTypes, onClickOk) {
                var params = {
                    nodeType: nodeType
                    , fileTypes: fileTypes
                };

                var modalInstance = $modal.open({
                    templateUrl: "directives/doc-tree/doc-tree.dnd.dialog.html"
                    , controller: 'directives.DocTreeDndDialogController'
                    , resolve: {
                        OpTypes: function () {
                            return DialogDnd.OpTypes
                        }
                        , params: function () {
                            return params;
                        }
                    }
                });
                modalInstance.result.then(function (result) {
                    if (result) {
                        onClickOk(result);
                    }
                });
            }
        };

        var Ui = {
            dlgModal: function ($s, onClickBtnPrimary, onClickBtnDefault) {
                var a = $s.html();

                if (onClickBtnPrimary) {
                    $s.find("button.btn-primary").unbind("click").on("click", function (e) {
                        onClickBtnPrimary(e, this);
                        $s.modal("hide");
                    });
                }
                if (onClickBtnDefault) {
                    $s.find("button.btn-default").unbind("click").on("click", function (e) {
                        onClickBtnDefault(e, this);
                    });
                }

                $s.modal("show");
            }
            , dlgConfirm: function (msg, callback) {
                bootbox.confirm(msg, callback);
            }

            , dlgOpenWindow: function (url, title, w, h, onDone) {
                var params = {};
                params.frevvoFormUrl = url;
                params.title = title;
                params.w = w;
                params.h = h;
                params.onDone = onDone;

                var modalInstance = $modal.open({
                    templateUrl: "directives/doc-tree/doc-tree.frevvo.dialog.html",
                    controller: ['$rootScope', '$scope', '$interval', 'params', function ($rootScope, $scope, $interval, params) {
                        $scope.title = params.title;
                        $scope.height = (params.h + 200) + "px";
                        $scope.width = (params.w + 200) + "px";
                        $scope.widthDialogBox = (params.w + 20) + "px";
                        $scope.frevvoFormUrl = params.frevvoFormUrl;
                        $scope.iframeLoaded = function(){
                            startCheckFrevvoSubmission();
                            startInitFrevvoMessaging();
                        };

                        var initFrevvoMessagingPromise;
                        function startInitFrevvoMessaging() {
                            stopInitFrevvoMessaging();
                            initFrevvoMessagingPromise = $interval(initFrevvoMessaging, 250);
                        }

                        function stopInitFrevvoMessaging() {
                            $interval.cancel(initFrevvoMessagingPromise);
                        }

                        function initFrevvoMessaging() {
                            var frevvoIframe = getFrevvoIframe();
                            if (!Util.isEmpty(frevvoIframe)) {
                                stopInitFrevvoMessaging();
                                if (Util.isEmpty($rootScope.frevvoMessaging)) {
                                    $rootScope.frevvoMessaging = {};
                                    $rootScope.frevvoMessaging.receiver = frevvoIframe;
                                    $rootScope.frevvoMessaging.send = function send(message) {
                                        if (!Util.isEmpty($rootScope.frevvoMessaging.receiver)) {
                                            $rootScope.frevvoMessaging.receiver.postMessage(message, '*');
                                        }
                                    }
                                    $rootScope.frevvoMessaging.receive = function receive(e) {
                                        if (!Util.isEmpty(e) && !Util.isEmpty(e.data) && !Util.isEmpty(e.data.source) && e.data.source == "frevvo" && !Util.isEmpty(e.data.action)) {
                                            // Do actions sent from Frevvo
                                            if (e.data.action == "open-user-picker") {
                                                openUserPicker(e.data);
                                            }
                                        }
                                    }

                                    window.addEventListener("message", $rootScope.frevvoMessaging.receive);
                                } else {
                                    $rootScope.frevvoMessaging.receiver = frevvoIframe;
                                }
                            }
                        }

                        function getFrevvoIframe() {
                            if (!Util.isEmpty(document) &&
                                !Util.isEmpty(document.getElementById('frevvoFormIframe')) &&
                                !Util.isEmpty(document.getElementById('frevvoFormIframe').contentWindow) &&
                                !Util.isEmpty(document.getElementById('frevvoFormIframe').contentWindow.document) &&
                                !Util.isEmpty(document.getElementById('frevvoFormIframe').contentWindow.document.getElementsByTagName('iframe')) &&
                                 document.getElementById('frevvoFormIframe').contentWindow.document.getElementsByTagName('iframe').length > 0 &&
                                !Util.isEmpty(document.getElementById('frevvoFormIframe').contentWindow.document.getElementsByTagName('iframe')[0]) &&
                                !Util.isEmpty(document.getElementById('frevvoFormIframe').contentWindow.document.getElementsByTagName('iframe')[0].contentWindow)
                            ) {
                                return document.getElementById('frevvoFormIframe').contentWindow.document.getElementsByTagName('iframe')[0].contentWindow;
                            }

                            return null;
                        }

                        function openUserPicker(data) {
                            var params = {};

                            params.header = $translate.instant("common.directive.coreParticipants.modal.dialogUserPicker.header");
                            params.filter = '"Object Type": USER';
                            params.config = Util.goodMapValue(DocTree.treeConfig, "dialogUserPicker");

                            var modalInstanceUserPicker = $modal.open({
                                templateUrl: "modules/frevvo/views/frevvo-participants-picker-modal.client.view.html",
                                controller: ['$scope', '$modalInstance', 'params', function ($scope, $modalInstance, params) {
                                    $scope.modalInstance = $modalInstance;
                                    $scope.header = params.header;
                                    $scope.filter = params.filter;
                                    $scope.config = params.config;
                                }],
                                animation: true,
                                size: 'lg',
                                backdrop: 'static',
                                resolve: {
                                    params: function () {
                                        return params;
                                    }
                                }
                            });
                            modalInstanceUserPicker.result.then(function (selected) {
                                if (!Util.isEmpty(selected)) {
                                    var message = {};
                                    message.source = "arkcase";
                                    message.data = selected;
                                    message.action = "fill-user-picker-data";
                                    message.elementId = data.elementId;

                                    $rootScope.frevvoMessaging.send(message);
                                }
                            });
                        }

                        var promise;
                        function startCheckFrevvoSubmission() {
                            stopCheckFrevvoSubmission();
                            promise = $interval(checkFrevvoSubmission, 250);
                        }

                        function stopCheckFrevvoSubmission() {
                            $interval.cancel(promise);
                        }

                        $scope.close = function (callback) {
                            stopCheckFrevvoSubmission();
                            modalInstance.close(false);
                            if (callback) {
                                callback();
                            }
                        }

                        function checkFrevvoSubmission() {
                            if (!Util.isEmpty($rootScope.frevvoMessaging) &&
                                !Util.isEmpty($rootScope.frevvoMessaging.receiver) &&
                                !Util.isEmpty($rootScope.frevvoMessaging.receiver.document) &&
                                !Util.isEmpty($rootScope.frevvoMessaging.receiver.document.body) &&
                                !Util.isEmpty($rootScope.frevvoMessaging.receiver.document.body.getElementsByTagName('div')) &&
                                $rootScope.frevvoMessaging.receiver.document.body.getElementsByTagName('div').length > 0 &&
                                !Util.isEmpty($rootScope.frevvoMessaging.receiver.document.body.getElementsByTagName('div')[0])  &&
                                !Util.isEmpty($rootScope.frevvoMessaging.receiver.document.body.getElementsByTagName('div')[0].innerHTML.trim()) &&
                                !Util.isEmpty($rootScope.frevvoMessaging.receiver.document.body.getElementsByTagName('div')[0].innerHTML.trim()) &&
                                angular.equals("Closing ...", $rootScope.frevvoMessaging.receiver.document.body.getElementsByTagName('div')[0].innerHTML.trim())
                            ) {
                                $scope.close(params.onDone);
                            }
                        }
                    }],
                    animation: true,
                    size: 'lg',
                    backdrop: 'static',
                    resolve: {
                        params: function () {
                            return params;
                        }
                    }
                });
            }

            , getValue: function ($s) {
                return $s.val();
            }
            , setValue: function ($s, value) {
                if (null == value) {
                    value = "";
                }
                $s.val(value);
            }
            , getText: function ($s) {
                return $s.text();
            }
            , setText: function ($s, value) {
                if (null == value) {
                    value = "";
                }
                $s.text(value);
            }

            //
            //i is zero based index to indicate which text node to use
            //i not specified -- return all text nodes as whole
            //i = -1          -- return last text node
            //
            , getTextNodeText: function ($s, i) {
                var textNodes = $s.contents().filter(function () {
                    return this.nodeType == 3;
                });

                if (0 >= textNodes.length) {
                    return "";
                } else if (undefined === i) {
                    return textNodes.text();
                } else if (-1 === i) {
                    i = textNodes.length - 1;
                }

                return textNodes[i].nodeValue;
            }
            , setTextNodeText: function ($s, value, i) {
                if (null == value) {
                    value = "";
                }

                var textNodes = $s.contents().filter(function () {
                    return this.nodeType == 3;
                });

                if (0 >= textNodes.length) {
                    return;
                } else if (undefined === i) {
                    i = 0;
                } else if (-1 === i) {
                    i = textNodes.length - 1;
                }

                textNodes[i].nodeValue = value;
            }
            , getSelectValue: function ($s) {
                var v = $s.find("option:selected").val();
                if ("placeholder" == v) {
                    v = "";
                }
                return v;
            }
            , getSelectedText: function ($s) {
                var v = $s.find("option:selected").text();
                if ("placeholder" == v) {
                    v = "";
                }
                return v;
            }
            , setSelectValue: function ($s, value) {
                $s.find("option").filter(function () {
                    return jQuery(this).val() == value;
                    //}).prop('selected', true); //for jQuery v1.6+
                }).attr('selected', true);
            }
            , appendSelect: function ($s, key, val) {
                $s.append($("<option></option>")
                    .attr("value", key)
                    .text(val));
            }

            //ignore first option, which is instruction
            , getSelectValueIgnoreFirst: function ($s) {
                var selected = Ui.getSelectValue($s);
                var firstOpt = $s.find("option:first").val();
                return (selected == firstOpt) ? null : selected;
            }
            , getSelectTextIgnoreFirst: function ($s) {
                var selected = Ui.getSelectedText($s);
                var firstOpt = $s.find("option:first").val();
                return (selected == firstOpt) ? null : selected;
            }
            , getSelectValues: function ($s) {
                var mv = [];
                $s.find("option:selected").each(function (i, selected) {
                    mv[i] = $(selected).val();
                });
                return mv;
            }
            , getSelectValuesAsString: function ($s, sep) {
                return $s.find("option:selected").map(function () {
                    return this.value;
                }).get().join(sep);
            }

            , getPlaceHolderInput: function ($s) {
                var v;
                v = $s.val();
                v = ($s.attr('placeholder') !== v) ? v : "";
                return v;
            }
            , setPlaceHolderInput: function ($s, val) {
                //$s.val(Util.goodValue(val, ""));
                $s.trigger('focus').val(Util.goodValue(val, "")).trigger('blur');
            }

            , changePlaceHolderSelect: function ($s) {
                if ($s.val() == "placeholder") {
                    $s.addClass("placeholder");
                } else {
                    $s.removeClass("placeholder");
                }
            }
            , isChecked: function ($s) {
                return $s.is(":checked");
            }
            , setChecked: function ($s, value) {
                if ("true" == value || true == value) {
                    $s.attr("checked", "checked");
                    //$s.prop("checked", true); //for v1.6+
                } else {
                    $s.removeAttr("checked");
                    //$s.prop("checked", false); //for v1.6+
                }
            }
            , getHtml: function ($s) {
                return $s.html();
            }
            , setHtml: function ($s, value) {
                $s.html(value);
            }

            // Setting value directly to a date picker causes date picker popup initially visible.
            // Use setValueDatePicker() to solve the problem.
            , setValueDatePicker: function ($s, val) {
                $s.attr("style", "display:none");
                Ui.setPlaceHolderInput($s, val);
                Ui.show($s, true);
            }

            , setEnable: function ($s, value) {
                if (value == "true" || value == true) {
                    $s.removeAttr("disabled");
                    //$s.prop("disabled", false); //for v1.6+
                } else {
                    $s.attr("disabled", "disabled");
                    //$s.prop("disabled", true); //for v1.6+
                }
            }
            , isEnable: function ($s) {
                var d = $s.attr("disabled");
                return !d;
            }
            , removeClick: function ($s) {
                $s.unbind("click")
                    .click(function (event) {
                        return event.preventDefault();
                    });
            }
            , show: function ($s, show) {
                if (show == "true" || show == true) {
                    $s.show();
                } else {
                    $s.hide();
                }
            }
            , showParent: function ($s, show) {
                var p = $s.parent();
                if (p)
                    if ("true" == show || true == show) {
                        p.show();
                    } else {
                        p.hide();
                    }
            }

            //work around for hiding options in select list in IE
            , showOption: function ($s, show) {
                if (show) {
                    $s.each(function (index, val) {
                        if (navigator.appName == 'Microsoft Internet Explorer') {
                            if (this.nodeName.toUpperCase() === 'OPTION') {
                                var span = $(this).parent();
                                var opt = this;
                                if ($(this).parent().is('span')) {
                                    $(opt).show();
                                    $(span).replaceWith(opt);
                                }
                            }
                        } else {
                            $(this).show(); //all other browsers use standard .show()
                        }
                    });
                } else {
                    $s.each(function (index, val) {
                        if ($(this).is('option') && (!$(this).parent().is('span')))
                            $(this).wrap((navigator.appName == 'Microsoft Internet Explorer') ? '<span>' : null).hide();
                    });
                }
            }
            , isVisible: function ($s) {
                return $s.is(":visible");
            }
            , empty: function ($s) {
                $s.empty();
            }
        }; //end Ui

        var Validator = {
            validateNodes: function (data) {
                if (!Util.isArray(data)) {
                    return false;
                }
                for (var i = 0; i < data.length; i++) {
                    if (!this.validateNode(data[i])) {
                        return false;
                    }
                }
                return true;
            }
            , validateNode: function (data) {
                if (!this.validateFancyTreeNode(data)) {
                    return false;
                }
                if (Util.isEmpty(data.data.objectId)) {
                    return false;
                }
                return true;
            }
            , validateFancyTreeNodes: function (data) {
                if (!Util.isArray(data)) {
                    return false;
                }
                for (var i = 0; i < data.length; i++) {
                    if (!this.validateFancyTreeNode(data[i])) {
                        return false;
                    }
                }
                return true;
            }
            , validateFancyTreeNode: function (data) {
                if (Util.isEmpty(data)) {
                    return false;
                }
                if (Util.isEmpty(data.tree)) {
                    return false;
                }
                if (Util.isEmpty(data.data)) {
                    return false;
                }
                if (Util.isEmpty(data.key)) {
                    return false;
                }
                return true;
            }

            , validateFolderList: function (data) {
                if (Util.isEmpty(data)) {
                    return false;
                }
                if (!Util.isArray(data.children)) {
                    return false;
                }
                return true;
            }
            , validateCreateInfo: function (data) {
                if (Util.isEmpty(data)) {
                    return false;
                }
                if (Util.isEmpty(data.id)) {
                    return false;
                }
                if (0 == data.id) {
                    return false;
                }
                if (Util.isEmpty(data.parentFolder)) {
                    return false;
                }
                return true;
            }
            , validateDeletedFolder: function (data) {
                if (Util.isEmpty(data)) {
                    return false;
                }
                if (Util.isEmpty(data.deletedFolderId)) {
                    return false;
                }
                return true;
            }
            , validateDeletedFile: function (data) {
                if (Util.isEmpty(data)) {
                    return false;
                }
                if (Util.isEmpty(data.deletedFileId)) {
                    return false;
                }
                return true;
            }
            , validateRenamedFolder: function (data) {
                if (Util.isEmpty(data)) {
                    return false;
                }
                if (Util.isEmpty(data.id)) {
                    return false;
                }
                if (Util.isEmpty(data.name)) {
                    return false;
                }
                return true;
            }
            , validateRenamedFile: function (data) {
                if (Util.isEmpty(data)) {
                    return false;
                }
                if (Util.isEmpty(data.fileId)) {
                    return false;
                }
                if (Util.isEmpty(data.fileName)) {
                    return false;
                }
                return true;
            }
            , validateMoveFileInfo: function (data) {
                if (Util.isEmpty(data)) {
                    return false;
                }
                if (Util.isEmpty(data.fileId)) {
                    return false;
                }
                if (Util.isEmpty(data.folder)) {
                    return false;
                }
                if (Util.isEmpty(data.folder.id)) {
                    return false;
                }
                return true;
            }
            , validateCopyFileInfo: function (data) {
                if (Util.isEmpty(data)) {
                    return false;
                }
                if (Util.isEmpty(data.originalId)) {
                    return false;
                }
                if (Util.isEmpty(data.newFile)) {
                    return false;
                }
                if (Util.isEmpty(data.newFile.fileId)) {
                    return false;
                }
                if (Util.isEmpty(data.newFile.folder)) {
                    return false;
                }
                if (Util.isEmpty(data.newFile.folder.id)) {
                    return false;
                }
                return true;
            }
            , validateMoveFolderInfo: function (data) {
                if (Util.isEmpty(data)) {
                    return false;
                }
                if (Util.isEmpty(data.id)) {
                    return false;
                }
                return true;
            }
            , validateCopyFolderInfo: function (data) {
                if (Util.isEmpty(data)) {
                    return false;
                }
                if (Util.isEmpty(data.originalFolderId)) {
                    return false;
                }
                if (Util.isEmpty(data.newFolder)) {
                    return false;
                }
                if (Util.isEmpty(data.newFolder.id)) {
                    return false;
                }
                if (Util.isEmpty(data.newFolder.parentFolderId)) {
                    return false;
                }
                return true;
            }
            , validateUploadInfo: function (data) {
                if (Util.isArrayEmpty(data)) {
                    return false;
                }
                for (var i = 0; i < data.length; i++) {
                    if (!Validator.validateUploadInfoItem(data[i])) {
                        return false;
                    }
                }
                return true;
            }
            , validateReplaceInfo: function (data) {
                if (Util.isEmpty(data)) {
                    return false;
                }
                if (Util.isEmpty(data.fileId)) {
                    return false;
                }
                return true;
            }
            , validateUploadInfoItem: function (data) {
                if (Util.isEmpty(data)) {
                    return false;
                }
                if (Util.isEmpty(data.fileId)) {
                    return false;
                }
                if (Util.isEmpty(data.folder)) {
                    return false;
                }
                if (!Util.isArray(data.versions)) {
                    return false;
                }
                if (!Util.isArray(data.tags)) {
                    return false;
                }
                return true;
            }
            , validateActiveVersion: function (data) {
                if (Util.isEmpty(data)) {
                    return false;
                }
                if (Util.isEmpty(data.fileId)) {
                    return false;
                }
                if (Util.isEmpty(data.activeVersionTag)) {
                    return false;
                }
                if (Util.isEmpty(data.fileActiveVersionNameExtension)) {
                    return false;
                }
                return true;
            }

        };

        //todo: move Validator inside DocTree
        DocTree.Validator = Validator;

        return {
            restrict: 'E'
            , template: ''
            , scope: {
                treeConfig: '='
                , objectInfo: '='
                , treeControl: '='
                , objectType: '='
                , objectId: '='
                //, fileTypes: '='
                //, correspondenceForms: '='
                , uploadForm: '&'
                , onInitTree: '&'
                , readOnly: '@'
            }

            , link: function (scope, element, attrs) {
                Ui.scope = scope;

                DocTree.scope = scope;
                DocTree.jqTree = null;
                DocTree.setObjType(scope.objectType);
                DocTree.setObjId(scope.objectId);
                DocTree.treeConfig = {};
                DocTree.objectInfo = null;
                DocTree.doUploadForm = ("undefined" != typeof attrs.uploadForm) ? scope.uploadForm() : (function () {
                }); //if not defined, do nothing
                DocTree.readOnly = ("true" === attrs.readOnly);

                scope.treeControl = {
                    getSelectedNodes: DocTree.getSelectedNodes
                    , refreshTree: DocTree.refreshTree
                    , refreshNode: DocTree.refreshNode
                    , updateNodeData: DocTree.updateNodeData
                    , addCommandHandler: function (args) {
                        DocTree.Command.addHandler(args);
                    }
                    , addColumnRenderer: function (args) {
                        DocTree.CustomData.addData(args.model);
                        DocTree.Column.addRenderer(args.name, args.renderer);
                    }
                };

                DocTree.Command.setHandlers(DocTree.Command.getCoreHandlers());
                DocTree.Column.setRenderers(DocTree.Column.getCoreRenderers());


                // With scope.treeControl set above, according to Angular documentation, the host controller $scope.treeControl should be defined.
                // Yet, it is not always the case. It works sometimes and does not the other times. As a work around, scope.treeControl is
                // passed as argument of onInitTree(). Parent controller need to assign it to its $scope.treeControl
                if ("undefined" != typeof attrs.onInitTree) {
                    scope.onInitTree()(scope.treeControl);
                }

                var promiseCommon = ConfigService.getModuleConfig("common").then(function (moduleConfig) {
                    var treeConfig = Util.goodMapValue(moduleConfig, "docTree", {});
                    DocTree.treeConfig = _.merge(treeConfig, DocTree.treeConfig);

                    var extensions = Util.goodMapValue(treeConfig, "extensions", []);
                    for (var i = 0; i < extensions.length; i++) {
                        if ($injector.has(extensions[i])) {
                            var extService = $injector.get(extensions[i]);
                            var extHandlers = extService.getCommandHandlers(DocTree);
                            var extRenderers = extService.getColumnRenderers(DocTree);
                            DocTree.Command.addHandlers(extHandlers);
                            DocTree.Column.addRenderers(extRenderers);
                        }
                    }

                    return moduleConfig;
                });

                scope.$watchGroup(['treeConfig', 'objectInfo', 'treeConfig.fileTypes'], function (newValues, oldValues, scope) {

                    promiseCommon.then(function () {

                        DocTree.objectInfo = newValues[1];

                        _.merge(DocTree.treeConfig, newValues[0]);
                        if (!Util.isEmpty(DocTree.jqTree)) {
                            DocTree.jqTree.empty();
                        }

                        if (Util.goodMapValue(DocTree.treeConfig, "columnDefs")) {
                            DocTree.jqTree = DocTree.makeTable(element, DocTree.treeConfig.columnDefs);
                            DocTree.tree = DocTree.create(DocTree.jqTree);
                            DocTree.makeDownloadDocForm(DocTree.jqTree);
                            DocTree.makeUploadDocForm(DocTree.jqTree);

                            var fileTypes = Util.goodMapValue(DocTree.treeConfig, "fileTypes", []).concat(Util.goodMapValue(DocTree.treeConfig, "formTypes", []));
                            DocTree.fileTypes = fileTypes;
                            var jqTreeBody = DocTree.jqTree.find("tbody");
                            DocTree.Menu.useContextMenu(jqTreeBody);

                            var extensions = Util.goodMapValue(DocTree.treeConfig, "extensions", []);
                            for (var i = 0; i < extensions.length; i++) {
                                if ($injector.has(extensions[i])) {
                                    var extService = $injector.get(extensions[i]);
                                    if (extService.onConfigUpdated) {
                                        extService.onConfigUpdated(DocTree);
                                    }
                                }
                            }
                        }

                    });
                });
            }
        };

    }
]);

angular.module('directives').controller('directives.DocTreeDndDialogController', ['$scope', '$modalInstance'
        , 'UtilService', 'OpTypes', 'params'
        , function ($scope, $modalInstance, Util, OpTypes, params) {

            $scope.result = {op: OpTypes.OP_NOOP, fileType: null};

            $scope.fileTypes = params.fileTypes;
            if ("folder" == params.nodeType) {
                $scope.result.op = OpTypes.OP_UPLOAD_TO_FOLDER;
            }

            $scope.disableOk = function () {
                if (OpTypes.OP_UPLOAD_TO_FOLDER == $scope.result.op || OpTypes.OP_UPLOAD_TO_PARENT == $scope.result.op) {
                    return Util.isEmpty($scope.result.fileType);
                } else if (OpTypes.OP_REPLACE == $scope.result.op) {
                    return false;
                } else {
                    return true;
                }
            };

            $scope.showRadioButtons = function () {
                return OpTypes.OP_UPLOAD_TO_FOLDER != $scope.result.op;
            };

            $scope.showSelFileTypes = function () {
                return OpTypes.OP_UPLOAD_TO_FOLDER == $scope.result.op || OpTypes.OP_UPLOAD_TO_PARENT == $scope.result.op;
            };

            $scope.onClickCancel = function () {
                $modalInstance.close(false);
            };
            $scope.onClickOk = function () {
                $modalInstance.close($scope.result);
            };

        }
    ]
);



