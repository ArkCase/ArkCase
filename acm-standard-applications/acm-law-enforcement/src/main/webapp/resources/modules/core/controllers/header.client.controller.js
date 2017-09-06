'use strict';

angular.module('core').controller('HeaderController', ['$scope', '$q', '$state', 'Acm.StoreService', 'UtilService'
    , 'Authentication', 'Menus', 'ServCommService', 'Search.AutoSuggestService', 'Config.LocaleService', 'ConfigService'
    , function ($scope, $q, $state, Store, Util
        , Authentication, Menus, ServCommService, AutoSuggestService, LocaleService, ConfigService) {
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
        function applyConfig(e, componentId, config) {
            if (componentId == 'header') {
                $scope.config = config;
                $scope.start = config.searchParams.start;
                $scope.count = config.searchParams.n;
                $scope.typeAheadColumn = config.typeAheadColumn;
            }
        }

        // ConfigService.getComponentConfig("core", "header").then(function (config) {
        //     $scope.config = config;
        //     $scope.start = config.searchParams.start;
        //     $scope.count = config.searchParams.n;
        //     $scope.typeAheadColumn = config.typeAheadColumn;
        // });

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
        };

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
            $state.go('search', {
                query: $scope.data.inputQuery
            });
        };

        $scope.keyDown = function (event) {
            if (event.keyCode == 13) {
                $scope.isSelected = isSelected;
                $state.go('search', {
                    query: $scope.data.inputQuery,
                    isSelected: $scope.isSelected
                });
            }
            isSelected = false;
        };


        var localeData = LocaleService.getLocaleData();
        var locales = Util.goodMapValue(localeData, "locales", LocaleService.DEFAULT_LOCALES);
        var localeCode = Util.goodMapValue(localeData, "code", LocaleService.DEFAULT_CODE);
        $scope.localeDropdownOptions = locales;
        $scope.localeSelected = _.find(locales, {code: localeCode});
        LocaleService.useLocale($scope.localeSelected.code);

        $scope.changeLocale = function ($event, localeNew) {
            $event.preventDefault();
            $scope.localeSelected = localeNew;
            var localeData = LocaleService.getLocaleData();
            localeData.code = localeNew.code;
            localeData.iso = localeNew.iso;
            LocaleService.setLocaleData(localeData);
            LocaleService.useLocale(localeNew.code);
        };

        $scope.updateLocales = function($event) {
            $event.preventDefault();
            LocaleService.getSettings().then(function(data){
                $scope.localeDropdownOptions = Util.goodMapValue(data, "locales", LocaleService.DEFAULT_LOCALES);
                var localeData = LocaleService.getLocaleData();
                localeData.locales = $scope.localeDropdownOptions;
                LocaleService.setLocaleData(localeData);
                return data;
            });
        }
    }
]);