'use strict';

angular.module('dashboard.hoursSummary', [ 'adf.provider' ]).config(function(dashboardProvider) {
    dashboardProvider.widget('hoursSummary', {
        title: 'preference.overviewWidgets.hoursSummary.title',
        description: 'dashboard.widgets.hoursSummary.description',
        controller: 'Dashboard.HoursSummaryController',
        controllerAs: 'hoursSummary',
        reload: true,
        templateUrl: 'modules/dashboard/views/components/hours-summary-widget.client.view.html',
        commonName: 'hoursSummary'
    });
}).controller(
        'Dashboard.HoursSummaryController',
        [ '$scope', '$translate', '$stateParams', '$filter', 'UtilService', 'TimeTracking.InfoService', 'Helper.ObjectBrowserService', 'ConfigService', 'moment', 'Helper.UiGridService', 'Case.InfoService', 'Helper.ModulesServicesStructure', "ObjectService",
                function($scope, $translate, $stateParams, $filter, Util, TimeTrackingInfoService, HelperObjectBrowserService, ConfigService, moment, HelperUiGridService, CaseInfoService, ModulesServicesStructure, ObjectService) {

                    var modules = ModulesServicesStructure.getModulesServiceStructure();

                    var module = _.find(modules, function(module) {
                        return module.name == $stateParams.type;
                    });

                    $scope.gridOptions = {
                        enableColumnResizing: true,
                        columnDefs: []
                    };

                    var gridHelper = new HelperUiGridService.Grid({
                        scope: $scope
                    });

                    var decorateObject = function(objectInfo) {
                        if (objectInfo.type == ObjectService.ObjectTypes.CASE_FILE) {
                            objectInfo.object.number = objectInfo.object.caseNumber;
                        } else if (objectInfo.type == ObjectService.ObjectTypes.COMPLAINT) {
                            objectInfo.object.number = objectInfo.object.complaintNumber;
                            objectInfo.object.title = objectInfo.object.complaintTitle;
                        } else {
                            if (objectInfo.object == undefined) {
                                objectInfo.object = {};
                            }
                            objectInfo.object.title = objectInfo.code;
                            objectInfo.object.number = "";
                        }

                        objectInfo.date = moment(new Date(objectInfo.date)).format($translate.instant('common.defaultDateFormat'));
                        return objectInfo;
                    };
                    var onObjectInfoRetrieved = function(objectInfo) {
                        $scope.data = angular.copy(objectInfo.times);

                        _.forEach($scope.data, function(object) {
                            var getAdditionalDataInfo = _.find(modules, function(module) {
                                return module.name == object.type
                            });
                            if (getAdditionalDataInfo != undefined) {
                                getAdditionalDataInfo.getInfo(object.objectId).then(function(res) {
                                    object.object = res;
                                    object = decorateObject(object);
                                });
                            } else {
                                object = decorateObject(object);
                            }
                        });

                        gridHelper.setWidgetsGridData($scope.data);
                    };

                    var onConfigRetrieved = function(componentConfig) {
                        $scope.widgetInfo = _.find(componentConfig.widgets, function(widget) {
                            return widget.id === "hoursSummary";
                        });

                        gridHelper.setColumnDefs($scope.widgetInfo);
                    };

                    var componentHelper = new HelperObjectBrowserService.Component({
                        scope: $scope,
                        stateParams: $stateParams,
                        moduleId: module.configName,
                        componentId: "main",
                        retrieveObjectInfo: module.getInfo,
                        validateObjectInfo: module.validateInfo,
                        onObjectInfoRetrieved: function(objectInfo) {
                            onObjectInfoRetrieved(objectInfo);
                        },
                        onConfigRetrieved: function(componentConfig) {
                            onConfigRetrieved(componentConfig);
                        }
                    });

                } ]);
