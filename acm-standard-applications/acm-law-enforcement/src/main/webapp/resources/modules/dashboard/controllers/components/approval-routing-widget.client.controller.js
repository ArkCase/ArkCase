'use strict';

angular.module('dashboard.approvalrouting', ['adf.provider'])
    .config(function (dashboardProvider) {
        dashboardProvider
            .widget('organizations', {
                    title: 'dashboard.widgets.approvalRouting.title',
                    description: 'dashboard.widgets.approvalRouting.description',
                    controller: 'Dashboard.ApprovalRoutingController',
                    reload: true,
                    templateUrl: 'modules/dashboard/views/components/approval-routing-widget.client.view.html',
                    commonName: 'approvalrouting'
                }
            );
    })
    .controller('Dashboard.ApprovalRoutingController', ['$scope', '$stateParams'
        , function ($scope, $stateParams) {

        }
    ]);