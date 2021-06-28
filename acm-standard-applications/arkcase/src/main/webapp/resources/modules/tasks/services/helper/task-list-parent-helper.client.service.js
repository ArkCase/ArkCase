"use strict";

/**
 * @ngdoc service
 * @name services:Helper.TaskListParentNode
 *
 * @description
 * Helper.TaskListParentNode provide common help for task listing grid. Uses the HelperObjectBrowserService Component.
 * Helper.TaskListParentNode implements all the method needed for showing a functional task list table.
 * Every function is overridable by the controller's scope if it's defined otherwise will just take the default helper function.
 *
 * TaskTableComponent takes the same arguments as Helper.ObjectBrowserService component plus have additional:
 * - enableNewTaskButton for enabling the new task button can also be enabled after initialization of the DocumentTreeComponent with enableNewTaskButton function.
 * - Required commit function at the end after everything is defined from the scope and can send request for getting the object information and the config.
 *
 */

angular.module('tasks').factory(
        'Helper.TaskListParentNode',
        [ 'UtilService', 'Helper.ObjectBrowserService', 'CostTracking.InfoService', 'Helper.UiGridService', 'Object.TaskService', 'Task.AlertsService', 'ObjectService', 'Task.WorkflowService', 'ModalDialogService', '$timeout', 'Authentication',
                function(Util, HelperObjectBrowserService, CostTrackingInfoService, HelperUiGridService, ObjectTaskService, TaskAlertsService, ObjectService, TaskWorkflowService, ModalDialogService, $timeout, Authentication) {

                    var Service = {

                        TaskTableComponent: function(arg) {
                            var that = this;
                            that.arg = arg;
                            that.scope = arg.scope;
                            that.objectType = arg.objectType || that.scope.objectType;
                            that.gridHelper = arg.gridHelper || new HelperUiGridService.Grid({
                                scope: that.scope
                            });
                            that.scope.enableNewTaskButton = arg.enableNewTaskButton || that.scope.enableNewTaskButton;

                            that.scope.afterObjectInfo = that.scope.afterObjectInfo || function() {
                                if (that.objectType === ObjectService.ObjectTypes.COSTSHEET) {
                                    that.scope.parentObject = that.scope.objectInfo.costsheetNumber;
                                } else if (that.objectType === ObjectService.ObjectTypes.CASE_FILE) {
                                    that.scope.parentObject = that.scope.objectInfo.caseNumber;
                                } else if (that.objectType === ObjectService.ObjectTypes.COMPLAINT) {
                                    that.scope.parentObject = that.scope.objectInfo.costsheetNumber;
                                }
                            };

                            that.scope.deleteRow = that.scope.deleteRow || function(entity) {
                                var objectInfo = Util.omitNg(that.scope.objectInfo);
                                if (arg.validateObjectInfo(objectInfo)) {
                                    TaskWorkflowService.deleteTask(entity.object_id_s).then(function(objectInfo) {
                                        that.gridHelper.deleteRow(entity);
                                        that.scope.$emit("report-object-updated", objectInfo);
                                        return objectInfo;
                                    });
                                }

                            };
                            that.promiseUsers = arg.promiseUsers || that.gridHelper.getUsers();
                            that.scope.onConfigRetrieved = arg.onConfigRetrieved || function(config) {
                                that.scope.config = config;
                                //first the filter is set, and after that everything else,
                                //so that the data loads with the new filter applied
                                that.gridHelper.setUserNameFilterToConfig(that.promiseUsers).then(function(updatedConfig) {
                                    that.scope.config = updatedConfig;
                                    if (that.scope.gridApi != undefined)
                                        that.scope.gridApi.core.refresh();
                                    that.gridHelper.setColumnDefs(updatedConfig);
                                    that.gridHelper.setBasicOptions(updatedConfig);
                                    that.gridHelper.disableGridScrolling(updatedConfig);
                                    that.gridHelper.setExternalPaging(updatedConfig, that.scope.populateGridData);
                                    that.gridHelper.addButton(updatedConfig, "delete", null, null, "isDeleteDisabled");
                                });
                                that.scope.componentHelper.doneConfig(config);
                            };
                            that.scope.onObjectInfoRetrieved = arg.onObjectInfoRetrieved || function(object) {
                                that.scope.objectInfo = object;
                                that.scope.populateGridData();
                                if (that.scope.afterObjectInfo) {
                                    that.scope.afterObjectInfo();
                                }
                            };

                            that.scope.populateGridData = arg.populateGridData || function() {
                                var currentObjectId = Util.goodMapValue(that.scope.objectInfo, "id");
                                if (Util.goodPositive(currentObjectId, false)) {
                                    ObjectTaskService.resetChildTasks(that.objectType, currentObjectId);
                                    ObjectTaskService.queryChildTasks(that.objectType, currentObjectId, Util.goodValue(that.scope.start, 0), Util.goodValue(that.scope.pageSize, 10), Util.goodMapValue(that.scope.sort, "by"), Util.goodMapValue(that.scope.sort, "dir")).then(function(data) {
                                        var tasks = data.response.docs;
                                        angular.forEach(tasks, function(task) {
                                            //calculate to show alert icons if task is in overdue or deadline is approaching if the status of the task is in different state than CLOSED.
                                            task.isOverdue = TaskAlertsService.calculateOverdue(new Date(task.dueDate_tdt)) && !(task.status_lcs === "CLOSED");
                                            task.isDeadline = TaskAlertsService.calculateDeadline(new Date(task.dueDate_tdt)) && !(task.status_lcs === "CLOSED");
                                        });
                                        that.scope.gridOptions = that.scope.gridOptions || {};
                                        that.scope.gridOptions.data = tasks;
                                        that.scope.gridOptions.totalItems = data.response.numFound;

                                        return data;
                                    });
                                }
                            };

                            Authentication.queryUserInfo().then(function(userInfo) {
                                that.scope.userInfo = userInfo;
                                that.scope.userId = userInfo.userId;
                                return userInfo;
                            });

                            that.scope.isDeleteDisabled = arg.isDeleteDisabled || function(rowEntity) {
                                return ((Util.isEmpty(rowEntity.assignee_id_lcs) || (rowEntity.assignee_id_lcs !== that.scope.userId)) || (rowEntity.status_lcs === "CLOSED") || (!rowEntity.adhocTask_b));
                            };

                            that.scope.onClickObjLink = arg.onClickObjLink || function(event, rowEntity) {
                                event.preventDefault();
                                var targetType = (Util.goodMapValue(rowEntity, "adhocTask_b", false)) ? ObjectService.ObjectTypes.ADHOC_TASK : ObjectService.ObjectTypes.TASK;
                                //var targetType = Util.goodMapValue(rowEntity, "object_type_s");
                                var targetId = Util.goodMapValue(rowEntity, "object_id_s");
                                that.gridHelper.showObject(targetType, targetId);
                            };

                            if (that.scope.enableNewTaskButton) {
                                that.enableNewTaskButton();
                            }

                        }
                    };

                    Service.TaskTableComponent.prototype.enableNewTaskButton = function() {
                        var that = this;
                        that.scope.addNew = function() {
                            var modalMetadata = {
                                moduleName: "tasks",
                                templateUrl: "modules/tasks/views/components/task-new-task.client.view.html",
                                controllerName: "Tasks.NewTaskController",
                                params: {
                                    parentType: that.objectType,
                                    parentObject: that.scope.parentObject,
                                    parentId: that.scope.objectInfo.id,
                                    parentTitle: that.scope.objectInfo.title,
                                    taskType: 'ACM_TASK'
                                }
                            };
                            ModalDialogService.showModal(modalMetadata).then(function (value) {
                                $timeout(function() {
                                    that.scope.populateGridData();
                                    //3 seconds delay so solr can index the new task
                                }, 3000);
                            });
                        };
                    };

                    Service.TaskTableComponent.prototype.commit = function() {
                        this.arg.onConfigRetrieved = this.arg.onConfigRetrieved || function(componentConfig) {
                            return this.scope.onConfigRetrieved(componentConfig);
                        };
                        this.arg.onObjectInfoRetrieved = this.arg.onObjectInfoRetrieved || function(objectInfo) {
                            return this.scope.onObjectInfoRetrieved(objectInfo);
                        };
                        this.scope.componentHelper = new HelperObjectBrowserService.Component(this.arg);
                    };

                    return Service;

                } ]);
