'use strict';

/**
 * @ngdoc service
 * @name services:Helper.ObjectTreeService
 *
 * @description
 *
 * {@link https://github.com/Armedia/ACM3/blob/develop/acm-user-interface/ark-web/src/main/webapp/resources/services/helper/helper-objtree.client.service.js services/helper/helper-objtree.client.service.js}

 * Helper.ObjectTreeService provide help to use 'object-tree' directive
 */
angular.module('services').factory('Helper.ObjectTreeService', ['$resource', '$translate', 'UtilService',
    function ($resource, $translate, Util) {
        var Service = {

            /**
             * @ngdoc method
             * @name Tree Constructor
             * @methodOf services:Helper.ObjectTreeService
             *
             * @param {Object} arg Map arguments
             * @param {Object} arg.scope Angular $scope
             * @param {number} arg.nodeId Node II of initial selected tree node. If null or zero, first tree node is selected
             * @param {Object} arg.getNodeData Promise object of a function that query list of tree nodes.
             * @param {Object} arg.getNodeData Promise object of a function that query data of a specified node
             * @param {Function} arg.makeTreeNode Function to make tree node from object data
             *
             * @description
             * Helper.ObjectTreeService.Tree is to help a typical usage of 'object-tree' directive.
             * Assumption: 'object-tree' directive must set 'tree-data' with data model $scope.treeData and
             * 'tree-control' with $scope.treeControl.
             */
            Tree: function (arg) {
                this.scope = arg.scope;
                this.nodeId = arg.nodeId;
                this.getTreeData = arg.getTreeData;
                this.getNodeData = arg.getNodeData;
                this.makeTreeNode = arg.makeTreeNode;
                this.firstLoad = true;
            }
        };

        Service.Tree.prototype = {
            /**
             * @ngdoc method
             * @name onLoad
             * @methodOf services:Helper.ObjectTreeService
             *
             * @param {Number} start Start index of tree data
             * @param {Number} n Max number of tree data records
             * @param {String} sort Sort parameter for tree data query
             * @param {String} filters Filter parameter for tree data query
             *
             * @description
             * A callback function to respond object tree events to load tree data of a given page
             */
            onLoad: function (start, n, sort, filters) {
                var that = this;
                if (that.firstLoad && that.nodeId) {
                    that.scope.treeData = null;
                }

                that.getTreeData(start, n, sort, filters).then(
                    function (treeData) {
                        if (that.firstLoad) {
                            if (that.nodeId) {
                                if (that.scope.treeData) {            //It must be set by CallTasksService.getTaskInfo(), only 1 items in docs[] is expected
                                    var found = that.findByNodeId(treeData.docs, that.scope.treeData.docs[0].nodeId);
                                    if (!found) {
                                        var clone = _.clone(treeData.docs);
                                        clone.unshift(that.scope.treeData.docs[0]);
                                        treeData.docs = clone;
                                    }
                                    that.firstLoad = false;
                                }


                            } else {
                                if (0 < treeData.docs.length) {
                                    var selectNode = treeData.docs[0];
                                    that.scope.treeControl.select({
                                        pageStart: start
                                        , nodeType: selectNode.nodeType
                                        , nodeId: selectNode.nodeId
                                    });
                                }
                                that.firstLoad = false;
                            }
                        }

                        that.scope.treeData = treeData;
                        return treeData;
                    }
                );


                if (that.firstLoad && Util.goodPositive(that.nodeId)) {
                    that.getNodeData(that.nodeId).then(
                        function (objectInfo) {
                            var treeNode = that.makeTreeNode(objectInfo);
                            that.scope.treeControl.select({
                                pageStart: start
                                , nodeType: treeNode.nodeType
                                , nodeId: treeNode.nodeId
                            });

                            var treeData = {docs: [], total: 0};
                            if (that.scope.treeData) {            //It must be set by CallTasksService.queryTasksTreeData()
                                var found = that.findByNodeId(that.scope.treeData.docs, that.nodeId);
                                if (!found) {
                                    treeData.docs = _.clone(that.scope.treeData.docs);
                                    treeData.total = that.scope.treeData.total;
                                    treeData.docs.unshift(treeNode);
                                } else {
                                    treeData = that.scope.treeData; //use what is there already
                                }
                                that.firstLoad = false;

                            } else {
                                treeData.total = 1;
                                treeData.docs.unshift(treeNode);
                            }

                            that.scope.treeData = treeData;
                            return objectInfo;
                        }
                        , function (errorData) {
                            that.scope.treeControl.select({
                                pageStart: start
                                , nodeType: "ERROR"
                                , nodeId: that.nodeId
                            });


                            var treeData = {docs: [], total: 0};
                            var errorNode = {
                                nodeId: that.nodeId
                                , nodeType: "ERROR"
                                , nodeTitle: $translate.instant("common.directive.objectTree.errorNode.title")
                                , nodeToolTip: $translate.instant("common.directive.objectTree.errorNode.toolTip")
                            };
                            if (that.scope.treeData) {            //It must be set by CallTasksService.queryTasksTreeData()
                                var found = that.findByNodeId(that.scope.treeData.docs, that.nodeId);
                                if (!found) {
                                    treeData.docs = _.clone(that.scope.treeData.docs);
                                    treeData.total = that.scope.treeData.total;
                                    treeData.docs.unshift(errorNode);
                                } else {
                                    treeData = that.scope.treeData; //use what is there already
                                }
                                that.firstLoad = false;

                            } else {
                                treeData.total = 1;
                                treeData.docs.unshift(errorNode);
                            }

                            that.scope.treeData = treeData;
                            return errorData;
                        }
                    );
                }
            }

            , findByNodeId: function (docs, nodeId) {
                //return _.find(docs, {nodeId: nodeId});   //somehow, _.find() does not always work
                var found = null;
                for (var i = 0; i < docs.length; i++) {
                    if (docs[i].nodeId == nodeId) {
                        found = docs[i];
                        break;
                    }
                }
                return found;
            }
        };

        return Service;
    }
]);
