/*
*/

/* $Id$ */

#include "zend.h"
#include "zend_compile.h"
#include "zend_language_parser.h"

#include <stdio.h>
#include <stdlib.h>

zend_compiler_globals cg;


#ifdef SALSA3_PHP_DEBUG
int token_debug;
#endif

static void escaped_print_string(const char* str, int is_escaped) {
	while(*str != 0) {
		switch (*str) {
		  case '\"':
		    fputs("\\\"", stdout);
		    break;
		  case '\'':
		    fputs("\\\'", stdout);
		    break;
		  case '\\':
		    if(is_escaped) {
		    	fputc('\\', stdout);
		    }
		    else {
		    	fputs("\\\\", stdout);
		    }
			break;
		  case '\a':
		    fputs("\\a", stdout);
		    break;
		  case '\b':
			fputs("\\b", stdout);
		    break;
		  case '\n':
			fputs("\\n", stdout);
		    break;
		  case '\t':
			fputs("\\t", stdout);
		    break;
		  default:
		    fputc(*str, stdout);
		}

		++str;
	}
}

static void salsa3_begin(const char* state) {
	printf("{\"lineno\": %d, \"state\": \"%s\" ",
				CG(zend_lineno), state);
}

static void _salsa3_dump_int_param(const char* name, int param) {
	printf(", \"%s\": %d", name, param);
}

static void _salsa3_dump_znode(const char* name, const znode* arg) {
	if(arg == NULL)
		return;

	printf(", \"%s\" : {\"nodeid\": %lu", name, arg->nodeid);

	if(arg->op_type == IS_CONST) {
		const zval* zv = &arg->u.constant;

		switch(zv->type) {
		case IS_LONG:
			if(zv->token != NULL)
				printf(", \"value\": \"%s\"", zv->token);
			else
				printf(", \"value\": \"%l\"", Z_LVAL(*zv));
			break;
		case IS_DOUBLE:
			if(zv->token != NULL)
				printf(", \"value\": \"%s\"", zv->token);
			else
				printf(", \"value\": \"%e\"", Z_DVAL(*zv));
			break;
		case IS_STRING:
		case IS_CONSTANT:
			fputs(", \"value\": \"", stdout);
			escaped_print_string(Z_STRVAL_P(zv), Z_STRISESC_P(zv));
			fputc('\"', stdout);
			break;
		}

		printf(", \"type\": %d", zv->type);
	}

	fputs("}", stdout);
}

static void salsa3_end() {
	fputs("}\n", stdout);
}

#define salsa3_dump_int_param(param) _salsa3_dump_int_param(#param, param)
#define salsa3_dump_znode(node) _salsa3_dump_znode(#node, node)

#define salsa3_unimplimented() 									\
	do {														\
		fprintf(stderr, "'%s' is not implemented\n", __func__);	\
		abort();												\
	} while(0)

/* Zend compiler code  */

void zend_init_compiler_context(TSRMLS_D) /* {{{ */
{
	cg.zend_lineno = 0;
	cg.increment_lineno = 0;

	cg.access_type = 0;
	cg.active_op_array = -1;
	cg.parse_error = 0;

	cg.asp_tags = 1;
	cg.short_tags = 1;

	cg.doc_comment_len = 0;

	cg.zend_nodeid = 0;
}
/* }}} */

void zend_del_literal(zend_op_array *op_array, int n) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

int zend_add_literal(zend_op_array *op_array, const zval *zv TSRMLS_DC) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */


int zend_append_individual_literal(zend_op_array *op_array, const zval *zv TSRMLS_DC) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

int zend_add_func_name_literal(zend_op_array *op_array, const zval *zv TSRMLS_DC) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

int zend_add_ns_func_name_literal(zend_op_array *op_array, const zval *zv TSRMLS_DC) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

int zend_add_class_name_literal(zend_op_array *op_array, const zval *zv TSRMLS_DC) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

int zend_add_const_name_literal(zend_op_array *op_array, const zval *zv, int unqualified TSRMLS_DC) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

void zend_do_binary_op(zend_uchar op, znode *result, const znode *op1, const znode *op2 TSRMLS_DC) /* {{{ */
{
	salsa3_begin("binary_op");
	salsa3_dump_int_param(op);
	salsa3_dump_znode(result);
	salsa3_dump_znode(op1);
	salsa3_dump_znode(op2);
	salsa3_end();
}
/* }}} */

