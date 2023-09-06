    import java.io.File;
    import java.util.ArrayList;
    import java.util.Scanner;


public class JackTokenizer {
    private static final String LINE_COMMENT_OPPENER = "//";
    private static final String BLOCK_COMMENT_OPENER = "/*";
    private static final String BLOCK_COMMENT_CLOSER = "*/";
    public static final String KEYWORD = "KEYWORD";
    public static final String SYMBOL = "SYMBOL";
    public static final String INT_CONST = "INT_CONST";
    public static final String STRING_CONST = "STRING_CONST";
    public static final String IDENTIFIER = "IDENTIFIER";
    private static final  ArrayList<String> keyWords;
    static{
        keyWords = new ArrayList<>();
        keyWords.add("class");
        keyWords.add("constructor");
        keyWords.add("function");
        keyWords.add("method");
        keyWords.add("field");
        keyWords.add("static");
        keyWords.add("var");
        keyWords.add("int");
        keyWords.add("char");
        keyWords.add("boolean");
        keyWords.add("void");
        keyWords.add("true");
        keyWords.add("false");
        keyWords.add("null");
        keyWords.add("this");
        keyWords.add("let");
        keyWords.add("do");
        keyWords.add("if");
        keyWords.add("else");
        keyWords.add("while");
        keyWords.add("return");
    }
    private final static ArrayList<Character> symbols;
    static {
        symbols = new ArrayList<>();
        symbols.add('(');
        symbols.add(')');
        symbols.add('[');
        symbols.add(']');
        symbols.add('{');
        symbols.add('}');
        symbols.add(',');
        symbols.add('=');
        symbols.add('.');
        symbols.add('+');
        symbols.add('-');
        symbols.add('/');
        symbols.add('*');
        symbols.add('&');
        symbols.add('|');
        symbols.add('~');
        symbols.add('<');
        symbols.add('>');
        symbols.add(';');
        }
    private ArrayList<String> Tokens;
    private String currentToken;
    private int pointer; //the index of the current token
    private Scanner reader;
    private File source;
    private boolean notesblock; //indicates if we are in the middle of a block of comments.

    //constructs the tokenizer with all the tokens from the supplied file
    public JackTokenizer(String path){
        String line, parsedline;
        source = new File(path);
        this.pointer = 0;
        this.Tokens = new ArrayList<>();
        try{
            notesblock = false;
            this.reader = new Scanner(source);
            //reads the lines and parses them into tokens than add the tokens to the array "Tokens".
            while(reader.hasNextLine()){
                line = reader.nextLine();
                line = line.trim();
                if(validLine(line)){
                    parsedline = parseLine(line);
                    Tokens.addAll(checkLine(parsedline));
                }
            }
            // initilizes the current token field with the first token
            this.currentToken = Tokens.get(0);  
        }
        catch(Exception e){
            System.err.println(e);
        }  
    }

    // parses the line's content from it's notes.
    private String parseLine(String line) {
        String parsedline = line;
        if(Validwithnotes(line)){
            parsedline = removenotes(line);
        }
        return parsedline;
    }

    //checks if the line is valid (not empty & not in the middle of a notes block)
    private boolean validLine(String line){
        if(line.length()== 0){return false;}
        //if a new block of notes is starting update notesblock field.
        if(line.startsWith(BLOCK_COMMENT_OPENER)){notesblock = true;}
        if(notesblock){
            notesblock = notesblockEnd(line); 
            return false;
        }
        return true;
    }

    //checks if the line contains notes that ends in the same line.
    public boolean linernotes(String line){
        return (line.contains(LINE_COMMENT_OPPENER)||((line.startsWith(BLOCK_COMMENT_OPENER))&&(line.endsWith(BLOCK_COMMENT_CLOSER))));
    }

    //checks if the line ends the current notes block.
    private boolean notesblockEnd(String line){
        return !(line.endsWith(BLOCK_COMMENT_CLOSER));
    }

    //checks if the line contains content except for its notes.
    private boolean Validwithnotes(String line){
        return (line.contains(LINE_COMMENT_OPPENER)||line.contains(BLOCK_COMMENT_OPENER));
    }

    //checks if there are more tokens if so advances the pointer one index forward.
    public void advance(){
        if(HasMoreTokens()){
            pointer++;
            currentToken = Tokens.get(pointer);
        }
        else{
        throw new IllegalThreadStateException("No more tokens");
        }
    }
    public void advance(int i){
        for (int j = 0; j < i; j++) {
            if(HasMoreTokens()){
                pointer++;
                currentToken = Tokens.get(pointer);
            }
            else{
            throw new IllegalThreadStateException("No more tokens");
            }
        }

    }

