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
                    return 'modules/admin/views/components/' + $stateParams.nodeName + '.client.view.html';
                }
            });
    }
]);