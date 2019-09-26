<!DOCTYPE html>
<html>
<head>
</head>
<body>
	<center>
	<h1><u> Library System</u><hr/></h1>
	<h2>New User Registration</h2>


	<form action="newpage5.php?tds" method="POST">
	SSN: <input id="usr1" type="text" required name="usrin1">
	<br/> <br/>
	Name: <input id="usr2" type="text" required name="usrin2">
	<br/> <br/>
	Address: <input id="usr3" type="text" required name="usrin3">
	<br/> <br/>
	Phone: <input id="usr4" type="text" required name="usrin4">
	<br/> <br/>
	<input id="find3" type="submit" name="sub4" value="Register">
	</form>
	<br/>
	<a href="newpage1.php">Home</a><br/>
	<a href="newpage8.php">Refresh fines</a><br/>
	<a href="newpage3.php">Check-In Book</a><br/>
	<a href="newpage5.php">New User registration</a><br/>
	<br/>

<?php

if(isset($_POST['sub4']))
{
	if(isset($_GET['tds']))
	{
		$var10 = $_POST['usrin1'];
		//echo $var10;
		$var11 = $_POST['usrin2'];
		//echo $var11;
		$var12 = $_POST['usrin3'];
		//echo $var12;
		$var13 = $_POST['usrin4'];
		//echo $var13;
		$conn = mysqli_connect("localhost","root","password","library");

		date_default_timezone_set("America/Chicago");

		$sql9 = "SELECT * FROM borrowers AS br WHERE br.ssn = '$var10' ;";
		$result7 = mysqli_query($conn,$sql9);

		if($result7->num_rows>0){

			echo"user already exists, please input different ssn";

		}

		else{

			$sql10 = "INSERT INTO borrowers(ssn,bname,address,phone) values('$var10','$var11','$var12','$var13');";

			if($conn->query($sql10)){


				$sql11 = "SELECT card_id FROM borrowers AS br WHERE br.ssn='$var10';";
				$result9 = mysqli_query($conn,$sql11);

				
					$row = mysqli_fetch_array($result9);
					$vark = $row['card_id'];

					echo "You are now registered, Your Card ID is:";
					echo $vark;


				




			}
			else{

				echo "check code outer";
			}




		}
	}
	else{

		echo "enter proper details";
	}



}




?>
</center>
</body>
</html>