// Yacc grammar for ArduinoML in C
//
//           Author: Erick Gallesio [eg@unice.fr]
//    Creation date: 16-Nov-2017 17:54 (eg)
// Last file update: 30-Nov-2017 15:27 (eg)

%{
#define  YYERROR_VERBOSE 1      // produce verbose syntax error messages

#include <stdio.h>
#include <stdlib.h>
#include "arduino_syntax.h"
#include "arduino.h"

#define  YYERROR_VERBOSE 1      // produce verbose syntax error messages

//  Prototypes
int  yylex(void);
void yyerror(const char *s);
%}

%union {
    int                             int_value;
    float                           float_value;
    char                            *name;
    struct arduino_transition       *transition;
    struct arduino_action           *action;
    struct arduino_state            *state;
    struct arduino_brick            *brick;
    struct arduino_constant         *constant;
    struct arduino_condition_tree   *condition_tree;
    struct arduino_condition        *condition;
    enum binary_op                  binop;
};

%token KAPPL KSENSOR KACTUATOR KIS KCONST KAND KOR LEFT RIGHT INITSTATE
%token  <name>          IDENT KHIGH KLOW KEQ KNE KGEQ KLEQ KGT KLT
%token  <int_value>     INT
%token  <float_value>   FLOAT

%type   <name>                name
%type   <int_value>           signal
%type   <float_value>         value
%type   <transition>          transition
%type   <action>              action actions
%type   <state>               state states
%type   <brick>               brick bricks
%type   <constant>            constants constant
%type   <binop>               binary_rel binary_op
%type   <condition_tree>      condition_tree
%type   <condition>           condition
%%

start:          KAPPL name '{' bricks constants states '}'  { emit_code($2, $4, $5, $6); }
     ;

bricks:         bricks brick ';'                            { $$ = add_brick($2, $1); }
      |         error ';'                                   { yyerrok; }
      |         /* empty */                                 { $$ = NULL; }
      ;

brick:          KACTUATOR name ':' INT                      { $$ = make_brick($4, actuator, $2); }
     |          KSENSOR   name ':' INT                      { $$ = make_brick($4, sensor, $2); }
     ;

constants:      constants constant ';'                      { $$ = add_constant($1, $2); }
      |         error ';'                                   { yyerrok; }
      |         /* empty */                                 { $$ = NULL; }
      ;

constant:       KCONST name ':' FLOAT ';'                   { $$ = make_constant($2, $4); }
      |         KCONST name ':' INT ';'                     { $$ = make_constant($2, (float) $4); }
      ;

states:         states state                                { $$ = add_state($1, $2); }
      |         /*empty */                                  { $$ = NULL; }
      ;

state:          name '{' actions  transition '}'            { $$ = make_state($1, $3, $4, 0); }
      |         INITSTATE name '{' actions  transition '}'  { $$ = make_state($2, $4, $5, 1); }
      ;


actions:        actions action ';'                          { $$ = add_action($1, $2); }
       |        action ';'                                  { $$ = $1; }
       |        error ';'                                   { yyerrok; }
       ;

action:         name LEFT signal                            { $$ = make_signal_action($1, signal, $3); }
      |         name LEFT value                             { $$ = make_float_action($1, value, $3); }
      |         name LEFT name                              { $$ = make_var_action($1, var, $3); }
      ;

transition:     condition_tree RIGHT name ';'               { $$ = make_transition($1, $3, $5); }
          |     error ';'                                   { yyerrok; }
          ;

condition_tree: condition_tree binary_rel condition_tree    { $$ = make_boolean_condition_tree(make_boolean_condition($2, $1, $3)); }
          |     '(' condition_tree ')'                      { $$ = $2; }
          |      condition                                  { $$ = make_condition_tree($1); }
          ;

condition:      name KIS signal                             { $$ = make_condition(make_signal_condition($1, $3, EQ)); }
          |     name binary_op value                        { $$ = make_condition(make_float_condition($1, $3, $2)); }
          |     name binary_op name                         { $$ = make_condition(make_var_condition($1, $3, $2)); }

binary_rel:     KAND                                        { $$ = AND; }
          |     KOR                                         { $$ = OR; }
          ;

binary_op:      KEQ                                         { $$ = EQ; }
          |     KNE                                         { $$ = NEQ; }
          |     KGEQ                                        { $$ = GEQ; }
          |     KLEQ                                        { $$ = LEQ; }
          |     KGT                                         { $$ = GT; }
          |     KLT                                         { $$ = LT; }
          ;

signal:         KHIGH                                       { $$ = 1; }
      |         KLOW                                        { $$ = 0; }
      ;

name:           IDENT                                       { $$ = $1;}
      ;

value:          INT                                         { $$ = (float) $1; }
      |         FLOAT                                       { $$ = $1; }
      ;


%%
void yyerror(const char *msg) { extern int yylineno; error_msg(yylineno, msg); }
