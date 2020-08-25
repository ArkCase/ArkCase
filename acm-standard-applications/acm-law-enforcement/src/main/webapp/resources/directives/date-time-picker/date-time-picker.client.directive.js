angular.module('directives').directive('dateTimePicker', ['moment', 'Util.DateService', 'UtilService', '$translate', function (moment, UtilDateService, UtilService, $translate) {
    return{
        restrict: 'E',
        templateUrl: 'directives/date-time-picker/date-time-picker.client.directive.html',
        scope: {
            data: '=',
            property: '@',
            timeFormatDisabled: '@',
            datePickerId: '@',
            afterSave : '&onAfterSave'
        },
        link: function ($scope, element) {
            $scope.editable = false;
            var dateInput = element[0].children[0].firstElementChild;
            if (dateInput) {
                dateInput.addEventListener("keydown",function(e){
                    // if you haven't already:
                    e = e || window.event;
                    // to cancel the event:
                    if( e.preventDefault) e.preventDefault();
                    return false;
                });
            }
            $scope.minYear = "";
            $scope.utcDate = "";
            $scope.maxYear = "";
            var defaultDateTimePickerFormat = $translate.instant("common.defaultDateTimePickerFormat");

            $scope.setDate = function (date) {
                if (UtilService.isEmpty(date)) {
                    $scope.today = "";
                    $scope.dateInPicker = new Date();
                } else {
                    if ($scope.timeFormatDisabled === "true") {
                        $scope.today = (date instanceof String || typeof date == 'string') ? moment(date).local().format("MM/DD/YYYY") : moment(date).format("MM/DD/YYYY");
                    } else {
                        $scope.today = (date instanceof String || typeof date == 'string') ? moment.utc(date).local().format(defaultDateTimePickerFormat) : moment(date).format(defaultDateTimePickerFormat);
                    }
                    $scope.dateInPicker = UtilDateService.isoToDate($scope.today);
                }
                $scope.minYear = 1900;
                $scope.maxYear = moment.utc($scope.dateInPicker).year() + 1;
            };

            $scope.setDate($scope.data);

            $scope.toggleEditable = function () {
                $scope.editable = !$scope.editable;
                if(!moment($(comboField).combodate("getValue")).isSame($scope.dateInPicker)){
                    $(comboField).combodate('setValue', $scope.dateInPicker);
                }
            };

            var comboField = element[0].children[1].firstElementChild;
            $scope.$watch('timeFormatDisabled', function () {
                if ($scope.timeFormatDisabled === "true") {
                    $(comboField).combodate({
                        format: 'MM/DD/YYYY',
                        template: 'MMM / DD / YYYY',
                        minuteStep: 1,
                        minYear: $scope.minYear,
                        maxYear: $scope.maxYear,
                        smartDays: true,
                        value: $scope.dateInPicker
                    });
                    if(!UtilService.isEmpty($scope.data)) {
                        $scope.today = ($scope.data instanceof String || typeof $scope.data == 'string') ? moment.utc($scope.data).local().format("MM/DD/YYYY") : moment($scope.data).format("MM/DD/YYYY");
                    } else {
                        $scope.today = "";
                    }
                    $scope.dateInPicker = !UtilService.isEmpty($scope.data) ? UtilDateService.isoToDate($scope.data) : new Date();
                } else {
                    $(comboField).combodate({
                        format: defaultDateTimePickerFormat,
                        template: 'MMM / DD / YYYY h:mm A',
                        minuteStep: 1,
                        minYear: $scope.minYear,
                        maxYear: $scope.maxYear,
                        smartDays: true,
                        value: $scope.dateInPicker
                    });
                    if(!UtilService.isEmpty($scope.data)) {
                        $scope.today = ($scope.data instanceof String || typeof $scope.data == 'string') ? moment.utc($scope.data).local().format(defaultDateTimePickerFormat) : moment($scope.data).format(defaultDateTimePickerFormat);
                    } else {
                        $scope.today = "";
                    }
                    $scope.dateInPicker = !UtilService.isEmpty($scope.data) ? UtilDateService.isoToLocalDateTime($scope.data) : new Date();
                }
            });

            $scope.saveDate = function () {
                var editedDate = $(comboField).combodate('getValue', null);
                if ($scope.timeFormatDisabled === "true") {
                    $scope.dateInPicker = moment(editedDate);
                    $scope.data = UtilDateService.localDateToIso($scope.dateInPicker.toDate());
                } else {
                    $scope.dateInPicker = moment(editedDate);
                    $scope.data = UtilDateService.dateToIsoDateTime($scope.dateInPicker);
                }
                $scope.toggleEditable();
            };

            $scope.cancel = function () {
                $scope.toggleEditable();
                $(comboField).combodate('setValue', $scope.dateInPicker);
            };
        },
        controller: function ($scope) {
            $scope.$watch('data', function () {
                //called any time $scope.data changes
                $scope.setDate($scope.data);
            });
        }
    }
}]);
