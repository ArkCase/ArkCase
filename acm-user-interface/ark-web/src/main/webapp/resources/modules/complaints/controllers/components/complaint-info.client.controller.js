'use strict';

angular.module('complaints').controller('Complaints.InfoController', ['$scope', '$stateParams'
    , 'UtilService', 'ConfigService', 'Object.LookupService', 'Complaint.LookupService', 'Complaint.InfoService'
    , 'Object.ModelService', 'Helper.ObjectBrowserService'
    , function ($scope, $stateParams
        , Util, ConfigService, ObjectLookupService, ComplaintLookupService, ComplaintInfoService
        , ObjectModelService, HelperObjectBrowserService) {

        ConfigService.getComponentConfig("complaints", "info").then(function (componentConfig) {
            $scope.config = componentConfig;
            return componentConfig;
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


        //$scope.$on('object-selected', function onSelectedComplaint(e, selectedComplaint) {
        //    $scope.complaintSolr = selectedComplaint;
        //});

        $scope.dueDate = null;
        var previousId = null;
        $scope.$on('object-updated', function (e, data) {
            if (!ComplaintInfoService.validateComplaintInfo(data)) {
                return;
            }
            $scope.complaintInfo = data;
            $scope.dueDate = ($scope.complaintInfo) ? moment($scope.complaintInfo.dueDate).toDate() : null;
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
        //var currentObjectId = HelperObjectBrowserService.getCurrentObjectId();
        //if (Util.goodPositive(currentObjectId, false)) {
        //    ComplaintInfoService.getComplaintInfo(currentObjectId).then(function (complaintInfo) {
        //        $scope.complaintInfo = complaintInfo;
        //        $scope.assignee = ObjectModelService.getAssignee(complaintInfo);
        //        $scope.owningGroup = ObjectModelService.getGroup(complaintInfo);
        //        ComplaintLookupService.getApprovers($scope.owningGroup, $scope.assignee).then(function (approvers) {
        //            var options = [];
        //            _.each(approvers, function (approver) {
        //                options.push({id: approver.userId, name: approver.fullName});
        //            });
        //            $scope.assignees = options;
        //            return approvers;
        //        });
        //        return complaintInfo;
        //    });
        //}

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
        $scope.updateDueDate = function (dueDate) {
            $scope.complaintInfo.dueDate = (dueDate) ? moment(dueDate).format($scope.config.dateFormat): null;
            saveComplaint();
        };

    }
]);