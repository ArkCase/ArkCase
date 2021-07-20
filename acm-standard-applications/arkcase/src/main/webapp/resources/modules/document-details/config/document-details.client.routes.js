'use strict';

angular.module('services').config([ '$provide', function($provide) {
    $provide.decorator('$state', [ '$delegate', '$window', function($delegate, $window) {
        var origGo = $delegate.go;
        var extended = {
            go: function(stateName, params, newTab) {
                if (newTab === true) {
                    $window.open($delegate.href(stateName, params, {
                        absolute: true
                    }), '_blank');
                } else {
                    origGo(stateName, params);
                }
            }
        };

        angular.extend($delegate, extended);
        return $delegate;
    } ]);
} ]);


angular.module('document-details').config([ '$stateProvider', function($stateProvider) {
    $stateProvider.state('viewer', {
        url: '/viewer/:id/:containerId/:containerType/:name/:selectedIds/:documentStatus',
        templateUrl: 'modules/document-details/views/document-details.client.view.html',
        resolve: {
            translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                $translatePartialLoader.addPart('document-details');
                $translatePartialLoader.addPart('common');
                $translatePartialLoader.addPart('tasks');
                $translatePartialLoader.addPart('preference');
                $translatePartialLoader.addPart('cases');
                $translatePartialLoader.addPart('complaints');
                $translatePartialLoader.addPart('progress-bar');
                return $translate.refresh();
            } ]
        },
        target: "_blank"
    }).state('viewer.media', {
        url: '/:seconds',
        templateUrl: 'modules/document-details/views/document-details.client.view.html'
    });
} ]);