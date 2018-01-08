//Roll a Dice
var dice = Math.floor(Math.random() * 6) + 1

console.log(dice + " rolled");

if(dice > 3) {
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
$( document ).ready(function() {
    var gameName = Android.getGameName();
    $('#gameName').text(gameName);
    var levels = Android.showLevel();
    var obj = jQuery.parseJSON(levels);
   obj["maps"].forEach(function(element) {
        $('#level-selector').append('<li><a href="#' + element["saveLocation"] + '">' + element["name"] + '</a></li>');
   });
}); 