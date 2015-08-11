'use strict';

angular.module('services').factory('SearchService', ['$resource',
    function ($resource) {
        return $resource('acm/api/latest/plugin/search', {}, {
            queryFacetedSearch: {
                method: 'GET',
                cache: true,
                url: 'acm/api/v1/plugin/search/facetedSearch?q=:searchString&filters=fq="Object Type"::objectType&fq="Owner"::owner&start=:startWith&n=:count',
                isArray: false,
                transformResponse: function (data, headerGetter) {
                    var result = {};
                    var searchObj = JSON.parse(data);

                    // Process Faceted fields
                    if (searchObj && searchObj.facet_counts && searchObj.facet_counts.facet_fields) {
                        var fields = searchObj.facet_counts.facet_fields;
                        var newFields = {};
                        _.forEach(fields, function (items, fieldName) {
                            // Convert field's dirty array to the array of structures
                            var newItems = [];
                            for (var i = 0; i < items.length; i += 2) {
                                if (items[i + 1] > 0) {
                                    newItems.push({
                                        name: items[i],
                                        count: items[i + 1]
                                    });
                                }
                            }
                            newFields[fieldName] = newItems;
                        });

                        searchObj.facet_counts.facet_fields = newFields;
                    }
                    return searchObj;
                }
            }
        });
    }
]);