+function ($) {

  $(function(){

    $('#wizardform').bootstrapWizard({
      'tabClass': 'nav nav-tabs',
      'onNext': function(tab, navigation, index) {
        return true;
      },
      onTabClick: function(tab, navigation, index) {
        return true;
      },
      onTabShow: function(tab, navigation, index) {
        var $total = navigation.find('li').length;
        var $current = index+1;
        var $percent = ($current/$total) * 100;
        $('#wizardform').find('.progress-bar').css({width:$percent+'%'});
      }
    });

    
  });
}(window.jQuery);