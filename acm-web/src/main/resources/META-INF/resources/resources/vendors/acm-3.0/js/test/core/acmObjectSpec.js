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


    it("Fancy tree Builder stack", function() {
        var builder = Acm.Object.FancyTreeBuilder;
        expect(builder._depth).toEqual(0);
        expect(builder._peekDepth()).toEqual(null);
        expect(builder._popDepth()).toEqual([]);

        //popping empty stack remains empty
        expect(builder._depth).toEqual(0);
        expect(builder._peekDepth()).toEqual(null);
        expect(builder._popDepth()).toEqual([]);

        //
        //push 3 items, and pop 3 items
        //
        builder._pushDepth({"some1": "thing1"});
        expect(builder._depth).toEqual(1);

        builder._pushDepth({"some2": "thing2"});
        expect(builder._depth).toEqual(2);

        builder._pushDepth({"some3": "thing3"});
        expect(builder._depth).toEqual(3);

        expect(builder._peekDepth()).toEqual({"some3": "thing3"});
        expect(builder._popDepth()) .toEqual({"some3": "thing3"});
        expect(builder._peekDepth()).toEqual({"some2": "thing2"});
        expect(builder._popDepth()) .toEqual({"some2": "thing2"});
        expect(builder._peekDepth()).toEqual({"some1": "thing1"});
        expect(builder._popDepth()) .toEqual({"some1": "thing1"});
        expect(builder._peekDepth()).toEqual(null);

        //should be empty at this point
        expect(builder._depth).toEqual(0);
        expect(builder._popDepth()).toEqual([]);

        //
        //mix of push and pop should work
        //
        builder._pushDepth({"key": 101, "name": "name1"});
        expect(builder._depth).toEqual(1);

        builder._pushDepth({"key": 102, "name": "name2"});
        expect(builder._depth).toEqual(2);

        expect(builder._popDepth()).toEqual({"key": 102, "name": "name2"});

        builder._pushDepth({"key": 108, "name": "name8"});
        expect(builder._depth).toEqual(2);

        builder._pushDepth({"key": 109, "name": "name9"});
        expect(builder._depth).toEqual(3);

        expect(builder._peekDepth()).toEqual({"key": 109, "name": "name9"});
        expect(builder._popDepth()) .toEqual({"key": 109, "name": "name9"});
        expect(builder._peekDepth()).toEqual({"key": 108, "name": "name8"});
        expect(builder._popDepth()) .toEqual({"key": 108, "name": "name8"});
        expect(builder._peekDepth()).toEqual({"key": 101, "name": "name1"});
        expect(builder._popDepth()) .toEqual({"key": 101, "name": "name1"});

        //pop all, back to empty
        expect(builder._depth).toEqual(0);
        expect(builder._peekDepth()).toEqual(null);
    });

    //
    // This test builds a tree (forest):
    //  __1
    // |  |__11
    // |  |__12
    // |      |__121
    // |      |   |__1211
    // |      |        |_12111
    // |      |        |_12112
    // |      |        |_12113
    // |      |__122
    // |
    // |__2
    // |
    // |__3
    //   |__31
    //       |__311
    //
    it("Fancy tree Builder", function() {
        var builder = Acm.Object.FancyTreeBuilder;
        expect(builder.getTree()).toEqual([]);

        expect(builder.reset().getTree()).toEqual([]);

        expect(builder.addBranch({"key":1})
            .getTree()).toEqual([{"key":1}]);

        expect(builder.addLeaf({"key":11})
            .getTree()).toEqual([{"key":1, children:[{"key":11}]}]);

        expect(builder.addBranchLast({"key":12})
            .getTree()).toEqual([{"key":1, children:[{"key":11}, {"key":12}]}]);

        expect(builder.addBranch({"key":121})
            .getTree()).toEqual([{"key":1, children:[{"key":11}, {"key":12, children:[{"key":121}]}]}]);

        expect(builder.addBranchLast({"key":1211})
            .getTree()).toEqual([{"key":1, children:[{"key":11}, {"key":12, children:[{"key":121, children:[{"key":1211}]}]}]}]);

        expect(builder.addLeaf({"key":12111})
            .addLeaf({"key":12112})
            .getTree()).toEqual([{"key":1, children:[{"key":11}, {"key":12, children:[{"key":121, children:[{"key":1211, children:[{"key":12111}, {"key":12112}]}]}]}]}]);

        expect(builder.addLeafLast({"key":12113})
            .getTree()).toEqual([{"key":1, children:[{"key":11}, {"key":12, children:[{"key":121, children:[{"key":1211, children:[{"key":12111}, {"key":12112}, {"key":12113}]}]}]}]}]);

        expect(builder.addLeafLast({"key":122})
            .getTree()).toEqual([
                {"key":1, children:[{"key":11}
                    ,{"key":12, children:[{"key":121, children:[{"key":1211, children:[{"key":12111}, {"key":12112}, {"key":12113}]}]}
                        ,{"key":122}
                    ]}]}
            ]);

        expect(builder.addLeaf({"key":2})
            .getTree()).toEqual([
                {"key":1, children:[{"key":11}
                    ,{"key":12, children:[
                        {"key":121, children:[{"key":1211, children:[{"key":12111}, {"key":12112}, {"key":12113}]}]}
                        ,{"key":122}
                    ]}]}
                ,{"key":2}
            ]);

        expect(builder.addBranchLast({"key":3})
            .getTree()).toEqual([
                {"key":1, children:[{"key":11}
                    ,{"key":12, children:[
                        {"key":121, children:[{"key":1211, children:[{"key":12111}, {"key":12112}, {"key":12113}]}]}
                        ,{"key":122}
                    ]}]}
                ,{"key":2}
                ,{"key":3}
            ]);

        expect(builder.addBranchLast({"key":31})
            .addLeafLast({"key":311})
            .getTree()).toEqual([
                {"key":1, children:[{"key":11}
                    ,{"key":12, children:[
                        {"key":121, children:[{"key":1211, children:[{"key":12111}, {"key":12112}, {"key":12113}]}]}
                        ,{"key":122}
                    ]}]}
                ,{"key":2}
                ,{"key":3, children:[{"key":31, children:[{"key":311}]}]}
            ]);

        expect(builder.reset().getTree()).toEqual([]);
    });


});
