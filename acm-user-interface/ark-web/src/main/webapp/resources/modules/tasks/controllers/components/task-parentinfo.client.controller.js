'use strict';

angular.module('tasks').controller('Tasks.ParentInfoController', ['$scope', '$stateParams', 'UtilService', 'ConstantService', 'Case.InfoService', 'Complaint.InfoService', 'Object.ModelService',
    function ($scope, $stateParams, Util, Constant, CaseInfoService, ComplaintInfoService, ObjectModelService) {
        $scope.$emit('req-component-config', 'parentinfo');
        $scope.$on('component-config', function (e, componentId, config) {
            if ("parentinfo" == componentId) {
                $scope.config = config;
            }
        });

        $scope.$on('task-updated', function (e, data) {
            $scope.taskInfo = data;
            if (Util.isEmpty($scope.taskInfo.parentObjectId)) {
                return;
            }

            //for test
            //if (1148 == $scope.taskInfo.taskId) {
            //    $scope.taskInfo.parentObjectType = "COMPLAINT";
            //    $scope.taskInfo.parentObjectId = 123;
            //}


            if (Constant.ObjectTypes.CASE_FILE == $scope.taskInfo.parentObjectType) {
                CaseInfoService.getCaseInfo($scope.taskInfo.parentObjectId).then(
                    function (caseInfo) {
                        $scope.parentCaseInfo = caseInfo;
                        $scope.owningGroup = ObjectModelService.getGroup(caseInfo);
                        $scope.assignee = ObjectModelService.getAssignee(caseInfo);
                        return caseInfo;
                    }
                );
            } else if (Constant.ObjectTypes.COMPLAINT == $scope.taskInfo.parentObjectType) {
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