angular.module('directives').directive('dateTimePicker', ['moment', 'Util.DateService', function(moment, UtilDateService){
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
            $scope.today = moment($scope.data).format('MM/DD/YYYY HH:mm');
            var minYear = $scope.data.getFullYear();
            var utcDate = moment.utc(UtilDateService.dateToIso($scope.data)).format();
            var maxYear = moment(utcDate).add(1, 'years').toDate().getFullYear();

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
                value: $scope.data
            });

            $scope.saveDate = function () {
                $scope.toggleEditable();
                var editedDate = $(comboField).combodate('getValue', null);
                $scope.today = moment(editedDate).format('MM/DD/YYYY HH:mm');
                $scope.data = moment($scope.today).toDate();
            };

            $scope.cancel = function () {
                $scope.toggleEditable();
                $(comboField).combodate('setValue', $scope.today);
            };
        },
        controller: function ($scope) {
            $scope.$watch('data', function(){
                //called any time $scope.data changes
                $scope.today = moment($scope.data).format('MM/DD/YYYY HH:mm');
            });
        }
    }
}]);