'use strict';

angular.module('tasks').controller('Tasks.ParentInfoController', ['$scope', '$stateParams', 'UtilService', 'ConstantService', 'CallTasksService', 'CallCasesService', 'CallComplaintsService', 'ObjectsModelsService',
    function ($scope, $stateParams, Util, Constant, CallTasksService, CallCasesService, CallComplaintsService, ObjectsModelsService) {
        $scope.$emit('req-component-config', 'parentinfo');
        $scope.$on('component-config', function (e, componentId, config) {
            if ("parentinfo" == componentId) {
                $scope.config = config;
            }
        });

        $scope.$on('task-retrieved', function (e, data) {
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
                CallCasesService.getCaseInfo($scope.taskInfo.parentObjectId).then(
                    function (caseInfo) {
                        $scope.parentCaseInfo = caseInfo;
                        $scope.owningGroup = ObjectsModelsService.getGroup(caseInfo);
                        $scope.assignee = ObjectsModelsService.getAssignee(caseInfo);
                        return caseInfo;
                    }
                );
            } else if (Constant.ObjectTypes.COMPLAINT == $scope.taskInfo.parentObjectType) {
                CallComplaintsService.getComplaintInfo($scope.taskInfo.parentObjectId).then(
                    function (complaintInfo) {
                        $scope.parentComplaintInfo = complaintInfo;
                        $scope.owningGroup = ObjectsModelsService.getGroup(complaintInfo);
                        $scope.assignee = ObjectsModelsService.getAssignee(complaintInfo);
                        return complaintInfo;
                    }
                );
            }
        });

    }
]);