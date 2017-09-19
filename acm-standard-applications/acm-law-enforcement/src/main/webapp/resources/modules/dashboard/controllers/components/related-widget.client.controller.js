'use strict';

angular.module('dashboard.related', ['adf.provider'])
    .config(function (dashboardProvider) {
        dashboardProvider
            .widget('related', {
                    title: 'dashboard.widgets.related.title',
                    description: 'dashboard.widgets.related.description',
                    controller: 'Dashboard.RelatedController',
                    reload: true,
                    templateUrl: 'modules/dashboard/views/components/related-widget.client.view.html',
                    commonName: 'related'
                }
            );
    })
    .controller('Dashboard.RelatedController', ['$scope', '$stateParams', '$translate',
        'Person.InfoService', 'ObjectAssociation.Service', 'ObjectService', 'UtilService', 'Helper.ObjectBrowserService', 'Helper.UiGridService',
        function ($scope, $stateParams, $translate,
                  PersonInfoService, ObjectAssociationService, ObjectService, Util, HelperObjectBrowserService, HelperUiGridService) {

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
                $scope.objectInfo = objectInfo;
                refreshGridData(objectInfo.id, objectInfo.objectType);
            };

            function refreshGridData(objectId, objectType) {
                ObjectAssociationService.getObjectAssociations(objectId, objectType, 'PERSON').then(function (data) {
                    gridHelper.setWidgetsGridData(data.response.docs);
                });
            }

            var onConfigRetrieved = function (componentConfig) {
                var widgetInfo = _.find(componentConfig.widgets, function (widget) {
                    return widget.id === "related";
                });
                gridHelper.setColumnDefs(widgetInfo);
            };

            $scope.onClickObjLink = function (event, rowEntity) {
                event.preventDefault();
                var targetType = ObjectService.ObjectTypes.PERSON;
                var targetId = Util.goodMapValue(rowEntity, "target_object.object_id_s");
                gridHelper.showObject(targetType, targetId);
            };
        }
    ]);