'use strict';

angular.module('people').controller('Person.PicturesController', ['$scope', '$stateParams', '$translate'
    , 'UtilService', 'ConfigService', 'Person.InfoService', 'MessageService', 'Helper.ObjectBrowserService', 'Helper.UiGridService', 'Authentication', 'Person.PicturesService', '$modal'
    , function ($scope, $stateParams, $translate
        , Util, ConfigService, PersonInfoService, MessageService, HelperObjectBrowserService, HelperUiGridService, Authentication, PersonPicturesService, $modal) {

        new HelperObjectBrowserService.Component({
            scope: $scope
            , stateParams: $stateParams
            , moduleId: "people"
            , componentId: "pictures"
            , retrieveObjectInfo: PersonInfoService.getPersonInfo
            , onConfigRetrieved: function (componentConfig) {
                return onConfigRetrieved(componentConfig);
            }
            , onObjectInfoRetrieved: function (objectInfo) {
                onObjectInfoRetrieved(objectInfo);
            }
        });

        $scope.userPicture = null;
        $scope.gridOptions = $scope.gridOptions || {};
        $scope.personInfo = null;

        var currentUser = '';

        var gridHelperPictures = new HelperUiGridService.Grid({scope: $scope});

        Authentication.queryUserInfo().then(function (data) {
            currentUser = data.userId;
        });

        var onConfigRetrieved = function (config) {
            $scope.config = config;
            gridHelperPictures.setColumnDefs(config);
            gridHelperPictures.setBasicOptions(config);
            gridHelperPictures.disableGridScrolling(config);
        };


        var onObjectInfoRetrieved = function (objectInfo) {
            $scope.personInfo = objectInfo;

            PersonPicturesService.listPersonPictures(objectInfo.id).then(function (result) {
                if (result.response.docs) {
                    $scope.gridOptions.data = result.response.docs;
                    $scope.gridOptions.noData = false;
                } else {
                    $scope.gridOptions.data = [];
                    $scope.gridOptions.noData = true;
                    $scope.noDataMessage = $translate.instant('people.comp.pictures.noData');
                }
            });
        };

        $scope.disableBtnSetPrimary = function () {
            return true;
        };

        $scope.disableBtnDelete = function () {
            return true;
        };

        $scope.delete = function () {
            return true;
        };

        $scope.setPrimary = function () {
            return true;
        };

        $scope.upload = function () {
            var modalInstance = $modal.open({
                templateUrl: "modules/people/views/components/person-pictures-upload.dialog.view.html",
                controller: 'Person.PictureUploadDialogController'
            });
            modalInstance.result.then(function (result) {
                if (result) {
                    console.log(result);
                    PersonPicturesService.savePersonPicture($scope.personInfo.id, result.file, result.isDefault);
                }
            });
        };

    }
]);