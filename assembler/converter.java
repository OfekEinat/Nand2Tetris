import java.util.HashMap;

public class converter {
    private symbolTable symbolTable;

    public converter(){
        symbolTable = new symbolTable();
    }

    public void addUserSymbols(HashMap<String,Integer> userSymbols){
        symbolTable.addUserSymbols(userSymbols);
    }
    //parse address in to binary representation and add zeros to the left up to 16 bits total.
    public static String Integerto16bitBinaryString(int address){
        String binaryRep = "";
        String tempBinaryRep = Integer.toBinaryString(address);
        int numOfLeadingZeros = 16 - tempBinaryRep.length();
        for (int i = 0; i < numOfLeadingZeros ; i++) {
            binaryRep += "0"; 
        }
        binaryRep += tempBinaryRep;
        return binaryRep;
    }

    public String convertA_command(String[] A_Command) {
        int DecRep;
        try{
            //if the command value is a number parse it to Integer.
              DecRep = Integer.parseInt(A_Command[1]);
        }
        catch(Exception e){
            //else its a string. check if it is in the table if not add it.
                if(symbolTable.hasKey(A_Command[1])){
                    DecRep = symbolTable.getValue(A_Command[1]);
                }
                else{
                    symbolTable.putVariable(A_Command[1]);
                    DecRep = symbolTable.getValue(A_Command[1]);
                }
        }
        return Integerto16bitBinaryString(DecRep);
    }
    
    public String convertC_command(String[] C_Command){
        String BinaryRep = "111";
        String comp,jump,dest;
        dest = symbolTable.getValueBinaryRep("d" + C_Command[1] ,3);
        comp = symbolTable.getValueBinaryRep(C_Command[2] ,7);
        jump = symbolTable.getValueBinaryRep(C_Command[3] ,3);
        BinaryRep += (comp + dest + jump);
        return BinaryRep;
    }
       
    public String convertCommand(String[] parsedCommand){
        String convertedCommand;
        if(parsedCommand[0].equals("A")) {
            convertedCommand = this.convertA_command(parsedCommand);
        }
        else{
            convertedCommand = this.convertC_command(parsedCommand);
        }    
            return convertedCommand;
    }

    public static void main(String[] args) {
       /*test converter converter = new converter();
        parser parser = new parser(".\\Max.asm");
        converter.addUserSymbols(parser.getLabels());
        String[] line = parser.parseNextCommand();
        while(line != null){
            System.out.println(converter.convertCommand(line));
            System.out.println("\n*************************************************************************\n");
            line = parser.parseNextCommand();
        } */
      
    }
}

