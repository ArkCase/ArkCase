/**
 * Test script for Acm.Dispatcher
 *
 * @author jwu
 */
describe("Acm.Dispatcher", function()
{
    beforeEach(function() {
    });

    it("Create/remove event listeners", function() {
        expect(Acm.Dispatcher.numOfListeners("someEvent")).toBe(0);
        expect(Acm.Dispatcher.isListening   ("someEvent", onSomeEventHandler)).toBe(false);

        Acm.Dispatcher.addEventListener     ("someEvent", onSomeEventHandler);
        expect(Acm.Dispatcher.numOfListeners("someEvent")).toBe(1);
        expect(Acm.Dispatcher.isListening   ("someEvent", onSomeEventHandler)).toBe(true);

        Acm.Dispatcher.addEventListener     ("anotherEvent", onAnotherEventHandler);
        expect(Acm.Dispatcher.numOfListeners("anotherEvent")).toBe(1);
        expect(Acm.Dispatcher.isListening   ("anotherEvent", onAnotherEventHandler)).toBe(true);

        Acm.Dispatcher.removeEventListener  ("someEvent", onSomeEventHandler);
        expect(Acm.Dispatcher.numOfListeners("someEvent")).toBe(0);
        expect(Acm.Dispatcher.isListening   ("someEvent", onSomeEventHandler)).toBe(false);
    });

    var onSomeEventHandler    = function(event, data) {};
    var onAnotherEventHandler = function(event, data) {};

});
