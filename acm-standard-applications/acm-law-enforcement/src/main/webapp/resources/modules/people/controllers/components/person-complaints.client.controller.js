'use strict';

angular.module('people').controller('People.ComplaintsController', ['$scope', '$q', '$stateParams', '$translate', '$modal'
    , 'UtilService', 'ObjectService', 'Person.InfoService', 'Authentication'
    , 'Helper.UiGridService', 'Helper.ObjectBrowserService', 'PersonAssociation.Service', 'Object.LookupService'
    , function ($scope, $q, $stateParams, $translate, $modal
        , Util, ObjectService, PersonInfoService, Authentication
        , HelperUiGridService, HelperObjectBrowserService, PersonAssociationService, ObjectLookupService) {

        const typeInitiator = 'Initiator';

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

        var componentHelper = new HelperObjectBrowserService.Component({
            scope: $scope
            , stateParams: $stateParams
            , moduleId: "people"
            , componentId: "complaints"
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
            gridHelper.addButton(config, "edit", null, null, "isEditDisabled");
            gridHelper.addButton(config, "delete", null, null, "isDeleteDisabled");
            gridHelper.setColumnDefs(config);
            gridHelper.setBasicOptions(config);
            gridHelper.disableGridScrolling(config);
            gridHelper.setUserNameFilterToConfig(promiseUsers, config);
        };

        var onObjectInfoRetrieved = function (objectInfo) {
            $scope.objectInfo = objectInfo;
            var currentObjectId = Util.goodMapValue($scope.objectInfo, "id");
            if (Util.goodPositive(currentObjectId, false)) {
                PersonAssociationService.getPersonAssociations(currentObjectId, ObjectService.ObjectTypes.COMPLAINT).then(function (data) {
                    $scope.gridOptions.data = data.response.docs;
                    $scope.gridOptions.totalItems = data.response.numFound;
                    return data;
                });
            }
        };

        $scope.addComplaintAssociation = function () {
            pickComplaint();
        };

        $scope.editRow = function (rowEntity) {
            PersonAssociationService.getPersonAssociation(rowEntity.object_id_s).then(function (association) {
                pickComplaint(association, rowEntity);
            });
        };

        $scope.deleteRow = function (rowEntity) {
            var id = Util.goodMapValue(rowEntity, "object_id_s", 0);
            PersonAssociationService.deletePersonAssociationInfo(id).then(function (data) {
                //success
                //remove it from the grid immediately
                _.remove($scope.gridOptions.data, function (row) {
                    return row === rowEntity;
                });
            });
        };

        function pickComplaint(association, rowEntity) {

            var params = {};
            params.types = $scope.personTypes;
            params.showDescription = true;
            params.customFilter = '"Object Type": ' + ObjectService.ObjectTypes.COMPLAINT;
            params.objectTypeLabel = $translate.instant("people.comp.complaints.objectType.label");

            if (rowEntity) {
                angular.extend(params, {
                    objectId: rowEntity.parent_object.object_id_s,
                    objectName: rowEntity.parent_object.name,
                    type: rowEntity.type_lcs,
                    description: association.personDescription
                });
            } else {
                association = new newPersonAssociation();
            }

            var modalInstance = $modal.open({
                scope: $scope,
                animation: true,
                templateUrl: 'modules/common/views/add-object-association-modal.client.view.html',
                controller: 'Common.AddObjectAssociationModalController',
                size: 'md',
                backdrop: 'static',
                resolve: {
                    params: function () {
                        return params;
                    }
                }
            });

            modalInstance.result.then(function (data) {
                updatePersonAssociationData(association, data, rowEntity);
            });
        }

        function updatePersonAssociationData(association, data, rowEntity) {
            if (!rowEntity) {
                association.person = $scope.objectInfo;
                association.parentId = data.solrDocument.object_id_s;
                association.parentType = data.solrDocument.object_type_s;
            }
            association.personType = data.type;
            association.personDescription = data.description;

            PersonAssociationService.savePersonAssociation(association).then(function (response) {
                if (rowEntity) {
                    //update current row
                    rowEntity.type_lcs = response.personType;
                } else {
                    //add row to the grid
                    rowEntity = {
                        object_id_s: response.id,
                        type_lcs: response.personType,
                        parent_object: data.solrDocument
                    };
                    $scope.gridOptions.data.push(rowEntity);
                }
            });
        }

        var newPersonAssociation = function () {
            return {
                id: null
                , personType: ""
                , parentId: $scope.objectInfo.id
                , parentType: $scope.objectInfo.objectType
                , parentTitle: $scope.objectInfo.complaintNumber
                , personDescription: ""
                , notes: ""
                , person: null
                , className: "com.armedia.acm.plugins.person.model.PersonAssociation"
            };
        };

        $scope.isEditDisabled = function (rowEntity) {
            return rowEntity.type_lcs == typeInitiator;
        };

        $scope.isDeleteDisabled = function (rowEntity) {
            return rowEntity.type_lcs == typeInitiator;
        };

    }
]);