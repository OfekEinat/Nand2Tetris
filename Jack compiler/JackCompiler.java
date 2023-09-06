import java.io.File;
public class JackCompiler {
    static CompileEngine ce;
    public static void main(String[] args){
        //checks if we got an input else prints error msg.
        if(args.length == 0){
            System.err.println("Missing file name!");
            return;
        }
        //if the given arg is a dir compiles all the file within it,
        //else if it is a file compiles it
        File input = new File(args[0]);
        if (input.isDirectory()) {
            for (File file : input.listFiles()) {
                if (isJack(file)) {
                    String resultFilePath = jackToVm(file);
                    compileFile(file.getAbsolutePath(),resultFilePath);
                }
            }
        } 
        else {
            if(isJack(input)){
                String resultFilePath = jackToVm(input);
                compileFile(input.getAbsolutePath(),resultFilePath);
            }
            else{
                System.err.println("not a jack file");
            }
        }
    }
    
    //compiles a file 
    private static void compileFile(String inputPath,String outputPath) {
        ce = new CompileEngine(inputPath,outputPath);
        ce.compileClass();
    }

    //checks if the given file is a jack file.
    private static boolean isJack(File input) {
        return input.getAbsolutePath().endsWith(".jack");
    }

    //replaces the .jack in the given path to .vm
    private static String jackToVm(File jackPath) {
        return jackPath.getAbsolutePath().replace(".jack", ".vm");
    }

}
