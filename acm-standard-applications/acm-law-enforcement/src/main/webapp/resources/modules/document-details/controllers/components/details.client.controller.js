'use strict';

angular.module('document-details').controller('Document.DetailsController',
    ['$scope', '$translate', '$filter', 'Object.LookupService', 'ConfigService'
        , function ($scope, $translate, $filter, ObjectLookupService, ConfigService) {

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
        });

        $scope.options = {
            focus: false,
            dialogsInBody: true
        };

        ObjectLookupService.getFileTypes().then(function (fileTypes) {
                $scope.fileTypes = fileTypes;
                return fileTypes;
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
            // TODO: Add Organisation
        };

        $scope.addPerson = function () {
            // TODO: Add Person
        };

        // Save Details
        $scope.save = function () {
            // TODO: Save changes
        }
    }
    ]);