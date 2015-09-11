**IMPORTANT NOTICE:**

THIS IS VERY OLD AND YOU SHOULD NOT USE THIS. NO RESPONSES TO EMAILS OR TICKETS WILL BE GIVEN
FROM ME

---

# PHOTOSPHERE

This allows you to view the new Photospheres in Android 4.2 without a problem **without Google+**

Thanks to

* Three.JS
* http://stackoverflow.com/questions/1578169/how-can-i-read-xmp-data-from-a-jpg-with-php
* Uses a portion of Modernizr that has been modified https://github.com/Modernizr/Modernizr/blob/master/feature-detects/webgl-extensions.js

## TO TEST

Open test.html and press "makeIt"

## TO USE

Get Three.js (or use the bundled copy) and sphere.js and run:

	new Photosphere("link-to-image.jpg").loadPhotosphere(document.getElementById("element-on-page"));

If you are including this into some gallery app which can read EXIF XMP tags, you can make it render faster (this is why you don't need every single shot for it to work, Android adds these tags in):

	new Photosphere("link...").setEXIF({
		"full_width" : "GPano:FullPanoWidthPixels value",
		"full_height" : "GPano:FullPanoHeightPixels value",
		"crop_width" : "GPano:CroppedAreaImageWidthPixels value",
		"crop_height" : "GPano:CroppedAreaImageHeightPixels value",
		"x" : "GPano:CroppedAreaLeftPixels value",
		"y" : "GPano:CroppedAreaTopPixels value"
	}).loadPhotosphere(...);

Tada! :D

## LICENSE

GPLv3 http://www.tldrlegal.com/license/gnu-general-public-license-v3-(gpl-3)

## HOW IT WORKS

1. We load EXIF XMP data
  1. AJAX request as string
  2. Use Javascript string positioning to pull out the xmp chunk
  3. Pull out our attributes we need
2. Create an image of full_width x full_height and put the linked image on at x, y with crop_width x crop_height.
3. Load Three.JS using Panorma-like details (I used the Three.JS example for this and baked it into the class. I don't have a clue about 3D stuff nor am I really **that** interested)
4. Boom!
