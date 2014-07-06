<?php
switch($beer)
{
    case 'tuborg';
    case 'carlsberg';
    case 'heineken';
        echo 'Good choice';
    break;
    case 'some-other-beer';
    	echo 'Acceptable.';
    break;
    default;
        echo 'Please make a new selection...';
    break;
}
?>
