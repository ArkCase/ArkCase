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
            var personInfo = Util.omitNg($scope.objectInfo);
            PersonInfoService.savePersonInfo(personInfo).then(
                function (personInfo) {
                    MessageService.info($translate.instant("people.comp.details.informSaved"));
                    return personInfo;
                }
            );
        };
    }
]);