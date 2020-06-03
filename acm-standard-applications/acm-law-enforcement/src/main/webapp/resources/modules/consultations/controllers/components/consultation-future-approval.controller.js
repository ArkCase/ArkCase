'use strict';

angular.module('consultations').controller(
    'Consultations.FutureApprovalRoutingController',
    [
        '$scope',
        '$stateParams',
        '$q',
        '$translate',
        '$modal',
        'UtilService',
        'Util.DateService',
        'ConfigService',
        'ObjectService',
        'LookupService',
        'Object.LookupService',
        'Consultation.InfoService',
        'Helper.UiGridService',
        'Helper.ObjectBrowserService',
        'Authentication',
        'PermissionsService',
        'Profile.UserInfoService',
        'Object.TaskService',
        'Task.InfoService',
        'Consultation.FutureApprovalService',
        'MessageService',
        'Acm.StoreService',
        'ModalDialogService',
        '$timeout',
        function($scope, $stateParams, $q, $translate, $modal, Util, UtilDateService, ConfigService, ObjectService, LookupService, ObjectLookupService, ConsultationInfoService, HelperUiGridService, HelperObjectBrowserService, Authentication, PermissionsService, UserInfoService, ObjectTaskService,
                 TaskInfoService, ConsultationFutureApprovalService, MessageService, Store, ModalDialogService, $timeout) {

            $scope.userSearchConfig = null;
            $scope.gridOptions = $scope.gridOptions || {};
            $scope.oldData = null;
            $scope.taskInfo = null;
            $scope.showInitButton = false;
            $scope.showWithdrawButton = false;
            $scope.initInProgress = false;
            $scope.withdrawInProgress = false;
            $scope.nonConcurEndsApprovals = true;

            var currentUser = '';

            new HelperObjectBrowserService.Component({
                scope: $scope,
                stateParams: $stateParams,
                moduleId: "consultations",
                componentId: "approvalRouting",
                retrieveObjectInfo: ConsultationInfoService.getCaseInfo,
                validateObjectInfo: ConsultationInfoService.validateCaseInfo,
                onConfigRetrieved: function(componentConfig) {
                    return onConfigRetrieved(componentConfig);
                }
            });

            var gridHelper = new HelperUiGridService.Grid({
                scope: $scope
            });

            ConfigService.getModuleConfig("consultations").then(function(moduleConfig) {
                $scope.userSearchConfig = _.find(moduleConfig.components, {
                    id: "userSearch"
                });
                return moduleConfig;
            });

            Authentication.queryUserInfo().then(function(data) {
                currentUser = data.fullName;
            });

            var onConfigRetrieved = function(config) {
                $scope.config = config;
                $scope.addNewTooltip = $translate.instant(config.addNewTooltip);
                gridHelper.addCustomButton(config, "up", "fa fa-arrow-up", "moveUp", "gridButtonReadOnly", "order", null);
                gridHelper.addCustomButton(config, "down", "fa fa-arrow-down", "moveDown", "gridButtonReadOnly", "order", null);
                gridHelper.addCustomButton(config, "delete", null, null, "gridButtonReadOnly", "act", "Delete");
                gridHelper.setColumnDefs(config);
                gridHelper.setBasicOptions(config);
                gridHelper.disableGridScrolling(config);
            };

            var applyFutureTasksDataToGrid = function(data) {
                $scope.gridOptions.data = data;
                $scope.gridOptions.noData = false;
                $scope.oldData = angular.copy($scope.gridOptions.data);
            };

            var showInitWithdrawButtons = function(processId) {
                if (Util.isEmpty(processId)) {
                    return;
                }

                ConsultationFutureApprovalService.isWorkflowInitiable(processId).then(function(response) {
                    $scope.showInitButton = response.data;
                });
                ConsultationFutureApprovalService.isWorkflowWithdrawable(processId).then(function(response) {
                    $scope.showWithdrawButton = response.data;
                });
            };

            var fetchBuckslipProcess = function(buckslipProcess) {
                $scope.buckslipProcess = buckslipProcess;
                showInitWithdrawButtons($scope.buckslipProcess.businessProcessId);
                applyFutureTasksDataToGrid($scope.buckslipProcess.futureTasks);
            };

            $scope.$bus.subscribe('buckslip-task-object-updated', function(objectInfo) {
                //set future tasks
                if (!Util.isEmpty(objectInfo.id)) {
                    ConsultationFutureApprovalService.getBuckslipProcessesForChildren("CONSULTATION", objectInfo.id).then(function(response) {
                        if (!Util.isArrayEmpty(response.data)) {
                            fetchBuckslipProcess(response.data[0]);
                        }
                    });

                    ConsultationFutureApprovalService.getBusinessProcessVariableForObject("CONSULTATION", objectInfo.id, "nonConcurEndsApprovals", true).then(function(response) {
                        if (!Util.isArray(response.data) && !Util.isEmpty(response.data)) {
                            $scope.nonConcurEndsApprovals = response.data;
                        }
                    });

                } else if (!Util.isEmpty(objectInfo.buckslipFutureTasks)) {
                    $scope.taskInfo = objectInfo;
                    ConsultationFutureApprovalService.getBuckslipProcessesForChildren(objectInfo.parentObjectType, objectInfo.parentObjectId).then(function(response) {
                        fetchBuckslipProcess(response.data[0]);
                        $scope.nonConcurEndsApprovals = $scope.buckslipProcess.nonConcurEndsApprovals;
                    });
                } else {
                    $scope.gridOptions.data = [];
                    $scope.gridOptions.noData = true;
                    $scope.noDataMessage = $translate.instant('consultations.comp.approvalRouting.noBuckslipMessage');
                }
            });

            $scope.$bus.publish('buckslip-task-object-updated-subscribe-created', true);

            $scope.userSearch = function() {
                var modalMetadata = {
                    moduleName: "consultations",
                    templateUrl: "modules/consultations/views/components/consultation-new-future-task.client.view.html",
                    controllerName: "Consultations.NewFutureTaskController"
                };
                ModalDialogService.showModal(modalMetadata).then(function(result) {
                    var futureTask = {
                        approverId: result.pickedUserId,
                        approverFullName: result.pickedUserName,
                        groupName: result.pickedGroupId,
                        taskName: result.futureTaskTitle,
                        details: result.futureTaskDetails,
                        addedByFullName: currentUser
                    }
                    if (!Util.isEmpty(futureTask.approverId) && !Util.isEmpty(futureTask.taskName)) {
                        $scope.buckslipProcess.futureTasks.push(futureTask);
                    }
                });
            };

            var cleanCachedConsultationFile = function(consultationId) {
                var cacheChildTaskData = new Store.CacheFifo(ObjectTaskService.CacheNames.CHILD_TASK_DATA);
                var cacheKey = ObjectService.ObjectTypes.CONSULTATION + "." + consultationId + "." + 0 + "." + 100 + "." + '' + "." + '';
                cacheChildTaskData.remove(cacheKey);
            };

            $scope.initiateTask = function() {
                if (!Util.isEmpty($scope.buckslipProcess)) {
                    $scope.initInProgress = true;
                    $scope.buckslipProcess.nonConcurEndsApprovals = $scope.nonConcurEndsApprovals;

                    ConsultationFutureApprovalService.updateBuckslipProcess($scope.buckslipProcess).then(function(result) {
                        ConsultationFutureApprovalService.initiateRoutingWorkflow($scope.buckslipProcess.businessProcessId).then(function(result) {
                            cleanCachedConsultationFile($stateParams.id);
                            $timeout(function() {
                                $scope.$emit('report-object-refreshed', $stateParams.id);
                                MessageService.info($translate.instant('consultations.comp.approvalRouting.processInitialize.successfull'));

                                $scope.initInProgress = false;
                            }, 2000);
                        }, function(reason) {
                            MessageService.error($translate.instant('consultations.comp.approvalRouting.processInitialize.fail'));
                        });
                    }, function(reason) {
                        MessageService.error($translate.instant('consultations.comp.approvalRouting.businessProcessUpdate.fail'));
                    });
                }
            };

            $scope.withdrawTask = function() {
                if (!Util.isEmpty($scope.taskInfo.taskId)) {
                    $scope.withdrawInProgress = true;
                    ConsultationFutureApprovalService.withdrawRoutingWorkflow($scope.taskInfo.taskId).then(function(result) {
                        cleanCachedConsultationFile($stateParams.id);
                        $timeout(function() {
                            $scope.$emit('report-object-refreshed', $stateParams.id);
                            $scope.$bus.publish('buckslip-task-object-updated', {
                                'id': $stateParams.id
                            });
                            MessageService.info($translate.instant('consultations.comp.approvalRouting.processWithdraw.successfull'));

                            $scope.nonConcurEndsApprovals = true;
                            $scope.withdrawInProgress = false;
                        }, 2000);
                    }, function(reason) {
                        MessageService.error($translate.instant('consultations.comp.approvalRouting.processWithdraw.fail'));
                    });
                }
            };

            $scope.deleteRow = function(rowEntity) {
                gridHelper.deleteRow(rowEntity);
            };

            $scope.moveUp = function(rowEntity) {
                var index = _.indexOf($scope.gridOptions.data, rowEntity)
                if (index > 0) {
                    var temp = $scope.gridOptions.data[index];
                    $scope.gridOptions.data[index] = $scope.gridOptions.data[index - 1];
                    $scope.gridOptions.data[index - 1] = temp;

                }
            };

            $scope.moveDown = function(rowEntity) {
                var index = _.indexOf($scope.gridOptions.data, rowEntity)
                if (index < $scope.gridOptions.data.length - 1) {
                    var temp = $scope.gridOptions.data[index];
                    $scope.gridOptions.data[index] = $scope.gridOptions.data[index + 1];
                    $scope.gridOptions.data[index + 1] = temp;
                }
            };

            $scope.gridButtonReadOnly = function(rowEntity) {
                if (Util.goodMapValue($scope.objectInfo, 'status') == 'Closed') {
                    return true;
                } else {
                    if (!Util.isEmpty(Util.goodMapValue(rowEntity, 'decision'))) {
                        return true;
                    } else {
                        return false;
                    }
                }
            };

            $scope.isDataChanged = function() {
                if (!$scope.oldData || !$scope.gridOptions.data) {
                    return false;
                }
                if ($scope.oldData.length != $scope.gridOptions.data.length) {
                    return true;
                }
                for (var i = 0; i < $scope.oldData.length; i++) {
                    if ($scope.oldData[i].approverId != $scope.gridOptions.data[i].approverId) {
                        return true;
                    }
                }
                return false;
            };

            $scope.saveTask = function() {
                ConsultationFutureApprovalService.updateBuckslipProcess($scope.buckslipProcess).then(function(result) {
                    $scope.oldData = angular.copy($scope.gridOptions.data);
                    MessageService.info($translate.instant('consultations.comp.approvalRouting.businessProcessUpdate.successfull'));
                }, function(reason) {
                    MessageService.error($translate.instant('consultations.comp.approvalRouting.businessProcessUpdate.fail'));
                });
            };

            gridHelper.addCustomButton = function(config, name, icon, clickFn, readOnlyFn, colName, tooltip) {
                if (Util.isEmpty(icon) || Util.isEmpty(clickFn) || Util.isEmpty(readOnlyFn)) {
                    var found = _.find(HelperUiGridService.CommonButtons, {
                        name: name
                    });
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
                var columnDef = _.find(columnDefs, {
                    name: colName
                });

                if (columnDefs) {
                    configureColumnDefs(columnDefs, columnDef, colName, cellTemplate);
                } else {
                    config.columnDefs = [];
                    configureColumnDefs(config.columnDefs, columnDef, colName, cellTemplate);
                }

                return this;
            };

            var configureColumnDefs = function(columnDefs, columnDef, colName, cellTemplate) {
                if (columnDef) {
                    if (Util.goodValue(columnDef.cellTemplate)) {
                        columnDef.cellTemplate += cellTemplate;
                    } else {
                        columnDef.cellTemplate = cellTemplate;
                    }
                } else {
                    columnDef = {
                        name: colName,
                        cellEditableCondition: false,
                        enableFiltering: false,
                        enableHiding: false,
                        enableSorting: false,
                        enableColumnResizing: true,
                        headerCellTemplate: "<span></span>",
                        cellTemplate: cellTemplate
                    };
                    columnDefs.push(columnDef);
                }
            };

            var configureCellTemplate = function(clickFn, icon, readOnlyFn, tooltip, name) {
                var cellTemplate = "<a class='inline animated btn btn-default btn-xs'" + " ng-click='grid.appScope." + clickFn + "(row.entity)'";

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
        } ]);