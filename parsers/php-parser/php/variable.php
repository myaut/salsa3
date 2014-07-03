<?

const a = 10;

$a = 1.1;

function f() {
	static $a;
	static $x = 10;
	
	$b = (int) $a + a;
	
	return $b;
}

?>