void zend_do_unary_op(zend_uchar op, znode *result, const znode *op1 TSRMLS_DC) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

void zend_do_binary_assign_op(zend_uchar op, znode *result, const znode *op1, const znode *op2 TSRMLS_DC) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

void fetch_simple_variable_ex(znode *result, znode *varname, int bp, zend_uchar op TSRMLS_DC) /* {{{ */
{
	salsa3_begin("fetch_simple_variable_ex");
	salsa3_dump_int_param(bp);
	salsa3_dump_int_param(op);
	salsa3_dump_znode(result);
	salsa3_dump_znode(varname);
	salsa3_end();
}
/* }}} */

void fetch_simple_variable(znode *result, znode *varname, int bp TSRMLS_DC) /* {{{ */
{
	salsa3_begin("fetch_simple_variable");
	salsa3_dump_int_param(bp);
	salsa3_dump_znode(result);
	salsa3_dump_znode(varname);
	salsa3_end();
}
/* }}} */

void zend_do_fetch_static_member(znode *result, znode *class_name TSRMLS_DC) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

void fetch_array_begin(znode *result, znode *varname, znode *first_dim TSRMLS_DC) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

void fetch_array_dim(znode *result, const znode *parent, const znode *dim TSRMLS_DC) /* {{{ */
{
	salsa3_begin("array_dim");
	salsa3_dump_znode(result);
	salsa3_dump_znode(parent);
	salsa3_dump_znode(dim);
	salsa3_end();
}
/* }}} */

void fetch_string_offset(znode *result, const znode *parent, const znode *offset TSRMLS_DC) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

void zend_do_print(znode *result, const znode *arg TSRMLS_DC) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

void zend_do_echo(const znode *arg TSRMLS_DC) /* {{{ */
{
	salsa3_begin("echo");
	salsa3_dump_znode(arg);
	salsa3_end();
}
/* }}} */

void zend_do_abstract_method(const znode *function_name, znode *modifiers, const znode *body TSRMLS_DC) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

void zend_do_assign(znode *result, znode *variable, znode *value TSRMLS_DC) /* {{{ */
{
	salsa3_begin("assign");
	salsa3_dump_znode(result);
	salsa3_dump_znode(variable);
	salsa3_dump_znode(value);
	salsa3_end();
}
/* }}} */

void zend_do_assign_ref(znode *result, const znode *lvar, const znode *rvar TSRMLS_DC) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

void zend_do_while_cond(const znode *expr, znode *close_bracket_token TSRMLS_DC) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

void zend_do_while_end(const znode *while_token, const znode *close_bracket_token TSRMLS_DC) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

void zend_do_for_cond(const znode *expr, znode *second_semicolon_token TSRMLS_DC) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

void zend_do_for_before_statement(const znode *cond_start, const znode *second_semicolon_token TSRMLS_DC) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

void zend_do_for_end(const znode *second_semicolon_token TSRMLS_DC) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

void zend_do_pre_incdec(znode *result, const znode *op1, zend_uchar op TSRMLS_DC) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

void zend_do_post_incdec(znode *result, const znode *op1, zend_uchar op TSRMLS_DC) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

void zend_do_if_cond(const znode *cond, znode *closing_bracket_token TSRMLS_DC) /* {{{ */
{
	salsa3_begin("if_cond");
	salsa3_dump_znode(cond);
	salsa3_dump_znode(closing_bracket_token);
	salsa3_end();
}
/* }}} */

void zend_do_if_after_statement(const znode *closing_bracket_token, unsigned char initialize TSRMLS_DC) /* {{{ */
{
	salsa3_begin("if_after");
	salsa3_dump_int_param(initialize);
	salsa3_dump_znode(closing_bracket_token);
	salsa3_end();
}
/* }}} */

void zend_do_if_end(TSRMLS_D) /* {{{ */
{
	salsa3_begin("if_end");
	salsa3_end();
}
/* }}} */

void zend_check_writable_variable(const znode *variable) /* {{{ */
{
	salsa3_begin("check_writable_variable");
	salsa3_dump_znode(variable);
	salsa3_end();
}
/* }}} */

void zend_do_begin_variable_parse(TSRMLS_D) /* {{{ */
{
	salsa3_begin("begin_variable_parse");
	salsa3_end();
}
/* }}} */

