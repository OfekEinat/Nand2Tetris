import java.util.ArrayList;


public class CompileEngine {
    private JackTokenizer tokenizer;
    private VMWriter vmWriter;
    private SymbolTable symbolTable;
    private String curClassName;//holds the class name
    private String curSubroutineName;//holds the current scope name.
    private int labelsCounter; //uses to create uniqness between diffrent labels

    private final String CLASS = "class";
    private final String LOCAL = "local";
    private final String STATIC = "static";
    private final String FIELD = "field";
    private final String METHOD = "method";
    private final String FUNCTION = "function";
    private final String CONSTRUCTOR = "constructor";
    private final String ARGUMENT = "argument";
    private final String POINTER = "pointer";
    private final String TEMP = "temp";
    private final String THAT = "that";
    private final String CONSTANT = "constant";
    private final String MEMORY_ALLOC_CALL = "Memory.alloc";
    private final String STRING_NEW_CALL = "String.new";
    private final String STRING_APPENDCHAR_CALL = "String.appendChar";
    private final String LET = "let";
    private final String DO = "do";
    private final String IF = "if";
    private final String WHILE = "while";
    private final String RETURN = "return";
    private final String ADD = "add";
    private final String NOT = "not";
    private final String SUB = "sub";
    private final String NEG = "neg";
    private final String MULT_2 = "call Math.multiply 2";
    private final String DIV_2 = "call Math.divide 2";
    private final String LT = "lt";
    private final String GT = "gt";
    private final String EQ = "eq";
    private final String AND = "and";
    private final String OR = "or";
    private final String ELSE = "else";
    private final String TRUE = "true";

    private final String INVALID_STATEMENT = "expected if/let/do/while/ return";
    private final String EXPECTED_SYMBOL   = "expected symbol:";

    private final String START_LOOP = "Loop_Start";
    private final String END_LOOP = "Loop_End";
    private final String LABEL_END = "If_True";
    private final String LABEL_ELSE = "If_False";

    private final char EQUAL = '=';
    private final char SEMICOLON = ';';
    private final char COMMA = ',';
    private final char DOT = '.';
    private final char OPENING_PARENTHESIS = '(';
    private final char CLOSING_PARENTHESIS = ')';
    private final char OPENING_CURLY_BRACKETS = '{';
    private final char CLOSING_CURLY_BRACKETS = '}';
    private final char OPENING_SQUARE_BRACKETS = '[';
    private final char CLOSING_SQUARE_BRACKETS = ']';

    private static ArrayList<String> operations = new ArrayList<>();
    static{
        operations.add("+");
        operations.add("-");
        operations.add("*");
        operations.add("/");
        operations.add("&");
        operations.add("|");
        operations.add("~");
        operations.add("+");
        operations.add("<");
        operations.add("=");
        operations.add(">");
        
    }
    public static ArrayList<String> keyWord_Terms = new ArrayList<>();
    static{
        keyWord_Terms.add("this");
        keyWord_Terms.add("null");
        keyWord_Terms.add("true");
        keyWord_Terms.add("false");
    }
  
    // create a new compile engine object
    public CompileEngine(String sinput, String soutput){
        try{
            //initilizes a tokenizer with the input.
            tokenizer = new JackTokenizer(sinput);
            //initilizes a symbol table
            symbolTable = new SymbolTable();
            //initilizes a VM writer that writed to the output.
            vmWriter = new VMWriter(soutput);
            curClassName = "";
            curSubroutineName = "";
            labelsCounter = 0 ;
        }
        catch(Exception e){
            System.err.println(e);
        }
    }

    //compiles a complete class
    public void compileClass(){
        checkKeyWord(CLASS);
        curClassName = tokenizer.identifier();
        tokenizer.advance();
        checkSymbol(OPENING_CURLY_BRACKETS);    
        compileClassVarDec();
        compileSubroutine();
        checkSymbol(CLOSING_CURLY_BRACKETS);
        vmWriter.close();
    }
    
    //handles any amount of fields or static fields declerations seperated in the same type with , and in different types with ';'.
    public void compileClassVarDec(){
        while(isStaticOrField()){
            addVarsToSymbolTable();
            checkSymbol(SEMICOLON);        
        }
    }
    
