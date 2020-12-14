'use strict';

angular.module('dashboard.pictures', [ 'adf.provider' ]).config(function(dashboardProvider) {
    dashboardProvider.widget('pictures', {
        title: 'preference.overviewWidgets.pictures.title',
        description: 'dashboard.widgets.pictures.description',
        controller: 'Dashboard.PicturesController',
        reload: true,
        templateUrl: 'modules/dashboard/views/components/pictures-widget.client.view.html',
        commonName: 'pictures'
    });
}).controller('Dashboard.PicturesController',
        [ '$scope', '$stateParams', '$translate', 'Person.InfoService', 'Person.PicturesService', 'Helper.ObjectBrowserService', 'Helper.UiGridService', function($scope, $stateParams, $translate, PersonInfoService, PersonPicturesService, HelperObjectBrowserService, HelperUiGridService) {

            var modules = [ {
                name: "PERSON",
                configName: "people",
                getInfo: PersonInfoService.getPersonInfo,
                validateInfo: PersonInfoService.validatePersonInfo
            } ];

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

            new HelperObjectBrowserService.Component({
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

            var onObjectInfoRetrieved = function(objectInfo) {
                $scope.objectInfo = objectInfo;
                refreshGridData(objectInfo.id);
            };

            function refreshGridData(objectId) {
                PersonPicturesService.listPersonPictures(objectId).then(function(result) {
                    gridHelper.setWidgetsGridData(result.response.docs);
                });
            }

            var onConfigRetrieved = function(componentConfig) {
                var widgetInfo = _.find(componentConfig.widgets, function(widget) {
                    return widget.id === "pictures";
                });
                gridHelper.setColumnDefs(widgetInfo);
            };
            $scope.isDefault = function(data) {
                if (data && data.object_id_s) {
                    var id = 0;
                    if ($scope.objectInfo.defaultPicture) {
                        id = $scope.objectInfo.defaultPicture.fileId;
                    }
                    return data.object_id_s == id;
                }
                if (data && data.fileId) {
                    var id = 0;
                    if ($scope.objectInfo.defaultPicture) {
                        id = $scope.objectInfo.defaultPicture.fileId;
                    }
                    return data.fileId == id;
                }
                if ($scope.images && $scope.images.length == 0) {
                    return true;
                }
                return false;
            };

        } ]);