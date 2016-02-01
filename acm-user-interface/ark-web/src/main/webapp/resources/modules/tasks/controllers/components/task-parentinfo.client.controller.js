'use strict';

angular.module('tasks').controller('Tasks.ParentInfoController', ['$scope', '$stateParams'
    , 'UtilService', 'ConfigService', 'ObjectService', 'Case.InfoService', 'Complaint.InfoService', 'Task.InfoService'
    , 'Object.ModelService', 'LookupService', 'Helper.ObjectBrowserService', '$log'
    , function ($scope, $stateParams
        , Util, ConfigService, ObjectService, CaseInfoService, ComplaintInfoService, TaskInfoService
        , ObjectModelService, LookupService, HelperObjectBrowserService, $log) {

        new HelperObjectBrowserService.Component({
            moduleId: "tasks"
            , componentId: "parentinfo"
            , scope: $scope
            , stateParams: $stateParams
            , retrieveObjectInfo: TaskInfoService.getTaskInfo
            , validateObjectInfo: TaskInfoService.validateTaskInfo
            , onObjectInfoRetrieved: function (taskInfo) {
                onObjectInfoRetrieved(taskInfo);
            }
        });

        LookupService.getUsers().then(
            function (users) {
                var options = [];
                _.each(users, function (user) {
                    options.push({object_id_s: user.object_id_s, name: user.name});
                });
                $scope.assignableUsers = options;
                return users;
            }
        );

        $scope.onClickTitle = function() {
            if ($scope.parentCaseInfo) {
                ObjectService.gotoUrl(ObjectService.ObjectTypes.CASE_FILE, $scope.parentCaseInfo.id);
            } else if ($scope.parentComplaintInfo) {
                ObjectService.gotoUrl(ObjectService.ObjectTypes.COMPLAINT, $scope.parentComplaintInfo.complaintId);
            } else {
                $log.error('parentCaseInfo is undefined, cannot redirect to the parent case');
            }
        };


        var onObjectInfoRetrieved = function (taskInfo) {
            $scope.taskInfo = taskInfo;

            if (Util.isEmpty($scope.taskInfo.parentObjectId)) {
                return;
            }

            //for test
            //if (1148 == $scope.taskInfo.taskId) {
            //    $scope.taskInfo.parentObjectType = "COMPLAINT";
            //    $scope.taskInfo.parentObjectId = 123;
            //}


            if (ObjectService.ObjectTypes.CASE_FILE == $scope.taskInfo.parentObjectType) {
                CaseInfoService.getCaseInfo($scope.taskInfo.parentObjectId).then(
                    function (caseInfo) {
                        $scope.parentCaseInfo = caseInfo;
                        $scope.owningGroup = ObjectModelService.getGroup(caseInfo);
                        $scope.assignee = ObjectModelService.getAssignee(caseInfo);
                        return caseInfo;
                    }
                );
            } else if (ObjectService.ObjectTypes.COMPLAINT == $scope.taskInfo.parentObjectType) {
                ComplaintInfoService.getComplaintInfo($scope.taskInfo.parentObjectId).then(
                    function (complaintInfo) {
                        $scope.parentComplaintInfo = complaintInfo;
                        $scope.owningGroup = ObjectModelService.getGroup(complaintInfo);
                        $scope.assignee = ObjectModelService.getAssignee(complaintInfo);
                        return complaintInfo;
                    }
                );
            }
        };

    }
]);