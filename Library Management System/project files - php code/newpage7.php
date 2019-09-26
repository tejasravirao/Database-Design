<!DOCTYPE html>
<?php
if(!isset($_POST['submit4'])){

	header('Location: newpage6.php');
}

?>
<html>
<head>
</head>
<body>
	<center>
<a href="newpage6.php">Go back</a><br/>
<a href="newpage3.php">Check-In Book</a><br/>
<a href="newpage5.php">New User registration</a><br/>
<a href="newpage1.php">Home</a><br/>
<a href="newpage8.php">Refresh fine</a><br/>


<br/>
<?php
if(isset($_POST['submit4'])){

	$var18 = $_POST['rd3'];

	$conn = mysqli_connect("localhost","root","password","library");
	date_default_timezone_set("America/Chicago");


	$sql18 = "UPDATE fines SET paid = '1' WHERE loan_id = '$var18';";

	if($conn->query($sql18))
	{
		echo "Payment completed";
	}
	else 
	{
		echo "Payment not completed";
	}
}

?>
</center>
</body>
</html>


