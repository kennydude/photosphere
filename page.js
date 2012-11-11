/****************************************************
     Author: Eric King
     Url: http://redrival.com/eak/index.shtml
     This script is free to use as long as this info is left in
     Featured on Dynamic Drive script library (http://www.dynamicdrive.com)
****************************************************/
var win=null;
function NewWindow(mypage,myname,w,h,scroll,pos){
if(pos=="random"){LeftPosition=(screen.width)?Math.floor(Math.random()*(screen.width-w)):100;TopPosition=(screen.height)?Math.floor(Math.random()*((screen.height-h)-75)):100;}
if(pos=="center"){LeftPosition=(screen.width)?(screen.width-w)/2:100;TopPosition=(screen.height)?(screen.height-h)/2:100;}
else if((pos!="center" && pos!="random") || pos==null){LeftPosition=0;TopPosition=20}
settings='width='+w+',height='+h+',top='+TopPosition+',left='+LeftPosition+',scrollbars='+scroll+',location=no,directories=no,status=no,menubar=no,toolbar=no,resizable=no';
win=window.open(mypage,myname,settings);}
// -->

function openPhotosphere(url){
	NewWindow( chrome.extension.getURL("cpop.html") + "?" + url ,'mywin','500','500','no','center');
}

if(document.location.toString().indexOf("imgur.com") != -1){
	img = document.getElementById("image");
	
	url = img.getElementsByTagName("img")[0].getAttribute("src");
	link = document.createElement("a");
	link.innerHTML = "Open Photosphere";
	link.onclick = function(){
		openPhotosphere(url);
	};
	document.getElementById("under-image").appendChild(link);
}
