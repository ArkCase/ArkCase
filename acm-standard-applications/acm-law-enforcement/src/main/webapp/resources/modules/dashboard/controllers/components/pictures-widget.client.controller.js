'use strict';

angular.module('dashboard.pictures', ['adf.provider'])
    .config(function (dashboardProvider) {
        dashboardProvider
            .widget('pictures', {
                    title: 'dashboard.widgets.pictures.title',
                    description: 'dashboard.widgets.pictures.description',
                    controller: 'Dashboard.PicturesController',
                    reload: true,
                    templateUrl: 'modules/dashboard/views/components/pictures-widget.client.view.html',
                    commonName: 'pictures'
                }
            );
    })
    .controller('Dashboard.PicturesController', ['$scope', '$stateParams', '$translate',
        'Person.InfoService', 'Helper.ObjectBrowserService', 'Helper.UiGridService',
        function ($scope, $stateParams, $translate,
                  PersonInfoService, HelperObjectBrowserService, HelperUiGridService) {

            var modules = [
                {
                    name: "PERSON",
                    configName: "people",
                    getInfo: PersonInfoService.getPersonInfo,
                    validateInfo: PersonInfoService.validatePersonInfo
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
                gridHelper.setWidgetsGridData(objectInfo.pictures);
            };

            var onConfigRetrieved = function (componentConfig) {
                var widgetInfo = _.find(componentConfig.widgets, function (widget) {
                    return widget.id === "pictures";
                });
                gridHelper.setColumnDefs(widgetInfo);
            };

        }
    ]);