    //adds a full line of variables to the symbol table.
    private void addVarsToSymbolTable() {
        Variable.KINDTYPE k = getKind(tokenizer.Keyword());
        tokenizer.advance();
        String type = getKeywordOrIdentifier();
        tokenizer.advance();
        symbolTable.add(tokenizer.identifier(), type, k);
        tokenizer.advance();
        //adding all the variables seperated by ','.
        while (isComma()) {
            tokenizer.advance();
            symbolTable.add(tokenizer.identifier(), type, k);
            tokenizer.advance();
        }
    }

    //handles one or more subrutines
    public void compileSubroutine(){
        String subRoutineType;
        while(isSubroutine()){
            subRoutineType = tokenizer.Keyword();
            symbolTable.clear_Subrutine_Scope();
            if(subRoutineType.equals(METHOD)){
                symbolTable.add(VMWriter.THIS, curClassName, Variable.KINDTYPE.ARG);
            }
            tokenizer.advance(2);
            curSubroutineName = tokenizer.identifier();
            tokenizer.advance();
            checkSymbol(OPENING_PARENTHESIS);
            compileParameterList();
            checkSymbol(CLOSING_PARENTHESIS);
            compileSubroutineBody(subRoutineType);
       } 
    }

    //handles the body of the current subrutine
    private void compileSubroutineBody(String subRoutineType) {
        //expects a '{' followed by zero or more var declerations and zero or more statements closed with '}'
        checkSymbol(OPENING_CURLY_BRACKETS);
        compileVarDec();
        vmWriter.writeFunction(curClassName + DOT + curSubroutineName, symbolTable.varCount(Variable.KINDTYPE.VAR));
        if (subRoutineType.equals(METHOD)) {
            vmWriter.writePush(ARGUMENT, 0);
            vmWriter.writePop(POINTER, 0);

        } else if (subRoutineType.equals(CONSTRUCTOR)) {
            vmWriter.writePush(CONSTANT, symbolTable.varCount(Variable.KINDTYPE.FIELD));
            vmWriter.writeCall(MEMORY_ALLOC_CALL, 1);
            vmWriter.writePop(POINTER, 0);
        }
        compileStatements();
        checkSymbol(CLOSING_CURLY_BRACKETS);
    }
    
    //handles a list of parameters
    public void compileParameterList(){
        String pType = "";
        //as long as the list didn't end
        while(!isSymbol(CLOSING_PARENTHESIS)){
            pType = getKeywordOrIdentifier();
            tokenizer.advance();
            symbolTable.add(tokenizer.identifier(), pType, Variable.KINDTYPE.ARG);
            tokenizer.advance();
            if(isSymbol(',')){
                tokenizer.advance();
            }
        }
    }

    //handles any amount of var declerations seperated in the same type with , and in different types with ';'.
    public void compileVarDec() {
        if(!isVar()){
            return;
        }
        while(isVar()){
            addVarsToSymbolTable();
            checkSymbol(SEMICOLON);        
        }
    }
    
    //handles a statments block '{' one or more statements ends with '}'
    public void compileStatements(){
        //as long as we didn't reach to the end of the block.
        while(!isSymbol(CLOSING_CURLY_BRACKETS)){
            //compiles the current statement if it is not a valid one raises an exception
            switch (tokenizer.Keyword()) {
                case WHILE:
                    tokenizer.advance();
                    compileWhile();
                break;
                case DO:
                    tokenizer.advance();
                    compileDo();
                break;
                case LET:
                    tokenizer.advance();
                    compileLet();
                break;
                case IF:
                    tokenizer.advance();
                    compileIf();
                break;
                case RETURN:
                    tokenizer.advance();
                    compileReturn();
                break;
                default:
                throw new IllegalArgumentException(INVALID_STATEMENT);
            }
        }
        }

    //handles if statement
    public void compileIf(){
        //expects the following order
        String endCondition = LABEL_END + labelsCounter;
        String elseLabel = LABEL_ELSE + labelsCounter;
        labelsCounter++;
        checkSymbol(OPENING_PARENTHESIS);
        compileExperssion();
        checkSymbol(CLOSING_PARENTHESIS);
        vmWriter.writeArithmetic(NOT);
        vmWriter.writeIf(elseLabel);
        checkSymbol(OPENING_CURLY_BRACKETS);
        compileStatements();
        checkSymbol(CLOSING_CURLY_BRACKETS);
        vmWriter.writeGoto(endCondition);
        vmWriter.writeLabel(elseLabel);
        //optinally one else
        if(isElse()){
            tokenizer.advance();
            checkSymbol(OPENING_CURLY_BRACKETS);
            compileStatements();
            checkSymbol(CLOSING_CURLY_BRACKETS);
        }
        vmWriter.writeLabel(endCondition);
    }

