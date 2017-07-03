'use strict';

angular.module('dashboard.parentDocs', ['adf.provider'])
    .config(function (dashboardProvider) {
        dashboardProvider
            .widget('parentDocs', {
                title: 'dashboard.widgets.parentDocs.title',
                description: 'dashboard.widgets.parentDocs.description',
                controller: 'Dashboard.ParentDocumentsController',
                reload: true,
                templateUrl: 'modules/dashboard/views/components/parent-documents-widget.client.view.html',
                commonName: 'parentDocs'
            });
    })
    .controller('Dashboard.ParentDocumentsController', ['$scope', '$stateParams'
        ,function ($scope, $stateParams) {

        }
    ]);