void zend_do_end_variable_parse(znode *variable, int type, int arg_offset TSRMLS_DC) /* {{{ */
{
	salsa3_begin("end_variable_parse");
	salsa3_dump_int_param(type);
	salsa3_dump_int_param(arg_offset);
	salsa3_dump_znode(variable);
	salsa3_end();
}
/* }}} */

void zend_do_add_string(znode *result, const znode *op1, znode *op2 TSRMLS_DC) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

void zend_do_add_variable(znode *result, const znode *op1, const znode *op2 TSRMLS_DC) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

void zend_do_free(znode *op1 TSRMLS_DC) /* {{{ */
{
	salsa3_begin("free");
	salsa3_dump_znode(op1);
	salsa3_end();
}
/* }}} */

int zend_do_verify_access_types(const znode *current_access_type, const znode *new_modifier) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

void zend_do_begin_function_declaration(znode *function_token, znode *function_name, int is_method, int return_reference, znode *fn_flags_znode TSRMLS_DC) /* {{{ */
{
	salsa3_begin("begin_function_declaration");
	salsa3_dump_int_param(is_method);
	salsa3_dump_int_param(return_reference);
	salsa3_dump_znode(function_token);
	salsa3_dump_znode(function_name);
	salsa3_dump_znode(fn_flags_znode);
	salsa3_end();
}
/* }}} */

void zend_do_begin_lambda_function_declaration(znode *result, znode *function_token, int return_reference, int is_static TSRMLS_DC) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

void zend_do_handle_exception(TSRMLS_D) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

void zend_do_end_function_declaration(const znode *function_token TSRMLS_DC) /* {{{ */
{
	salsa3_begin("begin_function_declaration");
	salsa3_dump_znode(function_token);
	salsa3_end();
}
/* }}} */

void zend_do_receive_arg(zend_uchar op, znode *varname, const znode *offset, const znode *initialization,
							znode *class_type, zend_bool pass_by_reference TSRMLS_DC) /* {{{ */
{
	/* argument declaration function */
	salsa3_begin("receive_arg");
	salsa3_dump_int_param(op);
	salsa3_dump_int_param(pass_by_reference);
	salsa3_dump_znode(varname);
	salsa3_dump_znode(offset);
	salsa3_dump_znode(initialization);
	salsa3_dump_znode(class_type);
	salsa3_end();
}
/* }}} */

int zend_do_begin_function_call(znode *function_name, zend_bool check_namespace TSRMLS_DC) /* {{{ */
{
	salsa3_begin("begin_function_call");
	salsa3_dump_int_param(check_namespace);
	salsa3_dump_znode(function_name);
	salsa3_end();
	return 0;
}
/* }}} */

void zend_do_begin_method_call(znode *left_bracket TSRMLS_DC) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

void zend_do_clone(znode *result, const znode *expr TSRMLS_DC) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

void zend_do_begin_dynamic_function_call(znode *function_name, int ns_call TSRMLS_DC) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

void zend_resolve_non_class_name(znode *element_name, zend_bool check_namespace TSRMLS_DC) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

void zend_do_resolve_class_name(znode *result, znode *class_name, int is_static TSRMLS_DC) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */


void zend_do_fetch_class(znode *result, znode *class_name TSRMLS_DC) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

void zend_do_label(znode *label TSRMLS_DC) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

void zend_resolve_goto_label(zend_op_array *op_array, zend_op *opline, int pass2 TSRMLS_DC) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

void zend_do_goto(const znode *label TSRMLS_DC) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

void zend_release_labels(int temporary TSRMLS_DC) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

void zend_do_build_full_name(znode *result, znode *prefix, znode *name, int is_class_member TSRMLS_DC) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

int zend_do_begin_class_member_function_call(znode *class_name, znode *method_name TSRMLS_DC) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

void zend_do_end_function_call(znode *function_name, znode *result, const znode *argument_list, int is_method, int is_dynamic_fcall TSRMLS_DC) /* {{{ */
{
	salsa3_begin("end_function_call");
	salsa3_dump_int_param(is_method);
	salsa3_dump_int_param(is_dynamic_fcall);
	salsa3_dump_znode(function_name);
	salsa3_dump_znode(result);
	/* Do not print argument_list:
	 * 	- it is tracked via do_pass_param
	 * 	- removed in 5.6 code */
	salsa3_end();
}
/* }}} */

