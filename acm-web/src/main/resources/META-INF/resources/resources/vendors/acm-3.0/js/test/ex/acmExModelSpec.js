/**
 * Test script for AcmEx
 *
 * @author jwu
 */
describe("AcmExModel", function()
{

    beforeEach(function() {
    });

    it("jTable: parse jtParam: abnormal test", function() {
        var jtParams;
        var pagingParam;

        jtParams = {
            jtPageSize: 10
            ,jtStartIndex: 0
        };
        pagingParam = AcmEx.Model.JTable._getPagingParam(jtParams);
        expect(pagingParam.pageStart).toEqual(0);
        expect(pagingParam.pageSize).toEqual(10);
        expect(pagingParam.sortBy).toEqual("");
        expect(pagingParam.sortDir).toEqual("");

        jtParams = {
            jtPageSize: 8
            ,jtStartIndex: 3
        };
        pagingParam = AcmEx.Model.JTable._getPagingParam(jtParams);
        expect(pagingParam.pageStart).toEqual(3);
        expect(pagingParam.pageSize).toEqual(8);
        expect(pagingParam.sortBy).toEqual("");
        expect(pagingParam.sortDir).toEqual("");

        jtParams = {
            jtPageSize: 10
            ,jtSorting: "type ASC"
            ,jtStartIndex: 0
        };
        pagingParam = AcmEx.Model.JTable._getPagingParam(jtParams);
        expect(pagingParam.pageStart).toEqual(0);
        expect(pagingParam.pageSize).toEqual(10);
        expect(pagingParam.sortBy).toEqual("type");
        expect(pagingParam.sortDir).toEqual("ASC");
    });

    it("jTable: parse jtParam: abnormal test", function() {
        var jtParams;
        var pagingParam;

        jtParams = {
            jtPageSize: 10
            ,jtSorting: "type"       //direction missing
            ,jtStartIndex: 0
        };
        pagingParam = AcmEx.Model.JTable._getPagingParam(jtParams);
        expect(pagingParam.pageStart).toEqual(0);
        expect(pagingParam.pageSize).toEqual(10);
        expect(pagingParam.sortBy).toEqual("");
        expect(pagingParam.sortDir).toEqual("");

        jtParams = {
            jtSorting: "type ASC"
        };
        pagingParam = AcmEx.Model.JTable._getPagingParam(jtParams);
        expect(pagingParam.pageStart).toEqual(0);
        expect(pagingParam.pageSize).toEqual(0);
        expect(pagingParam.sortBy).toEqual("type");
        expect(pagingParam.sortDir).toEqual("ASC");

        jtParams = {};
        pagingParam = AcmEx.Model.JTable._getPagingParam(jtParams);
        expect(pagingParam.pageStart).toEqual(0);
        expect(pagingParam.pageSize).toEqual(0);
        expect(pagingParam.sortBy).toEqual("");
        expect(pagingParam.sortDir).toEqual("");

        jtParams = null;
        pagingParam = AcmEx.Model.JTable._getPagingParam(jtParams);
        expect(pagingParam.pageStart).toEqual(0);
        expect(pagingParam.pageSize).toEqual(0);
        expect(pagingParam.sortBy).toEqual("");
        expect(pagingParam.sortDir).toEqual("");
    });

});
