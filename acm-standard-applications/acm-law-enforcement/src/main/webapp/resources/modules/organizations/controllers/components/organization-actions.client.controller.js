'use strict';

angular.module('organizations').controller('Organizations.ActionsController', ['$scope', '$state', '$stateParams', '$q'
    , 'UtilService', 'ConfigService', 'ObjectService', 'Authentication', 'Object.LookupService'
    , 'Object.SubscriptionService', 'Organization.InfoService', 'Helper.ObjectBrowserService', 'Object.ModelService', 'Profile.UserInfoService', '$translate'
    , function ($scope, $state, $stateParams, $q
        , Util, ConfigService, ObjectService, Authentication, ObjectLookupService
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

        var activationMode = false;

        var onObjectInfoRetrieved = function (objectInfo) {
            $scope.restricted = objectInfo.restricted;
            $scope.objectInfo = objectInfo;
            $scope.$bus.subscribe("object.changed/ORGANIZATION/" + $scope.objectInfo.organizationId, function () {
                if (activationMode) {
                    $scope.$emit("report-tree-updated");
                    $scope.activationIcon = !Util.isEmpty(objectInfo.status) && objectInfo.status == "ACTIVE" ? "fa fa-stop" : "fa fa-play-circle";
                }
            });
            if ($scope.activationIcon != "fa fa-circle-o-notch fa-spin") {
                $scope.activationIcon = !Util.isEmpty(objectInfo.status) && objectInfo.status == "ACTIVE" ? "fa fa-stop" : "fa fa-play-circle";
                activationMode = false;
            }
        };

        $scope.onClickRestrict = function ($event) {
            if ($scope.restricted != $scope.objectInfo.restricted) {
                $scope.objectInfo.restricted = $scope.restricted;

                var organizationInfo = Util.omitNg($scope.objectInfo);
                OrganizationInfoService.saveOrganizationInfo(organizationInfo).then(function () {

                }, function () {
                    $scope.restricted = !$scope.restricted;
                });
            }
        };

        $scope.export = function () {
            console.log('button export clicked');
        };

        $scope.import = function () {
            console.log('button import clicked');
        };

        $scope.activate = function () {
            $scope.objectInfo.status = 'ACTIVE';
            $scope.activationIcon = "fa fa-circle-o-notch fa-spin";
            saveObjectInfoAndRefresh();
        };

        $scope.deactivate = function () {
            $scope.objectInfo.status = 'INACTIVE';
            $scope.activationIcon = "fa fa-circle-o-notch fa-spin";
            saveObjectInfoAndRefresh();
        };

        $scope.merge = function () {
            console.log('button merge clicked');
        };

        $scope.refresh = function () {
            $scope.$emit('report-object-refreshed', $stateParams.id);
        };

        function saveObjectInfoAndRefresh(state) {
            var promiseSaveInfo = Util.errorPromise($translate.instant("common.service.error.invalidData"));
            if (OrganizationInfoService.validateOrganizationInfo($scope.objectInfo)) {
                var objectInfo = Util.omitNg($scope.objectInfo);
                promiseSaveInfo = OrganizationInfoService.saveOrganizationInfo(objectInfo);
                promiseSaveInfo.then(
                    function (objectInfo) {
                        $scope.$emit("report-object-updated", objectInfo);
                        activationMode = true;
                        return objectInfo;
                    }
                    , function (error) {
                        $scope.$emit("report-object-update-failed", error);
                        $scope.activationIcon = "fa fa-stop";
                        return error;
                    }
                );
            }
            return promiseSaveInfo;
        }
    }
]);