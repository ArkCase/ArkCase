'use strict';

angular.module('dashboard.ids', [ 'adf.provider' ]).config(function(dashboardProvider) {
    dashboardProvider.widget('ids', {
        title : 'preference.overviewWidgets.ids.title',
        description : 'dashboard.widgets.ids.description',
        controller : 'Dashboard.IdsController',
        reload : true,
        templateUrl : 'modules/dashboard/views/components/ids-widget.client.view.html',
        commonName : 'ids'
    });
}).controller(
        'Dashboard.IdsController',
        [
                '$scope',
                '$stateParams',
                '$translate',
                'Person.InfoService',
                'Organization.InfoService',
                'Helper.ObjectBrowserService',
                'Helper.UiGridService',
                'Object.LookupService',
                'Object.ModelService',
                function($scope, $stateParams, $translate, PersonInfoService, OrganizationInfoService, HelperObjectBrowserService,
                        HelperUiGridService, ObjectLookupService, ObjectModelService) {

                    var modules = [ {
                        name : "PERSON",
                        configName : "people",
                        getInfo : PersonInfoService.getPersonInfo,
                        validateInfo : PersonInfoService.validatePersonInfo
                    }, {
                        name : "ORGANIZATION",
                        configName : "organizations",
                        getInfo : OrganizationInfoService.getOrganizationInfo,
                        validateInfo : OrganizationInfoService.validateOrganizationInfo
                    } ];

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

                    new HelperObjectBrowserService.Component({
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

                    var onObjectInfoRetrieved = function(objectInfo) {
                        gridHelper.setWidgetsGridData(objectInfo.identifications);
                    };

                    var onConfigRetrieved = function(componentConfig) {
                        var widgetInfo = _.find(componentConfig.widgets, function(widget) {
                            return widget.id === "ids";
                        });
                        gridHelper.setColumnDefs(widgetInfo);
                    };

                    ObjectLookupService.getOrganizationIdTypes().then(function(organizationIdTypes) {
                        $scope.organizationIdTypes = organizationIdTypes;
                        return organizationIdTypes;
                    });

                    ObjectLookupService.getIdentificationTypes().then(function(identificationTypes) {
                        $scope.identificationTypes = identificationTypes;
                        return identificationTypes;
                    });

                    $scope.isDefault = function(data) {
                        return ObjectModelService.isObjectReferenceSame($scope.objectInfo, data, "defaultIdentification");
                    }
                } ]);