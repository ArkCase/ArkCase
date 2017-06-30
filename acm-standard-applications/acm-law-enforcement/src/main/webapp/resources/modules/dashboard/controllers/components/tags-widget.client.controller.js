'use strict';

angular.module('dashboard.tags', ['adf.provider'])
    .config(function (dashboardProvider) {
        dashboardProvider
            .widget('tags', {
                    title: 'dashboard.widgets.tags.title',
                    description: 'dashboard.widgets.tags.description',
                    controller: 'Dashboard.TagsController',
                    reload: true,
                    templateUrl: 'modules/dashboard/views/components/tags-widget.client.view.html',
                    commonName: 'tags'
                }
            );
    })
    .controller('Dashboard.TagsController', ['$scope', '$stateParams'
        , function ($scope, $stateParams) {

        }
    ]);