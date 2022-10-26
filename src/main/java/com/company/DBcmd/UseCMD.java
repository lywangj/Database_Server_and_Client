package com.company.DBcmd;

import com.company.*;
import com.company.Exception.CmdException;
import com.company.Exception.IOException;

import java.util.Locale;

public class UseCMD extends DBCmd {

    public UseCMD (Tokenizer TKs, FileHandle dbHandler){
        handler = dbHandler;
        tokens = TKs;
        normalRun = true;
    }

    public void initialize() {
        try{
            checkGrammar();
            storeCmdInformation();
            implement();
            query();
        } catch (Exception e){
            normalRun = false;
            outputResult = e.toString();
        }
    }

    @Override
    public void query(){
        outputResult = "Successfully use database '"+DBName+"'.";
    }

    @Override
    public void implement() throws IOException {
        checkFolderExists(DBName);
        handler.setDbNameInOperating(DBName.toLowerCase());
    }

    @Override
    public void storeCmdInformation(){
        cmdType = tokens.getValueByNum(0);
        int numOfDBName = tokens.getTypeList().indexOf(Token.KeyType.ID);
        DBName = tokens.getInputValueByNum(numOfDBName);
    }

    @Override
    public void checkGrammar() throws CmdException {
        Tokenizer model = new Tokenizer("USE database1;");
        checkLengthOfCmd(model);
        Integer num = getNumOfFirstError(model);
        if(num!=0){
            String errorTK = tokens.getInputValueByNum(num);
            throw new CmdException.grammarErrorException(errorTK);
        }
    }

    public String getOutputResult() { return outputResult; }

}
