'use strict';

/**
 * @ngdoc directive
 * @name global.core:acm-idle-logout
 * @restrict E
 *
 * @description
 *
 * {@link https://***REMOVED***/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/modules/core/components/acm-idle-logout/acm-idle-logout.client.directive.js core/components/acm-idle-logout/acm-idle-logout.client.directive.js}
 *
 * The "acm-idle-logout" directive monitors user activity and logout current session if idle time exceeds a limit
 *
 * @param {Number} limit Idle time limit to logout
 *
 * @example
 <example>
 <file name="index.html">
 <acmIdleLogout limit="6000"/>
 </file>
 </example>
 */
angular.module('core').directive('acmIdleLogout', ['$stateParams', '$q', '$translate', '$modal'
    , 'Acm.StoreService', 'UtilService', 'ConfigService'
    , function ($stateParams, $q, $translate, $modal
        , Store, Util, ConfigService
    ) {
        return {
            scope: {}
            , bindToController: {
                idleLimit: '@'
            }
            //, templateUrl: 'modules/core/components/acm-idle-logout/acm-idle-logout.client.view.html'
            , template: ''
            , controller: AcmIdleLogoutController
            , controllerAs: 'ctrl'
        }
    }
]);

