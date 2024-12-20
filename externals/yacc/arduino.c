/*
 * arduino.c     -- ArduinoML configuration and code generation
 *
 *           Author: Erick Gallesio [eg@unice.fr]
 *    Creation date: 17-Nov-2017 11:13
 * Last file update:  1-Dec-2017 18:47 (eg)
 */

#include <stdio.h>
#include <stdarg.h>
#include <stdlib.h>
#include <string.h>
#include "arduino.h"

extern int yylineno;               ///< line number (managed by lex)
static int error_detected = 0;     ///< The number of errors while compiling a file
char *input_path = NULL;           ///< Name of the input path or NULL if stdin

static void *__must_malloc(size_t sz, const char *func, const char *file, int line);
#define must_malloc(_sz)         (__must_malloc((_sz), __func__, __FILE__, __LINE__))

// ======================================================================
//                              B R I C K S
// ======================================================================
struct arduino_brick {
  char *var;
  enum port_assignment kind;
  int port_number;
  struct arduino_brick *next;
};


/// Find name in the list of already declared bricks
static int find_brick(char *name, Brick *list) {
  for (Brick *p = list; p; p = p->next) {
    if (strcmp(name, p->var) == 0) return 1;
  }
  return 0;
}


/// declare a new brick on port `number`
Brick *make_brick(int number, enum port_assignment kind, char *name) {
  Brick *p = must_malloc(sizeof(Brick));

  p-> var  = name;
  p-> kind = kind;
  p->port_number = number;
  p->next = NULL;
  return p;
}


/// Add a brick to a list of bricks
Brick *add_brick(Brick *b, Brick *list) {
  // Check that the given variable is not already used
  if (find_brick(b->var, list)) {
    error_msg(yylineno, "name '%s' was already used", b->var);
  }
  // Check that the given port is not already used
  for (Brick *p = list; p; p = p->next) {
    if (p->port_number == b->port_number)
      error_msg(yylineno, "port %d was already used by '%s'", p->port_number, b->var);
  }
  b->next = list;
  return b;
}

// ======================================================================
//                            C O N S T A N T S
// ======================================================================
struct arduino_constant {
  char *var;
  float value;
  struct arduino_constant *next;
};

/// Find name in the list of already declared constants
static int find_constant(char *name, Constant *list) {
  for (Constant *p = list; p; p = p->next) {
    if (strcmp(name, p->var) == 0) return 1;
  }
  return 0;
}

// Make a new constant named `name` with value `value`
Constant *make_constant(char *name, float value) {
  Constant *p = must_malloc(sizeof(Constant));

  p->var   = name;
  p->value = value;
  p->next  = NULL;
  return p;
}

// Add a constant to a list of constants
Constant *add_constant(Constant *list, Constant *c) {
  // Check that the given variable is not already used
  if (find_brick(c->var, list)) {
    error_msg(yylineno, "name '%s' was already used", c->var);
  }
  c->next = list;
  return c;
}


// ======================================================================
//                            T R A N S I T I O N
// ======================================================================
struct arduino_transition {
  int lineno;
  char *var_name;
  int sig_value;
  char *newstate;
};

/// Make a new transition (when `var` is `signal` goto `newstate`
Transition *make_transition(char *var, int signal, char *newstate) {
  Transition *p = must_malloc(sizeof(Transition));

  p->lineno    = yylineno;
  p->var_name  = var;
  p->sig_value = signal;
  p->newstate  = newstate;
  return p;
}


// ======================================================================
//                            A C T I O N
// ======================================================================

struct arduino_action {
  int lineno;
  char *var_name;
  enum const_type type;
  union value *value;
  struct arduino_action *next;
};

Action *make_signal_action(char *var, enum const_type type, int signal) {
  union value *v = must_malloc(sizeof(*v));
  v->signal_value = signal;
  
  Action *p = make_action(var, type, v);
  return p;
}

Action *make_float_action(char *var, enum const_type type, float float_value) {
  union value *v = must_malloc(sizeof(*v));
  v->float_value = float_value;
  
  Action *p = make_action(var, type, v);
  return p;
}

Action *make_var_action(char *var, enum const_type type, char *var_value) {
  union value *v = must_malloc(sizeof(*v));
  v->var_value = var_value;
  
  Action *p = make_action(var, type, v);
  return p;
}