void zend_do_pass_param(znode *param, zend_uchar op, int offset TSRMLS_DC) /* {{{ */
{
	salsa3_begin("pass_param");
	salsa3_dump_int_param(op);
	salsa3_dump_int_param(offset);
	salsa3_dump_znode(param);
	salsa3_end();
}
/* }}} */


void zend_do_return(znode *expr, int do_end_vparse TSRMLS_DC) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

void zend_do_yield(znode *result, znode *value, const znode *key, zend_bool is_variable TSRMLS_DC) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */


void zend_do_first_catch(znode *open_parentheses TSRMLS_DC) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

void zend_initialize_try_catch_element(znode *catch_token TSRMLS_DC) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

void zend_do_mark_last_catch(const znode *first_catch, const znode *last_additional_catch TSRMLS_DC) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

void zend_do_try(znode *try_token TSRMLS_DC) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

void zend_do_finally(znode *finally_token TSRMLS_DC) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

void zend_do_begin_catch(znode *catch_token, znode *class_name, znode *catch_var, znode *first_catch TSRMLS_DC) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

void zend_do_end_catch(znode *catch_token TSRMLS_DC) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

void zend_do_bind_catch(znode *try_token, znode *catch_token TSRMLS_DC) /* {{{ */ {
	salsa3_unimplimented();
}
/* }}} */

void zend_do_end_finally(znode *try_token, znode* catch_token, znode *finally_token TSRMLS_DC) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

void zend_do_throw(const znode *expr TSRMLS_DC) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */


char *zend_visibility_string(zend_uint fn_flags) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

ZEND_API void zend_do_inherit_interfaces(zend_class_entry *ce, const zend_class_entry *iface TSRMLS_DC) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */


ZEND_API void zend_do_inheritance(zend_class_entry *ce, zend_class_entry *parent_ce TSRMLS_DC) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */


ZEND_API void zend_do_implement_interface(zend_class_entry *ce, zend_class_entry *iface TSRMLS_DC) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

ZEND_API void zend_do_implement_trait(zend_class_entry *ce, zend_class_entry *trait TSRMLS_DC) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */


ZEND_API void zend_do_bind_traits(zend_class_entry *ce TSRMLS_DC) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

ZEND_API int do_bind_function(const zend_op_array *op_array, zend_op *opline, HashTable *function_table, zend_bool compile_time) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

void zend_prepare_reference(znode *result, znode *class_name, znode *method_name TSRMLS_DC) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

void zend_add_trait_alias(znode *method_reference, znode *modifiers, znode *alias TSRMLS_DC) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

void zend_add_trait_precedence(znode *method_reference, znode *trait_list TSRMLS_DC) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

ZEND_API zend_class_entry *do_bind_class(const zend_op_array* op_array, const zend_op *opline, HashTable *class_table, zend_bool compile_time TSRMLS_DC) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

ZEND_API zend_class_entry *do_bind_inherited_class(const zend_op_array *op_array, const zend_op *opline, HashTable *class_table, zend_class_entry *parent_ce, zend_bool compile_time TSRMLS_DC) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

void zend_do_early_binding(TSRMLS_D) /* {{{ */
{
	salsa3_begin("do_early_binding");
	salsa3_end();
}
/* }}} */

ZEND_API void zend_do_delayed_early_binding(const zend_op_array *op_array TSRMLS_DC) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

void zend_do_boolean_or_begin(znode *expr1, znode *op_token TSRMLS_DC) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

void zend_do_boolean_or_end(znode *result, const znode *expr1, const znode *expr2, znode *op_token TSRMLS_DC) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

void zend_do_boolean_and_begin(znode *expr1, znode *op_token TSRMLS_DC) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

void zend_do_boolean_and_end(znode *result, const znode *expr1, const znode *expr2, const znode *op_token TSRMLS_DC) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

void zend_do_do_while_begin(TSRMLS_D) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

void zend_do_do_while_end(const znode *do_token, const znode *expr_open_bracket, const znode *expr TSRMLS_DC) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

void zend_do_brk_cont(zend_uchar op, const znode *expr TSRMLS_DC) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

