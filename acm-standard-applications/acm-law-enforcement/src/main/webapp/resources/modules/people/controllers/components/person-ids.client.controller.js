'use strict';

angular.module('people').controller('People.IDsController', ['$scope', '$q', '$stateParams', '$translate', '$modal'
    , 'UtilService', 'ObjectService', 'Person.InfoService', 'Authentication'
    , 'Helper.UiGridService', 'Helper.ObjectBrowserService', 'PermissionsService'
    , function ($scope, $q, $stateParams, $translate, $modal
        , Util, ObjectService, PersonInfoService, Authentication
        , HelperUiGridService, HelperObjectBrowserService, PermissionsService) {


        Authentication.queryUserInfo().then(
            function (userInfo) {
                $scope.userId = userInfo.userId;
                return userInfo;
            }
        );

        var componentHelper = new HelperObjectBrowserService.Component({
            scope: $scope
            , stateParams: $stateParams
            , moduleId: "people"
            , componentId: "ids"
            , retrieveObjectInfo: PersonInfoService.getPersonInfo
            , validateObjectInfo: PersonInfoService.validatePersonInfo
            , onConfigRetrieved: function (componentConfig) {
                return onConfigRetrieved(componentConfig);
            }
            , onObjectInfoRetrieved: function (objectInfo) {
                onObjectInfoRetrieved(objectInfo);
            }
        });

        var gridHelper = new HelperUiGridService.Grid({scope: $scope});

        var promiseUsers = gridHelper.getUsers();

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
            gridHelper.setUserNameFilterToConfig(promiseUsers, config);
        };

        var onObjectInfoRetrieved = function (objectInfo) {
            $scope.objectInfo = objectInfo;
            $scope.gridOptions.data = $scope.objectInfo.identifications;
        };

        $scope.addNew = function () {
            var identification = {};
            identification.created = Util.dateToIsoString(new Date());
            identification.creator = $scope.userId;

            $scope.identification = identification;
            var item = {
                identificationID: '',
                identificationType: '',
                identificationNumber: '',
                identificationIssuer: '',
                identificationYearIssued: ''
            };
            showModal(item, false);
        };

        $scope.editRow = function (rowEntity) {
            $scope.identification = rowEntity;
            var item = {
                identificationID: rowEntity.identificationID,
                identificationType: rowEntity.identificationType,
                identificationNumber: rowEntity.identificationNumber,
                identificationIssuer: rowEntity.identificationIssuer,
                identificationYearIssued: new Date(rowEntity.identificationYearIssued)
            };
            showModal(item, true);
        };

        $scope.deleteRow = function (rowEntity) {
            var id = Util.goodMapValue(rowEntity, "identificationID", 0);
            if (0 < id) {    //do not need to call service when deleting a new row with id==0
                $scope.objectInfo.identifications = _.remove($scope.objectInfo.identifications, function (item) {
                    return item.identificationID != id;
                });
                saveObjectInfoAndRefresh()
            }
        };

        function showModal(identification, isEdit) {
            var params = {};
            params.identification = identification || {};
            params.isEdit = isEdit || false;
            params.isDefault = $scope.isDefault(identification);

            var modalInstance = $modal.open({
                animation: true,
                templateUrl: 'modules/people/views/components/person-ids-modal.client.view.html',
                controller: 'People.IDsModalController',
                size: 'md',
                backdrop: 'static',
                resolve: {
                    params: function () {
                        return params;
                    }
                }
            });
            modalInstance.result.then(function (data) {
                var identification;
                if (!data.isEdit)
                    identification = $scope.identification;
                else {
                    identification = _.find($scope.objectInfo.identifications, {identificationID: data.identification.identificationID});
                }

                identification.identificationType = data.identification.identificationType;
                identification.identificationNumber = data.identification.identificationNumber;
                identification.identificationIssuer = data.identification.identificationIssuer;
                identification.identificationYearIssued = data.identification.identificationYearIssued;

                if (!data.isEdit) {
                    $scope.objectInfo.identifications.push(identification);
                }

                if (data.isDefault || $scope.objectInfo.identifications.length == 1) {
                    $scope.objectInfo.defaultIdentification = identification;
                }
                saveObjectInfoAndRefresh();
            });
        }

        function saveObjectInfoAndRefresh() {
            var promiseSaveInfo = Util.errorPromise($translate.instant("common.service.error.invalidData"));
            if (PersonInfoService.validatePersonInfo($scope.objectInfo)) {
                var objectInfo = Util.omitNg($scope.objectInfo);
                promiseSaveInfo = PersonInfoService.savePersonInfo(objectInfo);
                promiseSaveInfo.then(
                    function (objectInfo) {
                        $scope.$emit("report-object-updated", objectInfo);
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

        $scope.isDefault = function (data) {
            var id = 0;
            if ($scope.objectInfo.defaultIdentification) {
                id = $scope.objectInfo.defaultIdentification.identificationID;
            }
            if ($scope.objectInfo.identifications && $scope.objectInfo.identifications.length == 0) {
                return true;
            }
            return data.identificationID == id;
        };
    }
]);