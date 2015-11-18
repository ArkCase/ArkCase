'use strict';

angular.module('complaints').controller('Complaints.InfoController', ['$scope', '$stateParams', 'StoreService', 'UtilService', 'ValidationService', 'HelperService', 'LookupService', 'Complaint.InfoService', 'ObjectsModelsService',
    function ($scope, $stateParams, Store, Util, Validator, Helper, LookupService, ComplaintInfoService, ObjectsModelsService) {
        $scope.$emit('req-component-config', 'info');
        $scope.$on('component-config', function (e, componentId, config) {
            if ("info" == componentId) {
                $scope.config = config;
            }
        });
        var z = 1;
        return;


        // Obtains the dropdown menu selection options via REST calls to ArkCase
        $scope.priorities = [];
        var cachePriorities = new Store.SessionData(Helper.SessionCacheNames.PRIORITIES);
        var priorities = cachePriorities.get();
        Util.serviceCall({
            service: LookupService.getPriorities
            , result: priorities
            , onSuccess: function (data) {
                if (Validator.validatePriorities(data)) {
                    priorities = data;
                    cachePriorities.set(priorities);
                    return priorities;
                }
            }
        }).then(
            function (priorities) {
                var options = [];
                _.each(priorities, function (priority) {
                    options.push({value: priority, text: priority});
                });
                $scope.priorities = options;
                return priorities;
            }
        );
        $scope.assignableUsers = [];
        var cacheUsers = new Store.SessionData(Helper.SessionCacheNames.USERS);
        var users = cacheUsers.get();
        Util.serviceCall({
            service: LookupService.getUsers
            , result: users
            , onSuccess: function (data) {
                if (Validator.validateUsers(data)) {
                    users = data;
                    cacheUsers.set(users);
                    return users;
                }
            }
        }).then(
            function (users) {
                var options = [];
                _.each(users, function (user) {
                    var userInfo = JSON.parse(user);
                    options.push({value: userInfo.object_id_s, text: userInfo.object_id_s});
                });
                $scope.assignableUsers = options;
                return users;
            }
        );

        $scope.owningGroups = [];
        var cacheGroups = new Store.SessionData(Helper.SessionCacheNames.GROUPS);
        var groups = cacheGroups.get();
        Util.serviceCall({
            service: LookupService.getGroups
            , result: groups
            , onSuccess: function (data) {
                if (Validator.validateSolrData(data)) {
                    var groups = data.response.docs;
                    cacheGroups.set(groups);
                    return groups;
                }
            }
        }).then(
            function (groups) {
                var options = [];
                _.each(groups, function (item) {
                    options.push({value: item.name, text: item.name});
                });
                $scope.owningGroups = options;
                return groups;
            }
        );

        $scope.complaintTypes = [];
        var cacheComplaintTypes = new Store.SessionData(Helper.SessionCacheNames.CASE_TYPES);
        var complaintTypes = cacheComplaintTypes.get();
        Util.serviceCall({
            service: LookupService.getComplaintTypes
            , result: complaintTypes
            , onSuccess: function (data) {
                if (Validator.validateComplaintTypes(data)) {
                    complaintTypes = data;
                    cacheComplaintTypes.set(complaintTypes);
                    return complaintTypes;
                }
            }
        }).then(
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
            if (Validator.validateComplaint(data)) {
                $scope.complaintInfo = data;
                $scope.assignee = ObjectsModelsService.getAssignee(data);
                $scope.owningGroup = ObjectsModelsService.getGroup(data);
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
            ObjectsModelsService.setGroup($scope.complaintInfo, $scope.owningGroup);
            saveComplaint();
        };
        $scope.updatePriority = function () {
            saveComplaint();
        };
        $scope.updateComplaintType = function () {
            saveComplaint();
        };
        $scope.updateAssignee = function () {
            ObjectsModelsService.setAssignee($scope.complaintInfo, $scope.assignee);
            saveComplaint();
        };
        $scope.updateDueDate = function () {
            saveComplaint();
        };

    }
]);