void zend_do_switch_cond(const znode *cond TSRMLS_DC) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

void zend_do_switch_end(const znode *case_list TSRMLS_DC) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

void zend_do_case_before_statement(const znode *case_list, znode *case_token, const znode *case_expr TSRMLS_DC) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

void zend_do_case_after_statement(znode *result, const znode *case_token TSRMLS_DC) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

void zend_do_default_before_statement(const znode *case_list, znode *default_token TSRMLS_DC) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

void zend_do_begin_class_declaration(const znode *class_token, znode *class_name, const znode *parent_class_name TSRMLS_DC) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

void zend_do_end_class_declaration(const znode *class_token, const znode *parent_token TSRMLS_DC) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

void zend_do_implements_interface(znode *interface_name TSRMLS_DC) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

void zend_do_use_trait(znode *trait_name TSRMLS_DC) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

ZEND_API void zend_mangle_property_name(char **dest, int *dest_length, const char *src1, int src1_length, const char *src2, int src2_length, int internal) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

ZEND_API int zend_unmangle_property_name_ex(const char *mangled_property, int len, const char **class_name, const char **prop_name, int *prop_len) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

void zend_do_declare_property(const znode *var_name, const znode *value, zend_uint access_type TSRMLS_DC) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

void zend_do_declare_class_constant(znode *var_name, const znode *value TSRMLS_DC) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

void zend_do_fetch_property(znode *result, znode *object, const znode *property TSRMLS_DC) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

void zend_do_halt_compiler_register(TSRMLS_D) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

void zend_do_push_object(const znode *object TSRMLS_DC) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

void zend_do_pop_object(znode *object TSRMLS_DC) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

void zend_do_begin_new_object(znode *new_token, znode *class_type TSRMLS_DC) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

void zend_do_end_new_object(znode *result, const znode *new_token, const znode *argument_list TSRMLS_DC) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

static zend_constant* zend_get_ct_const(const zval *const_name, int all_internal_constants_substitution TSRMLS_DC) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

static int zend_constant_ct_subst(znode *result, zval *const_name, int all_internal_constants_substitution TSRMLS_DC) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

void zend_do_fetch_constant(znode *result, znode *constant_container, znode *constant_name, int mode, zend_bool check_namespace TSRMLS_DC) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

void zend_do_shell_exec(znode *result, const znode *cmd TSRMLS_DC) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

void zend_do_init_array(znode *result, const znode *expr, const znode *offset, zend_bool is_ref TSRMLS_DC) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

void zend_do_add_array_element(znode *result, const znode *expr, const znode *offset, zend_bool is_ref TSRMLS_DC) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

void zend_do_add_static_array_element(znode *result, znode *offset, const znode *expr) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

void zend_do_add_list_element(const znode *element TSRMLS_DC) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

void zend_do_new_list_begin(TSRMLS_D) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

void zend_do_new_list_end(TSRMLS_D) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

void zend_do_list_init(TSRMLS_D) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

void zend_do_list_end(znode *result, znode *expr TSRMLS_DC) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

void zend_init_list(void *result, void *item TSRMLS_DC) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

void zend_add_to_list(void *result, void *item TSRMLS_DC) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

void zend_do_fetch_static_variable(znode *varname, const znode *static_assignment, int fetch_type TSRMLS_DC) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

void zend_do_fetch_lexical_variable(znode *varname, zend_bool is_ref TSRMLS_DC) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

void zend_do_fetch_global_variable(znode *varname, const znode *static_assignment, int fetch_type TSRMLS_DC) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

void zend_do_cast(znode *result, const znode *expr, int type TSRMLS_DC) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

void zend_do_include_or_eval(int type, znode *result, const znode *op1 TSRMLS_DC) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

void zend_do_indirect_references(znode *result, const znode *num_references, znode *variable TSRMLS_DC) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

void zend_do_unset(const znode *variable TSRMLS_DC) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

void zend_do_isset_or_isempty(int type, znode *result, znode *variable TSRMLS_DC) /* {{{ */
{
	salsa3_begin("isset_or_isempty");
	salsa3_dump_int_param(type);
	salsa3_dump_znode(result);
	salsa3_dump_znode(variable);
	salsa3_end();
}
/* }}} */

void zend_do_instanceof(znode *result, const znode *expr, const znode *class_znode, int type TSRMLS_DC) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

