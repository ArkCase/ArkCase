'use strict';

angular.module('organizations').controller(
        'Organizations.UrlsController',
        [ '$scope', '$q', '$stateParams', '$translate', '$modal', 'UtilService', 'ObjectService', 'Organization.InfoService', 'Authentication', 'Helper.UiGridService', 'Helper.ObjectBrowserService', 'PermissionsService', 'Object.LookupService', 'Mentions.Service',
                function($scope, $q, $stateParams, $translate, $modal, Util, ObjectService, OrganizationInfoService, Authentication, HelperUiGridService, HelperObjectBrowserService, PermissionsService, ObjectLookupService, MentionsService) {

                    Authentication.queryUserInfo().then(function(userInfo) {
                        $scope.userId = userInfo.userId;
                        return userInfo;
                    });

                    var componentHelper = new HelperObjectBrowserService.Component({
                        scope: $scope,
                        stateParams: $stateParams,
                        moduleId: "organizations",
                        componentId: "urls",
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
                        var urls = _.filter($scope.objectInfo.contactMethods, {
                            type: 'url'
                        });
                        $scope.gridOptions.data = urls;
                    };

                    ObjectLookupService.getSubContactMethodType('url').then(function(contactMethodTypes) {
                        $scope.urlTypes = contactMethodTypes;
                        return contactMethodTypes;
                    });

                    $scope.addNew = function() {
                        var url = {};
                        url.created = Util.dateToIsoString(new Date());
                        url.creator = $scope.userId;
                        url.className = "com.armedia.acm.plugins.addressable.model.ContactMethod";

                        //put contactMethod to scope, we will need it when we return from popup
                        $scope.url = url;
                        var item = {
                            id: '',
                            parentId: $scope.objectInfo.id,
                            type: 'url',
                            subType: '',
                            value: '',
                            description: ''
                        };
                        showModal(item, false);
                    };

                    $scope.editRow = function(rowEntity) {
                        $scope.url = rowEntity;
                        var item = {
                            id: rowEntity.id,
                            type: rowEntity.type,
                            subType: rowEntity.subType,
                            subLookup: rowEntity.subType,
                            value: rowEntity.value,
                            description: rowEntity.description
                        };
                        showModal(item, true);

                    };

                    $scope.deleteRow = function(rowEntity) {
                        var id = Util.goodMapValue(rowEntity, "id", 0);
                        if (0 < id) { //do not need to call service when deleting a new row with id==0
                            $scope.objectInfo.contactMethods = _.remove($scope.objectInfo.contactMethods, function(item) {
                                return item.id != id;
                            });
                            saveObjectInfoAndRefresh()
                        }
                    };

                    function showModal(url, isEdit) {
                        var params = {};
                        params.url = url || {};
                        params.isEdit = isEdit || false;
                        params.isDefault = $scope.isDefault(url);

                        var modalInstance = $modal.open({
                            animation: true,
                            templateUrl: 'modules/organizations/views/components/organization-urls-modal.client.view.html',
                            controller: 'Organizations.UrlsModalController',
                            size: 'md',
                            backdrop: 'static',
                            resolve: {
                                params: function() {
                                    return params;
                                }
                            }
                        });

                        modalInstance.result.then(function(data) {
                            var url;
                            if (!data.isEdit)
                                url = $scope.url;
                            else {
                                url = _.find($scope.objectInfo.contactMethods, {
                                    id: data.url.id
                                });
                            }
                            url.type = 'url';
                            url.subType = data.url.subLookup;
                            url.value = data.url.value;
                            url.description = data.url.description;
                            
                            if (!_.contains(url.value, 'https://') && !_.contains(url.value, 'http://')) {
                                url.value = "https://" + url.value;
                            }

                            if (!data.isEdit) {
                                $scope.objectInfo.contactMethods.push(url);
                            }

                            var urls = _.filter($scope.objectInfo.contactMethods, {
                                type: 'url'
                            });
                            if (data.isDefault || urls.length == 1) {
                                $scope.objectInfo.defaultUrl = url;
                            }

                            $scope.objectInfo.emailAddresses = data.emailAddresses;
                            $scope.objectInfo.usersMentioned = data.usersMentioned;
                            $scope.objectInfo.textMentioned = data.url.description;
                            saveObjectInfoAndRefresh();
                        });
                    }

                    function saveObjectInfoAndRefresh() {
                        var promiseSaveInfo = Util.errorPromise($translate.instant("common.service.error.invalidData"));
                        if (OrganizationInfoService.validateOrganizationInfo($scope.objectInfo)) {
                            var objectInfo = Util.omitNg($scope.objectInfo);
                            promiseSaveInfo = OrganizationInfoService.saveOrganizationInfo(objectInfo);
                            promiseSaveInfo.then(function(objectInfo) {
                                MentionsService.sendEmailToMentionedUsers($scope.objectInfo.emailAddresses, $scope.objectInfo.usersMentioned,
                                    ObjectService.ObjectTypes.ORGANIZATION, "URL", objectInfo.organizationId, $scope.objectInfo.textMentioned);
                                $scope.$emit("report-object-updated", objectInfo);
                                return objectInfo;
                            }, function(error) {
                                $scope.$emit("report-object-update-failed", error);
                                return error;
                            });
                        }
                        return promiseSaveInfo;
                    }

                    $scope.isDefault = function(url) {
                        var defaultUrl = $scope.objectInfo.defaultUrl;
                        if (Util.isEmpty(defaultUrl)) {
                            return true;
                        }
                        var comparisonProperties = [ "id", "type", "subType", "value", "description" ];
                        return Util.objectsComparisonByGivenProperties(defaultUrl, url, comparisonProperties);
                    }
                } ]);