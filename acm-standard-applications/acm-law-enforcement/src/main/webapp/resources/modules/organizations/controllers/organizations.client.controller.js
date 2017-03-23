'use strict';

angular.module('organizations').controller('OrganizationsController', ['$scope', '$state', '$stateParams', '$translate'
    , 'UtilService', 'ConfigService', 'Organization.InfoService', 'ObjectService', 'Helper.ObjectBrowserService'
    , function ($scope, $state, $stateParams, $translate
        , Util, ConfigService, OrganizationInfoService, ObjectService, HelperObjectBrowserService) {

        new HelperObjectBrowserService.Content({
            scope: $scope
            , state: $state
            , stateParams: $stateParams
            , moduleId: "organizations"
            , resetObjectInfo: OrganizationInfoService.resetOrganizationInfo
            , getObjectInfo: OrganizationInfoService.getOrganizationInfo
            , updateObjectInfo: OrganizationInfoService.updateOrganizationInfo
            , getObjectIdFromInfo: function (organizationInfo) {
                return Util.goodMapValue(organizationInfo, "organizationId");
            }
            , getObjectTypeFromInfo: function (organizationInfo) {
                return ObjectService.ObjectTypes.ORGANIZATION;
            }
        });
    }
]);