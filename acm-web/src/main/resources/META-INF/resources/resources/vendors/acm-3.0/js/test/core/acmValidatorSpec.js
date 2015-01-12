/**
 * Test script for Acm.Rule
 *
 * @author jwu
 */
describe("Acm.Rule", function()
{
    beforeEach(function() {
    });

    it("Check isAlpha", function() {
        expect(Acm.Validator.isAlpha("a")).toBe(true);
        expect(Acm.Validator.isAlpha("A")).toBe(true);
        expect(Acm.Validator.isAlpha("abc")).toBe(true);
        expect(Acm.Validator.isAlpha("123")).toBe(false);
        expect(Acm.Validator.isAlpha("h2")).toBe(false);
        expect(Acm.Validator.isAlpha("hello world")).toBe(false);
        expect(Acm.Validator.isAlpha("")).toBe(false);
    });

    it("Check isAlphaWithAnySpace", function() {
        expect(Acm.Validator.isAlphaWithAnySpace("a")).toBe(true);
        expect(Acm.Validator.isAlphaWithAnySpace("hello world")).toBe(true);
        expect(Acm.Validator.isAlphaWithAnySpace("hello, world")).toBe(false);
        expect(Acm.Validator.isAlphaWithAnySpace("")).toBe(false);
    });

    it("Check isAlphaNumeric", function() {
        expect(Acm.Validator.isAlphaNumeric("h")).toBe(true);
        expect(Acm.Validator.isAlphaNumeric("h1")).toBe(true);
        expect(Acm.Validator.isAlphaNumeric("999")).toBe(true);
        expect(Acm.Validator.isAlphaNumeric("3Stooges")).toBe(true);
        expect(Acm.Validator.isAlphaNumeric("3 Stooges")).toBe(false);
        expect(Acm.Validator.isAlphaNumeric("")).toBe(false);
    });

    it("Check isAlphaNumericSpace", function() {
        expect(Acm.Validator.isAlphaNumericSpace("3 Stooges")).toBe(true);
        expect(Acm.Validator.isAlphaNumericSpace("")).toBe(false);
    });

    it("Check isNumeric", function() {
        expect(Acm.Validator.isNumeric("1")).toBe(true);
        expect(Acm.Validator.isNumeric("123")).toBe(true);
        expect(Acm.Validator.isNumeric("0")).toBe(true);
        expect(Acm.Validator.isNumeric("a")).toBe(false);
        expect(Acm.Validator.isNumeric("1a")).toBe(false);
        expect(Acm.Validator.isNumeric("2K")).toBe(false);
        expect(Acm.Validator.isNumeric("")).toBe(false);
    });

});
