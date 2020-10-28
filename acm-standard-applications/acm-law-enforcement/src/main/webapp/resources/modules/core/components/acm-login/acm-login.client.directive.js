'use strict';

/**
 * @ngdoc directive
 * @name global.core:acm-login
 * @restrict E
 *
 * @description
 *
 * {@link /acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/modules/core/components/acm-login/acm-login.client.directive.js core/components/acm-login/acm-login.client.directive.js}
 *
 * The "acm-login" directive monitors login information and user activity; and logout current session if idle time exceeds a limit
 *
 * @param {Number} limit Idle time limit to logout
 *
 * @example
 <example>
 <file name="index.html">
 <acmLogin limit="6000"/>
 </file>
 </example>
 */
angular.module('core').directive('acmLogin', [ '$stateParams', function($stateParams) {
    return {
        scope: {},
        bindToController: {}
        //, templateUrl: 'modules/core/components/acm-login/acm-login.client.view.html'
        ,
        template: '',
        controller: AcmLoginController,
        controllerAs: 'ctrl'
    }
} ]);
