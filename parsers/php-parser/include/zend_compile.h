/*
 * zend_compile.h
 *
 *  Created on: Jun 20, 2014
 *      Author: myaut
 */

/*
   +----------------------------------------------------------------------+
   | Zend Engine                                                          |
   +----------------------------------------------------------------------+
   | Copyright (c) 1998-2014 Zend Technologies Ltd. (http://www.zend.com) |
   +----------------------------------------------------------------------+
   | This source file is subject to version 2.00 of the Zend license,     |
   | that is bundled with this package in the file LICENSE, and is        |
   | available through the world-wide-web at the following url:           |
   | http://www.zend.com/license/2_00.txt.                                |
   | If you did not receive a copy of the Zend license and are unable to  |
   | obtain it through the world-wide-web, please send a note to          |
   | license@zend.com so we can mail you a copy immediately.              |
   +----------------------------------------------------------------------+
   | Authors: Andi Gutmans <andi@zend.com>                                |
   |          Zeev Suraski <zeev@zend.com>                                |
   +----------------------------------------------------------------------+
*/

#ifndef ZEND_COMPILE_H_
#define ZEND_COMPILE_H_

#include <zend.h>
#include <zend_API.h>

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

/* var status for backpatching */
#define BP_VAR_R			0
#define BP_VAR_W			1
#define BP_VAR_RW			2
#define BP_VAR_IS			3
#define BP_VAR_NA			4	/* if not applicable */
#define BP_VAR_FUNC_ARG		5
#define BP_VAR_UNSET		6

/* variable parsing type (compile-time) */
#define ZEND_PARSED_MEMBER				(1<<0)
#define ZEND_PARSED_METHOD_CALL			(1<<1)
#define ZEND_PARSED_STATIC_MEMBER		(1<<2)
#define ZEND_PARSED_FUNCTION_CALL		(1<<3)
#define ZEND_PARSED_VARIABLE			(1<<4)
#define ZEND_PARSED_REFERENCE_VARIABLE	(1<<5)
#define ZEND_PARSED_NEW					(1<<6)
#define ZEND_PARSED_LIST_EXPR			(1<<7)

#define ZEND_EVAL				(1<<0)
#define ZEND_INCLUDE			(1<<1)
#define ZEND_INCLUDE_ONCE		(1<<2)
#define ZEND_REQUIRE			(1<<3)
#define ZEND_REQUIRE_ONCE		(1<<4)

/* class fetches */
#define ZEND_FETCH_CLASS_DEFAULT	0
#define ZEND_FETCH_CLASS_SELF		1
#define ZEND_FETCH_CLASS_PARENT		2
#define ZEND_FETCH_CLASS_MAIN		3	/* unused */
#define ZEND_FETCH_CLASS_GLOBAL		4	/* unused */
#define ZEND_FETCH_CLASS_AUTO		5
#define ZEND_FETCH_CLASS_INTERFACE	6
#define ZEND_FETCH_CLASS_STATIC		7
#define ZEND_FETCH_CLASS_TRAIT		14
#define ZEND_FETCH_CLASS_MASK        0x0f

#define ZEND_CT	(1<<0)
#define ZEND_RT (1<<1)

#define IS_CONST	(1<<0)
#define IS_TMP_VAR	(1<<1)
#define IS_VAR		(1<<2)
#define IS_UNUSED	(1<<3)	/* Unused variable */
#define IS_CV		(1<<4)	/* Compiled variable */

#define ZEND_ISSET				    0x02000000
#define ZEND_ISEMPTY			    0x01000000

/* method flags (types) */
#define ZEND_ACC_STATIC			0x01
#define ZEND_ACC_ABSTRACT		0x02
#define ZEND_ACC_FINAL			0x04
#define ZEND_ACC_IMPLEMENTED_ABSTRACT		0x08

