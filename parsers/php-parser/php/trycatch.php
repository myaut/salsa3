<?php

try {
	try {
		do_something_nasty();
	}
	catch(Exception $e) {
		throw $e;
	}
}
catch(SomeException $e) {
	echo $e;
}
catch(OtherException $e) {
	echo $e;
}
catch(ThirdException $e) {
	echo $e;
}
finally {
	clean_the_desk();
}
