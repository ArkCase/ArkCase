'use strict';

angular.module('dashboard.hoursSummary', [ 'adf.provider' ]).config(function(dashboardProvider) {
    dashboardProvider.widget('hoursSummary', {
        title : 'dashboard.widgets.hoursSummary.title',
        description : 'dashboard.widgets.hoursSummary.description',
        controller : 'Dashboard.HoursSummaryController',
        controllerAs : 'hoursSummary',
        reload : true,
        templateUrl : 'modules/dashboard/views/components/hours-summary-widget.client.view.html',
        commonName : 'hoursSummary'
    });
}).controller(
        'Dashboard.HoursSummaryController',
        [
                '$scope',
                '$translate',
                '$stateParams',
                '$filter',
                'UtilService',
                'TimeTracking.InfoService',
                'Helper.ObjectBrowserService',
                'ConfigService',
                'moment',
                'Helper.UiGridService',
                'Case.InfoService',
                'Helper.ModulesServicesStructure',
                function($scope, $translate, $stateParams, $filter, Util, TimeTrackingInfoService, HelperObjectBrowserService,
                        ConfigService, moment, HelperUiGridService, CaseInfoService, ModulesServicesStructure) {

                    var modules = ModulesServicesStructure.getModulesServiceStructure();

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

                    var onObjectInfoRetrieved = function(objectInfo) {
                        $scope.data = objectInfo.times;
                        _.forEach($scope.data, function(singleData) {
                            CaseInfoService.getCaseInfo(singleData.objectId).then(function(data) {
                                singleData.caseFile = data;
                            });
                        });
                        gridHelper.setWidgetsGridData($scope.data);
                    };

                    var onConfigRetrieved = function(componentConfig) {
                        var widgetInfo = _.find(componentConfig.widgets, function(widget) {
                            return widget.id === "hoursSummary";
                        });
                        gridHelper.setColumnDefs(widgetInfo);
                    };

                    var componentHelper = new HelperObjectBrowserService.Component({
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

                } ]);
