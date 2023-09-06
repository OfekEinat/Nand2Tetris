
import java.io.File;
import java.util.Scanner;

public class Parser {
    File source;
    Scanner reader ;

    //opens the file and set it for the read task.
    public Parser(String str){
        source = new File(str);
        try {
            reader = new Scanner(source);   
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    //squeeze all the white spaces and remove all the notes in the line.
    public String removeNotes(String line){
        line = line.replaceFirst("//.*", "");
        line = line.replaceAll("( )+"," ");
        return line;
    }


    //return the next line contains a command in the file.
    public String readNextCommand(){
        String line;
        do{
            if (reader.hasNextLine()) {
                line = reader.nextLine();
                line = removeNotes(line);
            }
            else{
                return null;
            }
        } 
        while(!line.matches(".*[a-z]+.*"));
        return line;
    }

    //return a String array representation of the command where parse[0] = command , parse[1] = segment , parse[2] = i.
    public static String[] parse(String line) {
        line = line.replaceAll("( )+", " ");
        return line.split(" ");

    }
    //read the next line correspond to a command and parse it. 
    public String[] parseNextCommand() {
        String line = readNextCommand();
        if(line == null){
            return null;
        }    
        return parse(line) ;
    }


    public static void main(String[] args) {
    /* tests 
    Translator translator = new Translator();
     Parser Parser = new Parser(".\\StackTest.vm");
     String command = Parser.readNextCommand();
     String[] ParsedLine;
     while (null != command) {
        ParsedLine = parse(command);
        //for (int i = 0; i < ParsedLine.length; i++) {
            System.out.println(translator.Translate(ParsedLine));
            //System.out.print(ParsedLine[i] + " | ");
        
        
        System.out.println("\n\n***************************************************\n\n");
        command = Parser.readNextCommand();             
        }
        */
    }
}









