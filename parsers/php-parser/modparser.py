'''modparser.py - a tool to modify original zend_language_parser.y'''

import sys
import re

target = sys.stdout    
source = file(sys.argv[1])

re_T_FOR = re.compile('\|\s*T_FOR\n')
counter_T_FOR = -1
update_args = False

def update_yacc_arg(line):
    idx = line.find('$')
    last_idx = 0
    new_line = ''
    
    # Replace $1 with $2, etc.
    
    while idx > 0:
        new_line += line[last_idx:idx]
        
        arg_num = int(line[idx + 1]) + 1
        new_line += '$' + str(arg_num)  
        
        last_idx = idx + 2
        idx = line.find('$', last_idx)
    
    return new_line + line[last_idx:]

for line in source:
    if re_T_FOR.search(line):
        counter_T_FOR = 1
    if counter_T_FOR == 0:
        line = line[:-1]
        line += ' { zend_do_for_begin(TSRMLS_C); } \n'
        update_args = True
    
    if update_args:
        if '|' in line:
            update_args = False
        else:
            line = update_yacc_arg(line)
    
    counter_T_FOR -= 1
    target.write(line) 