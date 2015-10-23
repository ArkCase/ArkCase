'use strict';

angular.module('cases').controller('Cases.InfoController', ['$scope', '$stateParams', '$log', 'ConfigService', 'CasesService', 'CasesModelsService', 'LookupService', 'UtilService', 'ValidationService',
    function($scope, $stateParams, $log, ConfigService, CasesService, CasesModelsService, LookupService, Util, Validator) {
        $scope.$emit('req-component-config', 'info');

        // Dropdown list options
        $scope.owningGroups = [];
        $scope.assignableUsers = [];
        $scope.caseTypes = [];
        $scope.priorities = [];

        $scope.config = null;
        $scope.$on('component-config', applyConfig);
        function applyConfig(e, componentId, config) {
            if (componentId == 'info') {
                $scope.config = config;
            }
        }

        $scope.components = null;
        ConfigService.getModule({moduleId: 'cases'}, function(moduleConfig){
            $scope.components = moduleConfig.components;
        });


        $scope.selectedCase = null;
        $scope.caseInfo = null;
        $scope.$on('case-selected', function onSelectedCase(e, selectedCase) {
            //if (!$scope.case || $scope.case.object_id_s != selectedCase.object_id_s) {
            $scope.selectedCase = selectedCase;
            //$scope.caseInfo = CasesService.get({id: selectedCase.object_id_s});
            //}
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
            var caseInfo = Util.omitNg($scope.caseInfo);
            CasesService.save({}, caseInfo
                ,function(successData) {
                    $log.debug("case saved successfully");
                }
                ,function(errorData) {
                    $log.error("case save failed");
                }
            );
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

        // Obtains the dropdown menu selection options via REST calls to ArkCase
        LookupService.getPriorities({}, function(data) {
            $scope.priorities = [];
            _.forEach(data, function (item) {
                $scope.priorities.push({value: item, text: item});
            });
        });
        LookupService.getUsers({}, function(data) {
            $scope.assignableUsers = [];
            _.forEach(data, function(item) {
                var userInfo = JSON.parse(item);
                $scope.assignableUsers.push({value: userInfo.object_id_s, text: userInfo.object_id_s});
            });
        });
        LookupService.getGroups({}, function(data) {
            $scope.owningGroups = [];
            var groups = data.response.docs;
            _.forEach(groups, function(item) {
                $scope.owningGroups.push({value: item.name, text: item.name});
            });
        });
        LookupService.getCaseTypes({}, function(data) {
            $scope.caseTypes = [];
            _.forEach(data, function(item) {
                $scope.caseTypes.push({value: item, text: item});
            });
        });
    }
]);