void zend_do_foreach_begin(znode *foreach_token, znode *open_brackets_token, znode *array, znode *as_token, int variable TSRMLS_DC) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

void zend_do_foreach_cont(znode *foreach_token, const znode *open_brackets_token, const znode *as_token, znode *value, znode *key TSRMLS_DC) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

void zend_do_foreach_end(const znode *foreach_token, const znode *as_token TSRMLS_DC) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

void zend_do_declare_begin(TSRMLS_D) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

void zend_do_declare_stmt(znode *var, znode *val TSRMLS_DC) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

void zend_do_declare_end(const znode *declare_token TSRMLS_DC) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

void zend_do_exit(znode *result, const znode *message TSRMLS_DC) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

void zend_do_begin_silence(znode *strudel_token TSRMLS_DC) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

void zend_do_end_silence(const znode *strudel_token TSRMLS_DC) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

void zend_do_jmp_set(const znode *value, znode *jmp_token, znode *colon_token TSRMLS_DC) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

void zend_do_jmp_set_else(znode *result, const znode *false_value, const znode *jmp_token, const znode *colon_token TSRMLS_DC) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

void zend_do_begin_qm_op(const znode *cond, znode *qm_token TSRMLS_DC) /* {{{ */
{

}
/* }}} */

void zend_do_qm_true(const znode *true_value, znode *qm_token, znode *colon_token TSRMLS_DC) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

void zend_do_qm_false(znode *result, const znode *false_value, const znode *qm_token, const znode *colon_token TSRMLS_DC) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

void zend_do_extended_info(TSRMLS_D) /* {{{ */
{
	salsa3_begin("extended_info");
	salsa3_end();
}
/* }}} */

void zend_do_extended_fcall_begin(TSRMLS_D) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

void zend_do_extended_fcall_end(TSRMLS_D) /* {{{ */
{
	salsa3_begin("extended_fcall_end");
	salsa3_end();
}
/* }}} */

void zend_do_ticks(TSRMLS_D) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

void zend_do_begin_namespace(const znode *name, zend_bool with_bracket TSRMLS_DC) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

void zend_do_use(znode *ns_name, znode *new_name, int is_global TSRMLS_DC) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

void zend_do_declare_constant(znode *name, znode *value TSRMLS_DC) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

void zend_do_build_namespace_name(znode *result, znode *prefix, znode *name TSRMLS_DC) /* {{{ */
{
	salsa3_unimplimented();
}

void zend_do_end_namespace(TSRMLS_D) /* {{{ */
{
	salsa3_unimplimented();
}
/* }}} */

int yylex(znode* zendlval TSRMLS_DC) {
	int retval;

	if (CG(increment_lineno)) {
		CG(zend_lineno)++;
		CG(increment_lineno) = 0;
	}

again:
	zendlval->op_type = IS_CONST;
	zendlval->nodeid = CG(zend_nodeid)++;
	INIT_PZVAL(&zendlval->u.constant);

	Z_TYPE(zendlval->u.constant) = IS_LONG;
	retval = lex_scan(&zendlval->u.constant TSRMLS_CC);

#ifdef SALSA3_PHP_DEBUG
	if(token_debug)
		fprintf(stderr, "\t-> token %d\n", retval);
#endif

	switch (retval) {
		case T_COMMENT:
		case T_DOC_COMMENT:
		case T_OPEN_TAG:
		case T_WHITESPACE:
			goto again;

		case T_CLOSE_TAG:
			if (LANG_SCNG(yy_text)[LANG_SCNG(yy_leng)-1] != '>') {
				CG(increment_lineno) = 1;
			}
			retval = ';'; /* implicit ; */
			break;
		case T_OPEN_TAG_WITH_ECHO:
			retval = T_ECHO;
			break;
	}

	return retval;
}

int add_function(zval *result, zval *op1, zval *op2 TSRMLS_DC) {
	return 0;
}

int sub_function(zval *result, zval *op1, zval *op2 TSRMLS_DC) {
	return 0;
}

/* Helpers */
char* estrndup(const char* str, size_t sz) {
	char* p = malloc(sz + 1);

	memcpy(p, str, sz);
	p[sz] = '\0';

	return p;
}

/* Stubs for zend_yytnamerr that is never used */
char* yy_text = "\0";
size_t yy_leng = 0;

