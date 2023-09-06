import java.util.HashMap;

public class symbolTable {
    private HashMap<String , Integer> symbolTable = new HashMap<>();
    int nextAddressAvilable = 16;
    public symbolTable() {
        symbolTable.put("SP"  ,0);
        symbolTable.put("LCL" ,1);
        symbolTable.put("ARG" ,2);
        symbolTable.put("THIS",3);
        symbolTable.put("THAT",4);
        symbolTable.put("R0"  ,0);
        symbolTable.put("R1"  ,1);
        symbolTable.put("R2"  ,2);
        symbolTable.put("R3"  ,3);
        symbolTable.put("R4"  ,4);
        symbolTable.put("R5"  ,5);
        symbolTable.put("R6"  ,6);
        symbolTable.put("R7"  ,7);
        symbolTable.put("R8"  ,8);
        symbolTable.put("R9"  ,9);
        symbolTable.put("R10" ,10);
        symbolTable.put("R11" ,11);
        symbolTable.put("R12" ,12);
        symbolTable.put("R13" ,13);
        symbolTable.put("R14" ,14);
        symbolTable.put("R15" ,15);
        symbolTable.put("SCREEN",16384);
        symbolTable.put("KBD" ,24576);
        //dest decimal rep
        symbolTable.put("dnull",0);
        symbolTable.put("dM"  , 1);
        symbolTable.put("dD"  , 2);
        symbolTable.put("dMD" , 3);
        symbolTable.put("dA"  , 4);
        symbolTable.put("dAM" , 5);
        symbolTable.put("dAD" , 6);
        symbolTable.put("dAMD", 7);
        //comp decimal rep
        symbolTable.put("0"  ,42);
        symbolTable.put("1"  ,63);
        symbolTable.put("-1" ,58);
        symbolTable.put("D"  ,12);
        symbolTable.put("A"  ,48);
        symbolTable.put("!D" ,13);
        symbolTable.put("!A" ,49);
        symbolTable.put("-D" ,15);
        symbolTable.put("-A" ,51);
        symbolTable.put("D+1",31);
        symbolTable.put("A+1",55);
        symbolTable.put("D-1",14);
        symbolTable.put("A-1",50);
        symbolTable.put("D+A",2 );
        symbolTable.put("D-A",19);
        symbolTable.put("A-D",7 );
        symbolTable.put("D&A",0 );
        symbolTable.put("D|A",21);
        symbolTable.put("M"  ,112);
        symbolTable.put("!M" ,113);
        symbolTable.put("-M" ,115);
        symbolTable.put("M+1",119);
        symbolTable.put("M-1",114);
        symbolTable.put("D+M",66 );
        symbolTable.put("D-M",83 );
        symbolTable.put("M-D",71 );
        symbolTable.put("D&M",64 );
        symbolTable.put("D|M",85 );
        //jump decimal rep.
        symbolTable.put("null",0);
        symbolTable.put("JGT", 1);
        symbolTable.put("JEQ", 2);
        symbolTable.put("JGE", 3);
        symbolTable.put("JLT", 4);
        symbolTable.put("JNE", 5);
        symbolTable.put("JLE", 6);
        symbolTable.put("JMP", 7);
    }

    //add a new varibale to the table and a allocate a register in the memory.
    public void putVariable(String key){
        symbolTable.put(key,nextAddressAvilable);
        nextAddressAvilable++;
    }

    //get the value correspond to key with "bits" amount of bits.
    public String getValueBinaryRep(String key , int bits){
        String binaryRep = Integer.toBinaryString(symbolTable.get(key));
        int binaryRepLength = binaryRep.length();
        if(binaryRepLength < bits){
            for (int i = 0; i < bits - binaryRepLength; i++) {
                binaryRep = "0" + binaryRep;
            }
        }
        return binaryRep;
    }
    
    public void addUserSymbols(HashMap<String,Integer> userSymbols){
        symbolTable.putAll(userSymbols);
    }

    public Boolean hasKey(String key) {
        return symbolTable.containsKey(key);
    }

    public int getValue(String key) {
        return symbolTable.get(key);
    }

}
