'use strict';

angular.module('admin').controller('Admin.ComponentsListController', ['$scope', '$q', '$state', '$stateParams', 'AdministrationService', 'SchemasService',
    function ($scope, $q, $state, $stateParams, AdministrationService, SchemasService) {
        $scope.schemas = {};
        $scope.components = [];
        $scope.formData = ["*", { "type": "submit", "title": "Save"}];

        $scope.selectComponent = selectComponent;
        $scope.submitForm = submitForm;
        $scope.toggleComponent = toggleComponent;
        $scope.enableComponent = enableComponent;

        $scope.$on('show-components', showComponents);


        function submitForm() {
            $scope.$emit('req-save-module');
        }

        /**
         * Toggle component panel on header click
         * @param component
         */
        function toggleComponent($event, component) {
            $event.preventDefault();
            _.forEach($scope.components, function(compIter){
                if (compIter.id != component.id) {
                    compIter.isCollapsed = true
                }
            });
            component.isCollapsed = !component.isCollapsed;
        }

        function enableComponent($event, component, enable) {
            $event.preventDefault();
            component.enabled = enable;
            $scope.$emit('req-save-module');
        }


        function showComponents(e, components) {
            // Get form schemas

            var requiredSchemas = _.pluck(components, 'type');
            requiredSchemas = _.uniq(requiredSchemas);
            var promises = [];
            _.forEach(requiredSchemas, function(schema){
                promises.push(

                    //FIXME there are no schemas so we have all the time some error message, when schemas being used than enable code below
                    //SchemasService.getSchema({schemaId: schema}).$promise.then(function(schemaData){
                    //    return schemaData;
                    //})
                );
            });

            $q.all(promises).then(function(schemasDefs){
                for(var i = 0; i < requiredSchemas.length; i++) {
                    $scope.schemas[requiredSchemas[i]] = schemasDefs[i];
                }
            });

            // Add collapsed property
            _.forEach(components, function(component){
                component.isCollapsed = true;
            });

            $scope.components = components;
        }

        function selectComponent(newActive){
            var prevActive = _.find($scope.components, {active: true});
            if (prevActive) {
                prevActive.active = false;
            }
            newActive.active = true;
            $scope.$emit('req-component-selected', newActive);
        }
    }
]);