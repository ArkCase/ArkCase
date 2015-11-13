'use strict';

angular.module('cases').controller('Cases.InfoController', ['$scope', '$stateParams', 'StoreService', 'UtilService', 'ValidationService', 'HelperService', 'CallLookupService', 'CallCasesService', 'LookupService', 'CasesService', 'ObjectsModelsService',
    function ($scope, $stateParams, Store, Util, Validator, Helper, CallLookupService, CallCasesService, LookupService, CasesService, ObjectsModelsService) {
        $scope.$emit('req-component-config', 'info');
        $scope.$on('component-config', function (e, componentId, config) {
            if ("info" == componentId) {
                $scope.config = config;
            }
        });


        CallLookupService.getUsers().then(
            function (users) {
                var options = [];
                _.each(users, function (user) {
                    options.push({object_id_s: user.object_id_s, name: user.name});
                });
                $scope.assignableUsers = options;
                return users;
            }
        );

        CallLookupService.getPriorities().then(
            function (priorities) {
                var options = [];
                _.each(priorities, function (priority) {
                    options.push({value: priority, text: priority});
                });
                $scope.priorities = options;
                return priorities;
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
            $scope.assignee = ObjectsModelsService.getAssignee(data);
            $scope.owningGroup = ObjectsModelsService.getGroup(data);
        });

        /**
         * Persists the updated casefile metadata to the ArkCase database
         */
        function saveCase() {
            if (CallCasesService.validateCaseInfo($scope.caseInfo)) {
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
        }

        // Updates the ArkCase database when the user changes a case attribute
        // in a case top bar menu item and clicks the save check button
        $scope.updateTitle = function() {
            saveCase();
        };
        $scope.updateOwningGroup = function() {
            ObjectsModelsService.setGroup($scope.caseInfo, $scope.owningGroup);
            saveCase();
        };
        $scope.updatePriority = function() {
            saveCase();
        };
        $scope.updateCaseType = function() {
            saveCase();
        };
        $scope.updateAssignee = function() {
            ObjectsModelsService.setAssignee($scope.caseInfo, $scope.assignee);
            saveCase();
        };
        $scope.updateDueDate = function() {
            saveCase();
        };

    }
]);