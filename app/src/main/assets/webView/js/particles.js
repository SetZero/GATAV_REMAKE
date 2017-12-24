var canvas = document.getElementById("canvas");
var ctx = canvas.getContext("2d");
canvas.width = window.innerWidth;
canvas.height = window.innerHeight;
var particles = [];
var num_particles = 1000;//Change that to your liking

//Helper function to get a random color - but not too dark
function GetRandomColor() {
    var r = 0, g = 0, b = 0;
    while (r < 100 && g < 100 && b < 100)
    {
        r = Math.floor(Math.random() * 128);
        g = Math.floor(Math.random() * 128);
        b = Math.floor(128 + Math.random() * 128);
    }

    return "rgb(" + r + "," + g + ","  + b + ")";
}
//Particle object with random starting position, velocity and color
var Particle = function () {
    this.x = canvas.width * Math.random();
    this.y = canvas.height * Math.random();
    this.vx = 0;
    this.vy = 2 * Math.random() + 2;
    this.Color = GetRandomColor();
}
//Ading two methods
Particle.prototype.Draw = function (ctx) {
    ctx.fillStyle = this.Color;
    ctx.fillRect(this.x, this.y, 2, 2 + 8 * Math.random());
}
Particle.prototype.Update = function () {
    this.y += this.vy;

    if (this.y > canvas.height)
        this.y = 0;
}
function loop() {
    ctx.clearRect(0, 0, canvas.width, canvas.height);

    for (var i = 0; i < num_particles; i++) {
        particles[i].Update();
        particles[i].Draw(ctx);
    }
    requestAnimationFrame(loop);
}
//Create particles
for (var i = 0; i < num_particles; i++)
    particles.push(new Particle());
loop();