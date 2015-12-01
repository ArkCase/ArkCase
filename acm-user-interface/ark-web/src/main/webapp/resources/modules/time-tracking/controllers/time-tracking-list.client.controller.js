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