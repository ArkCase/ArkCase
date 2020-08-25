'use strict';

angular.module('admin').controller('Admin.PDFConversionController',
        [ '$scope', 'Admin.PDFConversionService', 'MessageService', 'UtilService', function($scope, PDFConversionService, MessageService, Util) {

            $scope.pdfConversionProperties = {
                "responseFolderConversion": false
            };

            PDFConversionService.getProperties().then(function(response) {
                if (!Util.isEmpty(response.data)) {
                    $scope.pdfConversionProperties = response.data;
                }
            });

            $scope.save = function() {
                PDFConversionService.saveProperties($scope.pdfConversionProperties).then(function(response) {
                    MessageService.succsessAction();
                }, function(reaspon) {
                    MessageService.errorAction();
                });
            };

        } ]);