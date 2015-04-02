/**
 * Test script for ObjNav.Model
 *
 * @author jwu
 */

describe("ObjNav.Model", function() {

    it("ObjNav.Model.Tree.Key: get node info by key", function() {
        var key = null;
        expect(ObjNav.Model.Tree.Key.getNodeTypeByKey(key)).toEqual("");
        expect(ObjNav.Model.Tree.Key.getNodeIdByKey  (key)).toEqual("");
        expect(ObjNav.Model.Tree.Key.getPageIdByKey  (key)).toEqual("");
        expect(ObjNav.Model.Tree.Key.getObjIdByKey   (key)).toEqual("");
        expect(ObjNav.Model.Tree.Key.getLastKeyPart  (key)).toEqual("");

        key = "prevPage";
        expect(ObjNav.Model.Tree.Key.getNodeTypeByKey(key)).toEqual("prevPage");
        expect(ObjNav.Model.Tree.Key.getNodeIdByKey  (key)).toEqual("");
        expect(ObjNav.Model.Tree.Key.getPageIdByKey  (key)).toEqual("");
        expect(ObjNav.Model.Tree.Key.getObjIdByKey   (key)).toEqual("");
        expect(ObjNav.Model.Tree.Key.getLastKeyPart  (key)).toEqual("prevPage");

        key = "p.2";
        expect(ObjNav.Model.Tree.Key.getNodeTypeByKey(key)).toEqual("p");
        expect(ObjNav.Model.Tree.Key.getNodeIdByKey  (key)).toEqual("2");
        expect(ObjNav.Model.Tree.Key.getPageIdByKey  (key)).toEqual("2");
        expect(ObjNav.Model.Tree.Key.getObjIdByKey   (key)).toEqual("");
        expect(ObjNav.Model.Tree.Key.getLastKeyPart  (key)).toEqual("p.2");

        key = "p.2/c.1993";
        expect(ObjNav.Model.Tree.Key.getNodeTypeByKey(key)).toEqual("p/c");
        expect(ObjNav.Model.Tree.Key.getNodeIdByKey  (key)).toEqual("1993");
        expect(ObjNav.Model.Tree.Key.getPageIdByKey  (key)).toEqual("2");
        expect(ObjNav.Model.Tree.Key.getObjIdByKey   (key)).toEqual("1993");
        expect(ObjNav.Model.Tree.Key.getLastKeyPart  (key)).toEqual("c.1993");

        key = "p.2/c.1993/doc";
        expect(ObjNav.Model.Tree.Key.getNodeTypeByKey(key)).toEqual("p/c/doc");
        expect(ObjNav.Model.Tree.Key.getNodeIdByKey  (key)).toEqual("");
        expect(ObjNav.Model.Tree.Key.getPageIdByKey  (key)).toEqual("2");
        expect(ObjNav.Model.Tree.Key.getObjIdByKey   (key)).toEqual("1993");
        expect(ObjNav.Model.Tree.Key.getLastKeyPart  (key)).toEqual("doc");

        key = "p.2/c.1993/doc/item.#456";
        expect(ObjNav.Model.Tree.Key.getNodeTypeByKey(key)).toEqual("p/c/doc/item");
        expect(ObjNav.Model.Tree.Key.getNodeIdByKey  (key)).toEqual("#456");
        expect(ObjNav.Model.Tree.Key.getPageIdByKey  (key)).toEqual("2");
        expect(ObjNav.Model.Tree.Key.getObjIdByKey   (key)).toEqual("1993");
        expect(ObjNav.Model.Tree.Key.getLastKeyPart  (key)).toEqual("item.#456");

        key = "p.2/c.1993/doc/item.#456/name";
        expect(ObjNav.Model.Tree.Key.getNodeTypeByKey(key)).toEqual("p/c/doc/item/name");
        expect(ObjNav.Model.Tree.Key.getNodeIdByKey  (key)).toEqual("");
        expect(ObjNav.Model.Tree.Key.getPageIdByKey  (key)).toEqual("2");
        expect(ObjNav.Model.Tree.Key.getObjIdByKey   (key)).toEqual("1993");
        expect(ObjNav.Model.Tree.Key.getLastKeyPart  (key)).toEqual("name");

        key = "p.2/c.1993/people/spouse";
        expect(ObjNav.Model.Tree.Key.getNodeTypeByKey(key)).toEqual("p/c/people/spouse");
        expect(ObjNav.Model.Tree.Key.getNodeIdByKey  (key)).toEqual("");
        expect(ObjNav.Model.Tree.Key.getPageIdByKey  (key)).toEqual("2");
        expect(ObjNav.Model.Tree.Key.getObjIdByKey   (key)).toEqual("1993");
        expect(ObjNav.Model.Tree.Key.getLastKeyPart  (key)).toEqual("spouse");

    });


    it("ObjNav.Model.Tree.Key: node type map info", function() {
        ObjNav.Model.interface = {nodeTypeMap: function() {
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

        expect(ObjNav.Model.Tree.Key.getTabIdsByKey(null)).toEqual([]);
        expect(ObjNav.Model.Tree.Key.getTabIdsByKey(""))  .toEqual([]);
        expect(ObjNav.Model.Tree.Key.getTabIdsByKey("prevPage")).toEqual(["tabBlank"]);
        expect(ObjNav.Model.Tree.Key.getTabIdsByKey("nextPage")).toEqual(["tabBlank"]);
        expect(ObjNav.Model.Tree.Key.getTabIdsByKey("p.2"))     .toEqual(["tabBlank"]);
        expect(ObjNav.Model.Tree.Key.getTabIdsByKey("p.2/c.1993")).toEqual(["tabTitle","tabDetail","tabPeople","tabDocs","tabParticipants","tabNotes","tabTasks","tabRefs","tabHistory"]);
        expect(ObjNav.Model.Tree.Key.getTabIdsByKey("p.2/c.1993/d")).toEqual(["tabDetail"]);
        expect(ObjNav.Model.Tree.Key.getTabIdsByKey("p.2/c.1993/o/c.567")).toEqual(["tabDoc"]);

        expect(ObjNav.Model.Tree.Key.getIconByKey(null)).toEqual(null);
        expect(ObjNav.Model.Tree.Key.getIconByKey(""))  .toEqual(null);
        expect(ObjNav.Model.Tree.Key.getIconByKey("prevPage")).toEqual("i-arrow-up");
        expect(ObjNav.Model.Tree.Key.getIconByKey("nextPage")).toEqual("i-arrow-down");
        expect(ObjNav.Model.Tree.Key.getIconByKey("p.2"))     .toEqual("");
        expect(ObjNav.Model.Tree.Key.getIconByKey("p.2/c.1993")).toEqual("i-folder");
        expect(ObjNav.Model.Tree.Key.getIconByKey("p.2/c.1993/d")).toEqual("");
        expect(ObjNav.Model.Tree.Key.getIconByKey("p.2/c.1993/o/c.567")).toEqual(null);

        //todo: need better comparator for array with different order
        expect(ObjNav.Model.Tree.Key.getTabIds()).toEqual(["tabBlank", "tabTitle","tabDetail","tabPeople","tabDocs","tabParticipants","tabNotes","tabTasks","tabRefs","tabHistory","tabDoc","tabTemplates"]);
    });

    it("ObjNav.Model.Tree.Key: makeKey", function() {
        expect(ObjNav.Model.Tree.Key.makeKey([])).toEqual("");
        expect(ObjNav.Model.Tree.Key.makeKey([{type:"prevPage"}])).toEqual("prevPage");
        expect(ObjNav.Model.Tree.Key.makeKey([{type:"p",id:"2"}])).toEqual("p.2");
        expect(ObjNav.Model.Tree.Key.makeKey([{type:"p",id:"2"},{type:"c",id:"1993"}])).toEqual("p.2/c.1993");
        expect(ObjNav.Model.Tree.Key.makeKey([{type:"p",id:"2"},{type:"c",id:"1993"},{type:"d"}])).toEqual("p.2/c.1993/d");
        expect(ObjNav.Model.Tree.Key.makeKey([{type:"p",id:"2"},{type:"c",id:"1993"},{type:"o"},{type:"c",id:"567"}])).toEqual("p.2/c.1993/o/c.567");


        //integer id works as well
        expect(ObjNav.Model.Tree.Key.makeKey([{type:"p",id:0}])).toEqual("p.0");
        expect(ObjNav.Model.Tree.Key.makeKey([{type:"p",id:2},{type:"c",id:1993}])).toEqual("p.2/c.1993");
    });

    it("ObjNav.Model.Tree.Key: makeNodeType", function() {
        expect(ObjNav.Model.Tree.Key.makeNodeType([])).toEqual("");
        expect(ObjNav.Model.Tree.Key.makeNodeType(["prevPage"])).toEqual("prevPage");
        expect(ObjNav.Model.Tree.Key.makeNodeType(["p"])).toEqual("p");
        expect(ObjNav.Model.Tree.Key.makeNodeType(["p","c"])).toEqual("p/c");
        expect(ObjNav.Model.Tree.Key.makeNodeType(["p","c","d"])).toEqual("p/c/d");
        expect(ObjNav.Model.Tree.Key.makeNodeType(["p","c","o","c"])).toEqual("p/c/o/c");
    });


});