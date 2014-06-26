"""SCons.Tool.yacc

Tool-specific initialization for re2c.

"""

#
# Copyright (c) 2001, 2002, 2003, 2004, 2005, 2006, 2007, 2008, 2009 The SCons Foundation
#
# Permission is hereby granted, free of charge, to any person obtaining
# a copy of this software and associated documentation files (the
# "Software"), to deal in the Software without restriction, including
# without limitation the rights to use, copy, modify, merge, publish,
# distribute, sublicense, and/or sell copies of the Software, and to
# permit persons to whom the Software is furnished to do so, subject to
# the following conditions:
#
# The above copyright notice and this permission notice shall be included
# in all copies or substantial portions of the Software.
#
# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY
# KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
# WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
# NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
# LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
# OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
# WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
#

__revision__ = "src/engine/SCons/Tool/yacc.py 3897 2009/01/13 06:45:54 scons"

import os.path
import string

import SCons.Defaults
import SCons.Tool
import SCons.Util

Re2cAction = SCons.Action.Action("$RE2CCOM", "$RE2CCOMSTR")

def _re2cEmitter(target, source, env, ysuf, hsuf):
    re2cflags = env.subst("$RE2CFLAGS", target=target, source=source)
    flags = SCons.Util.CLVar(re2cflags)
    targetBase, targetExt = os.path.splitext(SCons.Util.to_String(target[0]))

    # With --defines and --graph, the name of the file is totally defined
    # in the options.
    fileGenOptions = ["--defines=", "--graph="]
    for option in flags:
        for fileGenOption in fileGenOptions:
            l = len(fileGenOption)
            if option[:l] == fileGenOption:
                # A file generating option is present, so add the file
                # name to the list of targets.
                fileName = string.strip(option[l:])
                target.append(fileName)

    return (target, source)

def rEmitter(target, source, env):
    return _re2cEmitter(target, source, env, ['.re2c'], '$RE2CHFILESUFFIX')

def generate(env):
    """Add Builders and construction variables for yacc to an Environment."""
    c_file, cxx_file = SCons.Tool.createCFileBuilders(env)

    # C
    c_file.add_action('.re2c', Re2cAction)
    c_file.add_emitter('.re2c', rEmitter)

    env['RE2C']      = env.Detect('re2c')
    env['RE2CFLAGS'] = SCons.Util.CLVar('')
    env['RE2CCOM']   = '$RE2C $RE2CFLAGS -o $TARGET $SOURCES'
    env['RE2CHFILESUFFIX'] = '.h'

def exists(env):
    return env.Detect(['re2c'])
