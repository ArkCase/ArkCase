'use strict';

angular.module('document-details').controller('Document.DetailsController',
    ['$scope', '$translate', '$filter', '$modal', 'Object.LookupService', 'Organization.InfoService', 'Person.InfoService', 'EcmService', 'MessageService', 'UtilService', 'ConfigService'
        , function ($scope, $translate, $filter, $modal, ObjectLookupService, OrganizationInfoService, PersonInfoService, EcmService, MessageService, UtilService, ConfigService) {

        $scope.$on('document-data', function (event, ecmFile) {

            var _ecmFile = angular.copy(ecmFile);
            var _activeVersion = $scope.getActiveVersion(_ecmFile);

            if (_ecmFile.created) {
                _ecmFile.created = $filter('date')(_ecmFile.created, $translate.instant("common.defaultDateTimeUIFormat"));
            }
            if (_ecmFile.modified) {
                _ecmFile.modified = $filter('date')(_ecmFile.created, $translate.instant("common.defaultDateTimeUIFormat"));
            }
            if (_activeVersion.created) {
                _activeVersion.created = $filter('date')(_activeVersion.created, $translate.instant("common.defaultDateTimeUIFormat"));
            }
            if (_activeVersion.modified) {
                _activeVersion.modified = $filter('date')(_activeVersion.created, $translate.instant("common.defaultDateTimeUIFormat"));
            }

            // Keep original EcmFile. We will need later just to get original (not changed) dates for created, modified, version-created, version-modified
            $scope.ecmFile = ecmFile;

            $scope.details = {};
            $scope.details.ecmFile = _ecmFile;
            $scope.details.activeVersion = _activeVersion;
            $scope.details.personFullName = '';

            if (_ecmFile.personAssociation) {
                $scope.details.personFullName = (_ecmFile.personAssociation.person.givenName + ' ' + _ecmFile.personAssociation.person.familyName).trim();
            }

        });

        $scope.options = {
            focus: false,
            dialogsInBody: true
        };

        ObjectLookupService.getFileTypes().then(function (fileTypes) {
                $scope.fileTypes = fileTypes;
                return fileTypes;
        });

        ObjectLookupService.getOrganizationTypes().then(function (organizationTypes) {
            $scope.organizationTypes = organizationTypes;
            return organizationTypes;
        });

        ObjectLookupService.getPersonTypes().then(function (personTypes) {
            var options = [];
            _.forEach(personTypes, function (v, k) {
                options.push({type: v, name: v});
            });
            $scope.personTypes = options;
            return personTypes;
        });

        $scope.getActiveVersion = function (ecmFile) {
            if (ecmFile && ecmFile.versions) {
                var found = _.find(ecmFile.versions, {versionTag: ecmFile.activeVersionTag});
                if (found) {
                    return found;
                }
            }

            return {};
        }

        $scope.setActiveVersion = function (ecmFile, activeVersion) {
            if (ecmFile && ecmFile.versions && activeVersion) {
                for (var i = 0; i < ecmFile.versions.length; i++) {
                    // Set strong guard
                    if (ecmFile.versions[i].versionTag === ecmFile.activeVersionTag && ecmFile.versions[i].versionTag === activeVersion.versionTag) {
                        ecmFile.versions[i] = activeVersion;
                    }
                }
            }

            return ecmFile;
        }

        ConfigService.getModuleConfig('common').then(function (moduleConfig) {
            $scope.commonConfig = moduleConfig;
        });


        $scope.addOrganization = function () {
            var params = {};
            params.types = $scope.organizationTypes;

            var modalInstance = $modal.open({
                scope: $scope,
                animation: true,
                templateUrl: 'modules/common/views/add-organization-modal.client.view.html',
                controller: 'Common.AddOrganizationModalController',
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
                    $scope.details.ecmFile.organization = data.organization;
                } else {
                    OrganizationInfoService.getOrganizationInfo(data.organizationId).then(function (organization) {
                        $scope.details.ecmFile.organization = organization;
                    })
                }
            });
        };

        $scope.addPerson = function () {
            var params = {};
            params.types = $scope.personTypes;

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
                    var association = $scope.newPersonAssociation();
                    association.person = data.person;
                    association.personType = data.type;
                    $scope.details.ecmFile.personAssociation = association;
                    // Do this since Person object don't have full name
                    $scope.details.personFullName = (data.person.givenName + ' ' + data.person.familyName).trim();
                } else {
                    PersonInfoService.getPersonInfo(data.personId).then(function (person) {
                        var association = $scope.newPersonAssociation();
                        association.person = person;
                        association.personType = data.type;
                        $scope.details.ecmFile.personAssociation = association;
                        // Do this since Person object don't have full name
                        $scope.details.personFullName = (person.givenName + ' ' + person.familyName).trim();
                    })
                }
            });
        };

        $scope.newPersonAssociation = function () {
            return {
                id: null
                , personType: ""
                , parentId: $scope.details.ecmFile.fileId
                , parentType: $scope.details.ecmFile.fileType
                , parentTitle: $scope.details.ecmFile.fileName
                , personDescription: ""
                , notes: ""
                , person: null
                , className: "com.armedia.acm.plugins.person.model.PersonAssociation"
            };
        };

        // Save Details
        $scope.save = function () {
            // Back changed date formats to original
            $scope.details.ecmFile.created = $scope.ecmFile.created;
            $scope.details.ecmFile.modified = $scope.ecmFile.modified;

            var _activeVersion = $scope.getActiveVersion($scope.ecmFile);
            $scope.details.activeVersion.created = _activeVersion.created;
            $scope.details.activeVersion.modified = _activeVersion.modified;

            $scope.details.ecmFile = $scope.setActiveVersion($scope.details.ecmFile, $scope.details.activeVersion);

            UtilService.serviceCall({
                service: EcmService.updateFile
                , param: {fileId: $scope.details.ecmFile.fileId}
                , data: JSOG.encode(UtilService.omitNg($scope.details.ecmFile))
            }).then(
                function (data) {
                    $scope.$broadcast('document-data', data);
                    MessageService.info($translate.instant('documentDetails.comp.details.message.save.success'));
                    return data;
                },
                function (error) {
                    MessageService.error($translate.instant('documentDetails.comp.details.message.save.error'));
                    return error;
                }
             );
        }
    }
    ]);