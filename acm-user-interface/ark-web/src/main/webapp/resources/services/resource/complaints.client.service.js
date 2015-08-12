'use strict';

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
			},

			queryComplaints: {
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