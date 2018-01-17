'use strict';

angular.module('dashboard.docReview', [ 'adf.provider' ]).config(function(dashboardProvider) {
    dashboardProvider.widget('docReview', {
        title : 'dashboard.widgets.docReview.title',
        description : 'dashboard.widgets.docReview.description',
        controller : 'Dashboard.DocReviewController',
        reload : true,
        templateUrl : 'modules/dashboard/views/components/doc-review-widget.client.view.html',
        commonName : 'docReview'
    });
}).controller(
        'Dashboard.DocReviewController',
        [ '$scope', '$stateParams', '$translate', 'Task.InfoService', 'Helper.ObjectBrowserService', 'Helper.UiGridService',
                function($scope, $stateParams, $translate, TaskInfoService, HelperObjectBrowserService, HelperUiGridService) {

                    var modules = [ {
                        name : "TASK",
                        configName : "tasks",
                        getInfo : TaskInfoService.getTaskInfo,
                        validateInfo : TaskInfoService.validateTaskInfo
                    }, {
                        name : "ADHOC",
                        configName : "tasks",
                        getInfo : TaskInfoService.getTaskInfo,
                        validateInfo : TaskInfoService.validateTaskInfo
                    } ];

                    var module = _.find(modules, function(module) {
                        return module.name == $stateParams.type;
                    });

                    $scope.gridOptions = {
                        enableColumnResizing : true,
                        columnDefs : []
                    };

                    var gridHelper = new HelperUiGridService.Grid({
                        scope : $scope
                    });

                    new HelperObjectBrowserService.Component({
                        scope : $scope,
                        stateParams : $stateParams,
                        moduleId : module.configName,
                        componentId : "main",
                        retrieveObjectInfo : module.getInfo,
                        validateObjectInfo : module.validateInfo,
                        onObjectInfoRetrieved : function(objectInfo) {
                            onObjectInfoRetrieved(objectInfo);
                        },
                        onConfigRetrieved : function(componentConfig) {
                            onConfigRetrieved(componentConfig);
                        }
                    });

                    var onObjectInfoRetrieved = function(objectInfo) {
                        gridHelper.setWidgetsGridData([ objectInfo.documentUnderReview ]);
                    };

                    var onConfigRetrieved = function(componentConfig) {
                        var widgetInfo = _.find(componentConfig.widgets, function(widget) {
                            return widget.id === "docsReview";
                        });
                        gridHelper.setColumnDefs(widgetInfo);
                    };
                } ]);