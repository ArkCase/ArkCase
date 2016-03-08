'use-strict';

angular.module('audit').controller('Audit.SelectionController', ['$scope',
    function ($scope) {
        $scope.$on('component-config', applyConfig);
        $scope.$emit('req-component-config', 'auditselection');
        $scope.config = null;

        $scope.$watchGroup(['selectId','auditDropdown'], function(){
            $scope.$emit('send-type-id', $scope.auditDropdown, $scope.selectId);
        });

        $scope.auditDropdown = "";

        function applyConfig(e, componentId, config) {
            if (componentId == 'auditselection') {
                $scope.config = config;
            }
        }
    }
]);