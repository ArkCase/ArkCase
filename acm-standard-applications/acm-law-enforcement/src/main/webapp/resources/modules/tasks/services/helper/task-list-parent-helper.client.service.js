"use strict";

/**
 * @ngdoc service
 * @name services:Helper.ObjectBrowserService
 *
 * @description
 *
 * {@link https://***REMOVED***/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/services/helper/helper-objbrowser.client.service.js services/helper/helper-objbrowser.client.service.js}

 * Helper.ObjectBrowserService provide help for common functions for an object page. It includes navigation (or tree) part and content part.
 * Content part consists list of Components.
 * Tree helper uses 'object-tree' directive. Content helper includes component links and data loading. Component helper includes common object info handling
 */

angular
        .module('tasks')
        .factory(
                'Helper.TaskListParentNode',
                [
                        'UtilService',
                        'Helper.ObjectBrowserService',
                        'CostTracking.InfoService',
                        'Helper.UiGridService',
                        'Object.TaskService',
                        'Task.AlertsService',
                        'ObjectService',
                        'Task.WorkflowService',
                        'ModalDialogService',
                        function(Util, HelperObjectBrowserService, CostTrackingInfoService, HelperUiGridService, ObjectTaskService,
                                TaskAlertsService, ObjectService, TaskWorkflowService, ModalDialogService) {

                            var Service = {

                                TaskTableComponent : function(arg) {
                                    var that = this;
                                    that.arg = arg;
                                    that.scope = arg.scope;
                                    that.objectType = arg.objectType || that.scope.objectType;
                                    that.gridHelper = arg.gridHelper || new HelperUiGridService.Grid({
                                        scope : that.scope
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
                                        that.gridHelper.setColumnDefs(config);
                                        that.gridHelper.setBasicOptions(config);
                                        that.gridHelper.disableGridScrolling(config);
                                        that.gridHelper.setExternalPaging(config, that.scope.populateGridData);
                                        that.gridHelper.setUserNameFilter(that.promiseUsers);
                                        that.gridHelper.addButton(config, "delete", null, null, "isDeleteDisabled");
                                        that.scope.componentHelper.doneConfig(config);
                                    };
                                    that.scope.onObjectInfoRetrieved = arg.onObjectInfoRetrieved || function(object) {
                                        that.scope.objectInfo = object;
                                        that.scope.populateGridData();
                                        if (that.scope.afterObjectInfo) {
                                            that.scope.afterObjectInfo();
                                        }
                                    };

                                    that.scope.populateGridData = arg.populateGridData
                                            || function() {
                                                var currentObjectId = Util.goodMapValue(that.scope.objectInfo, "id");
                                                if (Util.goodPositive(currentObjectId, false)) {
                                                    ObjectTaskService.queryChildTasks(that.objectType, currentObjectId,
                                                            Util.goodValue(that.scope.start, 0), Util.goodValue(that.scope.pageSize, 10),
                                                            Util.goodValue(that.scope.sort.by), Util.goodValue(that.scope.sort.dir)).then(
                                                            function(data) {
                                                                var tasks = data.response.docs;
                                                                angular.forEach(tasks, function(task) {
                                                                    //calculate to show alert icons if task is in overdue or deadline is approaching
                                                                    task.isOverdue = TaskAlertsService.calculateOverdue(new Date(
                                                                            task.due_tdt));
                                                                    task.isDeadline = TaskAlertsService.calculateDeadline(new Date(
                                                                            task.due_tdt));
                                                                });
                                                                that.scope.gridOptions = that.scope.gridOptions || {};
                                                                that.scope.gridOptions.data = tasks;
                                                                that.scope.gridOptions.totalItems = data.response.numFound;

                                                                return data;
                                                            });
                                                }
                                            };

                                    that.scope.isDeleteDisabled = arg.isDeleteDisabled || function(rowEntity) {
                                        return !rowEntity.adhocTask_b;
                                    };

                                    that.scope.onClickObjLink = arg.onClickObjLink
                                            || function(event, rowEntity) {
                                                event.preventDefault();
                                                var targetType = (Util.goodMapValue(rowEntity, "adhocTask_b", false)) ? ObjectService.ObjectTypes.ADHOC_TASK
                                                        : ObjectService.ObjectTypes.TASK;
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
                                        moduleName : "tasks",
                                        templateUrl : "modules/tasks/views/components/task-new-task.client.view.html",
                                        controllerName : "Tasks.NewTaskController",
                                        params : {
                                            parentType : that.objectType,
                                            parentObject : that.scope.parentObject,
                                            parentId : that.scope.objectInfo.id,
                                            parentTitle : that.scope.objectInfo.title,
                                            taskType : 'ACM_TASK'
                                        }
                                    };
                                    ModalDialogService.showModal(modalMetadata);
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