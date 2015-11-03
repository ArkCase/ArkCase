'use strict';

angular.module('cases').controller('Cases.InfoController', ['$scope', '$stateParams', 'StoreService', 'UtilService', 'ValidationService', 'HelperService', 'LookupService', 'CasesService', 'CasesModelsService',
    function ($scope, $stateParams, Store, Util, Validator, Helper, LookupService, CasesService, CasesModelsService) {
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
        //var cacheAssignableUsers = new Store.SessionData(Helper.SessionCacheNames.ASSIGNABLE_USERS);
        //var assignableUsers = cacheAssignableUsers.get();
        LookupService.getUsers({}, function (data) {
            $scope.assignableUsers = [];
            _.forEach(data, function (item) {
                var userInfo = JSON.parse(item);
                $scope.assignableUsers.push({value: userInfo.object_id_s, text: userInfo.object_id_s});
            });
        });

        $scope.owningGroups = [];
        //var cacheOwningGroups = new Store.SessionData(Helper.SessionCacheNames.OWNING_GROUPS);
        //var owningGroups = cacheOwningGroups.get();
        LookupService.getGroups({}, function (data) {
            $scope.owningGroups = [];
            var groups = data.response.docs;
            _.forEach(groups, function (item) {
                $scope.owningGroups.push({value: item.name, text: item.name});
            });
        });

        $scope.caseTypes = [];
        //var cacheCaseTypes = new Store.SessionData(Helper.SessionCacheNames.CASE_TYPES);
        //var caseTypes = cacheCaseTypes.get();
        LookupService.getCaseTypes({}, function (data) {
            $scope.caseTypes = [];
            _.forEach(data, function (item) {
                $scope.caseTypes.push({value: item, text: item});
            });
        });


        $scope.caseSolr = null;
        $scope.caseInfo = null;
        $scope.$on('case-selected', function onSelectedCase(e, selectedCase) {
            $scope.caseSolr = selectedCase;
        });
        $scope.assignee = null;
        $scope.owningGroup = null;
        $scope.$on('case-retrieved', function(e, data){
            if (Validator.validateCaseFile(data)) {
                $scope.caseInfo = data;
                $scope.assignee = CasesModelsService.getAssignee(data);
                $scope.owningGroup = CasesModelsService.getGroup(data);
            }
        });

        /**
         * Persists the updated casefile metadata to the ArkCase database
         */
        function saveCase() {
            if (Validator.validateCaseFile($scope.caseInfo)) {
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