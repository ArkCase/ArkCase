'use strict';

angular.module('dashboard.relOrganizations', ['adf.provider'])
    .config(function (dashboardProvider) {
        dashboardProvider
            .widget('relOrganizations', {
                    title: 'dashboard.widgets.relOrganizations.title',
                    description: 'dashboard.widgets.relOrganizations.description',
                    controller: 'Dashboard.RelatedOrganizationsController',
                    reload: true,
                    templateUrl: 'modules/dashboard/views/components/related-organizations-widget.client.view.html',
                    commonName: 'relOrganizations'
                }
            );
    })
    .controller('Dashboard.RelatedOrganizationsController', ['$scope', '$stateParams', '$translate',
        'Organization.InfoService', 'ObjectAssociation.Service', 'Helper.ObjectBrowserService', 'Helper.UiGridService', 'Object.LookupService',
        function ($scope, $stateParams, $translate,
                  OrganizationInfoService, ObjectAssociationService, HelperObjectBrowserService, HelperUiGridService, ObjectLookupService) {

            var modules = [
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
                refreshGridData(objectInfo.organizationId, objectInfo.objectType);
            };

            function refreshGridData(objectId, objectType) {
                ObjectAssociationService.getObjectAssociations(objectId, objectType, 'ORGANIZATION').then(function (data) {
                    gridHelper.setWidgetsGridData(data.response.docs);
                });
            }

            var onConfigRetrieved = function (componentConfig) {
                var widgetInfo = _.find(componentConfig.widgets, function (widget) {
                    return widget.id === "relOrganizations";
                });
                gridHelper.setColumnDefs(widgetInfo);
            };

            $scope.relationshipTypes = [];
            ObjectLookupService.getOrganizationRelationTypes().then(
                function (relationshipTypes) {
                    for (var i = 0; i < relationshipTypes.length; i++) {
                        $scope.relationshipTypes.push({"key": relationshipTypes[i].inverseKey, "value" : relationshipTypes[i].inverseValue, "inverseKey": relationshipTypes[i].key, "inverseValue": relationshipTypes[i].value});
                    }

                    return relationshipTypes;
                });

            $scope.getLookupValue = function(value, key){
                return ObjectLookupService.getLookupValue(value, key);
            };
        }
    ]);