'use strict';

angular.module('people').controller('People.EmailsController', ['$scope', '$q', '$stateParams', '$translate', '$modal'
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
            , componentId: "emails"
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
            gridHelper.addButton(config, "delete");
            gridHelper.setColumnDefs(config);
            gridHelper.setBasicOptions(config);
            gridHelper.disableGridScrolling(config);
            gridHelper.setUserNameFilterToConfig(promiseUsers, config);
        };

        var onObjectInfoRetrieved = function (objectInfo) {
            $scope.objectInfo = objectInfo;
            var emails = _.filter($scope.objectInfo.contactMethods, {type: 'email'});
            $scope.gridOptions.data = emails;
        };

        $scope.addNew = function () {
            var email = {};
            email.created = Util.dateToIsoString(new Date());
            email.creator = $scope.userId;

            //put contactMethod to scope, we will need it when we return from popup
            $scope.email = email;
            var item = {
                id: '',
                parentId: $scope.objectInfo.id,
                type: 'email',
                subType: '',
                value: '',
                description: ''
            };
            showModal(item, false);
        };

        $scope.editRow = function (rowEntity) {
            $scope.email = rowEntity;
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


        $scope.setPrimary = function () {
            console.log('set primary');
        };


        function showModal(email, isEdit) {
            var modalScope = $scope.$new();
            modalScope.email = email || {};
            modalScope.isEdit = isEdit || false;

            var modalInstance = $modal.open({
                scope: modalScope,
                animation: true,
                templateUrl: 'modules/people/views/components/person-emails-modal.client.view.html',
                controller: 'People.EmailsModalController',
                size: 'sm'
            });
            modalInstance.result.then(function (data) {
                var email;
                if (!data.isEdit)
                    email = $scope.email;
                else {
                    email = _.find($scope.objectInfo.contactMethods, {id: data.email.id});
                }
                email.type = 'email';
                email.subType = data.email.subType;
                email.value = data.email.value;
                email.description = data.email.description;

                if (!data.isEdit) {
                    if (data.isDefault) {
                        $scope.objectInfo.defaultEmail = email;
                    }
                    else {
                        //if emails is empty then we will not add it to the array
                        //but we will set it as default
                        var emails = _.filter($scope.objectInfo.contactMethods, {type: 'email'});
                        if (emails.length > 0) {
                            $scope.objectInfo.contactMethods.push(email);
                        } else {
                            $scope.objectInfo.defaultEmail = email;
                        }
                    }
                }
                else if (data.isDefault) {
                    $scope.objectInfo.defaultEmail = email;
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
    }
]);