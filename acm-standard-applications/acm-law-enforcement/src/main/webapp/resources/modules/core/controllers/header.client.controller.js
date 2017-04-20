'use strict';

angular.module('core').controller('HeaderController', ['$scope', '$q', '$state', '$translate'
    , 'UtilService', 'Acm.StoreService', 'Authentication', 'Menus', 'ServCommService', 'Search.AutoSuggestService'
    , 'Config.LocaleService'
    , function ($scope, $q, $state, $translate
        , Util, Store, Authentication, Menus, ServCommService, AutoSuggestService
        , LocaleService, LabelsConfigService) {
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


        var cacheLocale = new Store.LocalData({name: "AcmLocale", noOwner: true, noRegistry: true});
        var lastLocale = cacheLocale.get();
        if (Util.isEmpty(lastLocale)) {
            var defaultSettings = LocaleService.DEFAULT_SETTINGS;
            lastLocale = {};
            lastLocale.locales = defaultSettings.locales;
            lastLocale.selected = defaultSettings.defaultLocale;
            cacheLocale.set(lastLocale);
        }
        var locales = Util.goodMapValue(lastLocale, "locales", []);
        var localeCode = Util.goodMapValue(lastLocale, "selected", LocaleService.DEFAULT_SETTINGS.defaultLocale);
        $scope.localeDropdownOptions = locales;
        $scope.localeSelected = _.find(locales, {locale: localeCode});

        $translate.use($scope.localeSelected.locale);

        $scope.changeLocale = function ($event, localeNew) {
            $event.preventDefault();
            $scope.localeSelected = localeNew;
            var lastLocale = cacheLocale.get();
            lastLocale.selected = localeNew.locale;
            cacheLocale.set(lastLocale);
            $translate.use(localeNew.locale);
        };

        $scope.updateLocales = function($event) {
            $event.preventDefault();
            LocaleService.getSettings().then(function(data){
                $scope.localeDropdownOptions = Util.goodMapValue(data, "locales", []);

                lastLocale = cacheLocale.get();
                lastLocale.locales = $scope.localeDropdownOptions;
                cacheLocale.set(lastLocale);
                return data;
            });
        }
    }
]);