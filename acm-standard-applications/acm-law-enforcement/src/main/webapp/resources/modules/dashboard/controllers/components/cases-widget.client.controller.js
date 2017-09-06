'use strict';

angular.module('dashboard.cases', ['adf.provider'])
    .config(function (dashboardProvider) {
        dashboardProvider
            .widget('cases', {
                title: 'dashboard.widgets.cases.title',
                description: 'dashboard.widgets.cases.description',
                controller: 'Dashboard.CasesController',
                reload: true,
                templateUrl: 'modules/dashboard/views/components/cases-widget.client.view.html',
                commonName: 'cases'
            });
    })
    .controller('Dashboard.CasesController', ['$scope', '$stateParams', '$translate',
        'Person.InfoService', 'Organization.InfoService', 'PersonAssociation.Service', 'Object.PersonService', 'OrganizationAssociation.Service', 'ObjectService', 'Helper.ObjectBrowserService', 'Helper.UiGridService',
            function ($scope, $stateParams, $translate,
                  PersonInfoService, OrganizationInfoService, PersonAssociationService, ObjectPersonService, OrganizationAssociationService, ObjectService, HelperObjectBrowserService, HelperUiGridService) {

            var modules = [
                {
                    name: "PERSON",
                    configName: "people",
                    getInfo: PersonInfoService.getPersonInfo,
                    validateInfo: PersonInfoService.validatePersonInfo
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
                $scope.objectInfo = objectInfo;
                OrganizationAssociationService.getOrganizationAssociations(objectInfo.organizationId, ObjectService.ObjectTypes.COMPLAINT).then(function (data) {
                    $scope.gridOptions.data = data.response.docs;
                });
            };

            var onConfigRetrieved = function (componentConfig) {
                var widgetInfo = _.find(componentConfig.widgets, function (widget) {
                    return widget.id === "cases";
                });
                gridHelper.setColumnDefs(widgetInfo);
            };

        }
    ]);