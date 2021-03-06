'use strict';

angular.module('people').controller(
        'Person.AliasesController',
        [ '$scope', '$stateParams', '$translate', 'UtilService', 'ConfigService', 'Person.InfoService', 'MessageService', 'Helper.ObjectBrowserService', 'Helper.UiGridService', 'Authentication', 'Person.PicturesService', '$modal', 'PermissionsService', 'ObjectService', 'Object.LookupService',
                'Mentions.Service',
                function($scope, $stateParams, $translate, Util, ConfigService, PersonInfoService, MessageService, HelperObjectBrowserService, HelperUiGridService, Authentication, PersonPicturesService, $modal, PermissionsService, ObjectService, ObjectLookupService, MentionsService) {

                    new HelperObjectBrowserService.Component({
                        scope: $scope,
                        stateParams: $stateParams,
                        moduleId: "people",
                        componentId: "aliases",
                        retrieveObjectInfo: PersonInfoService.getPersonInfo,
                        onConfigRetrieved: function(componentConfig) {
                            return onConfigRetrieved(componentConfig);
                        },
                        onObjectInfoRetrieved: function(objectInfo) {
                            onObjectInfoRetrieved(objectInfo);
                        }
                    });

                    $scope.personInfo = null;

                    var currentUser = '';

                    var gridHelper = new HelperUiGridService.Grid({
                        scope: $scope
                    });

                    var promiseUsers = gridHelper.getUsers();

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
                        gridHelper.setUserNameFilterToConfig(promiseUsers, config);
                    };

                    var onObjectInfoRetrieved = function(objectInfo) {
                        $scope.objectInfo = objectInfo;

                        if (objectInfo.personAliases) {
                            $scope.gridOptions.data = objectInfo.personAliases;
                            $scope.gridOptions.noData = false;
                        } else {
                            $scope.gridOptions.data = [];
                            $scope.gridOptions.noData = true;
                        }
                    };

                    ObjectLookupService.getAliasTypes().then(function(aliasTypes) {
                        $scope.aliasTypes = aliasTypes;
                    });

                    //Aliases
                    $scope.addNew = function() {

                        var alias = {};
                        alias.created = Util.dateToIsoString(new Date());
                        alias.creator = $scope.userId;
                        $scope.alias = alias;
                        var item = {
                            id: '',
                            parentId: $scope.objectInfo.id,
                            aliasType: '',
                            aliasValue: '',
                            description: ''
                        };
                        showModal(item, false);
                    };
                    $scope.editRow = function(rowEntity) {
                        $scope.alias = rowEntity;
                        var item = {
                            id: rowEntity.id,
                            parentId: $scope.objectInfo.id,
                            aliasType: rowEntity.aliasType,
                            aliasValue: rowEntity.aliasValue,
                            description: rowEntity.description
                        };
                        showModal(item, true);
                    };

                    $scope.deleteRow = function(rowEntity) {
                        var id = Util.goodMapValue(rowEntity, "id", 0);
                        if (0 < id) { //do not need to call service when deleting a new row with id==0
                            $scope.objectInfo.personAliases = _.remove($scope.objectInfo.personAliases, function(item) {
                                return item.id != id;
                            });
                            saveObjectInfoAndRefresh()
                        }
                    };

                    function showModal(alias, isEdit) {
                        var params = {};
                        params.alias = alias || {};
                        params.isEdit = isEdit || false;
                        params.isDefault = $scope.isDefault(alias);

                        var modalInstance = $modal.open({
                            animation: true,
                            templateUrl: "modules/people/views/components/person-aliases-modal.client.view.html",
                            controller: 'Person.AliasesModalController',
                            size: 'md',
                            backdrop: 'static',
                            resolve: {
                                params: function() {
                                    return params;
                                }
                            }
                        });

                        modalInstance.result.then(function(data) {
                            var alias;
                            if (!data.isEdit)
                                alias = $scope.alias;
                            else {
                                alias = _.find($scope.objectInfo.personAliases, {
                                    id: data.alias.id
                                });
                            }
                            alias.aliasType = data.alias.aliasType;
                            alias.aliasValue = data.alias.aliasValue;
                            alias.description = data.alias.description;
                            if (!data.isEdit) {
                                $scope.objectInfo.personAliases.push(alias);
                            }

                            if (data.isDefault || $scope.objectInfo.personAliases.length == 1) {
                                $scope.objectInfo.defaultAlias = alias;
                            }

                            $scope.objectInfo.emailAddresses = data.emailAddresses;
                            $scope.objectInfo.usersMentioned = data.usersMentioned;
                            $scope.objectInfo.textMentioned = data.alias.description;
                            saveObjectInfoAndRefresh();
                        });
                    }

                    function saveObjectInfoAndRefresh() {
                        var promiseSaveInfo = Util.errorPromise($translate.instant("common.service.error.invalidData"));
                        if (PersonInfoService.validatePersonInfo($scope.objectInfo)) {
                            var objectInfo = Util.omitNg($scope.objectInfo);
                            promiseSaveInfo = PersonInfoService.savePersonInfo(objectInfo);
                            promiseSaveInfo.then(function(objectInfo) {
                                MentionsService.sendEmailToMentionedUsers($scope.objectInfo.emailAddresses, $scope.objectInfo.usersMentioned,
                                    ObjectService.ObjectTypes.PERSON, "ALIAS", objectInfo.id, $scope.objectInfo.textMentioned);
                                $scope.$emit("report-object-updated", objectInfo);
                                return objectInfo;
                            }, function(error) {
                                $scope.$emit("report-object-update-failed", error);
                                return error;
                            });
                        }
                        return promiseSaveInfo;
                    }

                    $scope.isDefault = function(alias) {
                        var defaultAlias = $scope.objectInfo.defaultAlias;
                        if (Util.isEmpty(defaultAlias)) {
                            return true;
                        }
                        var comparisonProperties = [ "id", "aliasType", "aliasValue", "description" ];
                        return Util.objectsComparisonByGivenProperties(defaultAlias, alias, comparisonProperties);
                    }

                } ]);