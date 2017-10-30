'use strict';

angular.module('cases').controller('Cases.FutureApprovalRoutingController', ['$scope', '$stateParams', '$q', '$translate', '$modal'
    , 'UtilService', 'Util.DateService', 'ConfigService', 'ObjectService', 'LookupService', 'Object.LookupService'
    , 'Case.InfoService', 'Helper.UiGridService', 'Helper.ObjectBrowserService', 'Authentication'
    , 'PermissionsService', 'Profile.UserInfoService', 'Object.TaskService', 'Task.InfoService', 'Case.FutureApprovalService', 'MessageService'
    , function ($scope, $stateParams, $q, $translate, $modal
        , Util, UtilDateService, ConfigService, ObjectService, LookupService, ObjectLookupService
        , CaseInfoService, HelperUiGridService, HelperObjectBrowserService, Authentication
        , PermissionsService, UserInfoService, ObjectTaskService, TaskInfoService, CaseFutureApprovalService, MessageService) {

        $scope.userSearchConfig = null;
        $scope.gridOptions = $scope.gridOptions || {};
        $scope.oldData = null;
        $scope.taskInfo = null;
        $scope.processInitialized = false;

        var currentUser = '';

        new HelperObjectBrowserService.Component({
            scope: $scope
            , stateParams: $stateParams
            , moduleId: "cases"
            , componentId: "approvalRouting"
            , onConfigRetrieved: function (componentConfig) {
                return onConfigRetrieved(componentConfig);
            }
        });

        var gridHelper = new HelperUiGridService.Grid({scope: $scope});

        ConfigService.getModuleConfig("cases").then(function (moduleConfig) {
            $scope.userSearchConfig = _.find(moduleConfig.components, {id: "userSearch"});
            return moduleConfig;
        });

        Authentication.queryUserInfo().then(function (data) {
            currentUser = data.userId;
        });

        var onConfigRetrieved = function (config) {
            $scope.config = config;
            $scope.addNewTooltip = $translate.instant(config.addNewTooltip);
            gridHelper.addCustomButton(config, "up", "fa fa-arrow-up", "moveUp", "gridButtonReadOnly", "order", null);
            gridHelper.addCustomButton(config, "down", "fa fa-arrow-down", "moveDown", "gridButtonReadOnly", "order", null);
            gridHelper.addCustomButton(config, "delete", null, null, "gridButtonReadOnly", "act", "Delete");
            gridHelper.setColumnDefs(config);
            gridHelper.setBasicOptions(config);
            gridHelper.disableGridScrolling(config);
        };

        var applyFutureTasksDataToGrid = function(data){
            $scope.gridOptions.data = data;
            $scope.gridOptions.noData = false;
            $scope.oldData = angular.copy($scope.gridOptions.data);
        };

        $scope.$bus.subscribe('buckslip-task-object-updated', function (objectInfo) {

            $scope.taskInfo = objectInfo;

            var futureTasksGridData = [];
            //set future tasks
            if(!Util.isEmpty($scope.taskInfo.id)){
                CaseFutureApprovalService.getBuckslipProcessesForChildren("CASE_FILE", $scope.taskInfo.id)
                    .then(function (response){
                        $scope.buckslipProcesses = response.data;
                        futureTasksGridData = $scope.buckslipProcesses[0].futureTasks;
                        applyFutureTasksDataToGrid(futureTasksGridData);

                        $scope.processInitialized = false;

                    });
            }
            else if(!Util.isEmpty($scope.taskInfo.buckslipFutureTasks)){
                futureTasksGridData = $scope.taskInfo.buckslipFutureTasks;
                applyFutureTasksDataToGrid(futureTasksGridData);

                $scope.buckslipFutureTaskId = $scope.taskInfo.taskId;
                $scope.processInitialized = true;
            }
            else {
                $scope.gridOptions.data = [];
                $scope.gridOptions.noData = true;
                $scope.noDataMessage = $translate.instant('cases.comp.approvalRouting.noBuckslipMessage');
            }
        });

        $scope.$bus.publish('buckslip-task-object-updated-subscribe-created', true);

        $scope.userSearch = function () {
            var modalInstance = $modal.open({
                animation: $scope.animationsEnabled,
                templateUrl: 'modules/cases/views/components/case-user-search.client.view.html',
                controller: 'Cases.UserSearchController',
                size: 'lg',
                resolve: {
                    $filter: function () {
                        return $scope.config.userSearch.userFacetFilter;
                    },
                    $extraFilter: function () {
                        return $scope.config.userSearch.userFacetExtraFilter;
                    },
                    $config: function () {
                        return $scope.userSearchConfig;
                    }
                }
            });

            modalInstance.result.then(function (chosenUser) {
                if (chosenUser) {
                    UserInfoService.getUserInfoById(chosenUser.object_id_s).then(function (user) {
                        var userConverted = convertProfileToUser(user);
                        if (!$scope.gridOptions.data) {
                            $scope.gridOptions.data = [userConverted];
                        } else {
                            $scope.gridOptions.data.push(userConverted);
                        }
                        $scope.gridOptions.noData = false;
                    });

                }

            }, function () {
                // Cancel button was clicked.
                return [];
            });

        };

        $scope.initiateTask = function () {
            if(!Util.isArrayEmpty($scope.buckslipProcesses)){
                var processId = $scope.buckslipProcesses[0].businessProcessId;
                CaseFutureApprovalService.initiateRoutingWorkflow(processId, "rtInitiate")
                    .then(function (result){
                        $scope.processInitialized = true;
                        MessageService.info($translate.instant('cases.comp.approvalRouting.processInitialize.successfull'));
                    },function (reason){
                        MessageService.error($translate.instant('cases.comp.approvalRouting.processInitialize.fail'));
                    });
            }
        };

        $scope.withdrawTask = function () {
            if(!Util.isEmpty($scope.taskInfo.taskId)){
                CaseFutureApprovalService.withdrawRoutingWorkflow($scope.taskInfo.taskId, "Withdraw Message")
                    .then(function (result){
                        $scope.processInitialized = false;
                        MessageService.info($translate.instant('cases.comp.approvalRouting.processWithdraw.successfull'));
                    }, function (reason){
                        MessageService.error($translate.instant('cases.comp.approvalRouting.processWithdraw.fail'));
                    });
            }
        };

        $scope.deleteRow = function (rowEntity) {
            gridHelper.deleteRow(rowEntity);
        };

        $scope.moveUp = function (rowEntity) {
            var index = _.indexOf($scope.gridOptions.data, rowEntity)
            if (index > 0) {
                var temp = $scope.gridOptions.data[index];
                $scope.gridOptions.data[index] = $scope.gridOptions.data[index - 1];
                $scope.gridOptions.data[index - 1] = temp;

            }
        };


        $scope.moveDown = function (rowEntity) {
            var index = _.indexOf($scope.gridOptions.data, rowEntity)
            if (index < $scope.gridOptions.data.length - 1) {
                var temp = $scope.gridOptions.data[index];
                $scope.gridOptions.data[index] = $scope.gridOptions.data[index + 1];
                $scope.gridOptions.data[index + 1] = temp;
            }
        };

        $scope.gridButtonReadOnly = function (rowEntity) {
            if (Util.goodMapValue($scope.objectInfo, 'status') == 'Closed') {
                return true;
            } else {
                if (!Util.isEmpty(Util.goodMapValue(rowEntity, 'decision'))) {
                    return true;
                }
                else {
                    return false;
                }
            }
        };

        $scope.isDataChanged = function () {
            if (!$scope.oldData || !$scope.gridOptions.data) {
                return false;
            }
            if ($scope.oldData.length != $scope.gridOptions.data.length) {
                return true;
            }
            for (var i = 0; i < $scope.oldData.length; i++) {
                if ($scope.oldData[i].userId != $scope.gridOptions.data[i].userId) {
                    return true;
                }
            }
            return false;
        };

        $scope.saveTask = function () {
            var promiseSaveInfo = Util.errorPromise($translate.instant("common.service.error.invalidData"));
            if (TaskInfoService.validateTaskInfo($scope.taskInfo)) {

                $scope.taskInfo.buckslipFutureApprovers = $scope.gridOptions.data;
                promiseSaveInfo = TaskInfoService.saveTaskInfo($scope.taskInfo);
                promiseSaveInfo.then(
                    function (taskInfo) {
                        $scope.$bus.publish('buckslip-task-object-updated', taskInfo);
                        return taskInfo;
                    }
                    , function (error) {
                        $scope.$emit("report-object-update-failed", error);
                        return error;
                    }
                )
            }
            return promiseSaveInfo;
        };


        gridHelper.addCustomButton = function (config, name, icon, clickFn, readOnlyFn, colName, tooltip) {
            if (Util.isEmpty(icon) || Util.isEmpty(clickFn) || Util.isEmpty(readOnlyFn)) {
                var found = _.find(HelperUiGridService.CommonButtons, {name: name});
                if (found) {
                    if (Util.isEmpty(icon)) {
                        icon = found.icon;
                    }
                    if (Util.isEmpty(clickFn)) {
                        clickFn = found.clickFn;
                    }
                    if (Util.isEmpty(readOnlyFn)) {
                        readOnlyFn = found.readOnlyFn;
                    }
                }
            }

            var cellTemplate = configureCellTemplate(clickFn, icon, readOnlyFn, tooltip, name);
            var columnDefs = Util.goodArray(config.columnDefs);
            var columnDef = _.find(columnDefs, {name: colName});

            if (columnDefs) {
                configureColumnDefs(columnDefs, columnDef, colName, cellTemplate);
            } else {
                config.columnDefs = [];
                configureColumnDefs(config.columnDefs, columnDef, colName, cellTemplate);
            }

            return this;
        };

        var configureColumnDefs = function (columnDefs, columnDef, colName, cellTemplate) {
            if (columnDef) {
                if (Util.goodValue(columnDef.cellTemplate)) {
                    columnDef.cellTemplate += cellTemplate;
                } else {
                    columnDef.cellTemplate = cellTemplate;
                }
            } else {
                columnDef = {
                    name: colName
                    , cellEditableCondition: false
                    , enableFiltering: false
                    , enableHiding: false
                    , enableSorting: false
                    , enableColumnResizing: true
                    , headerCellTemplate: "<span></span>"
                    , cellTemplate: cellTemplate
                };
                columnDefs.push(columnDef);
            }
        };

        var configureCellTemplate = function (clickFn, icon, readOnlyFn, tooltip, name) {
            var cellTemplate = "<a class='inline animated btn btn-default btn-xs'"
                + " ng-click='grid.appScope." + clickFn + "(row.entity)'";

            if (tooltip) {
                cellTemplate += " tooltip='" + tooltip + "' tooltip-append-to-body='true' tooltip-popup-delay='400'";
            }

            if (readOnlyFn) {
                cellTemplate += " ng-hide='grid.appScope." + readOnlyFn + "(row.entity)'";
            }

            cellTemplate += "><i class='" + icon + "'></i></a>";

            return cellTemplate;
        };

        function convertProfileToUser(userProfile) {
            //we are using for now just this to fields, if needed add rest of them
            var user = {
                userId: userProfile.userId,
                fullName: userProfile.fullName
            };
            return user;
        }
    }
]);