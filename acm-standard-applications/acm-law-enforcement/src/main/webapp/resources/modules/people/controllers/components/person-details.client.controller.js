'use strict';

angular.module('people').controller('People.DetailsController', ['$scope', '$stateParams', '$translate'
    , 'UtilService', 'ConfigService', 'Person.InfoService', 'MessageService', 'Helper.ObjectBrowserService'
    , function ($scope, $stateParams, $translate
        , Util, ConfigService, PersonInfoService, MessageService, HelperObjectBrowserService) {

        new HelperObjectBrowserService.Component({
            scope: $scope
            , stateParams: $stateParams
            , moduleId: "people"
            , componentId: "details"
            , retrieveObjectInfo: PersonInfoService.getPersonInfo
            , validateObjectInfo: PersonInfoService.validatePersonInfo
        });


        $scope.options = {
            focus: true,
            dialogsInBody: true
            //,height: 120
        };

        $scope.saveDetails = function () {
            var promiseSaveInfo = Util.errorPromise($translate.instant("common.service.error.invalidData"));
            if (PersonInfoService.validatePersonInfo($scope.objectInfo)) {
                var objectInfo = Util.omitNg($scope.objectInfo);
                promiseSaveInfo = PersonInfoService.savePersonInfo(objectInfo);
                promiseSaveInfo.then(
                    function (objectInfo) {
                        $scope.$emit("report-object-updated", objectInfo);
                        MessageService.info($translate.instant("people.comp.details.informSaved"));
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