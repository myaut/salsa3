/*
 * zend_API.h
 *
 *  Created on: Jun 20, 2014
 *      Author: myaut
 */

#ifndef ZEND_API_H_
#define ZEND_API_H_

#include <zend.h>

/* This file is a stub for zend_language_parser */

/* Compiler state */
typedef struct hash_table {
	int unused;
} HashTable;

typedef struct _zend_compiler_globals {
	int access_type;
	int active_op_array;	/* unused */
	int zend_lineno;
	int parse_error;

	int short_tags;
	int asp_tags;

	int increment_lineno;

	int doc_comment_len;

	unsigned long zend_nodeid;
} zend_compiler_globals;

extern zend_compiler_globals cg;
#define CG(member)			(cg).member

/* Some stubs for Zend Compiler */
void zend_error(int error, const char* msg, ...);

static void yyerror(const char* msg) {
	zend_error(E_PARSE, msg);
}

#define get_next_op_number(arg)		-1

#define LANG_SCNG(arg)		arg
#define SCNG(arg)		arg
extern char* yy_text;
extern size_t yy_leng;

#define HANDLE_INTERACTIVE()
#define DO_TICKS()
#define TSRMLS_FETCH()
#define RESET_DOC_COMMENT()

#define STR_EMPTY_ALLOC() estrndup("", sizeof("")-1)
char* estrndup(const char* str, size_t sz);

/* ZVAL API */

#define Z_LVAL(zval)			(zval).value.lval
#define Z_BVAL(zval)			((zend_bool)(zval).value.lval)
#define Z_DVAL(zval)			(zval).value.dval
#define Z_STRVAL(zval)			(zval).value.str.val
#define Z_STRLEN(zval)			(zval).value.str.len
#define Z_STRISESC(zval)			(zval).value.str.is_escaped

#define Z_TYPE(zval)		(zval).type

#define Z_STRVAL_P(zval)			(*zval).value.str.val
#define Z_STRLEN_P(zval)			(*zval).value.str.len
#define Z_STRISESC_P(zval)			(*zval).value.str.is_escaped

#define ZVAL_LONG(z, l) {			\
		zval *__z = (z);			\
		Z_LVAL(*__z) = l;			\
		Z_TYPE(*__z) = IS_LONG;		\
	}

#define ZVAL_STRINGL(z, s, l, duplicate) do {	\
		const char *__s=(s); int __l=l;			\
		zval *__z = (z);						\
		Z_STRLEN(*__z) = __l;					\
		Z_STRVAL(*__z) = (duplicate ? 			\
						  estrndup(__s, __l) : 	\
						  (char*)__s);			\
		Z_STRISESC_P(__z) = 1;					\
		Z_TYPE(*__z) = IS_STRING;				\
	} while (0)

#define ZVAL_EMPTY_STRING(z) do {				\
		zval *__z = (z);						\
		Z_STRLEN(*__z) = 0;						\
		Z_STRVAL(*__z) = STR_EMPTY_ALLOC();		\
		Z_STRISESC_P(__z) = 1;					\
		Z_TYPE(*__z) = IS_STRING;				\
	} while (0)

#define INIT_PZVAL(zv)							\
	do {										\
		(zv)->token = (char*) 0;				\
		Z_TYPE(*(zv)) = IS_UNKNOWN_ZVAL;		\
	} while (0)

typedef union _zvalue_value {
	long lval;					/* long value */
	double dval;				/* double value */
	struct {
		char *val;
		int len;
		int is_escaped;
	} str;

#if 0
	HashTable *ht;				/* hash table value */
	zend_object_value obj;
	zend_ast *ast;
#endif
} zvalue_value;

typedef struct _zval {
	zvalue_value value;
	char* token;			/* Values that won't parsed represented as tokens */
	int type;
} zval;

typedef struct _zend_op {
	int unused;
} zend_op;

typedef struct _zend_op_array {
	int unused;
} zend_op_array;

typedef struct _zend_class_entry {
	int unused;
} zend_class_entry;

typedef struct _zend_function {
	int unused;
} zend_function;

typedef struct _zend_constant {
	int unused;
} zend_constant;

typedef struct _zend_literal {
	int unused;
} zend_literal;


/* znode API */
typedef union _znode_op {
	zend_uint      constant;
	zend_uint      var;
	zend_uint      num;
	zend_ulong     hash;
	zend_uint      opline_num; /*  Needs to be signed */
	zend_op       *jmp_addr;
	zval          *zv;
	zend_literal  *literal;
	void          *ptr;        /* Used for passing pointers from the compile to execution phase, currently used for traits */
} znode_op;


typedef struct _znode {
	int nodeid;
	int op_type;
	union {
		znode_op op;
		zval constant; /* replaced by literal/zv */
		zend_op_array *op_array;
	} u;
	zend_uint EA;      /* extended attributes */
} znode;

int lex_scan(zval *zendlval TSRMLS_DC);
int yylex(znode* zn TSRMLS_DC);

static int array_init(zval *arg) {
	return 0;
}

#endif /* ZEND_API_H_ */
