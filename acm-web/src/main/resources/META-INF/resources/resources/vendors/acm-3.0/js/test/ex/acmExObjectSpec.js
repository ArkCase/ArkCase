/**
 * Test script for AcmEx
 *
 * @author jwu
 */
describe("AcmExObject", function()
{

    beforeEach(function() {
        this.testingData = [
            {
                id: 100
                ,fieldTitle: "title100"
                ,fieldType:  "g_type100"
                ,fieldOther: "other100"
            }
            ,{
                id: 101
                ,fieldTitle: "title101"
                ,fieldType:  "h_type101"
                ,fieldOther: "other101"
            }
            ,{
                id: 102
                ,fieldTitle: "title102"
                ,fieldType:  "e_type102"
                ,fieldOther: "other102"
            }
            ,{
                id: 103
                ,fieldTitle: "title103"
                ,fieldType:  "a_type103"
                ,fieldOther: "other103"
            }
            ,{
                id: 104
                ,fieldTitle: "title104"
                ,fieldType:  "c_type104"
                ,fieldOther: "other104"
            }
            ,{
                id: 105
                ,fieldTitle: "title105"
                ,fieldType:  "f_type105"
                ,fieldOther: "other105"
            }
            ,{
                id: 106
                ,fieldTitle: "title106"
                ,fieldType:  "b_type106"
                ,fieldOther: "other106"
            }
            ,{
                id: 107
                ,fieldTitle: "title107"
                ,fieldType:  "d_type107"
                ,fieldOther: "other107"
            }
        ];
    });

    it("jTable sort and paging: all sorted items", function() {
        var sortMap = {};
        sortMap["title"] = "fieldTitle";
        sortMap["type"]  = "fieldType";

        var jtParams = {
            jtPageSize: 10
            ,jtSorting: "type ASC"
            ,jtStartIndex: 0
        };

        var result = AcmEx.Object.JTable.getPagingItems(jtParams, this.testingData, sortMap);
        //result after sort: a(103) b(106) c(104) d(107) e(102) f(105) g(100) h(101)
        expect(result.length).toEqual(this.testingData.length);
        expect(result[0].item.id).toEqual(103);
        expect(result[1].item.id).toEqual(106);
        expect(result[2].item.id).toEqual(104);
        expect(result[3].item.id).toEqual(107);
        expect(result[4].item.id).toEqual(102);
        expect(result[5].item.id).toEqual(105);
        expect(result[6].item.id).toEqual(100);
        expect(result[7].item.id).toEqual(101);
    });

    it("jTable sort and paging: sort by DESC", function() {
        var sortMap = {};
        sortMap["title"] = "fieldTitle";
        sortMap["type"]  = "fieldType";

        var jtParams = {
            jtPageSize: 10
            ,jtSorting: "type DESC"
            ,jtStartIndex: 0
        };

        var result = AcmEx.Object.JTable.getPagingItems(jtParams, this.testingData, sortMap);
        //result after sort is the reverse of this: a(103) b(106) c(104) d(107) e(102) f(105) g(100) h(101)
        expect(result.length).toEqual(this.testingData.length);
        expect(result[0].item.id).toEqual(101);
        expect(result[1].item.id).toEqual(100);
        expect(result[2].item.id).toEqual(105);
        expect(result[3].item.id).toEqual(102);
        expect(result[4].item.id).toEqual(107);
        expect(result[5].item.id).toEqual(104);
        expect(result[6].item.id).toEqual(106);
        expect(result[7].item.id).toEqual(103);
    });

    it("jTable sort and paging: no sort", function() {
        var sortMap = {};
        sortMap["title"] = "fieldTitle";
        sortMap["type"]  = "fieldType";

        var jtParams = {
            jtPageSize: 10
            ,jtStartIndex: 0
        };

        var result = AcmEx.Object.JTable.getPagingItems(jtParams, this.testingData, sortMap);
        expect(result.length).toEqual(this.testingData.length);
        expect(result[0].item.id).toEqual(100);
        expect(result[1].item.id).toEqual(101);
        expect(result[2].item.id).toEqual(102);
        expect(result[3].item.id).toEqual(103);
        expect(result[4].item.id).toEqual(104);
        expect(result[5].item.id).toEqual(105);
        expect(result[6].item.id).toEqual(106);
        expect(result[7].item.id).toEqual(107);
    });

    it("jTable sort and paging: first page", function() {
        var sortMap = {};
        sortMap["title"] = "fieldTitle";
        sortMap["type"]  = "fieldType";

        var jtParams = {
            jtPageSize: 3
            ,jtSorting: "type ASC"
            ,jtStartIndex: 0
        };

        var result = AcmEx.Object.JTable.getPagingItems(jtParams, this.testingData, sortMap);
        //result after sort:   a(103) b(106) c(104) d(107) e(102) f(105) g(100) h(101)
        //result after paging: a(103) b(106) c(104)
        expect(result.length).toEqual(3);
        expect(result[0].item.id).toEqual(103);
        expect(result[1].item.id).toEqual(106);
        expect(result[2].item.id).toEqual(104);
    });

    it("jTable sort and paging: second page", function() {
        var sortMap = {};
        sortMap["title"] = "fieldTitle";
        sortMap["type"]  = "fieldType";

        var jtParams = {
            jtPageSize: 3
            ,jtSorting: "type ASC"
            ,jtStartIndex: 3
        };

        var result = AcmEx.Object.JTable.getPagingItems(jtParams, this.testingData, sortMap);
        //result after sort: a(103) b(106) c(104) d(107) e(102) f(105) g(100) h(101)
        //result after paging:                    d(107) e(102) f(105)
        expect(result.length).toEqual(3);
        expect(result[0].item.id).toEqual(107);
        expect(result[1].item.id).toEqual(102);
        expect(result[2].item.id).toEqual(105);
    });

    it("jTable sort and paging: last page", function() {
        var sortMap = {};
        sortMap["title"] = "fieldTitle";
        sortMap["type"]  = "fieldType";

        var jtParams = {
            jtPageSize: 3
            ,jtSorting: "type ASC"
            ,jtStartIndex: 6
        };

        var result = AcmEx.Object.JTable.getPagingItems(jtParams, this.testingData, sortMap);
        //result after sort: a(103) b(106) c(104) d(107) e(102) f(105) g(100) h(101)
        //result after paging:                                         g(100) h(101)
        expect(result.length).toEqual(2);
        expect(result[0].item.id).toEqual(100);
        expect(result[1].item.id).toEqual(101);
    });

    it("jTable sort and paging: null source", function() {
        var sortMap = {};
        sortMap["title"] = "fieldTitle";
        sortMap["type"]  = "fieldType";

        var jtParams = {
            jtPageSize: 10
            ,jtSorting: "type ASC"
            ,jtStartIndex: 0
        };

        var result = AcmEx.Object.JTable.getPagingItems(jtParams, null, sortMap);
        expect(result.length).toEqual(0);
        result = AcmEx.Object.JTable.getPagingItems(jtParams, [], sortMap);
        expect(result.length).toEqual(0);
        result = AcmEx.Object.JTable.getPagingItems(jtParams, undefined, sortMap);
        expect(result.length).toEqual(0);
    });
});
