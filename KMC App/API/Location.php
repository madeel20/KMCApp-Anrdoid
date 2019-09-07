<?php 
session_start();
if(isset($_SESSION['name']) == false){
	header("Location: index.php");
}

if(isset($_POST['AUId'])){
	$deviceid = $_POST['AUId'];
	$_SESSION['AUId'] = $_POST['AUId'];
	$_SESSION['uname'] = $_POST['WUsername'];
	$_SESSION['ustatus'] = $_POST['ustatus'];
	
}
else{
$deviceid = $_SESSION['AUId'];
}
$name = $_SESSION['name'];
$lupdated ="";
if(isset($_SESSION['ustatus']) && $_SESSION['ustatus']==0){
	$errorfornotactive = "User is not Active!";
}
else {

include 'connection.php';
$conn = new mysqli($server,$username,$password,$dbname);
$sql = "select * from locations where AUId=".$deviceid;
	$result = $conn->query($sql);
	if($result->num_rows >0) 
	while($row = $result->fetch_assoc()){
		$latitude= $row['Latitude'];
		$Longitude= $row['Longtitude'];
		
		$lupdated = $row['lastupdated'];
	}
	else {
		$error = "Location has not been recieved yet!";
	}
}

?><!DOCTYPE html>
<html>
  <head>
  
<script>
function showUser(str) {
    if (str == "") {
        document.getElementById("txtHint").innerHTML = "";
        return;
    } else { 
        if (window.XMLHttpRequest) {
            // code for IE7+, Firefox, Chrome, Opera, Safari
            xmlhttp = new XMLHttpRequest();
        } else {
            // code for IE6, IE5
            xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
        }
        xmlhttp.onreadystatechange = function() {
            if (this.readyState == 4 && this.status == 200) {
                document.getElementById("txtHint").innerHTML= this.responseText;
            }
        };
        xmlhttp.open("GET","loaddates.php?q="+str,true);
        xmlhttp.send();
    }
}
</script>
    <meta name="viewport" content="initial-scale=1.0, user-scalable=no">
    <meta charset="utf-8">
    <title>Location - <?php echo $_SESSION['name'];?></title>
    <style>
      /* Always set the map height explicitly to define the size of the div
       * element that contains the map. */
      #map {
        height: 500px;
      }
      /* Optional: Makes the sample page fill the window. */
      html, body {
        height: 100%;
        margin: 0;
        padding: 0;
      }
    </style>
    <script src="http://maps.googleapis.com/maps/api/js?key=AIzaSyAtHX7DuTsjKc-QsUzco4Xoj7SsUI0zddk&libraries=geometry"></script>
    <script>
        var map;
        var marker1;
var y;
var x;
        function initialize() {
           y = <?php if(isset($latitude)) echo $latitude;?>;
           x = <?php if(isset($Longitude)) echo $Longitude;?>;
		
            var latLng = new google.maps.LatLng(x, y);

            var mapProp = {
                center: latLng,
                zoom: 15,
                mapTypeId: google.maps.MapTypeId.ROADMAP

            };

            map = new google.maps.Map(document.getElementById("googleMap"), mapProp);

            marker1 = new google.maps.Marker({
                position: latLng,
                title: 'Point A',

                draggable: false,
            });
marker1.setAnimation(google.maps.Animation.BOUNCE);
            marker1.setMap(map);

           
            google.maps.event.trigger(marker1, 'dragend', {
                latLng: marker1.getPosition()
            });
        }

       google.maps.event.addDomListener(window, 'load', initialize);
        
        function updateMarker(lat,lng) {
            var latLng = new google.maps.LatLng(lat, lng);
			
            marker1.setPosition(latLng);
            map.panTo(latLng);
        }
		function loadonlinepl(){
	  
	if (window.XMLHttpRequest) {
            // code for IE7+, Firefox, Chrome, Opera, Safari
            xmlhttp = new XMLHttpRequest();
        } else {
            // code for IE6, IE5
            xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
        }
        xmlhttp.onreadystatechange = function() {
            if (this.readyState == 4 && this.status == 200) {
				myObj = JSON.parse(this.responseText);
				document.getElementById('lup').innerHTML = myObj.lup;
				if(myObj.lat == y && myObj.long == x){
					
				}
				else {
					
				updateMarker(myObj.lat,myObj.long);
				}
		
       
            }
        };
        xmlhttp.open("GET","loadlocation.php",true);
        xmlhttp.send();
	  
  }
 
 setInterval("loadonlinepl();",1000
 );
		
    </script>
    <link rel="stylesheet" type="text/css" href="css/bootstrap.min.css">