Action *make_action(char *var, enum const_type type, union value *value) {
  Action *p = must_malloc(sizeof(Action));

  p->lineno         = yylineno;
  p->var_name       = var;
  p->type           = type;
  p->value          = value;
  p->next           = NULL;
  return p;
}

// Add an action to a list of actions
Action *add_action(Action *list, Action *a) {
  if (list) {
    Action *tmp = list;
    while (tmp->next) tmp = tmp->next;
    tmp->next = a;
    return list;
  }
  return a;
}

// ======================================================================
//                            S Τ A Τ E
// ======================================================================
struct arduino_state {
  int lineno;
  char *name;
  Action *actions;
  Transition *transition;
  struct arduino_state *next;
};

static State *initial_state = NULL;

static int find_state(char *name, State *list) {
  for (State *p = list; p; p = p->next) {
    if (strcmp(name, p->name) == 0) return 1;
  }
  return 0;
}


// Make a new state named `var` with a list of `actions` and a `transition`
// `initial` must be one if the state is the initial one
State *make_state(char *var, Action *actions, Transition *transition, int initial) {
  State *p = must_malloc(sizeof(State));

  p->lineno     = yylineno;
  p->name       = var;
  p-> actions   = actions;
  p->transition = transition;
  p->next       = NULL;
  if (initial) initial_state = p;       // Keep a reference on the initial state
  return p;
}

// Add a state to a list of states
State *add_state(State *list, State *s) {
  if (list) {
    State *tmp = list;
    while (tmp->next) tmp = tmp->next;
    tmp->next = s;
    return list;
  }
  return s;
}

// ======================================================================
//                          C O N D I T I O N S
// ======================================================================

struct arduino_condition_tree {
  int is_condition;
  union condition_tree_union *condition_tree_union;
};

struct arduino_condition {
  char *var;
  enum const_type type;
  union value *value;
  enum binary_op op;
};

struct arduino_boolean_condition {
  enum binary_op op;
  struct arduino_condition_tree *left;
  struct arduino_condition_tree *right;
};

Condition *make_condition(char *var, enum const_type type, union value *value, enum binary_op op) {
  Condition *p = must_malloc(sizeof(Condition));

  p->var   = var;
  p->type  = type;
  p->value = value;
  p->op    = op;
  return p;
}

Condition *make_signal_condition(char *var, int signal, enum binary_op op) {
  union value *v = must_malloc(sizeof(*v));
  v->signal_value = signal;
  
  Condition *p = make_condition(var, signal, v, op);
  return p;
}

Condition *make_float_condition(char *var, float float_value, enum binary_op op) {
  union value *v = must_malloc(sizeof(*v));
  v->float_value = float_value;
  
  Condition *p = make_condition(var, value, v, op);
  return p;
}

Condition *make_var_condition(char *var, char *var_value, enum binary_op op) {
  union value *v = must_malloc(sizeof(*v));
  v->var_value = var_value;
  
  Condition *p = make_condition(var, var, v, op);
  return p;
}

BooleanCondition *make_boolean_condition(enum binary_op op, struct arduino_condition_tree *left, struct arduino_condition_tree *right) {
  BooleanCondition *p = must_malloc(sizeof(BooleanCondition));

  p->op    = op;
  p->left  = left;
  p->right = right;
  return p;
}

ConditionTree *make_condition_tree(Condition *condition) {
  ConditionTree *p = must_malloc(sizeof(ConditionTree));
  union condition_tree_union *ct = must_malloc(sizeof(union condition_tree_union));
  ct->condition = condition;

  p->is_condition = 1;
  p->condition_tree_union = ct;
  return p;
}

ConditionTree *make_boolean_condition_tree(BooleanCondition *boolean_condition) {
  ConditionTree *p = must_malloc(sizeof(ConditionTree));
  union condition_tree_union *ct = must_malloc(sizeof(union condition_tree_union));
  ct->boolean_condition = boolean_condition;

  p->is_condition = 0;
  p->condition_tree_union = ct;
  return p;
}


// ======================================================================
//                     S E M A N T I C   C H E C K S
// ======================================================================

