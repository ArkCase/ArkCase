'use strict';

angular.module('document-details').controller('Document.MedicalComprehendController',
    ['$scope', 'DocumentDetails.MedicalComprehendService', 'UtilService', 'MessageService', '$log',
        function ($scope, MedicalComprehendService, Util, MessageService, $log) {

            var activeVersion = null;

            $scope.comprehendMedicalModel = null;
            $scope.ecmFile = null;

            $scope.$on('document-data', function (event, ecmFile) {
                $scope.ecmFile = ecmFile;
                getMedicalComprehend($scope.ecmFile);
            });

            $scope.$on('refresh-medical-comprehend', function (event) {
                getMedicalComprehend($scope.ecmFile);
            });

            function getMedicalComprehend(ecmFile) {
                if (!Util.isEmpty(ecmFile)) {
                    activeVersion = $scope.getEcmFileActiveVersion(ecmFile);
                    MedicalComprehendService.getComprehendMedicalByMediaId(activeVersion.id).then(function (result) {
                        var comprehendMedicalResult = Util.omitNg(result);
                        if (_.isEmpty(comprehendMedicalResult)) {
                            $log.warn('Comprehend Medical model for file with id:' + ecmFile.fileId + 'cannot be found');
                            return;
                        }
                        $scope.comprehendMedicalModel = comprehendMedicalResult;
                        $scope.$emit('comprehend-medical-model', comprehendMedicalResult);
                    }, function (error) {
                        MessageService.error(error.data);
                    });
                }
            }

            $scope.getEcmFileActiveVersion = function (ecmFile) {
                if (Util.isEmpty(ecmFile) || Util.isEmpty(ecmFile.activeVersionTag) || Util.isArrayEmpty(ecmFile.versions)) {
                    return null;
                } else {
                    return _.find(ecmFile.versions, function (version) {
                        return ecmFile.activeVersionTag === version.versionTag;
                    });
                }
            };

        }]);