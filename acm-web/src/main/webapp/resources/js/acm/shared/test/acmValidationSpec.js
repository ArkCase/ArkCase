/**
 * Test script for Acm.Validation
 *
 * @author jwu
 */
describe("Acm.Validation", function()
{
    beforeEach(function() {
    });

    it("Check isAlpha", function() {
        expect(Acm.Validation.isAlpha("a")).toBe(true);
        expect(Acm.Validation.isAlpha("A")).toBe(true);
        expect(Acm.Validation.isAlpha("abc")).toBe(true);
        expect(Acm.Validation.isAlpha("123")).toBe(false);
        expect(Acm.Validation.isAlpha("h2")).toBe(false);
        expect(Acm.Validation.isAlpha("hello world")).toBe(false);
        expect(Acm.Validation.isAlpha("")).toBe(false);
    });

    it("Check isAlphaWithAnySpace", function() {
        expect(Acm.Validation.isAlphaWithAnySpace("a")).toBe(true);
        expect(Acm.Validation.isAlphaWithAnySpace("hello world")).toBe(true);
        expect(Acm.Validation.isAlphaWithAnySpace("hello, world")).toBe(false);
        expect(Acm.Validation.isAlphaWithAnySpace("")).toBe(false);
    });

    it("Check isAlphaNumeric", function() {
        expect(Acm.Validation.isAlphaNumeric("h")).toBe(true);
        expect(Acm.Validation.isAlphaNumeric("h1")).toBe(true);
        expect(Acm.Validation.isAlphaNumeric("999")).toBe(true);
        expect(Acm.Validation.isAlphaNumeric("3Stooges")).toBe(true);
        expect(Acm.Validation.isAlphaNumeric("3 Stooges")).toBe(false);
        expect(Acm.Validation.isAlphaNumeric("")).toBe(false);
    });

    it("Check isAlphaNumericSpace", function() {
        expect(Acm.Validation.isAlphaNumericSpace("3 Stooges")).toBe(true);
        expect(Acm.Validation.isAlphaNumericSpace("")).toBe(false);
    });

    it("Check isNumeric", function() {
        expect(Acm.Validation.isNumeric("1")).toBe(true);
        expect(Acm.Validation.isNumeric("123")).toBe(true);
        expect(Acm.Validation.isNumeric("0")).toBe(true);
        expect(Acm.Validation.isNumeric("a")).toBe(false);
        expect(Acm.Validation.isNumeric("1a")).toBe(false);
        expect(Acm.Validation.isNumeric("2K")).toBe(false);
        expect(Acm.Validation.isNumeric("")).toBe(false);
    });

});
