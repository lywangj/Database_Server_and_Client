package com.company.DBcmd;

import com.company.*;
import com.company.Exception.CmdException;
import com.company.Exception.IOException;
import com.company.Exception.TableException;

import java.util.ArrayList;

public class UpdateCMD extends DBCmd {

    public UpdateCMD(Tokenizer TKs, FileHandle dbHandler){
        handler = dbHandler;
        tokens = TKs;
        normalRun = true;
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
        int size = tableNames.size();
        String tableName = tableNames.get(size-1);
        String nameValueList = outputNameValueList();

        String conditionList = getOriginalOP(outputConditionList());
        outputResult = "Successfully "+action+" ["+ nameValueList +
                "] in table '"+tableName+"' when ["+conditionList+"].";
    }

    public String outputNameValueList(){
        int toNum = tokens.getTypeList().indexOf(Token.KeyType.WHERE);
        String[] nameValueList = tokens.getInputList().subList(3, toNum).toArray(new String[0]);
        return String.join("",nameValueList);
    }

    public String outputConditionList(){
        int fromNum = tokens.getTypeList().indexOf(Token.KeyType.WHERE)+1;
        int toNum = tokens.getTypeList().indexOf(Token.KeyType.SemiCO);
        String[] conditionList = tokens.getInputList().subList(fromNum,toNum).toArray(new String[0]);
        return String.join("",conditionList);
    }

    public String getOriginalOP(String condition) {
        condition = condition.replaceAll("%EQ%","==")
                .replaceAll("%LT%",">=")
                .replaceAll("%ST%","<=")
                .replaceAll("%NE%","!=");
        return condition;
    }

    @Override
    public void implement() throws TableException, IOException, java.io.IOException {
        String TBName = tableNames.get(0).toLowerCase();
        checkFileExists(tableNames.get(0));
        handler.storeInUseDBInMemory();
        handler.deleteExistingFile(TBName);
        ArrayList<String> valueList = outputValueList();
        handler.updateWithConditions(colNames, valueList, conditions, TBName);

        String wholeContent = handler.outputTableToString(TBName);
        handler.createNewFile(TBName);
        handler.writeWholeFile(wholeContent, TBName);
    }

    public void storeCmdWithCondition() throws CmdException {
        cmdType = tokens.getValueByNum(0);
        tableNames = new ArrayList<>();
        storeTableNameByNum(1);
        colNames = new ArrayList<>();
        storeAttributeList();
        values = new ArrayList<>();
        storeValueList();
        conditions = new ArrayList<>();
        storeConditions();
    }

    @Override
    public void storeAttributeList(){
        boolean turningOnToAction = false;
        for(Token element: tokens.getList()){
            if("SET".equals(element.getTypeName())){
                turningOnToAction = true;
            }
            if("WHERE".equals(element.getTypeName())){
                turningOnToAction = false;
            }
            if(turningOnToAction && checkValidAttribute(element.getTypeName())){
                colNames.add(element.getInput());
            }
        }
    }

    @Override
    public void storeValueList(){
        boolean turningOnToAction = true;
        for(Token element: tokens.getList()){
            if(turningOnToAction&&checkValidValue(element.getTypeName())){
                values.add(element);
            }
            if("WHERE".equals(element.getTypeName())){
                turningOnToAction=false;
            }
        }
    }

    @Override
    public void checkGrammar() throws CmdException {
        Tokenizer model1 = new Tokenizer("UPDATE table1 SET mark=75 WHERE name=='Dave';");
        checkLengthOfCmd(model1);

        int num;
        action = "update";
        num = getNumOfFirstError(model1);
        if(num!=0) {
            String errorTK = tokens.getInputValueByNum(num);
            throw new CmdException.grammarErrorException(errorTK);
        }
    }

    // UPDATE table1 SET mark=75 WHERE name=='Dave';
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
        int num = checkWhenKeywords(model);
        if(num!=0){ return num; }
        num = checkSameCurrToken(model);
        return num;
    }

    public int checkWhenKeywords(Tokenizer model) throws CmdException {
        int num = checkWithSET(model);
        if(num!=0){ return num; }
        num = checkWithWHERE(model);
        return num;
    }

    public int checkWithSET(Tokenizer model) throws CmdException {
        int num =0;
        if(model.getCurrTKType().equals("SET")){
            if(tokens.getCurrTKType().equals("SET")) {
                num = checkInNameValueList();
                model.setCurrTokenNum(6);
            } else {
                num = tokens.getCurrNum();
            }
        }
        return num;
    }

    public int checkWithWHERE(Tokenizer model) throws CmdException {
        int num =0;
        if(model.getCurrTKType().equals("WHERE")){
            if(tokens.getCurrTKType().equals("WHERE")){
                tokens.nextTokenByNum();
                checkConditions();
                model.setCurrTokenNum(10);
            } else {
                num = tokens.getCurrNum();
            }
        }
        return num;
    }

    // SET mark=75, mark=75 WHERE
    public Integer checkInNameValueList() throws CmdException {
        tokens.nextTokenByNum();
        checkBasicNameValue();
        if(tokens.getCurrTKType().equals("WHERE")){
            return 0;
        }else if(tokens.getCurrTKType().equals("Comma")){
            return checkInNameValueList();
        }
        return tokens.getCurrNum();
    }

    public void checkBasicNameValue() throws CmdException {
        checkValidKeyword("ID");
        tokens.nextTokenByNum();
        checkValidKeyword("ASSIGN");
        tokens.nextTokenByNum();
        checkValidValue();
        tokens.nextTokenByNum();
    }

    public ArrayList<String> outputValueList(){
        ArrayList<String> list = new ArrayList<>();
        for(Token element: values){
            list.add(element.getValue());
        }
        return list;
    }

}
