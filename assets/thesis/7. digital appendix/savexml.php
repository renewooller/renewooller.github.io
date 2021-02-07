<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/xml; charset=iso-8859-1">
<title>saveXML</title>
</head>

<body>
<?php
 //Capture data from $_POST array
$tosave = "$HTTP_RAW_POST_DATA";
$name = simplexml_load_string($tosave)->participant;
if(empty($name)) {
	$name = "unnamed";
}

if(empty($tosave)) {
	$tosave = "<nodata />";
}

//Open a file in write mode
$fp = fopen("responses/" . "$name" . ".xml", "w");
fwrite($fp, "$tosave");
fclose($fp);

$result = file_get_contents("responses/" . "$name" . ".xml");
echo "<br>result = " . "$result";
?>
</body>
</html>