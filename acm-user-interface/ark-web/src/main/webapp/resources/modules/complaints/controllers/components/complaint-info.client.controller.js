'use strict';

angular.module('complaints').controller('Complaints.InfoController', ['$scope', '$stateParams', 'UtilService'
    , 'Object.LookupService', 'Complaint.LookupService', 'Complaint.InfoService', 'Object.ModelService'
    , function ($scope, $stateParams, Util
        , ObjectLookupService, ComplaintLookupService, ComplaintInfoService, ObjectModelService) {

        $scope.$emit('req-component-config', 'info');
        $scope.$on('component-config', function (e, componentId, config) {
            if ("info" == componentId) {
                $scope.config = config;
            }
        });

        ObjectLookupService.getPriorities().then(
            function (priorities) {
                var options = [];
                _.each(priorities, function (priority) {
                    options.push({value: priority, text: priority});
                });
                $scope.priorities = options;
                return priorities;
            }
        );

        ObjectLookupService.getGroups().then(
            function (groups) {
                var options = [];
                _.each(groups, function (group) {
                    options.push({value: group.name, text: group.name});
                });
                $scope.owningGroups = options;
                return groups;
            }
        );

        ComplaintLookupService.getComplaintTypes().then(
            function (complaintTypes) {
                var options = [];
                _.forEach(complaintTypes, function (item) {
                    options.push({value: item, text: item});
                });
                $scope.complaintTypes = options;
                return complaintTypes;
            }
        );


        $scope.$on('complaint-selected', function onSelectedComplaint(e, selectedComplaint) {
            $scope.complaintSolr = selectedComplaint;
        });

        var previousId = null;
        $scope.$on('complaint-updated', function (e, data) {
            $scope.complaintInfo = data;
            $scope.assignee = ObjectModelService.getAssignee(data);
            $scope.owningGroup = ObjectModelService.getGroup(data);
            if (previousId != $stateParams.id) {
                ComplaintLookupService.getApprovers($scope.owningGroup, $scope.assignee).then(
                    function (approvers) {
                        var options = [];
                        _.each(approvers, function (approver) {
                            options.push({id: approver.userId, name: approver.fullName});
                        });
                        $scope.assignees = options;
                        return approvers;
                    }
                );
                previousId = $stateParams.id;
            }
        });

        /**
         * Persists the updated complaint metadata to the ArkComplaint data
         */
        function saveComplaint() {
            var complaintInfo = Util.omitNg($scope.complaintInfo);
            if (ComplaintInfoService.validateComplaintInfo(complaintInfo)) {
                ComplaintInfoService.saveComplaintInfo(complaintInfo).then(
                    function (complaintInfo) {
                        //update tree node tittle
                        $scope.$emit("report-complaint-updated", complaintInfo);
                        return complaintInfo;
                    }
                    , function (error) {
                        //set error to x-editable title
                        //update tree node tittle
                        return error;
                    }
                );
            }
        }

        // Updates the ArkComplaint data when the user changes a complaint attribute
        // in a complaint top bar menu item and clicks the save check button
        $scope.updateTitle = function () {
            saveComplaint();
        };
        $scope.updateOwningGroup = function () {
            ObjectModelService.setGroup($scope.complaintInfo, $scope.owningGroup);
            saveComplaint();
        };
        $scope.updatePriority = function () {
            saveComplaint();
        };
        $scope.updateComplaintType = function () {
            saveComplaint();
        };
        $scope.updateAssignee = function () {
            ObjectModelService.setAssignee($scope.complaintInfo, $scope.assignee);
            saveComplaint();
        };
        $scope.updateDueDate = function () {
            saveComplaint();
        };

    }
]);