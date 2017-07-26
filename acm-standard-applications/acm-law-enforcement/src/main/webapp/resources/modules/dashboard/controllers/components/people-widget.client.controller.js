'use strict';

angular.module('dashboard.people', ['adf.provider'])
    .config(function (dashboardProvider) {
        dashboardProvider
            .widget('people', {
                title: 'dashboard.widgets.people.title',
                description: 'dashboard.widgets.people.description',
                controller: 'Dashboard.PeopleController',
                reload: true,
                templateUrl: 'modules/dashboard/views/components/people-widget.client.view.html',
                commonName: 'people'
            });
    })
    .controller('Dashboard.PeopleController', ['$scope', '$stateParams', '$translate',
        'Case.InfoService', 'Complaint.InfoService', 'Organization.InfoService', 'Helper.ObjectBrowserService', 'Helper.UiGridService',
            function ($scope, $stateParams, $translate,
                      CaseInfoService, ComplaintInfoService, OrganizationInfoService, HelperObjectBrowserService, HelperUiGridService) {

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
                        },
                        {
                            name: "ORGANIZATION",
                            configName: "organizations",
                            getInfo: OrganizationInfoService.getOrganizationInfo,
                            validateInfo: OrganizationInfoService.validateOrganizationInfo
                        }
                ];

            var module = _.find(modules, function (module) {
                return module.name == $stateParams.type;
            });

            $scope.gridOptions = {
                enableColumnResizing: true,
                columnDefs: []
            };

            var gridHelper = new HelperUiGridService.Grid({scope: $scope});

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
                gridHelper.setWidgetsGridData(objectInfo.personAssociations);
            };

            var onConfigRetrieved = function (componentConfig) {
                var widgetInfo = _.find(componentConfig.widgets, function (widget) {
                    return widget.id === "people";
                });
                $scope.gridOptions.columnDefs = widgetInfo ? widgetInfo.columnDefs : [];
            };
        }
    ]);