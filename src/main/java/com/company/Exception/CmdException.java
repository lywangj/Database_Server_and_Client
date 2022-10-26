package com.company.Exception;


public class CmdException extends Exception{

    private static final long serialVersionUID = -2405736440969523511L;

    public CmdException(String message) {
        super(message);
    }

    public static class invalidCmdLengthException extends CmdException {
        private static final long serialVersionUID = -2405736440969523512L;

        public invalidCmdLengthException() {
            super("Length of this command is invalid.");
        }
    }

    public static class invalidEndOfCmdException extends CmdException {
        private static final long serialVersionUID = -2405736440969523513L;

        public invalidEndOfCmdException() {
            super("Semicolon ';' must be at the end of command.");
        }
    }

    public static class grammarErrorException extends CmdException {
        private static final long serialVersionUID = -2405736440969523514L;

        public grammarErrorException(String s) {
            super("Grammar error with at least one unexpected keyword '"+s+"' is found.");
        }
    }

    public static class invalidConditionException extends CmdException {
        private static final long serialVersionUID = -2405736440969523515L;

        public invalidConditionException(String condition) {
            super("Invalid condition setting ["+condition+"] is found.");
        }
    }

    public String toString(){
        return getMessage();
    }


}
