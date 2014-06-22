#include <stdio.h>
#include <zend.h>
#include <stdlib.h>

extern FILE * yyin;
extern void yyparse(void);

extern void yy_scan_buffer(char *str, unsigned int len TSRMLS_DC);

void zend_error(int error, const char* msg) {
	fprintf(stderr, "ERROR %d: %s\n", error, msg);
	exit(1);
}

int main(int argc, char* argv[]) {
	if(argc == 1) {
		fputs("Do not call php-parser directly, use SALSA3 instead!\n", stderr);
		return 1;
	}

	yyin = fopen(argv[1], "r");
	yyparse();

	return 0;
}
