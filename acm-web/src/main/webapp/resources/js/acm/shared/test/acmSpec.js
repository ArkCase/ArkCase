/**
 * Test script for ACM.Dispatcher
 *
 * @author jwu
 */
describe("ACM", function()
{
    beforeEach(function() {
    });

    it("ACM.Initialize", function() {
        spyOn(ACM.Dispatcher, "initialize");
        spyOn(ACM.Ajax,       "initialize");
        spyOn(ACM.Object,     "initialize");
        spyOn(ACM.Validation, "initialize");
        ACM.initialize();
        expect(ACM.Dispatcher.initialize).toHaveBeenCalled();
        expect(ACM.Ajax      .initialize).toHaveBeenCalled();
        expect(ACM.Object    .initialize).toHaveBeenCalled();
        expect(ACM.Validation.initialize).toHaveBeenCalled();
    });

    it("Test ACM.isEmpty() function", function() {
        var varNotInitialized;
        var varObject = ACM;
        var varJQuery = jQuery(".dummy");

        //expect(ACM.isEmpty(varNosuch)).toBe(true);
        expect(ACM.isEmpty(varNotInitialized)).toBe(true);
        expect(ACM.isEmpty(undefined)).toBe(true);
        expect(ACM.isEmpty("")).toBe(true);
        expect(ACM.isEmpty(null)).toBe(true);
        expect(ACM.isEmpty("null")).toBe(true);

        expect(ACM.isEmpty(false)).toBe(false);
        expect(ACM.isEmpty(0)).toBe(false);
        expect(ACM.isEmpty("someEvent")).toBe(false);
        expect(ACM.isEmpty(varObject)).toBe(false);
        expect(ACM.isEmpty(varJQuery)).toBe(false);
    });

    it("Test ACM.isNotEmpty() function", function() {
        var varNotInitialized;
        var varObject = ACM;
        var varJQuery = jQuery(".dummy");

        expect(ACM.isNotEmpty(varNotInitialized)).toBe(false);
        expect(ACM.isNotEmpty(undefined)).toBe(false);
        expect(ACM.isNotEmpty("")).toBe(false);
        expect(ACM.isNotEmpty(null)).toBe(false);
        expect(ACM.isNotEmpty("null")).toBe(false);

        expect(ACM.isNotEmpty(false)).toBe(true);
        expect(ACM.isNotEmpty(0)).toBe(true);
        expect(ACM.isNotEmpty("some string")).toBe(true);
        expect(ACM.isNotEmpty(varObject)).toBe(true);
        expect(ACM.isNotEmpty(varJQuery)).toBe(true);
    });

    it("Test ACM.goodValue() function", function() {
        var varNotInitialized;
        expect(ACM.goodValue("some value")).toBe("some value");
        expect(ACM.goodValue("some value",      "should not be")).toBe("some value");
        expect(ACM.goodValue("",                "dont be empty")) .toBe("dont be empty");
        expect(ACM.goodValue("",                ""))             .toBe("");    //empty string really allowed
        expect(ACM.goodValue(varNotInitialized, "good value"))   .toBe("good value");

        expect(ACM.goodValue(null, "string")).toBe("string");
        expect(ACM.goodValue(null, true))    .toBe(true);
        expect(ACM.goodValue(null, false))   .toBe(false);
        expect(ACM.goodValue(null, 123))     .toBe(123);

        expect(ACM.goodValue(false, true))   .toBe(false);  //false is a valid boolean value
        expect(ACM.goodValue(0,    123))     .toBe(0);      //0 is a valid number
    });


});
