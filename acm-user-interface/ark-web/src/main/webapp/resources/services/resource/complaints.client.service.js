'use strict';

/**
 * @ngdoc service
 * @name services.service:ComplaintsService
 *
 * @description
 *
 * {@link https://github.com/Armedia/ACM3/blob/develop/acm-user-interface/ark-web/src/main/webapp/resources/services/resource/complaints.client.service.js services/resource/complaints.client.service.js}

 * CasesService includes group of REST calls related to Cases module. Functions are implemented using $resource.
 */
angular.module('services').factory('ComplaintsService', ['$resource',
	function($resource) {
		return $resource('proxy/arkcase/api/latest/plugin',{
		},{
			/**
			 * @ngdoc method
			 * @name get
			 * @methodOf services.service:ComplaintsService
			 *
			 * @description
			 * Query complaint data
			 *
			 * @param {Object} params Map of input parameter.
			 * @param {Number} params.id  Complaint ID
			 * @param {Function} onSuccess (Optional)Callback function of success query.
			 * @param {Function} onError (Optional) Callback function when fail.
			 *
			 * @returns {Object} Object returned by $resource
			 */
			get: {
				method: 'GET',
				cache: true,
				url: 'proxy/arkcase/api/latest/plugin/complaint/byId/:id',
				isArray: false
			}

			/**
			 * @ngdoc method
			 * @name queryComplaints
			 * @methodOf services.service:ComplaintsService
			 *
			 * @description
			 * Save complaint data
			 *
			 * @param {Object} params Map of input parameter.
			 * @param {Number} params.id  Complaint ID
			 * @param {Function} onSuccess (Optional)Callback function of success query.
			 * @param {Function} onError (Optional) Callback function when fail.
			 *
			 * @returns {Object} Object returned by $resource
			 */
			, save: {
				method: 'POST',
				url: 'proxy/arkcase/api/latest/plugin/complaint',
				cache: false
			}

			/**
			 * @ngdoc method
			 * @name queryComplaints
			 * @methodOf services.service:ComplaintsService
			 *
			 * @description
			 * Query list of complaints from SOLR.
			 *
			 * @param {Object} params Map of input parameter.
			 * @param {Number} params.start  zero based index of result starts from
			 * @param {Number} params.n max number of list to return
			 * @param {String} params.sort  sort value. Allowed choice is based on backend specification
			 * @param {String} params.filters  filter value. Allowed choice is based on backend specification
			 * @param {Function} onSuccess (Optional)Callback function of success query.
			 * @param {Function} onError (Optional)Callback function when fail.
			 *
			 * @returns {Object} Object returned by $resource
			 */
			, queryComplaints: {
				method: 'GET',
				url: 'proxy/arkcase/api/latest/plugin/search/COMPLAINT?start=:start&n=:n&sort=:sort&filters=:filters',
				cache: false,
				isArray: false
			}

			, queryTasks: {
				method: 'GET',
				cache: true,
				url: 'proxy/arkcase/api/latest/plugin/search/children?parentType=COMPLAINT&childType=TASK&parentId=:id&start=:startWith&n=:count'
			}

			, queryAudit: {
				method: 'GET',
				cache: true,
				url: 'proxy/arkcase/api/latest/plugin/audit/COMPLAINT/:id?start=:startWith&n=:count'
			}

		});
	}
]);