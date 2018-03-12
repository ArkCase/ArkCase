'use strict';

angular.module('document-details').controller('Document.TranscriptionController', [ '$scope', function($scope) {

    //Resize textarea based on content length
    var tx = document.getElementsByTagName('textarea');
    for (var i = 0; i < tx.length; i++) {
        tx[i].setAttribute('style', 'height:' + (tx[i].scrollHeight + tx[i].height) + 'px;overflow-y:hidden;');
        tx[i].addEventListener("input", OnInput, false);
    }

    function OnInput() {
        this.style.height = 'auto';
        this.style.height = (this.scrollHeight) + 'px';
    }

} ]);