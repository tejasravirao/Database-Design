<!DOCTYPE html>
<html>
<head>
<!-- 	<script src="jquery-3.3.1.min.js"></script>
 --></head>
<body><center>
	<h1><u> Library System</u><hr/></h1>
	<h2>Search by ISBN13, Author or Title </h2>
	<form action="newpage1.php?tds" method="POST">
	<input id="expl1" type="text" required name="explorebook">
	<br/> <br/>
	<input id="find" type="submit" name="sub1" value="Find book">
	</form>
	<br/>

<a href="newpage1.php">Refresh Home</a><br/>
<a href="newpage3.php">Check-In Book</a><br/>
<a href="newpage5.php">New User registration</a><br/>
<a href="newpage6.php">Pay Fine</a><br/>
<a href="newpage8.php">Refresh fines</a><br/>

<?php

if(isset($_POST['sub1']))
{
	if(isset($_GET['tds']))
	{

		$var = $_POST['explorebook'];

		// if($var == ""){
		// 	echo "Please enter something";
		// }

		// else{
		echo '<form method="POST" action="newpage2.php">';
		echo'<br/>';
		echo 'Checkout: <input  type="submit" name="submit2" value="check-out"><br/>';
		echo "<br/>Results for:  ";
		echo $var;
		echo "<br/>"; 

		$conn = mysqli_connect("localhost","root","password","library");
	
		$sql1 = "SELECT  b.isbn,b.title,a.name,b.available 
		FROM books AS b,authors AS a,book_authors AS ba 
		WHERE (b.isbn = '$var' OR b.title like '%$var%' OR a.name like '%$var%') AND b.isbn = ba.isbn AND a.author_id = ba.author_id;";
		$result = mysqli_query($conn,$sql1);

	echo "<br/><table border='1'>";
	echo "<tr><th>isbn</th><th>title</th><th>author</th><th>available</th><th></th></tr>";
			while($row = mysqli_fetch_array($result))
			{
				$var2 = $row['isbn'];
				if($row['available']=='1'){
					
				echo "<tr><td>".$row['isbn']."</td><td>".$row['title']."</td><td>".$row['name']."</td><td>".$row['available']."</td>
					<td><input type='radio' name='rd1' id='lib1' value='$var2'></td></tr>";
					}
				else{
				echo "<tr><td>".$row['isbn']."</td><td>".$row['title']."</td><td>".$row['name']."</td><td>".$row['available']."</td>
					</tr>";
				}
			}
			echo "</table>";
			
			echo "</form>";
		
// }
}
		else{

			echo "<p> enter proper details</p>";
		}






	}


?>


</center>
</body>
</html>


