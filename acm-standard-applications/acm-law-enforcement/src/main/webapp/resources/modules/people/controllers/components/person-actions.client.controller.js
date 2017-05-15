'use strict';

angular.module('people').controller('People.ActionsController', ['$scope', '$state', '$stateParams', '$q'
    , 'UtilService', 'ConfigService', 'ObjectService', 'Authentication', 'Object.LookupService', 'Person.LookupService'
    , 'Object.SubscriptionService', 'Person.InfoService', 'Helper.ObjectBrowserService', 'Object.ModelService', 'Profile.UserInfoService'
    , function ($scope, $state, $stateParams, $q
        , Util, ConfigService, ObjectService, Authentication, ObjectLookupService, PersonLookupService
        , ObjectSubscriptionService, PersonInfoService, HelperObjectBrowserService, ObjectModelService, UserInfoService) {

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
            $scope.objectInfo = objectInfo;
        };

        $scope.export = function () {
            console.log('button export clicked');
        };

        $scope.import = function () {
            console.log('button import clicked');
        };

        $scope.delete = function () {
            console.log('button delete clicked');
        };

        $scope.activate = function () {
            console.log('button activate clicked');
        };

        $scope.deactivate = function () {
            console.log('button deactivate clicked');
        };

        $scope.merge = function () {
            console.log('button merge clicked');
        };

        $scope.refresh = function () {
            $scope.$emit('report-object-refreshed', $stateParams.id);
        };
    }
]);