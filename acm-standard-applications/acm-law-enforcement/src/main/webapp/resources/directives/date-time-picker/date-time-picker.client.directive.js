angular.module('directives').directive('dateTimePicker', ['moment', function(moment){
    return{
        restrict: 'E',
        templateUrl: 'directives/date-time-picker/date-time-picker.client.directive.html',
        scope: {
            date: '=',
            minDate: '='
        },
        controller: function ($scope) {
            $scope.editable = false;

            $scope.today = moment().format('MM/DD/YY HH:mm');
            // var minYear = $scope.minDate.getFullYear();
            // var minMount = $scope.date.getMonth();
            // var minDay = date.getDay();
            // var minHour = date.getHours();
            // var minMinute = date.getMinutes();

            angular.element(document.getElementById('comboDate')).combodate({
                minuteStep: 1,
                // minYear: minYear,
                value: $scope.today
            });

            angular.element(document.getElementById('datePick')).popover(
                {template: '<div class="popover" role="tooltip"><div class="arrow"></div><h3 class="popover-title"></h3><div class="popover-content"></div></div>'}
            );

            $scope.toggleEditable = function () {
                $scope.editable = !$scope.editable;

            }
        }
    }
}]);