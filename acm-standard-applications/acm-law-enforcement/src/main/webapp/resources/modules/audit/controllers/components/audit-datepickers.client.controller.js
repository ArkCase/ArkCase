'use strict';

/**
 * @ngdoc controller
 * @name audit.controller:Audit.DatepickersController
 *
 * @description
 * {@link https://github.com/Armedia/ACM3/tree/develop/acm-user-interface/ark-web/src/main/webapp/resources/modules/audit/controllers/components/audit-datepickers.client.controller.js modules/audit/controllers/components/audit-datepickers.client.controller.js}
 *
 * The Audit module Datepickers controller
 */
angular.module('audit').controller('Audit.DatepickersController', ['$scope', 'Helper.LocaleService',
    function ($scope, LocaleHelper) {

        $scope.config = null;
        $scope.$on('component-config', applyConfig);
        function applyConfig(e, componentId, config) {
            if (componentId == 'datepickers') {
                $scope.config = config;
            }
        }
        $scope.$emit('req-component-config', 'datepickers');


        new LocaleHelper.Locale({scope: $scope});


        $scope.dateFrom = new Date();
        $scope.dateTo = new Date();
        $scope.$on('fix-date-values', fixDateValues);
        /**
         * @ngdoc method
         * @name fixDateValues
         * @methodOf audit.controller:Audit.DatepickersController
         *
         * @description
         * This function is callback function which gets called when "fix-date-values" event is emitted.
         * In this function values are being assigned for $scope.dateFrom and $scope.dateTo from selected datepickers.
         * Value for dateTo will be same as dateFrom because dateFrom can't have value bigger than dateTo
         *
         * @param {Object} e This is event object which have several useful properties and functions
         * @param {String} dateFrom String that represents value for date chosen from dateFrom input
         * @param {String} dateTo String that represents value for date that is same for dateFrom input
         */
        function fixDateValues(e, dateFrom, dateTo){
            $scope.dateFrom = new Date(dateFrom);
            $scope.dateTo = new Date(dateTo);
        }

        $scope.$watchGroup(['dateFrom','dateTo'], function(){
            $scope.$emit('send-date', $scope.dateFrom, $scope.dateTo);
        });

        $scope.opened = {};
        $scope.opened.openedStart = false;
        $scope.opened.openedEnd = false;
    }
]);