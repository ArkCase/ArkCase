angular.module('directives').directive('dateTimePicker', ['moment', 'Util.DateService', 'UtilService', function (moment, UtilDateService, UtilService) {
    return{
        restrict: 'E',
        templateUrl: 'directives/date-time-picker/date-time-picker.client.directive.html',
        scope: {
            data: '=',
            property: '@',
            datePickerId: '@',
            afterSave : '&onAfterSave'
        },
        link: function ($scope, element) {
            $scope.editable = false;

            var minYear = "";
            var utcDate = "";
            var maxYear = "";

            if (UtilService.isEmpty($scope.data)) {
                $scope.today = "";
                $scope.dateInPicker = new Date();
                minYear = $scope.dateInPicker.getFullYear() - 50;
                utcDate = moment.utc(UtilDateService.dateToIso($scope.dateInPicker)).format();
                maxYear = moment(utcDate).add(1, 'years').toDate().getFullYear();
            } else {
                $scope.dateInPicker = moment($scope.data).format(UtilDateService.defaultDateTimeFormat);
                minYear = $scope.data.getFullYear();
                utcDate = moment.utc(UtilDateService.dateToIso($scope.data)).format();
                maxYear = moment(utcDate).add(1, 'years').toDate().getFullYear();
            }

            $scope.toggleEditable = function () {
                $scope.editable = !$scope.editable;
            };

            var comboField = element[0].children[1].firstElementChild;
            $(comboField).combodate({
                format: 'MM/DD/YYYY HH:mm',
                template: 'MMM / DD / YYYY HH:mm',
                minuteStep: 1,
                minYear: minYear,
                maxYear: maxYear,
                smartDays: true,
                value: $scope.dateInPicker
            });

            $scope.saveDate = function () {
                $scope.toggleEditable();
                var editedDate = $(comboField).combodate('getValue', null);
                $scope.dateInPicker = moment(editedDate).format(UtilDateService.defaultDateTimeFormat);
                $scope.data = moment($scope.dateInPicker).toDate();
            };

            $scope.cancel = function () {
                $scope.toggleEditable();
                $(comboField).combodate('setValue', $scope.dateInPicker);
            };
        },
        controller: function ($scope) {
            $scope.$watch('data', function(){
                //called any time $scope.data changes
                if (UtilService.isEmpty($scope.data)) {
                    $scope.today = "";
                } else {
                    $scope.today = moment($scope.data).format(UtilDateService.defaultDateTimeFormat);
                }
            });
        }
    }
}]);
