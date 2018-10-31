angular.module('directives').directive('dateTimePicker', ['moment', 'Util.DateService', function(moment, UtilDateService){
    return{
        restrict: 'E',
        templateUrl: 'directives/date-time-picker/date-time-picker.client.directive.html',
        scope: {
            data: '=',
            property: '@',
            minDate: '='
        },
        link: function ($scope) {
            $scope.editable = false;
            var prop = $scope.property;
            var data = $scope.data;
            var datum = data[prop];
            $scope.today = moment(datum).format('MM/DD/YYYY HH:mm');
            var minYear = datum.getFullYear();
            var utcDate = moment.utc(UtilDateService.dateToIso(datum)).format();
            var maxYear = moment(utcDate).add(1, 'years').toDate().getFullYear();

            $('#comboDateInput').combodate({
                format: 'MM/DD/YYYY HH:mm',
                template: 'DD / MM / YYYY HH:mm',
                minuteStep: 1,
                minYear: minYear,
                maxYear: maxYear,
                value: datum
            });

            $scope.saveDate = function () {
                $scope.toggleEditable();
                var editedDate = $('#comboDateInput').combodate('getValue', null);
                $scope.today = moment(editedDate).format('MM/DD/YYYY HH:mm');
                $scope.data[prop] = moment($scope.today).toDate();
            };

            $scope.cancel = function () {
                $scope.toggleEditable();
                $('#comboDateInput').combodate('setValue', $scope.today);
            };

            $scope.toggleEditable = function () {
                $scope.editable = !$scope.editable;
            };
        }
    }
}]);