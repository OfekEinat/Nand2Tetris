import java.io.File;
import java.util.HashMap;
import java.util.Scanner;

public class parser {
    File source;
    Scanner reader , firstReader;

    //opens the file and set two readers one for the first read of lable declarations, and the second for the translation task.
    public parser(String str){
        source = new File(str);
        try {
            reader = new Scanner(source);   
            firstReader = new Scanner(source); 
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    //returns a table with all the user lable declarations.
    public HashMap<String,Integer> getLabels(){
        HashMap<String,Integer> lablesTable = new HashMap<>();
        String line;
        int lineIndex = 0;
        while (firstReader.hasNextLine()){
            line = firstReader.nextLine();
            line = removeWhiteSpacesAndNotes(line);
            if(line.length() != 0){
                if(!isLabel_Command(line)){
                    lineIndex++;
                }
                else{
                    line = line.substring(1, line.length()-1);
                    lablesTable.put(line, lineIndex);
                }
            }
            
        } 
        return lablesTable;
    }
    //remove all the white spaces and notes in line.
    public String removeWhiteSpacesAndNotes(String line){
        line = line.replaceAll("\\s", "");
        line = line.replaceFirst("//.*", "");
        return line;
    }

    //checks if a line is A-Command
    public static Boolean isA_Command(String command){
        if(command.charAt(0) == '@'){
            return true;
        }
        return false;
    }
    
    //checks if a line is Lable-Command(declaration)
    public static Boolean isLabel_Command(String command){
        if(command.charAt(0) == '('){
            return true;
        }
        return false;
    }

    //returns a String array in which the first cell holds command type and the second holds the address or symbol.
    public static String[] parseA_Command(String A_Command) {
        String[] parsedCommand = new String[2];
        parsedCommand[0] = "A";
        parsedCommand[1] = A_Command.substring(1);
        return parsedCommand;
    }

    //returns a String array in which the first cell holds command type and the next three are the dest , comp , jump values respectively.
    public static String[] parseC_Command(String line) {
        String[] parsedCommand = new String[4];
        int endIndexDest;
        int endIndexComp;
        //check if there is dest in the command.
        parsedCommand[0] = "C";
        if(!line.contains("=")){
            parsedCommand[1] = "null";            
        }

        else{
            // initializes the dest command in the corresponding cell in the array.
            endIndexDest = line.indexOf("=");
            parsedCommand[1] = line.substring(0 , endIndexDest);
            line = line.substring(endIndexDest + 1);
        }
        //check if there is a jump in the command.
        if(!line.contains(";")){
            parsedCommand[3] = "null";
            parsedCommand[2] = line;
        }
        
        else{
            // initializes the comp command and the jump command in the corresponding cells in the array.
            endIndexComp =  line.indexOf(";");
            parsedCommand[2] = line.substring(0 ,endIndexComp);
            line = line.substring(endIndexComp + 1);
            parsedCommand[3] = line;
        }
        return parsedCommand;
    }
  
    //return the first line contains an A/C instruction.
    public String readNextCommand(){
        String line;
        do{
            if (reader.hasNextLine()) {
                line = reader.nextLine();
                line = removeWhiteSpacesAndNotes(line);
            }
            else{
                return null;
            }
        } 
        while(line.length() == 0 || isLabel_Command(line));
        return line;
    }

    //return a String array representation as described in lines 63 , 71 .
    public static String[] parse(String line) {
        if (isA_Command(line)) {
            return parseA_Command(line);
        }
        else{
            return parseC_Command(line);
        }
    }
    //read the next line correspond to an A/C instruction and parse it. 
    public String[] parseNextCommand() {
        String line = readNextCommand();
        if(line == null){
            return null;
        }
        if (isA_Command(line)) {
            return parseA_Command(line);
        }
        else{
            return parseC_Command(line);
        }
    }

    public static void main(String[] args) {
    //* test 
     parser parser = new parser(".\\Pong.asm");
     HashMap<String,Integer> map = parser.getLabels();
     String command = parser.readNextCommand();
     String[] ParsedLine;
     while (null != command) {
        ParsedLine = parse(command);
        for (int i = 0; i < ParsedLine.length; i++) {
            System.out.print(ParsedLine[i] + " | ");
        
        }
        System.out.println();
        command = parser.readNextCommand();             
    }

}
}