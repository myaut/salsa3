/*
 * parser.h
 *
 *  Created on: Jun 23, 2014
 *      Author: myaut
 */

#ifndef PARSER_H_
#define PARSER_H_

void yy_scanner_init(void);
void yy_scanner_fini(void);

void yyparse(void);

void yy_scan_buffer(char *str, unsigned int len);

void zend_init_compiler_context(void);

#endif /* PARSER_H_ */
