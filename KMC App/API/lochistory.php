<?php 
session_start();
if(isset($_SESSION['name']) == false){
	header("Location: index.php");
}
if($_POST['dt'] == "" ){
	header("Location: Location.php");
}
$name = $_SESSION['name'];
$deviceid = $_SESSION['AUId'];
if($_POST['tm'] == ""){
include 'connection.php';
$conn = new mysqli($server,$username,$password,$dbname);
if(isset($_SESSION['uname']))
$nm = $_SESSION['uname'];
else
$nm = $_SESSION['name'];
$sql = "select * from tblUser_".substr($nm,4)." where dt like '".$_POST['dt']."'";
	$result = $conn->query($sql);
	$points = array();
	if($result->num_rows >0) 
	while($row = $result->fetch_assoc()){
		$loc = array($row['lat'],$row['lon'],$row['tm']);
		array_push($points,$loc);
	}
	 //echo json_encode($points);
}
else {
	include 'connection.php';
$conn = new mysqli($server,$username,$password,$dbname);
if(isset($_SESSION['uname']))
$nm = $_SESSION['uname'];
else
$nm = $_SESSION['name'];
$sql = "select * from tblUser_".substr($nm,4)." where id=".$_POST['tm'];
	$result = $conn->query($sql);
	$points = array();
	if($result->num_rows >0) 
	while($row = $result->fetch_assoc()){
		$loc = array($row['lat'],$row['lon'],$row['tm']);
		array_push($points,$loc);
	}
	
	
}
?><!DOCTYPE html>
<html>
  <head>
    <meta name="viewport" content="initial-scale=1.0, user-scalable=no">
    <meta charset="utf-8">
    <title>History - <?php echo $_SESSION['name'];?></title>
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
    <script src="jquery-1.11.2.min.js"></script>
    <script src="http://maps.googleapis.com/maps/api/js?key=AIzaSyAtHX7DuTsjKc-QsUzco4Xoj7SsUI0zddk&libraries=geometry"></script>
    <script>
  var MapPoints = '<?php echo json_encode($points); ?>'; <!--'[{"address":{"address":"plac Grzybowski, Warszawa, Polska","lat":"52.2360592","lng":"21.002903599999968"},"title":"Warszawa"},{"address":{"address":"Jana Paw\u0142a II, Warszawa, Polska","lat":"52.2179967","lng":"21.222655600000053"},"title":"Wroc\u0142aw"},{"address":{"address":"Wawelska, Warszawa, Polska","lat":"52.2166692","lng":"20.993677599999955"},"title":"O\u015bwi\u0119cim"}]';-->

var MY_MAPTYPE_ID = 'custom_style';

function initialize() {

  if (jQuery('#map').length > 0) {

    var locations = jQuery.parseJSON(MapPoints);

    window.map = new google.maps.Map(document.getElementById('map'), {
      mapTypeId: google.maps.MapTypeId.ROADMAP,
      scrollwheel: false,
      zoom:10
    });

    var infowindow = new google.maps.InfoWindow();
    var flightPlanCoordinates = [];
    var bounds = new google.maps.LatLngBounds();

    for (i = 0; i < locations.length; i++) {
      marker = new google.maps.Marker({
        position: new google.maps.LatLng(locations[i][0], locations[i][1]),
        map: map
      });
      flightPlanCoordinates.push(marker.getPosition());
      bounds.extend(marker.position);

      google.maps.event.addListener(marker, 'click', (function(marker, i) {
        return function() {
          infowindow.setContent(locations[i][2]);
          infowindow.open(map, marker);
        }
      })(marker, i));
    }

    map.fitBounds(bounds);

    var flightPath = new google.maps.Polyline({
      map: map,
      path: flightPlanCoordinates,
      strokeColor: "#FF0000",
      strokeOpacity: 1.0,
      strokeWeight: 2
    });

  }
}
google.maps.event.addDomListener(window, 'load', initialize);
    </script>
    <link rel="stylesheet" type="text/css" href="css/bootstrap.min.css">
<link rel="stylesheet" type="text/css" href="css/stylesheet.css">

  </head>
  <body>
  <div class="row" style="">
  <div class="col-lg-12 text-center"> 
  <a href="#" ><img width="100px" src="img/logo1.jpg"/></a>
  </div></div>
  <div class="pull-left text-center" ><form method="post" action="Location.php"> <input style="margin-top:10px; background:#9a581c; color:white; margin-left:5px;" type="submit" class="btn btn-default" value="<- Back"> </form>
      </div>
  <div class="pull-right text-center" ><form method="post" action="logout.php"> <input style="margin-top:10px; background:#9a581c; color:white;" type="submit" class="btn btn-default" value="Log Out"> </form>
      </div>
<center> <h1>Locations Of: <?php 
if(isset($_SESSION['uname'])){
echo 	$_SESSION['uname'];
}
else
echo $name;
 ?> </h1><P style="font-size:14px;">Date: <?php echo $_POST['dt']; ?> </P>
<?php 
if($_POST['tm'] != "")
echo '<P style="font-size:14px;">Time:' .  $loc[2]. '</P>
';?></center>
    <div id="map" style="width:100%;height:400px;">
   </div>
      
  </body>
</html>
