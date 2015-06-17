/**
 * Test script for ObjNav.View
 *
 * @author jwu
 */

describe("ObjNav.View", function() {

    it("ObjNav.View.Tree.Key: node type map info", function() {
        ObjNav.View.interface = {nodeTypeMap: function() {
            return [
                {nodeType: "prevPage"    ,icon: "i-arrow-up"   ,tabIds: ["tabBlank"]}
                ,{nodeType: "nextPage"   ,icon: "i-arrow-down" ,tabIds: ["tabBlank"]}
                ,{nodeType: "p"          ,icon: ""             ,tabIds: ["tabBlank"]}
                ,{nodeType: "p/c"        ,icon: "i-folder"     ,tabIds: ["tabTitle","tabDetail","tabPeople","tabDocs","tabParticipants","tabNotes","tabTasks","tabRefs","tabHistory"]}
                ,{nodeType: "p/c/d"      ,icon: "",tabIds: ["tabDetail"]}
                ,{nodeType: "p/c/p"      ,icon: "",tabIds: ["tabPeople"]}
                ,{nodeType: "p/c/o"      ,icon: "",tabIds: ["tabDocs"]}
                ,{nodeType: "p/c/o/c"    ,icon: null,tabIds: ["tabDoc"]}
                ,{nodeType: "p/c/a"      ,icon: "",tabIds: ["tabParticipants"]}
                ,{nodeType: "p/c/n"      ,icon: "",tabIds: ["tabNotes"]}
                ,{nodeType: "p/c/t"      ,icon: "",tabIds: ["tabTasks"]}
                ,{nodeType: "p/c/r"      ,icon: "",tabIds: ["tabRefs"]}
                ,{nodeType: "p/c/h"      ,icon: "",tabIds: ["tabHistory"]}
                ,{nodeType: "p/c/tm"     ,icon: "",tabIds: ["tabTemplates"]}
            ];
        }};

        expect(ObjNav.View.Navigator.getTabIdsByKey(null)).toEqual([]);
        expect(ObjNav.View.Navigator.getTabIdsByKey(""))  .toEqual([]);
        expect(ObjNav.View.Navigator.getTabIdsByKey("prevPage")).toEqual(["tabBlank"]);
        expect(ObjNav.View.Navigator.getTabIdsByKey("nextPage")).toEqual(["tabBlank"]);
        expect(ObjNav.View.Navigator.getTabIdsByKey("p.2"))     .toEqual(["tabBlank"]);
        expect(ObjNav.View.Navigator.getTabIdsByKey("p.2/c.1993")).toEqual(["tabTitle","tabDetail","tabPeople","tabDocs","tabParticipants","tabNotes","tabTasks","tabRefs","tabHistory"]);
        expect(ObjNav.View.Navigator.getTabIdsByKey("p.2/c.1993/d")).toEqual(["tabDetail"]);
        expect(ObjNav.View.Navigator.getTabIdsByKey("p.2/c.1993/o/c.567")).toEqual(["tabDoc"]);

        expect(ObjNav.View.Navigator.getIconByKey(null)).toEqual(null);
        expect(ObjNav.View.Navigator.getIconByKey(""))  .toEqual(null);
        expect(ObjNav.View.Navigator.getIconByKey("prevPage")).toEqual("i-arrow-up");
        expect(ObjNav.View.Navigator.getIconByKey("nextPage")).toEqual("i-arrow-down");
        expect(ObjNav.View.Navigator.getIconByKey("p.2"))     .toEqual("");
        expect(ObjNav.View.Navigator.getIconByKey("p.2/c.1993")).toEqual("i-folder");
        expect(ObjNav.View.Navigator.getIconByKey("p.2/c.1993/d")).toEqual("");
        expect(ObjNav.View.Navigator.getIconByKey("p.2/c.1993/o/c.567")).toEqual(null);

        //todo: need better comparator for array with different order
        expect(ObjNav.View.Navigator.getTabIds()).toEqual(["tabBlank", "tabTitle","tabDetail","tabPeople","tabDocs","tabParticipants","tabNotes","tabTasks","tabRefs","tabHistory","tabDoc","tabTemplates"]);
    });

});