'use strict';

angular.module('dashboard.complaints', ['adf.provider'])
    .config(function (dashboardProvider) {
        dashboardProvider
            .widget('complaints', {
                title: 'dashboard.widgets.complaints.title',
                description: 'dashboard.widgets.complaints.description',
                controller: 'Dashboard.ComplaintsController',
                reload: true,
                templateUrl: 'modules/dashboard/views/components/complaints-widget.client.view.html',
                commonName: 'complaints'
            });
    })
    .controller('Dashboard.ComplaintsController', ['$scope', '$stateParams', '$translate',
        'Person.InfoService', 'Organization.InfoService', 'OrganizationAssociation.Service', 'PersonAssociation.Service', 'ObjectService', 'Helper.ObjectBrowserService', 'Helper.UiGridService',
            function ($scope, $stateParams, $translate,
                  PersonInfoService, OrganizationInfoService, OrganizationAssociationService, PersonAssociationService, ObjectService, HelperObjectBrowserService, HelperUiGridService) {

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
                if(objectInfo.objectType == "ORGANIZATION") {
                    OrganizationAssociationService.getOrganizationAssociations(objectInfo.organizationId, ObjectService.ObjectTypes.COMPLAINT).then(function (data) {
                        $scope.gridOptions.data = data.response.docs;
                    });
                }
                else {
                    PersonAssociationService.getPersonAssociations(objectInfo.id, ObjectService.ObjectTypes.COMPLAINT).then(function (data) {
                        $scope.gridOptions.data = data.response.docs;
                    });
                }
            };

            var onConfigRetrieved = function (componentConfig) {
                var widgetInfo = _.find(componentConfig.widgets, function (widget) {
                    return widget.id === "complaints";
                });
                gridHelper.setColumnDefs(widgetInfo);
            };

        }
    ]);