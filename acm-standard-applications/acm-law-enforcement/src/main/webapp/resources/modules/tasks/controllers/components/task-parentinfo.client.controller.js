'use strict';

angular.module('tasks').controller('Tasks.ParentInfoController', ['$scope', '$stateParams'
    , 'UtilService', 'ConfigService', 'ObjectService', 'Case.InfoService', 'Complaint.InfoService', 'Task.InfoService'
    , 'Object.ModelService', 'LookupService', 'Helper.ObjectBrowserService', '$log', 'Util.DateService'
    , function ($scope, $stateParams
        , Util, ConfigService, ObjectService, CaseInfoService, ComplaintInfoService, TaskInfoService
        , ObjectModelService, LookupService, HelperObjectBrowserService, $log, UtilDateService) {

        new HelperObjectBrowserService.Component({
            moduleId: "tasks"
            , componentId: "parentinfo"
            , scope: $scope
            , stateParams: $stateParams
            , retrieveObjectInfo: TaskInfoService.getTaskInfo
            , validateObjectInfo: TaskInfoService.validateTaskInfo
            , onObjectInfoRetrieved: function (objectInfo) {
                onObjectInfoRetrieved(objectInfo);
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

        $scope.onClickTitle = function () {
            if ($scope.parentCaseInfo) {
                ObjectService.gotoUrl(ObjectService.ObjectTypes.CASE_FILE, $scope.parentCaseInfo.id);
            } else if ($scope.parentComplaintInfo) {
                ObjectService.gotoUrl(ObjectService.ObjectTypes.COMPLAINT, $scope.parentComplaintInfo.complaintId);
            } else {
                $log.error('parentCaseInfo is undefined, cannot redirect to the parent case');
            }
        };

        $scope.defaultDatePickerFormat = UtilDateService.defaultDatePickerFormat;

        var onObjectInfoRetrieved = function (objectInfo) {
            $scope.objectInfo = objectInfo;

            if (Util.isEmpty($scope.objectInfo.attachedToObjectId)) {
                return;
            }

            //for test
            //if (1148 == $scope.objectInfo.taskId) {
            //    $scope.objectInfo.attachedToObjectType = "COMPLAINT";
            //    $scope.objectInfo.attachedToObjectId = 123;
            //}


            if (ObjectService.ObjectTypes.CASE_FILE == $scope.objectInfo.attachedToObjectType) {
                CaseInfoService.getCaseInfo($scope.objectInfo.attachedToObjectId).then(
                    function (caseInfo) {
                        $scope.parentCaseInfo = caseInfo;
                        $scope.owningGroup = ObjectModelService.getGroup(caseInfo);
                        $scope.assignee = ObjectModelService.getAssignee(caseInfo);
                        return caseInfo;
                    }
                );
            } else if (ObjectService.ObjectTypes.COMPLAINT == $scope.objectInfo.attachedToObjectType) {
                ComplaintInfoService.getComplaintInfo($scope.objectInfo.attachedToObjectId).then(
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