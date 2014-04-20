/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This file incorporates work covered by the following copyright and
 * permission notice:
 *
 *   ---
 *
 *   handlebars.js :
 *
 *   Copyright (C) 2011 by Yehuda Katz
 *
 *   Permission is hereby granted, free of charge, to any person obtaining a copy
 *   of this software and associated documentation files (the "Software"), to deal
 *   in the Software without restriction, including without limitation the rights
 *   to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *   copies of the Software, and to permit persons to whom the Software is
 *   furnished to do so, subject to the following conditions:
 *
 *   The above copyright notice and this permission notice shall be included in
 *   all copies or substantial portions of the Software.
 *
 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *   IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *   FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *   AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *   LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *   OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *   THE SOFTWARE.
 *
 *   ---
 *
 *   messageformat.js :
 *
 *   Copyright (C) 2011 by Alex Sexton - @SlexAxton
 *
 *   DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE
 *   Version 2, December 2004
 *
 *   Copyright (C) 2004 Sam Hocevar <sam@hocevar.net>
 *
 *   Everyone is permitted to copy and distribute verbatim or modified
 *   copies of this license document, and changing it is allowed as long
 *   as the name is changed.
 *
 *   DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE
 *   TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION
 *
 *   0. You just DO WHAT THE FUCK YOU WANT TO.
 *
 *    ---
 */


 // handlebars.min.js

