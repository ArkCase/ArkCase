/**
 * Test script for ACM.Validation
 *
 * @author jwu
 */
describe("ACM.Validation", function()
{
    beforeEach(function() {
    });

    it("Check isAlpha", function() {
        expect(ACM.Validation.isAlpha("a")).toBe(true);
        expect(ACM.Validation.isAlpha("A")).toBe(true);
        expect(ACM.Validation.isAlpha("abc")).toBe(true);
        expect(ACM.Validation.isAlpha("123")).toBe(false);
        expect(ACM.Validation.isAlpha("h2")).toBe(false);
        expect(ACM.Validation.isAlpha("hello world")).toBe(false);
        expect(ACM.Validation.isAlpha("")).toBe(false);
    });

    it("Check isAlphaWithAnySpace", function() {
        expect(ACM.Validation.isAlphaWithAnySpace("a")).toBe(true);
        expect(ACM.Validation.isAlphaWithAnySpace("hello world")).toBe(true);
        expect(ACM.Validation.isAlphaWithAnySpace("hello, world")).toBe(false);
        expect(ACM.Validation.isAlphaWithAnySpace("")).toBe(false);
    });

    it("Check isAlphaNumeric", function() {
        expect(ACM.Validation.isAlphaNumeric("h")).toBe(true);
        expect(ACM.Validation.isAlphaNumeric("h1")).toBe(true);
        expect(ACM.Validation.isAlphaNumeric("999")).toBe(true);
        expect(ACM.Validation.isAlphaNumeric("3Stooges")).toBe(true);
        expect(ACM.Validation.isAlphaNumeric("3 Stooges")).toBe(false);
        expect(ACM.Validation.isAlphaNumeric("")).toBe(false);
    });

    it("Check isAlphaNumericSpace", function() {
        expect(ACM.Validation.isAlphaNumericSpace("3 Stooges")).toBe(true);
        expect(ACM.Validation.isAlphaNumericSpace("")).toBe(false);
    });

    it("Check isNumeric", function() {
        expect(ACM.Validation.isNumeric("1")).toBe(true);
        expect(ACM.Validation.isNumeric("123")).toBe(true);
        expect(ACM.Validation.isNumeric("0")).toBe(true);
        expect(ACM.Validation.isNumeric("a")).toBe(false);
        expect(ACM.Validation.isNumeric("1a")).toBe(false);
        expect(ACM.Validation.isNumeric("2K")).toBe(false);
        expect(ACM.Validation.isNumeric("")).toBe(false);
    });

});
