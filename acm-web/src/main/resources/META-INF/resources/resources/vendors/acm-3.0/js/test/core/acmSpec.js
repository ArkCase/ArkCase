/**
 * Test script for Acm.Dispatcher
 *
 * @author jwu
 */
describe("Acm", function()
{
    beforeEach(function() {
        jasmine.addMatchers({
            toBeginWith: function() {
                return {
                    compare: function(actual, expected) {
                        return {
                            pass: actual.substring(0, expected.length) == expected
                        };
                    }
                }; //outer return
            }
        });
    });


//    it("Acm.create", function() {
//        spyOn(Acm.Dialog,     "create");
//        spyOn(Acm.Dispatcher, "create");
//        spyOn(Acm.Ajax,       "create");
//        spyOn(Acm.Object,     "create");
//        spyOn(Acm.Event,      "create");
//        spyOn(Acm.Rule,       "create");
//        spyOn(Acm.Model,      "create");
//        Acm.create();
//        expect(Acm.Dialog     .create).toHaveBeenCalled();
//        expect(Acm.Dispatcher .create).toHaveBeenCalled();
//        expect(Acm.Ajax       .create).toHaveBeenCalled();
//        expect(Acm.Object     .create).toHaveBeenCalled();
//        expect(Acm.Event      .create).toHaveBeenCalled();
//        expect(Acm.Rule       .create).toHaveBeenCalled();
//        expect(Acm.Model      .create).toHaveBeenCalled();
//    });

    it("Test Acm.isEmpty() function", function() {
        var varNotInitialized;
        var varObject = Acm;
        var varJQuery = jQuery(".dummy");

        //expect(Acm.isEmpty(varNosuch)).toBe(true);
        expect(Acm.isEmpty(varNotInitialized)).toBe(true);
        expect(Acm.isEmpty(undefined)).toBe(true);
        expect(Acm.isEmpty("")).toBe(true);
        expect(Acm.isEmpty(null)).toBe(true);
        expect(Acm.isEmpty("null")).toBe(true);

        expect(Acm.isEmpty(false)).toBe(false);
        expect(Acm.isEmpty(0)).toBe(false);
        expect(Acm.isEmpty("someEvent")).toBe(false);
        expect(Acm.isEmpty(varObject)).toBe(false);
        expect(Acm.isEmpty(varJQuery)).toBe(false);
    });

    it("Test Acm.isNotEmpty() function", function() {
        var varNotInitialized;
        var varObject = Acm;
        var varJQuery = jQuery(".dummy");

        expect(Acm.isNotEmpty(varNotInitialized)).toBe(false);
        expect(Acm.isNotEmpty(undefined)).toBe(false);
        expect(Acm.isNotEmpty("")).toBe(false);
        expect(Acm.isNotEmpty(null)).toBe(false);
        expect(Acm.isNotEmpty("null")).toBe(false);

        expect(Acm.isNotEmpty(false)).toBe(true);
        expect(Acm.isNotEmpty(0)).toBe(true);
        expect(Acm.isNotEmpty("some string")).toBe(true);
        expect(Acm.isNotEmpty(varObject)).toBe(true);
        expect(Acm.isNotEmpty(varJQuery)).toBe(true);
    });

    it("Test Acm.goodValue() function", function() {
        var varNotInitialized;
        expect(Acm.goodValue("some value")).toBe("some value");
        expect(Acm.goodValue("some value",      "should not be")).toBe("some value");
        expect(Acm.goodValue("",                "dont be empty")).toBe("dont be empty");
        expect(Acm.goodValue("",                ""))             .toBe("");    //empty string really allowed
        expect(Acm.goodValue(varNotInitialized, "good value"))   .toBe("good value");

        expect(Acm.goodValue(null, "string")).toBe("string");
        expect(Acm.goodValue(null, true))    .toBe(true);
        expect(Acm.goodValue(null, false))   .toBe(false);
        expect(Acm.goodValue(null, 123))     .toBe(123);

        expect(Acm.goodValue(false, true))   .toBe(false);  //false is a valid boolean value
        expect(Acm.goodValue(0,    123))     .toBe(0);      //0 is a valid number
    });

    xit("Test Acm.goodValue() with array", function() {
        var good = {name:"John", child:{name:"Charlie"}};
        var bad1 = {name:"John", child:{}};
        var bad2 = {name:"John"};

        expect(Acm.goodValue([good, "child", "name"])).toBe("Charlie");
        expect(Acm.goodValue([bad1, "child", "name"])).toBe("");
        expect(Acm.goodValue([bad2, "child", "name"], "BadValue")).toBe("BadValue");

    });

    it("Test Acm.makeNoneCacheUrl() function", function() {
        expect(Acm.makeNoneCacheUrl("some.com/some/path")).toBeginWith("some.com/some/path?rand=");
        expect(Acm.makeNoneCacheUrl("some.com/some/path/")).toBeginWith("some.com/some/path/?rand=");
        expect(Acm.makeNoneCacheUrl("some.com/some/path?var=abc")).toBeginWith("some.com/some/path?var=abc&rand=");

        var first  = Acm.makeNoneCacheUrl("some.com/some/path");
        var second = Acm.makeNoneCacheUrl("some.com/some/path");
        expect(second).not.toBe(first);
    });

//    it("Test Acm.getUrlParameter() function", function() {
//        //don't have a good way to test
//    });

    it("Test Acm.urlToJson() function", function() {
        expect(Acm.urlToJson("abc=foo&xyz=5&foo=bar&yes=true"))
            .toEqual({abc: "foo", xyz: "5", foo: "bar", yes: "true"});

        expect(Acm.urlToJson("abc=foo&def=%5Basf%5D&xyz=5&foo=bar"))
            .toEqual({abc: "foo", def: "[asf]", xyz: "5", foo: "bar"});

        expect(Acm.urlToJson("abc=foo&def=[asf]&xyz=5&foo=bar"))
            .toEqual({abc: "foo", def: "[asf]", xyz: "5", foo: "bar"});
    });


    it("Test Acm.equals() function", function() {
        var obj1 = {id:123, name:"Joe"};
        var obj2 = {name:"Joe", id:123};
        var obj3 = {id:123, name:"Joe", sex:'M'};

        expect(Acm.equals(obj1, obj2)).toEqual(true);
        expect(Acm.equals(obj1, obj3)).toEqual(false);

        expect(Acm.equals(obj1, null)).toEqual(false);
        expect(Acm.equals(null, obj1)).toEqual(false);
        expect(Acm.equals(null, null)).toEqual(true);

        expect(Acm.equals({}, {})).toEqual(true);
        expect(Acm.equals({}, null)).toEqual(false);
        expect(Acm.equals(null, {})).toEqual(false);

//array not working yet
//        var arr1 = [{id:100, name:"Joe"},  {id:200, name:"Mary"}, {id:300, name:"Kate"}];
//        var arr2 = [{id:300, name:"Kate"}, {id:100, name:"Joe"},  {id:200, name:"Mary"}];
//        var arr3 = [{id:123, name:"Joe"},  {id:200, name:"Mary"}, {id:300, name:"Kate"}];
//        var arr4 = [{id:100, name:"Joe"},  {id:200, name:"Mary"}];
//        var arr5 = [{id:100, name:"Joe"},  {id:200, name:"Mary"}, {id:300, name:"Kate"}, {id:400, name:"Kate"}];
//
//        expect(Acm.equals(arr1, arr2)).toEqual(true);
//        expect(Acm.equals(arr1, arr3)).toEqual(false);
//        expect(Acm.equals(arr1, arr4)).toEqual(false);
//        expect(Acm.equals(arr1, arr5)).toEqual(false);
    });


    it("Test Acm.Timer: listener", function() {
        var listenerName1 = "listener1";
        var listenerName2 = "listener2";
        var triggeredEventCount1 = 0;
        var triggeredEventCount2 = 0;

        Acm.Timer.registerListener(listenerName1
            ,3
            ,function() {
                triggeredEventCount1++;
                return false;
            }
        );
        Acm.Timer.registerListener(listenerName2
            ,2
            ,function() {
                triggeredEventCount2++;
                return true;
            }
        );

        //two listeners should have countDown 2 and 1
        Acm.Timer.triggerEvent();
        expect(triggeredEventCount1).toEqual(0);
        expect(triggeredEventCount2).toEqual(0);

        //listener1 should have countDown 1;
        //listener2 has 0, event triggered and set back to 2
        Acm.Timer.triggerEvent();
        expect(triggeredEventCount1).toEqual(0);
        expect(triggeredEventCount2).toEqual(1);

        //listener1 countDown reaches 0, event triggered and remove from listener list;
        //listener2 countDown to 1, no event
        Acm.Timer.triggerEvent();
        expect(triggeredEventCount1).toEqual(1);
        expect(triggeredEventCount2).toEqual(1);

        //listener1 is gone, no event
        //listener2 countDown to 0, event triggered again and every 2 triggers
        Acm.Timer.triggerEvent();
        expect(triggeredEventCount1).toEqual(1);
        expect(triggeredEventCount2).toEqual(2);

        Acm.Timer.triggerEvent();
        expect(triggeredEventCount1).toEqual(1);
        expect(triggeredEventCount2).toEqual(2);

        Acm.Timer.triggerEvent();
        expect(triggeredEventCount1).toEqual(1);
        expect(triggeredEventCount2).toEqual(3);

        Acm.Timer.triggerEvent();
        expect(triggeredEventCount1).toEqual(1);
        expect(triggeredEventCount2).toEqual(3);

        Acm.Timer.triggerEvent();
        expect(triggeredEventCount1).toEqual(1);
        expect(triggeredEventCount2).toEqual(4);

        //remove listener2, no more events expected after this
        Acm.Timer.removeListener(listenerName2);
        Acm.Timer.triggerEvent();
        expect(triggeredEventCount1).toEqual(1);
        expect(triggeredEventCount2).toEqual(4);

        Acm.Timer.triggerEvent();
        expect(triggeredEventCount1).toEqual(1);
        expect(triggeredEventCount2).toEqual(4);

        Acm.Timer.triggerEvent();
        expect(triggeredEventCount1).toEqual(1);
        expect(triggeredEventCount2).toEqual(4);
    });

    //same test but with shared callback function
    it("Test Acm.Timer: listener share callback", function() {
        var listenerName1 = "listener1";
        var listenerName2 = "listener2";
        var triggeredEventCount1 = 0;
        var triggeredEventCount2 = 0;

        var callback = function(name) {
            if (name == listenerName1) {
                triggeredEventCount1++;
                return false;
            } else if (name == listenerName2) {
                triggeredEventCount2++;
                return true;
            }
            return false;
        }
        Acm.Timer.registerListener(listenerName1
            ,3
            ,callback
        );
        Acm.Timer.registerListener(listenerName2
            ,2
            ,callback
        );

        //two listeners should have countDown 2 and 1
        Acm.Timer.triggerEvent();
        expect(triggeredEventCount1).toEqual(0);
        expect(triggeredEventCount2).toEqual(0);

        //listener1 should have countDown 1;
        //listener2 has 0, event triggered and set back to 2
        Acm.Timer.triggerEvent();
        expect(triggeredEventCount1).toEqual(0);
        expect(triggeredEventCount2).toEqual(1);

        //listener1 countDown reaches 0, event triggered and remove from listener list;
        //listener2 countDown to 1, no event
        Acm.Timer.triggerEvent();
        expect(triggeredEventCount1).toEqual(1);
        expect(triggeredEventCount2).toEqual(1);

        //listener1 is gone, no event
        //listener2 countDown to 0, event triggered again and every 2 triggers
        Acm.Timer.triggerEvent();
        expect(triggeredEventCount1).toEqual(1);
        expect(triggeredEventCount2).toEqual(2);

        Acm.Timer.triggerEvent();
        expect(triggeredEventCount1).toEqual(1);
        expect(triggeredEventCount2).toEqual(2);

        Acm.Timer.triggerEvent();
        expect(triggeredEventCount1).toEqual(1);
        expect(triggeredEventCount2).toEqual(3);

        Acm.Timer.triggerEvent();
        expect(triggeredEventCount1).toEqual(1);
        expect(triggeredEventCount2).toEqual(3);

        Acm.Timer.triggerEvent();
        expect(triggeredEventCount1).toEqual(1);
        expect(triggeredEventCount2).toEqual(4);

        //remove listener2, no more events expected after this
        Acm.Timer.removeListener(listenerName2);
        Acm.Timer.triggerEvent();
        expect(triggeredEventCount1).toEqual(1);
        expect(triggeredEventCount2).toEqual(4);

        Acm.Timer.triggerEvent();
        expect(triggeredEventCount1).toEqual(1);
        expect(triggeredEventCount2).toEqual(4);

        Acm.Timer.triggerEvent();
        expect(triggeredEventCount1).toEqual(1);
        expect(triggeredEventCount2).toEqual(4);
    });

});
