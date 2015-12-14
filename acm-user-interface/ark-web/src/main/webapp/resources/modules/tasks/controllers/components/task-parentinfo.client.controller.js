'use strict';

angular.module('tasks').controller('Tasks.ParentInfoController', ['$scope', '$stateParams'
    , 'UtilService', 'ConfigService', 'ObjectService', 'Case.InfoService', 'Complaint.InfoService', 'Task.InfoService'
    , 'Object.ModelService'
    , function ($scope, $stateParams
        , Util, ConfigService, ObjectService, CaseInfoService, ComplaintInfoService, TaskInfoService
        , ObjectModelService) {

        ConfigService.getComponentConfig("tasks", "parentinfo").then(function (componentConfig) {
            $scope.config = componentConfig;
            return componentConfig;
        });

        $scope.$on('task-updated', function (e, data) {
            if (!TaskInfoService.validateTaskInfo(data)) {
                return;
            }
            $scope.taskInfo = data;
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
        });

    }
]);