/* class flags (types) */
/* ZEND_ACC_IMPLICIT_ABSTRACT_CLASS is used for abstract classes (since it is set by any abstract method even interfaces MAY have it set, too). */
/* ZEND_ACC_EXPLICIT_ABSTRACT_CLASS denotes that a class was explicitly defined as abstract by using the keyword. */
#define ZEND_ACC_IMPLICIT_ABSTRACT_CLASS	0x10
#define ZEND_ACC_EXPLICIT_ABSTRACT_CLASS	0x20
#define ZEND_ACC_FINAL_CLASS	            0x40
#define ZEND_ACC_INTERFACE		            0x80
#define ZEND_ACC_TRAIT						0x120

/* op_array flags */
#define ZEND_ACC_INTERACTIVE				0x10

/* method flags (visibility) */
/* The order of those must be kept - public < protected < private */
#define ZEND_ACC_PUBLIC		0x100
#define ZEND_ACC_PROTECTED	0x200
#define ZEND_ACC_PRIVATE	0x400
#define ZEND_ACC_PPP_MASK  (ZEND_ACC_PUBLIC | ZEND_ACC_PROTECTED | ZEND_ACC_PRIVATE)

#define ZEND_ACC_CHANGED	0x800
#define ZEND_ACC_IMPLICIT_PUBLIC	0x1000

/* method flags (special method detection) */
#define ZEND_ACC_CTOR		0x2000
#define ZEND_ACC_DTOR		0x4000
#define ZEND_ACC_CLONE		0x8000

/* method flag (bc only), any method that has this flag can be used statically and non statically. */
#define ZEND_ACC_ALLOW_STATIC	0x10000

/* shadow of parent's private method/property */
#define ZEND_ACC_SHADOW 0x20000

/* deprecation flag */
#define ZEND_ACC_DEPRECATED 0x40000

/* class implement interface(s) flag */
#define ZEND_ACC_IMPLEMENT_INTERFACES 0x80000
#define ZEND_ACC_IMPLEMENT_TRAITS	  0x400000

/* class constants updated */
#define ZEND_ACC_CONSTANTS_UPDATED	  0x100000

/* user class has methods with static variables */
#define ZEND_HAS_STATIC_IN_METHODS    0x800000


#define ZEND_ACC_CLOSURE              0x100000
#define ZEND_ACC_GENERATOR            0x800000

/* function flag for internal user call handlers __call, __callstatic */
#define ZEND_ACC_CALL_VIA_HANDLER     0x200000

/* disable inline caching */
#define ZEND_ACC_NEVER_CACHE          0x400000

#define ZEND_ACC_VARIADIC				0x1000000

#define ZEND_ACC_RETURN_REFERENCE		0x4000000
#define ZEND_ACC_DONE_PASS_TWO			0x8000000

/* function has arguments with type hinting */
#define ZEND_ACC_HAS_TYPE_HINTS			0x10000000

#define ZEND_RETURN_VAL 0
#define ZEND_RETURN_REF 1

/* global/local fetches */
#define ZEND_FETCH_GLOBAL			0x00000000
#define ZEND_FETCH_LOCAL			0x10000000
#define ZEND_FETCH_STATIC			0x20000000
#define ZEND_FETCH_STATIC_MEMBER	0x30000000
#define ZEND_FETCH_GLOBAL_LOCK		0x40000000
#define ZEND_FETCH_LEXICAL			0x50000000

void zend_do_early_binding(TSRMLS_D);

/* parser-driven code generators */
void zend_do_binary_op(zend_uchar op, znode *result, const znode *op1, const znode *op2 TSRMLS_DC);
void zend_do_unary_op(zend_uchar op, znode *result, const znode *op1 TSRMLS_DC);
void zend_do_binary_assign_op(zend_uchar op, znode *result, const znode *op1, const znode *op2 TSRMLS_DC);
void zend_do_assign(znode *result, znode *variable, znode *value TSRMLS_DC);
void zend_do_assign_ref(znode *result, const znode *lvar, const znode *rvar TSRMLS_DC);
void fetch_simple_variable(znode *result, znode *varname, int bp TSRMLS_DC);
void fetch_simple_variable_ex(znode *result, znode *varname, int bp, zend_uchar op TSRMLS_DC);
void zend_do_indirect_references(znode *result, const znode *num_references, znode *variable TSRMLS_DC);
void zend_do_fetch_static_variable(znode *varname, const znode *static_assignment, int fetch_type TSRMLS_DC);
void zend_do_fetch_global_variable(znode *varname, const znode *static_assignment, int fetch_type TSRMLS_DC);

