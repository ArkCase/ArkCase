/**
 * Test script for DocTree.View
 *
 * @author jwu
 */

describe("DocTree.View", function() {
    beforeEach(function() {
    });

    it("DocTree.View: _getNameOrig()", function() {
        expect(DocTree.View._getNameOrig(null)).toEqual(null);
        expect(DocTree.View._getNameOrig("")).toEqual("");
        expect(DocTree.View._getNameOrig("some name")).toEqual("some name");
        expect(DocTree.View._getNameOrig("with ext_timestamp.txt")).toEqual("with ext.txt");
        expect(DocTree.View._getNameOrig("end dot_timestamp.")).toEqual("end dot.");
        expect(DocTree.View._getNameOrig("no ext_timestamp")).toEqual("no ext");

        expect(DocTree.View._getNameOrig("with_underscore_timestamp.txt")).toEqual("with_underscore.txt");
        expect(DocTree.View._getNameOrig("many.dot_timestamp.txt")).toEqual("many.dot.txt");
        expect(DocTree.View._getNameOrig("many.dot_timestamp.")).toEqual("many.dot.");
        expect(DocTree.View._getNameOrig("many.dot_timestamp")).toEqual("many.dot");

    });


});
