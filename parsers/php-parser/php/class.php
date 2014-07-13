<?php

// base class with member properties and methods
class Vegetable {
   var $edible = TRUE;
   private $color;

   const DEFAULT_COLOR = 'green';

   function Vegetable($edible, $color="green") 
   {
       $this->edible = $edible;
       $this->color = $this->color_to_hex($color);
   }
   
   private function color_to_hex($color) {
       if($color == "green") {
           return "#00FF00"; 
       }
       return "#000000";
   }

   function is_edible() 
   {
       return $this->edible;
   }

   function what_color() 
   {
       return $this->color;
   }
   
   public static function is_vegetable($object) {
   		return $object instanceof Vegetable;
   }   
}

interface Cookable {
    public function cook_it();
    public function is_cooked();
    
    public function cut_for_salad();
}

trait NotCuttable {
	public function cut_for_salad() {
		throw new Exception("Not cuttable for salad!");
	}
}

class Spinach extends Vegetable implements Cookable {
   var $cooked = false;
   var $tasty = false;
   
   use NotCuttable;

   function Spinach($tasty) 
   {
   	   $this->tasty = $tasty;
       $this->Vegetable(true, "green");
   }

   function cook_it() 
   {
       $this->cooked = true;
   }

   function is_cooked() 
   {
       return $this->cooked;
   }
   
}

$v = new Spinach(TRUE);
$v->cook_it();
echo Vegetable::is_vegetable($v) . "\n";
echo $v->what_color() . "\n";

?>
