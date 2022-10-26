package com.company.DBcmd;

import com.company.*;
import com.company.Exception.CmdException;
import com.company.Exception.IOException;

import java.util.ArrayList;

public class DropCMD extends DBCmd {

    public DropCMD(Tokenizer TKs, FileHandle dbHandler){
        handler = dbHandler;
        tokens = TKs;
        normalRun = true;
    }

    public void initialize() {
        try{
            checkGrammar();
            if(action.equals("drop database")){
                storeCmdInformation();
                implement();
                query();
            } else {
                storeCmdInformationForTable();
                implementForTable();
                queryForTable();
            }
        } catch (Exception e){
            normalRun = false;
            outputResult = e.toString();
        }
    }

    @Override
    public void query(){
        outputResult = "Successfully "+action+" '"+DBName+"'.";
    }

    public void queryForTable(){
        int size = tableNames.size();
        outputResult = "Successfully "+action+" '"+tableNames.get(size-1)+"'.";
    }

    @Override
    public void implement() throws IOException {
        checkFolderExists(DBName);
        handler.deleteExistingFolder(DBName.toLowerCase());
    }

    public void implementForTable() throws IOException {
        String TBName = tableNames.get(0).toLowerCase();
        checkFileExists(tableNames.get(0));
        handler.deleteExistingFile(TBName);
    }

    @Override
    public void storeCmdInformation(){
        int numOfID;
        cmdType = tokens.getValueByNum(0);
        numOfID = tokens.getTypeList().indexOf(Token.KeyType.ID);
        DBName = tokens.getInputValueByNum(numOfID);
    }

    public void storeCmdInformationForTable(){
        int numOfID;
        cmdType = tokens.getValueByNum(0);
        if(tableNames==null){ tableNames = new ArrayList<>(); }
        numOfID = tokens.getTypeList().indexOf(Token.KeyType.ID);
        tableNames.add(tokens.getInputValueByNum(numOfID));
    }

    // override
    public void checkGrammar() throws CmdException {
        Tokenizer model1 = new Tokenizer("DROP DATABASE database1;");
        Tokenizer model2 = new Tokenizer("DROP TABLE table1;");
        checkLengthOfCmd(model1);
        if(compareTypeList(model1, tokens)){
            action = "drop database";
        } else if (compareTypeList(model2, tokens)){
            action = "drop table";
        } else {
            Integer num = getNumOfFirstError(model1);
            String errorTK = tokens.getInputValueByNum(num);
            throw new CmdException.grammarErrorException(errorTK);
        }
    }

    public Boolean compareTypeList(Tokenizer model, Tokenizer input){
        return model.getTypeList().equals(input.getTypeList());
    }

    public String getOutputResult() { return outputResult; }

}
