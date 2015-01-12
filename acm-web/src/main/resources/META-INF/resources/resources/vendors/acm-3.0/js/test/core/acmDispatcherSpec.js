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

    it("Positive event response", function() {
        Acm.Dispatcher.addEventListener     ("oneEvent", onSomeEventHandler);
        Acm.Dispatcher.addEventListener     ("oneEvent", onAnotherEventHandler);
        Acm.Dispatcher.addEventListener     ("oneEvent", onMoreEventHandler);
        expect(Acm.Dispatcher.numOfListeners("oneEvent")).toBe(3);
        expect(Acm.Dispatcher.fireEvent     ("oneEvent")).toBe(1);
    });

    it("Event priority", function() {
        handlingOrder = "";
        Acm.Dispatcher.addEventListener     ("priorityEvent", onEventHandler1);
        Acm.Dispatcher.addEventListener     ("priorityEvent", onEventHandler2);
        Acm.Dispatcher.addEventListener     ("priorityEvent", onEventHandler3);
        expect(Acm.Dispatcher.numOfListeners("priorityEvent")).toBe(3);
        Acm.Dispatcher.fireEvent("priorityEvent");
        expect(handlingOrder).toBe("321");

        Acm.Dispatcher.removeEventListener  ("priorityEvent", onEventHandler1);
        Acm.Dispatcher.removeEventListener  ("priorityEvent", onEventHandler2);
        Acm.Dispatcher.removeEventListener  ("priorityEvent", onEventHandler3);
        expect(Acm.Dispatcher.numOfListeners("priorityEvent")).toBe(0);

        handlingOrder = "";
        Acm.Dispatcher.addEventListener     ("priorityEvent", onEventHandler1);
        Acm.Dispatcher.addEventListener     ("priorityEvent", onEventHandler2, Acm.Dispatcher.PRIORITY_HIGH);
        Acm.Dispatcher.addEventListener     ("priorityEvent", onEventHandler3, Acm.Dispatcher.PRIORITY_LOW);
        expect(Acm.Dispatcher.numOfListeners("priorityEvent")).toBe(3);
        Acm.Dispatcher.fireEvent("priorityEvent");
        expect(handlingOrder).toBe("213");

        Acm.Dispatcher.removeEventListener  ("priorityEvent", onEventHandler1);
        Acm.Dispatcher.removeEventListener  ("priorityEvent", onEventHandler2);
        Acm.Dispatcher.removeEventListener  ("priorityEvent", onEventHandler3);
        expect(Acm.Dispatcher.numOfListeners("priorityEvent")).toBe(0);

        //switch to different adding order, same event order
        handlingOrder = "";
        Acm.Dispatcher.addEventListener     ("priorityEvent", onEventHandler3, Acm.Dispatcher.PRIORITY_LOW);
        Acm.Dispatcher.addEventListener     ("priorityEvent", onEventHandler1);
        Acm.Dispatcher.addEventListener     ("priorityEvent", onEventHandler2, Acm.Dispatcher.PRIORITY_HIGH);
        expect(Acm.Dispatcher.numOfListeners("priorityEvent")).toBe(3);
        Acm.Dispatcher.fireEvent("priorityEvent");
        expect(handlingOrder).toBe("213");
    });

    var onSomeEventHandler    = function(event, data) {};
    var onAnotherEventHandler = function(event, data) {return true;};
    var onMoreEventHandler    = function(event, data) {return false;};

    var handlingOrder = "";
    var onEventHandler1 = function(event, data) {
        handlingOrder += "1";
    };
    var onEventHandler2 = function(event, data) {
        handlingOrder += "2";
    };
    var onEventHandler3 = function(event, data) {
        handlingOrder += "3";
    };
});
