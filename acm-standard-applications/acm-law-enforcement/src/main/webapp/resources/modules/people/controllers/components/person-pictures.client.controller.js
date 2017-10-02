'use strict';

angular.module('people').controller('Person.PicturesController', ['$scope', '$stateParams', '$translate', '$modal', '$timeout'
    , 'UtilService', 'ConfigService', 'Person.InfoService', 'MessageService', 'Helper.ObjectBrowserService'
    , 'Helper.UiGridService', 'Authentication', 'Person.PicturesService', 'EcmService', 'ObjectService', 'PermissionsService'
    , function ($scope, $stateParams, $translate, $modal, $timeout
        , Util, ConfigService, PersonInfoService, MessageService, HelperObjectBrowserService, HelperUiGridService
        , Authentication, PersonPicturesService, EcmService, ObjectService, PermissionsService) {

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

        var currentUser = '';

        var gridHelper = new HelperUiGridService.Grid({scope: $scope});

        Authentication.queryUserInfo().then(function (data) {
            currentUser = data.userId;
        });

        var onConfigRetrieved = function (config) {
            $scope.config = config;
            PermissionsService.getActionPermission('editPerson', $scope.objectInfo, {objectType: ObjectService.ObjectTypes.PERSON}).then(function (result) {
                if (result) {
                    gridHelper.addButton(config, "edit");
                    gridHelper.addButton(config, "delete", null, null, "isDefault");
                }
            });
            gridHelper.setColumnDefs(config);
            gridHelper.setBasicOptions(config);
            gridHelper.disableGridScrolling(config);
        };

        var onObjectInfoRetrieved = function (objectInfo) {
            $scope.objectInfo = objectInfo;
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
                PersonPicturesService.deletePersonPictures($scope.objectInfo.id, imageId).then(function () {
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
            if(image != null){
                params.userPicture = image.fileName + image.fileActiveVersionNameExtension;
            }
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
                    data.image.modified = null;
                    PersonPicturesService.savePersonPicture($scope.objectInfo.id, data.file, data.isDefault, data.image).then(function () {
                        MessageService.succsessAction();
                        $scope.refresh();
                    }, function () {
                        MessageService.errorAction();
                    });
                } else if (data.file) {
                    PersonPicturesService.insertPersonPicture($scope.objectInfo.id, data.file, data.isDefault, data.image.description).then(function () {
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
                    id = $scope.objectInfo.defaultPicture.fileId;
                }
                return data.object_id_s == id;
            }
            if (data && data.fileId) {
                var id = 0;
                if ($scope.objectInfo.defaultPicture) {
                    id = $scope.objectInfo.defaultPicture.fileId;
                }
                return data.fileId == id;
            }
            if ($scope.images && $scope.images.length == 0) {
                return true;
            }
            return false;
        };

        $scope.reloadGrid = function () {

            if ($scope.objectInfo.id) {
                $scope.gridOptions.data = [];
                PersonPicturesService.listPersonPictures($scope.objectInfo.id).then(function (result) {
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