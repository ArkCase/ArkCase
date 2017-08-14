'use strict';

angular.module('organizations').controller('Organizations.ActionsController', ['$scope', '$state', '$stateParams', '$q'
    , 'UtilService', 'ConfigService', 'ObjectService', 'Authentication', 'Object.LookupService', 'Organization.LookupService'
    , 'Object.SubscriptionService', 'Organization.InfoService', 'Helper.ObjectBrowserService', 'Object.ModelService', 'Profile.UserInfoService', '$translate'
    , function ($scope, $state, $stateParams, $q
        , Util, ConfigService, ObjectService, Authentication, ObjectLookupService, OrganizationLookupService
        , ObjectSubscriptionService, OrganizationInfoService, HelperObjectBrowserService, ObjectModelService, UserInfoService, $translate) {

        new HelperObjectBrowserService.Component({
            scope: $scope
            , stateParams: $stateParams
            , moduleId: "organizations"
            , componentId: "actions"
            , retrieveObjectInfo: OrganizationInfoService.getOrganizationInfo
            , validateObjectInfo: OrganizationInfoService.validateOrganizationInfo
            , onObjectInfoRetrieved: function (objectInfo) {
                onObjectInfoRetrieved(objectInfo);
            }
        });

        var onObjectInfoRetrieved = function (objectInfo) {
            $scope.objectInfo = objectInfo;
        };

        $scope.export = function () {
            console.log('button export clicked');
        };

        $scope.import = function () {
            console.log('button import clicked');
        };

        $scope.activate = function () {
            $scope.objectInfo.status = 'ACTIVE';
            saveObjectInfoAndRefresh();
        };

        $scope.deactivate = function () {
            $scope.objectInfo.status = 'INACTIVE';
            saveObjectInfoAndRefresh();
        };

        $scope.merge = function () {
            console.log('button merge clicked');
        };

        $scope.refresh = function () {
            $scope.$emit('report-object-refreshed', $stateParams.id);
        };

        function saveObjectInfoAndRefresh() {
            var promiseSaveInfo = Util.errorPromise($translate.instant("common.service.error.invalidData"));
            if (OrganizationInfoService.validateOrganizationInfo($scope.objectInfo)) {
                var objectInfo = Util.omitNg($scope.objectInfo);
                promiseSaveInfo = OrganizationInfoService.saveOrganizationInfo(objectInfo);
                promiseSaveInfo.then(
                    function (objectInfo) {
                        $scope.$emit("report-object-updated", objectInfo);
                        return objectInfo;
                    }
                    , function (error) {
                        $scope.$emit("report-object-update-failed", error);
                        return error;
                    }
                );
            }
            return promiseSaveInfo;
        }
    }
]);