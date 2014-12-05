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
        expect(Acm.Rule.isAlpha("a")).toBe(true);
        expect(Acm.Rule.isAlpha("A")).toBe(true);
        expect(Acm.Rule.isAlpha("abc")).toBe(true);
        expect(Acm.Rule.isAlpha("123")).toBe(false);
        expect(Acm.Rule.isAlpha("h2")).toBe(false);
        expect(Acm.Rule.isAlpha("hello world")).toBe(false);
        expect(Acm.Rule.isAlpha("")).toBe(false);
    });

    it("Check isAlphaWithAnySpace", function() {
        expect(Acm.Rule.isAlphaWithAnySpace("a")).toBe(true);
        expect(Acm.Rule.isAlphaWithAnySpace("hello world")).toBe(true);
        expect(Acm.Rule.isAlphaWithAnySpace("hello, world")).toBe(false);
        expect(Acm.Rule.isAlphaWithAnySpace("")).toBe(false);
    });

    it("Check isAlphaNumeric", function() {
        expect(Acm.Rule.isAlphaNumeric("h")).toBe(true);
        expect(Acm.Rule.isAlphaNumeric("h1")).toBe(true);
        expect(Acm.Rule.isAlphaNumeric("999")).toBe(true);
        expect(Acm.Rule.isAlphaNumeric("3Stooges")).toBe(true);
        expect(Acm.Rule.isAlphaNumeric("3 Stooges")).toBe(false);
        expect(Acm.Rule.isAlphaNumeric("")).toBe(false);
    });

    it("Check isAlphaNumericSpace", function() {
        expect(Acm.Rule.isAlphaNumericSpace("3 Stooges")).toBe(true);
        expect(Acm.Rule.isAlphaNumericSpace("")).toBe(false);
    });

    it("Check isNumeric", function() {
        expect(Acm.Rule.isNumeric("1")).toBe(true);
        expect(Acm.Rule.isNumeric("123")).toBe(true);
        expect(Acm.Rule.isNumeric("0")).toBe(true);
        expect(Acm.Rule.isNumeric("a")).toBe(false);
        expect(Acm.Rule.isNumeric("1a")).toBe(false);
        expect(Acm.Rule.isNumeric("2K")).toBe(false);
        expect(Acm.Rule.isNumeric("")).toBe(false);
    });

});
