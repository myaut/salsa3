<?php

// base class with member properties and methods
class Vegetable {
   var $edible;
   private $color;

   const DEFAULT_COLOR = 'green';

   function Vegetable($edible, $color="green") 
   {
       $this->edible = $edible;
       $this->color = $color;
   }

   function is_edible() 
   {
       return $this->edible;
   }

   function what_color() 
   {
       return $this->color;
   }
   
} // end of class Vegetable

interface Cookable
{
    public function cook_it();
    public function is_cooked();
}

// extends the base class
class Spinach extends Vegetable implements Cookable {
   var $cooked = false;
   var $tasty = false;

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
   
} // end of class Spinach

$v = new Spinach();
$v->cook_it();

?>
