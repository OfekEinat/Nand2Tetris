import java.io.File;
import java.io.PrintWriter;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class VMTranslator{

private static String getFileName(String pathString) {
        int endIndex = pathString.lastIndexOf(".");
        int beginIndex = pathString.lastIndexOf("/") + 1;
        return pathString.substring(beginIndex, endIndex);
}
    //change the .vm to .asm
public static String changeFileEndToAsm (String path){
    return path.replace("vm", "asm");
}

public static void WriteTheContentInASM(PrintWriter writer ,Parser parser , File resultFile) {
    try {
        //find the next command and parse it till you reach the end of the file.
        String[] Parsedline = parser.parseNextCommand();
        while(Parsedline != null){
            //translate the command to asmebly commands and append them to the new .asm file. 
            String line = Translator.Translate(Parsedline);
            writer.append(line);
            writer.append("\n");
            Parsedline = parser.parseNextCommand();
        }
        
        //if no input or file not found or the new file is not deletable or writeable.    
    } catch (Exception e) {
        System.err.println(e);
    }
    
}

public static void main(String[] args) {
    String pathString = args[0];
    Path path = Paths.get(pathString);
    Parser parser;
    File resultFile;
    PrintWriter writer;
    
    //open the new file for writing if exists override it else create it.
    if (Files.isDirectory(path)) {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
            System.out.println(path.toString());
            Path newFilePath = path.resolve(path.getFileName() + ".asm");
            System.out.println(newFilePath.toString());
            resultFile = newFilePath.toFile();
            
            if(resultFile.exists()){
                resultFile.delete();
            }
            resultFile.createNewFile();
            writer = new PrintWriter(resultFile);
            writer.append(Translator.Init());
            for (Path file : stream) {
                if (file.toString().endsWith(".vm")) {
                    Translator.setFileName(getFileName(file.toString()));
                    parser = new Parser(file.toString());
                    WriteTheContentInASM(writer ,parser ,resultFile);
                }
            }
            writer.close();
            stream.close();
            }
            catch(Exception e){
                System.err.println(e);
            }
            //close the writer and the directory stream.
            
        }
    
    else {
        resultFile = new File(changeFileEndToAsm(pathString)); 
        try {
            writer = new PrintWriter(resultFile); 
            parser = new Parser(pathString);
            WriteTheContentInASM(writer ,parser ,resultFile);     
            //close the writer
            writer.close();
        } catch (Exception e) {
            System.err.println(e);
        }
    }
}
}