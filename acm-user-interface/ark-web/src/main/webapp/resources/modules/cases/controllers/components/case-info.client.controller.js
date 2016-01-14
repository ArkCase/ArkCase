'use strict';

angular.module('cases').controller('Cases.InfoController', ['$scope', '$stateParams', 'UtilService', 'ConfigService'
    , 'Object.LookupService', 'Case.LookupService', 'Case.InfoService', 'Object.ModelService', 'Helper.ObjectBrowserService'
    , function ($scope, $stateParams, Util, ConfigService
        , ObjectLookupService, CaseLookupService, CaseInfoService, ObjectModelService, HelperObjectBrowserService) {

        ConfigService.getComponentConfig("cases", "info").then(function (componentConfig) {
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

        CaseLookupService.getCaseTypes().then(
            function (caseTypes) {
                var options = [];
                _.forEach(caseTypes, function (item) {
                    options.push({value: item, text: item});
                });
                $scope.caseTypes = options;
                return caseTypes;
            }
        );

        //$scope.$on('object-selected', function onSelectedCase(e, selectedCase) {
        //    $scope.caseSolr = selectedCase;
        //});

        $scope.dueDate = null;
        var previousId = null;
        $scope.$on('object-updated', function (e, data) {
            if (!CaseInfoService.validateCaseInfo(data)) {
                return;
            }
            $scope.caseInfo = data;
            $scope.dueDate = ($scope.caseInfo.dueDate) ? moment($scope.caseInfo.dueDate).toDate() : null;
            $scope.owningGroup = ObjectModelService.getGroup(data);
            $scope.assignee = ObjectModelService.getAssignee(data);
            if (previousId != $stateParams.id) {
                CaseLookupService.getApprovers($scope.owningGroup, $scope.assignee).then(
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


        $scope.$on('refresh-content', function (e, selectedObject) {
            console.log("info: refresh-content");
        });

        //var currentObjectId = HelperObjectBrowserService.getCurrentObjectId();
        //if (Util.goodPositive(currentObjectId, false)) {
        //    CaseInfoService.getCaseInfo(currentObjectId).then(function (caseInfo) {
        //        $scope.caseInfo = caseInfo;
        //        $scope.owningGroup = ObjectModelService.getGroup(caseInfo);
        //        $scope.assignee = ObjectModelService.getAssignee(caseInfo);
        //        CaseLookupService.getApprovers($scope.owningGroup, $scope.assignee).then(
        //            function (approvers) {
        //                var options = [];
        //                _.each(approvers, function (approver) {
        //                    options.push({id: approver.userId, name: approver.fullName});
        //                });
        //                $scope.assignees = options;
        //                return approvers;
        //            }
        //        );
        //        return caseInfo;
        //    });
        //}

        /**
         * Persists the updated casefile metadata to the ArkCase database
         */
        function saveCase() {
            var caseInfo = Util.omitNg($scope.caseInfo);
            if (CaseInfoService.validateCaseInfo(caseInfo)) {
                CaseInfoService.saveCaseInfo(caseInfo).then(
                    function (caseInfo) {
                        //update tree node tittle
                        $scope.$emit("report-object-update", caseInfo);
                        return caseInfo;
                    }
                    , function (error) {
                        //set error to x-editable title
                        //update tree node tittle
                        return error;
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
            ObjectModelService.setGroup($scope.caseInfo, $scope.owningGroup);
            saveCase();
        };
        $scope.updatePriority = function() {
            saveCase();
        };
        $scope.updateCaseType = function() {
            saveCase();
        };
        $scope.updateAssignee = function() {
            ObjectModelService.setAssignee($scope.caseInfo, $scope.assignee);
            saveCase();
        };
        $scope.updateDueDate = function(dueDate) {
            $scope.caseInfo.dueDate = (dueDate) ? moment(dueDate).format($scope.config.dateFormat): null;
            saveCase();
        };

    }
]);