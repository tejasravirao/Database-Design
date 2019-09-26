<!DOCTYPE html>
<?php
if(!isset($_POST['submit2'])&&!isset($_POST['sub2'])){
header('Location: newpage1.php');
}
?>
<html>
<head>
<!-- 	<script src="jquery-3.3.1.min.js"></script>
 --></head>
<body>	<center>
	<h1><u> Library System</u><hr/></h1>
	<h2>Checkout using registered card-id</h2>
	<form action="newpage2.php?tds" method="POST">
	<input id="chck1" type="text" required name="checkout1">
	<br/> <br/>
	<input id="find1" type="submit" name="sub2" value="checkout book">
	</form>
	<br/>
<a href="newpage1.php">Home</a><br/>
<a href="newpage5.php">New User registration</a><br/>
<a href="newpage3.php">Check-In Book</a><br/>
<a href="newpage5.php">New User registration</a><br/>
<?php
session_start();
if(!isset($_SESSION['pico'])){
	$_SESSION['pico']= $_POST['rd1'];
}
 //echo $_SESSION['pico'];
if(isset($_POST['sub2']))
{

if(isset($_GET['tds']))
	{
	$varx = $_POST['checkout1'];
	//echo $varx;
	$vary = $_SESSION['pico'];
	//echo $vary;
	// echo '<form method="POST" action="newpage1.php">';
	$conn = mysqli_connect("localhost","root","password","library");

	$sql2 = "SELECT * FROM book_loans WHERE card_id = '$varx' AND date_in = '0000-00-00';";

	$result2 = mysqli_query($conn,$sql2);

	if($result2->num_rows + 1 > 3){
		echo "User ";
		echo $varx;
		echo " cannot loan more than 3 books";
	}
	else{
		date_default_timezone_set("America/Chicago");
		$varz = Date("Y-m-d");
		//echo $varz;
		//date_default_timezone_set("America/Chicago");
		$vart = Date("Y-m-d", strtotime("+ 14 days"));
		//echo $vart;

		$sql3 = "INSERT INTO book_loans(isbn,card_id,date_out,due_date,date_in) values ('$vary','$varx','$varz','$vart','0000-00-00');";
		//echo $sql3;
		$sql4 = "UPDATE books SET available = '0' WHERE books.isbn = '$vary';";
		//echo $sql4;
		

		if(($conn->query($sql3)) && ($conn->query($sql4))){

			echo "Checked out successfully";
			session_destroy();


		}
		else {

			echo "checkout not possible";
			session_destroy();
		}




	}









}
else{

	header('Location: newpage1.php');
}

}


?>
</center>
</body>
</html>