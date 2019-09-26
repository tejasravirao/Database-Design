<!DOCTYPE html>
<html>
<head>
</head>
<body>
	<center>
	<h1><u> Library System</u><hr/></h1>
	<h2>Fine Updation</h2>
	<a href="newpage1.php">Home</a><br/>
	<a href="newpage6.php">Pay fine</a><br/>
	<a href="newpage3.php">Check-In Book</a><br/>
	<a href="newpage5.php">New User registration</a><br/>
	<br/>

<?php
$conn = mysqli_connect("localhost","root","password","library");
date_default_timezone_set("America/Chicago");
$vars = Date("Y-m-d");
//echo $vars;

$sql12 = "SELECT * FROM book_loans WHERE (date_in > due_date) OR ('$vars' > due_date AND date_in = '0000-00-00');";
$result11 = mysqli_query($conn,$sql12);

//echo $result11->num_rows;
while($row = mysqli_fetch_array($result11)){
		if($row['date_in']>$row['due_date']){

			$vard1 = date_create($row['date_in']);
			$vard2 = date_create($row['due_date']);
			$date_diff1 = date_diff($vard1,$vard2)->format("%a");
			//echo "returned after due date";
			//echo $date_diff1;
			//echo "//";
			$fineamt1 = $date_diff1 * 0.25;
			}
		else if (($vars > $row['due_date']) && ($row['date_in'] =='0000-00-00')){

			$vard1 = date_create($vars);
			$vard2 = date_create($row['due_date']);
			$date_diff1 = date_diff($vard1,$vard2)->format("%a");
			//echo "still not returned";
			//echo $date_diff2;
			//echo"//";
			$fineamt1 = $date_diff1 * 0.25;
		}
		$varld = $row['loan_id'];
		//echo $varld;

		$sql13 = "SELECT * FROM fines WHERE loan_id = '$varld';";
		$result12 = mysqli_query($conn,$sql13);

		if($result12->num_rows>0){
		$row = mysqli_fetch_array($result12);
				if($row['paid']=='0'){

					$sql14 = "UPDATE fines SET fine_amt = '$fineamt1' WHERE loan_id = '$varld';";
					if($conn->query($sql14)){

						echo "Z"."<br/>";
					}
				}
		}
				

		else {
				$sql15 = "INSERT INTO fines (loan_id,fine_amt,paid) values ('$varld','$fineamt1','0');";
				if($conn->query($sql15)){

						echo "Z"."<br/>";
				}

			}



}

echo "Fines refreshed successfully";	
?>
</center>
</body>
</html>