void fetch_array_begin(znode *result, znode *varname, znode *first_dim TSRMLS_DC);
void fetch_array_dim(znode *result, const znode *parent, const znode *dim TSRMLS_DC);
void fetch_string_offset(znode *result, const znode *parent, const znode *offset TSRMLS_DC);
void zend_do_fetch_static_member(znode *result, znode *class_znode TSRMLS_DC);
void zend_do_print(znode *result, const znode *arg TSRMLS_DC);
void zend_do_echo(const znode *arg TSRMLS_DC);
typedef int (*unary_op_type)(zval *, zval * TSRMLS_DC);
typedef int (*binary_op_type)(zval *, zval *, zval * TSRMLS_DC);
ZEND_API unary_op_type get_unary_op(int opcode);
ZEND_API binary_op_type get_binary_op(int opcode);

void zend_do_while_cond(const znode *expr, znode *close_bracket_token TSRMLS_DC);
void zend_do_while_end(const znode *while_token, const znode *close_bracket_token TSRMLS_DC);
void zend_do_do_while_begin(TSRMLS_D);
void zend_do_do_while_end(const znode *do_token, const znode *expr_open_bracket, const znode *expr TSRMLS_DC);


void zend_do_if_cond(const znode *cond, znode *closing_bracket_token TSRMLS_DC);
void zend_do_if_after_statement(const znode *closing_bracket_token, unsigned char initialize TSRMLS_DC);
void zend_do_if_end(TSRMLS_D);

void zend_do_for_begin(TSRMLS_DC);
void zend_do_for_cond(const znode *expr, znode *second_semicolon_token TSRMLS_DC);
void zend_do_for_before_statement(const znode *cond_start, const znode *second_semicolon_token TSRMLS_DC);
void zend_do_for_end(const znode *second_semicolon_token TSRMLS_DC);

void zend_do_pre_incdec(znode *result, const znode *op1, zend_uchar op TSRMLS_DC);
void zend_do_post_incdec(znode *result, const znode *op1, zend_uchar op TSRMLS_DC);

void zend_do_begin_variable_parse(TSRMLS_D);
void zend_do_end_variable_parse(znode *variable, int type, int arg_offset TSRMLS_DC);

void zend_check_writable_variable(const znode *variable);

void zend_do_free(znode *op1 TSRMLS_DC);

void zend_do_add_string(znode *result, const znode *op1, znode *op2 TSRMLS_DC);
void zend_do_add_variable(znode *result, const znode *op1, const znode *op2 TSRMLS_DC);

int zend_do_verify_access_types(const znode *current_access_type, const znode *new_modifier);
void zend_do_begin_function_declaration(znode *function_token, znode *function_name, int is_method, int return_reference, znode *fn_flags_znode TSRMLS_DC);
void zend_do_end_function_declaration(const znode *function_token TSRMLS_DC);
void zend_do_receive_arg(zend_uchar op, znode *varname, const znode *offset, const znode *initialization,
							znode *class_type, zend_bool pass_by_reference TSRMLS_DC);
int zend_do_begin_function_call(znode *function_name, zend_bool check_namespace TSRMLS_DC);
void zend_do_begin_method_call(znode *left_bracket TSRMLS_DC);
void zend_do_clone(znode *result, const znode *expr TSRMLS_DC);
void zend_do_begin_dynamic_function_call(znode *function_name, int prefix_len TSRMLS_DC);
void zend_do_fetch_class(znode *result, znode *class_name TSRMLS_DC);
void zend_do_build_full_name(znode *result, znode *prefix, znode *name, int is_class_member TSRMLS_DC);
int zend_do_begin_class_member_function_call(znode *class_name, znode *method_name TSRMLS_DC);
void zend_do_end_function_call(znode *function_name, znode *result, const znode *argument_list, int is_method, int is_dynamic_fcall TSRMLS_DC);
void zend_do_return(znode *expr, int do_end_vparse TSRMLS_DC);
void zend_do_yield(znode *result, znode *value, const znode *key, zend_bool is_variable TSRMLS_DC);
void zend_do_handle_exception(TSRMLS_D);

