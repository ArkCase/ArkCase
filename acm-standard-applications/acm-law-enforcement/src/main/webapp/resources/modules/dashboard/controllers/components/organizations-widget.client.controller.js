'use strict';

angular.module('dashboard.organizations', ['adf.provider'])
    .config(function (dashboardProvider) {
        dashboardProvider
            .widget('organizations', {
                    title: 'dashboard.widgets.organizations.title',
                    description: 'dashboard.widgets.organizations.description',
                    controller: 'Dashboard.OrganizationsController',
                    reload: true,
                    templateUrl: 'modules/dashboard/views/components/organizations-widget.client.view.html',
                    commonName: 'organizations'
                }
            );
    })
    .controller('Dashboard.OrganizationsController', ['$scope', '$stateParams', 'Case.InfoService', 'Complaint.InfoService', 'Helper.ObjectBrowserService'
        , function ($scope, $stateParams, CaseInfoService, ComplaintInfoService, HelperObjectBrowserService) {
            var modules = [
                {
                    name: "CASE_FILE",
                    configName: "cases",
                    getInfo: CaseInfoService.getCaseInfo,
                    validateInfo: CaseInfoService.validateCaseInfo
                },
                {
                    name: "COMPLAINT",
                    configName: "complaints",
                    getInfo: ComplaintInfoService.getComplaintInfo,
                    validateInfo: ComplaintInfoService.validateComplaintInfo
                }
            ];

            var module = _.find(modules, function (module) {
                return module.name == $stateParams.type;
            });

            $scope.gridOptions = {
                enableColumnResizing: true,
                columnDefs: []
            };

            new HelperObjectBrowserService.Component({
                scope: $scope
                , stateParams: $stateParams
                , moduleId: module.configName
                , componentId: "main"
                , retrieveObjectInfo: module.getInfo
                , validateObjectInfo: module.validateInfo
                , onObjectInfoRetrieved: function (objectInfo) {
                    onObjectInfoRetrieved(objectInfo);
                }
                , onConfigRetrieved: function (componentConfig) {
                    onConfigRetrieved(componentConfig);
                }
            });

            var onObjectInfoRetrieved = function (objectInfo) {
                if(objectInfo.organizationAssociations) {
                    $scope.gridOptions.data = objectInfo.organizationAssociations ? objectInfo.organizationAssociations : [];
                    $scope.gridOptions.totalItems = $scope.gridOptions.data.length;
                    $scope.gridOptions.noData = false;
                }
                else
                {
                    $scope.gridOptions.data = [];
                    $scope.gridOptions.noData = true;
                }
            };

            var onConfigRetrieved = function (componentConfig) {
                var widgetInfo = _.find(componentConfig.widgets, function (widget) {
                    return widget.id === "organizations";
                });
                $scope.gridOptions.columnDefs = widgetInfo ? widgetInfo.columnDefs : [];
            };
        }
    ]);