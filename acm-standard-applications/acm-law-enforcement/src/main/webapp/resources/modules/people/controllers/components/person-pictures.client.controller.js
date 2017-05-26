'use strict';

angular.module('people').controller('Person.PicturesController', ['$scope', '$stateParams', '$translate', '$modal', '$timeout'
    , 'UtilService', 'ConfigService', 'Person.InfoService', 'MessageService', 'Helper.ObjectBrowserService'
    , 'Helper.UiGridService', 'Authentication', 'Person.PicturesService', 'EcmService'
    , function ($scope, $stateParams, $translate, $modal, $timeout
        , Util, ConfigService, PersonInfoService, MessageService, HelperObjectBrowserService, HelperUiGridService
        , Authentication, PersonPicturesService, EcmService) {

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


        $scope.gridOptions = $scope.gridOptions || {};
        $scope.personInfo = null;

        var currentUser = '';

        var gridHelper = new HelperUiGridService.Grid({scope: $scope});

        Authentication.queryUserInfo().then(function (data) {
            currentUser = data.userId;
        });

        var onConfigRetrieved = function (config) {
            $scope.config = config;
            gridHelper.addButton(config, "edit");
            gridHelper.addButton(config, "delete", null, null, "isDefault");
            gridHelper.setColumnDefs(config);
            gridHelper.setBasicOptions(config);
            gridHelper.disableGridScrolling(config);
        };

        var onObjectInfoRetrieved = function (objectInfo) {
            $scope.personInfo = objectInfo;
            var eventName = "object.changed/" + objectInfo.objectType + "/" + objectInfo.id;
            //we want to subscribe to changes because the data is from solr and on edit it
            //will take time to get it so we need to wait for message
            $scope.$bus.subscribe(eventName, function (data) {
                $scope.refresh();
            });
            $scope.reloadGrid();
        };

        $scope.addNew = function () {
            showModal(null, false);
        };

        $scope.editRow = function (rowEntity) {

            EcmService.getFile({fileId: rowEntity.object_id_s}).$promise.then(function (data) {
                $scope.image = data;
                showModal(data, true);
            });
        };

        $scope.deleteRow = function (rowEntity) {

            var imageId = Util.goodMapValue(rowEntity, "object_id_s", 0);
            if (imageId) {
                PersonPicturesService.deletePersonPictures($scope.personInfo.id, imageId).then(function () {
                    $scope.reloadGrid();
                    MessageService.succsessAction();
                }, function () {
                    MessageService.errorAction();
                    $scope.reloadGrid();
                });
            }
            gridHelper.deleteRow(rowEntity);
        };

        function showModal(image, isEdit) {
            var params = {};
            params.image = image || {};
            params.isEdit = isEdit || false;
            params.isDefault = $scope.isDefault(image);

            var modalInstance = $modal.open({
                animation: true,
                templateUrl: "modules/people/views/components/person-pictures-upload.dialog.view.html",
                controller: 'Person.PictureUploadDialogController',
                size: 'md',
                backdrop: 'static',
                resolve: {
                    params: function () {
                        return params;
                    }
                }
            });

            modalInstance.result.then(function (data) {

                if (data.isEdit) {
                    PersonPicturesService.savePersonPicture($scope.personInfo.id, data.file, data.isDefault, data.image).then(function () {
                        MessageService.succsessAction();
                        $scope.refresh();
                    }, function () {
                        MessageService.errorAction();
                    });
                } else if (data.file) {
                    PersonPicturesService.insertPersonPicture($scope.personInfo.id, data.file, data.isDefault, data.image.description).then(function () {
                        MessageService.succsessAction();
                        $scope.refresh();
                    }, function () {
                        MessageService.errorAction();
                    });
                }
            });
        }

        $scope.isDefault = function (data) {
            if (data && data.object_id_s) {
                var id = 0;
                if ($scope.objectInfo.defaultPicture) {
                    id = $scope.objectInfo.defaultPicture.fileId
                }
                return data.object_id_s == id;
            }
            if (data && data.fileId) {
                var id = 0;
                if ($scope.objectInfo.defaultPicture) {
                    id = $scope.objectInfo.defaultPicture.fileId
                }
                return data.fileId == id;
            }
            return false;
        };

        $scope.reloadGrid = function () {

            if ($scope.personInfo.id) {
                PersonPicturesService.listPersonPictures($scope.personInfo.id).then(function (result) {

                    $scope.images = result.response.docs;
                    $scope.gridOptions.data = $scope.images;
                });
            }
        };

        $scope.refresh = function () {
            $scope.$emit('report-object-refreshed', $stateParams.id);
        };
    }
]);