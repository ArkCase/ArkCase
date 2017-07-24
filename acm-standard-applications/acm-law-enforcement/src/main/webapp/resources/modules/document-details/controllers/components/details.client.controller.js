'use strict';

angular.module('document-details').controller('Document.DetailsController',
    ['$scope', '$translate', '$filter', '$modal', 'Object.LookupService', 'Organization.InfoService', 'Person.InfoService', 'ConfigService'
        , function ($scope, $translate, $filter, $modal, ObjectLookupService, OrganizationInfoService, PersonInfoService, ConfigService) {

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
            // TODO: Save changes
        }
    }
    ]);