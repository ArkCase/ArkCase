'use strict';

angular.module('people').controller('People.PhonesController', ['$scope', '$q', '$stateParams', '$translate', '$modal'
    , 'UtilService', 'ObjectService', 'Person.InfoService', 'Authentication'
    , 'Helper.UiGridService', 'Helper.ObjectBrowserService', '$modal', 'Object.LookupService'
    , function ($scope, $q, $stateParams, $translate, $modal
        , Util, ObjectService, PersonInfoService, Authentication
        , HelperUiGridService, HelperObjectBrowserService, ObjectLookupService) {


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
            , componentId: "phones"
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
            gridHelper.addButton(config, "edit");
            gridHelper.addButton(config, "delete", null, null, "isDefault");
            gridHelper.setColumnDefs(config);
            gridHelper.setBasicOptions(config);
            gridHelper.disableGridScrolling(config);
            gridHelper.setUserNameFilterToConfig(promiseUsers, config);
        };

        var onObjectInfoRetrieved = function (objectInfo) {
            $scope.objectInfo = objectInfo;
            var phones = _.filter($scope.objectInfo.contactMethods, {type: 'phone'});
            $scope.gridOptions.data = phones;
        };

        $scope.addNew = function () {
            var phone = {};
            phone.created = Util.dateToIsoString(new Date());
            phone.creator = $scope.userId;

            //put contactMethod to scope, we will need it when we return from popup
            $scope.phone = phone;
            var item = {
                id: '',
                parentId: $scope.objectInfo.id,
                type: 'phone',
                subType: '',
                value: '',
                description: ''
            };
            showModal(item, false);
        };

        $scope.editRow = function (rowEntity) {
            $scope.phone = rowEntity;
            var item = {
                id: rowEntity.id,
                type: rowEntity.type,
                subType: rowEntity.subType,
                value: rowEntity.value,
                description: rowEntity.description
            };
            showModal(item, true);

        };

        $scope.deleteRow = function (rowEntity) {
            gridHelper.deleteRow(rowEntity);

            var id = Util.goodMapValue(rowEntity, "id", 0);
            if (0 < id) {    //do not need to call service when deleting a new row with id==0
                $scope.objectInfo.contactMethods = _.remove($scope.objectInfo.contactMethods, function (item) {
                    return item.id != id;
                });
                saveObjectInfoAndRefresh()
            }
        };

        function showModal(phone, isEdit) {
            var params = {};
            params.phone = phone || {};
            params.isEdit = isEdit || false;
            params.isDefault = $scope.isDefault(phone);

            var modalInstance = $modal.open({
                animation: true,
                templateUrl: 'modules/people/views/components/person-phones-modal.client.view.html',
                controller: 'People.PhonesModalController',
                size: 'md',
                backdrop: 'static',
                resolve: {
                    params: function () {
                        return params;
                    }
                }
            });
            modalInstance.result.then(function (data) {
                var phone;
                if (!data.isEdit)
                    phone = $scope.phone;
                else {
                    phone = _.find($scope.objectInfo.contactMethods, {id: data.phone.id});
                }
                phone.type = 'phone';
                phone.subType = data.phone.subType;
                phone.value = data.phone.value;
                phone.description = data.phone.description;

                if (!data.isEdit) {
                    $scope.objectInfo.contactMethods.push(phone);
                }

                var phones = _.filter($scope.objectInfo.contactMethods, {type: 'phone'});
                if (data.isDefault || phones.length == 1) {
                    $scope.objectInfo.defaultPhone = phone;
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
            if ($scope.objectInfo.defaultPhone) {
                id = $scope.objectInfo.defaultPhone.id
            }
            return data.id == id;
        };
    }
]);