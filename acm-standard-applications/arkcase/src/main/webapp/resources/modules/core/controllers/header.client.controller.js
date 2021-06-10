'use strict';

angular.module('core').controller(
        'HeaderController',
        [ '$scope', '$q', '$state', '$translate', 'UtilService', 'Acm.StoreService', 'Authentication', 'Menus', 'ServCommService', 'Search.AutoSuggestService', 'Config.LocaleService', 'ConfigService', 'Profile.UserInfoService', 'MessageService', 'ModalDialogService', 'i18nService',
                function($scope, $q, $state, $translate, Util, Store, Authentication, Menus, ServCommService, AutoSuggestService, LocaleService, ConfigService, UserInfoService, MessageService, ModalDialogService, i18nService) {

                    $scope.authentication = Authentication;
                    $scope.isCollapsed = false;
                    $scope.menu = Menus.getMenu('topbar');

                    $scope.config = null;
                    $scope.start = '';
                    $scope.count = '';
                    $scope.inputQuery = '';
                    $scope.data = {};
                    $scope.data.inputQuery = '';

                    ConfigService.getComponentConfig('core', 'header').then(function(config) {
                        $scope.config = config;
                        $scope.start = Util.goodMapValue(config, 'searchProperties.start', 0);
                        $scope.count = Util.goodMapValue(config, 'searchProperties.n', 10);
                        $scope.typeAheadColumn = config.typeAheadColumn;
                    });

                    ServCommService.handleRequest();

                    $scope.queryTypeahead = function(typeaheadQuery) {
                        var deferred = $q.defer();
                        var typeAheadColumn = "title_parseable";
                        if ($scope.typeAheadColumn) {
                            typeAheadColumn = $scope.typeAheadColumn;
                        }
                        if (typeaheadQuery.length >= 2) {
                            AutoSuggestService.autoSuggest(typeaheadQuery, 'ADVANCED', null).then(function(res) {
                                var results = _.pluck(res, typeAheadColumn);
                                deferred.resolve(results);
                            });
                            return deferred.promise;
                        }
                    };

                    var isSelected = false;
                    $scope.onSelect = function($item, $model, $label) {
                        isSelected = true;
                    };

                    $scope.toggleCollapsibleMenu = function() {
                        $scope.isCollapsed = !$scope.isCollapsed;
                    };

                    // Collapsing the menu after navigation
                    $scope.$on('$stateChangeSuccess', function() {
                        $scope.isCollapsed = false;
                    });

                    $scope.search = function() {
                        $state.go('search', {
                            query: $scope.data.inputQuery
                        });
                    };

                    $scope.keyDown = function(event) {
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
                    $q.all([ Authentication.queryUserInfo(), LocaleService.getSettings() ]).then(function(result) {
                        var userInfo = result[0];
                        var localeData = result[1];
                        $scope.localeDropdownOptions = Util.goodMapValue(localeData, 'locales', LocaleService.DEFAULT_LOCALES);
                        $scope.localeSelected = LocaleService.requestLocale(userInfo.langCode);
                        LocaleService.useLocale($scope.localeSelected.code);
                        i18nService.setCurrentLang($scope.localeSelected.code);
                    });

                    $scope.changeLocale = function($event, localeNew) {
                        $event.preventDefault();

                        $scope.localeSelected = LocaleService.requestLocale(localeNew.code);
                        LocaleService.useLocale(localeNew.code);
                        i18nService.setCurrentLang(localeNew.code);// set the current language in the ui-grid footer

                        Authentication.updateUserLang(localeNew.code).then(function() {
                        }, function(error) {
                            MessageService.error(error.data ? error.data : error);
                            return error;
                        });
                    };

                    // TODO delete UPDATE button and this function if not needed
                    $scope.updateLocales = function($event) {
                        $event.preventDefault();

                        LocaleService.getLatestSettings().then(function(data) {
                            $scope.localeDropdownOptions = Util.goodMapValue(data, 'locales', LocaleService.DEFAULT_LOCALES);
                            return data;
                        });
                    };

                    $scope.onCreateNew = function(event, item) {
                        event.preventDefault();
                        if (!item.modalDialog) {
                            $state.go(item.link, {}, {
                                reload: true
                            });
                        } else {
                            ModalDialogService.showModal(item.modalDialog);
                        }
                    };
                } ]);