<!DOCTYPE html>

<html>
<head>
</head>
<body>
	<center>
	<h1><u>Library System</u><hr/></h1>
	<h2>Fine Payment</h2>
	<a href="newpage1.php">Home</a><br/>
	<a href="newpage8.php">Refresh fines</a><br/>
	<a href="newpage3.php">Check-In Book</a><br/>
	<a href="newpage5.php">New User registration</a><br/>
	<br>

<form action="newpage6.php?tds" method="POST">
	Enter Card Id: <input id="finp1" type="text" required name="pay1">
	<br/> <br/>
	<input id="find4" type="submit" name="sub5" value="Search to pay">
	</form>
	<br/>
<?php
if(isset($_POST['sub5']))
{
	if(isset($_GET['tds']))
	{

		$var14 = $_POST['pay1'];
		//echo $var14;
		echo '<form method="POST" action="newpage7.php">';
		
		$conn = mysqli_connect("localhost","root","password","library");
		date_default_timezone_set("America/Chicago");

		$sql16 = "SELECT * from books NATURAL JOIN book_loans as bl NATURAL JOIN fines WHERE fines.paid = '0' AND bl.card_id = '$var14';";
		$result15 = mysqli_query($conn,$sql16);
		 echo $result15->num_rows;
		if($result15->num_rows>0){

			echo "<table border='1'>";
			echo "<tr><th>Card Id</th><th>ISBN</th><th>Title</th><th>Due Date</th><th>CheckIn Date</th><th>Fine</th><th>Pay</th></tr>";

			while($row = mysqli_fetch_array($result15)){

				$varlid = $row['loan_id'];

				if($row['date_in'] != '0000-00-00'){
					echo "<tr><td>".$row['card_id']."</td><td>".$row['isbn']."</td><td>".$row['title']."</td>
					<td>".$row['due_date']."</td><td>".$row['date_in']."</td><td> $ ".$row['fine_amt']."</td><td><input type='radio' name='rd3' id='lib3' value='$varlid'></td></tr>";
				}
				else{
					echo"<tr><td>".$row['card_id']."</td><td>".$row['isbn']."</td><td>".$row['title']."</td>
					<td>".$row['due_date']."</td><td>".$row['date_in']."</td><td> $ ".$row['fine_amt']."</td></tr>";

					}

			}

		echo "</table>";

		$sql17 = "SELECT SUM(fine_amt) as topay FROM fines NATURAL JOIN book_loans as bl where fines.paid ='0'AND bl.card_id = '$var14';";
		$result16 = mysqli_query($conn,$sql17);

		$row2 = mysqli_fetch_array($result16);

		$varxx = $row2['topay'];

		echo " Please pay total fine amount: $";
		echo $varxx;
		echo "<br/>";
		echo '<input  type="submit" name="submit4" value="Pay">';
		echo "</form>";


	}
	else{ 
		echo "Not found";

	}

 }
else{

	echo "Please enter proper details";
}
}

?>
</center>
</body>
</html>