package com.company.DBcmd;

import com.company.*;
import com.company.Exception.CmdException;
import com.company.Exception.IOException;

import java.util.ArrayList;

public class CreateCMD extends DBCmd {

    public CreateCMD(Tokenizer TKs, FileHandle dbHandler){
        handler = dbHandler;
        tokens = TKs;
        normalRun = true;
    }

    @Override
    public void initialize() {
        try{
            checkGrammar();
            if(action.equals("create database")){
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
        String tableName = tableNames.get(size-1);
        if (tokens.getLength()==4){
            outputResult = "Successfully "+action+" '"+tableName+"'.";
        }else{
            outputResult = "Successfully "+action+" '"+tableName+"' with "+colNames+".";
        }
    }

    @Override
    public void implement() throws IOException {
        if(handler.checkFolderExists(DBName.toLowerCase())){
            throw new IOException.conflictFolderException(DBName);
        }
        handler.createNewFolder(DBName.toLowerCase());
    }

    public void implementForTable() throws IOException, java.io.IOException {
        String TBName = tableNames.get(0).toLowerCase();
        if(handler.checkFileExists(TBName)){
            throw new IOException.conflictFileException(tableNames.get(0));
        }
        handler.createNewFile(TBName.toLowerCase());
        if(tokens.getLength()>4){
            handler.writeHeadLine(colNames, TBName);
        }
    }

    @Override
    public void storeCmdInformation(){
        cmdType = tokens.getValueByNum(0);
        DBName = tokens.getValueByNum(2);
    }

    public void storeCmdInformationForTable(){
        cmdType = tokens.getValueByNum(0);
        tableNames = new ArrayList<>();
        storeTableNameByNum(2);
        if(tokens.getLength()>4){
            colNames = new ArrayList<>();
            storeAttributeList();
        }
    }

    @Override
    public void checkGrammar() throws CmdException {
        Tokenizer model1 = new Tokenizer("CREATE DATABASE DB1;");
        checkLengthOfCmd(model1);

        int num;
        if("DATABASE".equals(tokens.getValueByNum(1))){
            num = checkGrammarForDatabase();
        } else {
            num = checkGrammarForTable();
        }
        if(num!=0) {
            String errorTK = tokens.getInputValueByNum(num);
            throw new CmdException.grammarErrorException(errorTK);
        }
    }

    public int checkGrammarForDatabase() throws CmdException {
        Tokenizer model1 = new Tokenizer("CREATE DATABASE DB1;");
        action = "create database";
        return getNumOfFirstError(model1);
    }

    public int checkGrammarForTable() throws CmdException {
        Tokenizer model2 = new Tokenizer("CREATE TABLE table1;");
        Tokenizer model3 = new Tokenizer("CREATE TABLE table1(Name);");
        int num ;
        action = "create table";
        if(tokens.getLength()==4){
            num = getNumOfFirstError(model2);
        } else {
            num = getNumOfFirstError(model3);
        }
        return num;
    }

    @Override
    public Integer getNumOfFirstError(Tokenizer model) throws CmdException {
        int num = 0;
        while(model.nextTokenByNum() && num==0){
            tokens.nextTokenByNum();
            num = checkForEachToken(model);
        }
        return num;
    }

    public int checkForEachToken(Tokenizer model) throws CmdException {
        int num = checkInAttributes(model);
        if(num == 0){
            num = checkSameCurrToken(model);
        }
        return num;
    }


    public int checkInAttributes(Tokenizer model) throws CmdException {
        int num =0;
        String typeOfModel = model.getCurrTKType();
        String typeOfInput = tokens.getCurrTKType();
        if(typeOfModel.equals("LParen")) {
            if(!typeOfModel.equals(typeOfInput)){ return model.getCurrNum(); }
            tokens.nextTokenByNum();
            num = checkBracketAttributes();
            model.nextTokenByNum();
            model.nextTokenByNum();
        }
        return num;
    }

    public String query(DBServer s){
        return outputResult;
    }
}
