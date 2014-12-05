/**
 * Test script for AcmEx
 *
 * @author jwu
 */
describe("AcmEx", function()
{
    beforeEach(function() {
    });

    it("Fancy tree Builder stack", function() {
        var builder = AcmEx.FancyTreeBuilder;
        expect(builder._depth).toEqual(0);
        expect(builder._peekDepth()).toEqual(null);
        expect(builder._popDepth()).toEqual(null);

        //popping empty stack remains empty
        expect(builder._depth).toEqual(0);
        expect(builder._peekDepth()).toEqual(null);
        expect(builder._popDepth()).toEqual(null);

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
        expect(builder._popDepth()).toEqual(null);

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
        var builder = AcmEx.FancyTreeBuilder;
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

    //Same test as above using makeLast()
    it("Fancy tree Builder using makeLast", function() {
        var builder = AcmEx.FancyTreeBuilder;
        expect(builder.getTree()).toEqual([]);

        expect(builder.reset().getTree()).toEqual([]);

        expect(builder.addBranch({"key":1})
            .getTree()).toEqual([{"key":1}]);

        expect(builder.addLeaf({"key":11})
            .getTree()).toEqual([{"key":1, children:[{"key":11}]}]);

        expect(builder.addBranch({"key":12})
            .getTree()).toEqual([{"key":1, children:[{"key":11}, {"key":12}]}]);

        expect(builder.addBranch({"key":121})
            .getTree()).toEqual([{"key":1, children:[{"key":11}, {"key":12, children:[{"key":121}]}]}]);

        expect(builder.addBranch({"key":1211})
            .getTree()).toEqual([{"key":1, children:[{"key":11}, {"key":12, children:[{"key":121, children:[{"key":1211}]}]}]}]);

        expect(builder.addLeaf({"key":12111})
            .addLeaf({"key":12112})
            .getTree()).toEqual([{"key":1, children:[{"key":11}, {"key":12, children:[{"key":121, children:[{"key":1211, children:[{"key":12111}, {"key":12112}]}]}]}]}]);

        expect(builder.addLeaf({"key":12113})
            .getTree()).toEqual([{"key":1, children:[{"key":11}, {"key":12, children:[{"key":121, children:[{"key":1211, children:[{"key":12111}, {"key":12112}, {"key":12113}]}]}]}]}]);

        builder.makeLast(); //12113
        builder.makeLast(); //1211

        expect(builder.addLeaf({"key":122})
            .getTree()).toEqual([
                {"key":1, children:[{"key":11}
                    ,{"key":12, children:[{"key":121, children:[{"key":1211, children:[{"key":12111}, {"key":12112}, {"key":12113}]}]}
                        ,{"key":122}
                    ]}]}
            ]);

        builder.makeLast(); //122
        builder.makeLast(); //12

        expect(builder.addLeaf({"key":2})
            .getTree()).toEqual([
                {"key":1, children:[{"key":11}
                    ,{"key":12, children:[
                        {"key":121, children:[{"key":1211, children:[{"key":12111}, {"key":12112}, {"key":12113}]}]}
                        ,{"key":122}
                    ]}]}
                ,{"key":2}
            ]);

        builder.makeLast(); //2

        expect(builder.addBranch({"key":3})
            .getTree()).toEqual([
                {"key":1, children:[{"key":11}
                    ,{"key":12, children:[
                        {"key":121, children:[{"key":1211, children:[{"key":12111}, {"key":12112}, {"key":12113}]}]}
                        ,{"key":122}
                    ]}]}
                ,{"key":2}
                ,{"key":3}
            ]);

        expect(builder.addBranch({"key":31})
            .addLeaf({"key":311})
            .getTree()).toEqual([
                {"key":1, children:[{"key":11}
                    ,{"key":12, children:[
                        {"key":121, children:[{"key":1211, children:[{"key":12111}, {"key":12112}, {"key":12113}]}]}
                        ,{"key":122}
                    ]}]}
                ,{"key":2}
                ,{"key":3, children:[{"key":31, children:[{"key":311}]}]}
            ]);

        builder.makeLast(); //311
        builder.makeLast(); //31
        builder.makeLast(); //3

        expect(builder.reset().getTree()).toEqual([]);
    });

    //Same test as above with mix of addXxxLast and makeLast()
    it("Fancy tree Builder with mix of addXxxLast and makeLast", function() {
        var builder = AcmEx.FancyTreeBuilder;
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

        expect(builder.addLeaf({"key":12113})
            .getTree()).toEqual([{"key":1, children:[{"key":11}, {"key":12, children:[{"key":121, children:[{"key":1211, children:[{"key":12111}, {"key":12112}, {"key":12113}]}]}]}]}]);

        builder.makeLast(); //12113
        //builder.makeLast(); //1211

        expect(builder.addLeaf({"key":122})
            .getTree()).toEqual([
                {"key":1, children:[{"key":11}
                    ,{"key":12, children:[{"key":121, children:[{"key":1211, children:[{"key":12111}, {"key":12112}, {"key":12113}]}]}
                        ,{"key":122}
                    ]}]}
            ]);

        builder.makeLast(); //122
        //builder.makeLast(); //12

        expect(builder.addLeaf({"key":2})
            .getTree()).toEqual([
                {"key":1, children:[{"key":11}
                    ,{"key":12, children:[
                        {"key":121, children:[{"key":1211, children:[{"key":12111}, {"key":12112}, {"key":12113}]}]}
                        ,{"key":122}
                    ]}]}
                ,{"key":2}
            ]);

        builder.makeLast(); //2

        expect(builder.addBranch({"key":3})
            .getTree()).toEqual([
                {"key":1, children:[{"key":11}
                    ,{"key":12, children:[
                        {"key":121, children:[{"key":1211, children:[{"key":12111}, {"key":12112}, {"key":12113}]}]}
                        ,{"key":122}
                    ]}]}
                ,{"key":2}
                ,{"key":3}
            ]);

        expect(builder.addBranch({"key":31})
            .addLeaf({"key":311})
            .getTree()).toEqual([
                {"key":1, children:[{"key":11}
                    ,{"key":12, children:[
                        {"key":121, children:[{"key":1211, children:[{"key":12111}, {"key":12112}, {"key":12113}]}]}
                        ,{"key":122}
                    ]}]}
                ,{"key":2}
                ,{"key":3, children:[{"key":31, children:[{"key":311}]}]}
            ]);

        builder.makeLast(); //311
        builder.makeLast(); //31
        builder.makeLast(); //3

        expect(builder.reset().getTree()).toEqual([]);
    });

});