    //handles let statement
    public void compileLet(){
        String varName = tokenizer.identifier();
        tokenizer.advance();
        boolean varIsArray = false;
        //checks if the variable is an array and calculate it's address
        if(isSymbol(OPENING_SQUARE_BRACKETS)){
            varIsArray = true;
            checkSymbol(OPENING_SQUARE_BRACKETS);
            compileExperssion();
            vmWriter.writePush(getSegment(varName), getIndex(varName));
            vmWriter.writeArithmetic(ADD);
            checkSymbol(CLOSING_SQUARE_BRACKETS);
        }
        //expects '='
        checkSymbol(EQUAL);
        //followed by expression.
        compileExperssion();
        if (varIsArray) {
            vmWriter.writePop(TEMP, 0);
            vmWriter.writePop(POINTER, 1);
            vmWriter.writePush(TEMP, 0);
            vmWriter.writePop(THAT, 0);
        } else {
            vmWriter.writePop(getSegment(varName), getIndex(varName));
        }
        //ends with ';'.
        checkSymbol(SEMICOLON);
    }

    //handles while statement
    public void compileWhile(){
        String beginLoop = START_LOOP + labelsCounter;
        String endLoop = END_LOOP + labelsCounter;
        labelsCounter++;
        vmWriter.writeLabel(beginLoop);
        checkSymbol(OPENING_PARENTHESIS);
        compileExperssion();
        checkSymbol(CLOSING_PARENTHESIS);
        vmWriter.writeArithmetic(NOT);
        vmWriter.writeIf(endLoop);
        checkSymbol(OPENING_CURLY_BRACKETS);
        compileStatements();
        checkSymbol(CLOSING_CURLY_BRACKETS);
        vmWriter.writeGoto(beginLoop);
        vmWriter.writeLabel(endLoop);
    }

    //handles do statement   
    public void compileDo(){
        //expects a function call.
        compileFunctionCall();
        //ends with ';'.
        checkSymbol(SEMICOLON);
        vmWriter.writePop(TEMP, 0);
    }

    //handles a function call
    public void compileFunctionCall(){
        // function or object
        String first = getKeywordOrIdentifier();
        //optinally .function
        String second;
        int countArgs = 0;
        tokenizer.advance();
        //expects to see either '.' followed by identifier
        if(isDot()){
            tokenizer.advance();
            second = tokenizer.identifier();
            Variable.KINDTYPE t = symbolTable.kindOf(first);
            if (t == Variable.KINDTYPE.NOTEXIST) {
                first = first + DOT + second;
            } else {
                vmWriter.writePush(getSegment(first), getIndex(first));
                countArgs++;
                first = symbolTable.typeOf(first) + DOT + second;
            }
            tokenizer.advance();
            checkSymbol(OPENING_PARENTHESIS);
            countArgs += compileExpressionList();
            checkSymbol(CLOSING_PARENTHESIS);
            vmWriter.writeCall(first, countArgs);
        }
        //or '('
        else {
            countArgs++;
            checkSymbol(OPENING_PARENTHESIS);
            vmWriter.writePush(POINTER, 0);
            countArgs += compileExpressionList();
            checkSymbol(CLOSING_PARENTHESIS);
            vmWriter.writeCall(curClassName + DOT + first, countArgs);
        }
    }

    //returns the segment which the var needs to be push to 
    private String getSegment(String varName) {
        Variable.KINDTYPE kind = symbolTable.kindOf(varName);
        switch (kind) {
            case ARG:
                return ARGUMENT;
            case FIELD:
                return FIELD;
            case VAR:
                return LOCAL;
            case STATIC:
                return STATIC;
            default:
                runTimeException("Undefined object");
                return "";
        }        
    }

    //handles a return statement
    public void compileReturn(){
        //if it is not followed by a ';' expects an expression.
        if(!isSemiColon()){
            compileExperssion();
        }
        else{
            vmWriter.writePush(CONSTANT, 0);
        }
        //followed by a ';'.
        checkSymbol(SEMICOLON);
        vmWriter.writeReturn();
        }
    
