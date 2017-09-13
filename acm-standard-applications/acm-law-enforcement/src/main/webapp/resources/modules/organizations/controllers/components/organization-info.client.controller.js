'use strict';

angular.module('organizations').controller('Organizations.InfoController', ['$scope', '$stateParams', '$translate', '$modal'
    , 'Organization.InfoService', 'Helper.ObjectBrowserService', 'UtilService'
    , function ($scope, $stateParams, $translate, $modal
        , OrganizationInfoService, HelperObjectBrowserService, Util) {

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

        var onObjectInfoRetrieved = function (objectInfo) {
            $scope.objectInfo = objectInfo;

        };

        $scope.addParent = function () {

            var params = {};
            if ($scope.objectInfo.parentOrganization != null) {
                params.organizationId = $scope.objectInfo.parentOrganization.organizationId;
                params.organizationValue = $scope.objectInfo.parentOrganization.organizationValue;
            }

            var modalInstance = $modal.open({
                scope: $scope,
                animation: true,
                templateUrl: 'modules/common/views/add-organization-modal.client.view.html',
                controller: 'Common.AddOrganizationModalController',
                size: 'md',
                backdrop: 'static',
                resolve: {
                    params: function () {
                        return params;
                    }
                }
            });

            modalInstance.result.then(function (data) {
                if (data.isNew) {
                    $scope.objectInfo.parentOrganization = data.organization;
                    $scope.saveOrganization();
                } else {
                    OrganizationInfoService.getOrganizationInfo(data.organizationId).then(function (organization) {
                        $scope.objectInfo.parentOrganization = organization;
                        $scope.saveOrganization();
                    })
                }
            });
        };

        $scope.saveOrganization = function () {
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