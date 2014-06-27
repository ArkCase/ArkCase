/**
 * Test script for Acm.Object
 *
 * @author jwu
 */
describe("Acm.Object", function()
{
    beforeEach(function() {
    });

    it("getValue/setValue", function() {
        var $s = $("<div></div>");
        expect(Acm.Object.getValue($s)).toBe("");

        Acm.Object.setValue($s, "whatever");
        expect(Acm.Object.getValue($s)).toBe("whatever");

        Acm.Object.setValue($s, null);
        expect(Acm.Object.getValue($s)).toBe("");

        Acm.Object.setValue($s, undefined);
        expect(Acm.Object.getValue($s)).toBe("");

        Acm.Object.setValue($s, "");
        expect(Acm.Object.getValue($s)).toBe("");
    });

    it("getText/setText", function() {
        var $s = $("<div></div>");
        expect(Acm.Object.getText($s)).toBe("");

        Acm.Object.setText($s, "whatever");
        expect(Acm.Object.getText($s)).toBe("whatever");

        Acm.Object.setText($s, null);
        expect(Acm.Object.getText($s)).toBe("");

        Acm.Object.setText($s, undefined);
        expect(Acm.Object.getText($s)).toBe("");

        Acm.Object.setText($s, "");
        expect(Acm.Object.getText($s)).toBe("");
    });

    it("getTextNodeText/setTextNodeText", function() {
        var $s = $("<h4>Begin<a>Middle</a>End</h4>");
        expect(Acm.Object.getTextNodeText($s)).toBe("BeginEnd");
        expect(Acm.Object.getTextNodeText($s, 0)).toBe("Begin");
        expect(Acm.Object.getTextNodeText($s, 1)).toBe("End");
        expect(Acm.Object.getTextNodeText($s, -1)).toBe("End");

        $s = $("<h4>Begin<a>Middle</a>End</h4>");
        Acm.Object.setTextNodeText($s, "First", 0);
        expect(Acm.Object.getTextNodeText($s, 0)).toBe("First");
        expect(Acm.Object.getTextNodeText($s)).toBe("FirstEnd");

        $s = $("<h4>Begin<a>Middle</a>End</h4>");
        Acm.Object.setTextNodeText($s, "Second", 1);
        expect(Acm.Object.getTextNodeText($s, 1)).toBe("Second");
        expect(Acm.Object.getTextNodeText($s)).toBe("BeginSecond");

        $s = $("<h4>Begin<a>Middle</a>End</h4>");
        Acm.Object.setTextNodeText($s, "Last", -1);
        expect(Acm.Object.getTextNodeText($s, -1)).toBe("Last");
        expect(Acm.Object.getTextNodeText($s)).toBe("BeginLast");

        $s = $("<h4>Begin<a>Middle</a>End</h4>");
        Acm.Object.setTextNodeText($s, "Zero");
        expect(Acm.Object.getTextNodeText($s, 0)).toBe("Zero");
        expect(Acm.Object.getTextNodeText($s)).toBe("ZeroEnd");

        $s = $("<h4>Begin<a>Middle</a>End</h4>");
        Acm.Object.setTextNodeText($s, null, 0);
        expect(Acm.Object.getTextNodeText($s, 0)).toBe("");
        expect(Acm.Object.getTextNodeText($s)).toBe("End");

        $s = $("<h4>Begin<a>Middle</a>End</h4>");
        Acm.Object.setTextNodeText($s, null, 1);
        expect(Acm.Object.getTextNodeText($s, 1)).toBe("");
        expect(Acm.Object.getTextNodeText($s)).toBe("Begin");
    });


    //todo: more tests
});
