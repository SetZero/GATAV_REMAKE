    $(function(){
        var x = 0;
        setInterval(function(){
            x-=1;
            $('.bottom-cityscape').css('background-position', x + 'px 0');
        }, 10);
    })