'use strict';

angular.module('people').controller('People.ActionsController', ['$scope', '$state', '$stateParams', '$q'
    , 'UtilService', 'ConfigService', 'ObjectService', 'Authentication', 'Object.LookupService', 'Person.LookupService'
    , 'Object.SubscriptionService', 'Person.InfoService', 'Helper.ObjectBrowserService', 'Object.ModelService', 'Profile.UserInfoService', '$translate'
    , function ($scope, $state, $stateParams, $q
        , Util, ConfigService, ObjectService, Authentication, ObjectLookupService, PersonLookupService
        , ObjectSubscriptionService, PersonInfoService, HelperObjectBrowserService, ObjectModelService, UserInfoService, $translate) {

        new HelperObjectBrowserService.Component({
            scope: $scope
            , stateParams: $stateParams
            , moduleId: "people"
            , componentId: "actions"
            , retrieveObjectInfo: PersonInfoService.getPersonInfo
            , validateObjectInfo: PersonInfoService.validatePersonInfo
            , onObjectInfoRetrieved: function (objectInfo) {
                onObjectInfoRetrieved(objectInfo);
            }
        });

        var onObjectInfoRetrieved = function (objectInfo) {
            $scope.restricted = objectInfo.restricted;
            $scope.objectInfo = objectInfo;
        };

        $scope.onClickRestrict = function ($event) {
            if ($scope.restricted != $scope.objectInfo.restricted) {
                $scope.objectInfo.restricted = $scope.restricted;

                var personInfo = Util.omitNg($scope.objectInfo);
                PersonInfoService.savePersonInfo(personInfo).then(function () {

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
            if (PersonInfoService.validatePersonInfo($scope.objectInfo)) {
                var objectInfo = Util.omitNg($scope.objectInfo);
                promiseSaveInfo = PersonInfoService.savePersonInfo(objectInfo);
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