void zend_do_begin_lambda_function_declaration(znode *result, znode *function_token, int return_reference, int is_static TSRMLS_DC);
void zend_do_fetch_lexical_variable(znode *varname, zend_bool is_ref TSRMLS_DC);

void zend_initialize_try_catch_element(znode *catch_token TSRMLS_DC);
void zend_do_try(znode *try_token TSRMLS_DC);
void zend_do_begin_catch(znode *try_token, znode *catch_class, znode *catch_var, znode *first_catch TSRMLS_DC);
void zend_do_bind_catch(znode *try_token, znode *catch_token TSRMLS_DC);
void zend_do_end_catch(znode *catch_token TSRMLS_DC);
void zend_do_finally(znode *finally_token TSRMLS_DC);
void zend_do_end_finally(znode *try_token, znode* catch_token, znode *finally_token TSRMLS_DC);
void zend_do_throw(const znode *expr TSRMLS_DC);
void zend_do_first_catch(znode *open_parentheses TSRMLS_DC);
void zend_do_mark_last_catch(const znode *first_catch, const znode *last_additional_catch TSRMLS_DC);

void zend_do_pass_param(znode *param, zend_uchar op, int offset TSRMLS_DC);

void zend_do_boolean_or_begin(znode *expr1, znode *op_token TSRMLS_DC);
void zend_do_boolean_or_end(znode *result, const znode *expr1, const znode *expr2, znode *op_token TSRMLS_DC);
void zend_do_boolean_and_begin(znode *expr1, znode *op_token TSRMLS_DC);
void zend_do_boolean_and_end(znode *result, const znode *expr1, const znode *expr2, const znode *op_token TSRMLS_DC);

void zend_do_brk_cont(zend_uchar op, const znode *expr TSRMLS_DC);

void zend_do_switch_cond(const znode *cond TSRMLS_DC);
void zend_do_switch_end(const znode *case_list TSRMLS_DC);
void zend_do_case_before_statement(const znode *case_list, znode *case_token, const znode *case_expr TSRMLS_DC);
void zend_do_case_after_statement(znode *result, const znode *case_token TSRMLS_DC);
void zend_do_default_before_statement(const znode *case_list, znode *default_token TSRMLS_DC);

void zend_do_begin_class_declaration(const znode *class_token, znode *class_name, const znode *parent_class_name TSRMLS_DC);
void zend_do_end_class_declaration(const znode *class_token, const znode *parent_token TSRMLS_DC);
void zend_do_declare_property(const znode *var_name, const znode *value, zend_uint access_type TSRMLS_DC);
void zend_do_declare_class_constant(znode *var_name, const znode *value TSRMLS_DC);

void zend_do_fetch_property(znode *result, znode *object, const znode *property TSRMLS_DC);

void zend_do_halt_compiler_register(TSRMLS_D);

void zend_do_push_object(const znode *object TSRMLS_DC);
void zend_do_pop_object(znode *object TSRMLS_DC);


void zend_do_begin_new_object(znode *new_token, znode *class_type TSRMLS_DC);
void zend_do_end_new_object(znode *result, const znode *new_token, const znode *argument_list TSRMLS_DC);

void zend_do_fetch_constant(znode *result, znode *constant_container, znode *constant_name, int mode, zend_bool check_namespace TSRMLS_DC);

void zend_do_shell_exec(znode *result, const znode *cmd TSRMLS_DC);

void zend_do_init_array(znode *result, const znode *expr, const znode *offset, zend_bool is_ref TSRMLS_DC);
void zend_do_add_array_element(znode *result, const znode *expr, const znode *offset, zend_bool is_ref TSRMLS_DC);
void zend_do_add_static_array_element(znode *result, znode *offset, const znode *expr);
void zend_do_list_init(TSRMLS_D);
void zend_do_list_end(znode *result, znode *expr TSRMLS_DC);
void zend_do_add_list_element(const znode *element TSRMLS_DC);
void zend_do_new_list_begin(TSRMLS_D);
void zend_do_new_list_end(TSRMLS_D);