    //handles an expression
    public void compileExperssion(){
        compileTerm();
        if (isOp()) {
            String op = getCommandRepresentaion();
            tokenizer.advance();
            compileTerm();
            vmWriter.writeArithmetic(op);
        }
    }

    //returns the vm string representation of the current opertator.  
    private String getCommandRepresentaion() {
        switch (tokenizer.symbol()) {
            case '+':
                return ADD;
            case '-':
                return SUB;
            case '*':
                return MULT_2;
            case '/':
                return DIV_2;
            case '<':
                return LT;
            case '>':
                return GT;
            case '=':
                return EQ;
            case '&':
                return AND;
            case '|':
                return OR;
            case '~':
                return NOT;
            default:
                runTimeException("Not valid operation!");
        }
        return null;
    }
    
    //handles a potentially start of a list of expressions seperated with ','.
    private int compileExpressionList(){
        int expCounter = 0;
            while(!isSymbol(CLOSING_PARENTHESIS)){
                expCounter++;
                compileExperssion();
                while(isComma()){
                    checkSymbol(COMMA);
                    expCounter++;
                    compileExperssion();
                }    
        }    
        return expCounter;
    }

    //hendles a generic term if it is not a valid term raises an exception.
    public void compileTerm(){
        switch (tokenizer.TokenType()) {
            case JackTokenizer.IDENTIFIER:
                compileIdentifierTerm();
                break;
            case JackTokenizer.INT_CONST:
                compileIntTerm();
                break;
            case JackTokenizer.STRING_CONST:
                compileStringTerm();
                break;
            case JackTokenizer.KEYWORD:
                compileKeywordTerm();
                break;
            case JackTokenizer.SYMBOL:
                compileSymbolTerm();
                break;
            default:
                runTimeException("Illegal term!");
        }
    }

    //handles an identifier term.
    public void compileIdentifierTerm(){
        String currentToken = tokenizer.identifier();
        tokenizer.advance();        
        //if the next token symbol is '[' expects to see an expression followed by ']'.
        if(isSymbol(OPENING_SQUARE_BRACKETS)){
            vmWriter.writePush(getSegment(currentToken), getIndex(currentToken));
            checkSymbol(OPENING_SQUARE_BRACKETS);
            compileExperssion();
            checkSymbol(CLOSING_SQUARE_BRACKETS);
            vmWriter.writeArithmetic(ADD);
            vmWriter.writePop(POINTER, 1);
            vmWriter.writePush(THAT, 0);
        }
        //else if the next token is . or ( than it is a function call
        else{
            if(isDot()|| isSymbol(OPENING_PARENTHESIS)){
                tokenizer.movePointerBack();
                compileFunctionCall();
            }
            else{
                vmWriter.writePush(getSegment(currentToken), getIndex(currentToken));
            }
  
        }
    }

    //handles a keyword term and advance the tokenizer.
    public void compileKeywordTerm(){
        switch (tokenizer.Keyword()) {
            case VMWriter.THIS:
                vmWriter.writePush(POINTER, 0);
                break;
            case TRUE:
                vmWriter.writePush(CONSTANT, 0);
                vmWriter.writeArithmetic(NOT);
                break;
            default:
                vmWriter.writePush(CONSTANT, 0);
                break;
        }
        tokenizer.advance();
    }

    //handles a String term.
    public void compileSymbolTerm(){
        //if the symbol is '(' expects to see an expression followed by ')'.
        if(tokenizer.symbol() == OPENING_PARENTHESIS){
            checkSymbol(OPENING_PARENTHESIS);
            compileExperssion();
            checkSymbol(CLOSING_PARENTHESIS);
            return;
        }
        //if the symbol is unary op compiles the full unaryOp-term
        else if(IsunarySymbol()){
            String op = getCommandRepresentaion();
            if(op.equals(SUB)){
                op = NEG;
            }
            tokenizer.advance();
            compileTerm();
            vmWriter.writeArithmetic(op);
        }
        //if it is not both of the above adavance the tokenizer.
        else{
        tokenizer.advance();
        }
    }

    //handles a String term and advance the tokenizer. 
    public void compileStringTerm(){
        String currentToken = tokenizer.stringValue();
        vmWriter.writePush(CONSTANT, currentToken.length());
        vmWriter.writeCall(STRING_NEW_CALL, 1);
        //using String.appendChar method on each char in the current string to create the string in the vm.
        for (int i = 0; i < currentToken.length(); i++) {
            vmWriter.writePush(CONSTANT, (int)currentToken.charAt(i));
            vmWriter.writeCall(STRING_APPENDCHAR_CALL, 2);
        }
        tokenizer.advance();
    }

