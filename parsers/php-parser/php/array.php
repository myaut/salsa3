<?

$e = array();
$i = array(10, 20, 35);
$a = array("a" => 100500, "b" => 9000);

define('ONE', 1);
define('TWO', 2);

function f() {
	static $v = array(
		ONE, TWO
	);
}

?>