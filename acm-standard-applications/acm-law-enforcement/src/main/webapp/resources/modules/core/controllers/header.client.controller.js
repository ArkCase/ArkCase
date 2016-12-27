'use strict';

angular.module('core').controller('HeaderController', ['$scope', 'Authentication', 'Menus', '$q', '$state', 'ServCommService', 'Search.AutoSuggestService'
    , function ($scope, Authentication, Menus, $q, $state, ServCommService, AutoSuggestService) {
        $scope.$emit('req-component-config', 'header');
        $scope.authentication = Authentication;
        $scope.isCollapsed = false;
        $scope.menu = Menus.getMenu('topbar');

        var config = null;

        $scope.config = null;
        $scope.start = '';
        $scope.count = '';
        $scope.inputQuery = '';
        $scope.data = {};
        $scope.data.inputQuery = '';
        $scope.$on('component-config', applyConfig);
        // $scope.$emit('req-component-config', 'header');
        function applyConfig(e, componentId, config) {
            if (componentId == 'header') {
                $scope.config = config;
                $scope.start = config.searchParams.start;
                $scope.count = config.searchParams.n;
                $scope.typeAheadColumn = config.typeAheadColumn;
            }
        };

        ServCommService.handleRequest();

        $scope.queryTypeahead = function (typeaheadQuery) {
            var deferred = $q.defer();
            var typeAheadColumn = "title_parseable";
            if ($scope.typeAheadColumn) {
                typeAheadColumn = $scope.typeAheadColumn;
            }
            if (typeaheadQuery.length >= 2) {
                AutoSuggestService.autoSuggest(typeaheadQuery, "QUICK", null).then(function (res) {
                    var results = _.pluck(res, typeAheadColumn);
                    deferred.resolve(results);
                });
                return deferred.promise;
            }
        }

        var isSelected = false;
        $scope.onSelect = function ($item, $model, $label) {
            isSelected = true;
        };


        $scope.toggleCollapsibleMenu = function () {
            $scope.isCollapsed = !$scope.isCollapsed;
        };

        // Collapsing the menu after navigation
        $scope.$on('$stateChangeSuccess', function () {
            $scope.isCollapsed = false;
        });

        $scope.search = function () {
            $state.go('quick-search', {
                query: $scope.data.inputQuery
            });
        };

        $scope.keyDown = function (event) {
            if (event.keyCode == 13) {
                $scope.isSelected = isSelected;
                $state.go('quick-search', {
                    query: $scope.data.inputQuery,
                    isSelected: $scope.isSelected
                });
            }
            isSelected = false;
        };
    }
]);