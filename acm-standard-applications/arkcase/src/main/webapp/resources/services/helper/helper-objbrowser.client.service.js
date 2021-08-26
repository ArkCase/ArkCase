'use strict';

/**
 * @ngdoc service
 * @name services:Helper.ObjectBrowserService
 *
 * @description
 *
 * {@link /acm-standard-applications/arkcase/src/main/webapp/resources/services/helper/helper-objbrowser.client.service.js services/helper/helper-objbrowser.client.service.js}

 * Helper.ObjectBrowserService provide help for common functions for an object page. It includes navigation (or tree) part and content part.
 * Content part consists list of Components.
 * Tree helper uses 'object-tree' directive. Content helper includes component links and data loading. Component helper includes common object info handling
 */
angular.module('services').factory(
        'Helper.ObjectBrowserService',
        [ '$q', '$resource', '$translate', '$timeout', '$locale', 'Acm.StoreService', 'UtilService', 'ConfigService', 'ServCommService', 'MessageService', 'ObjectService', 'Config.LocaleService', 'Admin.ObjectTitleConfigurationService',
                function($q, $resource, $translate, $timeout, $locale, Store, Util, ConfigService, ServCommService, MessageService, ObjectService, LocaleService, AdminObjectTitleConfigurationService) {

                    var SyncDataLoader = {
                        data: {},
                        getKey: function(moduleId, args) {
                            return moduleId + "_" + args.join("_");
                        },
                        load: function(moduleId, dataLoadFunc, args, success, error) {
                            var that = this;
                            var key = this.getKey(moduleId, args);
                            var entry = this.data[key];
                            if (entry) {
                                if (entry.then) {
                                    entry.then(success, error);
                                } else {
                                    //sync the local helper-objbrowser cache with the value from the main frontend cache
                                    entry = that.data[key] = dataLoadFunc.apply(this, args);
                                    entry.then(function(data) {
                                        entry = that.data[key] = data;
                                        success.apply(this, [ entry ]);
                                        return entry;
                                    });
                                }
                            } else {
                                entry = that.data[key] = dataLoadFunc.apply(this, args);
                                entry.then(function(data) {
                                    entry = that.data[key] = data;
                                    return entry;
                                }).then(success, error);
                            }
                            return entry;
                        },
                        reset: function(moduleId, args) {
                            var key = this.getKey(moduleId, args);
                            this.data[key] = null;
                        }
                    };

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
                        ,
                        Tree: function(arg) {
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

                            ConfigService.getModuleConfig(that.moduleId).then(function(config) {
                                that.scope.treeConfig = config.tree;
                                that.scope.componentsConfig = config.components;

                                var activeComponent = Service.getComponentByState(that.state);
                                if ("main" != activeComponent) {
                                    var nodeTypes = Util.goodArray(config.tree.nodeTypes);
                                    var found = _.find(nodeTypes, function(nodeType) {
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

                            that.scope.onReset = function() {
                                that.onReset();
                            };

                            that.scope.onLoad = function(start, n, sort, filters, query) {
                                that.treeParams = {};
                                that.treeParams.start = start;
                                that.treeParams.n = n;
                                that.treeParams.sort = sort;
                                that.treeParams.filters = filters;
                                that.treeParams.query = query;
                                that.onLoad(start, n, sort, filters, query);
                            };

                            that.scope.onSelect = function(selectedObject) {
                                that.selectedObject = selectedObject;
                                that.onSelect(selectedObject);
                            };

                            that.scope.$on("object-updated", function(e, objectInfo) {
                                var title;
                                var node = that.makeTreeNode(objectInfo);
                                if (!Util.isEmpty(that.scope.treeData) && !Util.isEmpty(that.scope.treeData.configTitleList[node.nodeType]))
                                {
                                    var configurationTitle = that.scope.treeData.configTitleList[node.nodeType].title;
                                    if (configurationTitle === "objectId") {
                                        title = node.nodeId;
                                    } else if (configurationTitle === "titleTitle") {
                                        title = node.nodeTitle;
                                    } else if (configurationTitle === "objectIdTitle") {
                                        title = node.nodeId + " - " + node.nodeTitle;
                                    } else if (configurationTitle === "titleObjectId") {
                                        title = node.nodeTitle + " - " + node.nodeId;
                                    } else if (configurationTitle === 'numberTitle') {
                                        title = node.nodeNumber + " - " + node.nodeTitle;
                                    }
                                }
                                else {
                                    title = node.nodeTitle;
                                }

                                that.scope.treeControl.setTitle(node.nodeType, node.nodeId, title, node.nodeToolTip);

                                if (that.updateTreeData && that.treeParams) {
                                    that.updateTreeData(that.treeParams.start, that.treeParams.n, that.treeParams.sort, that.treeParams.filters, that.treeParams.query, node);
                                }

                            });

                            that.scope.$on("object-update-failed", function(e, error) {
                                if (that.selectedObject && that.scope.treeControl) {
                                    var nodeType = Util.goodValue(that.selectedObject.nodeType);
                                    var nodeId = Util.goodValue(that.selectedObject.nodeId);
                                    that.scope.treeControl.setTitle(nodeType, nodeId, $translate.instant("common.directive.objectTree.errorNode.title"), $translate.instant("common.directive.objectTree.errorNode.toolTip"));
                                }
                            });

                            that.scope.$on("link-updated", function(event, linkParams) {
                                if (that.scope.treeControl) {
                                    that.scope.treeControl.selectComponent(linkParams.objectType, linkParams.objectId, linkParams.linkId);
                                }
                            });

                            that.scope.$on('tree-updated', function(e, objectInfo) {
                                if (that.scope.treeControl) {
                                    that.firstLoad = true;
                                    that.nodeId = undefined;

                                    Service.resetCurrentObjectSetting();
                                    that.scope.treeControl.refresh();
                                }
                            });

                            /**
                             * @ngdoc event
                             * @name req-switch-object
                             * @methodOf services:Helper.ObjectBrowserService.Tree
                             *
                             * @param {Object} eventData Event data
                             * @param {String} eventData.objectId Object ID
                             * @param {String} eventData.objectType Object type
                             * @param {String} (Optional)eventData.objectSubtype Object subType
                             *
                             * @description
                             * Send this event to select a tree node for the specified object
                             */
                            that.scope.$on('req-switch-object', function(e, eventData) {
                                if (that.scope.treeControl) {
                                    var nodeId = eventData.objectId;
                                    var nodeType = (eventData.objectSubtype) ? eventData.objectSubtype : eventData.objectType;
                                    that.scope.treeControl.select({
                                        nodeId: nodeId,
                                        nodeType: nodeType
                                    }, true);
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
                        ,
                        Content: function(arg) {
                            var that = this;
                            that.scope = arg.scope;
                            that.state = arg.state;
                            that.stateParams = arg.stateParams;
                            that.moduleId = arg.moduleId;

                            that.resetObjectInfo = arg.resetObjectInfo;
                            that.getObjectInfo = arg.getObjectInfo;
                            that.updateObjectInfo = arg.updateObjectInfo;
                            that.initComponentLinks = (arg.initComponentLinks) ? arg.initComponentLinks : function(config) {
                                var nodeType = Service.getCurrentObjectType();
                                return Service.createComponentLinks(config, nodeType);
                            };
                            that.selectComponentLinks = (arg.selectComponentLinks) ? arg.selectComponentLinks : function(selectedObject) {
                                if (!Util.isArrayEmpty(that.scope.componentLinks)) {
                                    return that.scope.componentLinks;
                                } else if (that.initComponentLinks) {
                                    return that.initComponentLinks(that.scope.config);
                                } else {
                                    return [];
                                }
                            };
                            that.getObjectIdFromInfo = (arg.getObjectIdFromInfo) ? arg.getObjectIdFromInfo : function(objectInfo) {
                                return Util.goodMapValue(objectInfo, "id");
                            };
                            that.getObjectTypeFromInfo = (arg.getObjectTypeFromInfo) ? arg.getObjectTypeFromInfo : function(objectInfo) {
                                return "";
                            };

                            that.getObjectNumberFromInfo = (arg.getObjectNumberFromInfo) ? arg.getObjectNumberFromInfo : function(objectInfo) {
                                return Util.goodMapValue(objectInfo, "acmObjectNumber");
                            }; 

                            that.scope.activeLinkId = Service.getComponentByState(that.state);
                            Service.updateObjectSetting(that.moduleId, that.scope.activeLinkId); //don't update objectId/Type; only set linkId

                            ConfigService.getModuleConfig(that.moduleId).then(function(moduleConfig) {
                                that.scope.config = moduleConfig;
                                that.scope.componentLinks = that.initComponentLinks(moduleConfig);
                                that.scope.linksShown = Util.goodValue(moduleConfig.initialLinksShown, true);
                                return moduleConfig;
                            });

                            that.scope.getActive = function(linkId) {
                                return (that.scope.activeLinkId == linkId) ? "active" : ""
                            };

                            that.scope.onClickComponentLink = function(linkId) {
                                that.scope.activeLinkId = linkId;
                                var objectId = that.getObjectIdFromInfo(that.scope.objectInfo);
                                var objectType = that.getObjectTypeFromInfo(that.scope.objectInfo);
                                Service.updateObjectSetting(that.moduleId, linkId, objectId, objectType);

                                var linkParams = {
                                    objectType: objectType,
                                    objectId: objectId,
                                    linkId: linkId
                                };
                                var rc = that.scope.$broadcast('link-updated', linkParams);

                                var stateParams = {
                                    id: objectId
                                };
                                if (!Util.isEmpty(objectType)) {
                                    stateParams.type = objectType;
                                }
                                that.state.go(arg.moduleId + "." + linkId, stateParams);
                            };

                            that.scope.linksShown = true;
                            that.scope.toggleShowLinks = function() {
                                that.scope.linksShown = !that.scope.linksShown;
                            };

                            that.scope.$on("collapsed", function(event, collapsed) {
                                that.scope.linksShown = !collapsed;
                            });

                            that.scope.$on('report-object-refreshed', function(e, objectId) {
                                that.resetObjectInfo(e.currentScope.objectInfo);
                                SyncDataLoader.reset(that.moduleId, [ objectId ]);
                                SyncDataLoader.load(that.moduleId, that.getObjectInfo, [ objectId ], function(objectInfo) {
                                    that.scope.objectInfo = objectInfo;
                                    that.scope.$broadcast('object-refreshed', objectInfo, true);
                                    return objectInfo;
                                }, function(error) {
                                    that.scope.objectInfo = null;
                                    MessageService.error($translate.instant("common.objects.progressError") + " " + objectId);
                                    return error;
                                })

                            });

                            that.scope.$on('report-tree-updated', function(e, objectInfo) {
                                that.scope.$broadcast('tree-updated', objectInfo);
                            });

                            that.scope.$on('report-object-updated', function(e, objectInfo) {
                                that.currentObjectId = Service.getCurrentObjectId();
                                var objectId = that.getObjectIdFromInfo(objectInfo);
                                that.scope.objectInfo = objectInfo;
                                that.scope.$broadcast('object-updated', objectInfo, objectId, true);
                            });

                            that.scope.$on('report-object-update-failed', function(e, objectInfo) {
                                that.scope.$broadcast('object-update-failed', objectInfo);
                            });

                            /**
                             * @ngdoc event
                             * @name request-show-object
                             * @methodOf services:Helper.ObjectBrowserService.Content
                             *
                             * @param {Object} eventData Event data
                             * @param {String} eventData.objectId Object ID
                             * @param {String} eventData.objectType Object type
                             * @param {String} (Optional)eventData.objectSubtype Object subType
                             *
                             * @description
                             * Send this event to open up an object
                             */
                            that.scope.$on('request-show-object', function(e, eventData) {
                                that.scope.$broadcast('req-switch-object', eventData);
                            });

                            that.scope.$on('req-select-object', function(e, selectedObject) {
                                that.scope.$broadcast('object-selected', selectedObject);

                                var objectId = Util.goodMapValue(selectedObject, "nodeId", null);
                                var objectType = Util.goodMapValue(selectedObject, "nodeType", null);
                                Service.updateObjectSetting(that.moduleId, that.scope.activeLinkId, objectId, objectType);

                                loadObject(objectId);

                                var leadComponent = selectedObject.leadComponent;
                                if (!leadComponent) {
                                    var components = Util.goodArray(selectedObject.components);
                                    leadComponent = (1 === components.length) ? components[0] : "main";
                                }
                                that.scope.activeLinkId = leadComponent;

                                that.scope.componentLinks = that.selectComponentLinks(selectedObject);
                            });

                            ServCommService.handleResponse(that.scope);

                            that.scope.progressMsg = $translate.instant("common.objects.progressNoData");
                            var loadObject = function(id) {
                                if (!Util.goodPositive(id)) {
                                    return;
                                }
                                if (that.scope.objectInfo && that.getObjectIdFromInfo(that.scope.objectInfo) != id) {
                                    that.scope.objectInfo = null;
                                }

                                that.scope.progressMsg = $translate.instant("common.objects.progressLoading") + " " + id + "...";
                                SyncDataLoader.load(that.moduleId, that.getObjectInfo, [ id ], function(objectInfo) {
                                    that.scope.progressMsg = null;
                                    that.scope.objectInfo = objectInfo;
                                    $timeout(function() {
                                        that.scope.$broadcast('object-updated', objectInfo, id);
                                    }, 0);

                                    //when object is loaded we want to subscribe to change events
                                    var objectId = id;
                                    var objectType = that.getObjectTypeFromInfo(that.scope.objectInfo);
                                    var objectNumber = that.getObjectNumberFromInfo(that.scope.objectInfo);

                                    //objectType fix for task
                                    if (objectType === 'ADHOC') {
                                        objectType = 'TASK';
                                    }

                                    var eventName = "object.changed/" + objectType + "/" + objectId;

                                    //if there is subscription from other object we want to unsubscribe
                                    //we want to have only one subscription from the current object
                                    if (that.scope.subscription) {
                                        that.scope.$bus.unsubscribe(that.scope.subscription);
                                    }
                                    that.scope.subscription = that.scope.$bus.subscribe(eventName, function(data) {
                                        //when we receive message that object was changed show it
                                        //we can change to show other popups with generated links etc...
                                        var objectTypeString = $translate.instant('common.objectTypes.' + objectType);
                                        if (!objectTypeString) {
                                            objectTypeString = objectType;
                                        }
                                        if (data.objectNumber) {
                                            MessageService.info(objectTypeString + " with number " + data.objectNumber + " was updated.");
                                        } else {
                                            if(objectTypeString == 'Task'){
                                                MessageService.info(objectTypeString + " with id " + objectId + " was updated.");
                                            }
                                            else{
                                                MessageService.info(objectTypeString + " with number " + objectNumber + " was updated.");
                                            }
                                        }

                                        var frevvoRequest = null;
                                        switch (objectType) {
                                        case ObjectService.ObjectTypes.COMPLAINT:
                                            frevvoRequest = ServCommService.popRequest("frevvo", "close-complaint");
                                            break;
                                        case ObjectService.ObjectTypes.TIMESHEET:
                                            frevvoRequest = ServCommService.popRequest("frevvo", "edit-timesheet") || ServCommService.popRequest("frevvo", "new-timesheet");
                                            break;
                                        }

                                        if (frevvoRequest) {
                                            that.scope.$emit('report-object-refreshed', objectId);
                                        }
                                    });

                                    return objectInfo;
                                }, function(error) {
                                    that.scope.objectInfo = null;
                                    that.scope.progressMsg = $translate.instant("common.objects.progressError") + " " + id;
                                    that.scope.$broadcast('object-update-failed', error);
                                    return error;
                                });

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
                        ,
                        Component: function(arg) {
                            var that = this;
                            that.scope = arg.scope;
                            that.stateParams = arg.stateParams;
                            that.moduleId = arg.moduleId;
                            that.componentId = arg.componentId;
                            that.retrieveObjectInfo = arg.retrieveObjectInfo;
                            that.currentObjectId = (arg.objectId ? that.scope.currentObjectId = arg.objectId : that.scope.currentObjectId = Service.getCurrentObjectId());

                            if (arg.resetComponentData) {
                                SyncDataLoader.reset(arg.objectId ? that.moduleId + that.componentId : that.moduleId, [ that.currentObjectId ]);
                            }

                            that.validateObjectInfo = (arg.validateObjectInfo) ? arg.validateObjectInfo : function(data) {
                                return (!Util.isEmpty(data));
                            };
                            that.onObjectInfoRetrieved = function(objectInfo, e) {
                                if (that.loaded) {
                                    return;
                                }
                                that.loaded = that.currentObjectId;
                                that.scope.objectInfo = objectInfo;
                                if (arg.onObjectInfoRetrieved) {
                                    arg.onObjectInfoRetrieved(objectInfo, e);
                                }
                            };
                            that.onConfigRetrieved = function(componentConfig) {
                                that.scope.config = componentConfig;
                                if (arg.onConfigRetrieved) {
                                    return arg.onConfigRetrieved(componentConfig);
                                }
                            };
                            that.scope.lang = LocaleService.getLocaleData().code;
                            that.scope.locale = $locale;
                            that.scope.currencySymbol = LocaleService.getCurrencySymbol($locale.id);
                            that.scope.$bus.subscribe('$translateChangeSuccess', function(data) {
                                that.scope.lang = Util.goodMapValue(data, "lang", LocaleService.getLocaleData().code);
                                that.scope.locale = $locale;
                                that.scope.currencySymbol = LocaleService.getCurrencySymbol(data.lang);
                                if (arg.onTranslateChangeSuccess) {
                                    return arg.onTranslateChangeSuccess(data);
                                }
                            });

                            that.deferConfigDone = $q.defer();
                            that.promiseConfig = ConfigService.getComponentConfig(that.moduleId, that.componentId);
                            that.promiseConfig.then(function(componentConfig) {
                                var done = that.onConfigRetrieved(componentConfig);
                                if (undefined === done || true === done) {
                                    that.deferConfigDone.resolve(componentConfig);
                                }
                                return componentConfig;
                            });

                            that.previousId = null;
                            that.scope.$on('object-updated', function(e, objectInfo, objectId, reload) {
                                if (reload || that.loaded != Service.getCurrentObjectId()) {
                                    delete that.loaded;
                                } else {
                                    return;
                                }
                                if (Service.getCurrentObjectId()) {
                                    that.currentObjectId = Service.getCurrentObjectId();
                                }
                                if (that.currentObjectId == objectId) {
                                    onObjectInfoUpdated(objectInfo, objectId, e);
                                }
                            });

                            that.scope.$on('object-refreshed', function(e, objectInfo, reload) {
                                if (reload || that.loaded != Service.getCurrentObjectId()) {
                                    delete that.loaded;
                                } else {
                                    return;
                                }
                                that.previousId = null;
                                if (Service.getCurrentObjectId()) {
                                    that.currentObjectId = Service.getCurrentObjectId();
                                }
                                onObjectInfoUpdated(objectInfo, that.currentObjectId, e);
                            });

                            that.scope.$on('report-object-refreshed', function(e, objectId) {
                                SyncDataLoader.reset(arg.objectId ? that.moduleId + that.componentId : that.moduleId, [ objectId ]);
                                SyncDataLoader.load(arg.objectId ? that.moduleId + that.componentId : that.moduleId, that.retrieveObjectInfo, [ objectId ], function(objectInfo) {
                                    that.scope.objectInfo = objectInfo;
                                    that.scope.$broadcast('object-refreshed', objectInfo, true);
                                    return objectInfo;
                                }, function(error) {
                                    that.scope.objectInfo = null;
                                    MessageService.error($translate.instant("common.objects.progressError") + " " + objectId);
                                    return error;
                                })

                            });

                            if (that.currentObjectId) {
                                SyncDataLoader.load(arg.objectId ? that.moduleId + that.componentId : that.moduleId, that.retrieveObjectInfo, [ that.currentObjectId ], function(objectInfo) {
                                    onObjectInfoUpdated(objectInfo, that.currentObjectId);
                                    return objectInfo;
                                });
                            }

                            function onObjectInfoUpdated(objectInfo, objectId, e) {
                                if (!that.validateObjectInfo(objectInfo)) {
                                    return;
                                }
                                if (!Util.goodPositive(objectId, false)) {
                                    return;
                                }

                                that.previousId = objectId;

                                that.deferConfigDone.promise.then(function(data) {
                                    that.onObjectInfoRetrieved(objectInfo, e);
                                });
                            }
                        }
                    };

                    Service.Tree.prototype = {
                        onReset: function() {
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
                        ,
                        onLoad: function(start, n, sort, filters, query) {
                            var that = this;

                            var promiseTreeData = that.getTreeData(start, n, sort, filters, query);

                            var promisConfigTitle = AdminObjectTitleConfigurationService.getObjectTitleConfiguration();

                            var deferNodeData = $q.defer();
                            if (that.nodeId) {
                                that.getNodeData(that.nodeId).then(function(objectInfo) {
                                    var treeNode = that.makeTreeNode(objectInfo);
                                    deferNodeData.resolve(treeNode);
                                    return objectInfo;
                                }, function(errorData) {
                                    var treeNode = {
                                        nodeId: that.nodeId,
                                        nodeType: "ERROR",
                                        nodeToolTip: Util.goodMapValue(errorData, "data")
                                    };
                                    deferNodeData.resolve(treeNode);
                                    return errorData;
                                });
                            } else {
                                deferNodeData.resolve(null);
                            }

                            $q.all([ promiseTreeData, deferNodeData.promise, promisConfigTitle ]).then(function(data) {
                                var treeData = Util.goodValue(data[0], {
                                    docs: [],
                                    total: 0
                                });
                                var treeNode = data[1];

                                var configTitleData = data[2];
                                var configTitleList = configTitleData.data;

                                var selectNode = Util.goodMapValue(treeData, "docs[0]", null);
                                if (treeNode) {
                                    var found = that.findByNodeId(treeData.docs, treeNode.nodeId);
                                    if (found) {
                                        selectNode = found;
                                    } else {
                                        var docs = _.clone(treeData.docs);
                                        docs.unshift(treeNode);
                                        treeData = {
                                            docs: docs,
                                            total: treeData.total + 1,
                                            configTitleList: configTitleList
                                        };
                                        selectNode = treeNode;
                                    }
                                }

                                that.scope.treeData = {
                                    docs: treeData.docs,
                                    total: treeData.total,
                                    configTitleList: configTitleList
                                };
                                if (selectNode) {
                                    that.scope.treeControl.select({
                                        pageStart: start,
                                        nodeType: selectNode.nodeType,
                                        nodeId: selectNode.nodeId,
                                        subKey: that.subKey
                                    });
                                }
                            });
                        }

                        ,
                        onSelect: function(selectedObject) {
                            var that = this;
                            that.scope.$emit('req-select-object', selectedObject);

                            var componentType = selectedObject.leadComponent;
                            if (Util.isEmpty(componentType)) {
                                var components = Util.goodArray(selectedObject.components);
                                componentType = (1 === components.length) ? components[0] : "main";
                            }
                            var stateName = that.moduleId + "." + componentType;

                            var params = {
                                id: selectedObject.nodeId,
                                type: selectedObject.nodeType
                            };

                            that.state.go(stateName, params);
                        }

                        ,
                        findByNodeId: function(docs, nodeId) {
                            if (nodeId) {
                                if (_.isNumber(nodeId)) {
                                    nodeId = nodeId.toString();
                                }
                            }
                            return _.find(docs, {
                                nodeId: nodeId
                            });
                        }
                    };

                    Service.Content.prototype = {

                        getCurrentObjectSetting: function() {
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
                        doneConfig: function(config) {
                            this.deferConfigDone.resolve(config);
                        }

                    };

                    Service.getComponentByState = function(state) {
                        var comp = "main";
                        var tokens = state.current.url.split("/");
                        if (1 < tokens.length) {
                            if (":id" === tokens[tokens.length - 2]) {
                                if ("main" !== tokens[tokens.length - 1]) {
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
                    Service.updateObjectSetting = function(moduleId, linkId, objectId, objectType) {
                        var objectSettingCache = new Store.Variable(Service.VariableNames.CURRENT_OBJECT_SETTING);
                        var objectSetting = objectSettingCache.get();
                        if (Util.isEmpty(objectSetting) || !Util.compare(objectSetting.moduleId, moduleId)) {
                            objectSetting = {
                                moduleId: moduleId,
                                objectId: objectId,
                                objectType: objectType,
                                linkId: "main"
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
                    Service.getCurrentObjectSetting = function() {
                        var objectSettingCache = new Store.Variable(Service.VariableNames.CURRENT_OBJECT_SETTING);
                        var objectSetting = objectSettingCache.get();
                        return objectSetting;
                    };

                    /**
                     * @ngdoc method
                     * @name resetCurrentObjectSetting
                     * @methodOf services:Helper.ObjectBrowserService
                     *
                     * @description
                     * Reset ObjectSetting
                     *
                     */
                    Service.resetCurrentObjectSetting = function() {
                        var objectSettingCache = new Store.Variable(Service.VariableNames.CURRENT_OBJECT_SETTING);
                        objectSettingCache.set(null);
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
                    Service.getCurrentObjectId = function() {
                        var objectSetting = this.getCurrentObjectSetting();
                        return (objectSetting) ? objectSetting.objectId : null;
                    };

                    /**
                     * @ngdoc method
                     * @name getCurrentObjectType
                     * @methodOf services:Helper.ObjectBrowserService
                     *
                     * @description
                     * Get current object Type
                     *
                     * @returns {Object} Current object type.
                     */
                    Service.getCurrentObjectType = function() {
                        var objectSetting = this.getCurrentObjectSetting();
                        return objectSetting.objectType;
                    };

                    var disabledNodes = {};
                    /**
                     *
                     * @param nodeType
                     * @param enabledDisabled
                     * @returns {{}}
                     */
                    Service.toggleNodeDisabled = function(nodeGroup, nodeType, enabledDisabled) {
                        disabledNodes[nodeGroup + nodeType] = enabledDisabled;
                    }
                    /**
                     *
                     * @param nodeType
                     * @returns {boolean}
                     */
                    Service.isNodeDisabled = function(nodeGroup, nodeType) {
                        if (disabledNodes[nodeGroup + nodeType])
                            return true;
                        return false;
                    }

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
                    Service.createComponentLinks = function(config, objType) {
                        var componentLinks = [];
                        var treeConfig = Util.goodMapValue(config, "tree", {});
                        var foundNodeType = _.find(Util.goodMapValue(treeConfig, "nodeTypes", []), {
                            "type": "p/" + objType
                        });
                        if (foundNodeType) {
                            var componentsConfig = Util.goodMapValue(config, "components", []);

                            var leadComponent = Util.goodValue(foundNodeType.leadComponent, "main");
                            var leadConfig = _.find(componentsConfig, {
                                id: leadComponent
                            });
                            if (leadConfig) {
                                componentLinks.push({
                                    id: Util.goodValue(leadConfig.id),
                                    title: Util.goodValue(leadConfig.title),
                                    icon: Util.goodValue(leadConfig.icon)
                                });
                            }

                            _.each(Util.goodMapValue(foundNodeType, "components", []), function(component) {
                                var foundComponent = _.find(componentsConfig, {
                                    id: component
                                });
                                if (foundComponent) {
                                    var title = "";
                                    if (foundComponent.linkTitle) {
                                        title = foundComponent.linkTitle;
                                    } else {
                                        title = foundComponent.title;
                                    }

                                    componentLinks.push({
                                        id: Util.goodValue(foundComponent.id),
                                        title: Util.goodValue(title),
                                        icon: Util.goodValue(foundComponent.icon)
                                    });
                                }
                            });
                        }
                        return componentLinks;
                    };

                    return Service;
                } ]);
