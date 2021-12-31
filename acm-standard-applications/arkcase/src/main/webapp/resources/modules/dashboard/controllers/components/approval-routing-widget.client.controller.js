'use strict';

angular.module('dashboard.approvalRouting', [ 'adf.provider' ]).config(function(dashboardProvider) {
    dashboardProvider.widget('approvalRouting', {
        title: 'preference.overviewWidgets.approvalRouting.title',
        description: 'dashboard.widgets.approvalRouting.description',
        controller: 'Dashboard.ApprovalRoutingController',
        reload: true,
        templateUrl: 'modules/dashboard/views/components/approval-routing-widget.client.view.html',
        commonName: 'approvalRouting'
    });
}).controller(
        'Dashboard.ApprovalRoutingController',
        [ '$scope', '$stateParams', '$q', '$translate', '$filter', 'ObjectService', 'Object.TaskService', 'UtilService', 'Case.InfoService', 'Complaint.InfoService', 'Task.InfoService', 'ConfigService', 'Helper.ObjectBrowserService',
                function($scope, $stateParams, $q, $translate, $filter, ObjectService, ObjectTaskService, Util, CaseInfoService, ComplaintInfoService, TaskInfoService, ConfigService, HelperObjectBrowserService) {

                    var modules = [ {
                        name: "CASE_FILE",
                        configName: "cases",
                        getInfo: CaseInfoService.getCaseInfo,
                        objectType: ObjectService.ObjectTypes.CASE_FILE,
                        validateInfo: CaseInfoService.validateCaseInfo
                    }, {
                        name: "COMPLAINT",
                        configName: "complaints",
                        getInfo: ComplaintInfoService.getComplaintInfo,
                        objectType: ObjectService.ObjectTypes.COMPLAINT,
                        validateInfo: ComplaintInfoService.validateComplaintInfo
                    } ];

                    var module = _.find(modules, function(module) {
                        return module.name == $stateParams.type;
                    });

                    $scope.gridOptions = {
                        enableColumnResizing: true,
                        columnDefs: []
                    };

                    var currentObjectId = HelperObjectBrowserService.getCurrentObjectId();
                    if (module && Util.goodPositive(currentObjectId, false)) {
                        ObjectTaskService.queryChildTasks(module.name, currentObjectId, 0, 100, '', '').then(function(queryChildResult) {
                            var tasks = queryChildResult.response.docs;
                            var objectId = _.result(_.find(tasks, function(task) {
                                return task.status_lcs === 'ACTIVE' && task.business_process_name_lcs === 'ArkCase Buckslip Process';
                            }), 'object_id_s');

                            if (!Util.isEmpty(objectId)) {

                                TaskInfoService.getTaskInfo(objectId).then(function(taskInfo) {
                                    $scope.objectInfo = taskInfo;
                                    if ($scope.objectInfo.buckslipFutureApprovers && $scope.objectInfo.buckslipPastApprovers) {
                                        var data = [];
                                        var currentApprover = {};
                                        currentApprover.status = "Current";
                                        currentApprover.name = $scope.objectInfo.assignee;
                                        var dueDateFormated = moment($scope.objectInfo.dueDate).format($translate.instant('common.defaultDateFormat'));
                                        currentApprover.date = "Due: " + dueDateFormated;

                                        data.push(currentApprover);

                                        if (!Util.isArrayEmpty($scope.objectInfo.buckslipFutureApprovers))
                                            for (var i = 0; i < $scope.objectInfo.buckslipFutureApprovers.length; i++) {
                                                var futureApprover = {};
                                                futureApprover.status = "Future";
                                                futureApprover.name = $scope.objectInfo.buckslipFutureApprovers[i].fullName;
                                                futureApprover.date = "";
                                                data.push(futureApprover);
                                            }

                                        if (!Util.isArrayEmpty($scope.objectInfo.buckslipPastApprovers))
                                            for (var i = 0; i < $scope.objectInfo.buckslipPastApprovers.length; i++) {
                                                var pastApprover = {};
                                                pastApprover.status = "Past";
                                                pastApprover.name = $scope.objectInfo.buckslipPastApprovers[i].name;
                                                var doneDateFormated = moment($scope.objectInfo.buckslipPastApprovers[i].date).format($translate.instant('common.defaultDateFormat'));
                                                pastApprover.date = "Done: " + doneDateFormated;
                                                data.push(pastApprover);
                                            }

                                        $scope.gridOptions.data = data;
                                        $scope.gridOptions.noData = false;
                                    } else {
                                        setEmptyGrid();
                                    }
                                });
                            } else {
                                setEmptyGrid();
                            }
                        });
                    }

                    function setEmptyGrid() {
                        $scope.gridOptions.data = [];
                        $scope.gridOptions.noData = true;
                        $scope.noDataMessage = $translate.instant('dashboard.widgets.approvalRouting.noDataMessage');
                    }
                } ]);