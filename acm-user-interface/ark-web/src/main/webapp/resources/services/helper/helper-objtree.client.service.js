'use strict';

/**
 * @ngdoc service
 * @name services:Helper.ObjectTreeService
 *
 * @description
 *
 * {@link https://github.com/Armedia/ACM3/blob/develop/acm-user-interface/ark-web/src/main/webapp/resources/services/helper/object-objtree.client.service.js services/helper/object-objtree.client.service.js}

 * Helper.ObjectTreeService has help functions to use 'object-tree' directive
 */
angular.module('services').factory('Helper.ObjectTreeService', ['$resource', '$translate', 'UtilService',
    function ($resource, $translate, Util) {
        var Service = {
            Tree: function (arg) {
                this.scope = arg.scope;
                this.nodeId = arg.nodeId;
                this.getTreeData = arg.getTreeData;
                this.getNodeData = arg.getNodeData;
                this.makeTreeNode = arg.makeTreeNode;
                this.firstLoad = false;
            }
        };

        Service.Tree.prototype = {
            onLoad: function (start, n, sort, filters, arg) {
                var that = this;
                if (that.firstLoad && that.nodeId) {
                    that.scope.treeData = null;
                }

                that.getTreeData(start, n, sort, filters).then(
                    function (treeData) {
                        if (that.firstLoad) {
                            if (that.nodeId) {
                                if (that.scope.treeData) {            //It must be set by CallTasksService.getTaskInfo(), only 1 items in docs[] is expected
                                    var found = _.find(treeData.docs, {nodeId: that.scope.treeData.docs[0].nodeId});
                                    //var found = that.findByNodeId(treeData.docs, that.scope.treeData.docs[0].nodeId);
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

                if (that.firstLoad && that.nodeId) {
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
                                var found = _.find(that.scope.treeData.docs, {nodeId: that.nodeId});
                                //var found = that.findByNodeId(that.scope.treeData.docs, that.nodeId);
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
                                var found = _.find(that.scope.treeData.docs, {nodeId: that.nodeId});
                                //var found = that.findByNodeId(that.scope.treeData.docs, that.nodeId);
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
            //, findByNodeId: function (docs, nodeId) {
            //    var found = null;
            //    for (var i = 0; i < docs.length; i++) {
            //        if (docs[i].nodeId == nodeId) {
            //            found = docs[i];
            //            break;
            //        }
            //    }
            //    return found;
            //}
        };


        /**
         * @ngdoc method
         * @name validateSolrData
         * @methodOf services:Helper.ObjectTreeService
         *
         * @description
         * Validate data of query from SOLR
         *
         * @param {Helper} data  Data to be validated
         *
         * @returns {Boolean} Return true if data is valid
         */
        Service.validateSolrData = function (data) {
            if (!data) {
                return false;
            }
            if (Util.isEmpty(data.responseHeader) || Util.isEmpty(data.response)) {
                return false;
            }
            if (Util.isEmpty(data.responseHeader.status)) {
                return false;
            }
//            if (0 != responseHeader.status) {
//                return false;
//            }
            if (Util.isEmpty(data.responseHeader.params)) {
                return false;
            }
            if (Util.isEmpty(data.responseHeader.params.q)) {
                return false;
            }

            if (Util.isEmpty(data.response.numFound) || Util.isEmpty(data.response.start)) {
                return false;
            }
            if (!Util.isArray(data.response.docs)) {
                return false;
            }
            return true;
        };

        return Service;
    }
]);
