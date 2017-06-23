'use strict';

angular.module('dashboard.tags', ['adf.provider'])
    .config(function (dashboardProvider) {
        dashboardProvider
            .widget('tags', {
                    title: 'Tags',
                    description: 'Displays Tags',
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