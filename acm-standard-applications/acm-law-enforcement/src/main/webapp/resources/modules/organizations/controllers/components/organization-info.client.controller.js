'use strict';

angular.module('organizations').controller('Organizations.InfoController', ['$scope', '$stateParams', '$translate', '$timeout'
    , 'UtilService', 'Util.DateService', 'ConfigService', 'Object.LookupService', 'Organization.LookupService', 'Organization.InfoService'
    , 'Object.ModelService', 'Helper.ObjectBrowserService', 'MessageService', 'ObjectService', 'Helper.UiGridService', '$modal'
    , 'Object.ParticipantService', '$q', '$filter', 'SearchService', 'Search.QueryBuilderService'
    , function ($scope, $stateParams, $translate, $timeout
        , Util, UtilDateService, ConfigService, ObjectLookupService, OrganizationLookupService, OrganizationInfoService
        , ObjectModelService, HelperObjectBrowserService, MessageService, ObjectService, HelperUiGridService, $modal, ObjectParticipantService, $q, $filter
        , SearchService, SearchQueryBuilder) {

        new HelperObjectBrowserService.Component({
            scope: $scope
            , stateParams: $stateParams
            , moduleId: "organizations"
            , componentId: "info"
            , retrieveObjectInfo: OrganizationInfoService.getOrganizationInfo
            , validateObjectInfo: OrganizationInfoService.validateOrganizationInfo
            , onObjectInfoRetrieved: function (objectInfo) {
                onObjectInfoRetrieved(objectInfo);
            }
        });

        var gridHelper = new HelperUiGridService.Grid({scope: $scope});
        var promiseUsers = gridHelper.getUsers();
        var promiseConfig = ConfigService.getModuleConfig("organizations");

        $q.all([promiseConfig]).then(function (data) {
            var foundComponent = data[0].components.filter(function (component) {
                return component.title === 'Participants';
            });
            $scope.config = foundComponent[0];
        });


        $scope.defaultDatePickerFormat = UtilDateService.defaultDatePickerFormat;
        $scope.picker = {opened: false};
        $scope.onPickerClick = function () {
            $scope.picker.opened = true;
        };


        var onObjectInfoRetrieved = function (objectInfo) {
            $scope.objectInfo = objectInfo;

        };
    }
]);