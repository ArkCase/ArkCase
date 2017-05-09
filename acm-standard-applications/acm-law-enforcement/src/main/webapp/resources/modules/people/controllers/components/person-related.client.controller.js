'use strict';

angular.module('people').controller('People.RelatedController', ['$scope', '$q', '$stateParams', '$translate', '$modal'
    , 'UtilService', 'ObjectService', 'Person.InfoService', 'Authentication'
    , 'Helper.UiGridService', 'Helper.ObjectBrowserService', 'Object.LookupService'
    , function ($scope, $q, $stateParams, $translate, $modal
        , Util, ObjectService, PersonInfoService, Authentication
        , HelperUiGridService, HelperObjectBrowserService, ObjectLookupService) {


        ObjectLookupService.getPersonRelationTypes().then(
            function (relationshipTypes) {
                $scope.relationshipTypes = relationshipTypes;
                return relationshipTypes;
            });

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
            , componentId: "related"
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
            gridHelper.addButton(config, "delete");
            gridHelper.setColumnDefs(config);
            gridHelper.setBasicOptions(config);
            gridHelper.disableGridScrolling(config);
            gridHelper.setUserNameFilterToConfig(promiseUsers, config);
        };

        var onObjectInfoRetrieved = function (objectInfo) {
            $scope.objectInfo = objectInfo;
            $scope.gridOptions.data = $scope.objectInfo.associationsToObjects;
        };

        var newPersonAssociation = function () {
            return {
                id: null
                , personType: ""
                , parentId: $scope.objectInfo.id
                , parentType: $scope.objectInfo.objectType
                , parentTitle: ""
                , personDescription: ""
                , notes: ""
                , person: null
                , className: "com.armedia.acm.plugins.person.model.PersonAssociation"
            };
        };

        $scope.addPerson = function () {

            var params = {};
            params.types = $scope.relationshipTypes;
            params.showDescription = true;

            var modalInstance = $modal.open({
                scope: $scope,
                animation: true,
                templateUrl: 'modules/common/views/add-person-modal.client.view.html',
                controller: 'Common.AddPersonModalController',
                size: 'md',
                backdrop: 'static',
                resolve: {
                    params: function () {
                        return params;
                    }
                }
            });

            modalInstance.result.then(function (data) {
                if (data.isNew) {
                    var association = new newPersonAssociation();
                    association.person = data.person;
                    association.personType = data.type;
                    association.personDescription = data.description;
                    $scope.objectInfo.associationsToObjects.push(association);
                    saveObjectInfoAndRefresh();
                } else {
                    PersonInfoService.getPersonInfo(data.personId).then(function (person) {
                        var association = new newPersonAssociation();
                        association.person = person;
                        association.personType = data.type;
                        association.personDescription = data.description;
                        $scope.objectInfo.associationsToObjects.push(association);
                        saveObjectInfoAndRefresh();
                    })
                }
            });
        };

        $scope.deleteRow = function (rowEntity) {
            var id = Util.goodMapValue(rowEntity, "id", 0);
            if (0 < id) {    //do not need to call service when deleting a new row with id==0
                $scope.objectInfo.associationsToObjects = _.remove($scope.objectInfo.associationsToObjects, function (item) {
                    return item.id != id;
                });
                saveObjectInfoAndRefresh()
            }
        };

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