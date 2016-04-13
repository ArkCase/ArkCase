'use strict';

/**
 * @ngdoc service
 * @name services:Helper.ObjectBrowserService
 *
 * @description
 *
 * {@link https://gitlab.armedia.com/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/services/helper/helper-objbrowser.client.service.js services/helper/helper-objbrowser.client.service.js}

 * Helper.ObjectBrowserService provide help for common functions for an object page. It includes navigation (or tree) part and content part.
 * Content part consists list of Components.
 * Tree helper uses 'object-tree' directive. Content helper includes component links and data loading. Component helper includes common object info handling
 */
angular.module('services').factory('Helper.ObjectBrowserService', ['$q', '$resource', '$translate'
    , 'Acm.StoreService', 'UtilService', 'ConfigService', 'ServCommService'
    , function ($q, $resource, $translate, Store, Util, ConfigService, ServCommService) {

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
             * @param {Object} arg.state Angular $state
             * @param {Object} arg.stateParams Angular stateParams
             * @param {String} arg.moduleId Module ID
             * @param {Function} arg.resetTreeData Function to reset tree data
             * @param {Function} arg.updateTreeData Function to update a tree node data in a tree
             * @param {Function} arg.resetTreeData Function to reset tree data
             * @param {Function} arg.getTreeData Function to retrieve tree data
             * @param {Function} arg.getNodeData Function to retrieve a tree node data
             * @param {Function} arg.makeTreeNode Function to make tree node from object info
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
                that.updateTreeData = arg.updateTreeData;
                that.getTreeData = arg.getTreeData;
                that.getNodeData = arg.getNodeData;
                that.makeTreeNode = arg.makeTreeNode;
                that.firstLoad = true;


                //
                // if url contains component, save its subKey to go to a sub tree node
                //
                that.subKey = Service.getComponentByState(that.state);
                if ("main" == that.subKey) {
                    that.subKey = null;
                }


                ConfigService.getModuleConfig(that.moduleId).then(function (config) {
                    that.scope.treeConfig = config.tree;
                    that.scope.componentsConfig = config.components;

                    var activeComponent = Service.getComponentByState(that.state);
                    if ("main" != activeComponent) {
                        var nodeTypes = Util.goodArray(config.tree.nodeTypes);
                        var found = _.find(nodeTypes, function (nodeType) {
                            var comp = Util.goodArray(nodeType.components);
                            if (1 == comp.length && activeComponent == comp[0]) {
                                return true;
                            }
                            return false;
                        });
                        that.subKey = Util.goodMapValue(found, "type").split("/").pop();
                    }

                    return config;
                });

                that.nodeId = Service.getCurrentObjectId();

                that.scope.onReset = function () {
                    that.onReset();
                };

                that.scope.onLoad = function (start, n, sort, filters, query) {
                    that.treeParams = {};
                    that.treeParams.start = start;
                    that.treeParams.n = n;
                    that.treeParams.sort = sort;
                    that.treeParams.filters = filters;
                    that.treeParams.query = query;
                    that.onLoad(start, n, sort, filters, query);
                };

                that.scope.onSelect = function (selectedObject) {
                    that.selectedObject = selectedObject;
                    that.onSelect(selectedObject);
                };

                that.scope.$on("object-updated", function (e, objectInfo) {
                    var node = that.makeTreeNode(objectInfo);
                    if (that.scope.treeControl) {
                        that.scope.treeControl.setTitle(node.nodeType, node.nodeId, node.nodeTitle, node.nodeToolTip);
                        if (that.updateTreeData && that.treeParams) {
                            that.updateTreeData(that.treeParams.start, that.treeParams.n, that.treeParams.sort, that.treeParams.filters, that.treeParams.query, node);
                        }
                    }
                });

                that.scope.$on("object-update-failed", function (e, error) {
                    if (that.selectedObject && that.scope.treeControl) {
                        var nodeType = Util.goodValue(that.selectedObject.nodeType);
                        var nodeId = Util.goodValue(that.selectedObject.nodeId);
                        that.scope.treeControl.setTitle(nodeType, nodeId
                            , $translate.instant("common.directive.objectTree.errorNode.title")
                            , $translate.instant("common.directive.objectTree.errorNode.toolTip")
                        );
                    }
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

                that.scope.activeLinkId = Service.getComponentByState(that.state);
                Service.updateObjectSetting(that.moduleId, that.scope.activeLinkId); //don't update objectId/Type; only set linkId

                ConfigService.getModuleConfig(that.moduleId).then(function (moduleConfig) {
                    that.scope.config = moduleConfig;
                    that.scope.componentLinks = that.initComponentLinks(moduleConfig);
                    that.scope.linksShown = Util.goodValue(moduleConfig.initialLinksShown, true);
                    return moduleConfig;
                });

                that.scope.getActive = function (linkId) {
                    return (that.scope.activeLinkId == linkId) ? "active" : ""
                };

                that.scope.onClickComponentLink = function (linkId) {
                    that.scope.activeLinkId = linkId;
                    var objectId = that.getObjectIdFromInfo(that.scope.objectInfo);
                    var objectType = that.getObjectTypeFromInfo(that.scope.objectInfo);
                    Service.updateObjectSetting(that.moduleId, linkId, objectId, objectType);
                    var params = {id: objectId};
                    if (!Util.isEmpty(objectType)) {
                        params.type = objectType;
                    }

                    if (that.scope.treeControl) {
                        that.scope.treeControl.selectComponent(objectType, objectId, linkId);
                    }

                    that.state.go(arg.moduleId + "." + linkId, params);
                };

                that.scope.linksShown = true;
                that.scope.toggleShowLinks = function () {
                    that.scope.linksShown = !that.scope.linksShown;
                };

                that.scope.$on("collapsed", function (event, collapsed) {
                    that.scope.linksShown = !collapsed;
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

                that.scope.$on('report-object-update-failed', function (e, objectInfo) {
                    that.scope.$broadcast('object-update-failed', objectInfo);
                });

                that.scope.$on('req-select-object', function (e, selectedObject) {
                    that.scope.$broadcast('object-selected', selectedObject);

                    var leadComponent = selectedObject.leadComponent;
                    if (!leadComponent) {
                        var components = Util.goodArray(selectedObject.components);
                        leadComponent = (1 == components.length) ? components[0] : "main";
                    }
                    that.scope.activeLinkId = leadComponent;

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
                                that.scope.$broadcast('object-update-failed', error);
                                return error;
                            }
                        );
                    }
                };

                var objectSetting = Service.updateObjectSetting(that.moduleId, null, that.stateParams.id, that.stateParams.type);
                loadObject(objectSetting.objectId);
            }


            /**
             * @ngdoc method
             * @name Component Constructor
             * @methodOf services:Helper.ObjectBrowserService
             *
             * @param {Object} arg Map arguments
             * @param {Object} arg.scope Angular $scope
             * @param {Object} arg.stateParams Angular $stateParams
             * @param {String} arg.moduleId Module ID
             * @param {String} arg.componentId Component ID
             * @param {Function} arg.getObjectInfo Function to retrieve object info
             * @param {Function} (Optional)arg.validateObjectInfo Function to validate object info data
             * @param {Function} (Optional)arg.onObjectInfoRetrieved Callback function when object info retrieved
             * @param {Function} (Optional)arg.onConfigRetrieved Callback function when component config retrieved.
             * The callback could optionally return true or false to indicate the config data is processed.
             * If no return is defined, it is the same as return true. With a false return, onObjectInfoRetrieved
             * callback would not be called until caller to call doneConfig() function later to notify Component
             * Helper to release onObjectInfoRetrieved().
             *
             * @description
             * Helper.ObjectBrowserService.Component captures common handling for typical components, including
             * retrieving configuration of the component and data of the object (ObjectInfo) the component represent.
             *
             * It retrieves component configuration and presents it by the callback function onConfigRetrieved argument.
             * By default, component config is saved to a scope variable '$scope.config', unless consumer code
             * override it in onConfigRetrieved() callback handler.
             *
             * Object info can be retrieved or updated via events between components. Assumption of the order of
             * Components being initialized is one of source of bugs. Component helper does not make any assumption of the
             * component initialization order to ensure it works regardless it is initialized before or after other
             * components. By default, object info is saved to a scope variable '$scope.objectInfo', but consumer
             * code may override it in onObjectInfoRetrieved() callback handler.
             *
             * The helper saves two instance variables 'currentObjectId' and 'promiseConfig'. 'currentObjectId' tracks
             * the current object ID; and 'promiseConfig' is the promise returned by configuration retrieving.
             * The promise variable may be helpful when retrieving data depends on configuration.
             *
             */
            , Component: function (arg) {
                var that = this;
                that.scope = arg.scope;
                that.stateParams = arg.stateParams;
                that.moduleId = arg.moduleId;
                that.componentId = arg.componentId;
                that.retrieveObjectInfo = arg.retrieveObjectInfo;
                that.validateObjectInfo = (arg.validateObjectInfo) ? arg.validateObjectInfo : function (data) {
                    return (!Util.isEmpty(data));
                };
                that.onObjectInfoRetrieved = function (objectInfo) {
                    that.scope.objectInfo = objectInfo;
                    if (arg.onObjectInfoRetrieved) {
                        arg.onObjectInfoRetrieved(objectInfo);
                    }
                };
                that.onConfigRetrieved = function (componentConfig) {
                    that.scope.config = componentConfig;
                    if (arg.onConfigRetrieved) {
                        return arg.onConfigRetrieved(componentConfig);
                    }
                };

                that.deferConfigDone = $q.defer();
                that.promiseConfig = ConfigService.getComponentConfig(that.moduleId, that.componentId);
                that.scope.promiseConfig = that.promiseConfig;  //phase out; keep for backward compatibility
                that.promiseConfig.then(function (componentConfig) {
                    var done = that.onConfigRetrieved(componentConfig);
                    if (undefined === done || true === done) {
                        that.deferConfigDone.resolve(componentConfig);
                    }
                    return componentConfig;
                });


                that.previousId = null;
                that.scope.$on('object-updated', function (e, objectInfo) {
                    that.currentObjectId = Service.getCurrentObjectId();
                    that.scope.currentObjectId = that.currentObjectId;  //phase out; keep for backward compatibility
                    updateObjectInfo(that.currentObjectId, objectInfo);
                });

                that.scope.$on('object-refreshed', function (e, objectInfo) {
                    that.previousId = null;
                    that.currentObjectId = Service.getCurrentObjectId();
                    that.scope.currentObjectId = that.currentObjectId;  //phase out; keep for backward compatibility
                    updateObjectInfo(that.currentObjectId, objectInfo);
                });

                that.currentObjectId = Service.getCurrentObjectId();
                that.scope.currentObjectId = that.currentObjectId;  //phase out; keep for backward compatibility
                if (Util.goodPositive(that.currentObjectId, false)) {
                    if (!Util.compare(that.previousId, that.currentObjectId)) {
                        that.retrieveObjectInfo(that.currentObjectId).then(function (objectInfo) {
                            updateObjectInfo(that.currentObjectId, objectInfo);
                            return objectInfo;
                        });
                    }
                }

                var updateObjectInfo = function (objectId, objectInfo) {
                    if (!that.validateObjectInfo(objectInfo)) {
                        return;
                    }
                    if (!Util.goodPositive(objectId, false)) {
                        return;
                    }
                    // EDTRM-491 Delete Adhoc task doesn't do anything: The fix is to remove the below block.
                    //
                    // With the below code, then when we re-load the same object, for example because we acted on
                    // it (deleted it, completed it etc etc), the new state of the object will not be displayed.
                    // So I have commented this code out, and added this warning comment, so nobody will try to add it
                    // back in later..... Of course I am open to better ideas.  --DGM
                    //if (Util.compare(that.previousId, objectId)) {
                    //    return;   NOTE do not enable this code, or re-add similar code somewhere else, until
                    //              you have read and understood the above comment.
                    //}
                    that.previousId = objectId;

                    that.deferConfigDone.promise.then(function (data) {
                        that.onObjectInfoRetrieved(objectInfo);
                    });
                };

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
                                        var selectNode = (found) ? found : treeData.docs[0];
                                        that.scope.treeControl.select({
                                            pageStart: start
                                            , nodeType: selectNode.nodeType
                                            , nodeId: selectNode.nodeId
                                            , subKey: that.subKey
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
                                        , subKey: that.subKey
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
                                , subKey: that.subKey
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
                                , subKey: that.subKey
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

                var componentType = selectedObject.leadComponent;
                if (Util.isEmpty(componentType)) {
                    var components = Util.goodArray(selectedObject.components);
                    componentType = (1 == components.length) ? components[0] : "main";
                }
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

        Service.Component.prototype = {

            /**
             * @ngdoc method
             * @name doneConfig
             * @methodOf services:Helper.ObjectBrowserService
             *
             * @param {Object} config Component configuration JSON object
             *
             * @description
             * Notify Component Helper that Config data is processed
             */
            doneConfig: function (config) {
                this.deferConfigDone.resolve(config);
            }

        };

        Service.getComponentByState = function (state) {
            var comp = "main";
            var tokens = state.current.url.split("/");
            if (1 < tokens.length) {
                if (":id" == tokens[tokens.length - 2]) {
                    if ("main" != tokens[tokens.length - 1]) {
                        comp = tokens[tokens.length - 1];
                    }
                }
            }
            return comp;
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
            var componentLinks = [];
            var treeConfig = Util.goodMapValue(config, "tree", {});
            var foundNodeType = _.find(Util.goodMapValue(treeConfig, "nodeTypes", []), {"type": "p/" + objType});
            if (foundNodeType) {
                var componentsConfig = Util.goodMapValue(config, "components", []);

                var leadComponent = Util.goodValue(foundNodeType.leadComponent, "main");
                var leadConfig = _.find(componentsConfig, {id: leadComponent});
                if (leadConfig) {
                    componentLinks.push({
                        id: Util.goodValue(leadConfig.id)
                        , title: Util.goodValue(leadConfig.title)
                        , icon: Util.goodValue(leadConfig.icon)
                    });
                }

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
            }
            return componentLinks;
        };

        return Service;
    }
]);
