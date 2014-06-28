<?

function plain_echo($arg) {
	echo $arg;
}

function base64_echo($arg) {
	echo base64_decode($arg);
}

if(isset($_GET['str'])) {
	plain_echo($_GET['str']);
}
else if(isset($_GET['b64'])) {
	base64_echo($_GET['b64']);
}

?>