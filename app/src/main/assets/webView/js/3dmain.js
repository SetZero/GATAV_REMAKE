// Set the scene size.  
const my_canvas = document.querySelector('#canvas');
my_canvas.width = window.innerWidth;
my_canvas.height = window.innerHeight;
const WIDTH = my_canvas.width;
const HEIGHT = my_canvas.height;

// Set some camera attributes.
const VIEW_ANGLE = 45;
const ASPECT = WIDTH / HEIGHT;
const NEAR = 0.1;
const FAR = 10000;

// Set up the sphere vars
const RADIUS = 50;
const SEGMENTS = 16;
const RINGS = 16;


var goingRIGHT = true;
var goingTOP = true;

var clock;
var texture;
var scene;
var renderer
var camera;
var sphere;


const visibleHeightAtZDepth = (depth, camera) => {
    // compensate for cameras not positioned at z=0
    const cameraOffset = camera.position.z;
    if (depth < cameraOffset) depth -= cameraOffset;
    else depth += cameraOffset;

    // vertical fov in radians
    const vFOV = camera.fov * Math.PI / 180;

    // Math.abs to ensure the result is always positive
    return 2 * Math.tan(vFOV / 2) * Math.abs(depth);
};

const visibleWidthAtZDepth = (depth, camera) => {
    const height = visibleHeightAtZDepth(depth, camera);
    return height * camera.aspect;
};


function init3D()  {
    clock  = new THREE.Clock();
    texture = new THREE.Texture();
    scene = new THREE.Scene();
    renderer = new THREE.WebGLRenderer({
        canvas: my_canvas
    });
    camera  = new THREE.PerspectiveCamera(
                    VIEW_ANGLE,
                    ASPECT,
                    NEAR,
                    FAR
                );

    texture.image = image;
    image.onload = function() {
        texture.needsUpdate = true;
    };
    
    
    scene.add(camera);
    
    renderer.setSize(WIDTH, HEIGHT);
    
    // create a point light
    const pointLight = new THREE.PointLight(0xFFFFFF);
    
    // set its position
    pointLight.position.x = 10;
    pointLight.position.y = 50;
    pointLight.position.z = 130;
    
    // add to the scene
    scene.add(pointLight);
    
    // create the sphere's material
    const sphereMaterial = new THREE.MeshLambertMaterial({
        map: texture
    });
    
    
    
    // Create a new mesh with
    // sphere geometry - we will cover
    // the sphereMaterial next!
    sphere = new THREE.Mesh(
        new THREE.SphereGeometry(
            RADIUS,
            SEGMENTS,
            RINGS),
        sphereMaterial);
    
    // Move the Sphere back in Z so we
    // can see it.
    sphere.position.z = -600;
    
    // Finally, add the sphere to the scene.
    scene.add(sphere);
}
function update() {
    // Draw!
    renderer.render(scene, camera);
    delta = clock.getDelta();
    sphere.rotation.x = (23.5 / 180) * Math.PI;
    sphere.rotation.y = Date.now() * 0.001;

    if (sphere.position.x < (visibleWidthAtZDepth(sphere.position.z, camera) / 2 - RADIUS) && goingRIGHT === true) {
        sphere.position.x += 100 * delta;
    } else if (sphere.position.x >= (visibleWidthAtZDepth(sphere.position.z, camera) / 2 - RADIUS) && goingRIGHT === true) {
        goingRIGHT = false;
    }

    if (sphere.position.x > -(visibleWidthAtZDepth(sphere.position.z, camera) / 2 - RADIUS) && goingRIGHT === false) {
        sphere.position.x -= 100 * delta;
    } else if (sphere.position.x <= -(visibleWidthAtZDepth(sphere.position.z, camera) / 2 - RADIUS) && goingRIGHT === false) {
        goingRIGHT = true;
    }

    if (sphere.position.y < (visibleHeightAtZDepth(sphere.position.z, camera) / 2 - RADIUS) && goingTOP === true) {
        sphere.position.y += 110 * delta;
    } else if (sphere.position.y >= (visibleHeightAtZDepth(sphere.position.z, camera) / 2 - RADIUS) && goingTOP === true) {
        goingTOP = false;
    }

    if (sphere.position.y > -(visibleHeightAtZDepth(sphere.position.z, camera) / 2 - RADIUS) && goingTOP === false) {
        sphere.position.y -= 110 * delta;
    } else if (sphere.position.y <= -(visibleHeightAtZDepth(sphere.position.z, camera) / 2 - RADIUS) && goingTOP === false) {
        goingTOP = true;
    }

    // Schedule the next frame.
    requestAnimationFrame(update);
}