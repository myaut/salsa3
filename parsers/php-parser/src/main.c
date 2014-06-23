#include <stdio.h>
#include <zend.h>
#include <stdlib.h>
#include <stdarg.h>

#include <parser.h>

#ifdef SALSA3_PHP_DEBUG
	extern int yydebug;
	extern int scanner_debug;
	extern int token_debug;
#endif

void zend_error(int error, const char* msg, ...) {
	va_list va;

	fprintf(stderr, "ERROR %d: ", error);

	va_start(va, msg);
	vfprintf(stderr, msg, va);
	va_end(va);

	fputc('\n', stderr);

	exit(1);
}

static int read_php_file(const char* path, char** pdata, size_t** psize) {
	FILE* yyin;
	size_t yysize;
	char* yydata;

	yyin = fopen(path, "r");

	if(yyin == NULL) {
		fprintf(stderr, "Failed to open file '%s'!\n", path);
		return -1;
	}

	fseek(yyin, 0, SEEK_END);
	yysize = ftell(yyin);
	fseek(yyin, 0, SEEK_SET);

	yydata = malloc(yysize + ZEND_MMAP_AHEAD + 1);

	if(yydata == NULL) {
		fprintf(stderr, "Out of memory!\n");
		fclose(yyin);
		return -1;
	}

	fread(yydata, 1, yysize, yyin);
	yydata[yysize] = '\0';

	fclose(yyin);

	*psize = yysize;
	*pdata = yydata;

	return 0;
}

int main(int argc, char* argv[]) {
	char* yydata = NULL;
	size_t yysize = 0;

#ifdef SALSA3_PHP_DEBUG
	if(getenv("SALSA3_PHP_DEBUG") != NULL) {
		yydebug = 1;
		token_debug = 1;
		scanner_debug = 1;
	}
#endif

	if(argc == 1) {
		fputs("Do not call php-parser directly, use SALSA3 instead!\n", stderr);
		return 1;
	}

	if(read_php_file(argv[1], &yydata, &yysize) != 0) {
		return 1;
	}

	yy_scanner_init();
	yy_scan_buffer(yydata, yysize);

	yyparse();

	free(yydata);
	yy_scanner_fini();

	return 0;
}
