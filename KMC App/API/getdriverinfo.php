<?php 

if(isset($_GET['key'])){
	if($_GET['key'] == "1345"){
$id = $_GET['AUId'];
	include 'connection.php';
	$conn = new MySQLi($server,$username,$password,$dbname);
	$sql ="select did from requests where uid=".$id;
	$result = $conn->query($sql);
	if($result->num_rows >0) 
	while($row = $result->fetch_assoc()){
	 $did= $row['did'];
	 $sql = "select * from androiduser where AUId=".$did;
	// echo $sql;
	  $result = $conn->query($sql);
	if($result->num_rows >0) 
	while($row = $result->fetch_assoc()){
	   $drivername = $row['Username'];
	}
		 $sql = "select * from locations where AUId=".$did;
	// echo $sql;
	  $result = $conn->query($sql);
	if($result->num_rows >0) 
	while($row = $result->fetch_assoc()){
	   $lon = $row['Longtitude'];
	  $lan = $row['Latitude'];
	}
	
	}
	else echo $conn->error;
	
		$data = array('name'=>$drivername,'lon'=>$lon,'lan'=>$lan);
			echo  json_encode($data);
}
}
?>