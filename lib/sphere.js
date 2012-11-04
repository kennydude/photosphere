// Google Photosphere
// built by @kennydude

// Thanks to three.js example and http://stackoverflow.com/questions/1578169/how-can-i-read-xmp-data-from-a-jpg-with-php

// Usage: new Photosphere("image.jpg").loadPhotosphere(document.getElementById("myPhotosphereID"));
// myPhotosphereID must have width/height specified!


function Photosphere(image){
	this.image = image;
}

Photosphere.prototype.loadPhotosphere = function(holder){
	holder.innerHTML = "wait...";
	
	this.holder = holder;
	self = this;
	this.loadEXIF(function(){
		self.cropImage();
	});
};

Photosphere.prototype.cropImage = function(){
	img = new Image();
	self = this;

	img.onload = function(){
		canvas = document.createElement('canvas');
		canvas.width = self.exif['full_width'];
		canvas.height = self.exif['full_height'];
		context = canvas.getContext("2d");
	
		context.fillStyle = "#000";
		context.fillRect(0,0,canvas.width,canvas.height);
		context.drawImage(img, self.exif['x'], self.exif['y'], self.exif['crop_width'], self.exif['crop_height']);
		self.start3D( canvas.toDataURL("image/png") );
	}
	img.src = this.image;
};

Photosphere.prototype.start3D = function(image){
	if(window['THREE'] == undefined){ alert("Please make sure three.js is loaded"); }
	
	// Start Three.JS rendering
	this.target = new THREE.Vector3();
	this.lat = 0; this.lon = 90;
	this.onMouseDownMouseX = 0, this.onMouseDownMouseY = 0, this.isUserInteracting = false, this.onMouseDownLon = 0, this.onMouseDownLat = 0;

	this.camera = new THREE.PerspectiveCamera( 75, parseInt(this.holder.offsetWidth) / parseInt(this.holder.offsetHeight), 1, 1100 );
	this.scene = new THREE.Scene();
	mesh = new THREE.Mesh( new THREE.SphereGeometry( 200, 20, 40 ), this.loadTexture( image ) );
	mesh.scale.x = - 1;
	this.scene.add( mesh );
	
	this.renderer = new THREE.CanvasRenderer();
	this.renderer.setSize( parseInt(this.holder.offsetWidth), parseInt(this.holder.offsetHeight) );
	this.holder.innerHTML = "";
	this.holder.appendChild( this.renderer.domElement );

	self = this;
	this.holder.addEventListener( 'touchstart', function(event){ self.onDocumentTouchStart(event, self); }, false );
	this.holder.addEventListener( 'touchmove', function(event){ self.onDocumentTouchMove(event, self); }, false );
	this.holder.addEventListener( 'mousedown', function(event){self.onDocumentMouseDown(event, self); }, false );
	this.holder.addEventListener( 'mousewheel', function(event){self.onMouseWheel(event, self); }, false );	

	document.addEventListener( 'mousemove', function(event){self.onDocumentMouseMove(event, self); }, false );
	document.addEventListener( 'mouseup', function(event){self.onDocumentMouseUp(event, self); }, false );

	this.resetTimer(this, 3000);
};

Photosphere.prototype.startMoving = function(){
	self = this;
	this.interval = setInterval(function(){
		self.lon = self.lon - 0.1;
		
		if( -3 < self.lat && self.lat < 3){}
		else if(self.lat > 0){ self.lat -= 0.1; }
		else if(self.lat < 0) { self.lat += 0.1;  }

		self.render();
	}, 10);
};

Photosphere.prototype.resetTimer = function(self, t){
	if(self.timer != undefined){ clearTimeout(self.timer); }
	if(self.interval != undefined){ clearInterval(self.interval); }

	self.timer = setTimeout(function(){
		self.startMoving();
	}, t);
};

Photosphere.prototype.onWindowResize = function(self) {

	self.camera.aspect = parseInt(self.holder.offsetWidth) / parseInt(self.holder.offsetHeight);
	self.camera.updateProjectionMatrix();

	self.renderer.setSize( parseInt(self.holder.offsetWidth), parseInt(self.holder.offsetHeight) );

	self.render();

}


Photosphere.prototype.onMouseWheel = function( event, self ) {

	self.camera.fov -= event.wheelDeltaY * 0.05;
	self.camera.updateProjectionMatrix();

	self.render();

	event.preventDefault();

}

