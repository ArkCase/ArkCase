'use strict';

angular.module('admin').controller('Admin.OutgoingIncomingEmailConversionController',
    [ '$scope', 'Admin.OutgoingIncomingEmailConversionService', 'MessageService', 'UtilService', function($scope, OutgoingIncomingEmailConversionService, MessageService, Util) {


        $scope.outgoingIncomingEmailConversionConfig = {
            "convertEmailsToPDF.incomingEmailToPdf": false,
            "convertEmailsToPDF.outgoingEmailToPdf": false
        };

        OutgoingIncomingEmailConversionService.getOutgoingIncomingEmailConversionConfiguration().then(
            function(result) {
                $scope.outgoingIncomingEmailConversionConfig = result.data;
            }
        );

        $scope.save = function() {
            OutgoingIncomingEmailConversionService.saveOutgoingIncomingEmailConversionConfiguration($scope.outgoingIncomingEmailConversionConfig).then(
                function(result) {
                    MessageService.succsessAction();
                },
                function(reason) {
                    MessageService.errorAction();
                }
            );
        };

    }]);