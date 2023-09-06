public class Translator {
private static String fileName ="";    
//set commands templates.        
private final static String Add =  "@SP \nAM=M-1 \nD=M \nA=A-1 \nM=M+D \n";
private final static String Sub =  "@SP \nAM=M-1 \nD=M \nA=A-1 \nM=M-D \n";    
private final static String Neg =  "@SP \nA=M-1 \nM=-M";
//counters to make difference between the lables in the Eq Gt and Lt command.
private static int EqCounter = 0;
private static int GtCounter = 0;
private static int LtCounter = 0;
private static String Eq =  Sub + "@SP \nA=M-1 \nD=M \n@NE" + EqCounter + " \nD;JNE \n@SP \nA=M-1 \nM=-1 \n@EQ" + EqCounter + " \n0;JMP \n(NE" + EqCounter + ") \n@SP \nA=M-1 \nM=0 \n(EQ" + EqCounter + ") \n";
private static String Gt =  Sub + "@SP \nA=M-1 \nD=M \n@LE" + GtCounter + " \nD;JLE \n@SP \nA=M-1 \nM=-1 \n@GT" + GtCounter + " \n0;JMP \n(LE" + GtCounter + ") \n@SP \nA=M-1 \nM=0 \n(GT" + GtCounter + ") \n";
private static String Lt =  Sub + "@SP \nA=M-1 \nD=M \n@GE" + LtCounter + " \nD;JGE \n@SP \nA=M-1 \nM=-1 \n@LT" + LtCounter + " \n0;JMP \n(GE" + LtCounter + ") \n@SP \nA=M-1 \nM=0 \n(LT" + LtCounter + ") \n";
private final static String And =  "@SP \nAM=M-1 \nD=M \nA=A-1 \nM=M&D \n";
private final static String Or =   "@SP \nAM=M-1 \nD=M \nA=A-1 \nM=M|D \n";
private final static String Not =  "@SP \nA=M-1 \nM=!M \n";
private final static String PushTemplate = "@SP \nA=M \nM=D \n@SP \nM=M+1 \n";
private final static String PopTemplate =  "A=M \nA=A+D \nD=A \n@R13 \nM=D \n@SP \nAM=M-1 \nD=M \n@R13 \nA=M \nM=D \n";
private static int returnCounter = 1;
//change the given statment syntax from vm to asm.
public static String changToHackSymbol(String segment){
    String hackSegment = "";
    switch (segment) {
        case "local":
            hackSegment = "@LCL";
        break;

        case "argument":
            hackSegment = "@ARG";
        break;

        case "this":
            hackSegment = "@THIS";
        break;

        case "that":
            hackSegment = "@THAT";
        break;

        case "temp":
            hackSegment = "@R5";
        break;

        case "static":
            hackSegment = "@16";    
        break;    
    }
    return hackSegment;
}
public static void setFileName(String newFileName) {
    fileName = newFileName;
}

public static String TranslatePushCommand(String[] PushCommand){
    String asemblyCommands = "//Push "+ PushCommand[1] +" " + PushCommand[2] + "\n" ;
    //pushCommand[1] signifies the segment in the vm command.
    String segment = changToHackSymbol(PushCommand[1]) + "\n";
    //pushCommand[2] signifies the index in the vm command.
    String index = "@" + PushCommand[2] + "\n";
    switch (PushCommand[1]) {

        case "constant":
            asemblyCommands += index + "D=A \n";
            break;

        case "temp":
            asemblyCommands += index +"D=A \n" + segment + "A=A+D \nD=M \n";
            break;    

        case "pointer":
            if (PushCommand[2].equals("0")) {
                asemblyCommands += "@THIS \nD=M \n";    
            }
            else{
                asemblyCommands += "@THAT \nD=M \n";
            }
            break;
            case "static":
                asemblyCommands += "@" + fileName + "." + PushCommand[2] + "\nD=M \n";
            break;
        
        default:
            asemblyCommands += index +"D=A \n" + segment + "A=M \nA=A+D \nD=M \n";
            break;
    }
        
    return asemblyCommands + PushTemplate;
}

public static String TranslatePopCommand(String[] PopCommand){
    String asemblyCommands = "//Pop "+ PopCommand[1] +" " + PopCommand[2] + "\n";
    //popCommand[1] signifies the segment in the vm command.
    String segment = changToHackSymbol(PopCommand[1]) + "\n";
    //popCommand[2] signifies the index in the vm command.
    String index = "@" + PopCommand[2] + "\n";    
    switch (PopCommand[1]) {
        case "temp":
            asemblyCommands += index + "D=A \n" + segment + PopTemplate.substring(5);    
            break;

        case "pointer":
            if (PopCommand[2].equals("0")) {
                asemblyCommands += "@SP \nAM=M-1 \nD=M \n@THIS \nM=D \n";    
            }
            else{
                asemblyCommands += "@SP \nAM=M-1 \nD=M \n@THAT \nM=D \n";
            }

            break;

        case "static":
            asemblyCommands += "@" + fileName + "." + PopCommand[2] + PopTemplate.substring(11);    
            break;

        default:
            asemblyCommands += index + "D=A \n" + segment + PopTemplate;
            break;
    }

    return asemblyCommands ;
}

public static String TranslateAddCommand(){
    return Add;
}

public static String TranslateSubCommand(){
    return Sub;
}

public static String TranslateGtCommand(){
    return Gt;
}

public static String TranslateEqCommand(){
    return Eq;
}

public static String TranslateLtCommand(){
    return Lt;
}

public static String TranslateNegCommand(){
    return Neg;
}

public static String TranslateAndCommand(){
    return And;
}

public static String TranslateOrCommand(){
    return Or;
}

public static String TranslateNotCommand(){
    return Not;
}

public static void setEqLablesCouter(){
    EqCounter++;
    Eq =  Sub + "@SP \nA=M-1 \nD=M \n@NE" + EqCounter + " \nD;JNE \n@SP \nA=M-1 \nM=-1 \n@EQ" + EqCounter + " \n0;JMP \n(NE" + EqCounter + ") \n@SP \nA=M-1 \nM=0 \n(EQ" + EqCounter + ") \n";
}

public static void setGtLablesCouter(){
    GtCounter++;
    Gt =  Sub + "@SP \nA=M-1 \nD=M \n@LE" + GtCounter + " \nD;JLE \n@SP \nA=M-1 \nM=-1 \n@GT" + GtCounter + " \n0;JMP \n(LE" + GtCounter + ") \n@SP \nA=M-1 \nM=0 \n(GT" + GtCounter + ") \n";
}

public static void setLtLablesCouter(){
    LtCounter++;
    Lt =  Sub + "@SP \nA=M-1 \nD=M \n@GE" + LtCounter + " \nD;JGE \n@SP \nA=M-1 \nM=-1 \n@LT" + LtCounter + " \n0;JMP \n(GE" + LtCounter + ") \n@SP \nA=M-1 \nM=0 \n(LT" + LtCounter + ") \n";
}

public static String TranslateLabelCommand(String[] LabelCommand){
    return "(" + LabelCommand[1] + ")\n";
}

public static String TranslateGoToCommand(String[] GoToCommand) {
    return "@" + GoToCommand[1] + "\n0;JMP\n";
}

public static String TranslateIfGoToCommand(String[] IfGoToCommand) {
    return "@SP \nAM=M-1 \nD=M \n@" + IfGoToCommand[1] + "\nD;JNE\n";    
}

public static String TranslateFunctionCommand(String[] FunctionCommand) {
     String asembllyCommands = "(" + FunctionCommand[1] + ") \n@SP \nD=M \n@LCL \nM=D \n";
     String[] initLocalVars = {"push","constant","0"};
     for (int i = 0; i < Integer.parseInt(FunctionCommand[2]); i++) {
        asembllyCommands += Translate(initLocalVars);
     }
     return asembllyCommands;     
}

public static String TranslateSaveSegments(){
    String asembllyCommands = "//push LCL segment to the stack\n";
    for (int i = 1; i < 5; i++) {       
        String[] pushTemp = {"push" , "temp" , Integer.toString(i)};
        asembllyCommands += Translate(pushTemp);
        if((i+1)!=5){
        asembllyCommands += "//push " + (i+1) + " segment to the stack\n";
        }
    }
    asembllyCommands += "//done save sements!\n";
    asembllyCommands = asembllyCommands.replaceAll("R5", "R0");
    return asembllyCommands;
}

public static String TranslateCallCommand(String[] CallCommand) {
    String asembllyCommands = "//push the return address to the stack.\n";
    String returnLable = CallCommand[1].replaceAll("\\..*","") + "$ret." + returnCounter;
    asembllyCommands += "@" + returnLable +"\nD=A \n@SP \nA=M \nM=D \n@SP \nM=M+1 \n"; 
    returnCounter++;
    //save the segment pointers of the caller.
    asembllyCommands += TranslateSaveSegments();
    //initialize ARG and LCL pointers to their new locations in the calle frame.
    asembllyCommands += "//set ARG and LCL of the calle to its location\n";
    asembllyCommands += "@" + CallCommand[2] + "\nD=A \n@5 \nD=D+A \n@SP \nA=M \nD=A-D \n@ARG \nM=D \n@SP \nD=M \n@LCL \nM=D \n";
    asembllyCommands += "//jump to the calle\n";
    asembllyCommands += "@" + CallCommand[1] + "\n0;JMP \n";
    //declare the return label
    asembllyCommands += "//set lable to return\n";
    asembllyCommands += "(" + returnLable + ")\n";
    return asembllyCommands;
}

public static String TranslateReturnSegments(){
    String asembllyCommands = "";
    for (int i = 4; i > 0; i--) {       
            asembllyCommands += Translate(new String[] {"pop" , "temp" , Integer.toString(i)});
    }
    asembllyCommands = asembllyCommands.replaceAll("R5", "R0");
    return asembllyCommands;
}

public static String Init() {
    return "// init\n @256 \nD=A \n@SP \nM=D \n" +Translate(new String[] {"call" ,"Sys.init" , "0"});
}

public static String TranslateReturnCommand() {
    String assemblyCommands ="//RETURN\n//put the return value in place\n";
    
    assemblyCommands += "//save the return adress in R14 \n@5 \nD=A \n@LCL \nA=M \nA=A-D \nD=M \n@R14 \nM=D \n";
    assemblyCommands += TranslatePopCommand(new String[] {"pop" , "argument" , "0"});
    assemblyCommands += "//put stack pointer right after the saved segments \n@LCL \nD=M \n@SP \nM=D \n";
    assemblyCommands += "//save the return value adress in R15 \n@ARG \nD=M \n@R15 \nM=D \n//pop all the previous segments pointers from the stack\n";
    assemblyCommands += TranslateReturnSegments();
    assemblyCommands += "//put the stack pointer right after the returned value\n@R15 \nD=M+1 \n@SP \nM=D \n//jump to the return address\n";
    assemblyCommands += "@R14 \nA=M \n0;JMP \n";
    return assemblyCommands;
}






public static String Translate(String[] Command){
    String asemblyCommands = "";
    //command[0] signifies which command was written;
    switch (Command[0]) {
        case "push":
            asemblyCommands = TranslatePushCommand(Command);
            break;
    
        case "pop":
            asemblyCommands = TranslatePopCommand(Command);
            break;

        case "add":
            asemblyCommands = TranslateAddCommand();
            break;
            
        case "sub":
            asemblyCommands = TranslateSubCommand();
            break;

        case "neg":
            asemblyCommands = TranslateNegCommand();
            break;

        case "and":
            asemblyCommands = TranslateAndCommand();
            break;

        case "or":
            asemblyCommands = TranslateOrCommand();
            break;

        case "not":
            asemblyCommands = TranslateNotCommand();
            break;

        case "gt":
            asemblyCommands = TranslateGtCommand();
            setGtLablesCouter();
            break;

        case "eq":
            asemblyCommands = TranslateEqCommand();
            setEqLablesCouter();
            break;

        case "lt":
            asemblyCommands = TranslateLtCommand();
            setLtLablesCouter();        
            break;

        case "label":
            asemblyCommands = TranslateLabelCommand(Command);
            break;
        case "goto":
            asemblyCommands = TranslateGoToCommand(Command);
            break;

        case "if-goto":
            asemblyCommands = TranslateIfGoToCommand(Command);
            break;
        
        case "function":
            asemblyCommands = TranslateFunctionCommand(Command);
            break;

        case "call":
            asemblyCommands = TranslateCallCommand(Command);
            break;
        case "return":
            asemblyCommands = TranslateReturnCommand();
    }
    return  asemblyCommands;
}

public static void main(String[] args) {
   /*tests 
    String[][] VmCommands = {{"pop" , "local" , "17"} , {"pop" , "arguments" , "6"} ,{"pop" , "that" , "9"} ,{"pop" , "this" , "4"}};
    System.out.println(TranslatePopCommand(VmCommands[0]));
    System.out.println("************************************");
    System.out.println(TranslatePopCommand(VmCommands[1]));
    System.out.println("************************************");
    System.out.println(TranslatePopCommand(VmCommands[2]));
    System.out.println("************************************");
    System.out.println(TranslatePopCommand(VmCommands[3]));
    System.out.println("************************************");
     
    String s = "a b c d          g";
    s=s.replaceAll("( )+"," ");
    String[] splitted = s.split("\s");
    
    for (int i = 0; i < splitted.length; i++) {
        System.out.println(splitted[i]);    
    }
    *//*
    String s  =  "                  ";
    String s2 = "           a    ";
    String s3 = "           A    ";
    String s4 = "           zxc    ";
    String[] strings = {s , s2 ,s3 ,s4};
    for (int i = 0; i < strings.length; i++) {
        System.out.println(strings[i].matches(".*[a-z]+.*"));
    }
    String[] s = {"eq"};
    System.out.println(Translate(s));
    System.out.println(Translate(s));
    System.out.println(Translate(s));
    System.out.println(Translate(s));
*/

//for (int  i= 0; i > -5; i--) {
//String[] pushTemp = {"push" , "temp" , Integer.toString(i)};
String[] s = {"call", "foo.main" , "3"};
System.out.println(PopTemplate.substring(11));
}
}
