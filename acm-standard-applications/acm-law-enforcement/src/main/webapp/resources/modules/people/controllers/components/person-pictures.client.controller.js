'use strict';

angular.module('people').controller(
        'Person.PicturesController',
        [ '$scope', '$stateParams', '$translate', '$modal', '$timeout', 'UtilService', 'ConfigService', 'Person.InfoService', 'MessageService', 'Helper.ObjectBrowserService', 'Helper.UiGridService', 'Authentication', 'Person.PicturesService', 'EcmService', 'ObjectService', 'PermissionsService', 'Mentions.Service',
                function($scope, $stateParams, $translate, $modal, $timeout, Util, ConfigService, PersonInfoService, MessageService, HelperObjectBrowserService, HelperUiGridService, Authentication, PersonPicturesService, EcmService, ObjectService, PermissionsService, MentionsService) {

                    new HelperObjectBrowserService.Component({
                        scope: $scope,
                        stateParams: $stateParams,
                        moduleId: "people",
                        componentId: "pictures",
                        retrieveObjectInfo: PersonInfoService.getPersonInfo,
                        onConfigRetrieved: function(componentConfig) {
                            return onConfigRetrieved(componentConfig);
                        },
                        onObjectInfoRetrieved: function(objectInfo) {
                            onObjectInfoRetrieved(objectInfo);
                        }
                    });

                    $scope.gridOptions = $scope.gridOptions || {};

                    var currentUser = '';

                    var gridHelper = new HelperUiGridService.Grid({
                        scope: $scope
                    });

                    Authentication.queryUserInfo().then(function(data) {
                        currentUser = data.userId;
                    });

                    var onConfigRetrieved = function(config) {
                        $scope.config = config;
                        PermissionsService.getActionPermission('editPerson', $scope.objectInfo, {
                            objectType: ObjectService.ObjectTypes.PERSON
                        }).then(function(result) {
                            if (result) {
                                gridHelper.addButton(config, "edit");
                                gridHelper.addButton(config, "delete", null, null, "isDefault");
                            }
                        });
                        gridHelper.setColumnDefs(config);
                        gridHelper.setBasicOptions(config);
                        gridHelper.disableGridScrolling(config);
                    };

                    var onObjectInfoRetrieved = function(objectInfo) {
                        $scope.objectInfo = objectInfo;
                        $scope.reloadGrid();
                    };

                    $scope.addNew = function() {
                        showModal(null, false);
                    };

                    $scope.editRow = function(rowEntity) {

                        EcmService.getFile({
                            fileId: rowEntity.object_id_s
                        }).$promise.then(function(data) {
                            $scope.image = data;
                            showModal(data, true);
                        });
                    };

                    $scope.deleteRow = function(rowEntity) {

                        var imageId = Util.goodMapValue(rowEntity, "object_id_s", 0);
                        if (imageId) {
                            PersonPicturesService.deletePersonPictures($scope.objectInfo.id, imageId).then(function(objectInfo) {
                                $scope.$emit("report-object-updated", objectInfo);
                                MessageService.succsessAction();
                                return objectInfo;
                            }, function(error) {
                                $scope.$emit("report-object-update-failed", error);
                                MessageService.errorAction();
                                return error;
                            });
                        }
                        gridHelper.deleteRow(rowEntity);
                    };

                    function showModal(image, isEdit) {
                        var params = {};
                        if (image != null) {
                            params.userPicture = image.fileName + image.fileActiveVersionNameExtension;
                        }
                        params.image = image || {};
                        params.isEdit = isEdit || false;
                        params.isDefault = $scope.isDefault(image);

                        // --------------  mention --------------
                        $scope.params = {
                            emailAddresses: [],
                            usersMentioned: []
                        };

                        var modalInstance = $modal.open({
                            animation: true,
                            templateUrl: "modules/people/views/components/person-pictures-upload.dialog.view.html",
                            controller: 'Person.PictureUploadDialogController',
                            size: 'md',
                            scope: $scope,
                            backdrop: 'static',
                            resolve: {
                                params: function() {
                                    return params;
                                }
                            }
                        });

                        modalInstance.result.then(function(data) {

                            if (data.isEdit) {
                                data.image.modified = null;
                                PersonPicturesService.savePersonPicture($scope.objectInfo.id, data.file, data.isDefault, data.image).then(function(picture) {
                                    if (data.isDefault) {
                                        $scope.objectInfo.defaultPicture = data.image;
                                    }
                                    MentionsService.sendEmailToMentionedUsers($scope.params.emailAddresses, $scope.params.usersMentioned,
                                        ObjectService.ObjectTypes.PERSON, "PICTURE", $scope.objectInfo.id, data.image.description);
                                    MessageService.succsessAction();
                                    $scope.$emit("report-object-updated", $scope.objectInfo);
                                }, function() {
                                    MessageService.errorAction();
                                });
                            } else if (data.file) {
                                var name = data.file.name.substr(0, (data.file.name.lastIndexOf('.'))); //get file name example.png -> example
                                var ext = data.file.name.substr(data.file.name.lastIndexOf('.')); //get file extension example.png -> .png
                                if (!Util.isEmpty($scope.images)) {
                                    var found = _.find($scope.images, function(image) {
                                        return image.title_parseable == name && image.ext_s == ext;
                                    });
                                }
                                if (found) {
                                    MessageService.error($translate.instant("people.comp.pictures.message.error.uploadSamePicture"));
                                } else {
                                    PersonPicturesService.insertPersonPicture($scope.objectInfo.id, data.file, data.isDefault, data.image.description).then(function(returnResponse) {
                                        var uploadedPictureId = returnResponse.data.fileId;
                                        MentionsService.sendEmailToMentionedUsers($scope.params.emailAddresses, $scope.params.usersMentioned,
                                            ObjectService.ObjectTypes.PERSON, "PICTURE", $scope.objectInfo.id, data.image.description);
                                        MessageService.succsessAction();
                                        EcmService.getFile({
                                            fileId: uploadedPictureId
                                        }).$promise.then(function(uploadedPic) {
                                            $scope.image = uploadedPic;

                                            if (data.isDefault) {
                                                $scope.objectInfo.defaultPicture = $scope.image;
                                            }
                                            $timeout(function() {
                                                $scope.$emit("report-object-updated", $scope.objectInfo);
                                            }, 2000);
                                        });

                                    }, function() {
                                        MessageService.errorAction();
                                    });
                                }
                            }
                        });
                    }

                    $scope.isDefault = function(data) {
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

                    $scope.reloadGrid = function() {
                        if ($scope.objectInfo.id) {
                            $scope.gridOptions.data = [];
                            PersonPicturesService.listPersonPictures($scope.objectInfo.id).then(function(result) {
                                $scope.images = result.response.docs;
                                $scope.gridOptions.data = $scope.images;
                            });
                        }
                    };

                    $scope.refresh = function() {
                        $scope.$emit('report-object-refreshed', $stateParams.id);
                    };
                } ]);