<?php

$a = 10;
$b = 1;

if($a > 20) {
	echo "X";
	
	if($b > 15) {
		echo "Q";
	}
}
else if($a < 5) {
	echo "Y";
}
else if($a > 5) {
	echo "OO";
}
else {
	echo 'Y';
}

if($b > 5) {	
	echo '1';
}
elseif($b < 10) {
	echo '10';
}

?>