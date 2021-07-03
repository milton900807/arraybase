grammar gb;

@header {
  package com.arraybase.lang;
}

compilationUnit
    :   block* EOF
    ;
block
    :   variableDecl
    |   statement
    ;
variableDecl
    :   type variableDeclarators ';'
    ;
variableDeclarators
    :   variableDeclarator (',' variableDeclarator)*
    ;
variableDeclarator
    :   variableDeclaratorId ('=' variableInitializer)?
    ;
variableDeclaratorId
    :   Identifier ('[' ']')*
    ;
variableInitializer  // add the array initializer here for doing some other fancy stuff. 
    :   expression
    ;
parExpression
	: '(' expression ')'
	;
expression
    :   primary
    | 	literal
    |   target
    |   expression conditional expression
    |   expression '.' Identifier
    |   target '.' action '(' data ')'
    |   target '.' action '(' data ')' ('[' field ']')*
    |   expression '[' Identifier ']'
 	|   expression ('='<assoc=right>) expression
    |   ('+'|'-'|'++'|'--') expression
    |   expression ('+'|'-') expression
    |   expression conditionalJoin expression
    ;  
statement
    :   'forall' parExpression '{' statement '}' ('else' '{' statement '}' )?
    |   'where' parExpression '{' statement '}' ('else' '{' statement '}' )?
    |	'for' parExpression '{' statement '}'
    |   statementExpression ';'
    ;
    
conditional
	:	'==' 
	|	'!='
	|	'<='
	|	'<'
	|	'>'
	;
conditionalJoin 
	:	'&&' 
	|	'||'
	;

statementExpression
    :   expression
    ;
target
	: Identifier
	| ('/')* Identifier ('/' target)*
	;
action
	:  Identifier
	;
data 
	:  Identifier
	|  '*'
	|  data ':' data
	;
field
	: Identifier
	;
primary
    :   literal
    |   Identifier
    ;
      
literal
    :   'table'
    |   'null'
    |	StringLiteral
    |   FloatingPointLiteral
    |   IntegerLiteral
	|   BooleanLiteral    
    ;
    
type
    :   primitiveType ('[' ']')*
    ;
primitiveType
    :   'boolean'
    |   'char'
    |   'byte'
    |   'int'
    |   'long'
    |   'float'
    |   'double'
    |   'sint'
    |   'slong'
    |   'sfloat'
    |   'sdouble'
    |   'table'
    |   'row'
    |   'cell'
    |   'field'
    |   'string'
    ;

StringLiteral
    :   '"' StringCharacters? '"'
    ;
BooleanLiteral
    :   'true'
    |   'false'
    ;
IntegerLiteral
    :   DecimalIntegerLiteral
    ;    
fragment
DecimalIntegerLiteral
    :   DecimalNumeral
    ;
fragment
DecimalNumeral
    :   '0'
    |   NonZeroDigit (Digits? | Underscores Digits)
    ;
fragment
Digits
    :   Digit (DigitOrUnderscore* Digit)?
    ;

fragment
Digit
    :   '0'
    |   NonZeroDigit
    ;

fragment
NonZeroDigit
    :   [1-9]
    ;

fragment
DigitOrUnderscore
    :   Digit
    |   '_'
    ;
fragment
Underscores
    :   '_'+
    ;    
FloatingPointLiteral
    :   DecimalFloatingPointLiteral
    ;
fragment
DecimalFloatingPointLiteral
    :   Digits '.' Digits? ExponentPart?
    |   '.' Digits ExponentPart?
    |   Digits ExponentPart
    |   Digits FloatTypeSuffix
    ;
fragment
FloatTypeSuffix
    :   [fFdD]
    ;
fragment
SignedInteger
    :   Sign? Digits
    ;
fragment
ExponentPart
    :   ExponentIndicator SignedInteger
    ;
fragment
ExponentIndicator
    :   [eE]
    ;
fragment
Sign
    :   [+-]
    ;
fragment
StringCharacters
    :   StringCharacter+
    ;

fragment
StringCharacter
    :   ~["\\]
    ;

// Identifiers
Identifier
    :   GBCharacters GBCharsOrDigit*
    ;

fragment
GBCharacters
    :   [a-zA-Z$_] // these are the "java letters" below 0xFF
    |   // covers all characters above 0xFF which are not a surrogate
        ~[\u0000-\u00FF\uD800-\uDBFF]
        {Character.isJavaIdentifierStart(_input.LA(-1))}?
    |   // covers UTF-16 surrogate pairs encodings for U+10000 to U+10FFFF
        [\uD800-\uDBFF] [\uDC00-\uDFFF]
        {Character.isJavaIdentifierStart(Character.toCodePoint((char)_input.LA(-2), (char)_input.LA(-1)))}?
    ;

fragment
GBCharsOrDigit
    :   [a-zA-Z0-9$_] // these are the "java letters or digits" below 0xFF
	|   // covers all characters above 0xFF which are not a surrogate
        ~[\u0000-\u00FF\uD800-\uDBFF]
        {Character.isJavaIdentifierPart(_input.LA(-1))}?
    |   // covers UTF-16 surrogate pairs encodings for U+10000 to U+10FFFF
        [\uD800-\uDBFF] [\uDC00-\uDFFF]
        {Character.isJavaIdentifierPart(Character.toCodePoint((char)_input.LA(-2), (char)_input.LA(-1)))}?
    ;

WS  :  [ \t\r\n\u000C]+ -> skip
    ;

COMMENT
    :   '/*' .*? '*/' -> skip
    ;

LINE_COMMENT
    :   '//' ~[\r\n]* -> skip
    ;