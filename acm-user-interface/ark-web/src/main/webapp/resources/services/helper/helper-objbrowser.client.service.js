'use strict';

/**
 * @ngdoc service
 * @name services:Helper.ObjectBrowserService
 *
 * @description
 *
 * {@link https://github.com/Armedia/ACM3/blob/develop/acm-user-interface/ark-web/src/main/webapp/resources/services/helper/helper-objbrowser.client.service.js services/helper/helper-objbrowser.client.service.js}

 * Helper.ObjectBrowserService provide help for common functions for an object page. It includes navigation (or tree) part and content part.
 * Tree part uses 'object-tree' directive. Content part includes component links and data loading.
 */
angular.module('services').factory('Helper.ObjectBrowserService', ['$resource', '$translate'
    , 'StoreService', 'UtilService', 'ConfigService', 'ServCommService'
    , function ($resource, $translate, Store, Util, ConfigService, ServCommService) {

        var Service = {
            VariableNames: {
                CURRENT_OBJECT_SETTING: "CurrentObjectSetting"
            }

            /**
             * @ngdoc method
             * @name Tree Constructor
             * @methodOf services:Helper.ObjectBrowserService
             *
             * @param {Object} arg Map arguments
             * @param {Object} arg.scope Angular $scope
             * @param {number} arg.nodeId Node II of initial selected tree node. If null or zero, first tree node is selected
             * @param {Object} arg.getNodeData Promise object of a function that query list of tree nodes.
             * @param {Object} arg.getNodeData Promise object of a function that query data of a specified node
             * @param {Function} arg.makeTreeNode Function to make tree node from object data
             *
             * @description
             * Helper.ObjectBrowserService.Tree is to help a typical usage of 'object-tree' directive.
             * Assumption: 'object-tree' directive must set 'tree-data' with data model $scope.treeData and
             * 'tree-control' with $scope.treeControl.
             */
            , Tree: function (arg) {
                var that = this;
                that.scope = arg.scope;
                that.state = arg.state;
                that.stateParams = arg.stateParams;
                that.moduleId = arg.moduleId;
                that.resetTreeData = arg.resetTreeData;
                that.getTreeData = arg.getTreeData;
                that.getNodeData = arg.getNodeData;
                that.makeTreeNode = arg.makeTreeNode;
                that.firstLoad = true;

                ConfigService.getModuleConfig(that.moduleId).then(function (config) {
                    that.scope.treeConfig = config.tree;
                    that.scope.componentsConfig = config.components;
                    return config;
                });

                that.nodeId = Service.getCurrentObjectId();

                that.scope.onReset = function () {
                    that.onReset();
                };

                that.scope.onLoad = function (start, n, sort, filters, query) {
                    that.onLoad(start, n, sort, filters, query);
                };

                that.scope.onSelect = function (selectedObject) {
                    that.onSelect(selectedObject);
                };

                that.scope.$on('refresh-content', function (e, selectedObject) {
                    console.log("helper.Tree: refresh-content");
                });
            }



            /**
             * @ngdoc method
             * @name Content Constructor
             * @methodOf services:Helper.ObjectBrowserService
             *
             * @param {Object} arg Map arguments
             * @param {Object} arg.scope Angular $scope
             *
             * @description
             * Helper.ObjectContentService.Content is to provide common functions needed for default behavior of
             * content part of an object browser.
             * Assumption: 'ui-grid' directive must set 'gridOptions' with data model $scope.gridOptions and
             * following data may also be used:
             *   $scope.gridApi
             *   $scope.start
             *   $scope.pageSize
             *   $scope.sort
             *   $scope.filters
             *   $scope.userFullNames
             */
            , Content: function (arg) {
                var that = this;
                that.scope = arg.scope;
                that.state = arg.state;
                that.stateParams = arg.stateParams;
                that.moduleId = arg.moduleId;

                that.resetObjectInfo = arg.resetObjectInfo;
                that.getObjectInfo = arg.getObjectInfo;
                that.updateObjectInfo = arg.updateObjectInfo;
                that.initComponentLinks = arg.initComponentLinks;
                that.selectComponentLinks = arg.selectComponentLinks;
                that.getObjectIdFromInfo = (arg.getObjectIdFromInfo) ? arg.getObjectIdFromInfo : function (objectInfo) {
                    return Util.goodMapValue(objectInfo, "id");
                };
                that.getObjectTypeFromInfo = (arg.getObjectTypeFromInfo) ? arg.getObjectTypeFromInfo : function (objectInfo) {
                    return "";
                };


                var promiseGetModuleConfig = ConfigService.getModuleConfig(that.moduleId).then(function (config) {
                    that.scope.config = config;
                    that.scope.componentLinks = that.initComponentLinks(config);
                    that.scope.activeLinkId = "main";
                    that.scope.linksShown = Util.goodValue(config.initialLinksShown, true);
                    return config;
                });

                that.scope.getActive = function (linkId) {
                    return (that.scope.activeLinkId == linkId) ? "active" : ""
                };

                that.scope.onClickComponentLink = function (linkId) {
                    that.scope.activeLinkId = linkId;
                    //var objectId = that.stateParams.id;
                    //var objectType = that.stateParams.type;
                    var objectId = that.getObjectIdFromInfo(that.scope.objectInfo);
                    var objectType = that.getObjectTypeFromInfo(that.scope.objectInfo);
                    Service.updateObjectSetting(that.moduleId, linkId, objectId, objectType);
                    var params = {id: objectId};
                    if (!Util.isEmpty(objectType)) {
                        params.type = objectType;
                    }
                    that.state.go(arg.moduleId + "." + linkId, params);
                };

                that.scope.linksShown = true;
                that.scope.toggleShowLinks = function () {
                    that.scope.linksShown = !that.scope.linksShown;
                };


                that.scope.$on('main-component-started', function (e) {
                    that.scope.activeLinkId = "main";
                    Service.updateObjectSetting(that.moduleId, "main"); //don't update objectId/Type; only set linkId = "main"
                });

                that.scope.$on('report-object-refreshed', function (e, objectId) {
                    that.resetObjectInfo();

                    that.getObjectInfo(objectId).then(
                        function (objectInfo) {
                            that.scope.objectInfo = objectInfo;
                            that.scope.$broadcast('object-refreshed', objectInfo);
                            return objectInfo;
                        }
                        , function (error) {
                            that.scope.objectInfo = null;
                            //todo: display error
                            return error;
                        }
                    );
                });

                that.scope.$on('report-object-updated', function (e, objectInfo) {
                    that.updateObjectInfo(objectInfo);
                    that.scope.objectInfo = objectInfo;
                    that.scope.$broadcast('object-updated', objectInfo);
                });

                that.scope.$on('req-select-object', function (e, selectedObject) {
                    that.scope.$broadcast('object-selected', selectedObject);

                    var components = Util.goodArray(selectedObject.components);
                    that.scope.activeLinkId = (1 == components.length) ? components[0] : "main";
                    var objectId = Util.goodMapValue(selectedObject, "nodeId", null);
                    var objectType = Util.goodMapValue(selectedObject, "nodeType", null);
                    Service.updateObjectSetting(that.moduleId, that.scope.activeLinkId, objectId, objectType);
                    if (that.selectComponentLinks) {
                        that.scope.componentLinks = that.selectComponentLinks(selectedObject);
                    }

                    loadObject(objectId);
                });

                ServCommService.handleResponse(that.scope);
                //that.scope.$on('rootScope:servcomm-response', function (event, data) {
                //    console.log("Help.objbrowser, rootScope:servcomm-response");
                //    //that.scope.$emit('report-object-refreshed', that.stateParams.id);
                //});

                that.scope.progressMsg = $translate.instant("common.objects.progressNoData");
                var loadObject = function (id) {
                    if (Util.goodPositive(id)) {
                        if (that.scope.objectInfo && that.getObjectIdFromInfo(that.scope.objectInfo) != id) {
                            that.scope.objectInfo = null;
                        }
                        that.scope.progressMsg = $translate.instant("common.objects.progressLoading") + " " + id + "...";

                        that.getObjectInfo(id).then(
                            function (objectInfo) {
                                that.scope.progressMsg = null;
                                that.scope.objectInfo = objectInfo;
                                that.scope.$broadcast('object-updated', objectInfo);
                                return objectInfo;
                            }
                            , function (error) {
                                that.scope.objectInfo = null;
                                that.scope.progressMsg = $translate.instant("common.objects.progressError") + " " + id;
                                return error;
                            }
                        );
                    }
                };

                var objectSetting = Service.updateObjectSetting(that.moduleId, null, that.stateParams.id, that.stateParams.type);
                loadObject(objectSetting.objectId);
            }
        };

        Service.Tree.prototype = {
            onReset: function () {
                var that = this;
                that.resetTreeData();
                that.firstLoad = true;
                that.scope.treeData = null;
            }

            /**
             * @ngdoc method
             * @name onLoad
             * @methodOf services:Helper.ObjectBrowserService
             *
             * @param {Number} start Start index of tree data
             * @param {Number} n Max number of tree data records
             * @param {String} sort Sort parameter for tree data query
             * @param {String} filters Filter parameter for tree data query
             * @param {String} query  Search term for tree entry to match
             *
             * @description
             * A callback function to respond object tree events to load tree data of a given page
             */
            , onLoad: function (start, n, sort, filters, query) {
                var that = this;
                if (that.firstLoad && that.nodeId) {
                    that.scope.treeData = null;
                }

                that.getTreeData(start, n, sort, filters, query).then(
                    function (treeData) {
                        if (that.firstLoad) {
                            if (that.nodeId) {
                                if (that.scope.treeData) {            //It must be set by getNodeData(), only 1 items in docs[] is expected
                                    var found = that.findByNodeId(treeData.docs, that.scope.treeData.docs[0].nodeId);
                                    if (!found) {
                                        var clone = _.clone(treeData.docs);
                                        clone.unshift(that.scope.treeData.docs[0]);
                                        treeData.docs = clone;
                                    }
                                    that.firstLoad = false;
                                } else {
                                    if (0 < treeData.docs.length) {
                                        var found = that.findByNodeId(treeData.docs, that.nodeId);
                                        var selectNode = treeData.docs[0];
                                        if (found) {
                                            selectNode = found;
                                        }
                                        that.scope.treeControl.select({
                                            pageStart: start
                                            , nodeType: selectNode.nodeType
                                            , nodeId: selectNode.nodeId
                                        });
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
                            if (that.scope.treeData) {            //It must be set by getTreeData()
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

            , onSelect: function (selectedObject) {
                var that = this;
                that.scope.$emit('req-select-object', selectedObject);


                //var a1 = that.state.current.name;
                //var stateName = ServCommService.getStateToGo();
                //if (stateName) {
                //    ServCommService.setStateToGo(null);
                //} else {
                //    var components = Util.goodArray(selectedObject.components);
                //    var componentType = (1 == components.length) ? components[0] : "main";
                //    stateName = that.moduleId + "." + componentType;
                //}
                var components = Util.goodArray(selectedObject.components);
                var componentType = (1 == components.length) ? components[0] : "main";
                var stateName = that.moduleId + "." + componentType;

                var params = {
                    id: selectedObject.nodeId
                    , type: selectedObject.nodeType
                };

                that.state.go(stateName, params);
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

        Service.Content.prototype = {

            getCurrentObjectSetting: function () {
                return Service.getCurrentObjectSetting();
            }

        };


        /**
         * @ngdoc method
         * @name updateObjectSetting
         * @methodOf services:Helper.ObjectBrowserService
         *
         * @description
         * updateObjectSetting() update objectId and linkId of current object setting in cache. If setting does not exist
         * or it is belong to other module, create a default setting first.
         *
         * @param {String} moduleId Module ID
         * @param {String} linkId (Optional)Active component ID. If null, linkId of setting is not updated
         * @param {Number} objectId (Optional)Object ID. If null, objectId of setting is not updated
         * @param {String} objectType (Optional)Object Type. If objectId is not valid, objectType is not updated
         *
         * @returns {Object} Return updated object setting.
         */
        Service.updateObjectSetting = function (moduleId, linkId, objectId, objectType) {
            var objectSettingCache = new Store.Variable(Service.VariableNames.CURRENT_OBJECT_SETTING);
            var objectSetting = objectSettingCache.get();
            if (Util.isEmpty(objectSetting) || !Util.compare(objectSetting.moduleId, moduleId)) {
                objectSetting = {
                    moduleId: moduleId
                    , objectId: objectId
                    , objectType: objectType
                    , linkId: "main"
                };
            } else if (Util.goodPositive(objectId, false)) {
                objectSetting.objectId = objectId;
                objectSetting.objectType = objectType;
            }

            if (!Util.isEmpty(linkId)) {
                objectSetting.linkId = linkId;
            }

            objectSettingCache.set(objectSetting);
            return objectSetting;
        };


        /**
         * @ngdoc method
         * @name getCurrentObjectSetting
         * @methodOf services:Helper.ObjectBrowserService
         *
         * @description
         * ObjectSetting includes moduleId, objectId, linkId, used to keep track settings for current object content.
         * Object setting is saved in a cache in $rootScope.
         *
         * @returns {Object} Current object setting.
         */
        Service.getCurrentObjectSetting = function () {
            var objectSettingCache = new Store.Variable(Service.VariableNames.CURRENT_OBJECT_SETTING);
            var objectSetting = objectSettingCache.get();
            return objectSetting;
        };


        /**
         * @ngdoc method
         * @name getCurrentObjectId
         * @methodOf services:Helper.ObjectBrowserService
         *
         * @description
         * Get current object ID
         *
         * @returns {Object} Current object ID.
         */
        Service.getCurrentObjectId = function () {
            var objectSetting = this.getCurrentObjectSetting();
            return objectSetting.objectId;
        };


        /**
         * @ngdoc method
         * @name createComponentLinks
         * @methodOf services:Helper.ObjectBrowserService
         *
         * @param {Object} config Configuration
         * @param {number} objType Object Type
         *
         * @description
         * createComponentLinks, used in pages of case, complaint, task, etc., to create component links configuration
         * based on tree configuration and component configuration.
         *
         * @returns {Object} Array of links with items in format of {id: "component ID", title: "Link Title", icon: "link icon"}
         */
        Service.createComponentLinks = function (config, objType) {
            var treeConfig = Util.goodMapValue(config, "tree", {});
            var componentsConfig = Util.goodMapValue(config, "components", []);

            var componentLinks = [];
            var mainConfig = _.find(componentsConfig, {id: "main"});
            if (mainConfig) {
                componentLinks.push({
                    id: Util.goodValue(mainConfig.id)
                    , title: Util.goodValue(mainConfig.title)
                    , icon: Util.goodValue(mainConfig.icon)
                });
            }

            var foundNodeType = _.find(Util.goodMapValue(treeConfig, "nodeTypes", []), {"type": "p/" + objType});
            _.each(Util.goodMapValue(foundNodeType, "components", []), function (component) {
                var foundComponent = _.find(componentsConfig, {id: component});
                if (foundComponent) {
                    componentLinks.push({
                        id: Util.goodValue(foundComponent.id)
                        , title: Util.goodValue(foundComponent.title)
                        , icon: Util.goodValue(foundComponent.icon)
                    });
                }
            });

            return componentLinks;
        };

        return Service;
    }
]);
