<?php 
session_start();
if(isset($_SESSION['name'])){
	if($_SESSION['name'] != 'admin') {
		header("Location: Location.php");
	}
	 /* Redirect browser */
}

/*if(isset($_POST['delete'])){
$devicesid = array();
$devicesid = $_POST['deviceid'];
include "connection.php";
$conn = new mysqli($server,$username,$password,$dbname);
foreach($devicesid as $id){
      $sql = "DELETE FROM `devices` WHERE deviceid=".$id;
	  $conn->query($sql);	
}	
}*/

?><!doctype html>
<html>
<head>
<meta charset="utf-8">
<title>Approval Requests - Admin Panel</title>
<link rel="stylesheet" type="text/css" href="css/bootstrap.min.css">
<link rel="stylesheet" type="text/css" href="css/stylesheet.css">
<style>
td{
	padding:3px;
}
</style>
 </head>

<script>
 function reload(){
	 window.location="appreq.php";
 }
 setInterval("reload()",60000);
 
  </script>
<body >
<div class="row"><div class="col-lg-12 text-center"> <a href="#" ><img width="200px" src="img/logo1.jpg"/></a></div></div>
<div class="row">
<div class=" col-lg-3 text-center"  ><form method="post" style="margin-left:10px; float:left;" action="admin.php"> <input style="margin-top:10px; margin-left:10px;" type="submit" class="btn btn-default" value="<- Back"> </form></div>
<div class="col-lg-6"> <center><h1> Approval Requests:</h1></center></div>
<div class="col-lg-3" style="text-align:right;" >
<form method="post" style="margin-right:10px;" action="logout.php"> <input style="margin-top:10px; margin-left:10px;" type="submit" class="btn btn-default" value="Log Out"> </form>
      </div>
     </div>
<!-- header ends here -->
<div class="container" style="" >
<center><table class="table-bordered table-hover table-responsive" style="font-size:17px; text-align:center; background:white; width:100%" id="alldevices"> 

<tr  style="background:#9a581c;  border:none; color:white; font-size:34px;"> <th class=" col-" style="text-align:center;border:1px d white; border-top-left-radius:40px; padding:1%;" colspan="2">Website</th>
<th style="text-align:center; padding:1%; border:1px d white; border-top-right-radius:40px;" colspan="6">Android</th>  </tr><tr  style="background:black; color:white;" > <th style="text-align:center; ">Username</th>
    <th style="text-align:center">Password</th>
    <th style="text-align:center"> Username</th>
    <th style="text-align:center"> Password</th>
    <th style="text-align:center">Date Created</th>
     <th  style="text-align:center" colspan="3">More</th>
    </tr><?php 
	
unset($_SESSION['devices']);
$_SESSION['devices'] = array();
	include "connection.php";
// Create connection
$conn = new mysqli($server, $username, $password,$dbname);
// Check connection
if ($conn->connect_error) {
	$error = die("Connection failed: " . $conn->connect_error);
	
	
    
} 

$sql = "SELECT webandruser.Username as WUsername , webandruser.Pass as WPass,androiduser.Username as AUsername,androiduser.Pass as APass ,androiduser.AUId , androiduser.CDate,androiduser.status FROM webandruser INNER JOIN androiduser on webandruser.AUId=androiduser.AUId where androiduser.appr=0  ";
$result = $conn->query($sql);
	if($result->num_rows>0){
	  while($row = $result->fetch_assoc()) { 
		$style="";
		   $status = "  Active  ";
		  
			echo "<tr style='font-size:15px; ".$style."'><td>". $row['WUsername'] . "</td><td>". $row['WPass'] . "</td><td>". $row['AUsername'] . "<td>". $row['APass'] . '</td><td>'. $row['CDate'] . '</td>'. '<td><form action="viewdetails.php" method="post"><input class="btn btn-default" type="submit" value="Details" name="vdetails"><input type="hidden" name="WUsername" value="'.$row['WUsername'].'"/><input type="hidden" name="approval" value="1"/><input type="hidden" name="AUId" value="'.$row['AUId'].'">'. '</form></td><td><form action="delete.php" method="post"><input type="submit" class="btn btn-danger" value="Delete" name="sdelete"><input type="hidden" name="WUsername" value="'.$row['WUsername'].'"/><input type="hidden" name="approval" value="1"/><input type="hidden" name="AUId" value="'.$row['AUId'].'">'. '</form></td><td><form action="approve.php" method="post"><input type="submit" class="btn" value="Approve" name="btnappr"><input type="hidden" name="WUsername" value="'.$row['WUsername'].'"/>
			<input type="hidden" name="AUId" value="'.$row['AUId'].'">'. '</form></td></tr>' ;}
	
	  }
?></table>
 </div>







<br>
<footer class="modal-footer"> </footer>

</body>
</html>

 