static void check_actions(Brick *brick_list, Action *list) {
  for (Action *current = list; current; current = current->next) {
    // Verify that the variable used in this action is declared
    if (! find_brick(current->var_name, brick_list))
      error_msg(list->lineno, "undeclared '%s'", current->var_name);
  }
}

static void check_transition(Brick *brick_list, State *state_list, Transition *trans){
  // Verify that the variable is declared
  if (! find_brick(trans->var_name, brick_list))
    error_msg(trans->lineno, "undeclared '%s'", trans->var_name);
  // Verify that the next state exists
  if (! find_state(trans->newstate, state_list))
    error_msg(trans->lineno, "undeclared state '%s'", trans->newstate);
}


static void check_states(Brick *brick_list, State *state_list) {
  for (State *current = state_list; current; current = current->next) {
    check_actions(brick_list, current->actions);
    check_transition(brick_list, state_list, current->transition);
    if (find_state(current->name, current->next))
      error_msg(current->lineno, "duplicate state name: '%s'", current->name);
  }
}


// ======================================================================
//                      C O D E   P R O D U C T I O N
// ======================================================================
static void emit_header(char *appname) {
  printf("// File generated by ArduinoML (C/yacc/lex) for %s\n"
         "long time = 0;\n"
         "long debounce = 200;\n\n",
         appname);
}

static void emit_bricks(Brick *lst) {
  // Produce variables
  for (Brick *p = lst; p; p = p->next) {
    printf("int %s = %d;\n", p->var, p->port_number);
  }

  // Produce setup() function
  printf("\nvoid setup() {\n");
  for (Brick *p = lst; p; p = p->next) {
    printf("  pinMode(%s, %s);\n", p->var, (p->kind == sensor)? "INPUT": "OUTPUT");
  }
  printf("}\n\n");
}

static void emit_actions(Action *list) {
  for (Action *p = list; p; p = p->next)
    printf("  digitalWrite(%s, %s);\n", p->var_name, p->sig_value ? "HIGH": "LOW");
}

static void emit_transition(char *current_state, Transition *transition) {
  printf("  boolean guard =  millis() - time > debounce;\n");
  printf("  if (digitalRead(%s) == %s && guard) {\n", transition->var_name,
         transition->sig_value? "HIGH": "LOW");
  printf("    time = millis();\n");
  printf("    state_%s();\n", transition->newstate);
  printf("  } else {\n");
  printf("    state_%s();\n", current_state);
  printf("  }\n");
}

static void emit_states(State *list) {
  for (State *p = list; p; p = p->next) {
    printf("void state_%s() {\n", p->name);
    emit_actions(p->actions);
    emit_transition(p->name, p->transition);
    printf("}\n\n");
  }
}

static void emit_loop(void) {
  printf("void loop() {\n  state_%s();\n}\n", initial_state->name);
}


/// emit the code for the parsed configuration
void emit_code(char *appname, Brick *brick_list, State *state_list) {
  check_states(brick_list, state_list);
  if (! initial_state)
    error_msg(yylineno, "no initial state declared");

  if (error_detected) {
    fprintf(stderr, "**** %d error%s\n", error_detected,
                    (error_detected>1) ? "s": "");
    return;
  }

  // No error ⇒ produce code
  emit_header(appname);
  emit_bricks(brick_list);
  emit_states(state_list);
  emit_loop();
}


// ======================================================================
//                               U T I L S
// ======================================================================

/// Display error message using the GNU conventions
void error_msg(int lineno, const char *format, ...) {
  va_list ap;

  if (input_path) fprintf(stderr, "%s:", input_path);
  fprintf(stderr, "%d: ", lineno);
  va_start(ap, format);  vfprintf(stderr, format, ap);  va_end(ap);
  fprintf(stderr, "\n");

  error_detected += 1;
}

// Allocate memory and die if not possible
static void *__must_malloc(size_t sz, const char *func, const char *file, int line) {
  void *res = malloc(sz);
  if (!res) {
    fprintf(stderr, "**** function '%s' cannot allocate memory (in file '%s':%d)\n",
            func, file, line);
    fprintf(stderr, "Abort.\n");
    exit(1);
  }
  return res;
}

// Create a new unique constant name
static char *new_const_name(void) {
  static int counter = 0;
  char *name = must_malloc(10);
  sprintf(name, "const_%d", counter++);
  return name;
}