    //returns the current token's type.
    public String TokenType(){
        String tokenType;
        try {
            Integer.parseInt(getcurrentToken());
            tokenType = INT_CONST;
        } catch (Exception e) {
            if(keyWords.contains(getcurrentToken())){tokenType = KEYWORD;}    
            else if(symbols.contains(getcurrentToken().charAt(0))){tokenType = SYMBOL;}
            else if(getcurrentToken().startsWith("\"") && getcurrentToken().endsWith("\"")){tokenType = STRING_CONST;}    
            else tokenType = IDENTIFIER;
        }
        
        return tokenType;   
    }
    
    //returns the current token.
    public String getcurrentToken(){
        return currentToken;
    }

    //returns the current token and checks if it's a Keyword if not raises an exception.
    public String Keyword(){
        if(TokenType().equals(KEYWORD)){
            return getcurrentToken();
        }
        else {
            throw new IllegalStateException("Expected a keyword!");
        }
    }

    //returns the current token and checks if it's a symbol if not raises an exception.
    public char symbol(){
        if(TokenType().equals(SYMBOL)){
            return getcurrentToken().charAt(0);
        }
        else{
            throw new IllegalStateException("Expected Symbol!");
        }
    }

    //returns the current token and checks if it's an identifier if not raises an exception.
    public String identifier(){
        if (TokenType().equals(IDENTIFIER)){
            return getcurrentToken();
        }
            throw new IllegalStateException("Expected identifier!");
    }

    //returns the current token and checks if it's a stringval if not raises an exception.
    public String stringValue(){
        if(TokenType().equals(STRING_CONST)){
            return getcurrentToken().substring(1, getcurrentToken().length()-1);
        }
        CompileEngine.runTimeException("Expected String constant!");
        return "";
    }

    //returns the current token and checks if it's an int value if not raises an exception.
    public int intValue(){
        try {
            return Integer.parseInt(getcurrentToken());
        } catch (Exception e) {
            CompileEngine.runTimeException("Expected Integer constant!");
            return 0;
        }
    }

    //check if more tokens left
    public boolean HasMoreTokens(){
        return pointer < Tokens.size() - 1;
    }

    //deletes the current line's notes.
    public String removenotes(String line){
        int index;
        if(line.contains(LINE_COMMENT_OPPENER))
            index = line.indexOf(LINE_COMMENT_OPPENER); 
    else{
            index = line.indexOf(BLOCK_COMMENT_OPENER);
    }
    return line.substring(0, index);
    }

    /*used for test
    public void printTokensFromArrayList(ArrayList<String> Tokens) {
        for (String token : Tokens) {
            System.out.println(token + " -> ");
        }
    }
    public void printTokens() {
        for (String token : Tokens) {
            System.out.println(token + " -> ");
        }
    }
    */

    //parses the line to its tokens.
    private ArrayList<String> checkLine(String line){
        ArrayList<String> lineparts = new ArrayList<>();
        String[] splittedline = line.split("\"");
        for (int i = 0; i < splittedline.length; i++) {
            if (i % 2== 1) {
                lineparts.add(cover(splittedline[i]));
            } else {
                for (String word : splittedline[i].split(" ")) {
                    lineparts.addAll(checkWord(word));
                }
            }
        }
            return lineparts;
    }

    //parses words that contains more than one token.
    private ArrayList<String> checkWord(String word) {
        ArrayList<String> wordparts = new ArrayList<>();
        if (word.length()== 1) {
            wordparts.add(word);
        }
        else {
            String newWord = "";
            for (int i = 0; i < word.length(); i++) {
                Character letter = word.charAt(i);
                if (symbols.contains(letter)) {
                    if (!newWord.equals("")) {
                        wordparts.add(newWord);
                        newWord = "";
                    }
                    wordparts.add(letter.toString());
                } 
                else {
                    newWord += letter;
                }
            }
            if (!newWord.equals("")){wordparts.add(newWord);}
        }
        return wordparts;
    }

    //covers a string with quotation marks.
    private String cover(String string) {
            return "\"" + string + "\"";
        }

    //reduces the pointer by 1 and updating the currnet token field to the previous.
    public void movePointerBack() {
        if(pointer > 0){
            pointer--;
            currentToken = Tokens.get(pointer);
        }
        else{
            CompileEngine.runTimeException("cant go back from first token");
        }
    }

}




