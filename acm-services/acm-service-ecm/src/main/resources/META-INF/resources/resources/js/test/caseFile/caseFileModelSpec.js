/**
 * Test script for CaseFile.Model
 *
 * @author jwu
 */

describe("CaseFile.Model", function() {
    beforeEach(function() {
        AcmEx.Model.Tree.Config.setName("/plugin/casefile");
        AcmEx.Model.Tree.Key.setNodeTypeMap(CaseFile.Model.Tree.Key.nodeTypeMap);
    });

    it("CaseFile.Model.Tree: get node info by key", function() {
        var key = null;
        expect(AcmEx.Model.Tree.Key.getNodeTypeByKey(key)).toEqual("");
        expect(AcmEx.Model.Tree.Key.getNodeIdByKey  (key)).toEqual("");
        expect(AcmEx.Model.Tree.Key.getPageIdByKey  (key)).toEqual("");
        expect(AcmEx.Model.Tree.Key.getObjIdByKey   (key)).toEqual("");

        key = "prevPage";
        expect(AcmEx.Model.Tree.Key.getNodeTypeByKey(key)).toEqual("prevPage");
        expect(AcmEx.Model.Tree.Key.getNodeIdByKey  (key)).toEqual("");
        expect(AcmEx.Model.Tree.Key.getPageIdByKey  (key)).toEqual("");
        expect(AcmEx.Model.Tree.Key.getObjIdByKey   (key)).toEqual("");

        key = "p.2";
        expect(AcmEx.Model.Tree.Key.getNodeTypeByKey(key)).toEqual("p");
        expect(AcmEx.Model.Tree.Key.getNodeIdByKey  (key)).toEqual("2");
        expect(AcmEx.Model.Tree.Key.getPageIdByKey  (key)).toEqual("2");
        expect(AcmEx.Model.Tree.Key.getObjIdByKey   (key)).toEqual("");

        key = "p.2/c.1993";
        expect(AcmEx.Model.Tree.Key.getNodeTypeByKey(key)).toEqual("p/c");
        expect(AcmEx.Model.Tree.Key.getNodeIdByKey  (key)).toEqual("1993");
        expect(AcmEx.Model.Tree.Key.getPageIdByKey  (key)).toEqual("2");
        expect(AcmEx.Model.Tree.Key.getObjIdByKey   (key)).toEqual("1993");

        key = "p.2/c.1993/d";
        expect(AcmEx.Model.Tree.Key.getNodeTypeByKey(key)).toEqual("p/c/d");
        expect(AcmEx.Model.Tree.Key.getNodeIdByKey  (key)).toEqual("");
        expect(AcmEx.Model.Tree.Key.getPageIdByKey  (key)).toEqual("2");
        expect(AcmEx.Model.Tree.Key.getObjIdByKey   (key)).toEqual("1993");

        key = "p.2/c.1993/tm";
        expect(AcmEx.Model.Tree.Key.getNodeTypeByKey(key)).toEqual("p/c/tm");
        expect(AcmEx.Model.Tree.Key.getNodeIdByKey  (key)).toEqual("");
        expect(AcmEx.Model.Tree.Key.getPageIdByKey  (key)).toEqual("2");
        expect(AcmEx.Model.Tree.Key.getObjIdByKey   (key)).toEqual("1993");

    });


    it("CaseFile.Model.Tree: node type map info", function() {
        expect(AcmEx.Model.Tree.Key.getTabIdsByKey(null))           .toEqual([]);
        expect(AcmEx.Model.Tree.Key.getTabIdsByKey(""))             .toEqual([]);
        expect(AcmEx.Model.Tree.Key.getTabIdsByKey("prevPage"))     .toEqual(["tabBlank"]);
        expect(AcmEx.Model.Tree.Key.getTabIdsByKey("nextPage"))     .toEqual(["tabBlank"]);
        expect(AcmEx.Model.Tree.Key.getTabIdsByKey("p.2"))          .toEqual(["tabBlank"]);
        expect(AcmEx.Model.Tree.Key.getTabIdsByKey("p.2/c.1993"))   .toEqual(["tabTitle","tabDetail","tabPeople","tabDocs","tabParticipants","tabNotes","tabTasks","tabRefs","tabHistory","tabTemplates"]);
        expect(AcmEx.Model.Tree.Key.getTabIdsByKey("p.2/c.1993/d")) .toEqual(["tabDetail"]);
        expect(AcmEx.Model.Tree.Key.getTabIdsByKey("p.2/c.1993/tm")).toEqual(["tabTemplates"]);

        expect(AcmEx.Model.Tree.Key.getIconByKey(null))           .toEqual(null);
        expect(AcmEx.Model.Tree.Key.getIconByKey(""))             .toEqual(null);
        expect(AcmEx.Model.Tree.Key.getIconByKey("prevPage"))     .toEqual("i-arrow-up");
        expect(AcmEx.Model.Tree.Key.getIconByKey("nextPage"))     .toEqual("i-arrow-down");
        expect(AcmEx.Model.Tree.Key.getIconByKey("p.2"))          .toEqual("");
        expect(AcmEx.Model.Tree.Key.getIconByKey("p.2/c.1993"))   .toEqual("i-folder");
        expect(AcmEx.Model.Tree.Key.getIconByKey("p.2/c.1993/d")) .toEqual("");
        expect(AcmEx.Model.Tree.Key.getIconByKey("p.2/c.1993/tm")).toEqual("");

        //todo: need better comparator for array with different order
        expect(AcmEx.Model.Tree.Key.getTabIds()).toEqual(["tabBlank", "tabTitle","tabDetail","tabPeople","tabDocs","tabParticipants","tabNotes","tabTasks","tabRefs","tabHistory","tabTemplates"]);
    });

});
