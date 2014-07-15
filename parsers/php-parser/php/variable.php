<?

const a = 10;

$a = 1.1;
$b = 10;
$c = 11;

function f() {
	static $a;
	static $x = 10;
	global $b;
	global $c;
	
	$b = (int) $a + a;
	
	return $b;
}

?>