    //handles an integer term and advance the tokenizer.
    public void compileIntTerm(){
        vmWriter.writePush(CONSTANT, tokenizer.intValue());
        tokenizer.advance();
    }

    //throws run time exception with the given error
    public static void runTimeException(String error) {
        throw new RuntimeException(error);
    }

    //converts from string to Variable.KINDTYPE (enum).
    private Variable.KINDTYPE getKind(String keyword) {
        switch (keyword) {
            case FIELD:
                return Variable.KINDTYPE.FIELD;
            case STATIC:
                return Variable.KINDTYPE.STATIC;
            case VMWriter.VAR:
                return Variable.KINDTYPE.VAR;
            default:
                return Variable.KINDTYPE.NOTEXIST;
        }
    }

    //return the index of the given variable.
    private int getIndex(String varName) {
        return symbolTable.indexOf(varName);
    }

    //checks if the current token is static or field.
    private boolean isStaticOrField() {
        return (tokenizer.Keyword().equals(STATIC)|| tokenizer.Keyword().equals(FIELD));
    }

    //returns the current toke , if it isn't a keyword or identifier raises exception.
    private String getKeywordOrIdentifier() {
        if(tokenizer.TokenType() == JackTokenizer.KEYWORD){
            return tokenizer.getcurrentToken();
        }
        else{
            return tokenizer.identifier();
        }
    }

    //checks if the current token is unaryOp symbol.
    private boolean IsunarySymbol(){
        return  (tokenizer.getcurrentToken().equals("-") || tokenizer.getcurrentToken().equals("~"));
    }

    //checks if the given symbol equals the current token and advances the tokenizer if it is not a symbol or not equal raises an exception.
    private void checkSymbol(char symbol){
        if((tokenizer.symbol() == symbol) &&(tokenizer.TokenType() == JackTokenizer.SYMBOL)){
            if(tokenizer.HasMoreTokens()){
                tokenizer.advance();
            }
        }
        else{
            runTimeException(EXPECTED_SYMBOL + symbol);
        }
    }

    //checks if the given keyword equals the current token.
    private void checkKeyWord(String keyWord) {
        if(tokenizer.Keyword().equals(keyWord)){
            tokenizer.advance();    
            return;
        }
        runTimeException("expected KeyWord:" + keyWord);
    }

    // checks if the current token is one of the subrutines declerations. 
    private boolean isSubroutine(){
        try {
            return (tokenizer.Keyword().equals(CONSTRUCTOR) || tokenizer.Keyword().equals(FUNCTION) || tokenizer.Keyword().equals(METHOD));
        } catch (Exception e) {
            return false;
        }
    }

    //checks if the current token is the supplied symbol.
    private boolean isSymbol(char symbol){
        return (tokenizer.TokenType().equals(JackTokenizer.SYMBOL)) && (tokenizer.symbol() == symbol);
    } 
    
    //checks if the current token is ';'
    private boolean isSemiColon(){
        return tokenizer.TokenType().equals(JackTokenizer.SYMBOL) && tokenizer.symbol() == SEMICOLON;
    }

    //checks if the current token is 'else'
    private boolean isElse(){
        return (tokenizer.TokenType() == JackTokenizer.KEYWORD) && (tokenizer.Keyword().equals(ELSE));
    }

    //checks if the current token is '.'
    private boolean isDot(){
        return tokenizer.TokenType().equals(JackTokenizer.SYMBOL)&& tokenizer.symbol() == DOT;
    }

    //checks if the current token is 'var'
    private boolean isVar(){
        return tokenizer.TokenType().equals(JackTokenizer.KEYWORD) && tokenizer.Keyword().equals(VMWriter.VAR);
    }

    //checks if the current token is ','
    private boolean isComma(){
        return tokenizer.symbol() == COMMA;
    }

    //checks if the current token is an operation    
    private boolean isOp() {
        return operations.contains(tokenizer.getcurrentToken());
    }
    public static void main(String[] args) {
    //CompileEngine ce = new CompileEngine("Square.jack", "Square.vm");
    //ce.compileClass();
    }
    }





