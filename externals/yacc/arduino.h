/*
 * arduino.h     -- ArduinoML configuration and code generation
 *
 *           Author: Erick Gallesio [eg@unice.fr]
 *    Creation date: 17-Nov-2017 11:13
 * Last file update: 28-Nov-2017 11:47 (eg)
 */

#ifndef ARDUINO_H
#define ARDUINO_H

enum binary_op { LT, LEQ, EQ, NEQ, GEQ, GT, AND, OR };

enum port_assignment { sensor, actuator };
extern char *input_path;      ///< Name of the input path or NULL if stdin

/// Display error message using the GNU conventions
void error_msg(int lineno, const char *format, ...);

//
// ========== BRICKS ==========
//
typedef struct arduino_brick Brick;

/// Declare a new brick on port `number`
Brick *make_brick(int number, enum port_assignment kind, char *name);
/// Add a brick to a list of bricks
Brick *add_brick(Brick *b, Brick *list);

//
// ========== TRANSITIONS ==========
//
typedef struct arduino_transition Transition;

/// Make a new transition (when `var` is `signal` goto `newstate`
Transition *make_transition(char *var, int signal, char *newstate);

//
// ========== CONSTANTS ==========
//
typedef struct arduino_constant Constant;

// Make a new constant named `name` with value `value`
Constant *make_constant(char *name, float value);
// Add a constant to a list of constants
Constant *add_constant(Constant *list, Constant *c);

//
// ========== ACTIONS ==========
//
union value {
  int signal_value;
  float float_value;
  char *var_value;
};

typedef struct arduino_action Action;

enum const_type { signal, value, var };

Action *make_signal_action(char *var, enum const_type type, int signal);
Action *make_float_action(char *var, enum const_type type, float float_value);
Action *make_var_action(char *var, enum const_type type, char *var_value);
Action *make_action(char *var, enum const_type type, union value *value);

// Add an action to a list of actions
Action *add_action(Action *list, Action *a);


//
// ========== STATES ==========
//
typedef struct arduino_state State;

// Make a new state named `var` with a list of `actions` and a `transition`
// `initial` must be one if the state is the initial one
State *make_state(char *var, Action *actions, Transition *transition, int initial);
// Add a state to a list of states
State *add_state(State *list, State *a);

//
// ========== CONDITION ==========
//

union condition_tree_union {
    struct arduino_condition *condition;
    struct arduino_boolean_condition *boolean_condition;
};

typedef struct arduino_condition Condition;
typedef struct arduino_boolean_condition BooleanCondition;
typedef struct arduino_condition_tree ConditionTree;

Condition *make_condition(char *var, enum const_type type, union value *value, enum binary_op op);
Condition *make_signal_condition(char *var, int signal, enum binary_op op);
Condition *make_float_condition(char *var, float float_value, enum binary_op op);
Condition *make_var_condition(char *var, char *var_value, enum binary_op op);
BooleanCondition *make_boolean_condition(enum binary_op op, struct arduino_condition_tree *left, struct arduino_condition_tree *right);
ConditionTree *make_condition_tree(Condition *condition);
ConditionTree *make_boolean_condition_tree(BooleanCondition *boolean_condition);

//
// ========== CODE PRODUCTION ==========
//

/// emit the code for the parsed configuration
void emit_code(char *appname, Brick *brick_list, State *state_list);

//
// ========== UTILS ==========
//

// Create a new unique constant name
static char *new_const_name(void)

#endif // ARDUINO_H
