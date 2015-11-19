'use strict';

angular.module('complaints').controller('Complaints.InfoController', ['$scope', '$stateParams', 'UtilService'
    , 'LookupService', 'Object.LookupService', 'Complaint.LookupService'
    , 'Complaint.InfoService', 'Object.ModelService'
    , function ($scope, $stateParams, Util
        , LookupService, ObjectLookupService, ComplaintLookupService
        , ComplaintInfoService, ObjectModelService) {

        $scope.$emit('req-component-config', 'info');
        $scope.$on('component-config', function (e, componentId, config) {
            if ("info" == componentId) {
                $scope.config = config;
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

        ObjectLookupService.getOwningGroups().then(
            function (groups) {
                var options = [];
                _.each(groups, function (item) {
                    options.push({value: item.name, text: item.name});
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


        $scope.complaintSolr = null;
        $scope.complaintInfo = null;
        $scope.$on('complaint-selected', function onSelectedComplaint(e, selectedComplaint) {
            $scope.complaintSolr = selectedComplaint;
        });
        $scope.assignee = null;
        $scope.owningGroup = null;
        $scope.$on('complaint-updated', function (e, data) {
            $scope.complaintInfo = data;
            $scope.assignee = ObjectModelService.getAssignee(data);
            $scope.owningGroup = ObjectModelService.getGroup(data);
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