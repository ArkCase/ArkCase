'use strict';

//Setting up route
angular.module('dashboard').config(['$stateProvider', 'dashboardProvider',
    function ($stateProvider, dashboardProvider) {
        // TODO: Find way to init dashboardProvider by values from config.json
        dashboardProvider
            .structure("6-6", {
               rows: [{
                    columns: [{
                        styleClass: "col-md-6"
                    }, {
                        styleClass: "col-md-6"
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


        $stateProvider.
            state('dashboard', {
                url: '/dashboard',
                templateUrl: 'modules/dashboard/views/dashboard.client.view.html',
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('dashboard');
                        return $translate.refresh();
                    }]
                }
            });
    }
]);