var Handlebars=function(){var a=function(){"use strict";function a(a){this.string=a}var b;return a.prototype.toString=function(){return""+this.string},b=a}(),b=function(a){"use strict";function b(a){return h[a]||"&amp;"}function c(a,b){for(var c in b)Object.prototype.hasOwnProperty.call(b,c)&&(a[c]=b[c])}function d(a){return a instanceof g?a.toString():a||0===a?(a=""+a,j.test(a)?a.replace(i,b):a):""}function e(a){return a||0===a?m(a)&&0===a.length?!0:!1:!0}var f={},g=a,h={"&":"&amp;","<":"&lt;",">":"&gt;",'"':"&quot;","'":"&#x27;","`":"&#x60;"},i=/[&<>"'`]/g,j=/[&<>"'`]/;f.extend=c;var k=Object.prototype.toString;f.toString=k;var l=function(a){return"function"==typeof a};l(/x/)&&(l=function(a){return"function"==typeof a&&"[object Function]"===k.call(a)});var l;f.isFunction=l;var m=Array.isArray||function(a){return a&&"object"==typeof a?"[object Array]"===k.call(a):!1};return f.isArray=m,f.escapeExpression=d,f.isEmpty=e,f}(a),c=function(){"use strict";function a(a,b){var d;b&&b.firstLine&&(d=b.firstLine,a+=" - "+d+":"+b.firstColumn);for(var e=Error.prototype.constructor.call(this,a),f=0;f<c.length;f++)this[c[f]]=e[c[f]];d&&(this.lineNumber=d,this.column=b.firstColumn)}var b,c=["description","fileName","lineNumber","message","name","number","stack"];return a.prototype=new Error,b=a}(),d=function(a,b){"use strict";function c(a,b){this.helpers=a||{},this.partials=b||{},d(this)}function d(a){a.registerHelper("helperMissing",function(a){if(2===arguments.length)return void 0;throw new h("Missing helper: '"+a+"'")}),a.registerHelper("blockHelperMissing",function(b,c){var d=c.inverse||function(){},e=c.fn;return m(b)&&(b=b.call(this)),b===!0?e(this):b===!1||null==b?d(this):l(b)?b.length>0?a.helpers.each(b,c):d(this):e(b)}),a.registerHelper("each",function(a,b){var c,d=b.fn,e=b.inverse,f=0,g="";if(m(a)&&(a=a.call(this)),b.data&&(c=q(b.data)),a&&"object"==typeof a)if(l(a))for(var h=a.length;h>f;f++)c&&(c.index=f,c.first=0===f,c.last=f===a.length-1),g+=d(a[f],{data:c});else for(var i in a)a.hasOwnProperty(i)&&(c&&(c.key=i,c.index=f,c.first=0===f),g+=d(a[i],{data:c}),f++);return 0===f&&(g=e(this)),g}),a.registerHelper("if",function(a,b){return m(a)&&(a=a.call(this)),!b.hash.includeZero&&!a||g.isEmpty(a)?b.inverse(this):b.fn(this)}),a.registerHelper("unless",function(b,c){return a.helpers["if"].call(this,b,{fn:c.inverse,inverse:c.fn,hash:c.hash})}),a.registerHelper("with",function(a,b){return m(a)&&(a=a.call(this)),g.isEmpty(a)?void 0:b.fn(a)}),a.registerHelper("log",function(b,c){var d=c.data&&null!=c.data.level?parseInt(c.data.level,10):1;a.log(d,b)})}function e(a,b){p.log(a,b)}var f={},g=a,h=b,i="1.3.0";f.VERSION=i;var j=4;f.COMPILER_REVISION=j;var k={1:"<= 1.0.rc.2",2:"== 1.0.0-rc.3",3:"== 1.0.0-rc.4",4:">= 1.0.0"};f.REVISION_CHANGES=k;var l=g.isArray,m=g.isFunction,n=g.toString,o="[object Object]";f.HandlebarsEnvironment=c,c.prototype={constructor:c,logger:p,log:e,registerHelper:function(a,b,c){if(n.call(a)===o){if(c||b)throw new h("Arg not supported with multiple helpers");g.extend(this.helpers,a)}else c&&(b.not=c),this.helpers[a]=b},registerPartial:function(a,b){n.call(a)===o?g.extend(this.partials,a):this.partials[a]=b}};var p={methodMap:{0:"debug",1:"info",2:"warn",3:"error"},DEBUG:0,INFO:1,WARN:2,ERROR:3,level:3,log:function(a,b){if(p.level<=a){var c=p.methodMap[a];"undefined"!=typeof console&&console[c]&&console[c].call(console,b)}}};f.logger=p,f.log=e;var q=function(a){var b={};return g.extend(b,a),b};return f.createFrame=q,f}(b,c),e=function(a,b,c){"use strict";function d(a){var b=a&&a[0]||1,c=m;if(b!==c){if(c>b){var d=n[c],e=n[b];throw new l("Template was precompiled with an older version of Handlebars than the current runtime. Please update your precompiler to a newer version ("+d+") or downgrade your runtime to an older version ("+e+").")}throw new l("Template was precompiled with a newer version of Handlebars than the current runtime. Please update your runtime to a newer version ("+a[1]+").")}}function e(a,b){if(!b)throw new l("No environment passed to template");var c=function(a,c,d,e,f,g){var h=b.VM.invokePartial.apply(this,arguments);if(null!=h)return h;if(b.compile){var i={helpers:e,partials:f,data:g};return f[c]=b.compile(a,{data:void 0!==g},b),f[c](d,i)}throw new l("The partial "+c+" could not be compiled when running in runtime-only mode")},d={escapeExpression:k.escapeExpression,invokePartial:c,programs:[],program:function(a,b,c){var d=this.programs[a];return c?d=g(a,b,c):d||(d=this.programs[a]=g(a,b)),d},merge:function(a,b){var c=a||b;return a&&b&&a!==b&&(c={},k.extend(c,b),k.extend(c,a)),c},programWithDepth:b.VM.programWithDepth,noop:b.VM.noop,compilerInfo:null};return function(c,e){e=e||{};var f,g,h=e.partial?e:b;e.partial||(f=e.helpers,g=e.partials);var i=a.call(d,h,c,f,g,e.data);return e.partial||b.VM.checkRevision(d.compilerInfo),i}}function f(a,b,c){var d=Array.prototype.slice.call(arguments,3),e=function(a,e){return e=e||{},b.apply(this,[a,e.data||c].concat(d))};return e.program=a,e.depth=d.length,e}function g(a,b,c){var d=function(a,d){return d=d||{},b(a,d.data||c)};return d.program=a,d.depth=0,d}function h(a,b,c,d,e,f){var g={partial:!0,helpers:d,partials:e,data:f};if(void 0===a)throw new l("The partial "+b+" could not be found");return a instanceof Function?a(c,g):void 0}function i(){return""}var j={},k=a,l=b,m=c.COMPILER_REVISION,n=c.REVISION_CHANGES;return j.checkRevision=d,j.template=e,j.programWithDepth=f,j.program=g,j.invokePartial=h,j.noop=i,j}(b,c,d),f=function(a,b,c,d,e){"use strict";var f,g=a,h=b,i=c,j=d,k=e,l=function(){var a=new g.HandlebarsEnvironment;return j.extend(a,g),a.SafeString=h,a.Exception=i,a.Utils=j,a.VM=k,a.template=function(b){return k.template(b,a)},a},m=l();return m.create=l,f=m}(d,a,c,b,e),g=function(a){"use strict";function b(a){a=a||{},this.firstLine=a.first_line,this.firstColumn=a.first_column,this.lastColumn=a.last_column,this.lastLine=a.last_line}var c,d=a,e={ProgramNode:function(a,c,d,f){var g,h;3===arguments.length?(f=d,d=null):2===arguments.length&&(f=c,c=null),b.call(this,f),this.type="program",this.statements=a,this.strip={},d?(h=d[0],h?(g={first_line:h.firstLine,last_line:h.lastLine,last_column:h.lastColumn,first_column:h.firstColumn},this.inverse=new e.ProgramNode(d,c,g)):this.inverse=new e.ProgramNode(d,c),this.strip.right=c.left):c&&(this.strip.left=c.right)},MustacheNode:function(a,c,d,f,g){if(b.call(this,g),this.type="mustache",this.strip=f,null!=d&&d.charAt){var h=d.charAt(3)||d.charAt(2);this.escaped="{"!==h&&"&"!==h}else this.escaped=!!d;this.sexpr=a instanceof e.SexprNode?a:new e.SexprNode(a,c),this.sexpr.isRoot=!0,this.id=this.sexpr.id,this.params=this.sexpr.params,this.hash=this.sexpr.hash,this.eligibleHelper=this.sexpr.eligibleHelper,this.isHelper=this.sexpr.isHelper},SexprNode:function(a,c,d){b.call(this,d),this.type="sexpr",this.hash=c;var e=this.id=a[0],f=this.params=a.slice(1),g=this.eligibleHelper=e.isSimple;this.isHelper=g&&(f.length||c)},PartialNode:function(a,c,d,e){b.call(this,e),this.type="partial",this.partialName=a,this.context=c,this.strip=d},BlockNode:function(a,c,e,f,g){if(b.call(this,g),a.sexpr.id.original!==f.path.original)throw new d(a.sexpr.id.original+" doesn't match "+f.path.original,this);this.type="block",this.mustache=a,this.program=c,this.inverse=e,this.strip={left:a.strip.left,right:f.strip.right},(c||e).strip.left=a.strip.right,(e||c).strip.right=f.strip.left,e&&!c&&(this.isInverse=!0)},ContentNode:function(a,c){b.call(this,c),this.type="content",this.string=a},HashNode:function(a,c){b.call(this,c),this.type="hash",this.pairs=a},IdNode:function(a,c){b.call(this,c),this.type="ID";for(var e="",f=[],g=0,h=0,i=a.length;i>h;h++){var j=a[h].part;if(e+=(a[h].separator||"")+j,".."===j||"."===j||"this"===j){if(f.length>0)throw new d("Invalid path: "+e,this);".."===j?g++:this.isScoped=!0}else f.push(j)}this.original=e,this.parts=f,this.string=f.join("."),this.depth=g,this.isSimple=1===a.length&&!this.isScoped&&0===g,this.stringModeValue=this.string},PartialNameNode:function(a,c){b.call(this,c),this.type="PARTIAL_NAME",this.name=a.original},DataNode:function(a,c){b.call(this,c),this.type="DATA",this.id=a},StringNode:function(a,c){b.call(this,c),this.type="STRING",this.original=this.string=this.stringModeValue=a},IntegerNode:function(a,c){b.call(this,c),this.type="INTEGER",this.original=this.integer=a,this.stringModeValue=Number(a)},BooleanNode:function(a,c){b.call(this,c),this.type="BOOLEAN",this.bool=a,this.stringModeValue="true"===a},CommentNode:function(a,c){b.call(this,c),this.type="comment",this.comment=a}};return c=e}(c),h=function(){"use strict";var a,b=function(){function a(a,b){return{left:"~"===a.charAt(2),right:"~"===b.charAt(0)||"~"===b.charAt(1)}}function b(){this.yy={}}var c={trace:function(){},yy:{},symbols_:{error:2,root:3,statements:4,EOF:5,program:6,simpleInverse:7,statement:8,openInverse:9,closeBlock:10,openBlock:11,mustache:12,partial:13,CONTENT:14,COMMENT:15,OPEN_BLOCK:16,sexpr:17,CLOSE:18,OPEN_INVERSE:19,OPEN_ENDBLOCK:20,path:21,OPEN:22,OPEN_UNESCAPED:23,CLOSE_UNESCAPED:24,OPEN_PARTIAL:25,partialName:26,partial_option0:27,sexpr_repetition0:28,sexpr_option0:29,dataName:30,param:31,STRING:32,INTEGER:33,BOOLEAN:34,OPEN_SEXPR:35,CLOSE_SEXPR:36,hash:37,hash_repetition_plus0:38,hashSegment:39,ID:40,EQUALS:41,DATA:42,pathSegments:43,SEP:44,$accept:0,$end:1},terminals_:{2:"error",5:"EOF",14:"CONTENT",15:"COMMENT",16:"OPEN_BLOCK",18:"CLOSE",19:"OPEN_INVERSE",20:"OPEN_ENDBLOCK",22:"OPEN",23:"OPEN_UNESCAPED",24:"CLOSE_UNESCAPED",25:"OPEN_PARTIAL",32:"STRING",33:"INTEGER",34:"BOOLEAN",35:"OPEN_SEXPR",36:"CLOSE_SEXPR",40:"ID",41:"EQUALS",42:"DATA",44:"SEP"},productions_:[0,[3,2],[3,1],[6,2],[6,3],[6,2],[6,1],[6,1],[6,0],[4,1],[4,2],[8,3],[8,3],[8,1],[8,1],[8,1],[8,1],[11,3],[9,3],[10,3],[12,3],[12,3],[13,4],[7,2],[17,3],[17,1],[31,1],[31,1],[31,1],[31,1],[31,1],[31,3],[37,1],[39,3],[26,1],[26,1],[26,1],[30,2],[21,1],[43,3],[43,1],[27,0],[27,1],[28,0],[28,2],[29,0],[29,1],[38,1],[38,2]],performAction:function(b,c,d,e,f,g){var h=g.length-1;switch(f){case 1:return new e.ProgramNode(g[h-1],this._$);case 2:return new e.ProgramNode([],this._$);case 3:this.$=new e.ProgramNode([],g[h-1],g[h],this._$);break;case 4:this.$=new e.ProgramNode(g[h-2],g[h-1],g[h],this._$);break;case 5:this.$=new e.ProgramNode(g[h-1],g[h],[],this._$);break;case 6:this.$=new e.ProgramNode(g[h],this._$);break;case 7:this.$=new e.ProgramNode([],this._$);break;case 8:this.$=new e.ProgramNode([],this._$);break;case 9:this.$=[g[h]];break;case 10:g[h-1].push(g[h]),this.$=g[h-1];break;case 11:this.$=new e.BlockNode(g[h-2],g[h-1].inverse,g[h-1],g[h],this._$);break;case 12:this.$=new e.BlockNode(g[h-2],g[h-1],g[h-1].inverse,g[h],this._$);break;case 13:this.$=g[h];break;case 14:this.$=g[h];break;case 15:this.$=new e.ContentNode(g[h],this._$);break;case 16:this.$=new e.CommentNode(g[h],this._$);break;case 17:this.$=new e.MustacheNode(g[h-1],null,g[h-2],a(g[h-2],g[h]),this._$);break;case 18:this.$=new e.MustacheNode(g[h-1],null,g[h-2],a(g[h-2],g[h]),this._$);break;case 19:this.$={path:g[h-1],strip:a(g[h-2],g[h])};break;case 20:this.$=new e.MustacheNode(g[h-1],null,g[h-2],a(g[h-2],g[h]),this._$);break;case 21:this.$=new e.MustacheNode(g[h-1],null,g[h-2],a(g[h-2],g[h]),this._$);break;case 22:this.$=new e.PartialNode(g[h-2],g[h-1],a(g[h-3],g[h]),this._$);break;case 23:this.$=a(g[h-1],g[h]);break;case 24:this.$=new e.SexprNode([g[h-2]].concat(g[h-1]),g[h],this._$);break;case 25:this.$=new e.SexprNode([g[h]],null,this._$);break;case 26:this.$=g[h];break;case 27:this.$=new e.StringNode(g[h],this._$);break;case 28:this.$=new e.IntegerNode(g[h],this._$);break;case 29:this.$=new e.BooleanNode(g[h],this._$);break;case 30:this.$=g[h];break;case 31:g[h-1].isHelper=!0,this.$=g[h-1];break;case 32:this.$=new e.HashNode(g[h],this._$);break;case 33:this.$=[g[h-2],g[h]];break;case 34:this.$=new e.PartialNameNode(g[h],this._$);break;case 35:this.$=new e.PartialNameNode(new e.StringNode(g[h],this._$),this._$);break;case 36:this.$=new e.PartialNameNode(new e.IntegerNode(g[h],this._$));break;case 37:this.$=new e.DataNode(g[h],this._$);break;case 38:this.$=new e.IdNode(g[h],this._$);break;case 39:g[h-2].push({part:g[h],separator:g[h-1]}),this.$=g[h-2];break;case 40:this.$=[{part:g[h]}];break;case 43:this.$=[];break;case 44:g[h-1].push(g[h]);break;case 47:this.$=[g[h]];break;case 48:g[h-1].push(g[h])}},table:[{3:1,4:2,5:[1,3],8:4,9:5,11:6,12:7,13:8,14:[1,9],15:[1,10],16:[1,12],19:[1,11],22:[1,13],23:[1,14],25:[1,15]},{1:[3]},{5:[1,16],8:17,9:5,11:6,12:7,13:8,14:[1,9],15:[1,10],16:[1,12],19:[1,11],22:[1,13],23:[1,14],25:[1,15]},{1:[2,2]},{5:[2,9],14:[2,9],15:[2,9],16:[2,9],19:[2,9],20:[2,9],22:[2,9],23:[2,9],25:[2,9]},{4:20,6:18,7:19,8:4,9:5,11:6,12:7,13:8,14:[1,9],15:[1,10],16:[1,12],19:[1,21],20:[2,8],22:[1,13],23:[1,14],25:[1,15]},{4:20,6:22,7:19,8:4,9:5,11:6,12:7,13:8,14:[1,9],15:[1,10],16:[1,12],19:[1,21],20:[2,8],22:[1,13],23:[1,14],25:[1,15]},{5:[2,13],14:[2,13],15:[2,13],16:[2,13],19:[2,13],20:[2,13],22:[2,13],23:[2,13],25:[2,13]},{5:[2,14],14:[2,14],15:[2,14],16:[2,14],19:[2,14],20:[2,14],22:[2,14],23:[2,14],25:[2,14]},{5:[2,15],14:[2,15],15:[2,15],16:[2,15],19:[2,15],20:[2,15],22:[2,15],23:[2,15],25:[2,15]},{5:[2,16],14:[2,16],15:[2,16],16:[2,16],19:[2,16],20:[2,16],22:[2,16],23:[2,16],25:[2,16]},{17:23,21:24,30:25,40:[1,28],42:[1,27],43:26},{17:29,21:24,30:25,40:[1,28],42:[1,27],43:26},{17:30,21:24,30:25,40:[1,28],42:[1,27],43:26},{17:31,21:24,30:25,40:[1,28],42:[1,27],43:26},{21:33,26:32,32:[1,34],33:[1,35],40:[1,28],43:26},{1:[2,1]},{5:[2,10],14:[2,10],15:[2,10],16:[2,10],19:[2,10],20:[2,10],22:[2,10],23:[2,10],25:[2,10]},{10:36,20:[1,37]},{4:38,8:4,9:5,11:6,12:7,13:8,14:[1,9],15:[1,10],16:[1,12],19:[1,11],20:[2,7],22:[1,13],23:[1,14],25:[1,15]},{7:39,8:17,9:5,11:6,12:7,13:8,14:[1,9],15:[1,10],16:[1,12],19:[1,21],20:[2,6],22:[1,13],23:[1,14],25:[1,15]},{17:23,18:[1,40],21:24,30:25,40:[1,28],42:[1,27],43:26},{10:41,20:[1,37]},{18:[1,42]},{18:[2,43],24:[2,43],28:43,32:[2,43],33:[2,43],34:[2,43],35:[2,43],36:[2,43],40:[2,43],42:[2,43]},{18:[2,25],24:[2,25],36:[2,25]},{18:[2,38],24:[2,38],32:[2,38],33:[2,38],34:[2,38],35:[2,38],36:[2,38],40:[2,38],42:[2,38],44:[1,44]},{21:45,40:[1,28],43:26},{18:[2,40],24:[2,40],32:[2,40],33:[2,40],34:[2,40],35:[2,40],36:[2,40],40:[2,40],42:[2,40],44:[2,40]},{18:[1,46]},{18:[1,47]},{24:[1,48]},{18:[2,41],21:50,27:49,40:[1,28],43:26},{18:[2,34],40:[2,34]},{18:[2,35],40:[2,35]},{18:[2,36],40:[2,36]},{5:[2,11],14:[2,11],15:[2,11],16:[2,11],19:[2,11],20:[2,11],22:[2,11],23:[2,11],25:[2,11]},{21:51,40:[1,28],43:26},{8:17,9:5,11:6,12:7,13:8,14:[1,9],15:[1,10],16:[1,12],19:[1,11],20:[2,3],22:[1,13],23:[1,14],25:[1,15]},{4:52,8:4,9:5,11:6,12:7,13:8,14:[1,9],15:[1,10],16:[1,12],19:[1,11],20:[2,5],22:[1,13],23:[1,14],25:[1,15]},{14:[2,23],15:[2,23],16:[2,23],19:[2,23],20:[2,23],22:[2,23],23:[2,23],25:[2,23]},{5:[2,12],14:[2,12],15:[2,12],16:[2,12],19:[2,12],20:[2,12],22:[2,12],23:[2,12],25:[2,12]},{14:[2,18],15:[2,18],16:[2,18],19:[2,18],20:[2,18],22:[2,18],23:[2,18],25:[2,18]},{18:[2,45],21:56,24:[2,45],29:53,30:60,31:54,32:[1,57],33:[1,58],34:[1,59],35:[1,61],36:[2,45],37:55,38:62,39:63,40:[1,64],42:[1,27],43:26},{40:[1,65]},{18:[2,37],24:[2,37],32:[2,37],33:[2,37],34:[2,37],35:[2,37],36:[2,37],40:[2,37],42:[2,37]},{14:[2,17],15:[2,17],16:[2,17],19:[2,17],20:[2,17],22:[2,17],23:[2,17],25:[2,17]},{5:[2,20],14:[2,20],15:[2,20],16:[2,20],19:[2,20],20:[2,20],22:[2,20],23:[2,20],25:[2,20]},{5:[2,21],14:[2,21],15:[2,21],16:[2,21],19:[2,21],20:[2,21],22:[2,21],23:[2,21],25:[2,21]},{18:[1,66]},{18:[2,42]},{18:[1,67]},{8:17,9:5,11:6,12:7,13:8,14:[1,9],15:[1,10],16:[1,12],19:[1,11],20:[2,4],22:[1,13],23:[1,14],25:[1,15]},{18:[2,24],24:[2,24],36:[2,24]},{18:[2,44],24:[2,44],32:[2,44],33:[2,44],34:[2,44],35:[2,44],36:[2,44],40:[2,44],42:[2,44]},{18:[2,46],24:[2,46],36:[2,46]},{18:[2,26],24:[2,26],32:[2,26],33:[2,26],34:[2,26],35:[2,26],36:[2,26],40:[2,26],42:[2,26]},{18:[2,27],24:[2,27],32:[2,27],33:[2,27],34:[2,27],35:[2,27],36:[2,27],40:[2,27],42:[2,27]},{18:[2,28],24:[2,28],32:[2,28],33:[2,28],34:[2,28],35:[2,28],36:[2,28],40:[2,28],42:[2,28]},{18:[2,29],24:[2,29],32:[2,29],33:[2,29],34:[2,29],35:[2,29],36:[2,29],40:[2,29],42:[2,29]},{18:[2,30],24:[2,30],32:[2,30],33:[2,30],34:[2,30],35:[2,30],36:[2,30],40:[2,30],42:[2,30]},{17:68,21:24,30:25,40:[1,28],42:[1,27],43:26},{18:[2,32],24:[2,32],36:[2,32],39:69,40:[1,70]},{18:[2,47],24:[2,47],36:[2,47],40:[2,47]},{18:[2,40],24:[2,40],32:[2,40],33:[2,40],34:[2,40],35:[2,40],36:[2,40],40:[2,40],41:[1,71],42:[2,40],44:[2,40]},{18:[2,39],24:[2,39],32:[2,39],33:[2,39],34:[2,39],35:[2,39],36:[2,39],40:[2,39],42:[2,39],44:[2,39]},{5:[2,22],14:[2,22],15:[2,22],16:[2,22],19:[2,22],20:[2,22],22:[2,22],23:[2,22],25:[2,22]},{5:[2,19],14:[2,19],15:[2,19],16:[2,19],19:[2,19],20:[2,19],22:[2,19],23:[2,19],25:[2,19]},{36:[1,72]},{18:[2,48],24:[2,48],36:[2,48],40:[2,48]},{41:[1,71]},{21:56,30:60,31:73,32:[1,57],33:[1,58],34:[1,59],35:[1,61],40:[1,28],42:[1,27],43:26},{18:[2,31],24:[2,31],32:[2,31],33:[2,31],34:[2,31],35:[2,31],36:[2,31],40:[2,31],42:[2,31]},{18:[2,33],24:[2,33],36:[2,33],40:[2,33]}],defaultActions:{3:[2,2],16:[2,1],50:[2,42]},parseError:function(a){throw new Error(a)},parse:function(a){function b(){var a;return a=c.lexer.lex()||1,"number"!=typeof a&&(a=c.symbols_[a]||a),a}var c=this,d=[0],e=[null],f=[],g=this.table,h="",i=0,j=0,k=0;this.lexer.setInput(a),this.lexer.yy=this.yy,this.yy.lexer=this.lexer,this.yy.parser=this,"undefined"==typeof this.lexer.yylloc&&(this.lexer.yylloc={});var l=this.lexer.yylloc;f.push(l);var m=this.lexer.options&&this.lexer.options.ranges;"function"==typeof this.yy.parseError&&(this.parseError=this.yy.parseError);for(var n,o,p,q,r,s,t,u,v,w={};;){if(p=d[d.length-1],this.defaultActions[p]?q=this.defaultActions[p]:((null===n||"undefined"==typeof n)&&(n=b()),q=g[p]&&g[p][n]),"undefined"==typeof q||!q.length||!q[0]){var x="";if(!k){v=[];for(s in g[p])this.terminals_[s]&&s>2&&v.push("'"+this.terminals_[s]+"'");x=this.lexer.showPosition?"Parse error on line "+(i+1)+":\n"+this.lexer.showPosition()+"\nExpecting "+v.join(", ")+", got '"+(this.terminals_[n]||n)+"'":"Parse error on line "+(i+1)+": Unexpected "+(1==n?"end of input":"'"+(this.terminals_[n]||n)+"'"),this.parseError(x,{text:this.lexer.match,token:this.terminals_[n]||n,line:this.lexer.yylineno,loc:l,expected:v})}}if(q[0]instanceof Array&&q.length>1)throw new Error("Parse Error: multiple actions possible at state: "+p+", token: "+n);switch(q[0]){case 1:d.push(n),e.push(this.lexer.yytext),f.push(this.lexer.yylloc),d.push(q[1]),n=null,o?(n=o,o=null):(j=this.lexer.yyleng,h=this.lexer.yytext,i=this.lexer.yylineno,l=this.lexer.yylloc,k>0&&k--);break;case 2:if(t=this.productions_[q[1]][1],w.$=e[e.length-t],w._$={first_line:f[f.length-(t||1)].first_line,last_line:f[f.length-1].last_line,first_column:f[f.length-(t||1)].first_column,last_column:f[f.length-1].last_column},m&&(w._$.range=[f[f.length-(t||1)].range[0],f[f.length-1].range[1]]),r=this.performAction.call(w,h,j,i,this.yy,q[1],e,f),"undefined"!=typeof r)return r;t&&(d=d.slice(0,-1*t*2),e=e.slice(0,-1*t),f=f.slice(0,-1*t)),d.push(this.productions_[q[1]][0]),e.push(w.$),f.push(w._$),u=g[d[d.length-2]][d[d.length-1]],d.push(u);break;case 3:return!0}}return!0}},d=function(){var a={EOF:1,parseError:function(a,b){if(!this.yy.parser)throw new Error(a);this.yy.parser.parseError(a,b)},setInput:function(a){return this._input=a,this._more=this._less=this.done=!1,this.yylineno=this.yyleng=0,this.yytext=this.matched=this.match="",this.conditionStack=["INITIAL"],this.yylloc={first_line:1,first_column:0,last_line:1,last_column:0},this.options.ranges&&(this.yylloc.range=[0,0]),this.offset=0,this},input:function(){var a=this._input[0];this.yytext+=a,this.yyleng++,this.offset++,this.match+=a,this.matched+=a;var b=a.match(/(?:\r\n?|\n).*/g);return b?(this.yylineno++,this.yylloc.last_line++):this.yylloc.last_column++,this.options.ranges&&this.yylloc.range[1]++,this._input=this._input.slice(1),a},unput:function(a){var b=a.length,c=a.split(/(?:\r\n?|\n)/g);this._input=a+this._input,this.yytext=this.yytext.substr(0,this.yytext.length-b-1),this.offset-=b;var d=this.match.split(/(?:\r\n?|\n)/g);this.match=this.match.substr(0,this.match.length-1),this.matched=this.matched.substr(0,this.matched.length-1),c.length-1&&(this.yylineno-=c.length-1);var e=this.yylloc.range;return this.yylloc={first_line:this.yylloc.first_line,last_line:this.yylineno+1,first_column:this.yylloc.first_column,last_column:c?(c.length===d.length?this.yylloc.first_column:0)+d[d.length-c.length].length-c[0].length:this.yylloc.first_column-b},this.options.ranges&&(this.yylloc.range=[e[0],e[0]+this.yyleng-b]),this},more:function(){return this._more=!0,this},less:function(a){this.unput(this.match.slice(a))},pastInput:function(){var a=this.matched.substr(0,this.matched.length-this.match.length);return(a.length>20?"...":"")+a.substr(-20).replace(/\n/g,"")},upcomingInput:function(){var a=this.match;return a.length<20&&(a+=this._input.substr(0,20-a.length)),(a.substr(0,20)+(a.length>20?"...":"")).replace(/\n/g,"")},showPosition:function(){var a=this.pastInput(),b=new Array(a.length+1).join("-");return a+this.upcomingInput()+"\n"+b+"^"},next:function(){if(this.done)return this.EOF;this._input||(this.done=!0);var a,b,c,d,e;this._more||(this.yytext="",this.match="");for(var f=this._currentRules(),g=0;g<f.length&&(c=this._input.match(this.rules[f[g]]),!c||b&&!(c[0].length>b[0].length)||(b=c,d=g,this.options.flex));g++);return b?(e=b[0].match(/(?:\r\n?|\n).*/g),e&&(this.yylineno+=e.length),this.yylloc={first_line:this.yylloc.last_line,last_line:this.yylineno+1,first_column:this.yylloc.last_column,last_column:e?e[e.length-1].length-e[e.length-1].match(/\r?\n?/)[0].length:this.yylloc.last_column+b[0].length},this.yytext+=b[0],this.match+=b[0],this.matches=b,this.yyleng=this.yytext.length,this.options.ranges&&(this.yylloc.range=[this.offset,this.offset+=this.yyleng]),this._more=!1,this._input=this._input.slice(b[0].length),this.matched+=b[0],a=this.performAction.call(this,this.yy,this,f[d],this.conditionStack[this.conditionStack.length-1]),this.done&&this._input&&(this.done=!1),a?a:void 0):""===this._input?this.EOF:this.parseError("Lexical error on line "+(this.yylineno+1)+". Unrecognized text.\n"+this.showPosition(),{text:"",token:null,line:this.yylineno})},lex:function(){var a=this.next();return"undefined"!=typeof a?a:this.lex()},begin:function(a){this.conditionStack.push(a)},popState:function(){return this.conditionStack.pop()},_currentRules:function(){return this.conditions[this.conditionStack[this.conditionStack.length-1]].rules},topState:function(){return this.conditionStack[this.conditionStack.length-2]},pushState:function(a){this.begin(a)}};return a.options={},a.performAction=function(a,b,c,d){function e(a,c){return b.yytext=b.yytext.substr(a,b.yyleng-c)}switch(c){case 0:if("\\\\"===b.yytext.slice(-2)?(e(0,1),this.begin("mu")):"\\"===b.yytext.slice(-1)?(e(0,1),this.begin("emu")):this.begin("mu"),b.yytext)return 14;break;case 1:return 14;case 2:return this.popState(),14;case 3:return e(0,4),this.popState(),15;case 4:return 35;case 5:return 36;case 6:return 25;case 7:return 16;case 8:return 20;case 9:return 19;case 10:return 19;case 11:return 23;case 12:return 22;case 13:this.popState(),this.begin("com");break;case 14:return e(3,5),this.popState(),15;case 15:return 22;case 16:return 41;case 17:return 40;case 18:return 40;case 19:return 44;case 20:break;case 21:return this.popState(),24;case 22:return this.popState(),18;case 23:return b.yytext=e(1,2).replace(/\\"/g,'"'),32;case 24:return b.yytext=e(1,2).replace(/\\'/g,"'"),32;case 25:return 42;case 26:return 34;case 27:return 34;case 28:return 33;case 29:return 40;case 30:return b.yytext=e(1,2),40;case 31:return"INVALID";case 32:return 5}},a.rules=[/^(?:[^\x00]*?(?=(\{\{)))/,/^(?:[^\x00]+)/,/^(?:[^\x00]{2,}?(?=(\{\{|\\\{\{|\\\\\{\{|$)))/,/^(?:[\s\S]*?--\}\})/,/^(?:\()/,/^(?:\))/,/^(?:\{\{(~)?>)/,/^(?:\{\{(~)?#)/,/^(?:\{\{(~)?\/)/,/^(?:\{\{(~)?\^)/,/^(?:\{\{(~)?\s*else\b)/,/^(?:\{\{(~)?\{)/,/^(?:\{\{(~)?&)/,/^(?:\{\{!--)/,/^(?:\{\{![\s\S]*?\}\})/,/^(?:\{\{(~)?)/,/^(?:=)/,/^(?:\.\.)/,/^(?:\.(?=([=~}\s\/.)])))/,/^(?:[\/.])/,/^(?:\s+)/,/^(?:\}(~)?\}\})/,/^(?:(~)?\}\})/,/^(?:"(\\["]|[^"])*")/,/^(?:'(\\[']|[^'])*')/,/^(?:@)/,/^(?:true(?=([~}\s)])))/,/^(?:false(?=([~}\s)])))/,/^(?:-?[0-9]+(?=([~}\s)])))/,/^(?:([^\s!"#%-,\.\/;->@\[-\^`\{-~]+(?=([=~}\s\/.)]))))/,/^(?:\[[^\]]*\])/,/^(?:.)/,/^(?:$)/],a.conditions={mu:{rules:[4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32],inclusive:!1},emu:{rules:[2],inclusive:!1},com:{rules:[3],inclusive:!1},INITIAL:{rules:[0,1,32],inclusive:!0}},a}();return c.lexer=d,b.prototype=c,c.Parser=b,new b}();return a=b}(),i=function(a,b){"use strict";function c(a){return a.constructor===f.ProgramNode?a:(e.yy=f,e.parse(a))}var d={},e=a,f=b;return d.parser=e,d.parse=c,d}(h,g),j=function(a){"use strict";function b(){}function c(a,b,c){if(null==a||"string"!=typeof a&&a.constructor!==c.AST.ProgramNode)throw new f("You must pass a string or Handlebars AST to Handlebars.precompile. You passed "+a);b=b||{},"data"in b||(b.data=!0);var d=c.parse(a),e=(new c.Compiler).compile(d,b);return(new c.JavaScriptCompiler).compile(e,b)}function d(a,b,c){function d(){var d=c.parse(a),e=(new c.Compiler).compile(d,b),f=(new c.JavaScriptCompiler).compile(e,b,void 0,!0);return c.template(f)}if(null==a||"string"!=typeof a&&a.constructor!==c.AST.ProgramNode)throw new f("You must pass a string or Handlebars AST to Handlebars.compile. You passed "+a);b=b||{},"data"in b||(b.data=!0);var e;return function(a,b){return e||(e=d()),e.call(this,a,b)}}var e={},f=a;return e.Compiler=b,b.prototype={compiler:b,disassemble:function(){for(var a,b,c,d=this.opcodes,e=[],f=0,g=d.length;g>f;f++)if(a=d[f],"DECLARE"===a.opcode)e.push("DECLARE "+a.name+"="+a.value);else{b=[];for(var h=0;h<a.args.length;h++)c=a.args[h],"string"==typeof c&&(c='"'+c.replace("\n","\\n")+'"'),b.push(c);e.push(a.opcode+" "+b.join(" "))}return e.join("\n")},equals:function(a){var b=this.opcodes.length;if(a.opcodes.length!==b)return!1;for(var c=0;b>c;c++){var d=this.opcodes[c],e=a.opcodes[c];if(d.opcode!==e.opcode||d.args.length!==e.args.length)return!1;for(var f=0;f<d.args.length;f++)if(d.args[f]!==e.args[f])return!1}if(b=this.children.length,a.children.length!==b)return!1;for(c=0;b>c;c++)if(!this.children[c].equals(a.children[c]))return!1;return!0},guid:0,compile:function(a,b){this.opcodes=[],this.children=[],this.depths={list:[]},this.options=b;var c=this.options.knownHelpers;if(this.options.knownHelpers={helperMissing:!0,blockHelperMissing:!0,each:!0,"if":!0,unless:!0,"with":!0,log:!0},c)for(var d in c)this.options.knownHelpers[d]=c[d];return this.accept(a)},accept:function(a){var b,c=a.strip||{};return c.left&&this.opcode("strip"),b=this[a.type](a),c.right&&this.opcode("strip"),b},program:function(a){for(var b=a.statements,c=0,d=b.length;d>c;c++)this.accept(b[c]);return this.isSimple=1===d,this.depths.list=this.depths.list.sort(function(a,b){return a-b}),this},compileProgram:function(a){var b,c=(new this.compiler).compile(a,this.options),d=this.guid++;this.usePartial=this.usePartial||c.usePartial,this.children[d]=c;for(var e=0,f=c.depths.list.length;f>e;e++)b=c.depths.list[e],2>b||this.addDepth(b-1);return d},block:function(a){var b=a.mustache,c=a.program,d=a.inverse;c&&(c=this.compileProgram(c)),d&&(d=this.compileProgram(d));var e=b.sexpr,f=this.classifySexpr(e);"helper"===f?this.helperSexpr(e,c,d):"simple"===f?(this.simpleSexpr(e),this.opcode("pushProgram",c),this.opcode("pushProgram",d),this.opcode("emptyHash"),this.opcode("blockValue")):(this.ambiguousSexpr(e,c,d),this.opcode("pushProgram",c),this.opcode("pushProgram",d),this.opcode("emptyHash"),this.opcode("ambiguousBlockValue")),this.opcode("append")},hash:function(a){var b,c,d=a.pairs;this.opcode("pushHash");for(var e=0,f=d.length;f>e;e++)b=d[e],c=b[1],this.options.stringParams?(c.depth&&this.addDepth(c.depth),this.opcode("getContext",c.depth||0),this.opcode("pushStringParam",c.stringModeValue,c.type),"sexpr"===c.type&&this.sexpr(c)):this.accept(c),this.opcode("assignToHash",b[0]);this.opcode("popHash")},partial:function(a){var b=a.partialName;this.usePartial=!0,a.context?this.ID(a.context):this.opcode("push","depth0"),this.opcode("invokePartial",b.name),this.opcode("append")},content:function(a){this.opcode("appendContent",a.string)},mustache:function(a){this.sexpr(a.sexpr),a.escaped&&!this.options.noEscape?this.opcode("appendEscaped"):this.opcode("append")},ambiguousSexpr:function(a,b,c){var d=a.id,e=d.parts[0],f=null!=b||null!=c;this.opcode("getContext",d.depth),this.opcode("pushProgram",b),this.opcode("pushProgram",c),this.opcode("invokeAmbiguous",e,f)},simpleSexpr:function(a){var b=a.id;"DATA"===b.type?this.DATA(b):b.parts.length?this.ID(b):(this.addDepth(b.depth),this.opcode("getContext",b.depth),this.opcode("pushContext")),this.opcode("resolvePossibleLambda")},helperSexpr:function(a,b,c){var d=this.setupFullMustacheParams(a,b,c),e=a.id.parts[0];if(this.options.knownHelpers[e])this.opcode("invokeKnownHelper",d.length,e);else{if(this.options.knownHelpersOnly)throw new f("You specified knownHelpersOnly, but used the unknown helper "+e,a);this.opcode("invokeHelper",d.length,e,a.isRoot)}},sexpr:function(a){var b=this.classifySexpr(a);"simple"===b?this.simpleSexpr(a):"helper"===b?this.helperSexpr(a):this.ambiguousSexpr(a)},ID:function(a){this.addDepth(a.depth),this.opcode("getContext",a.depth);var b=a.parts[0];b?this.opcode("lookupOnContext",a.parts[0]):this.opcode("pushContext");for(var c=1,d=a.parts.length;d>c;c++)this.opcode("lookup",a.parts[c])},DATA:function(a){if(this.options.data=!0,a.id.isScoped||a.id.depth)throw new f("Scoped data references are not supported: "+a.original,a);this.opcode("lookupData");for(var b=a.id.parts,c=0,d=b.length;d>c;c++)this.opcode("lookup",b[c])},STRING:function(a){this.opcode("pushString",a.string)},INTEGER:function(a){this.opcode("pushLiteral",a.integer)},BOOLEAN:function(a){this.opcode("pushLiteral",a.bool)},comment:function(){},opcode:function(a){this.opcodes.push({opcode:a,args:[].slice.call(arguments,1)})},declare:function(a,b){this.opcodes.push({opcode:"DECLARE",name:a,value:b})},addDepth:function(a){0!==a&&(this.depths[a]||(this.depths[a]=!0,this.depths.list.push(a)))},classifySexpr:function(a){var b=a.isHelper,c=a.eligibleHelper,d=this.options;if(c&&!b){var e=a.id.parts[0];d.knownHelpers[e]?b=!0:d.knownHelpersOnly&&(c=!1)}return b?"helper":c?"ambiguous":"simple"},pushParams:function(a){for(var b,c=a.length;c--;)b=a[c],this.options.stringParams?(b.depth&&this.addDepth(b.depth),this.opcode("getContext",b.depth||0),this.opcode("pushStringParam",b.stringModeValue,b.type),"sexpr"===b.type&&this.sexpr(b)):this[b.type](b)},setupFullMustacheParams:function(a,b,c){var d=a.params;return this.pushParams(d),this.opcode("pushProgram",b),this.opcode("pushProgram",c),a.hash?this.hash(a.hash):this.opcode("emptyHash"),d}},e.precompile=c,e.compile=d,e}(c),k=function(a,b){"use strict";function c(a){this.value=a}function d(){}var e,f=a.COMPILER_REVISION,g=a.REVISION_CHANGES,h=a.log,i=b;d.prototype={nameLookup:function(a,b){var c,e;return 0===a.indexOf("depth")&&(c=!0),e=/^[0-9]+$/.test(b)?a+"["+b+"]":d.isValidJavaScriptVariableName(b)?a+"."+b:a+"['"+b+"']",c?"("+a+" && "+e+")":e},compilerInfo:function(){var a=f,b=g[a];return"this.compilerInfo = ["+a+",'"+b+"'];\n"},appendToBuffer:function(a){return this.environment.isSimple?"return "+a+";":{appendToBuffer:!0,content:a,toString:function(){return"buffer += "+a+";"}}},initializeBuffer:function(){return this.quotedString("")},namespace:"Handlebars",compile:function(a,b,c,d){this.environment=a,this.options=b||{},h("debug",this.environment.disassemble()+"\n\n"),this.name=this.environment.name,this.isChild=!!c,this.context=c||{programs:[],environments:[],aliases:{}},this.preamble(),this.stackSlot=0,this.stackVars=[],this.registers={list:[]},this.hashes=[],this.compileStack=[],this.inlineStack=[],this.compileChildren(a,b);
    var e,f=a.opcodes;this.i=0;for(var g=f.length;this.i<g;this.i++)e=f[this.i],"DECLARE"===e.opcode?this[e.name]=e.value:this[e.opcode].apply(this,e.args),e.opcode!==this.stripNext&&(this.stripNext=!1);if(this.pushSource(""),this.stackSlot||this.inlineStack.length||this.compileStack.length)throw new i("Compile completed with content left on stack");return this.createFunctionContext(d)},preamble:function(){var a=[];if(this.isChild)a.push("");else{var b=this.namespace,c="helpers = this.merge(helpers, "+b+".helpers);";this.environment.usePartial&&(c=c+" partials = this.merge(partials, "+b+".partials);"),this.options.data&&(c+=" data = data || {};"),a.push(c)}this.environment.isSimple?a.push(""):a.push(", buffer = "+this.initializeBuffer()),this.lastContext=0,this.source=a},createFunctionContext:function(a){var b=this.stackVars.concat(this.registers.list);if(b.length>0&&(this.source[1]=this.source[1]+", "+b.join(", ")),!this.isChild)for(var c in this.context.aliases)this.context.aliases.hasOwnProperty(c)&&(this.source[1]=this.source[1]+", "+c+"="+this.context.aliases[c]);this.source[1]&&(this.source[1]="var "+this.source[1].substring(2)+";"),this.isChild||(this.source[1]+="\n"+this.context.programs.join("\n")+"\n"),this.environment.isSimple||this.pushSource("return buffer;");for(var d=this.isChild?["depth0","data"]:["Handlebars","depth0","helpers","partials","data"],e=0,f=this.environment.depths.list.length;f>e;e++)d.push("depth"+this.environment.depths.list[e]);var g=this.mergeSource();if(this.isChild||(g=this.compilerInfo()+g),a)return d.push(g),Function.apply(this,d);var i="function "+(this.name||"")+"("+d.join(",")+") {\n  "+g+"}";return h("debug",i+"\n\n"),i},mergeSource:function(){for(var a,b="",c=0,d=this.source.length;d>c;c++){var e=this.source[c];e.appendToBuffer?a=a?a+"\n    + "+e.content:e.content:(a&&(b+="buffer += "+a+";\n  ",a=void 0),b+=e+"\n  ")}return b},blockValue:function(){this.context.aliases.blockHelperMissing="helpers.blockHelperMissing";var a=["depth0"];this.setupParams(0,a),this.replaceStack(function(b){return a.splice(1,0,b),"blockHelperMissing.call("+a.join(", ")+")"})},ambiguousBlockValue:function(){this.context.aliases.blockHelperMissing="helpers.blockHelperMissing";var a=["depth0"];this.setupParams(0,a);var b=this.topStack();a.splice(1,0,b),this.pushSource("if (!"+this.lastHelper+") { "+b+" = blockHelperMissing.call("+a.join(", ")+"); }")},appendContent:function(a){this.pendingContent&&(a=this.pendingContent+a),this.stripNext&&(a=a.replace(/^\s+/,"")),this.pendingContent=a},strip:function(){this.pendingContent&&(this.pendingContent=this.pendingContent.replace(/\s+$/,"")),this.stripNext="strip"},append:function(){this.flushInline();var a=this.popStack();this.pushSource("if("+a+" || "+a+" === 0) { "+this.appendToBuffer(a)+" }"),this.environment.isSimple&&this.pushSource("else { "+this.appendToBuffer("''")+" }")},appendEscaped:function(){this.context.aliases.escapeExpression="this.escapeExpression",this.pushSource(this.appendToBuffer("escapeExpression("+this.popStack()+")"))},getContext:function(a){this.lastContext!==a&&(this.lastContext=a)},lookupOnContext:function(a){this.push(this.nameLookup("depth"+this.lastContext,a,"context"))},pushContext:function(){this.pushStackLiteral("depth"+this.lastContext)},resolvePossibleLambda:function(){this.context.aliases.functionType='"function"',this.replaceStack(function(a){return"typeof "+a+" === functionType ? "+a+".apply(depth0) : "+a})},lookup:function(a){this.replaceStack(function(b){return b+" == null || "+b+" === false ? "+b+" : "+this.nameLookup(b,a,"context")})},lookupData:function(){this.pushStackLiteral("data")},pushStringParam:function(a,b){this.pushStackLiteral("depth"+this.lastContext),this.pushString(b),"sexpr"!==b&&("string"==typeof a?this.pushString(a):this.pushStackLiteral(a))},emptyHash:function(){this.pushStackLiteral("{}"),this.options.stringParams&&(this.push("{}"),this.push("{}"))},pushHash:function(){this.hash&&this.hashes.push(this.hash),this.hash={values:[],types:[],contexts:[]}},popHash:function(){var a=this.hash;this.hash=this.hashes.pop(),this.options.stringParams&&(this.push("{"+a.contexts.join(",")+"}"),this.push("{"+a.types.join(",")+"}")),this.push("{\n    "+a.values.join(",\n    ")+"\n  }")},pushString:function(a){this.pushStackLiteral(this.quotedString(a))},push:function(a){return this.inlineStack.push(a),a},pushLiteral:function(a){this.pushStackLiteral(a)},pushProgram:function(a){null!=a?this.pushStackLiteral(this.programExpression(a)):this.pushStackLiteral(null)},invokeHelper:function(a,b,c){this.context.aliases.helperMissing="helpers.helperMissing",this.useRegister("helper");var d=this.lastHelper=this.setupHelper(a,b,!0),e=this.nameLookup("depth"+this.lastContext,b,"context"),f="helper = "+d.name+" || "+e;d.paramsInit&&(f+=","+d.paramsInit),this.push("("+f+",helper ? helper.call("+d.callParams+") : helperMissing.call("+d.helperMissingParams+"))"),c||this.flushInline()},invokeKnownHelper:function(a,b){var c=this.setupHelper(a,b);this.push(c.name+".call("+c.callParams+")")},invokeAmbiguous:function(a,b){this.context.aliases.functionType='"function"',this.useRegister("helper"),this.emptyHash();var c=this.setupHelper(0,a,b),d=this.lastHelper=this.nameLookup("helpers",a,"helper"),e=this.nameLookup("depth"+this.lastContext,a,"context"),f=this.nextStack();c.paramsInit&&this.pushSource(c.paramsInit),this.pushSource("if (helper = "+d+") { "+f+" = helper.call("+c.callParams+"); }"),this.pushSource("else { helper = "+e+"; "+f+" = typeof helper === functionType ? helper.call("+c.callParams+") : helper; }")},invokePartial:function(a){var b=[this.nameLookup("partials",a,"partial"),"'"+a+"'",this.popStack(),"helpers","partials"];this.options.data&&b.push("data"),this.context.aliases.self="this",this.push("self.invokePartial("+b.join(", ")+")")},assignToHash:function(a){var b,c,d=this.popStack();this.options.stringParams&&(c=this.popStack(),b=this.popStack());var e=this.hash;b&&e.contexts.push("'"+a+"': "+b),c&&e.types.push("'"+a+"': "+c),e.values.push("'"+a+"': ("+d+")")},compiler:d,compileChildren:function(a,b){for(var c,d,e=a.children,f=0,g=e.length;g>f;f++){c=e[f],d=new this.compiler;var h=this.matchExistingProgram(c);null==h?(this.context.programs.push(""),h=this.context.programs.length,c.index=h,c.name="program"+h,this.context.programs[h]=d.compile(c,b,this.context),this.context.environments[h]=c):(c.index=h,c.name="program"+h)}},matchExistingProgram:function(a){for(var b=0,c=this.context.environments.length;c>b;b++){var d=this.context.environments[b];if(d&&d.equals(a))return b}},programExpression:function(a){if(this.context.aliases.self="this",null==a)return"self.noop";for(var b,c=this.environment.children[a],d=c.depths.list,e=[c.index,c.name,"data"],f=0,g=d.length;g>f;f++)b=d[f],1===b?e.push("depth0"):e.push("depth"+(b-1));return(0===d.length?"self.program(":"self.programWithDepth(")+e.join(", ")+")"},register:function(a,b){this.useRegister(a),this.pushSource(a+" = "+b+";")},useRegister:function(a){this.registers[a]||(this.registers[a]=!0,this.registers.list.push(a))},pushStackLiteral:function(a){return this.push(new c(a))},pushSource:function(a){this.pendingContent&&(this.source.push(this.appendToBuffer(this.quotedString(this.pendingContent))),this.pendingContent=void 0),a&&this.source.push(a)},pushStack:function(a){this.flushInline();var b=this.incrStack();return a&&this.pushSource(b+" = "+a+";"),this.compileStack.push(b),b},replaceStack:function(a){var b,d,e,f="",g=this.isInline();if(g){var h=this.popStack(!0);if(h instanceof c)b=h.value,e=!0;else{d=!this.stackSlot;var i=d?this.incrStack():this.topStackName();f="("+this.push(i)+" = "+h+"),",b=this.topStack()}}else b=this.topStack();var j=a.call(this,b);return g?(e||this.popStack(),d&&this.stackSlot--,this.push("("+f+j+")")):(/^stack/.test(b)||(b=this.nextStack()),this.pushSource(b+" = ("+f+j+");")),b},nextStack:function(){return this.pushStack()},incrStack:function(){return this.stackSlot++,this.stackSlot>this.stackVars.length&&this.stackVars.push("stack"+this.stackSlot),this.topStackName()},topStackName:function(){return"stack"+this.stackSlot},flushInline:function(){var a=this.inlineStack;if(a.length){this.inlineStack=[];for(var b=0,d=a.length;d>b;b++){var e=a[b];e instanceof c?this.compileStack.push(e):this.pushStack(e)}}},isInline:function(){return this.inlineStack.length},popStack:function(a){var b=this.isInline(),d=(b?this.inlineStack:this.compileStack).pop();if(!a&&d instanceof c)return d.value;if(!b){if(!this.stackSlot)throw new i("Invalid stack pop");this.stackSlot--}return d},topStack:function(a){var b=this.isInline()?this.inlineStack:this.compileStack,d=b[b.length-1];return!a&&d instanceof c?d.value:d},quotedString:function(a){return'"'+a.replace(/\\/g,"\\\\").replace(/"/g,'\\"').replace(/\n/g,"\\n").replace(/\r/g,"\\r").replace(/\u2028/g,"\\u2028").replace(/\u2029/g,"\\u2029")+'"'},setupHelper:function(a,b,c){var d=[],e=this.setupParams(a,d,c),f=this.nameLookup("helpers",b,"helper");return{params:d,paramsInit:e,name:f,callParams:["depth0"].concat(d).join(", "),helperMissingParams:c&&["depth0",this.quotedString(b)].concat(d).join(", ")}},setupOptions:function(a,b){var c,d,e,f=[],g=[],h=[];f.push("hash:"+this.popStack()),this.options.stringParams&&(f.push("hashTypes:"+this.popStack()),f.push("hashContexts:"+this.popStack())),d=this.popStack(),e=this.popStack(),(e||d)&&(e||(this.context.aliases.self="this",e="self.noop"),d||(this.context.aliases.self="this",d="self.noop"),f.push("inverse:"+d),f.push("fn:"+e));for(var i=0;a>i;i++)c=this.popStack(),b.push(c),this.options.stringParams&&(h.push(this.popStack()),g.push(this.popStack()));return this.options.stringParams&&(f.push("contexts:["+g.join(",")+"]"),f.push("types:["+h.join(",")+"]")),this.options.data&&f.push("data:data"),f},setupParams:function(a,b,c){var d="{"+this.setupOptions(a,b).join(",")+"}";return c?(this.useRegister("options"),b.push("options"),"options="+d):(b.push(d),"")}};for(var j="break else new var case finally return void catch for switch while continue function this with default if throw delete in try do instanceof typeof abstract enum int short boolean export interface static byte extends long super char final native synchronized class float package throws const goto private transient debugger implements protected volatile double import public let yield".split(" "),k=d.RESERVED_WORDS={},l=0,m=j.length;m>l;l++)k[j[l]]=!0;return d.isValidJavaScriptVariableName=function(a){return!d.RESERVED_WORDS[a]&&/^[a-zA-Z_$][0-9a-zA-Z_$]*$/.test(a)?!0:!1},e=d}(d,c),l=function(a,b,c,d,e){"use strict";var f,g=a,h=b,i=c.parser,j=c.parse,k=d.Compiler,l=d.compile,m=d.precompile,n=e,o=g.create,p=function(){var a=o();return a.compile=function(b,c){return l(b,c,a)},a.precompile=function(b,c){return m(b,c,a)},a.AST=h,a.Compiler=k,a.JavaScriptCompiler=n,a.Parser=i,a.parse=j,a};return g=p(),g.create=p,f=g}(f,g,i,j,k);return l}();

// Mayocat

var Mayocat = (function (Mayocat) {

    var currentLocale,
        messageFormat,
        messageTemplates,
        templatesCache = {};

    $(function () {
        currentLocale = $("meta[property='mayocat:locale']").attr("content");
        if (typeof currentLocale !== 'undefined') {
            messageFormat = new MessageFormat(currentLocale);

            $.getJSON("/api/localization/theme/" + currentLocale, function (json)
            {
                messageTemplates = json;
            });
        }
    });

    Mayocat.localization = Mayocat.localization || {};

    Mayocat.localization.getMessage = function (key, args)
    {
        if (typeof currentLocale === 'undefined') {
            console.warn("We could not find the current locale.");
            console.warn("Please add the following meta tag to your index.html template:")
            console.warn('<meta property="mayocat:locale" content="{{locale.tag}}"/>')
            return;
        }
        if (typeof messageTemplates !== 'undefined') {
            try {
                var template = messageTemplates[key].value;
                return messageFormat.compile(template)(args);
            }
            catch (err) {
                console.warn(err);
            }
        }
        else {
            console.warn("We don't have localization for this locale: ", currentLocale);
        }
    };

    if (typeof Handlebars !== 'undefined') {
        Handlebars.registerHelper('message', function (key, options)
        {
            return String(Mayocat.localization.getMessage(key, options.hash));
        });
    }

    $(function(){

        $("html").addClass("js").removeClass("no-js");

        if ($("#cart").length) {
            // We are on the cart page, augment the cart with dynamic functionalities

            var template = Handlebars.compile($("#cart-table-template").html());

            var updateCart = function () {
                $.getJSON("/cart", function (result) {
                    var cart = result.cart,
                        updatedTable = template(cart);

                    $("#cart").html(updatedTable);
                    augmentCart();
                    window.picturefill && window.picturefill();
                });
            }

            var augmentCart = function () {

                var isUpdating = false;

                // AJAX change quantity
                $("#cart tr .plus, #cart tr .minus").click(function () {

                    if (isUpdating) {
                        return;
                    }
                    isUpdating = true;

                    var index = $(this).parents(".item").data("index"),
                        quantity = parseInt($(this).parents(".item").data("quantity")),
                        isAdd = $(this).hasClass("plus"),
                        data = {};

                    if (!isAdd && quantity == 1) {
                        // Delete item
                        isUpdating = true;

                        // Remove product asynchronously, then fetch updated cart and update HTML

                        var index = $(this).parents(".item").data("index"),
                            data = {},
                            item = $(this);

                        data["remove_" + index] = 1;

                        $.post("/cart/update", data, function () {
                            item.parents(".item").fadeOut(500, function () {
                                $(this).remove();

                                updateCart();
                            });
                        });
                    }

                    data["quantity_" + index] = isAdd ? quantity + 1 : quantity - 1;

                    $.post("/cart/update", data, function () {
                        updateCart();
                    });
                });

                // AJAX change shipping option
                $("[name='shipping_option']").change(function () {
                    isUpdating = true;
                    $.post("/cart/update", {
                        "shipping_option": $("[name='shipping_option']").val()
                    }, function () {
                        updateCart();
                    });
                });
            }

            augmentCart();


        }
    });


    Mayocat.render = function(templateId, context) {

        if (typeof templatesCache[templateId] === "undefined") {
            templatesCache[templateId] = Handlebars.compile($(templateId).html());
        }

        return templatesCache[templateId](context);
    }

    return Mayocat;

})(Mayocat || {});

// MessageFormat.js

/**
 * messageformat.js
 *
 * ICU PluralFormat + SelectFormat for JavaScript
 *
 * @author Alex Sexton - @SlexAxton
 * @version 0.1.5
 * @license WTFPL
 * @contributor_license Dojo CLA
 */
(function ( root ) {

    // Create the contructor function
    function MessageFormat ( locale, pluralFunc ) {
        var fallbackLocale;

        if ( locale && pluralFunc ) {
            MessageFormat.locale[ locale ] = pluralFunc;
        }

        // Defaults
        fallbackLocale = locale = locale || "en";
        pluralFunc = pluralFunc || MessageFormat.locale[ fallbackLocale = MessageFormat.Utils.getFallbackLocale( locale ) ];

        if ( ! pluralFunc ) {
            throw new Error( "Plural Function not found for locale: " + locale );
        }

        // Own Properties
        this.pluralFunc = pluralFunc;
        this.locale = locale;
        this.fallbackLocale = fallbackLocale;
    }

    // Set up the locales object. Add in english by default
    MessageFormat.locale = {
        "en" : function ( n ) {
            if ( n === 1 ) {
                return "one";
            }
            return "other";
        }
    };

    // Build out our basic SafeString type
    // more or less stolen from Handlebars by @wycats
    MessageFormat.SafeString = function( string ) {
        this.string = string;
    };

    MessageFormat.SafeString.prototype.toString = function () {
        return this.string.toString();
    };

    MessageFormat.Utils = {
        numSub : function ( string, key, depth ) {
            // make sure that it's not an escaped octothorpe
            return string.replace( /^#|[^\\]#/g, function (m) {
                var prefix = m && m.length === 2 ? m.charAt(0) : '';
                return prefix + '" + (function(){ var x = ' +
                    key+';\nif( isNaN(x) ){\nthrow new Error("MessageFormat: `"+lastkey_'+depth+'+"` isnt a number.");\n}\nreturn x;\n})() + "';
            });
        },
        escapeExpression : function (string) {
            var escape = {
                    "\n": "\\n",
                    "\"": '\\"'
                },
                badChars = /[\n"]/g,
                possible = /[\n"]/,
                escapeChar = function(chr) {
                    return escape[chr] || "&amp;";
                };

            // Don't escape SafeStrings, since they're already safe
            if ( string instanceof MessageFormat.SafeString ) {
                return string.toString();
            }
            else if ( string === null || string === false ) {
                return "";
            }

            if ( ! possible.test( string ) ) {
                return string;
            }
            return string.replace( badChars, escapeChar );
        },
        getFallbackLocale: function( locale ) {
            var tagSeparator = locale.indexOf("-") >= 0 ? "-" : "_";

            // Lets just be friends, fallback through the language tags
            while ( ! MessageFormat.locale.hasOwnProperty( locale ) ) {
                locale = locale.substring(0, locale.lastIndexOf( tagSeparator ));
                if (locale.length === 0) {
                    return null;
                }
            }

            return locale;
        }
    };

    // This is generated and pulled in for browsers.
    var mparser = (function(){
        /*
         * Generated by PEG.js 0.7.0.
         *
         * http://pegjs.majda.cz/
         */

        function quote(s) {
            /*
             * ECMA-262, 5th ed., 7.8.4: All characters may appear literally in a
             * string literal except for the closing quote character, backslash,
             * carriage return, line separator, paragraph separator, and line feed.
             * Any character may appear in the form of an escape sequence.
             *
             * For portability, we also escape escape all control and non-ASCII
             * characters. Note that "\0" and "\v" escape sequences are not used
             * because JSHint does not like the first and IE the second.
             */
            return '"' + s
                .replace(/\\/g, '\\\\')  // backslash
                .replace(/"/g, '\\"')    // closing quote character
                .replace(/\x08/g, '\\b') // backspace
                .replace(/\t/g, '\\t')   // horizontal tab
                .replace(/\n/g, '\\n')   // line feed
                .replace(/\f/g, '\\f')   // form feed
                .replace(/\r/g, '\\r')   // carriage return
                .replace(/[\x00-\x07\x0B\x0E-\x1F\x80-\uFFFF]/g, escape)
                + '"';
        }

        var result = {
            /*
             * Parses the input with a generated parser. If the parsing is successfull,
             * returns a value explicitly or implicitly specified by the grammar from
             * which the parser was generated (see |PEG.buildParser|). If the parsing is
             * unsuccessful, throws |PEG.parser.SyntaxError| describing the error.
             */
            parse: function(input, startRule) {
                var parseFunctions = {
                    "start": parse_start,
                    "messageFormatPattern": parse_messageFormatPattern,
                    "messageFormatPatternRight": parse_messageFormatPatternRight,
                    "messageFormatElement": parse_messageFormatElement,
                    "elementFormat": parse_elementFormat,
                    "pluralStyle": parse_pluralStyle,
                    "selectStyle": parse_selectStyle,
                    "pluralFormatPattern": parse_pluralFormatPattern,
                    "offsetPattern": parse_offsetPattern,
                    "selectFormatPattern": parse_selectFormatPattern,
                    "pluralForms": parse_pluralForms,
                    "stringKey": parse_stringKey,
                    "string": parse_string,
                    "id": parse_id,
                    "chars": parse_chars,
                    "char": parse_char,
                    "digits": parse_digits,
                    "hexDigit": parse_hexDigit,
                    "_": parse__,
                    "whitespace": parse_whitespace
                };

                if (startRule !== undefined) {
                    if (parseFunctions[startRule] === undefined) {
                        throw new Error("Invalid rule name: " + quote(startRule) + ".");
                    }
                } else {
                    startRule = "start";
                }

                var pos = 0;
                var reportFailures = 0;
                var rightmostFailuresPos = 0;
                var rightmostFailuresExpected = [];

                function padLeft(input, padding, length) {
                    var result = input;

                    var padLength = length - input.length;
                    for (var i = 0; i < padLength; i++) {
                        result = padding + result;
                    }

                    return result;
                }

                function escape(ch) {
                    var charCode = ch.charCodeAt(0);
                    var escapeChar;
                    var length;

                    if (charCode <= 0xFF) {
                        escapeChar = 'x';
                        length = 2;
                    } else {
                        escapeChar = 'u';
                        length = 4;
                    }

                    return '\\' + escapeChar + padLeft(charCode.toString(16).toUpperCase(), '0', length);
                }

                function matchFailed(failure) {
                    if (pos < rightmostFailuresPos) {
                        return;
                    }

                    if (pos > rightmostFailuresPos) {
                        rightmostFailuresPos = pos;
                        rightmostFailuresExpected = [];
                    }

                    rightmostFailuresExpected.push(failure);
                }

                function parse_start() {
                    var result0;
                    var pos0;

                    pos0 = pos;
                    result0 = parse_messageFormatPattern();
                    if (result0 !== null) {
                        result0 = (function(offset, messageFormatPattern) { return { type: "program", program: messageFormatPattern }; })(pos0, result0);
                    }
                    if (result0 === null) {
                        pos = pos0;
                    }
                    return result0;
                }

                function parse_messageFormatPattern() {
                    var result0, result1, result2;
                    var pos0, pos1;

                    pos0 = pos;
                    pos1 = pos;
                    result0 = parse_string();
                    if (result0 !== null) {
                        result1 = [];
                        result2 = parse_messageFormatPatternRight();
                        while (result2 !== null) {
                            result1.push(result2);
                            result2 = parse_messageFormatPatternRight();
                        }
                        if (result1 !== null) {
                            result0 = [result0, result1];
                        } else {
                            result0 = null;
                            pos = pos1;
                        }
                    } else {
                        result0 = null;
                        pos = pos1;
                    }
                    if (result0 !== null) {
                        result0 = (function(offset, s1, inner) {
                            var st = [];
                            if ( s1 && s1.val ) {
                                st.push( s1 );
                            }
                            for( var i in inner ){
                                if ( inner.hasOwnProperty( i ) ) {
                                    st.push( inner[ i ] );
                                }
                            }
                            return { type: 'messageFormatPattern', statements: st };
                        })(pos0, result0[0], result0[1]);
                    }
                    if (result0 === null) {
                        pos = pos0;
                    }
                    return result0;
                }

                function parse_messageFormatPatternRight() {
                    var result0, result1, result2, result3, result4, result5;
                    var pos0, pos1;

                    pos0 = pos;
                    pos1 = pos;
                    if (input.charCodeAt(pos) === 123) {
                        result0 = "{";
                        pos++;
                    } else {
                        result0 = null;
                        if (reportFailures === 0) {
                            matchFailed("\"{\"");
                        }
                    }
                    if (result0 !== null) {
                        result1 = parse__();
                        if (result1 !== null) {
                            result2 = parse_messageFormatElement();
                            if (result2 !== null) {
                                result3 = parse__();
                                if (result3 !== null) {
                                    if (input.charCodeAt(pos) === 125) {
                                        result4 = "}";
                                        pos++;
                                    } else {
                                        result4 = null;
                                        if (reportFailures === 0) {
                                            matchFailed("\"}\"");
                                        }
                                    }
                                    if (result4 !== null) {
                                        result5 = parse_string();
                                        if (result5 !== null) {
                                            result0 = [result0, result1, result2, result3, result4, result5];
                                        } else {
                                            result0 = null;
                                            pos = pos1;
                                        }
                                    } else {
                                        result0 = null;
                                        pos = pos1;
                                    }
                                } else {
                                    result0 = null;
                                    pos = pos1;
                                }
                            } else {
                                result0 = null;
                                pos = pos1;
                            }
                        } else {
                            result0 = null;
                            pos = pos1;
                        }
                    } else {
                        result0 = null;
                        pos = pos1;
                    }
                    if (result0 !== null) {
                        result0 = (function(offset, mfe, s1) {
                            var res = [];
                            if ( mfe ) {
                                res.push(mfe);
                            }
                            if ( s1 && s1.val ) {
                                res.push( s1 );
                            }
                            return { type: "messageFormatPatternRight", statements : res };
                        })(pos0, result0[2], result0[5]);
                    }
                    if (result0 === null) {
                        pos = pos0;
                    }
                    return result0;
                }

                function parse_messageFormatElement() {
                    var result0, result1, result2;
                    var pos0, pos1, pos2;

                    pos0 = pos;
                    pos1 = pos;
                    result0 = parse_id();
                    if (result0 !== null) {
                        pos2 = pos;
                        if (input.charCodeAt(pos) === 44) {
                            result1 = ",";
                            pos++;
                        } else {
                            result1 = null;
                            if (reportFailures === 0) {
                                matchFailed("\",\"");
                            }
                        }
                        if (result1 !== null) {
                            result2 = parse_elementFormat();
                            if (result2 !== null) {
                                result1 = [result1, result2];
                            } else {
                                result1 = null;
                                pos = pos2;
                            }
                        } else {
                            result1 = null;
                            pos = pos2;
                        }
                        result1 = result1 !== null ? result1 : "";
                        if (result1 !== null) {
                            result0 = [result0, result1];
                        } else {
                            result0 = null;
                            pos = pos1;
                        }
                    } else {
                        result0 = null;
                        pos = pos1;
                    }
                    if (result0 !== null) {
                        result0 = (function(offset, argIdx, efmt) {
                            var res = {
                                type: "messageFormatElement",
                                argumentIndex: argIdx
                            };
                            if ( efmt && efmt.length ) {
                                res.elementFormat = efmt[1];
                            }
                            else {
                                res.output = true;
                            }
                            return res;
                        })(pos0, result0[0], result0[1]);
                    }
                    if (result0 === null) {
                        pos = pos0;
                    }
                    return result0;
                }

                function parse_elementFormat() {
                    var result0, result1, result2, result3, result4, result5, result6;
                    var pos0, pos1;

                    pos0 = pos;
                    pos1 = pos;
                    result0 = parse__();
                    if (result0 !== null) {
                        if (input.substr(pos, 6) === "plural") {
                            result1 = "plural";
                            pos += 6;
                        } else {
                            result1 = null;
                            if (reportFailures === 0) {
                                matchFailed("\"plural\"");
                            }
                        }
                        if (result1 !== null) {
                            result2 = parse__();
                            if (result2 !== null) {
                                if (input.charCodeAt(pos) === 44) {
                                    result3 = ",";
                                    pos++;
                                } else {
                                    result3 = null;
                                    if (reportFailures === 0) {
                                        matchFailed("\",\"");
                                    }
                                }
                                if (result3 !== null) {
                                    result4 = parse__();
                                    if (result4 !== null) {
                                        result5 = parse_pluralStyle();
                                        if (result5 !== null) {
                                            result6 = parse__();
                                            if (result6 !== null) {
                                                result0 = [result0, result1, result2, result3, result4, result5, result6];
                                            } else {
                                                result0 = null;
                                                pos = pos1;
                                            }
                                        } else {
                                            result0 = null;
                                            pos = pos1;
                                        }
                                    } else {
                                        result0 = null;
                                        pos = pos1;
                                    }
                                } else {
                                    result0 = null;
                                    pos = pos1;
                                }
                            } else {
                                result0 = null;
                                pos = pos1;
                            }
                        } else {
                            result0 = null;
                            pos = pos1;
                        }
                    } else {
                        result0 = null;
                        pos = pos1;
                    }
                    if (result0 !== null) {
                        result0 = (function(offset, t, s) {
                            return {
                                type : "elementFormat",
                                key  : t,
                                val  : s.val
                            };
                        })(pos0, result0[1], result0[5]);
                    }
                    if (result0 === null) {
                        pos = pos0;
                    }
                    if (result0 === null) {
                        pos0 = pos;
                        pos1 = pos;
                        result0 = parse__();
                        if (result0 !== null) {
                            if (input.substr(pos, 6) === "select") {
                                result1 = "select";
                                pos += 6;
                            } else {
                                result1 = null;
                                if (reportFailures === 0) {
                                    matchFailed("\"select\"");
                                }
                            }
                            if (result1 !== null) {
                                result2 = parse__();
                                if (result2 !== null) {
                                    if (input.charCodeAt(pos) === 44) {
                                        result3 = ",";
                                        pos++;
                                    } else {
                                        result3 = null;
                                        if (reportFailures === 0) {
                                            matchFailed("\",\"");
                                        }
                                    }
                                    if (result3 !== null) {
                                        result4 = parse__();
                                        if (result4 !== null) {
                                            result5 = parse_selectStyle();
                                            if (result5 !== null) {
                                                result6 = parse__();
                                                if (result6 !== null) {
                                                    result0 = [result0, result1, result2, result3, result4, result5, result6];
                                                } else {
                                                    result0 = null;
                                                    pos = pos1;
                                                }
                                            } else {
                                                result0 = null;
                                                pos = pos1;
                                            }
                                        } else {
                                            result0 = null;
                                            pos = pos1;
                                        }
                                    } else {
                                        result0 = null;
                                        pos = pos1;
                                    }
                                } else {
                                    result0 = null;
                                    pos = pos1;
                                }
                            } else {
                                result0 = null;
                                pos = pos1;
                            }
                        } else {
                            result0 = null;
                            pos = pos1;
                        }
                        if (result0 !== null) {
                            result0 = (function(offset, t, s) {
                                return {
                                    type : "elementFormat",
                                    key  : t,
                                    val  : s.val
                                };
                            })(pos0, result0[1], result0[5]);
                        }
                        if (result0 === null) {
                            pos = pos0;
                        }
                    }
                    return result0;
                }

                function parse_pluralStyle() {
                    var result0;
                    var pos0;

                    pos0 = pos;
                    result0 = parse_pluralFormatPattern();
                    if (result0 !== null) {
                        result0 = (function(offset, pfp) {
                            return { type: "pluralStyle", val: pfp };
                        })(pos0, result0);
                    }
                    if (result0 === null) {
                        pos = pos0;
                    }
                    return result0;
                }

                function parse_selectStyle() {
                    var result0;
                    var pos0;

                    pos0 = pos;
                    result0 = parse_selectFormatPattern();
                    if (result0 !== null) {
                        result0 = (function(offset, sfp) {
                            return { type: "selectStyle", val: sfp };
                        })(pos0, result0);
                    }
                    if (result0 === null) {
                        pos = pos0;
                    }
                    return result0;
                }

                function parse_pluralFormatPattern() {
                    var result0, result1, result2;
                    var pos0, pos1;

                    pos0 = pos;
                    pos1 = pos;
                    result0 = parse_offsetPattern();
                    result0 = result0 !== null ? result0 : "";
                    if (result0 !== null) {
                        result1 = [];
                        result2 = parse_pluralForms();
                        while (result2 !== null) {
                            result1.push(result2);
                            result2 = parse_pluralForms();
                        }
                        if (result1 !== null) {
                            result0 = [result0, result1];
                        } else {
                            result0 = null;
                            pos = pos1;
                        }
                    } else {
                        result0 = null;
                        pos = pos1;
                    }
                    if (result0 !== null) {
                        result0 = (function(offset, op, pf) {
                            var res = {
                                type: "pluralFormatPattern",
                                pluralForms: pf
                            };
                            if ( op ) {
                                res.offset = op;
                            }
                            else {
                                res.offset = 0;
                            }
                            return res;
                        })(pos0, result0[0], result0[1]);
                    }
                    if (result0 === null) {
                        pos = pos0;
                    }
                    return result0;
                }

                function parse_offsetPattern() {
                    var result0, result1, result2, result3, result4, result5, result6;
                    var pos0, pos1;

                    pos0 = pos;
                    pos1 = pos;
                    result0 = parse__();
                    if (result0 !== null) {
                        if (input.substr(pos, 6) === "offset") {
                            result1 = "offset";
                            pos += 6;
                        } else {
                            result1 = null;
                            if (reportFailures === 0) {
                                matchFailed("\"offset\"");
                            }
                        }
                        if (result1 !== null) {
                            result2 = parse__();
                            if (result2 !== null) {
                                if (input.charCodeAt(pos) === 58) {
                                    result3 = ":";
                                    pos++;
                                } else {
                                    result3 = null;
                                    if (reportFailures === 0) {
                                        matchFailed("\":\"");
                                    }
                                }
                                if (result3 !== null) {
                                    result4 = parse__();
                                    if (result4 !== null) {
                                        result5 = parse_digits();
                                        if (result5 !== null) {
                                            result6 = parse__();
                                            if (result6 !== null) {
                                                result0 = [result0, result1, result2, result3, result4, result5, result6];
                                            } else {
                                                result0 = null;
                                                pos = pos1;
                                            }
                                        } else {
                                            result0 = null;
                                            pos = pos1;
                                        }
                                    } else {
                                        result0 = null;
                                        pos = pos1;
                                    }
                                } else {
                                    result0 = null;
                                    pos = pos1;
                                }
                            } else {
                                result0 = null;
                                pos = pos1;
                            }
                        } else {
                            result0 = null;
                            pos = pos1;
                        }
                    } else {
                        result0 = null;
                        pos = pos1;
                    }
                    if (result0 !== null) {
                        result0 = (function(offset, d) {
                            return d;
                        })(pos0, result0[5]);
                    }
                    if (result0 === null) {
                        pos = pos0;
                    }
                    return result0;
                }

                function parse_selectFormatPattern() {
                    var result0, result1;
                    var pos0;

                    pos0 = pos;
                    result0 = [];
                    result1 = parse_pluralForms();
                    while (result1 !== null) {
                        result0.push(result1);
                        result1 = parse_pluralForms();
                    }
                    if (result0 !== null) {
                        result0 = (function(offset, pf) {
                            return {
                                type: "selectFormatPattern",
                                pluralForms: pf
                            };
                        })(pos0, result0);
                    }
                    if (result0 === null) {
                        pos = pos0;
                    }
                    return result0;
                }

                function parse_pluralForms() {
                    var result0, result1, result2, result3, result4, result5, result6, result7;
                    var pos0, pos1;

                    pos0 = pos;
                    pos1 = pos;
                    result0 = parse__();
                    if (result0 !== null) {
                        result1 = parse_stringKey();
                        if (result1 !== null) {
                            result2 = parse__();
                            if (result2 !== null) {
                                if (input.charCodeAt(pos) === 123) {
                                    result3 = "{";
                                    pos++;
                                } else {
                                    result3 = null;
                                    if (reportFailures === 0) {
                                        matchFailed("\"{\"");
                                    }
                                }
                                if (result3 !== null) {
                                    result4 = parse__();
                                    if (result4 !== null) {
                                        result5 = parse_messageFormatPattern();
                                        if (result5 !== null) {
                                            result6 = parse__();
                                            if (result6 !== null) {
                                                if (input.charCodeAt(pos) === 125) {
                                                    result7 = "}";
                                                    pos++;
                                                } else {
                                                    result7 = null;
                                                    if (reportFailures === 0) {
                                                        matchFailed("\"}\"");
                                                    }
                                                }
                                                if (result7 !== null) {
                                                    result0 = [result0, result1, result2, result3, result4, result5, result6, result7];
                                                } else {
                                                    result0 = null;
                                                    pos = pos1;
                                                }
                                            } else {
                                                result0 = null;
                                                pos = pos1;
                                            }
                                        } else {
                                            result0 = null;
                                            pos = pos1;
                                        }
                                    } else {
                                        result0 = null;
                                        pos = pos1;
                                    }
                                } else {
                                    result0 = null;
                                    pos = pos1;
                                }
                            } else {
                                result0 = null;
                                pos = pos1;
                            }
                        } else {
                            result0 = null;
                            pos = pos1;
                        }
                    } else {
                        result0 = null;
                        pos = pos1;
                    }
                    if (result0 !== null) {
                        result0 = (function(offset, k, mfp) {
                            return {
                                type: "pluralForms",
                                key: k,
                                val: mfp
                            };
                        })(pos0, result0[1], result0[5]);
                    }
                    if (result0 === null) {
                        pos = pos0;
                    }
                    return result0;
                }

                function parse_stringKey() {
                    var result0, result1;
                    var pos0, pos1;

                    pos0 = pos;
                    result0 = parse_id();
                    if (result0 !== null) {
                        result0 = (function(offset, i) {
                            return i;
                        })(pos0, result0);
                    }
                    if (result0 === null) {
                        pos = pos0;
                    }
                    if (result0 === null) {
                        pos0 = pos;
                        pos1 = pos;
                        if (input.charCodeAt(pos) === 61) {
                            result0 = "=";
                            pos++;
                        } else {
                            result0 = null;
                            if (reportFailures === 0) {
                                matchFailed("\"=\"");
                            }
                        }
                        if (result0 !== null) {
                            result1 = parse_digits();
                            if (result1 !== null) {
                                result0 = [result0, result1];
                            } else {
                                result0 = null;
                                pos = pos1;
                            }
                        } else {
                            result0 = null;
                            pos = pos1;
                        }
                        if (result0 !== null) {
                            result0 = (function(offset, d) {
                                return d;
                            })(pos0, result0[1]);
                        }
                        if (result0 === null) {
                            pos = pos0;
                        }
                    }
                    return result0;
                }

                function parse_string() {
                    var result0, result1, result2, result3, result4;
                    var pos0, pos1, pos2;

                    pos0 = pos;
                    pos1 = pos;
                    result0 = parse__();
                    if (result0 !== null) {
                        result1 = [];
                        pos2 = pos;
                        result2 = parse__();
                        if (result2 !== null) {
                            result3 = parse_chars();
                            if (result3 !== null) {
                                result4 = parse__();
                                if (result4 !== null) {
                                    result2 = [result2, result3, result4];
                                } else {
                                    result2 = null;
                                    pos = pos2;
                                }
                            } else {
                                result2 = null;
                                pos = pos2;
                            }
                        } else {
                            result2 = null;
                            pos = pos2;
                        }
                        while (result2 !== null) {
                            result1.push(result2);
                            pos2 = pos;
                            result2 = parse__();
                            if (result2 !== null) {
                                result3 = parse_chars();
                                if (result3 !== null) {
                                    result4 = parse__();
                                    if (result4 !== null) {
                                        result2 = [result2, result3, result4];
                                    } else {
                                        result2 = null;
                                        pos = pos2;
                                    }
                                } else {
                                    result2 = null;
                                    pos = pos2;
                                }
                            } else {
                                result2 = null;
                                pos = pos2;
                            }
                        }
                        if (result1 !== null) {
                            result0 = [result0, result1];
                        } else {
                            result0 = null;
                            pos = pos1;
                        }
                    } else {
                        result0 = null;
                        pos = pos1;
                    }
                    if (result0 !== null) {
                        result0 = (function(offset, ws, s) {
                            var tmp = [];
                            for( var i = 0; i < s.length; ++i ) {
                                for( var j = 0; j < s[ i ].length; ++j ) {
                                    tmp.push(s[i][j]);
                                }
                            }
                            return {
                                type: "string",
                                val: ws + tmp.join('')
                            };
                        })(pos0, result0[0], result0[1]);
                    }
                    if (result0 === null) {
                        pos = pos0;
                    }
                    return result0;
                }

                function parse_id() {
                    var result0, result1, result2, result3;
                    var pos0, pos1;

                    pos0 = pos;
                    pos1 = pos;
                    result0 = parse__();
                    if (result0 !== null) {
                        if (/^[a-zA-Z$_]/.test(input.charAt(pos))) {
                            result1 = input.charAt(pos);
                            pos++;
                        } else {
                            result1 = null;
                            if (reportFailures === 0) {
                                matchFailed("[a-zA-Z$_]");
                            }
                        }
                        if (result1 !== null) {
                            result2 = [];
                            if (/^[^ \t\n\r,.+={}]/.test(input.charAt(pos))) {
                                result3 = input.charAt(pos);
                                pos++;
                            } else {
                                result3 = null;
                                if (reportFailures === 0) {
                                    matchFailed("[^ \\t\\n\\r,.+={}]");
                                }
                            }
                            while (result3 !== null) {
                                result2.push(result3);
                                if (/^[^ \t\n\r,.+={}]/.test(input.charAt(pos))) {
                                    result3 = input.charAt(pos);
                                    pos++;
                                } else {
                                    result3 = null;
                                    if (reportFailures === 0) {
                                        matchFailed("[^ \\t\\n\\r,.+={}]");
                                    }
                                }
                            }
                            if (result2 !== null) {
                                result3 = parse__();
                                if (result3 !== null) {
                                    result0 = [result0, result1, result2, result3];
                                } else {
                                    result0 = null;
                                    pos = pos1;
                                }
                            } else {
                                result0 = null;
                                pos = pos1;
                            }
                        } else {
                            result0 = null;
                            pos = pos1;
                        }
                    } else {
                        result0 = null;
                        pos = pos1;
                    }
                    if (result0 !== null) {
                        result0 = (function(offset, s1, s2) {
                            return s1 + (s2 ? s2.join('') : '');
                        })(pos0, result0[1], result0[2]);
                    }
                    if (result0 === null) {
                        pos = pos0;
                    }
                    return result0;
                }

                function parse_chars() {
                    var result0, result1;
                    var pos0;

                    pos0 = pos;
                    result1 = parse_char();
                    if (result1 !== null) {
                        result0 = [];
                        while (result1 !== null) {
                            result0.push(result1);
                            result1 = parse_char();
                        }
                    } else {
                        result0 = null;
                    }
                    if (result0 !== null) {
                        result0 = (function(offset, chars) { return chars.join(''); })(pos0, result0);
                    }
                    if (result0 === null) {
                        pos = pos0;
                    }
                    return result0;
                }

                function parse_char() {
                    var result0, result1, result2, result3, result4;
                    var pos0, pos1;

                    pos0 = pos;
                    if (/^[^{}\\\0-\x1F \t\n\r]/.test(input.charAt(pos))) {
                        result0 = input.charAt(pos);
                        pos++;
                    } else {
                        result0 = null;
                        if (reportFailures === 0) {
                            matchFailed("[^{}\\\\\\0-\\x1F \\t\\n\\r]");
                        }
                    }
                    if (result0 !== null) {
                        result0 = (function(offset, x) {
                            return x;
                        })(pos0, result0);
                    }
                    if (result0 === null) {
                        pos = pos0;
                    }
                    if (result0 === null) {
                        pos0 = pos;
                        if (input.substr(pos, 2) === "\\#") {
                            result0 = "\\#";
                            pos += 2;
                        } else {
                            result0 = null;
                            if (reportFailures === 0) {
                                matchFailed("\"\\\\#\"");
                            }
                        }
                        if (result0 !== null) {
                            result0 = (function(offset) {
                                return "\\#";
                            })(pos0);
                        }
                        if (result0 === null) {
                            pos = pos0;
                        }
                        if (result0 === null) {
                            pos0 = pos;
                            if (input.substr(pos, 2) === "\\{") {
                                result0 = "\\{";
                                pos += 2;
                            } else {
                                result0 = null;
                                if (reportFailures === 0) {
                                    matchFailed("\"\\\\{\"");
                                }
                            }
                            if (result0 !== null) {
                                result0 = (function(offset) {
                                    return "\u007B";
                                })(pos0);
                            }
                            if (result0 === null) {
                                pos = pos0;
                            }
                            if (result0 === null) {
                                pos0 = pos;
                                if (input.substr(pos, 2) === "\\}") {
                                    result0 = "\\}";
                                    pos += 2;
                                } else {
                                    result0 = null;
                                    if (reportFailures === 0) {
                                        matchFailed("\"\\\\}\"");
                                    }
                                }
                                if (result0 !== null) {
                                    result0 = (function(offset) {
                                        return "\u007D";
                                    })(pos0);
                                }
                                if (result0 === null) {
                                    pos = pos0;
                                }
                                if (result0 === null) {
                                    pos0 = pos;
                                    pos1 = pos;
                                    if (input.substr(pos, 2) === "\\u") {
                                        result0 = "\\u";
                                        pos += 2;
                                    } else {
                                        result0 = null;
                                        if (reportFailures === 0) {
                                            matchFailed("\"\\\\u\"");
                                        }
                                    }
                                    if (result0 !== null) {
                                        result1 = parse_hexDigit();
                                        if (result1 !== null) {
                                            result2 = parse_hexDigit();
                                            if (result2 !== null) {
                                                result3 = parse_hexDigit();
                                                if (result3 !== null) {
                                                    result4 = parse_hexDigit();
                                                    if (result4 !== null) {
                                                        result0 = [result0, result1, result2, result3, result4];
                                                    } else {
                                                        result0 = null;
                                                        pos = pos1;
                                                    }
                                                } else {
                                                    result0 = null;
                                                    pos = pos1;
                                                }
                                            } else {
                                                result0 = null;
                                                pos = pos1;
                                            }
                                        } else {
                                            result0 = null;
                                            pos = pos1;
                                        }
                                    } else {
                                        result0 = null;
                                        pos = pos1;
                                    }
                                    if (result0 !== null) {
                                        result0 = (function(offset, h1, h2, h3, h4) {
                                            return String.fromCharCode(parseInt("0x" + h1 + h2 + h3 + h4));
                                        })(pos0, result0[1], result0[2], result0[3], result0[4]);
                                    }
                                    if (result0 === null) {
                                        pos = pos0;
                                    }
                                }
                            }
                        }
                    }
                    return result0;
                }

                function parse_digits() {
                    var result0, result1;
                    var pos0;

                    pos0 = pos;
                    if (/^[0-9]/.test(input.charAt(pos))) {
                        result1 = input.charAt(pos);
                        pos++;
                    } else {
                        result1 = null;
                        if (reportFailures === 0) {
                            matchFailed("[0-9]");
                        }
                    }
                    if (result1 !== null) {
                        result0 = [];
                        while (result1 !== null) {
                            result0.push(result1);
                            if (/^[0-9]/.test(input.charAt(pos))) {
                                result1 = input.charAt(pos);
                                pos++;
                            } else {
                                result1 = null;
                                if (reportFailures === 0) {
                                    matchFailed("[0-9]");
                                }
                            }
                        }
                    } else {
                        result0 = null;
                    }
                    if (result0 !== null) {
                        result0 = (function(offset, ds) {
                            return parseInt((ds.join('')), 10);
                        })(pos0, result0);
                    }
                    if (result0 === null) {
                        pos = pos0;
                    }
                    return result0;
                }

                function parse_hexDigit() {
                    var result0;

                    if (/^[0-9a-fA-F]/.test(input.charAt(pos))) {
                        result0 = input.charAt(pos);
                        pos++;
                    } else {
                        result0 = null;
                        if (reportFailures === 0) {
                            matchFailed("[0-9a-fA-F]");
                        }
                    }
                    return result0;
                }

                function parse__() {
                    var result0, result1;
                    var pos0;

                    reportFailures++;
                    pos0 = pos;
                    result0 = [];
                    result1 = parse_whitespace();
                    while (result1 !== null) {
                        result0.push(result1);
                        result1 = parse_whitespace();
                    }
                    if (result0 !== null) {
                        result0 = (function(offset, w) { return w.join(''); })(pos0, result0);
                    }
                    if (result0 === null) {
                        pos = pos0;
                    }
                    reportFailures--;
                    if (reportFailures === 0 && result0 === null) {
                        matchFailed("whitespace");
                    }
                    return result0;
                }

                function parse_whitespace() {
                    var result0;

                    if (/^[ \t\n\r]/.test(input.charAt(pos))) {
                        result0 = input.charAt(pos);
                        pos++;
                    } else {
                        result0 = null;
                        if (reportFailures === 0) {
                            matchFailed("[ \\t\\n\\r]");
                        }
                    }
                    return result0;
                }


                function cleanupExpected(expected) {
                    expected.sort();

                    var lastExpected = null;
                    var cleanExpected = [];
                    for (var i = 0; i < expected.length; i++) {
                        if (expected[i] !== lastExpected) {
                            cleanExpected.push(expected[i]);
                            lastExpected = expected[i];
                        }
                    }
                    return cleanExpected;
                }

                function computeErrorPosition() {
                    /*
                     * The first idea was to use |String.split| to break the input up to the
                     * error position along newlines and derive the line and column from
                     * there. However IE's |split| implementation is so broken that it was
                     * enough to prevent it.
                     */

                    var line = 1;
                    var column = 1;
                    var seenCR = false;

                    for (var i = 0; i < Math.max(pos, rightmostFailuresPos); i++) {
                        var ch = input.charAt(i);
                        if (ch === "\n") {
                            if (!seenCR) { line++; }
                            column = 1;
                            seenCR = false;
                        } else if (ch === "\r" || ch === "\u2028" || ch === "\u2029") {
                            line++;
                            column = 1;
                            seenCR = true;
                        } else {
                            column++;
                            seenCR = false;
                        }
                    }

                    return { line: line, column: column };
                }


                var result = parseFunctions[startRule]();

                /*
                 * The parser is now in one of the following three states:
                 *
                 * 1. The parser successfully parsed the whole input.
                 *
                 *    - |result !== null|
                 *    - |pos === input.length|
                 *    - |rightmostFailuresExpected| may or may not contain something
                 *
                 * 2. The parser successfully parsed only a part of the input.
                 *
                 *    - |result !== null|
                 *    - |pos < input.length|
                 *    - |rightmostFailuresExpected| may or may not contain something
                 *
                 * 3. The parser did not successfully parse any part of the input.
                 *
                 *   - |result === null|
                 *   - |pos === 0|
                 *   - |rightmostFailuresExpected| contains at least one failure
                 *
                 * All code following this comment (including called functions) must
                 * handle these states.
                 */
                if (result === null || pos !== input.length) {
                    var offset = Math.max(pos, rightmostFailuresPos);
                    var found = offset < input.length ? input.charAt(offset) : null;
                    var errorPosition = computeErrorPosition();

                    throw new this.SyntaxError(
                        cleanupExpected(rightmostFailuresExpected),
                        found,
                        offset,
                        errorPosition.line,
                        errorPosition.column
                    );
                }

                return result;
            },

            /* Returns the parser source code. */
            toSource: function() { return this._source; }
        };

        /* Thrown when a parser encounters a syntax error. */

        result.SyntaxError = function(expected, found, offset, line, column) {
            function buildMessage(expected, found) {
                var expectedHumanized, foundHumanized;

                switch (expected.length) {
                    case 0:
                        expectedHumanized = "end of input";
                        break;
                    case 1:
                        expectedHumanized = expected[0];
                        break;
                    default:
                        expectedHumanized = expected.slice(0, expected.length - 1).join(", ")
                            + " or "
                            + expected[expected.length - 1];
                }

                foundHumanized = found ? quote(found) : "end of input";

                return "Expected " + expectedHumanized + " but " + foundHumanized + " found.";
            }

            this.name = "SyntaxError";
            this.expected = expected;
            this.found = found;
            this.message = buildMessage(expected, found);
            this.offset = offset;
            this.line = line;
            this.column = column;
        };

        result.SyntaxError.prototype = Error.prototype;

        return result;
    })();

    MessageFormat.prototype.parse = function () {
        // Bind to itself so error handling works
        return mparser.parse.apply( mparser, arguments );
    };

    MessageFormat.prototype.precompile = function ( ast ) {
        var self = this,
            needOther = false,
            fp = {
                begin: 'function(d){\nvar r = "";\n',
                end  : "return r;\n}"
            };

        function interpMFP ( ast, data ) {
            // Set some default data
            data = data || {};
            var s = '', i, tmp, lastkeyname;

            switch ( ast.type ) {
                case 'program':
                    return interpMFP( ast.program );
                case 'messageFormatPattern':
                    for ( i = 0; i < ast.statements.length; ++i ) {
                        s += interpMFP( ast.statements[i], data );
                    }
                    return fp.begin + s + fp.end;
                case 'messageFormatPatternRight':
                    for ( i = 0; i < ast.statements.length; ++i ) {
                        s += interpMFP( ast.statements[i], data );
                    }
                    return s;
                case 'messageFormatElement':
                    data.pf_count = data.pf_count || 0;
                    s += 'if(!d){\nthrow new Error("MessageFormat: No data passed to function.");\n}\n';
                    if ( ast.output ) {
                        s += 'r += d["' + ast.argumentIndex + '"];\n';
                    }
                    else {
                        lastkeyname = 'lastkey_'+(data.pf_count+1);
                        s += 'var '+lastkeyname+' = "'+ast.argumentIndex+'";\n';
                        s += 'var k_'+(data.pf_count+1)+'=d['+lastkeyname+'];\n';
                        s += interpMFP( ast.elementFormat, data );
                    }
                    return s;
                case 'elementFormat':
                    if ( ast.key === 'select' ) {
                        s += interpMFP( ast.val, data );
                        s += 'r += (pf_' +
                            data.pf_count +
                            '[ k_' + (data.pf_count+1) + ' ] || pf_'+data.pf_count+'[ "other" ])( d );\n';
                    }
                    else if ( ast.key === 'plural' ) {
                        s += interpMFP( ast.val, data );
                        s += 'if ( pf_'+(data.pf_count)+'[ k_'+(data.pf_count+1)+' + "" ] ) {\n';
                        s += 'r += pf_'+data.pf_count+'[ k_'+(data.pf_count+1)+' + "" ]( d ); \n';
                        s += '}\nelse {\n';
                        s += 'r += (pf_' +
                            data.pf_count +
                            '[ MessageFormat.locale["' +
                            self.fallbackLocale +
                            '"]( k_'+(data.pf_count+1)+' - off_'+(data.pf_count)+' ) ] || pf_'+data.pf_count+'[ "other" ] )( d );\n';
                        s += '}\n';
                    }
                    return s;
                /* // Unreachable cases.
                 case 'pluralStyle':
                 case 'selectStyle':*/
                case 'pluralFormatPattern':
                    data.pf_count = data.pf_count || 0;
                    s += 'var off_'+data.pf_count+' = '+ast.offset+';\n';
                    s += 'var pf_' + data.pf_count + ' = { \n';
                    needOther = true;
                    // We're going to simultaneously check to make sure we hit the required 'other' option.

                    for ( i = 0; i < ast.pluralForms.length; ++i ) {
                        if ( ast.pluralForms[ i ].key === 'other' ) {
                            needOther = false;
                        }
                        if ( tmp ) {
                            s += ',\n';
                        }
                        else{
                            tmp = 1;
                        }
                        s += '"' + ast.pluralForms[ i ].key + '" : ' + interpMFP( ast.pluralForms[ i ].val,
                            (function(){ var res = JSON.parse(JSON.stringify(data)); res.pf_count++; return res; })() );
                    }
                    s += '\n};\n';
                    if ( needOther ) {
                        throw new Error("No 'other' form found in pluralFormatPattern " + data.pf_count);
                    }
                    return s;
                case 'selectFormatPattern':

                    data.pf_count = data.pf_count || 0;
                    s += 'var off_'+data.pf_count+' = 0;\n';
                    s += 'var pf_' + data.pf_count + ' = { \n';
                    needOther = true;

                    for ( i = 0; i < ast.pluralForms.length; ++i ) {
                        if ( ast.pluralForms[ i ].key === 'other' ) {
                            needOther = false;
                        }
                        if ( tmp ) {
                            s += ',\n';
                        }
                        else{
                            tmp = 1;
                        }
                        s += '"' + ast.pluralForms[ i ].key + '" : ' + interpMFP( ast.pluralForms[ i ].val,
                            (function(){
                                var res = JSON.parse( JSON.stringify( data ) );
                                res.pf_count++;
                                return res;
                            })()
                        );
                    }
                    s += '\n};\n';
                    if ( needOther ) {
                        throw new Error("No 'other' form found in selectFormatPattern " + data.pf_count);
                    }
                    return s;
                /* // Unreachable
                 case 'pluralForms':
                 */
                case 'string':
                    return 'r += "' + MessageFormat.Utils.numSub(
                        MessageFormat.Utils.escapeExpression( ast.val ),
                        'k_' + data.pf_count + ' - off_' + ( data.pf_count - 1 ),
                        data.pf_count
                    ) + '";\n';
                default:
                    throw new Error( 'Bad AST type: ' + ast.type );
            }
        }
        return interpMFP( ast );
    };

    MessageFormat.prototype.compile = function ( message ) {
        return (new Function( 'MessageFormat',
            'return ' +
                this.precompile(
                    this.parse( message )
                )
        ))(MessageFormat);
    };


    if (typeof exports !== 'undefined') {
        if (typeof module !== 'undefined' && module.exports) {
            exports = module.exports = MessageFormat;
        }
        exports.MessageFormat = MessageFormat;
    }
    else if (typeof define === 'function' && define.amd) {
        define(function() {
            return MessageFormat;
        });
    }
    else {
        root['MessageFormat'] = MessageFormat;
    }

})( this );


MessageFormat.locale.af = function ( n ) {
    if ( n === 1 ) {
        return "one";
    }
    return "other";
};
MessageFormat.locale.am = function(n) {
    if (n === 0 || n == 1) {
        return 'one';
    }
    return 'other';
};
MessageFormat.locale.ar = function(n) {
    if (n === 0) {
        return 'zero';
    }
    if (n == 1) {
        return 'one';
    }
    if (n == 2) {
        return 'two';
    }
    if ((n % 100) >= 3 && (n % 100) <= 10 && n == Math.floor(n)) {
        return 'few';
    }
    if ((n % 100) >= 11 && (n % 100) <= 99 && n == Math.floor(n)) {
        return 'many';
    }
    return 'other';
};
MessageFormat.locale.bg = function ( n ) {
    if ( n === 1 ) {
        return "one";
    }
    return "other";
};
MessageFormat.locale.bn = function ( n ) {
    if ( n === 1 ) {
        return "one";
    }
    return "other";
};
MessageFormat.locale.br = function (n) {
    if (n === 0) {
        return 'zero';
    }
    if (n == 1) {
        return 'one';
    }
    if (n == 2) {
        return 'two';
    }
    if (n == 3) {
        return 'few';
    }
    if (n == 6) {
        return 'many';
    }
    return 'other';
};
MessageFormat.locale.ca = function ( n ) {
    if ( n === 1 ) {
        return "one";
    }
    return "other";
};
MessageFormat.locale.cs = function (n) {
    if (n == 1) {
        return 'one';
    }
    if (n == 2 || n == 3 || n == 4) {
        return 'few';
    }
    return 'other';
};
MessageFormat.locale.cy = function (n) {
    if (n === 0) {
        return 'zero';
    }
    if (n == 1) {
        return 'one';
    }
    if (n == 2) {
        return 'two';
    }
    if (n == 3) {
        return 'few';
    }
    if (n == 6) {
        return 'many';
    }
    return 'other';
};
MessageFormat.locale.da = function ( n ) {
    if ( n === 1 ) {
        return "one";
    }
    return "other";
};
MessageFormat.locale.de = function ( n ) {
    if ( n === 1 ) {
        return "one";
    }
    return "other";
};
MessageFormat.locale.el = function ( n ) {
    if ( n === 1 ) {
        return "one";
    }
    return "other";
};
MessageFormat.locale.en = function ( n ) {
    if ( n === 1 ) {
        return "one";
    }
    return "other";
};
MessageFormat.locale.es = function ( n ) {
    if ( n === 1 ) {
        return "one";
    }
    return "other";
};
MessageFormat.locale.et = function ( n ) {
    if ( n === 1 ) {
        return "one";
    }
    return "other";
};
MessageFormat.locale.eu = function ( n ) {
    if ( n === 1 ) {
        return "one";
    }
    return "other";
};
MessageFormat.locale.fa = function ( n ) {
    return "other";
};
MessageFormat.locale.fi = function ( n ) {
    if ( n === 1 ) {
        return "one";
    }
    return "other";
};
MessageFormat.locale.fil = function(n) {
    if (n === 0 || n == 1) {
        return 'one';
    }
    return 'other';
};
MessageFormat.locale.fr = function (n) {
    if (n >= 0 && n < 2) {
        return 'one';
    }
    return 'other';
};
MessageFormat.locale.ga = function (n) {
    if (n == 1) {
        return 'one';
    }
    if (n == 2) {
        return 'two';
    }
    return 'other';
};
MessageFormat.locale.gl = function ( n ) {
    if ( n === 1 ) {
        return "one";
    }
    return "other";
};
MessageFormat.locale.gsw = function ( n ) {
    if ( n === 1 ) {
        return "one";
    }
    return "other";
};
MessageFormat.locale.gu = function ( n ) {
    if ( n === 1 ) {
        return "one";
    }
    return "other";
};
MessageFormat.locale.he = function ( n ) {
    if ( n === 1 ) {
        return "one";
    }
    return "other";
};
MessageFormat.locale.hi = function(n) {
    if (n === 0 || n == 1) {
        return 'one';
    }
    return 'other';
};
MessageFormat.locale.hr = function (n) {
    if ((n % 10) == 1 && (n % 100) != 11) {
        return 'one';
    }
    if ((n % 10) >= 2 && (n % 10) <= 4 &&
        ((n % 100) < 12 || (n % 100) > 14) && n == Math.floor(n)) {
        return 'few';
    }
    if ((n % 10) === 0 || ((n % 10) >= 5 && (n % 10) <= 9) ||
        ((n % 100) >= 11 && (n % 100) <= 14) && n == Math.floor(n)) {
        return 'many';
    }
    return 'other';
};
MessageFormat.locale.hu = function(n) {
    return 'other';
};
MessageFormat.locale.id = function(n) {
    return 'other';
};
MessageFormat.locale["in"] = function(n) {
    return 'other';
};
MessageFormat.locale.is = function ( n ) {
    if ( n === 1 ) {
        return "one";
    }
    return "other";
};
MessageFormat.locale.it = function ( n ) {
    if ( n === 1 ) {
        return "one";
    }
    return "other";
};
MessageFormat.locale.iw = function ( n ) {
    if ( n === 1 ) {
        return "one";
    }
    return "other";
};
MessageFormat.locale.ja = function ( n ) {
    return "other";
};
MessageFormat.locale.kn = function ( n ) {
    return "other";
};
MessageFormat.locale.ko = function ( n ) {
    return "other";
};
MessageFormat.locale.lag = function (n) {
    if (n === 0) {
        return 'zero';
    }
    if (n > 0 && n < 2) {
        return 'one';
    }
    return 'other';
};
MessageFormat.locale.ln = function(n) {
    if (n === 0 || n == 1) {
        return 'one';
    }
    return 'other';
};
MessageFormat.locale.lt = function (n) {
    if ((n % 10) == 1 && ((n % 100) < 11 || (n % 100) > 19)) {
        return 'one';
    }
    if ((n % 10) >= 2 && (n % 10) <= 9 &&
        ((n % 100) < 11 || (n % 100) > 19) && n == Math.floor(n)) {
        return 'few';
    }
    return 'other';
};
MessageFormat.locale.lv = function (n) {
    if (n === 0) {
        return 'zero';
    }
    if ((n % 10) == 1 && (n % 100) != 11) {
        return 'one';
    }
    return 'other';
};
MessageFormat.locale.mk = function (n) {
    if ((n % 10) == 1 && n != 11) {
        return 'one';
    }
    return 'other';
};
MessageFormat.locale.ml = function ( n ) {
    if ( n === 1 ) {
        return "one";
    }
    return "other";
};
MessageFormat.locale.mo = function (n) {
    if (n == 1) {
        return 'one';
    }
    if (n === 0 || n != 1 && (n % 100) >= 1 &&
        (n % 100) <= 19 && n == Math.floor(n)) {
        return 'few';
    }
    return 'other';
};
MessageFormat.locale.mr = function ( n ) {
    if ( n === 1 ) {
        return "one";
    }
    return "other";
};
MessageFormat.locale.ms = function ( n ) {
    return "other";
};
MessageFormat.locale.mt = function (n) {
    if (n == 1) {
        return 'one';
    }
    if (n === 0 || ((n % 100) >= 2 && (n % 100) <= 4 && n == Math.floor(n))) {
        return 'few';
    }
    if ((n % 100) >= 11 && (n % 100) <= 19 && n == Math.floor(n)) {
        return 'many';
    }
    return 'other';
};
MessageFormat.locale.nl = function ( n ) {
    if ( n === 1 ) {
        return "one";
    }
    return "other";
};
MessageFormat.locale.no = function ( n ) {
    if ( n === 1 ) {
        return "one";
    }
    return "other";
};
MessageFormat.locale.or = function ( n ) {
    if ( n === 1 ) {
        return "one";
    }
    return "other";
};
MessageFormat.locale.pl = function (n) {
    if (n == 1) {
        return 'one';
    }
    if ((n % 10) >= 2 && (n % 10) <= 4 &&
        ((n % 100) < 12 || (n % 100) > 14) && n == Math.floor(n)) {
        return 'few';
    }
    if ((n % 10) === 0 || n != 1 && (n % 10) == 1 ||
        ((n % 10) >= 5 && (n % 10) <= 9 || (n % 100) >= 12 && (n % 100) <= 14) &&
            n == Math.floor(n)) {
        return 'many';
    }
    return 'other';
};
MessageFormat.locale.pt = function ( n ) {
    if ( n === 1 ) {
        return "one";
    }
    return "other";
};
MessageFormat.locale.ro = function (n) {
    if (n == 1) {
        return 'one';
    }
    if (n === 0 || n != 1 && (n % 100) >= 1 &&
        (n % 100) <= 19 && n == Math.floor(n)) {
        return 'few';
    }
    return 'other';
};
MessageFormat.locale.ru = function (n) {
    if ((n % 10) == 1 && (n % 100) != 11) {
        return 'one';
    }
    if ((n % 10) >= 2 && (n % 10) <= 4 &&
        ((n % 100) < 12 || (n % 100) > 14) && n == Math.floor(n)) {
        return 'few';
    }
    if ((n % 10) === 0 || ((n % 10) >= 5 && (n % 10) <= 9) ||
        ((n % 100) >= 11 && (n % 100) <= 14) && n == Math.floor(n)) {
        return 'many';
    }
    return 'other';
};
MessageFormat.locale.shi = function(n) {
    if (n >= 0 && n <= 1) {
        return 'one';
    }
    if (n >= 2 && n <= 10 && n == Math.floor(n)) {
        return 'few';
    }
    return 'other';
};
MessageFormat.locale.sk = function (n) {
    if (n == 1) {
        return 'one';
    }
    if (n == 2 || n == 3 || n == 4) {
        return 'few';
    }
    return 'other';
};
MessageFormat.locale.sl = function (n) {
    if ((n % 100) == 1) {
        return 'one';
    }
    if ((n % 100) == 2) {
        return 'two';
    }
    if ((n % 100) == 3 || (n % 100) == 4) {
        return 'few';
    }
    return 'other';
};
MessageFormat.locale.sq = function ( n ) {
    if ( n === 1 ) {
        return "one";
    }
    return "other";
};
MessageFormat.locale.sr = function (n) {
    if ((n % 10) == 1 && (n % 100) != 11) {
        return 'one';
    }
    if ((n % 10) >= 2 && (n % 10) <= 4 &&
        ((n % 100) < 12 || (n % 100) > 14) && n == Math.floor(n)) {
        return 'few';
    }
    if ((n % 10) === 0 || ((n % 10) >= 5 && (n % 10) <= 9) ||
        ((n % 100) >= 11 && (n % 100) <= 14) && n == Math.floor(n)) {
        return 'many';
    }
    return 'other';
};
MessageFormat.locale.sv = function ( n ) {
    if ( n === 1 ) {
        return "one";
    }
    return "other";
};
MessageFormat.locale.sw = function ( n ) {
    if ( n === 1 ) {
        return "one";
    }
    return "other";
};
MessageFormat.locale.ta = function ( n ) {
    if ( n === 1 ) {
        return "one";
    }
    return "other";
};
MessageFormat.locale.te = function ( n ) {
    if ( n === 1 ) {
        return "one";
    }
    return "other";
};
MessageFormat.locale.th = function ( n ) {
    return "other";
};
MessageFormat.locale.tl = function(n) {
    if (n === 0 || n == 1) {
        return 'one';
    }
    return 'other';
};
MessageFormat.locale.tr = function(n) {
    return 'other';
};
MessageFormat.locale.uk = function (n) {
    if ((n % 10) == 1 && (n % 100) != 11) {
        return 'one';
    }
    if ((n % 10) >= 2 && (n % 10) <= 4 &&
        ((n % 100) < 12 || (n % 100) > 14) && n == Math.floor(n)) {
        return 'few';
    }
    if ((n % 10) === 0 || ((n % 10) >= 5 && (n % 10) <= 9) ||
        ((n % 100) >= 11 && (n % 100) <= 14) && n == Math.floor(n)) {
        return 'many';
    }
    return 'other';
};
MessageFormat.locale.ur = function ( n ) {
    if ( n === 1 ) {
        return "one";
    }
    return "other";
};
MessageFormat.locale.vi = function ( n ) {
    return "other";
};
MessageFormat.locale.zh = function ( n ) {
    return "other";
};
