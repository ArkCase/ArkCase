'use strict';

angular.module('time-tracking').controller('TimeTrackingListController', ['$scope', '$state', '$stateParams', '$translate', 'ConfigService', 'UtilService', 'ConstantService', 'TimeTrackingService', 'CallTimeTrackingService', 'ValidationService', 'Profile.UserInfoService', 'HelperService', 'CallConfigService', 'CallAuthentication',
    function($scope, $state, $stateParams, $translate, ConfigService, Util, Constant, TimeTrackingService, CallTimeTrackingService, Validator, UserInfoService, Helper, CallConfigService, CallAuthentication) {
        CallConfigService.getModuleConfig("time-tracking").then(function (config) {
            $scope.treeConfig = config.tree;
            $scope.componentsConfig = config.components;
            return config;
        });

        var firstLoad = true;
        $scope.onLoad = function (start, n, sort, filters) {
            if (firstLoad && $stateParams.id) {
                $scope.treeData = null;
            }
            CallAuthentication.queryUserInfo().then(function (data){
                var userId = data.userId;
                CallTimeTrackingService.queryTimeTrackingTreeData(userId, start, n, sort).then(
                    function (treeData) {
                        if (firstLoad) {
                            if ($stateParams.id) {
                                if ($scope.treeData) {

                                    var found = _.find(treeData.docs, {nodeId: $scope.treeData.docs[0].nodeId});
                                    if (!found) {
                                        var clone = _.clone(treeData.docs);
                                        clone.unshift($scope.treeData.docs[0]);
                                        treeData.docs = clone;
                                    }
                                    firstLoad = false;
                                }


                            } else {
                                if (0 < treeData.docs.length) {
                                    var selectNode = treeData.docs[0];
                                    $scope.treeControl.select({
                                        pageStart: start
                                        , nodeType: selectNode.nodeType
                                        , nodeId: selectNode.nodeId
                                    });
                                }
                                firstLoad = false;
                            }
                        }

                        $scope.treeData = treeData;
                        return treeData;
                    }
                );
            });
            if (firstLoad && $stateParams.id) {
                CallTimeTrackingService.getTimeTrackingInfo($stateParams.id).then(
                    function (timesheetInfo) {
                        $scope.treeControl.select({
                            pageStart: start
                            , nodeType: Constant.ObjectTypes.TIMESHEET
                            , nodeId: timesheetInfo.id
                        });

                        var treeData = {docs: [], total: 0};
                        if ($scope.treeData) {
                            var found = _.find($scope.treeData.docs, {nodeId: timesheetInfo.id});
                            if (!found) {
                                treeData.docs = _.clone($scope.treeData.docs);
                                treeData.total = $scope.treeData.total;
                                treeData.docs.unshift({
                                    nodeId: Util.goodValue(timesheetInfo.id, 0)
                                    , nodeType: Constant.ObjectTypes.TIMESHEET
                                    , nodeTitle: Util.goodValue(timesheetInfo.title)
                                    , nodeToolTip: Util.goodValue(timesheetInfo.title)
                                });
                            }
                            firstLoad = false;

                        } else {
                            treeData.total = 1;
                            treeData.docs.unshift({
                                nodeId: Util.goodValue(timesheetInfo.id, 0)
                                , nodeType: Constant.ObjectTypes.TIMESHEET
                                , nodeTitle: Util.goodValue(timesheetInfo.title)
                                , nodeToolTip: Util.goodValue(timesheetInfo.title)
                            });
                        }

                        $scope.treeData = treeData;
                        return timesheetInfo;
                    }
                    , function (errorData) {
                        $scope.treeControl.select({
                            pageStart: start
                            , nodeType: Constant.ObjectTypes.TIMESHEET
                            , nodeId: $stateParams.id
                        });


                        var treeData = {docs: [], total: 0};
                        if ($scope.treeData) {            //It must be set by CallCasesService.queryCasesTreeData()
                            var found = _.find($scope.treeData.docs, {nodeId: $stateParams.id});
                            if (!found) {
                                treeData.docs = _.clone($scope.treeData.docs);
                                treeData.total = $scope.treeData.total;
                                treeData.docs.unshift({
                                    nodeId: $stateParams.id
                                    , nodeType: Constant.ObjectTypes.TIMESHEET
                                    , nodeTitle: $translate.instant("common.directive.objectTree.errorNode.title")
                                    , nodeToolTip: $translate.instant("common.directive.objectTree.errorNode.toolTip")
                                });
                            }
                            firstLoad = false;

                        } else {
                            treeData.total = 1;
                            treeData.docs.unshift({
                                nodeId: $stateParams.id
                                , nodeType: Constant.ObjectTypes.TIMESHEET
                                , nodeTitle: $translate.instant("common.directive.objectTree.errorNode.title")
                                , nodeToolTip: $translate.instant("common.directive.objectTree.errorNode.toolTip")
                            });
                        }

                        $scope.treeData = treeData;
                        return errorData;
                    }
                );
            }

        };

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