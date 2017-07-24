'use strict';

angular.module('cases').controller('Cases.PeopleController', ['$scope', '$q', '$stateParams', '$translate', '$modal'
    , 'UtilService', 'ObjectService', 'Case.InfoService', 'Authentication', 'Object.LookupService'
    , 'Helper.UiGridService', 'Helper.ObjectBrowserService', 'Person.InfoService'
    , function ($scope, $q, $stateParams, $translate, $modal
        , Util, ObjectService, CaseInfoService, Authentication, ObjectLookupService
        , HelperUiGridService, HelperObjectBrowserService, PersonInfoService) {


        Authentication.queryUserInfo().then(
            function (userInfo) {
                $scope.userId = userInfo.userId;
                return userInfo;
            }
        );

        ObjectLookupService.getPersonTypes().then(
            function (personTypes) {
                var options = [];
                _.forEach(personTypes, function (v, k) {
                    options.push({type: v, name: v});
                });
                $scope.personTypes = options;
                return personTypes;
            });

        new HelperObjectBrowserService.Component({
            scope: $scope
            , stateParams: $stateParams
            , moduleId: "cases"
            , componentId: "people"
            , retrieveObjectInfo: CaseInfoService.getCaseInfo
            , validateObjectInfo: CaseInfoService.validateCaseInfo
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
            gridHelper.addButton(config, "edit", null, null, "isEditable");
            gridHelper.addButton(config, "delete", null, null, "isDeletable");
            gridHelper.setColumnDefs(config);
            gridHelper.setBasicOptions(config);
            gridHelper.disableGridScrolling(config);
            gridHelper.setUserNameFilterToConfig(promiseUsers, config);
        };

        var onObjectInfoRetrieved = function (objectInfo) {
            $scope.objectInfo = objectInfo;
            $scope.gridOptions.data = $scope.objectInfo.personAssociations;
        };

        var newPersonAssociation = function () {
            return {
                id: null
                , personType: ""
                , parentId: $scope.objectInfo.id
                , parentType: $scope.objectInfo.caseType
                , parentTitle: $scope.objectInfo.caseNumber
                , personDescription: ""
                , notes: ""
                , person: null
                , className: "com.armedia.acm.plugins.person.model.PersonAssociation"
            };
        };

        $scope.addPerson = function () {
            pickPerson(null);
        };

        function pickPerson(association) {

            var params = {};
            params.types = $scope.personTypes;

            if (association) {
                angular.extend(params, {
                    personId: association.person.id,
                    personName: association.person.givenName + ' ' + association.person.familyName,
                    type: association.personType,
                    description: association.personDescription
                });
            } else {
                association = new newPersonAssociation();
            }

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
                    updatePersonAssociationData(association, data.person, data);
                } else {
                    PersonInfoService.getPersonInfo(data.personId).then(function (person) {
                        updatePersonAssociationData(association, person, data);
                    })
                }
            });
        }

        function updatePersonAssociationData(association, person, data) {
            association.person = person;
            association.personType = data.type;
            association.personDescription = data.description;
            if (!association.id) {
                $scope.objectInfo.personAssociations.push(association);
            }
            saveObjectInfoAndRefresh();
        }

        $scope.deleteRow = function (rowEntity) {
            var id = Util.goodMapValue(rowEntity, "id", 0);
            _.remove($scope.objectInfo.personAssociations, function (item) {
                return item === rowEntity;
            });
            if (rowEntity.id) {
                saveObjectInfoAndRefresh();
            }
        };

        $scope.editRow = function (rowEntity) {
            pickPerson(rowEntity);
        };

        function saveObjectInfoAndRefresh() {
            var promiseSaveInfo = Util.errorPromise($translate.instant("common.service.error.invalidData"));
            if (CaseInfoService.validateCaseInfo($scope.objectInfo)) {
                var objectInfo = Util.omitNg($scope.objectInfo);
                promiseSaveInfo = CaseInfoService.saveCaseInfo(objectInfo);
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

        $scope.isEditable = function (rowEntity) {
            return rowEntity.personType == 'Initiator';
        };

        $scope.isDeletable = function (rowEntity) {
            return rowEntity.personType == 'Initiator';
        };
    }
]);