<!DOCTYPE html>

<?php
if(!isset($_POST['submit3'])){

	header('Location: newpage3.php');
}

?>
<html>
<head>
</head>
<body>
	<center>
<a href="newpage3.php">Go back</a><br/>
<a href="newpage1.php">Home</a><br/>
<a href="newpage8.php">Refresh fines</a><br/><br/>




<?php
if(isset($_POST['submit3'])){
	$var9 = $_POST['rd2'];
	//echo $var9;

	$conn = mysqli_connect("localhost","root","password","library");
	date_default_timezone_set("America/Chicago");
	$varf = Date("Y-m-d");
	$sql7 = "UPDATE book_loans AS bl SET date_in='$varf' where bl.isbn = '$var9';";
	$sql8 = "UPDATE books SET available = '1' WHERE isbn = '$var9';";

	if(($conn->query($sql7)) && ($conn->query($sql8))){


		echo "<br/> checked in successfully";



	}
	else{
			echo "did not happen";

	}









}

?>
</center>
</body>
</html>