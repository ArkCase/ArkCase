'use strict';

/**
 * @ngdoc service
 * @name services.service:ComplaintsService
 *
 * @description
 *
 * {@link https://github.com/Armedia/ACM3/blob/develop/acm-user-interface/ark-web/src/main/webapp/resources/services/resource/complaints.client.service.js services/resource/complaints.client.service.js}

 * CasesService includes group of REST calls related to Cases module. Functions are implemented using $resoruce.
 */
angular.module('services').factory('ComplaintsService', ['$resource',
	function($resource) {
		return $resource('proxy/arkcase/api/latest/plugin',{
		},{
			get: {
				method: 'GET',
				cache: true,
				url: 'proxy/arkcase/api/latest/plugin/complaint/byId/:id',
				isArray: false
			},

			queryAudit: {
				method: 'GET',
				cache: true,
				url: 'proxy/arkcase/api/latest/plugin/audit/COMPLAINT/:id?start=:startWith&n=:count'
			},

			queryTasks: {
				method: 'GET',
				cache: true,
				url: 'proxy/arkcase/api/latest/plugin/search/children?parentType=COMPLAINT&childType=TASK&parentId=:id&start=:startWith&n=:count'
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

			, queryComplaints_old: {
				method: 'GET',
				cache: true,
				url: 'proxy/arkcase/api/latest/plugin/search/COMPLAINT?start=0&n=50&s=sort-created-date-desc&filters=my-complaints-assigned',
				isArray: true,
				transformResponse: function(data, headerGetter){
					var result = [];
					var complaintsObj = JSON.parse(data);
					if (complaintsObj && complaintsObj.response && _.isArray(complaintsObj.response.docs)) {
						result = complaintsObj.response.docs;
						for (var i = 0; i < result.length; i++) {
							result[i].nodeType = 'info';
							result[i].actions = [
								{nodeType: 'details', title_parseable: 'Details', parent: result[i]},
								{nodeType: 'location', title_parseable: 'Location', parent: result[i]},
								{nodeType: 'people', title_parseable: 'People', parent: result[i]},
								{nodeType: 'documents', title_parseable: 'Documents', parent: result[i]},
								{nodeType: 'tasks', title_parseable: 'Tasks', parent: result[i]},
								{nodeType: 'notes', title_parseable: 'Notes', parent: result[i]},
								{nodeType: 'participants', title_parseable: 'Participants', parent: result[i]},
								{nodeType: 'references', title_parseable: 'References', parent: result[i]},
								{nodeType: 'history', title_parseable: 'History', parent: result[i]},
								{nodeType: 'calendar', title_parseable: 'Calendar', parent: result[i]},
								{nodeType: 'time', title_parseable: 'Time', parent: result[i]},
								{nodeType: 'cost', title_parseable: 'Cost', parent: result[i]},
							];
						}
					}
					return result;
				}
			}
        });
	}
]);