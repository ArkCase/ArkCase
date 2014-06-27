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


    it("Acm.Initialize", function() {
        spyOn(Acm.Dialog,     "initialize");
        spyOn(Acm.Dispatcher, "initialize");
        spyOn(Acm.Ajax,       "initialize");
        spyOn(Acm.Object,     "initialize");
        spyOn(Acm.Validation, "initialize");
        Acm.initialize();
        expect(Acm.Dialog    .initialize).toHaveBeenCalled();
        expect(Acm.Dispatcher.initialize).toHaveBeenCalled();
        expect(Acm.Ajax      .initialize).toHaveBeenCalled();
        expect(Acm.Object    .initialize).toHaveBeenCalled();
        expect(Acm.Validation.initialize).toHaveBeenCalled();
    });

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

    it("Test Acm.makeNoneCacheUrl() function", function() {
        expect(Acm.makeNoneCacheUrl("some.com/some/path")).toBeginWith("some.com/some/path?rand=");
        expect(Acm.makeNoneCacheUrl("some.com/some/path/")).toBeginWith("some.com/some/path/?rand=");
        expect(Acm.makeNoneCacheUrl("some.com/some/path?var=abc")).toBeginWith("some.com/some/path?var=abc&rand=");

        var first  = Acm.makeNoneCacheUrl("some.com/some/path");
        var second = Acm.makeNoneCacheUrl("some.com/some/path");
        expect(second).not.toBe(first);
    });

//    it("Test Acm.getUrlParameter() function", function() {
//        //don't the best way to test
//    });

    it("Test Acm.urlToJson() function", function() {
//        expect(Acm.urlToJson("abc='20'&xyz=5&foo='bar'&yes=true"))
//            .toEqual({abc: "20", xyz: 5, foo: "bar", yes: true});
        expect(Acm.urlToJson("abc=foo&xyz=5&foo=bar&yes=true"))
            .toEqual({abc: "foo", xyz: "5", foo: "bar", yes: "true"});

//        expect(Acm.urlToJson("abc=foo&def=%5Basf%5D&xyz=5&foo=b%3Dar"))
//            .toEqual({abc: "foo", def: "[asf]", xyz: "5", foo: "b=ar"});
        expect(Acm.urlToJson("abc=foo&def=%5Basf%5D&xyz=5&foo=bar"))
            .toEqual({abc: "foo", def: "[asf]", xyz: "5", foo: "bar"});

//        expect(Acm.urlToJson("abc=foo&def=[asf]&xyz=5&foo=bar"))
//            .toEqual({abc: "foo", def: [asf], xyz: "5", foo: "bar"});
        expect(Acm.urlToJson("abc=foo&def=[asf]&xyz=5&foo=bar"))
            .toEqual({abc: "foo", def: "[asf]", xyz: "5", foo: "bar"});
    });


});
