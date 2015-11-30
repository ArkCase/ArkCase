'use strict';

angular.module('time-tracking').controller('TimeTrackingListController', ['$scope', '$state', '$stateParams', '$q', '$translate', 'ConfigService', 'UtilService', 'ConstantService', 'TimeTracking.ListService', 'TimeTracking.InfoService', 'Profile.UserInfoService', 'HelperService', 'Authentication', 'Helper.ObjectTreeService',
    function ($scope, $state, $stateParams, $q, $translate, ConfigService, Util, Constant, TimeTrackingListService, TimeTrackingInfoService, UserInfoService, Helper, Authentication, HelperObjectTreeService) {
        ConfigService.getModuleConfig("time-tracking").then(function (config) {
            $scope.treeConfig = config.tree;
            $scope.componentsConfig = config.components;
            return config;
        });

        var treeHelper = new HelperObjectTreeService.Tree({
            scope: $scope
            , nodeId: $stateParams.id
            , getTreeData: function (start, n, sort, filters) {
                var dfd = $q.defer();
                Authentication.queryUserInfoNew().then(
                    function (userInfo) {
                        var userId = userInfo.userId;
                        TimeTrackingListService.queryTimeTrackingTreeData(userId, start, n, sort, filters).then(
                            function (treeData) {
                                dfd.resolve(treeData);
                                return treeData;
                            }
                            , function (error) {
                                dfd.reject(error);
                                return error;
                            }
                        );
                        return userInfo;
                    }
                    , function (error) {
                        dfd.reject(error);
                        return error;
                    }
                );
                return dfd.promise;
            }
            , getNodeData: function (timesheetId) {
                return TimeTrackingInfoService.getTimeTrackingInfo(timesheetId);
            }
            , makeTreeNode: function (timesheetInfo) {
                return {
                    nodeId: Util.goodValue(timesheetInfo.id, 0)
                    , nodeType: Constant.ObjectTypes.TIMESHEET
                    , nodeTitle: Util.goodValue(timesheetInfo.title)
                    , nodeToolTip: Util.goodValue(timesheetInfo.title)
                };
            }
        });
        $scope.onLoad = function (start, n, sort, filters) {
            treeHelper.onLoad(start, n, sort, filters);
        };

        //var firstLoad = true;
        //$scope.onLoad = function (start, n, sort, filters) {
        //    if (firstLoad && $stateParams.id) {
        //        $scope.treeData = null;
        //    }
        //    Authentication.queryUserInfoNew().then(function (userInfo) {
        //        var userId = userInfo.userId;
        //        CallTimeTrackingService.queryTimeTrackingTreeData(userId, start, n, sort).then(
        //            function (treeData) {
        //                if (firstLoad) {
        //                    if ($stateParams.id) {
        //                        if ($scope.treeData) {
        //
        //                            var found = _.find(treeData.docs, {nodeId: $scope.treeData.docs[0].nodeId});
        //                            if (!found) {
        //                                var clone = _.clone(treeData.docs);
        //                                clone.unshift($scope.treeData.docs[0]);
        //                                treeData.docs = clone;
        //                            }
        //                            firstLoad = false;
        //                        }
        //
        //
        //                    } else {
        //                        if (0 < treeData.docs.length) {
        //                            var selectNode = treeData.docs[0];
        //                            $scope.treeControl.select({
        //                                pageStart: start
        //                                , nodeType: selectNode.nodeType
        //                                , nodeId: selectNode.nodeId
        //                            });
        //                        }
        //                        firstLoad = false;
        //                    }
        //                }
        //
        //                $scope.treeData = treeData;
        //                return treeData;
        //            }
        //        );
        //        return userInfo;
        //    });
        //    if (firstLoad && $stateParams.id) {
        //        CallTimeTrackingService.getTimeTrackingInfo($stateParams.id).then(
        //            function (timesheetInfo) {
        //                $scope.treeControl.select({
        //                    pageStart: start
        //                    , nodeType: Constant.ObjectTypes.TIMESHEET
        //                    , nodeId: timesheetInfo.id
        //                });
        //
        //                var treeData = {docs: [], total: 0};
        //                if ($scope.treeData) {
        //                    var found = _.find($scope.treeData.docs, {nodeId: timesheetInfo.id});
        //                    if (!found) {
        //                        treeData.docs = _.clone($scope.treeData.docs);
        //                        treeData.total = $scope.treeData.total;
        //                        treeData.docs.unshift({
        //                            nodeId: Util.goodValue(timesheetInfo.id, 0)
        //                            , nodeType: Constant.ObjectTypes.TIMESHEET
        //                            , nodeTitle: Util.goodValue(timesheetInfo.title)
        //                            , nodeToolTip: Util.goodValue(timesheetInfo.title)
        //                        });
        //                    }
        //                    firstLoad = false;
        //
        //                } else {
        //                    treeData.total = 1;
        //                    treeData.docs.unshift({
        //                        nodeId: Util.goodValue(timesheetInfo.id, 0)
        //                        , nodeType: Constant.ObjectTypes.TIMESHEET
        //                        , nodeTitle: Util.goodValue(timesheetInfo.title)
        //                        , nodeToolTip: Util.goodValue(timesheetInfo.title)
        //                    });
        //                }
        //
        //                $scope.treeData = treeData;
        //                return timesheetInfo;
        //            }
        //            , function (errorData) {
        //                $scope.treeControl.select({
        //                    pageStart: start
        //                    , nodeType: Constant.ObjectTypes.TIMESHEET
        //                    , nodeId: $stateParams.id
        //                });
        //
        //
        //                var treeData = {docs: [], total: 0};
        //                if ($scope.treeData) {            //It must be set by CallCasesService.queryCasesTreeData()
        //                    var found = _.find($scope.treeData.docs, {nodeId: $stateParams.id});
        //                    if (!found) {
        //                        treeData.docs = _.clone($scope.treeData.docs);
        //                        treeData.total = $scope.treeData.total;
        //                        treeData.docs.unshift({
        //                            nodeId: $stateParams.id
        //                            , nodeType: Constant.ObjectTypes.TIMESHEET
        //                            , nodeTitle: $translate.instant("common.directive.objectTree.errorNode.title")
        //                            , nodeToolTip: $translate.instant("common.directive.objectTree.errorNode.toolTip")
        //                        });
        //                    }
        //                    firstLoad = false;
        //
        //                } else {
        //                    treeData.total = 1;
        //                    treeData.docs.unshift({
        //                        nodeId: $stateParams.id
        //                        , nodeType: Constant.ObjectTypes.TIMESHEET
        //                        , nodeTitle: $translate.instant("common.directive.objectTree.errorNode.title")
        //                        , nodeToolTip: $translate.instant("common.directive.objectTree.errorNode.toolTip")
        //                    });
        //                }
        //
        //                $scope.treeData = treeData;
        //                return errorData;
        //            }
        //        );
        //    }
        //
        //};

        $scope.onSelect = function (selectedTimesheet) {
            $scope.$emit('req-select-timesheet', selectedTimesheet);
            var components = Util.goodArray(selectedTimesheet.components);
            var componentType = (1 == components.length) ? components[0] : "main";
            $state.go('time-tracking.' + componentType, {
                id: selectedTimesheet.nodeId
            });
        };
    }
]);