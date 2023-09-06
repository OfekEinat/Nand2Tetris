import java.io.File;
import java.io.PrintWriter;
public class HackAssembler{
    //change the .asm to .hack
    public static String changeFileEndToHack (String path){
        return path.replace("asm", "hack");
    }
public static void main(String[] args) {
    converter converter = new converter();
    //open the file for parsing 
    parser parser = new parser(args[0]);
    //initialize user lables
    converter.addUserSymbols(parser.getLabels());
    //open the new file for writing if exists override it else create it.
    File resultFile = new File(changeFileEndToHack(args[0]));
    try {
            if(resultFile.exists()){
                resultFile.delete();
            }
            resultFile.createNewFile();
    
            PrintWriter writer = new PrintWriter(resultFile);  
            //find the next command and parse it till you reach the end of the file.
            String[] Parsedline = parser.parseNextCommand();
            while(Parsedline != null){
            //convert the command to it's binary representation and append it to the new .hack file. 
            String line = converter.convertCommand(Parsedline);
            writer.append(line);
            writer.append("\n");
            Parsedline = parser.parseNextCommand();
        }
            //close the writer.
            writer.close();
    //if no input or file not found or the new file is not deletable or writeable.    
    } catch (Exception e) {
        System.err.println(e);
    }
}
}