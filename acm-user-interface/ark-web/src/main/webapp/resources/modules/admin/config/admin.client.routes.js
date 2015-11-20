'use strict';

angular.module('admin').config(['$stateProvider',
    function ($stateProvider) {
        $stateProvider.
            state('admin', {
                url: '/admin',
                templateUrl: 'modules/admin/views/admin.client.view.html',
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('admin');
                        $translatePartialLoader.addPart('common');
                        return $translate.refresh();
                    }]
                }
            }).state('admin.view-node', {
                url: '/view-node/:nodeName',
                templateUrl: function ($stateParams) {
                    return 'modules/admin/views/components/' + $stateParams.nodeName + '.view.html';
                }
            })

        .state('editplainform', {
            url: '/editplainform',
            templateUrl: 'modules/admin/views/components/edit-plain-form.client.view.html',
            params: {
                key: null,
                target: null
            }
        })

        .state('newplainform', {
            url: '/newplainform',
            templateUrl: 'modules/admin/views/components/new-plain-form.client.view.html',
            params: {
                target: null
            }
        });
    }
]);