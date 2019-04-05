'use strict';

angular.module('document-details').controller(
        'Document.DetailsController',
        [ '$scope', '$translate', '$filter', '$modal', '$q', 'Object.LookupService', 'Organization.InfoService', 'Person.InfoService', 'EcmService', 'MessageService', 'UtilService', 'LookupService', 'ConfigService', 'Helper.LocaleService', 'Mentions.Service',
                function($scope, $translate, $filter, $modal, $q, ObjectLookupService, OrganizationInfoService, PersonInfoService, EcmService, MessageService, UtilService, LookupService, ConfigService, LocaleHelper, MentionsService) {

                    new LocaleHelper.Locale({
                        scope: $scope
                    });

                    $scope.$on('document-data', function(event, ecmFile) {

                        var _ecmFile = angular.copy(ecmFile);
                        var promiseGetUserFullNames = LookupService.getUserFullNames();
                        var promiseGetPersonInfo = null;
                        var promiseGetOrganizationInfo = null;

                        if (_ecmFile.personAssociation) {
                            promiseGetPersonInfo = PersonInfoService.getPersonInfo(_ecmFile.personAssociation.targetId);
                        }

                        if (_ecmFile.organizationAssociation) {
                            promiseGetOrganizationInfo = OrganizationInfoService.getOrganizationInfo(_ecmFile.organizationAssociation.targetId);
                        }

                        var promises = [ promiseGetUserFullNames, promiseGetPersonInfo, promiseGetOrganizationInfo ];

                        // Be sure that all needed information will be loaded. After that proceed with populating all information.
                        // This will show all information on the UI in the same moment
                        $q.all(promises).then(function(response) {
                            var _activeVersion = $scope.getActiveVersion(_ecmFile);

                            // Keep original EcmFile. We will need later just to get original version-modified date
                            $scope.ecmFile = ecmFile;

                            $scope.details.ecmFile = _ecmFile;
                            $scope.details.activeVersion = _activeVersion;
                            $scope.details.person = response[1] != null ? (response[1].givenName + ' ' + response[1].familyName).trim() : '';
                            $scope.details.organization = response[2] != null ? response[2].organizationValue : '';
                            $scope.details.creator = $scope.get(_.find(response[0], {
                                id: $scope.details.activeVersion.creator
                            }), 'name');
                            $scope.details.modifier = $scope.get(_.find(response[0], {
                                id: $scope.details.activeVersion.modifier
                            }), 'name');
                            $scope.details.verModified = _activeVersion.created;
                            $scope.details.size = UtilService.convertBytesToSize($scope.details.activeVersion.fileSizeBytes);
                            $scope.details.verMediaCreated = _activeVersion.mediaCreated;

                            $scope.saveButton.disabled = false;
                            $scope.saveInProgress = false;
                        });

                        $scope.objectType = _ecmFile.container.containerObjectType;

                        if ($scope.objectType != "TASK") {
                            ObjectLookupService.getPersonTypes($scope.objectType).then(function(personTypes) {
                                $scope.personTypes = personTypes;
                                return personTypes;
                            });
                        }
                    });

                    $scope.details = {};
                    $scope.saveButton = {};
                    $scope.saveButton.disabled = true;
                    $scope.saveInProgress = false;

                                        
                    $scope.paramsSummernote = {
                        emailAddresses: [],
                        usersMentioned: []
                    };

                    $scope.get = function(object, key) {
                        if (_.has(object, key)) {
                            return object[key];
                        }
                        return '';
                    };

                    /*$scope.convert = function(bytes, precision) {
                        if (isNaN(parseFloat(bytes)) || !isFinite(bytes)) {
                            return '';
                        }
                        if (typeof precision === 'undefined') {
                            precision = 1;
                        }
                        var units = [ 'bytes', 'KB', 'MB', 'GB', 'TB', 'PB' ];
                        var number = Math.floor(Math.log(bytes) / Math.log(1024));

                        return (bytes / Math.pow(1024, Math.floor(number))).toFixed(precision) + ' ' + units[number];
                    };*/

                    ObjectLookupService.getFileTypes().then(function(fileTypes) {
                        $scope.fileTypes = fileTypes;
                        return fileTypes;
                    });

                    ObjectLookupService.getOrganizationTypes().then(function(organizationTypes) {
                        $scope.organizationTypes = organizationTypes;
                        return organizationTypes;
                    });

                    $scope.getActiveVersion = function(ecmFile) {
                        if (ecmFile && ecmFile.versions) {
                            var found = _.find(ecmFile.versions, {
                                versionTag: ecmFile.activeVersionTag
                            });
                            if (found) {
                                return found;
                            }
                        }

                        return {};
                    };

                    $scope.setActiveVersion = function(ecmFile, activeVersion) {
                        if (ecmFile && ecmFile.versions && activeVersion) {
                            for (var i = 0; i < ecmFile.versions.length; i++) {
                                // Set strong guard
                                if (ecmFile.versions[i].versionTag === ecmFile.activeVersionTag && ecmFile.versions[i].versionTag === activeVersion.versionTag) {
                                    ecmFile.versions[i] = activeVersion;
                                }
                            }
                        }

                        return ecmFile;
                    };

                    ConfigService.getModuleConfig('common').then(function(moduleConfig) {
                        $scope.commonConfig = moduleConfig;
                    });

                    $scope.addOrganization = function() {
                        var params = {};
                        params.organizationValue = $scope.details.organization;
                        params.hideAssociationTypes = true;

                        var modalInstance = $modal.open({
                            scope: $scope,
                            animation: true,
                            templateUrl: 'modules/common/views/add-organization-modal.client.view.html',
                            controller: 'Common.AddOrganizationModalController',
                            size: 'md',
                            backdrop: 'static',
                            resolve: {
                                params: function() {
                                    return params;
                                }
                            }
                        });

                        modalInstance.result.then(function(data) {
                            if (data.isNew) {
                                OrganizationInfoService.saveOrganizationInfo(data.organization).then(function(saved) {
                                    var association = $scope.newAssociation();
                                    association.targetId = saved.organizationId;
                                    association.targetType = 'ORGANIZATION';
                                    association.associationType = "REFERENCE";
                                    $scope.details.ecmFile.organizationAssociation = association;
                                    $scope.details.organization = saved.organizationValue;
                                });
                            } else {
                                OrganizationInfoService.getOrganizationInfo(data.organizationId).then(function(organization) {
                                    var association = $scope.newAssociation();
                                    association.targetId = organization.organizationId;
                                    association.targetType = 'ORGANIZATION';
                                    association.associationType = "REFERENCE";
                                    $scope.details.ecmFile.organizationAssociation = association;
                                    $scope.details.organization = organization.organizationValue;
                                })
                            }
                        });
                    };

                    $scope.addPerson = function() {
                        var params = {};
                        params.personName = $scope.details.person;
                        params.hideAssociationTypes = true;

                        var modalInstance = $modal.open({
                            scope: $scope,
                            animation: true,
                            templateUrl: 'modules/common/views/add-person-modal.client.view.html',
                            controller: 'Common.AddPersonModalController',
                            size: 'md',
                            backdrop: 'static',
                            resolve: {
                                params: function() {
                                    return params;
                                }
                            }
                        });

                        modalInstance.result.then(function(data) {
                            if (data.isNew) {
                                PersonInfoService.savePersonInfo(data.person).then(function(saved) {
                                    var fullName = (saved.givenName + ' ' + saved.familyName).trim();
                                    var association = $scope.newAssociation();
                                    association.targetId = saved.id;
                                    association.targetType = 'PERSON';
                                    association.associationType = "REFERENCE";
                                    $scope.details.ecmFile.personAssociation = association;
                                    $scope.details.person = fullName;
                                });
                            } else {
                                PersonInfoService.getPersonInfo(data.personId).then(function(person) {
                                    var fullName = (person.givenName + ' ' + person.familyName).trim();
                                    var association = $scope.newAssociation();
                                    association.targetId = person.id;
                                    association.targetType = 'PERSON';
                                    association.associationType = "REFERENCE";
                                    $scope.details.ecmFile.personAssociation = association;
                                    $scope.details.person = fullName;
                                })
                            }
                        });
                    };

                    $scope.newAssociation = function() {
                        return {
                            associationId: null,
                            status: '',
                            parentId: $scope.details.ecmFile.fileId,
                            parentType: $scope.details.ecmFile.fileType,
                            parentTitle: '',
                            targetId: null,
                            targetType: '',
                            targetSubtype: '',
                            associationType: '',
                            targetName: '',
                            targetTitle: '',
                            category: '',
                            description: ''
                        };
                    };

                    // Save Details
                    $scope.save = function() {
                        $scope.saveButton.disabled = true;
                        $scope.saveInProgress = true;
                        $scope.details.ecmFile = $scope.setActiveVersion($scope.details.ecmFile, $scope.details.activeVersion);

                        UtilService.serviceCall({
                            service: EcmService.updateFile,
                            param: {
                                fileId: $scope.details.ecmFile.fileId
                            },
                            data: JSOG.encode(UtilService.omitNg($scope.details.ecmFile))
                        }).then(function(data) {
                            $scope.$broadcast('document-data', data);
                            var url = "/home.html#!/viewer/" + data.fileId + "/" + data.container.containerObjectId + "/" + data.container.containerObjectType + "/" + encodeURIComponent(data.fileName) + "/" + data.fileId;
                            MentionsService.sendEmailToMentionedUsersWithUrl($scope.paramsSummernote.emailAddresses, $scope.paramsSummernote.usersMentioned, data.container.containerObjectType, data.container.containerObjectId, url, data.description);
                            MessageService.info($translate.instant('documentDetails.comp.details.message.save.success'));
                            return data;
                        }, function(error) {
                            MessageService.error($translate.instant('documentDetails.comp.details.message.save.error'));
                            return error;
                        });
                    }
                } ]);
