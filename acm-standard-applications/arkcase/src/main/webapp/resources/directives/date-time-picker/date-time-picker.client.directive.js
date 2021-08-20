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
            isRequired: '=',
            disableWeekends: '=?',
            onDateSelect: '=?',
            isReadonly: '=?',
            noDefaultDate: "=?",
            pickerId: "@"
        },
        link: function ($scope, element, attrs) {
            var inputElement = element.children()[0].firstElementChild;
            $scope.message = null;
            $scope.format = $scope.showTime ? $translate.instant("common.defaultDateTimePickerFormat") : $translate.instant("common.defaultDateFormat");
            $scope.validFormat = $scope.showTime ? $translate.instant("common.validDateTimeFormat") : $translate.instant("common.defaultDateFormat");
            $scope.invalidFormatMessage =  $translate.instant("common.invalidDateFormat");
            $scope.datepickerOptions = {isOpen: false};
            $scope.placeholder = $scope.noDefaultDate && $scope.disable ? '' : $translate.instant("common.defaultDateInputPlaceholder");
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
                    if (!$scope.noDefaultDate) {
                        $scope.dateInPicker = moment(new Date());
                    }
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
             * @param {String|Date} newValue  new date
             *
             */
            $scope.onDateChange = function (newValue) {
                if (newValue) {
                    if (!$scope.disable) {
                        // TODO find better way to handle manually changing data. This is hack to update datepicker data with
                        // TODO for example in new task when user choose today date later than due date and we update due date manually
                        if ($scope.$parent.dateChangedManually) {
                            $scope.dateInPicker = moment($scope.data);
                            inputElement.value = $scope.data;
                            $scope.message = null;
                            $scope.$parent.dateChangedManually = false;

                        } else {
                            if (!moment(inputElement.value).isSame(moment($scope.dateInPicker))) {
                                inputElement.value = $scope.dateInPicker;
                            }
                            $scope.updateDate(newValue);

                        }
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
             * @param {String|Date} date  new date
             *
             */
            $scope.onBlur = function (date) {
                if (!$scope.disable) {
                    // check if date is entered manually, otherwise user removed data manually
                    if (date.target.value) {
                        // if user enter date manually or blur is triggered after user close picker
                        // check if input date is same as datepicker chosen, if yes do nothing else
                        // update datepicker and date with manually entered date
                        if (!moment(date.target.value).isSame(moment($scope.dateInPicker))) {
                            if (isValidDate(date.target.value)) {
                                $scope.updateDate(date.target.value);
                            } else {
                                // set all dates to null
                                $scope.dateInPicker = null;
                                $scope.data = null;
                                inputElement.value = null;
                            }
                        }
                    } else {
                        // set all dates to null
                        $scope.dateInPicker = null;
                        $scope.data = null;
                        inputElement.value = null;
                    }
                }
            }

            /**
             * @ngdoc method
             * @name isValidDate
             * @methodOf directives:dateTimePicker
             *
             * @description
             * On manually entered date check for validation
             *
             * @param {String|Date} date to be validated
             *
             */
            function isValidDate(date) {
                if (moment(date, $scope.format, true).isValid()) {
                    if ($scope.minDate) {
                        if (moment(date).isBefore(moment($scope.minDate))) {
                            $scope.message = $translate.instant("common.invalidMinDate");
                            return false;
                        }
                        return true;
                    } else if ($scope.maxDate) {
                        if (moment(date).isAfter(moment($scope.maxDate))) {
                            $scope.message = $translate.instant("common.invalidMaxDate");
                            return false;
                        }
                        return true;
                    }
                    return true;

                } else {
                    $scope.message = $translate.instant("common.invalidDateFormat");
                    return false;
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
                $scope.message = null;
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
