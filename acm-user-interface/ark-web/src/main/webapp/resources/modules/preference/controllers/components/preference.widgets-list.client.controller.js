'use strict';

angular.module('preference').controller('Preference.WidgetsListController', ['$scope', '$q', '$state', '$stateParams',
    function ($scope, $q, $state, $stateParams) {
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
