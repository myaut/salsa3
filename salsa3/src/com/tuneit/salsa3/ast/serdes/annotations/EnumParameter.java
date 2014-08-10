package com.tuneit.salsa3.ast.serdes.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value=ElementType.FIELD)
@Retention(value= RetentionPolicy.RUNTIME)
public @interface EnumParameter {
	@SuppressWarnings("rawtypes")
	public Class enumClass();
}
