/**
 * Test script for SearchBase.Model
 *
 * @author jwu
 */

describe("SearchBase.Model", function() {
    var testData = null;

    beforeEach(function() {
        SearchBase.Model.setSearchInfo(AcmEx.Model.Search.getDefaultSearchInfo());

        testData =
        {
            "responseHeader":{
                "status":0,
                "QTime":5705,
                "params":{
                    "facet":"true",
                    "sort":"",
                    "indent":"true",
                    "facet.query":["{!key='Incident Date, Previous Week'}incident_date_tdt:[NOW/DAY-7DAY TO *]",
                        "{!key='Incident Date, Previous Month'}incident_date_tdt:[NOW/DAY-1MONTH TO *]",
                        "{!key='Incident Date, Previous Year'}incident_date_tdt:[NOW/DAY-1YEAR TO *]",
                        "{!key='Due_Date, Previous Week'}dueDate_tdt:[NOW/DAY-7DAY TO *]",
                        "{!key='Due_Date, Previous Month'}dueDate_tdt:[NOW/DAY-1MONTH TO *]",
                        "{!key='Due_Date, Previous Year'}dueDate_tdt:[NOW/DAY-1YEAR TO *]",
                        "{!key='Modify Date, Previous Week'}modified_date_tdt:[NOW/DAY-7DAY TO *]",
                        "{!key='Modify Date, Previous Month'}modified_date_tdt:[NOW/DAY-1MONTH TO *]",
                        "{!key='Modify Date, Previous Year'}modified_date_tdt:[NOW/DAY-1YEAR TO *]",
                        "{!key='Create Date, Previous Week'}create_date_tdt:[NOW/DAY-7DAY TO *]",
                        "{!key='Create Date, Previous Month'}create_date_tdt:[NOW/DAY-1MONTH TO *]",
                        "{!key='Create Date, Previous Year'}create_date_tdt:[NOW/DAY-1YEAR TO *]"],
                    "start":"0",
                    "q":"ann",
                    "facet.field":["{!key='Create User'}creator_lcs",
                        "{!key='City'}location_city_lcs",
                        "{!key='Object Type'}object_type_s",
                        "{!key='Status'}status_lcs"],

                    "facet.date":"create_dt",
                    "f.create_dt.facet.date.start":"2014-01-01T00:00:00Z",
                    "f.create_dt.facet.date.gap":"+1MONTH",
                    //"facet.field_xxxx":"object_type_s",
                    "f.create_dt.facet.date.end":"2015-01-01T00:00:00Z",

                    "wt":"json",
                    "rows":"500"}},
            "response":{"numFound":434,"start":0,"docs":[] //array data not tested in this test
            },
            "facet_counts":{
                "facet_queries":{
                    "Incident Date, Previous Week":0,
                    "Incident Date, Previous Month":0,
                    "Incident Date, Previous Year":0,
                    "Due_Date, Previous Week":0,
                    "Due_Date, Previous Month":0,
                    "Due_Date, Previous Year":0,
                    "Modify Date, Previous Week":1,
                    "Modify Date, Previous Month":1,
                    "Modify Date, Previous Year":1,
                    "Create Date, Previous Week":0,
                    "Create Date, Previous Month":0,
                    "Create Date, Previous Year":1},
                "facet_fields":{
                    "Create User":[
                        "acm3",0,
                        "ann-acm",0],
                    "City":[
                        "city",0,
                        "fairfax",0,
                        "mclean",0,
                        "peoria",0,
                        "vienna",0],
                    "Object Type":[
                        "USER",33,
                        "CASE",2,
                        "CASE_FILE",20,
                        "COMPLAINT",127,
                        "CONTACT-METHOD",0,
                        "DOCUMENT",81,
                        "GROUP",31,
                        "LOCATION",0,
                        "ORGANIZATION",0,
                        "PERSON",51,
                        "PERSON-ASSOCIATION",0,
                        "TASK",89],
                    "Status":[
                        "valid",1,
                        "active",0,
                        "draft",0]},

                "facet_dates":{
                    "create_dt":{
                        "2014-01-01T00:00:00Z":0,
                        "2014-02-01T00:00:00Z":0,
                        "2014-03-01T00:00:00Z":0,
                        "2014-04-01T00:00:00Z":0,
                        "2014-05-01T00:00:00Z":0,
                        "2014-06-01T00:00:00Z":0,
                        "2014-07-01T00:00:00Z":0,
                        "2014-08-01T00:00:00Z":0,
                        "2014-09-01T00:00:00Z":50,
                        "2014-10-01T00:00:00Z":110,
                        "2014-11-01T00:00:00Z":90,
                        "2014-12-01T00:00:00Z":89,
                        "gap":"+1MONTH",
                        "start":"2014-01-01T00:00:00Z",
                        "end":"2015-01-01T00:00:00Z"}},

                "facet_ranges":{}
            }};
    });

    it("SearchBase.Model: parse facet label, key, def", function() {
        var s = "";
        var facetEntry = SearchBase.Model._parseFacetLabelKeyDef(s);
        expect(facetEntry).toEqual(null);

        s = "object_type_s";
        facetEntry = SearchBase.Model._parseFacetLabelKeyDef(s);
        expect(facetEntry.label).toEqual(undefined);
        expect(facetEntry.key)  .toEqual("object_type_s");
        expect(facetEntry.def)  .toEqual(undefined);

        s = "{!key='Object Type'}object_type_s";
        facetEntry = SearchBase.Model._parseFacetLabelKeyDef(s);
        expect(facetEntry.label).toEqual("Object Type");
        expect(facetEntry.key)  .toEqual("object_type_s");
        expect(facetEntry.def)  .toEqual(undefined);

        s = "{!key='Object Type'}object_type_s:something";
        facetEntry = SearchBase.Model._parseFacetLabelKeyDef(s);
        expect(facetEntry.label).toEqual("Object Type");
        expect(facetEntry.key)  .toEqual("object_type_s");
        expect(facetEntry.def)  .toEqual("something");

        s = "{!key='Incident Date, Previous Week'}incident_date_tdt:[NOW/DAY-7DAY TO *]";
        facetEntry = SearchBase.Model._parseFacetLabelKeyDef(s);
        expect(facetEntry.label).toEqual("Incident Date, Previous Week");
        expect(facetEntry.key)  .toEqual("incident_date_tdt");
        expect(facetEntry.def)  .toEqual("[NOW/DAY-7DAY TO *]");
    });

    it("SearchBase.Model: parse facet param", function() {
        var param = "";
        var facetEntries = SearchBase.Model._parseFacetEntries(param);
        expect(facetEntries).toEqual([]);

        param = "object_type_s";
        facetEntries = SearchBase.Model._parseFacetEntries(param);
        expect(facetEntries.length).toEqual(1);
        expect(facetEntries[0].key).toEqual("object_type_s");

        param = ["{!key='Object Type'}object_type_s"];
        facetEntries = SearchBase.Model._parseFacetEntries(param);
        expect(facetEntries.length).toEqual(1);
        expect(facetEntries[0].label).toEqual("Object Type");
        expect(facetEntries[0].key)  .toEqual("object_type_s");

        param = ["{!key='Object Type'}object_type_s"
            ,"{!key='Status'}status_lcs"
        ];
        facetEntries = SearchBase.Model._parseFacetEntries(param);
        expect(facetEntries.length).toEqual(2);
        expect(facetEntries[0].key).toEqual("object_type_s");
        expect(facetEntries[1].key).toEqual("status_lcs");


        param = ["{!key='Incident Date, Previous Week'}incident_date_tdt:[NOW/DAY-7DAY TO *]"
            ,"{!key='Incident Date, Previous Month'}incident_date_tdt:[NOW/DAY-1MONTH TO *]"
            ,"{!key='Incident Date, Previous Year'}incident_date_tdt:[NOW/DAY-1YEAR TO *]"
            ,"{!key='Create Date, Previous Year'}create_date_tdt:[NOW/DAY-1YEAR TO *]"
        ];
        facetEntries = SearchBase.Model._parseFacetEntries(param);
        expect(facetEntries.length).toEqual(4);
        expect(facetEntries[0].key).toEqual("incident_date_tdt");
        expect(facetEntries[1].key).toEqual("incident_date_tdt");
        expect(facetEntries[2].key).toEqual("incident_date_tdt");
        expect(facetEntries[3].key).toEqual("create_date_tdt");
    });

    it("SearchBase.Model: make facet", function() {
        expect(SearchBase.Model.validateFacetSearchData(testData)).toEqual(true);

        var facet = SearchBase.Model.makeFacet(testData);
        expect(facet.facet_queries.length).toEqual(4);
        expect(facet.facet_fields .length).toEqual(4);
        expect(facet.facet_dates  .length).toEqual(1);
        expect(facet.facet_ranges .length).toEqual(0);

        expect(facet.facet_queries[3].label).toEqual("Create Date");
        expect(facet.facet_queries[3].key).toEqual("create_date_tdt");
        expect(facet.facet_queries[3].count).toEqual(1);
        expect(facet.facet_queries[3].values.length).toEqual(3);
        expect(facet.facet_queries[3].values[0].name).toEqual("Previous Week");
        expect(facet.facet_queries[3].values[0].count).toEqual(0);
        expect(facet.facet_queries[3].values[1].name).toEqual("Previous Month");
        expect(facet.facet_queries[3].values[1].count).toEqual(0);
        expect(facet.facet_queries[3].values[2].name).toEqual("Previous Year");
        expect(facet.facet_queries[3].values[2].count).toEqual(1);

        expect(facet.facet_fields[2].label).toEqual("Object Type");
        expect(facet.facet_fields[2].values.length).toEqual(12);
        expect(facet.facet_fields[2].values[11].count).toEqual(89);

        expect(facet.facet_dates[0].key).toEqual("create_dt");
        expect(facet.facet_dates[0].values.length).toEqual(12);
        expect(facet.facet_dates[0].values[10].count).toEqual(90);
        expect(facet.facet_dates[0].values[11].count).toEqual(89);
        expect(facet.facet_dates[0].gap)  .toEqual("+1MONTH");
        expect(facet.facet_dates[0].start).toEqual("2014-01-01T00:00:00Z");
        expect(facet.facet_dates[0].end)  .toEqual("2015-01-01T00:00:00Z");

    });

    it("SearchBase.Model: add/remove/find filter, makeFilterParam", function() {
        var si = SearchBase.Model.getSearchInfo();
        var k;
        var v;

        expect(SearchBase.Model.findFilter(si, k, v)).toEqual(false);
        expect(SearchBase.Model.makeFilterParam(si)).toEqual('');

        k = "Create Date";
        v = "Previous Year";
        SearchBase.Model.addFilter(si, k, v);
        expect(SearchBase.Model.findFilter(si, k, v)).toEqual(true);
        expect(SearchBase.Model.makeFilterParam(si)).toEqual('&filters=fq="Create Date":Previous Year');

        SearchBase.Model.removeFilter(si, k, v);
        expect(SearchBase.Model.findFilter(si, k, v)).toEqual(false);
        expect(SearchBase.Model.makeFilterParam(si)).toEqual('');

        SearchBase.Model.addFilter(si, k, v);
        expect(SearchBase.Model.findFilter(si, k, v)).toEqual(true);
        expect(SearchBase.Model.makeFilterParam(si)).toEqual('&filters=fq="Create Date":Previous Year');

        k = "Create Date";
        v = "Previous Month";
        SearchBase.Model.addFilter(si, k, v);
        expect(SearchBase.Model.findFilter(si, k, v)).toEqual(true);
        expect(SearchBase.Model.makeFilterParam(si)).toEqual('&filters=fq="Create Date":Previous Year|Previous Month');

        k = "Object Type";
        v = "CASE_FILE";
        SearchBase.Model.addFilter(si, k, v);
        expect(SearchBase.Model.findFilter(si, k, v)).toEqual(true);
        expect(SearchBase.Model.makeFilterParam(si)).toEqual('&filters=fq="Create Date":Previous Year|Previous Month%26fq="Object Type":CASE_FILE');

        k = "Create Date";
        v = "Previous Year";
        SearchBase.Model.removeFilter(si, k, v);
        expect(SearchBase.Model.findFilter(si, k, v)).toEqual(false);
        expect(SearchBase.Model.makeFilterParam(si)).toEqual('&filters=fq="Create Date":Previous Month%26fq="Object Type":CASE_FILE');

    });


    it("SearchBase.Model: onViewChangedFacetSelection", function() {
        var si = SearchBase.Model.getSearchInfo();

        var selected = null;
        SearchBase.Model.onViewChangedFacetSelection(selected);
        expect(si.filter).toEqual([]);

        selected = [];
        SearchBase.Model.onViewChangedFacetSelection(selected);
        expect(si.filter).toEqual([]);

        si.filter = [];
        selected = [{type:"facet_fields", name:"Object Type", value:"CASE_FILE"}];
        SearchBase.Model.onViewChangedFacetSelection(selected);
        expect(si.filter).toEqual([{key:"Object Type", values:["CASE_FILE"]}]);

        si.filter = [];
        selected = [{type:"facet_fields", name:"Object Type", value:"CASE_FILE"}
            ,{type:"facet_fields", name:"Object Type", value:"TASK"}
        ];
        SearchBase.Model.onViewChangedFacetSelection(selected);
        expect(si.filter).toEqual([{key:"Object Type", values:["CASE_FILE","TASK"]}]);

        si.filter = [];
        selected = [{type:"facet_fields", name:"Object Type", value:"CASE_FILE"}
            ,{type:"facet_fields", name:"Object Type", value:"TASK"}
            ,{type:"facet_fields", name:"Status", value:"valid"}
        ];
        SearchBase.Model.onViewChangedFacetSelection(selected);
        expect(si.filter).toEqual([{key:"Object Type", values:["CASE_FILE","TASK"]}
            ,{key:"Status", values:["valid"]}
        ]);

    });

});
