/**
 * Test script for Acm.Dispatcher
 *
 * @author jwu
 */
describe("Acm", function()
{
    beforeEach(function() {
    });

    it("Acm.Initialize", function() {
        spyOn(Acm.Dispatcher, "initialize");
        spyOn(Acm.Ajax,       "initialize");
        spyOn(Acm.Object,     "initialize");
        spyOn(Acm.Validation, "initialize");
        Acm.initialize();
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
        expect(Acm.goodValue("",                "dont be empty")) .toBe("dont be empty");
        expect(Acm.goodValue("",                ""))             .toBe("");    //empty string really allowed
        expect(Acm.goodValue(varNotInitialized, "good value"))   .toBe("good value");

        expect(Acm.goodValue(null, "string")).toBe("string");
        expect(Acm.goodValue(null, true))    .toBe(true);
        expect(Acm.goodValue(null, false))   .toBe(false);
        expect(Acm.goodValue(null, 123))     .toBe(123);

        expect(Acm.goodValue(false, true))   .toBe(false);  //false is a valid boolean value
        expect(Acm.goodValue(0,    123))     .toBe(0);      //0 is a valid number
    });


});
