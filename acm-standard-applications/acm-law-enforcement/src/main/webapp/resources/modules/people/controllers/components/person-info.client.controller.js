'use strict';

angular.module('people').controller('People.InfoController', ['$scope', '$stateParams', '$translate', '$timeout'
    , 'UtilService', 'Util.DateService', 'ConfigService', 'Object.LookupService', 'Person.LookupService', 'Person.InfoService'
    , 'Object.ModelService', 'Helper.ObjectBrowserService', 'MessageService', 'ObjectService', 'Helper.UiGridService', '$modal'
    , 'Object.ParticipantService', '$q', '$filter', 'SearchService', 'Search.QueryBuilderService'
    , function ($scope, $stateParams, $translate, $timeout
        , Util, UtilDateService, ConfigService, ObjectLookupService, PersonLookupService, PersonInfoService
        , ObjectModelService, HelperObjectBrowserService, MessageService, ObjectService, HelperUiGridService, $modal, ObjectParticipantService, $q, $filter
        , SearchService, SearchQueryBuilder) {

        new HelperObjectBrowserService.Component({
            scope: $scope
            , stateParams: $stateParams
            , moduleId: "people"
            , componentId: "info"
            , retrieveObjectInfo: PersonInfoService.getPersonInfo
            , validateObjectInfo: PersonInfoService.validatePersonInfo
            , onObjectInfoRetrieved: function (objectInfo) {
                onObjectInfoRetrieved(objectInfo);
            }
        });

        var gridHelper = new HelperUiGridService.Grid({scope: $scope});
        var promiseUsers = gridHelper.getUsers();
        var promiseConfig = ConfigService.getModuleConfig("people");

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