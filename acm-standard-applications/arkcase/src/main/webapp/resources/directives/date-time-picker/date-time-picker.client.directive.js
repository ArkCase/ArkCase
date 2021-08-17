'use strict';

/**
 * @ngdoc directive
 * @name directives:dateTimePicker
 *
 * @description
 *
 * {@link /acm-standard-applications/arkcase/src/main/webapp/resources/directives/date-time-picker/date-time-picker.client.directive.js directives/date-time-picker/date-time-picker.client.directive.js}
 *
 * Directive for date and time picker that uses Moment.js
 */

angular.module('directives').directive('dateTimePicker', ['moment', 'Util.DateService', 'UtilService', '$translate', function (moment, UtilDateService, UtilService, $translate) {
    return{
        restrict: 'E',
        templateUrl: 'directives/date-time-picker/date-time-picker.client.directive.html',
        scope: {
            data: '=',
            showTime : '=?',
            disable: '=?',
            startDate: '=?',
            minDate: '=?',
            maxDate: '=?',
            onSelectClose: '=?',
            placeholder: '=?',
            isRequired: '=',
            disableWeekends: '=?',
            onDateSelect: '=?',
            isReadonly: '=?'
        },
        link: function ($scope, element) {
            $scope.format = $scope.showTime ? $translate.instant("common.defaultDateTimePickerFormat") : $translate.instant("common.defaultDateFormat");
            $scope.datepickerOptions = {isOpen: false};
            if ($scope.minDate) {
                $scope.minDate = moment($scope.minDate)
            }
            /**
             * @ngdoc method
             * @name setDate
             * @methodOf directives:dateTimePicker
             *
             * @description
             * set dates in local date/time format
             *
             * @param {String|Date} date  DocTree object defined in doc-tree directive
             *
             */
            $scope.setDate = function (date) {
                if (!UtilService.isEmpty(date)) {
                    if ($scope.showTime) {
                        $scope.today = (date instanceof String || typeof date == 'string') ? moment.utc(date).local().format($scope.format) : moment(date).format($scope.format);
                    } else {
                        $scope.today = (date instanceof String || typeof date == 'string') ? moment(date).local().format($scope.format) : moment(date).format($scope.format);
                    }
                    $scope.dateInPicker = moment(UtilDateService.isoToDate($scope.today));
                } else {
                    $scope.dateInPicker = moment(new Date());
                }
            };

            $scope.setDate($scope.data);

            /**
             * @ngdoc method
             * @name selectable
             * @methodOf directives:dateTimePicker
             *
             * @description
             * Disable weekends selection
             *
             * @param {Date} date  Date
             * @param {String} type  type of calendar
             *
             * @returns {boolean} Returns true if date can be selected
             */
            $scope.selectable = function (date, type) {
                return $scope.disableWeekends ? type !== 'day' || (date.format('dddd') !== $translate.instant("common.days.saturday") && date.format('dddd') !== $translate.instant("common.days.sunday")) : true;
            };

            /**
             * @ngdoc method
             * @name onDateChange
             * @methodOf directives:dateTimePicker
             *
             * @description
             * On date selection update date picker date and data
             *
             * @param {Date} newValue  DocTree object defined in doc-tree directive
             *
             */
            $scope.onDateChange = function (newValue) {
                if (!$scope.disable) {
                    // TODO find better way to handle manually changing data. This is hack to update datepicker data with
                    // TODO for example in new task when user choose today date later than due date and we update due date manually
                    if ($scope.$parent.dateChangedManually) {
                        $scope.dateInPicker = moment($scope.data);
                        $scope.$parent.dateChangedManually = false;
                        $scope.updateDate($scope.data);
                    } else {
                        $scope.updateDate(newValue);
                    }
                }

            }
            /**
             * @ngdoc method
             * @name onBlur
             * @methodOf directives:dateTimePicker
             *
             * @description
             * On manually entered date update date picker date and data
             *
             * @param {String} date  DocTree object defined in doc-tree directive
             *
             */
            $scope.onBlur = function (date) {
                if ($scope.dateInPicker !== date) {
                    $scope.updateDate(date);
                }
            }

            /**
             * @ngdoc method
             * @name updateDate
             * @methodOf services:DocTreeExt.Checkin
             *
             * @description
             * Updates data in utc format date/time
             *
             * @param {String|Date} date  DocTree object defined in doc-tree directive
             *
             */
            $scope.updateDate = function (date) {
                $scope.dateInPicker = moment(date);
                $scope.data = $scope.showTime ? $scope.data = UtilDateService.dateToIsoDateTime($scope.dateInPicker)
                    : $scope.data = UtilDateService.localDateToIso($scope.dateInPicker.toDate());
            }
        },
        controller: function ($scope) {
            $scope.$watch('data', function () {
                // called any time $scope.data changes
                // send updated date
                if ($scope.data) {
                    if ($scope.onDateSelect) {
                        $scope.onDateSelect({data: $scope.data, dateInPicker: $scope.dateInPicker});
                    }
                }
            });
        }
    }
}]);
