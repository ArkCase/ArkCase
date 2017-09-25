'use strict';

angular.module('core').controller('HeaderController', ['$scope', '$q', '$state', '$translate'
    , 'UtilService', 'Acm.StoreService', 'Authentication', 'Menus', 'ServCommService', 'Search.AutoSuggestService'
    , 'Config.LocaleService', 'ConfigService', 'Profile.UserInfoService', 'MessageService'
    , function ($scope, $q, $state, $translate
        , Util, Store, Authentication, Menus, ServCommService, AutoSuggestService
        , LocaleService, ConfigService, UserInfoService, MessageService) {
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

        // set application language for the user
        var localeSettingsPromise = LocaleService.getSettings()
        var userInfoPromise = Authentication.queryUserInfo();
        
        $q.all([localeSettingsPromise, userInfoPromise]).then(function(result) {
            var userInfo = result[1];

            var userLocale = _.findWhere(result[0].locales, {code: userInfo.langCode});

            $scope.localeDropdownOptions = Util.goodMapValue(result[0], "locales", LocaleService.DEFAULT_LOCALES);;
            $scope.localeSelected = userLocale;

            LocaleService.useLocale($scope.localeSelected.code);
        });

        $scope.changeLocale = function ($event, localeNew) {
            $event.preventDefault();
            userInfoPromise.then(function (userInfo) {                
                Authentication.updateUserLang(localeNew.code).then(function () {
                    userInfo.langCode = localeNew.code;
                    $scope.localeSelected = localeNew;
                    LocaleService.setLocaleData(localeData);
                    LocaleService.useLocale(localeNew.code);
                }
                , function (error) {
                    MessageService.error(error.data ? error.data : error);
                    return error;
                });
            });
        };

        // TODO delete UPDATE button and this function if not needed
        $scope.updateLocales = function($event) {
            $event.preventDefault();
            localeSettingsPromise.then(function(data) {
                $scope.localeDropdownOptions = Util.goodMapValue(data, "locales", LocaleService.DEFAULT_LOCALES);
                return data;
            });
        }
    }
]);