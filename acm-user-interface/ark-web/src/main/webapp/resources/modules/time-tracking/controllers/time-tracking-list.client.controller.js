'use strict';


angular.module('time-tracking').controller('TimeTrackingListController', ['$scope', '$state', '$stateParams', '$q', '$translate'
    , 'Authentication', 'UtilService', 'ObjectService', 'Helper.ObjectBrowserService'
    , 'TimeTracking.ListService', 'TimeTracking.InfoService'
    , function ($scope, $state, $stateParams, $q, $translate
        , Authentication, Util, ObjectService, HelperObjectBrowserService
        , TimeTrackingListService, TimeTrackingInfoService) {

        //"treeConfig", "treeData", "onLoad", and "onSelect" will be set by Tree Helper
        new HelperObjectBrowserService.Tree({
            scope: $scope
            , state: $state
            , stateParams: $stateParams
            , moduleId: "time-tracking"
            , resetTreeData: function () {
                return TimeTrackingListService.resetTimeTrackingTreeData();
            }
            , getTreeData: function (start, n, sort, filters) {
                var dfd = $q.defer();
                Authentication.queryUserInfo().then(
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
                return TimeTrackingInfoService.getTimesheetInfo(timesheetId);
            }
            , makeTreeNode: function (timesheetInfo) {
                return {
                    nodeId: Util.goodValue(timesheetInfo.id, 0)
                    , nodeType: ObjectService.ObjectTypes.TIMESHEET
                    , nodeTitle: Util.goodValue(timesheetInfo.title)
                    , nodeToolTip: Util.goodValue(timesheetInfo.title)
                };
            }
        });

    }
]);