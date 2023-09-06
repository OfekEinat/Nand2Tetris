import java.util.HashMap;

public class SymbolTable {

    private HashMap<String, Variable> class_Scope; //holds the class variables
    private HashMap<String, Variable> subroutine_Scope; //holds the current scope variables.
    private HashMap<Variable.KINDTYPE, Integer> kind_Counter; //for each kind stores the amount of variables.

    public SymbolTable() {
        //initializes an empty class scope table
        class_Scope = new HashMap<>();
        //initializes an empty subrutine scope table
        subroutine_Scope = new HashMap<>();
        //initializes an the kind counters with 0;
        kind_Counter = new HashMap<>();
        kind_Counter.put(Variable.KINDTYPE.ARG, 0);
        kind_Counter.put(Variable.KINDTYPE.STATIC, 0);
        kind_Counter.put(Variable.KINDTYPE.FIELD, 0);
        kind_Counter.put(Variable.KINDTYPE.VAR, 0);
    }

    //resets the local subroutines vars and their counters
    //uses when we finished with a subrutine
    public void clear_Subrutine_Scope() {
        subroutine_Scope.clear();
        kind_Counter.put(Variable.KINDTYPE.VAR, 0);
        kind_Counter.put(Variable.KINDTYPE.ARG, 0);
    }

    // adds a new variable to the symbol table
    public void add(String n, String t, Variable.KINDTYPE k) {
        int i = kind_Counter.get(k);
        //updates the counter of the new Variable's kind.
        kind_Counter.put(k, i + 1);
        Variable newVar = new Variable(t, k, i);
        if (isClass_Scope(k)) {
            class_Scope.put(n, newVar);
        } else if (isSubRoutine_Scope(k)) {
            subroutine_Scope.put(n, newVar);
        }
    }

    // Checks if the var is a var or arg kind meaning its in a subrutine scope
    private boolean isSubRoutine_Scope(Variable.KINDTYPE k) {
        return (k == Variable.KINDTYPE.VAR || k == Variable.KINDTYPE.ARG);
    }

    // Checks if the var is a field or static kind meaning its in a class scope
    private boolean isClass_Scope(Variable.KINDTYPE k) {
        return (k == Variable.KINDTYPE.FIELD || k == Variable.KINDTYPE.STATIC);
    }

    // Returns the amount of variables of a given kind
    public int varCount(Variable.KINDTYPE kind) {
        return kind_Counter.get(kind);
    }

    // Returns the kind of the given var
    // If the var wasnt declared yet, return NOT_EXIST
    public Variable.KINDTYPE kindOf(String name) {
        Variable var = subroutine_Scope.get(name);
        if (var == null) {
            var = class_Scope.get(name);
        }
        if (var != null) {
            return var.getKind();
        }
        return Variable.KINDTYPE.NOTEXIST;
    }

    // Returns the type of the named variable
    // If the var wasnt declared yet, return empty string.
    public String typeOf(String n) {
        Variable var = subroutine_Scope.get(n);
        if (var == null) {
            var = class_Scope.get(n);
        }
        if (var != null) {
            return var.getType();
        }
        return "";
    }

    // Returns the index of the given variable if it was
    // If the var wasnt declared yet, return -1
    public int indexOf(String n) {
        Variable var = subroutine_Scope.get(n);
        if (var == null) {
            var = class_Scope.get(n);
        }
        if (var != null) {
            return var.getIndex();
        }
        return -1;
    }

}
