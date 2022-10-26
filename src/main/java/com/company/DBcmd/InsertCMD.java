package com.company.DBcmd;

import com.company.*;
import com.company.Exception.CmdException;
import com.company.Exception.IOException;
import com.company.Exception.TableException;

import java.util.ArrayList;

public class InsertCMD extends DBCmd {

    public InsertCMD(Tokenizer TKs, FileHandle dbHandler){
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
        int size = tableNames.size();
        String tableName = tableNames.get(size-1);
        ArrayList<String> valueList = outputInputValueList();
        outputResult = "Successfully "+action+" "+valueList+" in table '"+tableName+"'.";
    }

    @Override
    public void implement() throws IOException, TableException, java.io.IOException {
        String TBName = tableNames.get(0).toLowerCase();
        checkFileExists(tableNames.get(0));
        ArrayList<String> valueList = outputValueList();
        handler.checkSameNumOfCols(valueList.size(), TBName);
        handler.writeOneLine(valueList, TBName);
    }

    @Override
    public void storeCmdInformation(){
        cmdType = tokens.getValueByNum(0);
        action = "insert";
        tableNames = new ArrayList<>();
        tableNames.add(tokens.getInputValueByNum(2));
        values = new ArrayList<>();
        storeValueList();
    }

    @Override
    public void checkGrammar() throws CmdException {
        Tokenizer model1 = new Tokenizer("INSERT INTO table1 VALUES(100);");
        checkLengthOfCmd(model1);
        int num = getNumOfFirstError(model1);
        if(num!=0) {
            String errorTK = tokens.getInputValueByNum(num);
            throw new CmdException.grammarErrorException(errorTK);
        }
    }

    //  CREATE TABLE table1(Name);
    public Integer getNumOfFirstError(Tokenizer model) throws CmdException {
        int num = 0;
        while(model.nextTokenByNum() && num==0){
            tokens.nextTokenByNum();
            num = checkForEachToken(model);
        }
        return num;
    }

    public int checkForEachToken(Tokenizer model) throws CmdException {
        int num = checkWithLParen(model);
        if(num == 0){
            num = checkSameCurrToken(model);
        }
        return num;
    }

    public int checkWithLParen(Tokenizer model) throws CmdException {
        int num =0;
        String typeOfModel = model.getCurrTKType();
        String typeOfInput = tokens.getCurrTKType();
        if(typeOfModel.equals("LParen")) {
            if(typeOfModel.equals(typeOfInput)){
                tokens.nextTokenByNum();
                num = checkInValueList();
                model.nextTokenByNum();
                model.nextTokenByNum();
            } else{
                num = model.getCurrNum();
            }
        }
        return num;
    }

    public int checkInValueList() throws CmdException {
        if(tokens.getCurrTKType().equals("RParen")){ return 0; }
        checkValidValue();
        tokens.nextTokenByNum();
        if(tokens.getCurrTKType().equals("RParen")){
            return 0;
        }
        checkValidKeyword("Comma");
        tokens.nextTokenByNum();
        return checkInValueList();
    }

    public void storeValueList(){
        for(Token element: tokens.getList()){
            if(checkValidValue(element.getTypeName())){
                values.add(element);
            }
        }
    }

    public ArrayList<String> outputInputValueList(){
        ArrayList<String> list = new ArrayList<>();
        for(Token element: values){
            list.add(element.getInput());
        }
        return list;
    }

    public ArrayList<String> outputValueList(){
        ArrayList<String> list = new ArrayList<>();
        for(Token element: values){
            list.add(element.getValue());
        }
        return list;
    }

    public String getOutputResult() { return outputResult; }

}