<link rel="stylesheet" type="text/css" href="css/stylesheet.css">

  </head>
  <body>
  <div class="row" style="">
  <div class="col-lg-12 text-center"> 
  <a href="#" ><img width="100px" src="img/logo1.jpg"/></a>
  </div></div>
 <div class="row">
	<div class="col-lg-2 text-center" >
    <form method="post" action="admin.php">
     <input style="margin-top:10px; margin-left:5px;background:#9a581c; color:white;" type="submit"
      <?php 
  if($_SESSION['name'] == 'admin'){echo 'type="hidden"';}?> class="btn btn-default" value="<- Back"> </form>
      </div>
      
	<div class="col-lg-8" ><center> <h1>Location Of: <?php 
if(isset($_SESSION['uname'])){
echo 	$_SESSION['uname'];
}
else
echo $name;
 ?> </h1><P style="font-size:14px;"><?php 
 include 'connection.php';
$conn = new mysqli($server,$username,$password,$dbname);
$sql = "select * from tbl_udetails where AUId=".$deviceid;
	$result = $conn->query($sql);
	if($result->num_rows >0) 
	while($row = $result->fetch_assoc()){
		echo '<b>Driver Name:</b> '.$row['firstname'].' '.$row['lastname'];
		echo '&nbsp;&nbsp;&nbsp;<b>Vehicle NO:</b> '.$row['vehicleno'];
	}
	?>
 </P><P style="font-size:14px;">Last Updated: <span id="lup"><?php echo $lupdated; ?> </span></P></center></div>
 <div class="col-lg-2 text-center" ><form method="post" action="logout.php"> <input style="margin-top:10px; background:#9a581c; color:white;" type="submit" class="btn btn-default" value="Log Out"> </form>
      </div>
  </div>
  

<div class="row" style="margin-top:5px;" >
<form action="lochistory.php" method="post" >
<div class="col-md-2"></div>
<div class="col-md-1 text-center">
<h4>History: </h4>
</div>
<div class="col-md-2">
<span style="">
<select onchange="showUser(this.value)" style="float:left"  class=" form-control dropdown text-center" name="dt" id="Sell"> 
<option value="" selected>
Select Date</option>
<?php 
include 'connection.php';
$conn = new mysqli($server,$username,$password,$dbname);
if(isset($_SESSION['uname']))
$nm = $_SESSION['uname'];
else
$nm = $_SESSION['name'];
$sql = "select distinct dt from tblUser_".substr($nm,4);
$result = $conn->query($sql);
	if($result->num_rows >0) 
	while($row = $result->fetch_assoc()){
		echo '<option value="'.$row['dt'].'">'.$row['dt']."</option>";
	}

?>
</select>
</div>
<div class=" col-lg-2">


<select name="tm" id="txtHint" style="float:left" class="form-control dropdown text-center" id="Sell"> 
<option value="" selected>
Select Time</option>
</select></div>
<div class=" col-lg-3"><input class="form-control " style=" background:#06F; color:white;" type="submit" value="Load"/>
</div>
</form></div>
<div id="googleMap" style="width:100%;min-height:390px;">
    <?php 
	if(isset($errorfornotactive)){
		echo "<br><br><center><h1 style='color:red'>".$errorfornotactive."</h1></center>";

	}
	if(isset($error)){
		
		echo "<br><br><center><h1 style='color:red'>".$error."</h1></center>";
	}
	
	
	?></div>
      
  </body>
</html>
