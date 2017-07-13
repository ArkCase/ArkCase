'use strict';

angular.module('people').controller('People.RelatedController', ['$scope', '$q', '$stateParams', '$translate', '$modal'
    , 'UtilService', 'ObjectService', 'Person.InfoService', 'Authentication'
    , 'Helper.UiGridService', 'Helper.ObjectBrowserService', 'Object.LookupService', 'ObjectAssociation.Service', '$timeout'
    , function ($scope, $q, $stateParams, $translate, $modal
        , Util, ObjectService, PersonInfoService, Authentication
        , HelperUiGridService, HelperObjectBrowserService, ObjectLookupService, ObjectAssociationService, $timeout) {

        $scope.relationshipTypes = [];
        ObjectLookupService.getPersonRelationTypes().then(
            function (relationshipTypes) {
                $scope.relationshipTypes = relationshipTypes;
                return relationshipTypes;
            });

        $scope.gridOptions = {
            data: []
        };

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
            gridHelper.addButton(config, "edit");
            gridHelper.addButton(config, "delete");
            gridHelper.setColumnDefs(config);
            gridHelper.setBasicOptions(config);
            gridHelper.disableGridScrolling(config);
            gridHelper.setUserNameFilterToConfig(promiseUsers, config);
        };

        var onObjectInfoRetrieved = function (objectInfo) {
            $scope.objectInfo = objectInfo;
            refreshGridData(objectInfo.id, objectInfo.objectType);
        };

        function refreshGridData(objectId, objectType) {
            ObjectAssociationService.getObjectAssociations(objectId, objectType, 'PERSON').then(function (response) {
                $scope.gridOptions.data = response.response.docs;
            });
        }

        $scope.addPersonAssociation = function () {
            personAssociationModal({});
        };

        $scope.editRow = function (rowEntity) {
            ObjectAssociationService.getAssociationInfo(rowEntity.object_id_s).then(function (association) {
                personAssociationModal(association, rowEntity);
            });
        };

        function personAssociationModal(association, rowEntity) {
            if (!association) {
                association = {};
            }
            var params = {
                showSetPrimary: false,
                types: $scope.relationshipTypes
            };
            if (rowEntity) {
                angular.extend(params, {
                    personId: rowEntity.target_object.object_id_s,
                    personName: rowEntity.target_object.full_name_lcs,
                    type: rowEntity.association_type_s,
                    description: rowEntity.description_s
                });
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
                if (data.person) {
                    if (!data.person.id) {
                        PersonInfoService.savePersonInfoWithPictures(data.person, data.personImages).then(function (response) {
                            data['person'] = response.data;
                            updateAssociation(association, $scope.objectInfo, data.person, data, rowEntity);
                        });
                    } else {
                        updateAssociation(association, $scope.objectInfo, data.person, data, rowEntity);
                    }
                } else {
                    PersonInfoService.getPersonInfo(data.personId).then(function (person) {
                        updateAssociation(association, $scope.objectInfo, person, data, rowEntity);
                    });
                }
            });
        }

        function updateAssociation(association, parent, target, associationData, rowEntity) {
            association.parentId = parent.id;
            association.parentType = parent.objectType;

            association.targetId = target.id;
            association.targetType = target.objectType;

            association.associationType = associationData.type;

            if (associationData.inverseType) {
                if (!association.inverseAssociation) {
                    association.inverseAssociation = {};
                }
                if (association.inverseAssociation.inverseAssociation != association) {
                    association.inverseAssociation.inverseAssociation = association;
                }
                association.inverseAssociation.parentId = target.id;
                association.inverseAssociation.parentType = target.objectType;

                association.inverseAssociation.targetId = parent.id;
                association.inverseAssociation.targetType = parent.objectType;

                association.inverseAssociation.associationType = associationData.inverseType;
                association.inverseAssociation.description = associationData.description;
            }
            association.description = associationData.description;
            ObjectAssociationService.saveObjectAssociation(association).then(function (payload) {
                //success
                if (!rowEntity) {
                    //append new entity as last item in the grid
                    rowEntity = {
                        target_object: {}
                    };
                    $scope.gridOptions.data.push(rowEntity);
                }

                //update row immediately
                rowEntity.object_id_s = payload.associationId;
                rowEntity.association_type_s = payload.associationType;
                rowEntity.target_object.object_id_s = target.id;
                rowEntity.description_s = payload.description;

                rowEntity.target_object.first_name_lcs = target.givenName;
                rowEntity.target_object.last_name_lcs = target.familyName;
                rowEntity.target_object.default_organization_s = target.defaultOrganization ? target.defaultOrganization.organization.organizationValue : "";
                rowEntity.target_object.default_phone_s = formatPhone(target.defaultPhone);
                rowEntity.target_object.default_location_s = formatAddress(target.defaultAddress);
                // wait 2.5 sec and refresh because of solr indexing
                //below functionality is disabled since we are already updating rows, however if in future we need to be refreshed from solr, than just enable code bellow
                // $timeout(function () {
                //     refreshGridData($scope.objectInfo.id, $scope.objectInfo.objectType);
                // }, 2500);
            });
        }

        function formatPhone(phone) {
            if (!phone) {
                return "";
            }

            var formattedPhone = phone.value;
            if (phone.subType) {
                formattedPhone += " [" + phone.subType + "]";
            }
            return formattedPhone;
        }

        function formatAddress(address) {
            if (!address) {
                return "";
            }
            var formattedAddress = "";
            if (address.city) {
                formattedAddress += address.city;
            }
            if (address.state) {
                if (formattedAddress.length > 0) {
                    formattedAddress += ", ";
                }
                formattedAddress += address.state;
            }
            return formattedAddress;
        }

        $scope.deleteRow = function (rowEntity) {
            var id = Util.goodMapValue(rowEntity, "object_id_s", 0);
            ObjectAssociationService.deleteAssociationInfo(id).then(function (data) {
                //success
                //remove it from the grid immediately
                _.remove($scope.gridOptions.data, function (row) {
                    return row === rowEntity;
                });
                //refresh grid after 2.5 sec because of solr indexing
                //below functionality is disabled since we are already updating rows, however if in future we need to be refreshed from solr, than just enable code bellow
                // $timeout(function () {
                //     refreshGridData($scope.objectInfo.id, $scope.objectInfo.objectType);
                // }, 2500);
            });
        };
    }
]);