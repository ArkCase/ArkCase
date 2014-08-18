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
        spyOn(Acm.Event,      "initialize");
        spyOn(Acm.Rule,       "initialize");
        Acm.initialize();
        expect(Acm.Dialog     .initialize).toHaveBeenCalled();
        expect(Acm.Dispatcher .initialize).toHaveBeenCalled();
        expect(Acm.Ajax       .initialize).toHaveBeenCalled();
        expect(Acm.Object     .initialize).toHaveBeenCalled();
        expect(Acm.Event      .initialize).toHaveBeenCalled();
        expect(Acm.Rule       .initialize).toHaveBeenCalled();
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

    it("Acm.CacheFifo: maxSize", function() {
        var cache = new Acm.CacheFifo(4);
        expect(cache.getMaxSize()).toEqual(4);

        cache.setMaxSize(1);
        expect(cache.getMaxSize()).toEqual(1);
    });

    it("Acm.CacheFifo: put/get", function() {
        var cache = new Acm.CacheFifo(3);
        expect(cache.getMaxSize()).toEqual(3);

        expect(cache.get("k1")).toEqual(null);

        cache.put("k1", {name:"n1",age:1});
        expect(cache.get("k1")).toEqual({name:"n1",age:1});

        cache.put("k2", {name:"n2",age:2});
        expect(cache.get("k1")).toEqual({name:"n1",age:1});
        expect(cache.get("k2")).toEqual({name:"n2",age:2});

        cache.put("k3", {name:"n3",age:3});
        expect(cache.get("k1")).toEqual({name:"n1",age:1});
        expect(cache.get("k2")).toEqual({name:"n2",age:2});
        expect(cache.get("k3")).toEqual({name:"n3",age:3});

        cache.put("k4", {name:"n4",age:4});
        expect(cache.get("k1")).toEqual(null);
        expect(cache.get("k2")).toEqual({name:"n2",age:2});
        expect(cache.get("k3")).toEqual({name:"n3",age:3});
        expect(cache.get("k4")).toEqual({name:"n4",age:4});

        cache.put("k5", {name:"n5",age:5});
        expect(cache.get("k1")).toEqual(null);
        expect(cache.get("k2")).toEqual(null);
        expect(cache.get("k3")).toEqual({name:"n3",age:3});
        expect(cache.get("k4")).toEqual({name:"n4",age:4});
        expect(cache.get("k5")).toEqual({name:"n5",age:5});

        cache.put("k6", {name:"n6",age:6});
        expect(cache.get("k1")).toEqual(null);
        expect(cache.get("k2")).toEqual(null);
        expect(cache.get("k3")).toEqual(null);
        expect(cache.get("k4")).toEqual({name:"n4",age:4});
        expect(cache.get("k5")).toEqual({name:"n5",age:5});
        expect(cache.get("k6")).toEqual({name:"n6",age:6});

        cache.put("k7", {name:"n7",age:7});
        expect(cache.get("k1")).toEqual(null);
        expect(cache.get("k2")).toEqual(null);
        expect(cache.get("k3")).toEqual(null);
        expect(cache.get("k4")).toEqual(null);
        expect(cache.get("k5")).toEqual({name:"n5",age:5});
        expect(cache.get("k6")).toEqual({name:"n6",age:6});
        expect(cache.get("k7")).toEqual({name:"n7",age:7});

        cache.reset();
        expect(cache.get("k1")).toEqual(null);
        expect(cache.get("k2")).toEqual(null);
        expect(cache.get("k3")).toEqual(null);
        expect(cache.get("k4")).toEqual(null);
        expect(cache.get("k5")).toEqual(null);
        expect(cache.get("k6")).toEqual(null);
        expect(cache.get("k7")).toEqual(null);

        cache.put("k8", {name:"n8",age:8});
        expect(cache.get("k1")).toEqual(null);
        expect(cache.get("k2")).toEqual(null);
        expect(cache.get("k3")).toEqual(null);
        expect(cache.get("k4")).toEqual(null);
        expect(cache.get("k5")).toEqual(null);
        expect(cache.get("k6")).toEqual(null);
        expect(cache.get("k7")).toEqual(null);
        expect(cache.get("k8")).toEqual({name:"n8",age:8});
    });

    it("Acm.CacheFifo: put as update", function() {
        var cache = new Acm.CacheFifo(3);
        expect(cache.getMaxSize()).toEqual(3);

        expect(cache.get("k1")).toEqual(null);

        cache.put("k1", {name:"n1",age:1001});
        expect(cache.get("k1")).toEqual({name:"n1",age:1001});

        cache.put("k1", {name:"n1",age:1});
        expect(cache.get("k1")).toEqual({name:"n1",age:1});

        cache.put("k2", {name:"n2",age:1002});
        expect(cache.get("k1")).toEqual({name:"n1",age:1});
        expect(cache.get("k2")).toEqual({name:"n2",age:1002});

        cache.put("k3", {name:"n3",age:1003});
        expect(cache.get("k1")).toEqual({name:"n1",age:1});
        expect(cache.get("k2")).toEqual({name:"n2",age:1002});
        expect(cache.get("k3")).toEqual({name:"n3",age:1003});

        cache.put("k2", {name:"n2",age:2});
        cache.put("k3", {name:"n3",age:3});
        expect(cache.get("k1")).toEqual({name:"n1",age:1});
        expect(cache.get("k2")).toEqual({name:"n2",age:2});
        expect(cache.get("k3")).toEqual({name:"n3",age:3});

        cache.put("k4", {name:"n4",age:1004});
        expect(cache.get("k1")).toEqual(null);
        expect(cache.get("k2")).toEqual({name:"n2",age:2});
        expect(cache.get("k3")).toEqual({name:"n3",age:3});
        expect(cache.get("k4")).toEqual({name:"n4",age:1004});

        cache.put("k4", {name:"n4",age:4});
        expect(cache.get("k1")).toEqual(null);
        expect(cache.get("k2")).toEqual({name:"n2",age:2});
        expect(cache.get("k3")).toEqual({name:"n3",age:3});
        expect(cache.get("k4")).toEqual({name:"n4",age:4});

        cache.put("k5", {name:"n5",age:1005});
        expect(cache.get("k1")).toEqual(null);
        expect(cache.get("k2")).toEqual(null);
        expect(cache.get("k3")).toEqual({name:"n3",age:3});
        expect(cache.get("k4")).toEqual({name:"n4",age:4});
        expect(cache.get("k5")).toEqual({name:"n5",age:1005});

        cache.put("k5", {name:"n5",age:5});
        expect(cache.get("k1")).toEqual(null);
        expect(cache.get("k2")).toEqual(null);
        expect(cache.get("k3")).toEqual({name:"n3",age:3});
        expect(cache.get("k4")).toEqual({name:"n4",age:4});
        expect(cache.get("k5")).toEqual({name:"n5",age:5});

        cache.reset();
        expect(cache.get("k1")).toEqual(null);
        expect(cache.get("k2")).toEqual(null);
        expect(cache.get("k3")).toEqual(null);
        expect(cache.get("k4")).toEqual(null);
        expect(cache.get("k5")).toEqual(null);
    });

    it("Acm.CacheFifo: cache size 1 = assignment", function() {
        var cache = new Acm.CacheFifo(1);
        expect(cache.getMaxSize()).toEqual(1);

        expect(cache.get(1)).toEqual(null);

        cache.put(1, {name:"n1",age:1});
        expect(cache.get(1)).toEqual({name:"n1",age:1});

        cache.put(1, {name:"n1",age:11});
        expect(cache.get(1)).toEqual({name:"n1",age:11});

        cache.put(2, {name:"n2",age:2});
        expect(cache.get(1)).toEqual(null);
        expect(cache.get(2)).toEqual({name:"n2",age:2});
    });

    it("Acm.CacheFifo: cache size 0, no exception", function() {
        var cache = new Acm.CacheFifo(0);
        expect(cache.getMaxSize()).toEqual(0);

        expect(cache.get(1)).toEqual(null);

        cache.put(1, {name:"n1",age:1});
        expect(cache.get(1)).toEqual(null);

        cache.put(2, {name:"n2",age:2});
        expect(cache.get(1)).toEqual(null);
        expect(cache.get(2)).toEqual(null);
    });

    it("Acm.CacheFifo: remove", function() {
        var cache1 = new Acm.CacheFifo(0);
        cache1.remove("nosuch");
        //no exception thrown


        var cache2 = new Acm.CacheFifo(1);
        cache2.remove("nosuch");                //no exception thrown
        cache2.put("k1", {name:"n1",age:1});
        expect(cache2.get("k1")).toEqual({name:"n1",age:1});
        cache2.remove("k1");
        expect(cache2.get("k1")).toEqual(null);
        cache2.remove("k1");                     //no effect
        expect(cache2.get("k1")).toEqual(null);
        cache2.put("k1", {name:"n1",age:1});
        expect(cache2.get("k1")).toEqual({name:"n1",age:1});


        var cache3 = new Acm.CacheFifo(3);
        cache3.put("k1", {name:"n1",age:1});
        expect(cache3.get("k1")).toEqual({name:"n1",age:1});

        cache3.remove("k1");
        expect(cache3.get("k1")).toEqual(null);

        cache3.put("k2", {name:"n2",age:2});
        cache3.put("k3", {name:"n3",age:3});
        expect(cache3.get("k1")).toEqual(null);
        expect(cache3.get("k2")).toEqual({name:"n2",age:2});
        expect(cache3.get("k3")).toEqual({name:"n3",age:3});

        cache3.remove("k3");
        expect(cache3.get("k1")).toEqual(null);
        expect(cache3.get("k2")).toEqual({name:"n2",age:2});
        expect(cache3.get("k3")).toEqual(null);


        cache3.put("k4", {name:"n4",age:4});
        cache3.put("k5", {name:"n5",age:5});
        expect(cache3.get("k1")).toEqual(null);
        expect(cache3.get("k2")).toEqual({name:"n2",age:2});
        expect(cache3.get("k3")).toEqual(null);
        expect(cache3.get("k4")).toEqual({name:"n4",age:4});
        expect(cache3.get("k5")).toEqual({name:"n5",age:5});

        cache3.remove("k5");
        expect(cache3.get("k1")).toEqual(null);
        expect(cache3.get("k2")).toEqual({name:"n2",age:2});
        expect(cache3.get("k3")).toEqual(null);
        expect(cache3.get("k4")).toEqual({name:"n4",age:4});
        expect(cache3.get("k5")).toEqual(null);

        cache3.remove("k4");
        expect(cache3.get("k1")).toEqual(null);
        expect(cache3.get("k2")).toEqual({name:"n2",age:2});
        expect(cache3.get("k3")).toEqual(null);
        expect(cache3.get("k4")).toEqual(null);
        expect(cache3.get("k5")).toEqual(null);

        cache3.remove("k2");
        expect(cache3.get("k1")).toEqual(null);
        expect(cache3.get("k2")).toEqual(null);
        expect(cache3.get("k3")).toEqual(null);
        expect(cache3.get("k4")).toEqual(null);
        expect(cache3.get("k5")).toEqual(null);

        cache3.put("k6", {name:"n6",age:6});
        cache3.put("k7", {name:"n7",age:7});
        expect(cache3.get("k1")).toEqual(null);
        expect(cache3.get("k2")).toEqual(null);
        expect(cache3.get("k3")).toEqual(null);
        expect(cache3.get("k4")).toEqual(null);
        expect(cache3.get("k5")).toEqual(null);
        expect(cache3.get("k6")).toEqual({name:"n6",age:6});
        expect(cache3.get("k7")).toEqual({name:"n7",age:7});

    });


    it("Acm.CacheFifo: no interference", function() {
        var cache = new Acm.CacheFifo(2);
        var cache2 = new Acm.CacheFifo(3);
        expect(cache.getMaxSize()).toEqual(2);
        expect(cache2.getMaxSize()).toEqual(3);

        expect(cache.get("k1")).toEqual(null);
        expect(cache2.get("k1")).toEqual(null);

        cache.put("k1", {name:"n1",age:1});
        expect(cache.get("k1")).toEqual({name:"n1",age:1});
        expect(cache2.get("k1")).toEqual(null);

        cache2.put("k1", {name:"n1",age:21});
        expect(cache.get("k1")).toEqual({name:"n1",age:1});
        expect(cache2.get("k1")).toEqual({name:"n1",age:21});

        cache.put("k2", {name:"n2",age:2});
        expect(cache.get("k1")).toEqual({name:"n1",age:1});
        expect(cache.get("k2")).toEqual({name:"n2",age:2});
        expect(cache2.get("k1")).toEqual({name:"n1",age:21});

        cache2.put("k2", {name:"n2",age:22});
        expect(cache.get("k1")).toEqual({name:"n1",age:1});
        expect(cache.get("k2")).toEqual({name:"n2",age:2});
        expect(cache2.get("k1")).toEqual({name:"n1",age:21});
        expect(cache2.get("k2")).toEqual({name:"n2",age:22});

        cache.put("k3", {name:"n3",age:3});
        expect(cache.get("k1")).toEqual(null);                 //1st cache is full
        expect(cache.get("k2")).toEqual({name:"n2",age:2});
        expect(cache.get("k3")).toEqual({name:"n3",age:3});
        expect(cache2.get("k1")).toEqual({name:"n1",age:21});
        expect(cache2.get("k2")).toEqual({name:"n2",age:22});

        cache2.put("k3", {name:"n3",age:23});
        expect(cache.get("k1")).toEqual(null);
        expect(cache.get("k2")).toEqual({name:"n2",age:2});
        expect(cache.get("k3")).toEqual({name:"n3",age:3});
        expect(cache2.get("k1")).toEqual({name:"n1",age:21});
        expect(cache2.get("k2")).toEqual({name:"n2",age:22});
        expect(cache2.get("k3")).toEqual({name:"n3",age:23});

        cache.put("k4", {name:"n4",age:4});
        cache2.put("k4", {name:"n4",age:24});
        expect(cache.get("k1")).toEqual(null);
        expect(cache.get("k2")).toEqual(null);
        expect(cache.get("k3")).toEqual({name:"n3",age:3});
        expect(cache.get("k4")).toEqual({name:"n4",age:4});
        expect(cache2.get("k1")).toEqual(null);                //2nd cache is full
        expect(cache2.get("k2")).toEqual({name:"n2",age:22});
        expect(cache2.get("k3")).toEqual({name:"n3",age:23});
        expect(cache2.get("k4")).toEqual({name:"n4",age:24});

        cache.reset();
        expect(cache.get("k1")).toEqual(null);
        expect(cache.get("k2")).toEqual(null);
        expect(cache.get("k3")).toEqual(null);
        expect(cache.get("k4")).toEqual(null);
        expect(cache2.get("k1")).toEqual(null);                //2nd cache is full
        expect(cache2.get("k2")).toEqual({name:"n2",age:22});
        expect(cache2.get("k3")).toEqual({name:"n3",age:23});
        expect(cache2.get("k4")).toEqual({name:"n4",age:24});

        cache2.reset();
        expect(cache.get("k1")).toEqual(null);
        expect(cache.get("k2")).toEqual(null);
        expect(cache.get("k3")).toEqual(null);
        expect(cache.get("k4")).toEqual(null);
        expect(cache2.get("k1")).toEqual(null);
        expect(cache2.get("k2")).toEqual(null);
        expect(cache2.get("k3")).toEqual(null);
        expect(cache2.get("k4")).toEqual(null);

        cache2.put("k5", {name:"n5",age:25});
        expect(cache.get("k1")).toEqual(null);
        expect(cache.get("k2")).toEqual(null);
        expect(cache.get("k3")).toEqual(null);
        expect(cache.get("k4")).toEqual(null);
        expect(cache2.get("k1")).toEqual(null);
        expect(cache2.get("k2")).toEqual(null);
        expect(cache2.get("k3")).toEqual(null);
        expect(cache2.get("k4")).toEqual(null);
        expect(cache2.get("k5")).toEqual({name:"n5",age:25});
    });

});
