angular.module('audit').controller('Audit.DatepickersController', ['$scope', '$filter',
    function ($scope, $filter) {
        $scope.$on('component-config', applyConfig);
        $scope.$emit('req-component-config', 'datepickers');
        $scope.config = null;
        $scope.$on('fix-date-values', fixDateValues);

        $scope.dateFrom = new Date();
        $scope.dateTo = new Date();

        function fixDateValues(e, dateFrom, dateTo){
            var df = new Date(dateFrom);
            var dt = new Date(dateTo);
            $scope.dateFrom = df;
            $scope.dateTo = dt;
        }

        $scope.$watchGroup(['dateFrom','dateTo'], function(){
            $scope.$emit('send-date', $scope.dateFrom, $scope.dateTo);
        });

        function applyConfig(e, componentId, config) {
            if (componentId == 'datepickers') {
                $scope.config = config;
            }
        }

        $scope.opened = {};
        $scope.opened.openedStart = false;
        $scope.opened.openedEnd = false;

        $scope.open = function ($event, datepicker) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope.opened[datepicker] = true;
        };

    }
]);