D                       [0-9]

%{
   #include "arduino_syntax.h"

%}
%option noyywrap
%option yylineno

%%
  /* ==== Keywords ==== */
"application"           return KAPPL;
"sensor"                return KSENSOR;
"actuator"              return KACTUATOR;
"HIGH"                  return KHIGH;
"LOW"                   return KLOW;
"is"                    return KIS;
"const"                 return KCONST;
"and"                   return KAND;
"or"                    return KOR;
"=="                    return KEQ;
"!="                    return KNE;
">="                    return KGEQ;
"<="                    return KLEQ;
">"                     return KGT;
"<"                     return KLT;
"->"                    return RIGHT;
"<-"                    return LEFT;
">>"                    return INITSTATE;


  /* ==== port numbers (as strings) and identifiers */
/* [1-9]|(1[012])          { yylval.value = atoi(yytext); return PORT_NUMBER; } */
{D}+"."{D}+             { yylval.fval = atof(yytext); return FLOAT; }
{D}+                    { yylval.ival = atoi(yytext); return INT; }
[a-zA-Z][a-zA-Z0-9_]+   { yylval.name  = strdup(yytext); return IDENT; }

  /* ==== Spaces and comments ==== */
[ \t\n]                 { }
"//".*                  { }

 /* ==== Default ==== */
.                       return *yytext;

