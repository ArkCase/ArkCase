'use strict';

angular.module('document-details').controller('Document.OCRController',
        [ '$scope','DocumentDetails.OCRService', 'UtilService',  'MessageService', 'moment', '$modal', 'Admin.OcrManagementService', '$translate', '$log' , function($scope, OCRService, Util, MessageService, moment, $modal, OcrManagementService, $translate, $log) {

            var activeVersion = null;

            $scope.ocrDataModel = null;
            $scope.ecmFile = null;
            $scope.$on('document-data', function(event, ecmFile) {
                $scope.ecmFile = ecmFile;
                getOcr($scope.ecmFile);
            });

            $scope.$on('refresh-ocr', function(event){
                getOcr($scope.ecmFile);
            });

            function getOcr(ecmFile) {
                if (!Util.isEmpty(ecmFile)) {
                    activeVersion = $scope.getEcmFileActiveVersion(ecmFile);
                    OcrManagementService.getProperties().then(function (configResult) {
                        if (!Util.isEmpty(configResult) && !Util.isEmpty(configResult.data) && configResult.data['ocr.enabled']) {
                            if (!Util.isEmpty(activeVersion)) {
                                OCRService.getOCRbyFileId(ecmFile.fileId).then(function (ocrResult) {
                                    var ocrRes = Util.omitNg(ocrResult);
                                    if (_.isEmpty(ocrRes)) {
                                        $log.warn('OCR for file with id:' + ecmFile.fileId + 'cannot be found');
                                        return;
                                    }

                                    $scope.ocrDataModel = ocrResult;
                                    $scope.$emit('ocr-data-model', ocrResult);
                                }, function (ocrError) {
                                    MessageService.error(ocrError.data);
                                });
                            }
                        }
                    }, function (configError) {
                        MessageService.error(configError.data);
                    });
                }
            }

            $scope.getEcmFileActiveVersion = function(ecmFile) {
                if (Util.isEmpty(ecmFile) || Util.isEmpty(ecmFile.activeVersionTag) || Util.isArrayEmpty(ecmFile.versions)) {
                    return null;
                }

                var activeVersion = _.find(ecmFile.versions, function(version) {
                    return ecmFile.activeVersionTag === version.versionTag;
                });

                return activeVersion;
            };

        } ]);