$(function(){
    $('#submit').click(function(e){
        e.preventDefault();
        $.ajax({
            type: 'POST',
            url: 'acm/j_spring_security_check',
            data: {
                j_username: $('#j_username').val(),
                j_password: $('#j_password').val()
            }
        })
            .done(function(page){
                if (page.indexOf('objectTypes') != -1) {
                    window.location = '/#!complaints';
                } else {
                    alert('bad credentials')
                }
            })
            .fail(function(result){
                alert('Server error');
            });
    });
})