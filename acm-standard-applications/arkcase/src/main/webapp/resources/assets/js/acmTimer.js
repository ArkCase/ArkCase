/**
 * Support for ACM Timer Worker
 *
 * @author jwu
 */

var i = 0;

function timedCount() {
    i = i + 1;
    postMessage(i);
    setTimeout("timedCount()", 100);
}

timedCount();