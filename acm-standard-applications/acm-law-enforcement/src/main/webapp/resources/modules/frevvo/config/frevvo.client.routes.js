'use strict';

angular.module('frevvo').config(['$stateProvider',
    function ($stateProvider) {
        $stateProvider.state('frevvo', {
            url: '/frevvo',
            templateUrl: 'modules/frevvo/views/frevvo.client.view.html',
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('common');
                    $translatePartialLoader.addPart('frevvo');
                    return $translate.refresh();
                }]
            }
        })
            .state('frevvo.new-case', {
                url: '/new-case',
                params: {name: 'new-case'}
            })
            .state('frevvo.edit-case', {
                url: '/edit-case/:caseId/:caseNumber/:containerId/:folderId',
                params: {name: 'edit-case', mode: 'edit'}
            })
            .state('frevvo.change-case-status', {
                url: '/change-case-status/:caseId/:caseNumber/:status',
                params: {name: 'change-case-status'}
            })
            .state('frevvo.reinvestigate', {
                url: '/reinvestigate/:caseId/:caseNumber/:containerId/:folderId',
                params: {name: 'reinvestigate', mode: 'reinvestigate'}
            })
            .state('frevvo.new-complaint', {
                url: '/new-complaint',
                params: {name: 'new-complaint'}
            })
            .state('frevvo.close-complaint', {
                url: '/close-complaint/:complaintId/:complaintNumber',
                params: {name: 'close-complaint', mode: 'create'}
            })
            .state('frevvo.new-costsheet', {
                url: '/new-costsheet',
                params: {name: 'new-costsheet'}
            })
            .state('frevvo.new-costsheet-from-object', {
            	url: '/new-costsheet/:type/:objectId',
                params: {name: 'new-costsheet'}
            })
            .state('frevvo.edit-costsheet', {
                url: '/edit-costsheet/:id',
                params: {name: 'edit-costsheet'}
            })
            .state('frevvo.new-timesheet', {
                url: '/new-timesheet',
                params: {name: 'new-timesheet'}
            })
             .state('frevvo.new-timesheet-from-object', {
            	url: '/new-timesheet/:_type/:_id/:_number',
                params: {name: 'new-timesheet'}
            })
            .state('frevvo.edit-timesheet', {
                url: '/edit-timesheet/:period',
                params: {name: 'edit-timesheet'}
            })
            .state('frevvo.edit-plainform', {
                url: '/edit-plainform/:formKey/:formTarget',
                params: {name: 'edit-plainform', mode: 'edit'}
            })
            .state('frevvo.new-plainform', {
                url: '/new-plainform/:target',
                params: {name: 'new-plainform'}
            });
    }
]);
