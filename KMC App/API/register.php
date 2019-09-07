<?php 

//
if($_GET['key'] == '1234'){
        $user = $_GET['username'];
        $pass = $_GET['password'];
        $userName = $_GET['name'];
        $userType = $_GET['userType'];
        $cdate = $_GET['cdate'];
    	include 'connection.php';
    	$conn = new MySQLi($server,$username,$password,$dbname);
        $sql = "select * from androiduser where Username='".$user."'";
        $result = $conn->query($sql); 
        
            if ($result->num_rows > 0) {
                die("Username already Exists!");
                exit();
            }
            else {
               
                	$sql ="INSERT INTO `androiduser`(`name`, `Username`, `Pass`, `type`, `CDate`) VALUES ('".$userName."','".$user. "','".$pass. "','".$userType."','".$cdate. "')";
             
                	if($conn->query($sql) == true){
                	    
                    	$sql = "select  * from androiduser where Username like '".$user."'";
                    	$result = $conn->query($sql); 
                                while($row = $result->fetch_assoc()) 
                            		$auid= $row['AUId'];
                    $sql = "INSERT INTO `locations`(`AUId`, `Latitude`, `Longtitude`) VALUES (".$auid.",'','')";
                    		if($conn->query($sql) == true)
                              echo "1";
                             else 
                             echo $conn->error;
                    }
                     else{
                    
                            $conn->error;
                    }
                 
            }
}
                    



?>