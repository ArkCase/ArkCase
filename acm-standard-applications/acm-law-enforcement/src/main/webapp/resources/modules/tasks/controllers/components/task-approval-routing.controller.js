'use strict';

angular.module('cases').controller('Tasks.ApprovalRoutingController', ['$scope', '$stateParams', '$q', '$translate', '$modal'
    , 'UtilService', 'Util.DateService', 'ConfigService', 'ObjectService', 'LookupService', 'Object.LookupService'
    , 'Task.InfoService', 'Helper.UiGridService', 'Helper.ObjectBrowserService', 'Authentication'
    , 'PermissionsService', 'Profile.UserInfoService'
    , function ($scope, $stateParams, $q, $translate, $modal
        , Util, UtilDateService, ConfigService, ObjectService, LookupService, ObjectLookupService
        , TaskInfoService, HelperUiGridService, HelperObjectBrowserService, Authentication
        , PermissionsService, UserInfoService) {

        $scope.userSearchConfig = null;
        $scope.gridOptions = $scope.gridOptions || {};
        $scope.pastApprovers = {};
        $scope.pastApprovers.GridOptions = $scope.pastApprovers.GridOptions || {};
        $scope.oldData = null;
        $scope.taskInfo = null;

        var currentUser = '';
        var canEditMetadata = true;
        var canEditBuckslip = true;

        new HelperObjectBrowserService.Component({
            scope: $scope
            , stateParams: $stateParams
            , moduleId: "tasks"
            , componentId: "approvalrouting"
            , retrieveObjectInfo: TaskInfoService.getTaskInfo
            , validateObjectInfo: TaskInfoService.validateTaskInfo
            , onConfigRetrieved: function (componentConfig) {
                return onConfigRetrieved(componentConfig);
            }
            , onObjectInfoRetrieved: function (objectInfo) {
                onObjectInfoRetrieved(objectInfo);
            }
        });

        new HelperObjectBrowserService.Component({
            scope: $scope
            , stateParams: $stateParams
            , moduleId: "tasks"
            , componentId: "pastapprovals"
            , retrieveObjectInfo: TaskInfoService.getTaskInfo
            , validateObjectInfo: TaskInfoService.validateTaskInfo
            , onConfigRetrieved: function (componentConfig) {
                return onPastApprovalsConfigRetrieved(componentConfig);
            }
            , onObjectInfoRetrieved: function (objectInfo) {
                onObjectInfoRetrieved(objectInfo);
            }
        });

        var gridHelper = new HelperUiGridService.Grid({scope: $scope});
        var gridHelperPastApprovers = new HelperUiGridService.Grid({scope: $scope.pastApprovers});


        ConfigService.getModuleConfig("tasks").then(function (moduleConfig) {
            $scope.config = _.find(moduleConfig.components, {id: "approvalrouting"});

            $scope.userSearchConfig = _.find(moduleConfig.components, {id: "userSearch"});
            return moduleConfig;
        });

        Authentication.queryUserInfo().then(function (data) {
            currentUser = data.userId;
        });

        var onPastApprovalsConfigRetrieved = function (config) {
            $scope.pastApproversConfig = config;
            gridHelperPastApprovers.setColumnDefs(config);
            gridHelperPastApprovers.setBasicOptions(config);
            gridHelperPastApprovers.disableGridScrolling(config);
        };

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

        var onObjectInfoRetrieved = function (objectInfo) {
            $scope.taskInfo = objectInfo;

            //set future approvers info
            if (!Util.isArrayEmpty(objectInfo.buckslipFutureApprovers)) {
                var data = [];
                _.forEach(objectInfo.buckslipFutureApprovers, function (userProfile) {
                    data.push(convertProfileToUser(userProfile));
                });
                $scope.gridOptions.data = data;
                $scope.gridOptions.noData = false;
            } else {
                $scope.gridOptions.data = [];
                $scope.gridOptions.noData = true;
                $scope.noDataMessage = $translate.instant('tasks.comp.approvalRouting.noBuckslipMessage');
            }
            //set past approvers info
            if (objectInfo.buckslipPastApprovers) {
                $scope.pastApprovers.GridOptions.data = objectInfo.buckslipPastApprovers;
                $scope.pastApprovers.GridOptions.noData = false;
            } else {
                $scope.pastApprovers.GridOptions.data = [];
                $scope.pastApprovers.GridOptions.noData = true;
                $scope.noDataMessage = $translate.instant('tasks.comp.approvalRouting.noBuckslipMessage');
            }
            $scope.oldData = _.cloneDeep($scope.gridOptions.data);
        };

        $scope.userSearch = function () {
            var modalInstance = $modal.open({
                animation: $scope.animationsEnabled,
                templateUrl: 'modules/tasks/views/components/task-user-search.client.view.html',
                controller: 'Tasks.UserSearchController',
                size: 'lg',
                resolve: {
                    $filter: function () {
                        return $scope.config.userSearch.userFacetFilter;
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

        $scope.isReadOnly = function (objectInfo) {
            if (canEditMetadata) {
                return (objectInfo) ? ("Closed" == objectInfo.status) : true;
            }
            return true;
        };

        $scope.gridButtonReadOnly = function (rowEntity) {
            if (canEditBuckslip) {
                if (Util.goodMapValue($scope.objectInfo, 'status') == 'Closed') {
                    return true;
                } else {
                    if (!Util.isEmpty(Util.goodMapValue(rowEntity, 'decision'))) {
                        return true;
                    }
                    return false;
                }
            }
            return true;
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
            if (TaskInfoService.validateTaskInfo($scope.objectInfo)) {

                $scope.taskInfo.buckslipFutureApprovers = $scope.gridOptions.data;
                $scope.taskInfo.buckslipTask = true;
                promiseSaveInfo = TaskInfoService.saveTaskInfo($scope.taskInfo);
                promiseSaveInfo.then(
                    function (taskInfo) {
                        $scope.$emit("report-object-updated", taskInfo);
                        TaskInfoService.resetTaskCacheById(taskInfo.taskId);
                        return TaskInfoService.getTaskInfo(taskInfo.taskId);
                    }
                    , function (error) {
                        $scope.$emit("report-object-update-failed", error);
                        return error;
                    }
                ).then(function (taskInfo) {
                    onObjectInfoRetrieved(taskInfo);
                });
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

        $scope.gridEditButtonReadOnly = function (rowEntity) {
            if (canEditBuckslip) {
                if (Util.goodMapValue($scope.objectInfo, 'status') == 'Closed') {
                    return true;
                } else {
                    if (!Util.isEmpty(Util.goodMapValue(rowEntity, 'decision'))) {
                        return true;
                    }
                    else if (!Util.compare(rowEntity.defaultApprover, currentUser)) {
                        return true;
                    }
                    return false;
                }
            }

            return true;
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
])
;