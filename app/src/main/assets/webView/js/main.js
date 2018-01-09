//Roll a Dice
var dice = Math.floor(Math.random() * 6) + 1

console.log(dice + " rolled");

if(supportsWebGL && dice > 3) {
    $('.bottom-cityscape').hide();
    init3D();
    // Schedule the first frame.
    requestAnimationFrame(update);
} else {
    initParticles();
    //start City
    cityStart();
    //Create particles
    for (var i = 0; i < num_particles; i++)
        particles.push(new Particle());
    loopParticles();
}


$( "#level-selector" ).on("click", "a", function() {
    Android.loadLevel($(this).attr("href").replace('#',''));
});

$( "#play-game" ).click(function() {
    $('#main-settings-container').hide();
    $('#main-menu-container').hide();
    $('#level-container').show();
});

$( "#settings" ).click(function() {
    $('#main-menu-container').hide();
    $('#level-container').hide();
    $('#main-settings-container').show();
});

$( "#go-back" ).click(function() {
    $('#main-menu-container').show();
    $('#level-container').hide();
    $('#main-settings-container').hide();
});

$( document ).ready(function() {
    var gameName = Android.getGameName();
    $('#gameName').text(gameName);
    var levels = Android.showLevel();
    var obj = jQuery.parseJSON(levels);
   obj["maps"].forEach(function(element) {
        $('#level-selector').append('<li><a href="#' + element["saveLocation"] + '">' + element["name"] + '</a></li>');
   });
   $('#enable-bgm').prop( "checked" , Android.getBGMEnabledValue());
   $('#enable-eyecandy').prop( "checked" , Android.getEyecandyValue());
});

$('#enable-bgm').change(function() {
    Android.setBGMEnabledValue($(this).prop("checked"));
});

$('#enable-eyecandy').change(function() {
    Android.setEyecandyValue($(this).prop("checked"));
});


// FUNCTIONS
var supportsWebGL = () => {
    if (!window.WebGLRenderingContext) {
        return false;
    } else {
        try {
            var canvas = document.getElementById("myCanvas");
            var context = canvas.getContext("webgl");
            if (!context) {
                return false;
            }
        } catch(e) {
            return false;
        }
    }
    return true;
}