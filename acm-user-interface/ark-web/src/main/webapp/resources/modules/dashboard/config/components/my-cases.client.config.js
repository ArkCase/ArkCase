'use strict';

/**
 * @ngdoc controller
 * @name dashboard.my-cases.controller:Dashboard.MyCasesController
 *
 * @description
 *
 * {@link https://github.com/Armedia/ACM3/blob/develop/acm-user-interface/ark-web/src/main/webapp/resources/modules/dashboard/controllers/components/my-cases.client.controller.js modules/dashboard/controllers/components/my-cases.client.controller.js}
 *
 * Loads cases in the "My Cases" widget.
 */
angular.module('dashboard.my-cases', ['adf.provider'])
    .config(function (dashboardProvider) {
        dashboardProvider
            .widget('myCases', {
                title: 'My Cases',
                description: 'Displays my cases',
                controller: 'Dashboard.MyCasesController',
                controllerAs: 'myCases',
                reload: true,
                templateUrl: 'modules/dashboard/views/components/my-cases.client.view.html'
            });
    });