package com.company;

import com.company.DBcmd.*;
import com.company.Exception.CmdException;

public class Parser {

    private FileHandle handler;
    private Tokenizer tokens;
    private DBCmd cmd;
    private Boolean normalParsing;
    private String outputResult;

    public Parser(String comingCommand, FileHandle dbHandler){
        if(!comingCommand.equals("")){
            try{
                handler = dbHandler;
                outputResult = "Thanks for your input message.";
                normalParsing = true;
                tokens = new Tokenizer(comingCommand);
                operate();
            } catch (CmdException e) {
                normalParsing = false;
            }
        }else{
            normalParsing = false;
            outputResult = "Empty command is found.";
        }
    }

    private void executeCmd() {
        cmd.initialize();
        outputResult = cmd.getOutputResult();
        normalParsing = cmd.getNormalRun();
    }

    private void operate() throws CmdException {
        if(!normalParsing) return;
        String token = tokens.getValueByNum(0);
        switch (token) {
            case "USE" -> {
                cmd = new UseCMD(tokens, handler);
                executeCmd();
            }
            case "CREATE" -> {
                cmd = new CreateCMD(tokens, handler);
                executeCmd();
            }
            case "DROP" -> {
                cmd = new DropCMD(tokens, handler);
                executeCmd();
            }
            case "ALTER" -> {
                cmd = new AlterCMD(tokens, handler);
                executeCmd();
            }
            case "INSERT" -> {
                cmd = new InsertCMD(tokens, handler);
                executeCmd();
            }
            case "SELECT" -> {
                cmd = new SelectCMD(tokens, handler);
                executeCmd();
            }
            case "UPDATE" -> {
                cmd = new UpdateCMD(tokens, handler);
                executeCmd();
            }
            case "DELETE" -> {
                cmd = new DeleteCMD(tokens, handler);
                executeCmd();
            }
            case "JOIN" -> {
                cmd = new JoinCMD(tokens, handler);
                executeCmd();
            }
            default -> {
                normalParsing = false;
                outputResult = "Undefined command type '" + token + "' is found.";
            }
        }
    }

    public DBCmd getCmd(){ return cmd; }

    public Tokenizer getTokens(){ return tokens; }

    public String getOutputResult(){ return outputResult; }

    public Boolean getNormalParsing() {
        return normalParsing;
    }

}
