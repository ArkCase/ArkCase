'use strict';

angular.module('cases').controller('Cases.InfoController', ['$scope', '$stateParams', 'StoreService', 'UtilService', 'ValidationService', 'HelperService', 'LookupService', 'CallCasesService', 'CasesService', 'CasesModelsService',
    function ($scope, $stateParams, Store, Util, Validator, Helper, LookupService, CallCasesService, CasesService, CasesModelsService) {
        $scope.$emit('req-component-config', 'info');
        $scope.$on('component-config', function (e, componentId, config) {
            if ("info" == componentId) {
                $scope.config = config;
            }
        });


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
                    options.push({object_id_s: userInfo.object_id_s, name: userInfo.name});
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

        $scope.caseTypes = [];
        var cacheCaseTypes = new Store.SessionData(Helper.SessionCacheNames.CASE_TYPES);
        var caseTypes = cacheCaseTypes.get();
        Util.serviceCall({
            service: LookupService.getCaseTypes
            , result: caseTypes
            , onSuccess: function (data) {
                if (Validator.validateCaseTypes(data)) {
                    caseTypes = data;
                    cacheCaseTypes.set(caseTypes);
                    return caseTypes;
                }
            }
        }).then(
            function (caseTypes) {
                var options = [];
                _.forEach(caseTypes, function (item) {
                    options.push({value: item, text: item});
                });
                $scope.caseTypes = options;
                return caseTypes;
            }
        );

        $scope.caseSolr = null;
        $scope.caseInfo = null;
        $scope.$on('case-selected', function onSelectedCase(e, selectedCase) {
            $scope.caseSolr = selectedCase;
        });
        $scope.assignee = null;
        $scope.owningGroup = null;
        $scope.$on('case-retrieved', function(e, data){
            $scope.caseInfo = data;
            $scope.assignee = CasesModelsService.getAssignee(data);
            $scope.owningGroup = CasesModelsService.getGroup(data);
        });

        /**
         * Persists the updated casefile metadata to the ArkCase database
         */
        function saveCase() {
            if (CallCasesService.validateCaseFile($scope.caseInfo)) {
                var caseInfo = Util.omitNg($scope.caseInfo);
                Util.serviceCall({
                    service: CasesService.save
                    , data: caseInfo
                    , onSuccess: function (data) {
                        return data;
                    }
                }).then(
                    function (successData) {
                        //notify "case saved successfully" ?
                    }
                    , function (errorData) {
                        //handle error
                    }
                );
            }

            //var caseInfo = Util.omitNg($scope.caseInfo);
            //CasesService.save({}, caseInfo
            //    ,function(successData) {
            //        $log.debug("case saved successfully");
            //    }
            //    ,function(errorData) {
            //        $log.error("case save failed");
            //    }
            //);
        }

        // Updates the ArkCase database when the user changes a case attribute
        // in a case top bar menu item and clicks the save check button
        $scope.updateTitle = function() {
            saveCase();
        };
        $scope.updateOwningGroup = function() {
            CasesModelsService.setGroup($scope.caseInfo, $scope.owningGroup);
            saveCase();
        };
        $scope.updatePriority = function() {
            saveCase();
        };
        $scope.updateCaseType = function() {
            saveCase();
        };
        $scope.updateAssignee = function() {
            CasesModelsService.setAssignee($scope.caseInfo, $scope.assignee);
            saveCase();
        };
        $scope.updateDueDate = function() {
            saveCase();
        };

    }
]);