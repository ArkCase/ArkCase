// frevvo custom JavaScript

// Import Rich TextArea plugin
document.writeln('<script type="text/javascript" src="/frevvo/js-25644/libs/rich-textarea-plugin-v2.1/tinymce/tinymce.min.js"></script>');
document.writeln('<script type="text/javascript" src="/frevvo/js-25644/libs/rich-textarea-plugin-v2.1/richtextarea.plugin.js"></script>');


var CustomEventHandlers = {
   setup: function (el) {
       if (CustomView.hasClass(el, 'nextTab')) {
           FEvent.observe(el, 'click', this.scrollTop.bindAsObserver(this, el));
       } else if (CustomView.hasClass(el, 'previousTab')) {
           FEvent.observe(el, 'click', this.scrollTop.bindAsObserver(this, el));
       }
   },
   scrollTop: function (event, element) {
       document.getElementById("wrapper").scrollIntoView();
   }
}