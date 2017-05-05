'use strict';

angular.module('people').controller('PeopleController', ['$scope', '$state', '$stateParams', '$translate'
    , 'UtilService', 'ConfigService', 'Person.InfoService', 'ObjectService', 'Helper.ObjectBrowserService'
    , function ($scope, $state, $stateParams, $translate
        , Util, ConfigService, PersonInfoService, ObjectService, HelperObjectBrowserService) {

        new HelperObjectBrowserService.Content({
            scope: $scope
            , state: $state
            , stateParams: $stateParams
            , moduleId: "people"
            , resetObjectInfo: PersonInfoService.resetPersonInfo
            , getObjectInfo: PersonInfoService.getPersonInfo
            , updateObjectInfo: PersonInfoService.updatePersonInfo
            , getObjectIdFromInfo: function (personInfo) {
                return Util.goodMapValue(personInfo, "id");
            }
            , getObjectTypeFromInfo: function (objectInfo) {
                return ObjectService.ObjectTypes.PERSON;
            }
        });
    }
]);