Photosphere.prototype.onDocumentMouseDown = function( event, self ) {

	event.preventDefault();

	self.isUserInteracting = true;

	self.onPointerDownPointerX = event.clientX;
	self.onPointerDownPointerY = event.clientY;

	self.onPointerDownLon = self.lon;
	self.onPointerDownLat = self.lat;

};

Photosphere.prototype.onDocumentMouseMove = function( event, self ) {

	if ( self.isUserInteracting ) {

		self.lon = ( self.onPointerDownPointerX - event.clientX ) * 0.1 + self.onPointerDownLon;
		self.lat = ( event.clientY - self.onPointerDownPointerY ) * 0.1 + self.onPointerDownLat;
		self.render();

		self.resetTimer(self, 9000);

	}

};

Photosphere.prototype.onDocumentTouchStart = function( event, self ) {

	if ( event.touches.length == 1 ) {

		event.preventDefault();

		self.onPointerDownPointerX = event.touches[ 0 ].pageX;
		self.onPointerDownPointerY = event.touches[ 0 ].pageY;

		self.onPointerDownLon = lon;
		self.onPointerDownLat = lat;

	}

}

Photosphere.prototype.onDocumentTouchMove = function( event, self ) {

	if ( event.touches.length == 1 ) {

		event.preventDefault();

		self.lon = ( self.onPointerDownPointerX - event.touches[0].pageX ) * 0.1 + self.onPointerDownLon;
		self.lat = ( event.touches[0].pageY - self.onPointerDownPointerY ) * 0.1 + self.onPointerDownLat;

		self.render();
		self.resetTimer(self, 9000);

	}

}

Photosphere.prototype.onDocumentMouseUp = function( event, self ) {

	self.isUserInteracting = false;
	self.render();

};

Photosphere.prototype.loadTexture = function( path ) {
	var texture = new THREE.Texture(  );
	var material = new THREE.MeshBasicMaterial( { map: texture, overdraw: true } );

	var image = new Image();
	self = this;
	image.onload = function () {

		texture.needsUpdate = true;
		material.map.image = this;

		setTimeout(function(){ self.render(); }, 100);
	};
	image.src = path;

	return material;
}

Photosphere.prototype.render = function(){
	this.lat = Math.max( - 85, Math.min( 85, this.lat ) );
	phi = ( 90 - this.lat ) * Math.PI / 180;
	theta = this.lon * Math.PI / 180;

	this.target.x = 500 * Math.sin( phi ) * Math.cos( theta );
	this.target.y = 500 * Math.cos( phi );
	this.target.z = 500 * Math.sin( phi ) * Math.sin( theta );

	this.camera.lookAt( this.target );

	this.renderer.render( this.scene, this.camera );
};

Photosphere.prototype.loadBinary = function(callback){
	if(this.binary_data != undefined){ callback(this.binary_data); return; }
	var oHTTP = null;
	if (window.ActiveXObject) {
		oHTTP = new ActiveXObject("Microsoft.XMLHTTP");
	} else if (window.XMLHttpRequest) {
		oHTTP = new XMLHttpRequest();
	}
	
	if (typeof(oHTTP.onload) != "undefined") {
		oHTTP.onload = function() {
			if (oHTTP.status == "200") {
				callback(oHTTP.responseText);
			} else {
				// Error?
			}
			oHTTP = null;
		};
	} else {
		oHTTP.onreadystatechange = function() {
			if (oHTTP.readyState == 4) {
				if (oHTTP.status == "200") {
					callback(oHTTP.responseText);
				} else {
					// Error?
				}
				oHTTP = null;
			}
		};
	}
	oHTTP.open("GET", this.image, true);
	oHTTP.send(null);
};

Photosphere.prototype.loadEXIF = function(callback){
	self = this;
	this.loadBinary(function(data){
		xmpEnd = "</x:xmpmeta>";
		xmpp = data.substring(data.indexOf("<x:xmpmeta"), data.indexOf(xmpEnd) + xmpEnd.length);

		getAttr = function(attr){
			x = xmpp.indexOf(attr+'="') + attr.length + 2;
			return xmpp.substring( x, xmpp.indexOf('"', x) );
		};

		self.exif = {
			"full_width" : getAttr("GPano:FullPanoWidthPixels"),
			"full_height" : getAttr("GPano:FullPanoHeightPixels"),
			"crop_width" : getAttr("GPano:CroppedAreaImageWidthPixels"),
			"crop_height" : getAttr("GPano:CroppedAreaImageHeightPixels"),
			"x" : getAttr("GPano:CroppedAreaLeftPixels"),
			"y" : getAttr("GPano:CroppedAreaTopPixels")
		}
		callback();
	});
};
