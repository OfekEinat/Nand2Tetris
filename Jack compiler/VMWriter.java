import java.io.PrintWriter;

public class VMWriter {
    private PrintWriter writer;
    public static final String VAR = "var";
    public static final String LOCAL = "local";
    public static final String FIELD = "field";
    public static final String THIS = "this";
    public static final String PUSH = "push ";
    public static final String POP = "pop ";
    public static final String LABEL = "label ";
    public static final String GOTO = "goto ";
    public static final String IF_GOTO = "if-goto ";
    public static final String FUNCTION = "function ";
    public static final String CALL = "call ";
    public static final String RETURN = "return ";

    // Creates a new output .vm file
    public VMWriter(String outputPath) {
        try {
            writer = new PrintWriter(outputPath);
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    // Writes a VM push command
    public void writePush(String segment, int index) {
        if (segment.equals(VAR)) {
            segment = LOCAL;
        }
        if (segment.equals(FIELD)) {
            segment = THIS;
        }
        writeLine(PUSH + segment + " " + index);
    }

    // Writes a VM pop command
    public void writePop(String segment, int index) {
        if (segment.equals(VAR)) {
            segment = LOCAL;
        }
        if (segment.equals(FIELD)) {
            segment = THIS;
        }
        writeLine(POP + segment + " " + index);
    }

    // Writes a VM arithmetic-logical command.
    public void writeArithmetic(String ar_command) {
        writeLine(ar_command);
    }

    // Writes a VM label command
    public void writeLabel(String label) {
        writeLine(LABEL + label);
    }

    // Writes a VM goto command
    public void writeGoto(String label) {
        writeLine(GOTO + label);
    }

    // Writes a VM if-goto command
    public void writeIf(String label) {
        writeLine(IF_GOTO + label);
    }

    // Writes a VM call command
    public void writeCall(String name, int nArgs) {
        writeLine(CALL + name + " " + nArgs);
    }

    // Writes a VM function command
    public void writeFunction(String name, int nArgs) {
        writeLine(FUNCTION + name + " " + nArgs);
    }

    // Writes a VM return command
    public void writeReturn() {
        writeLine(RETURN);
    }

    // Closes the output file writer
    public void close(){
        writer.close();
    }

    // Prints a line to the file
    // If an error occurred, closes the file
    private void writeLine(String str) {
        writer.println(str);
    }
}
