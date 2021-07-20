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

//Setting up route
angular.module('request-info').config([ '$stateProvider', function($stateProvider) {
    // request info state routing
    $stateProvider.state('request-info', {
        url: '/request-info/:id/:fileId',
        templateUrl: 'modules/request-info/views/request-info.client.view.html',
        resolve: {
            translatePartialLoader: [ '$translate', '$translatePartialLoader', function($translate, $translatePartialLoader) {
                $translatePartialLoader.addPart('common');
                $translatePartialLoader.addPart('core');
                $translatePartialLoader.addPart('request-info');
                $translatePartialLoader.addPart('cases');
                $translatePartialLoader.addPart('document-details');
                $translatePartialLoader.addPart('tasks');
                $translatePartialLoader.addPart('progress-bar');
                return $translate.refresh();
            } ]
        },
        target: "_blank"
    }).state('request-info.tasks', {
        url: '',
        templateUrl: 'modules/request-info/views/request-info.client.view.html',
        resolve: {
            translatePartialLoader: [ '$translate', '$translatePartialLoader', function($translate, $translatePartialLoader) {
                $translatePartialLoader.addPart('common');
                $translatePartialLoader.addPart('request-info');
                return $translate.refresh();
            } ]
        }
    }).state('request-viewer', {
        url: '/viewer/:fileId/:id/:containerType/:name/:selectedIds',
        templateUrl: 'modules/request-info/views/request-info.client.view.html',
        resolve: {
            translatePartialLoader: [ '$translate', '$translatePartialLoader', function($translate, $translatePartialLoader) {
                $translatePartialLoader.addPart('request-info');
                $translatePartialLoader.addPart('common');
                $translatePartialLoader.addPart('document-details');
                return $translate.refresh();
            } ]
        }
    });

} ]);