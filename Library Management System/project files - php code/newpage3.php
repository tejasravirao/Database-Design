<!DOCTYPE html>
	

<html>
<head>
</head>
<body>	<center>
	<h1><u> Library System</u><hr/></h1>
	<h2>Please checkin book by ISBN13/Card Id/Name</h2>

	<form action="newpage3.php?tds" method="POST">
	<input id="chck2" type="text" required name="chekinbu">
	<br/> <br/>
	<input id="find2" type="submit" name="sub3" value="Find Loan">
	</form>
	<br/>

<a href="newpage1.php">Home</a><br/>
<a href="newpage5.php">New User registration</a><br/>
<a href="newpage6.php">Pay Fine</a><br/>
<a href="newpage3.php">Check-In Book</a><br/>
<a href="newpage8.php">Refresh fines</a><br/>
<?php
if(isset($_POST['sub3']))
{
	if(isset($_GET['tds']))
	{
		$var7 = $_POST['chekinbu'];
		//echo $var7;
		echo '<form method="POST" action="newpage4.php">';
		$conn = mysqli_connect("localhost","root","password","library");

		$sql5 = "SELECT bl.card_id, br.bname, bl.isbn,bo.title,bl.due_date FROM book_loans AS bl, borrowers AS br,books as bo
		WHERE (br.bname like '%$var7%' OR bl.isbn = '$var7' OR bl.card_id = '$var7') AND br.card_id = bl.card_id AND bo.isbn = bl.isbn AND bl.date_in='0000-00-00';";
		$result5 = mysqli_query($conn,$sql5);
		//echo $result5->num_rows;
		echo "<br/>Results for: ";
		echo $var7;
		echo "<br/>";
		if($result5->num_rows>0){


			echo "<br/><table border ='1'>";
			echo "<tr><th>Card Id</th><th>Name</th><th>isbn13</th><th>title</th><th>due_date</th><th></th></tr>";
			while($row = mysqli_fetch_array($result5))
			{

				$var8 = $row['isbn'];
				echo "<tr><td>".$row['card_id']."</td><td>".$row['bname']."</td><td>".$row['isbn']."</td>
					<td>".$row['title']."</td><td>".$row['due_date']."</td><td><input type='radio' name='rd2' id='lib2' value='$var8'></td></tr>";
				//$result->num_rows--;
			}
			echo "</table><br/>";
			echo 'Check-In Book: <input  type="submit" name="submit3" value="check-in"><br/>';
			echo "</form>";

					

		}


		else {echo "no record found";}

	}
	else{

			
	header("Location: newpage1.php");


	}



}



?>


</center>
</body>
</html>
