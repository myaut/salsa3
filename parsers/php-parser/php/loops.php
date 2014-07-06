<?

$a = 0;
$x = array("a" => 10, "b" => 20);

while($a < 20) {
	$a++;
}

do {
	if($a > 30) {
		break;
	}
	if($b > 30) {
		continue;
	}
} while($a > 0);


for($b = 0; $b < 10 && $a > 30; $b--, $a++) {
	echo $b;
}

foreach($x as $k => $v) {
	echo $k . ":" . $v;
}

foreach(array(10, 20) as $i) {
	echo i;
}

?>