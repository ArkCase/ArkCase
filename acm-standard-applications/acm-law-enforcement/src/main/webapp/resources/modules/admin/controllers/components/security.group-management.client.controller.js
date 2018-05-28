'use strict';

angular.module('admin').controller('Admin.GroupManagementController', [ '$scope', '$q', 'Admin.GroupManagementService', 'MessageService', 'UtilService', '$log', '$translate', function($scope, $q, GroupManagementService, MessageService, Util, $log, $translate) {

    $scope.onObjSelect = onObjSelect;
    $scope.onAuthRoleSelected = onAuthRoleSelected;
    $scope.initGroup = initGroup;
    $scope.fillList = fillList;
    $scope.retrieveDataFilter = retrieveDataFilter;
    //scroll functions
    $scope.groupScroll = groupScroll;
    $scope.unauthorizedScroll = unauthorizedScroll;
    $scope.authorizedScroll = authorizedScroll;
    $scope.retrieveDataScroll = retrieveDataScroll;
    //filter functions
    $scope.groupManagementFilter = groupManagementFilter;
    $scope.groupUnauthorizedFilter = groupUnauthorizedFilter;
    $scope.groupAuthorizedFilter = groupAuthorizedFilter;

    var makePaginationRequest = true;
    var currentAuthGroups;
    $scope.objectTitle = $translate.instant('admin.security.group.management.group');
    $scope.lastSelectedGroup = {};
    $scope.userData = {
        "chooseObject": [],
        "selectedNotAuthorized": [],
        "selectedAuthorized": []
    };
    $scope.scrollLoadData = {
        "loadObjectsScroll": $scope.groupScroll,
        "loadUnauthorizedScroll": $scope.unauthorizedScroll,
        "loadAuthorizedScroll": $scope.authorizedScroll
    };
    $scope.filterData = {
        "objectsFilter": $scope.groupManagementFilter,
        "unauthorizedFilter": $scope.groupUnauthorizedFilter,
        "authorizedFilter": $scope.groupAuthorizedFilter
    };

    function initGroup(userNumber) {
        if (makePaginationRequest) {
            var userRequestInfo = {};
            userRequestInfo.start = Util.isEmpty(userNumber) ? 0 : $scope.userData.chooseObject.length;
            GroupManagementService.getAdHocGroups(userRequestInfo).then(function(response) {
                $scope.fillList($scope.userData.chooseObject, response.data.response.docs);
                if (_.isEmpty($scope.lastSelectedGroup)) {
                    $scope.lastSelectedGroup = $scope.userData.chooseObject[0];
                    $scope.onObjSelect($scope.lastSelectedGroup);
                }
                makePaginationRequest = response.data.response.numFound > $scope.userData.chooseObject.length;
            });
        }
    }

    $scope.initGroup();

    //callback function when user is selected
    function onObjSelect(selectedObject) {
        $scope.userData.selectedAuthorized = [];
        $scope.userData.selectedNotAuthorized = [];

        if (!_.isEmpty($scope.userData.chooseObject)) {
            var data = {};
            data.member = selectedObject;
            data.isAuthorized = false;
            var unAuthorizedGroupsForUser = GroupManagementService.getAdhocGroupsForAdhocGroup(data);
            data.isAuthorized = true;
            var authorizedGroupsForUser = GroupManagementService.getAdhocGroupsForAdhocGroup(data);
            currentAuthGroups = [];
            $scope.lastSelectedGroup = angular.copy(selectedObject);
            $q.all([ authorizedGroupsForUser, unAuthorizedGroupsForUser ]).then(function(result) {
                _.forEach(result[0].data.response.docs, function(group) {
                    var authObject = {};
                    authObject.key = group.name;
                    authObject.name = group.name;
                    $scope.userData.selectedAuthorized.push(authObject);
                    currentAuthGroups.push(authObject.key);
                });
                _.forEach(result[1].data.response.docs, function(group) {
                    var authObject = {};
                    authObject.key = group.name;
                    authObject.name = group.name;
                    $scope.userData.selectedNotAuthorized.push(authObject);
                });
            });
        }
    }

    //callback function when groups are moved
    function onAuthRoleSelected(selectedObject, authorized, notAuthorized) {
        var toBeAdded = [];
        var toBeRemoved = [];
        var deferred = $q.defer();

        //get roles which needs to be added
        _.forEach(authorized, function(group) {
            if (currentAuthGroups.indexOf(group.key) === -1) {
                toBeAdded.push(group.key);
            }
        });
        _.forEach(notAuthorized, function(group) {
            if (currentAuthGroups.indexOf(group.key) !== -1) {
                toBeRemoved.push(group.key);
            }
        });
        //perform adding on server
        if (toBeAdded.length > 0) {
            currentAuthGroups = currentAuthGroups.concat(toBeAdded);
            var data = {
                parentId: selectedObject.key,
                groups: toBeAdded
            };

            GroupManagementService.addGroupSubGroups(data).then(function(data) {
                MessageService.succsessAction();
            }, function() {
                //error adding group
                MessageService.errorAction();
            });
            return deferred.promise;
        }

        if (toBeRemoved.length > 0) {
            _.forEach(toBeRemoved, function(element) {
                currentAuthGroups.splice(currentAuthGroups.indexOf(element), 1);
            });
            var data = {
                parentId: selectedObject.key,
                groups: toBeRemoved
            };

            GroupManagementService.deleteGroupSubGroups(data).then(function(data) {
                MessageService.succsessAction();
            }, function() {
                //error adding group
                MessageService.errorAction();
            });
            return deferred.promise;
        }
    }

    function fillList(listToFill, data) {
        _.forEach(data, function(obj) {
            var element = {};
            element.name = obj.name;
            element.key = obj.object_id_s;
            element.directory = obj.directory_name_s;
            element.type = obj.object_sub_type_s;
            listToFill.push(element);
        });
    }

    function retrieveDataScroll(data, methodName, panelName) {
        GroupManagementService[methodName](data).then(function(response) {
            if (_.isArray(response.data)) {
                $scope.fillList($scope.userData[panelName], response.data);
            } else {
                $scope.fillList($scope.userData[panelName], response.data.response.docs);
            }
            if (panelName === "selectedAuthorized") {
                currentAuthGroups = [];
                _.forEach($scope.userData[panelName], function(obj) {
                    currentAuthGroups.push(obj.key);
                });
            }
        }, function() {
            $log.error('Error during calling the method ' + methodName);
        });
    }

    function groupScroll() {
        $scope.initGroup($scope.userData.chooseObject.length * 2);
    }

    function unauthorizedScroll() {
        var data = {};
        data.member = $scope.lastSelectedGroup;
        data.start = $scope.userData.selectedNotAuthorized.length;
        data.isAuthorized = false;
        $scope.retrieveDataScroll(data, "getAdhocGroupsForAdhocGroup", "selectedNotAuthorized");
    }

    function authorizedScroll() {
        var data = {};
        data.member = $scope.lastSelectedGroup;
        data.start = $scope.userData.selectedAuthorized.length;
        data.isAuthorized = true;
        $scope.retrieveDataScroll(data, "getAdhocGroupsForAdhocGroup", "selectedAuthorized");
    }

    function retrieveDataFilter(data, methodName, panelName) {
        GroupManagementService[methodName](data).then(function(response) {
            $scope.userData[panelName] = [];
            if (_.isArray(response.data)) {
                $scope.fillList($scope.userData[panelName], response.data);
            } else {
                $scope.fillList($scope.userData[panelName], response.data.response.docs);
            }
            if (methodName === "getAdHocGroups" || methodName === "getGroupsAdhocFiltered") {
                $scope.onObjSelect($scope.userData.chooseObject[0]);
            }
        }, function() {
            $log.error('Error during calling the method ' + methodName);
        });
    }

    function groupManagementFilter(data) {
        if (Util.isEmpty(data.filterWord)) {
            data.n = Util.isEmpty(data.n) ? 50 : data.n;
            $scope.retrieveDataFilter(data, "getAdHocGroups", "chooseObject");
        } else {
            $scope.retrieveDataFilter(data, "getGroupsAdhocFiltered", "chooseObject");
        }
    }

    function groupUnauthorizedFilter(data) {
        data.member = $scope.lastSelectedGroup;
        data.isAuthorized = false;
        if (Util.isEmpty(data.filterWord)) {
            $scope.retrieveDataFilter(data, "getAdhocGroupsForAdhocGroup", "selectedNotAuthorized");
        } else {
            $scope.retrieveDataFilter(data, "getAdhocGroupsForAdhocGroupFiltered", "selectedNotAuthorized");
        }
    }

    function groupAuthorizedFilter(data) {
        data.member = $scope.lastSelectedGroup;
        data.isAuthorized = true;
        if (Util.isEmpty(data.filterWord)) {
            data.n = Util.isEmpty(data.n) ? 50 : data.n;
            $scope.retrieveDataFilter(data, "getAdhocGroupsForAdhocGroup", "selectedAuthorized");
        } else {
            $scope.retrieveDataFilter(data, "getAdhocGroupsForAdhocGroupFiltered", "selectedAuthorized");
        }
    }

} ]);
