public class Variable {

    // enum for the kinds of variables
    public static enum KINDTYPE {FIELD, STATIC, ARG, VAR, NOTEXIST};
    private String type; // ("int" , "boolean" etc.)
    private KINDTYPE kind;
    private int index;
    

    // create a new Variable and initializes its type kind and index.
    public Variable(String t, KINDTYPE k, int i) {
        type = t;
        kind = k;
        index = i;
    }

    // Returns the type
    public String getType() {
        return type;
    }

    // Returns the kind
    public KINDTYPE getKind() {
        return kind;
    }

    // Returns the index
    public int getIndex() {
        return index;
    }
}
