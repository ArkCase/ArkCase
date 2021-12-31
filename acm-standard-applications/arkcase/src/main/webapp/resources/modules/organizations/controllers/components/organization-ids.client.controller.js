'use strict';

angular.module('organizations').controller(
        'Organizations.IDsController',
        [ '$scope', '$q', '$stateParams', '$translate', '$modal', 'UtilService', 'ObjectService', 'Organization.InfoService', 'Authentication', 'Helper.UiGridService', 'Helper.ObjectBrowserService', 'PermissionsService', 'Object.LookupService', 'Object.ModelService',
                function($scope, $q, $stateParams, $translate, $modal, Util, ObjectService, OrganizationInfoService, Authentication, HelperUiGridService, HelperObjectBrowserService, PermissionsService, ObjectLookupService, ObjectModelService) {

                    Authentication.queryUserInfo().then(function(userInfo) {
                        $scope.userId = userInfo.userId;
                        return userInfo;
                    });

                    var componentHelper = new HelperObjectBrowserService.Component({
                        scope: $scope,
                        stateParams: $stateParams,
                        moduleId: "organizations",
                        componentId: "ids",
                        retrieveObjectInfo: OrganizationInfoService.getOrganizationInfo,
                        validateObjectInfo: OrganizationInfoService.validateOrganizationInfo,
                        onConfigRetrieved: function(componentConfig) {
                            return onConfigRetrieved(componentConfig);
                        },
                        onObjectInfoRetrieved: function(objectInfo) {
                            onObjectInfoRetrieved(objectInfo);
                        }
                    });

                    var gridHelper = new HelperUiGridService.Grid({
                        scope: $scope
                    });

                    var promiseUsers = gridHelper.getUsers();

                    var onConfigRetrieved = function(config) {
                        $scope.config = config;
                        PermissionsService.getActionPermission('editOrganization', $scope.objectInfo, {
                            objectType: ObjectService.ObjectTypes.ORGANIZATION
                        }).then(function(result) {
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

                    var onObjectInfoRetrieved = function(objectInfo) {
                        $scope.objectInfo = objectInfo;
                        $scope.gridOptions.data = $scope.objectInfo.identifications;
                    };

                    ObjectLookupService.getOrganizationIdTypes().then(function(identificationTypes) {
                        $scope.identificationTypes = identificationTypes;
                        return identificationTypes;
                    });

                    $scope.addNew = function() {
                        var identification = {
                            className: $scope.config.identificationClassName
                        };
                        identification.created = Util.dateToIsoString(new Date());
                        identification.creator = $scope.userId;

                        $scope.identification = identification;
                        var item = {
                            identificationID: '',
                            identificationType: '',
                            identificationNumber: '',
                            identificationIssuer: '',
                            identificationYearIssued: '',
                            className: $scope.config.identificationClassName
                        };
                        showModal(item, false);
                    };

                    $scope.editRow = function(rowEntity) {
                        $scope.identification = rowEntity;
                        var item = {
                            identificationID: rowEntity.identificationID,
                            identificationType: rowEntity.identificationType,
                            identificationNumber: rowEntity.identificationNumber,
                            identificationIssuer: rowEntity.identificationIssuer,
                            className: rowEntity.className
                        };

                        if (Util.isEmpty(rowEntity.identificationYearIssued)) {
                            item.identificationYearIssued = '';
                        } else {
                            item.identificationYearIssued = new Date(rowEntity.identificationYearIssued);
                        }

                        showModal(item, true);
                    };

                    $scope.deleteRow = function(rowEntity) {
                        gridHelper.deleteRow(rowEntity);

                        var id = Util.goodMapValue(rowEntity, "identificationID", 0);
                        if (0 < id) { //do not need to call service when deleting a new row with id==0
                            $scope.objectInfo.identifications = _.remove($scope.objectInfo.identifications, function(item) {
                                return item.identificationID != id;
                            });
                            saveObjectInfoAndRefresh()
                        }
                    };

                    function showModal(identification, isEdit) {
                        var params = {};
                        params.identification = identification || {className: $scope.config.identificationClassName};
                        params.isEdit = isEdit || false;
                        params.isDefault = $scope.isDefault(identification);

                        var modalInstance = $modal.open({
                            animation: true,
                            templateUrl: 'modules/organizations/views/components/organization-ids-modal.client.view.html',
                            controller: 'Organizations.IDsModalController',
                            size: 'md',
                            backdrop: 'static',
                            resolve: {
                                params: function() {
                                    return params;
                                }
                            }
                        });
                        modalInstance.result.then(function(data) {
                            var identification;
                            if (!data.isEdit)
                                identification = $scope.identification;
                            else {
                                identification = _.find($scope.objectInfo.identifications, {
                                    identificationID: data.identification.identificationID
                                });
                            }

                            var orgTypeObj = _.filter($scope.identificationTypes, function(ident) {
                                if (ident.key == data.identification.identificationType) {
                                    return ident;
                                }
                            });

                            if (data.identification.identificationType) {
                                identification.identificationType = $translate.instant(orgTypeObj[0].value);
                            }

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
                        if (OrganizationInfoService.validateOrganizationInfo($scope.objectInfo)) {
                            var objectInfo = Util.omitNg($scope.objectInfo);
                            promiseSaveInfo = OrganizationInfoService.saveOrganizationInfo(objectInfo);
                            promiseSaveInfo.then(function(objectInfo) {
                                $scope.$emit("report-object-updated", objectInfo);
                                return objectInfo;
                            }, function(error) {
                                $scope.$emit("report-object-update-failed", error);
                                return error;
                            });
                        }
                        return promiseSaveInfo;
                    }

                    $scope.isDefault = function(identification) {
                        var defaultIdentification = $scope.objectInfo.defaultIdentification;
                        if (Util.isEmpty(defaultIdentification)) {
                            return true;
                        }
                        var comparisonProperties = [ "identificationID", "identificationType", "identificationNumber", "identificationIssuer" ];
                        return Util.objectsComparisonByGivenProperties(defaultIdentification, identification, comparisonProperties);
                    }
                }

        ]);