/**
 * Test script for Acm.Model
 *
 * @author jwu
 */
describe("Acm.Model", function()
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

    it("Acm.Model.SessionData: get/set", function() {
        var data = new Acm.Model.SessionData("someData");

        data.set("A string");
        expect(data.get()).toEqual("A string");

        data.set(123);
        expect(data.get()).toEqual(123);

        data.set(null);
        expect(data.get()).toEqual(null);

        data.set({id:123, name:"Joe"});
        expect(data.get()).toEqual({id:123, name:"Joe"});
    });

    it("Acm.Model.SessionData: shared by name", function() {
        var data1 = new Acm.Model.SessionData("shareData");
        var data2 = new Acm.Model.SessionData("shareData");
        var dataA = new Acm.Model.SessionData("otherData");

        data1.set("something");
        expect(data1.get()).toEqual("something");
        expect(data2.get()).toEqual("something");

        data2.set("another");
        dataA.set("different thing");
        expect(data1.get()).toEqual("another");
        expect(data2.get()).toEqual("another");
        expect(dataA.get()).toEqual("different thing");
    });

    it("Acm.Model.CacheFifo: maxSize", function() {
        var cache = new Acm.Model.CacheFifo(4);
        expect(cache.getMaxSize()).toEqual(4);

        cache.setMaxSize(1);
        expect(cache.getMaxSize()).toEqual(1);
    });

    it("Acm.Model.CacheFifo: put/get", function() {
        var cache = new Acm.Model.CacheFifo(3);
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

    it("Acm.Model.CacheFifo: put as update", function() {
        var cache = new Acm.Model.CacheFifo(3);
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

    it("Acm.Model.CacheFifo: cache size 1 = assignment", function() {
        var cache = new Acm.Model.CacheFifo(1);
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

    it("Acm.Model.CacheFifo: cache size 0, no exception", function() {
        var cache = new Acm.Model.CacheFifo(0);
        expect(cache.getMaxSize()).toEqual(0);

        expect(cache.get(1)).toEqual(null);

        cache.put(1, {name:"n1",age:1});
        expect(cache.get(1)).toEqual(null);

        cache.put(2, {name:"n2",age:2});
        expect(cache.get(1)).toEqual(null);
        expect(cache.get(2)).toEqual(null);
    });

    it("Acm.Model.CacheFifo: remove", function() {
        var cache1 = new Acm.Model.CacheFifo(0);
        cache1.remove("nosuch");
        //no exception thrown


        var cache2 = new Acm.Model.CacheFifo(1);
        cache2.remove("nosuch");                //no exception thrown
        cache2.put("k1", {name:"n1",age:1});
        expect(cache2.get("k1")).toEqual({name:"n1",age:1});
        cache2.remove("k1");
        expect(cache2.get("k1")).toEqual(null);
        cache2.remove("k1");                     //no effect
        expect(cache2.get("k1")).toEqual(null);
        cache2.put("k1", {name:"n1",age:1});
        expect(cache2.get("k1")).toEqual({name:"n1",age:1});


        var cache3 = new Acm.Model.CacheFifo(3);
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

    it("Acm.Model.CacheFifo: no interference", function() {
        var cache = new Acm.Model.CacheFifo(2);
        var cache2 = new Acm.Model.CacheFifo(3);
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


    it("Acm.Model.CacheFifo: lock", function() {
        var cache = new Acm.Model.CacheFifo(3);
        cache.put("k1", {name:"n1",age:1});
        cache.put("k2", {name:"n2",age:2});
        cache.put("k3", {name:"n3",age:3});
        expect(cache.get("k1")).toEqual({name:"n1",age:1});
        expect(cache.get("k2")).toEqual({name:"n2",age:2});
        expect(cache.get("k3")).toEqual({name:"n3",age:3});

        cache.lock("k1");
        cache.put("k4", {name:"n4",age:4});
        expect(cache.get("k1")).toEqual({name:"n1",age:1}); //k1 survives
        expect(cache.get("k2")).toEqual(null);
        expect(cache.get("k3")).toEqual({name:"n3",age:3});
        expect(cache.get("k4")).toEqual({name:"n4",age:4});

        cache.put("k5", {name:"n5",age:5});
        expect(cache.get("k1")).toEqual({name:"n1",age:1});
        expect(cache.get("k2")).toEqual(null);
        expect(cache.get("k3")).toEqual(null);
        expect(cache.get("k4")).toEqual({name:"n4",age:4});
        expect(cache.get("k5")).toEqual({name:"n5",age:5});

        cache.unlock("k1");
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
    });
});
