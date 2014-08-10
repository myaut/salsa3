package com.tuneit.salsa3.ast.serdes.annotations;

import java.lang.annotation.*;

@Target(value=ElementType.FIELD)
@Retention(value= RetentionPolicy.RUNTIME)
public @interface Parameter {
	int offset();
	boolean optional();
}