/* Functions for a null terminated pointer list, used for traits parsing and compilation */
void zend_init_list(void *result, void *item TSRMLS_DC);
void zend_add_to_list(void *result, void *item TSRMLS_DC);


void zend_do_cast(znode *result, const znode *expr, int type TSRMLS_DC);
void zend_do_include_or_eval(int type, znode *result, const znode *op1 TSRMLS_DC);

void zend_do_unset(const znode *variable TSRMLS_DC);
void zend_do_isset_or_isempty(int type, znode *result, znode *variable TSRMLS_DC);

void zend_do_instanceof(znode *result, const znode *expr, const znode *class_znode, int type TSRMLS_DC);

void zend_do_foreach_begin(znode *foreach_token, znode *open_brackets_token, znode *array, znode *as_token, int variable TSRMLS_DC);
void zend_do_foreach_cont(znode *foreach_token, const znode *open_brackets_token, const znode *as_token, znode *value, znode *key TSRMLS_DC);
void zend_do_foreach_end(const znode *foreach_token, const znode *as_token TSRMLS_DC);

void zend_do_declare_begin(TSRMLS_D);
void zend_do_declare_stmt(znode *var, znode *val TSRMLS_DC);
void zend_do_declare_end(const znode *declare_token TSRMLS_DC);

void zend_do_exit(znode *result, const znode *message TSRMLS_DC);

void zend_do_begin_silence(znode *strudel_token TSRMLS_DC);
void zend_do_end_silence(const znode *strudel_token TSRMLS_DC);

void zend_do_jmp_set(const znode *value, znode *jmp_token, znode *colon_token TSRMLS_DC);
void zend_do_jmp_set_else(znode *result, const znode *false_value, const znode *jmp_token, const znode *colon_token TSRMLS_DC);

void zend_do_begin_qm_op(const znode *cond, znode *qm_token TSRMLS_DC);
void zend_do_qm_true(const znode *true_value, znode *qm_token, znode *colon_token TSRMLS_DC);
void zend_do_qm_false(znode *result, const znode *false_value, const znode *qm_token, const znode *colon_token TSRMLS_DC);

void zend_do_extended_info(TSRMLS_D);
void zend_do_extended_fcall_begin(TSRMLS_D);
void zend_do_extended_fcall_end(TSRMLS_D);

void zend_do_abstract_method(const znode *function_name, znode *modifiers, const znode *body TSRMLS_DC);

void zend_do_implements_interface(znode *interface_name TSRMLS_DC);

void zend_do_declare_constant(znode *name, znode *value TSRMLS_DC);
void zend_do_build_namespace_name(znode *result, znode *prefix, znode *name TSRMLS_DC);
void zend_do_begin_namespace(const znode *name, zend_bool with_brackets TSRMLS_DC);
void zend_do_end_namespace(TSRMLS_D);
static void zend_verify_namespace(TSRMLS_D) {}
void zend_do_use(znode *name, znode *new_name, int is_global TSRMLS_DC);
static void zend_do_end_compilation(TSRMLS_D) {}

void zend_do_resolve_class_name(znode *result, znode *class_name, int is_static TSRMLS_DC);

void zend_do_label(znode *label TSRMLS_DC);
void zend_do_goto(const znode *label TSRMLS_DC);
void zend_resolve_goto_label(zend_op_array *op_array, zend_op *opline, int pass2 TSRMLS_DC);
void zend_release_labels(int temporary TSRMLS_DC);

void zend_do_use_trait(znode *trait_name TSRMLS_DC);

void zend_add_trait_alias(znode *method_reference, znode *modifiers, znode *alias TSRMLS_DC);
void zend_add_trait_precedence(znode *method_reference, znode *trait_list TSRMLS_DC);

void zend_prepare_reference(znode *result, znode *class_name, znode *method_name TSRMLS_DC);

int add_function(zval *result, zval *op1, zval *op2 TSRMLS_DC);
int sub_function(zval *result, zval *op1, zval *op2 TSRMLS_DC);

/* Stub */
static void zend_resolve_class_name(znode *class_name, ulong fetch_type, int check_ns_name TSRMLS_DC) {}

#endif /* ZEND_COMPILE_H_ */
