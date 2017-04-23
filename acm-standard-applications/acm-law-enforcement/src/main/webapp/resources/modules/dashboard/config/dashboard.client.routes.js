'use strict';

//Setting up route
angular.module('dashboard').config(['$stateProvider', 'ArkCaseDashboardProvider',
    function ($stateProvider, ArkCaseDashboardProvider) {
        // TODO: Find way to init dashboardProvider by values from config.json
        ArkCaseDashboardProvider
            .structure("6-6", {
                rows: [{
                    columns: [{
                        styleClass: "col-md-6"
                    }, {
                        styleClass: "col-md-6"
                    }]
                }]
            })
            .structure("12", {
                rows: [{
                    columns: [{
                        styleClass: "col-md-12"
                    }]
                }]
            })
            .structure("4-4-4", {
                rows: [{
                    columns: [{
                        styleClass: "col-md-4"
                    }, {
                        styleClass: "col-md-4"
                    }, {
                        styleClass: "col-md-4"
                    }]
                }]
            })
            .structure("4-8", {
                rows: [{
                    columns: [{
                        styleClass: "col-md-4",
                        widgets: []
                    }, {
                        styleClass: "col-md-8",
                        widgets: []
                    }]
                }]
            })
            .structure("8-4", {
                rows: [{
                    columns: [{
                        styleClass: "col-md-8",
                        widgets: []
                    }, {
                        styleClass: "col-md-4",
                        widgets: []
                    }]
                }]
            });

        ArkCaseDashboardProvider.addLocale('de-DE',
            {
                ADF_COMMON_CLOSE: 'Schließen',
                ADF_COMMON_DELETE: 'Löschen',
                ADF_COMMON_TITLE: 'Title',
                ADF_COMMON_CANCEL: 'Cancel',
                ADF_COMMON_APPLY: 'Apply',
                ADF_COMMON_EDIT_DASHBOARD: 'Edit dashboard',
                ADF_EDIT_DASHBOARD_STRUCTURE_LABEL: 'Structure',
                ADF_DASHBOARD_TITLE_TOOLTIP_ADD: 'Add new widget',
                ADF_DASHBOARD_TITLE_TOOLTIP_SAVE: 'Save changes',
                ADF_DASHBOARD_TITLE_TOOLTIP_EDIT_MODE: 'Enable edit mode',
                ADF_DASHBOARD_TITLE_TOOLTIP_UNDO: 'Undo changes',
                ADF_WIDGET_ADD_HEADER: 'Add new widget',
                ADF_WIDGET_DELETE_CONFIRM_MESSAGE: 'Are you sure you want to delete this widget ?',
                ADF_WIDGET_TOOLTIP_REFRESH: 'Reload Widget Content',
                ADF_WIDGET_TOOLTIP_MOVE: 'Change widget location',
                ADF_WIDGET_TOOLTIP_COLLAPSE: 'Collapse widget',
                ADF_WIDGET_TOOLTIP_EXPAND: 'Expand widget',
                ADF_WIDGET_TOOLTIP_EDIT: 'Edit widget configuration',
                ADF_WIDGET_TOOLTIP_FULLSCREEN: 'Fullscreen widget',
                ADF_WIDGET_TOOLTIP_REMOVE: 'Remove widget'
            }
        );
        //ArkCaseDashboardProvider.setLocale('sv-SE');


        $stateProvider.state('dashboard', {
            url: '/dashboard',
            templateUrl: 'modules/dashboard/views/dashboard.client.view.html',
            controller: 'DashboardController',
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('common');
                    $translatePartialLoader.addPart('dashboard');
                    return $translate.refresh();
                }]
            }
        });
    }
]);