'use strict';

angular.module('people').controller('Person.AliasesController', ['$scope', '$stateParams', '$translate'
    , 'UtilService', 'ConfigService', 'Person.InfoService', 'MessageService', 'Helper.ObjectBrowserService', 'Helper.UiGridService', 'Authentication', 'Person.PicturesService', '$modal'
    , function ($scope, $stateParams, $translate
        , Util, ConfigService, PersonInfoService, MessageService, HelperObjectBrowserService, HelperUiGridService, Authentication, PersonPicturesService, $modal) {

        new HelperObjectBrowserService.Component({
            scope: $scope
            , stateParams: $stateParams
            , moduleId: "people"
            , componentId: "aliases"
            , retrieveObjectInfo: PersonInfoService.getPersonInfo
            , onConfigRetrieved: function (componentConfig) {
                return onConfigRetrieved(componentConfig);
            }
            , onObjectInfoRetrieved: function (objectInfo) {
                onObjectInfoRetrieved(objectInfo);
            }
        });

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

            if (objectInfo.personAliases) {
                $scope.gridOptions.data = objectInfo.personAliases;
                $scope.gridOptions.noData = false;
            } else {
                $scope.gridOptions.data = [];
                $scope.gridOptions.noData = true;
                $scope.noDataMessage = $translate.instant('people.comp.aliases.noData');
            }
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

        $scope.newAlias = function () {
            var modalInstance = $modal.open({
                templateUrl: "modules/people/views/components/person-aliases-new-alias.dialog.view.html",
                controller: 'Person.AliasesNewAliasDialogController'
            });
            modalInstance.result.then(function (result) {
                if (result) {
                    console.log(result);
                    $scope.objectInfo.personAliases.push(result.alias);
                    if (result.isDefault) {
                        $scope.objectInfo.defaultAlias = result.alias;
                    }
                }
            });
        };
    }
]);