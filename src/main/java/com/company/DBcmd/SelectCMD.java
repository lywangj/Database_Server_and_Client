package com.company.DBcmd;

import com.company.*;
import com.company.Exception.CmdException;
import com.company.Exception.IOException;
import com.company.Exception.TableException;

import java.util.ArrayList;

public class SelectCMD extends DBCmd {

    private Boolean availableCondition;

    public SelectCMD(Tokenizer TKs, FileHandle dbHandler){
        handler = dbHandler;
        tokens = TKs;
        normalRun = true;
        availableCondition = false;
    }

    @Override
    public void initialize() {
        try{
            checkGrammar();
            storeCmdWithCondition();
            implement();
            query();
        } catch (Exception e){
            normalRun = false;
            outputResult = e.toString();
        }
    }

    @Override
    public void query(){
        String tableName1 = tableNames.get(0);
        String comment="Successfully "+action+" in table '"+tableName1+"'.";
        outputResult = comment+"\n"+outputResult;
    }

    @Override
    public void implement() throws IOException, TableException, java.io.IOException {
        String TBName = tableNames.get(0).toLowerCase();
        checkFileExists(tableNames.get(0));
        handler.storeInUseDBInMemory();
        if(availableCondition){
            handler.storeRowsByConditions(conditions, TBName);
        }
        handler.selectColsInTable(colNames, TBName);
        outputResult = handler.outputTableToString(TBName);
    }

    public void storeCmdWithCondition() throws CmdException {
        cmdType = tokens.getValueByNum(0);
        if(tokens.getTypeByNum(1).equals("ALL")){
            action = "select all columns";
        } else {
            action = "select columns";
        }
        int numOfFrom = tokens.getTypeList().indexOf(Token.KeyType.FROM);
        tableNames = new ArrayList<>();
        storeTableNameByNum(numOfFrom+1);
        colNames = new ArrayList<>();
        storeAttributeList();
        conditions = new ArrayList<>();
        storeConditions();
    }

    @Override
    public void storeAttributeList(){
        boolean turningOnToAction = true;
        for(Token element: tokens.getList()){
            if("FROM".equals(element.getTypeName())){
                turningOnToAction = false;
            }
            if(turningOnToAction && checkValidAttribute(element.getTypeName())){
                colNames.add(element.getInput());
            }
        }
    }

    @Override
    public void checkGrammar() throws CmdException {
        Tokenizer model1 = new Tokenizer("SELECT * FROM table1;");
        Tokenizer model2 = new Tokenizer("SELECT * FROM table1 WHERE score==100;");
        checkLengthOfCmd(model1);
        int num;
        if(tokens.getValueList().contains("WHERE")){
            num = getNumOfFirstError(model2);
        }else{
            num = getNumOfFirstError(model1);
        }
        if(num!=0) {
            String errorTK = tokens.getInputValueByNum(num);
            throw new CmdException.grammarErrorException(errorTK);
        }
    }

    // SELECT * FROM table1 WHERE score==100;
    @Override
    public Integer getNumOfFirstError(Tokenizer model) throws CmdException {
        int num = 0;
        while(model.nextTokenByNum() && num==0){
            tokens.nextTokenByNum();
            num = checkGrammarForEachToken(model);
        }
        return num;
    }


    public int checkGrammarForEachToken(Tokenizer model) throws CmdException {
        int num ;
        String typeOfModel = model.getCurrTKType();
        if(typeOfModel.equals("ALL")){
            num = checkWithALL(model);
        } else if(typeOfModel.equals("WHERE")) {
            num = checkWithWHERE(model);
        } else{
            num = checkSameCurrToken(model);
        }
        return num;
    }

    public int checkWithALL(Tokenizer model) throws CmdException {
        int num =0;
        String typeOfModel = model.getCurrTKType();
        String typeOfInput = tokens.getCurrTKType();
        if(typeOfModel.equals("ALL")){
            if(!typeOfModel.equals(typeOfInput)){
                num = checkAttributes();
                model.nextTokenByNum();
            }
        }
        return num;
    }

    public int checkWithWHERE(Tokenizer model) throws CmdException {
        int num =0;
        String typeOfModel = model.getCurrTKType();
        String typeOfInput = tokens.getCurrTKType();
        if(typeOfModel.equals("WHERE")&&typeOfInput.equals("WHERE")) {
            tokens.nextTokenByNum();
            checkConditions();
            availableCondition = true;
            model.setCurrTokenNum(8);
        }
        return num;
    }

}
