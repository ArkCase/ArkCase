'use strict';

angular.module('preference').controller('Preference.WidgetsListController', ['$scope', '$q', '$state', '$stateParams', 'AdministrationService', 'SchemasService',
    function ($scope, $q, $state, $stateParams, AdministrationService, SchemasService) {
        $scope.schemas = {};
        $scope.widgets = [];

        $scope.formData = ["*", {"type": "submit", "title": "Save"}];

        $scope.selectWidget = selectWidget;
        $scope.submitForm = submitForm;
        $scope.toggleWidget = toggleWidget;
        $scope.enableWidget = enableWidget;

        $scope.showDefaultForm = false;
        $scope.toggleDefaultView = toggleDefaultView;
        $scope.defaultViewExpand = "";

        $scope.$on('show-widgets', showWidgets);

        function toggleDefaultView($event) {
            //          $event.preventDefault();
            console.log("Default view toggled");
        }

        function submitForm() {
            $scope.$emit('req-save-module');
        }

        /**
         * Toggle widget panel on header click
         * @param widget
         */
        function toggleWidget($event, widget) {
            $event.preventDefault();
            _.forEach($scope.widgets, function (compIter) {
                if (compIter.id != widget.id) {
                    compIter.isCollapsed = true
                }
            });
            widget.isCollapsed = !widget.isCollapsed;
        }

        function enableWidget($event, widget, enable) {
            // $event.preventDefault();
            widget.enabled = enable;
            // $scope.$emit('req-save-module');

            /**
             * Save Widget
             */
            if (enable) {
                console.log("Widget: " + widget.title + " enabled");
            } else {
                console.log("Widget: " + widget.title + " disabled");
            }
        }


        function showWidgets(e, widgets) {
            // Get form schemas

            var requiredSchemas = _.pluck(widgets, 'type');
            requiredSchemas = _.uniq(requiredSchemas);
            var promises = [];
            _.forEach(requiredSchemas, function (schema) {
                promises.push(
                    //FIXME there are no schemas so we have all the time some error message, when schemas being used than enable code below
                    //SchemasService.getSchema({schemaId: schema}).$promise.then(function(schemaData){
                    //    return schemaData;
                    //})
                );
            });

            $q.all(promises).then(function (schemasDefs) {
                for (var i = 0; i < requiredSchemas.length; i++) {
                    $scope.schemas[requiredSchemas[i]] = schemasDefs[i];
                }
            });

            // Add collapsed property
            _.forEach(widgets, function (widget) {
                widget.isCollapsed = true;
            });

            $scope.widgets = widgets;
            $scope.showDefaultForm = true;
        }

        function selectWidget(newActive) {
            var prevActive = _.find($scope.widgets, {active: true});
            if (prevActive) {
                prevActive.active = false;
            }
            newActive.active = true;
            $scope.$emit('req-widget-selected', newActive);
        }
    }
]);
