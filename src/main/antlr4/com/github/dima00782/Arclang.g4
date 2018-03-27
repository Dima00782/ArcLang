grammar Arclang;

/* Lexer */

fragment DIGIT: [0-9];
fragment NONDIGIT: [a-zA-Z_];
fragment HEXADECIMALDIGIT: [0-9a-fA-F];
fragment Hexquad: HEXADECIMALDIGIT HEXADECIMALDIGIT HEXADECIMALDIGIT HEXADECIMALDIGIT;
fragment Universalcharactername : '\\u' Hexquad
	                            | '\\U' Hexquad Hexquad;
fragment Identifiernondigit : NONDIGIT
                            | Universalcharactername;

/*
    Identifiernondigit
	| Identifier Identifiernondigit
	| Identifier DIGIT
*/
Identifier: Identifiernondigit(Identifiernondigit | DIGIT)*;

BlockComment: '/*' .*? '*/' -> skip;
LineComment: '//' ~[\r\n]* -> skip;

/* ignore white space characters */
WS: (' ' | '\t') -> skip;

/* Parser */

NEWLINE: [\r\n]+ ;
BR_OPEN:  '{';
BR_CLOSE: '}';

prog:                   instList EOF;
instList:	            (instr)*;
assignmentoperator:     '=';
weakassignmentoperator: '~=';
objectOperator:         'object';
threadOperator:         'thread';
sleepOperator:          'sleep';
sleepRandomOperator:    'sleepr';
dumpOperator:           'dump';
instr:                  Identifier (assignmentoperator | weakassignmentoperator) (objectOperator | Identifier)
     |	                sleepOperator
     |	                sleepRandomOperator
     |	                dumpOperator Identifier
     |                  threadOperator BR_OPEN instList BR_CLOSE
     |                  NEWLINE // empty instruction
     ;
