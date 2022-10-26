package com.company.DBcmd;

import com.company.*;
import com.company.Exception.CmdException;
import com.company.Exception.IOException;
import com.company.Exception.TableException;

import java.util.ArrayList;

public class DeleteCMD extends DBCmd {

    public DeleteCMD(Tokenizer TKs, FileHandle dbHandler){
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
        String conditionList = getOriginalOP(outputConditionList());
        outputResult = "Successfully "+action+" in table '"+tableNames.get(0)
                +"' when ["+conditionList+"].";
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
    public void implement() throws IOException, TableException, java.io.IOException {
        String TBName = tableNames.get(0).toLowerCase();
        checkFileExists(tableNames.get(0));
        handler.updateMaxIdNumber(TBName);
//        handler.storeInUseDBInMemory();
        handler.deleteExistingFile(TBName);
        handler.dropRowsByConditions(conditions, TBName);

        String wholeContent = handler.outputTableToString(TBName);
        handler.createNewFile(TBName);
        handler.writeWholeFile(wholeContent, TBName);
    }

    public void storeCmdWithCondition() throws CmdException {
        cmdType = tokens.getValueByNum(0);
        tableNames = new ArrayList<>();
        storeTableNameByNum(2);
        conditions = new ArrayList<>();
        storeConditions();
    }

    @Override
    public void checkGrammar() throws CmdException {
        Tokenizer model1 = new Tokenizer("DELETE FROM table1 WHERE score==100;");
        checkLengthOfCmd(model1);

        int num;
        action = "delete";
        num = getNumOfFirstError(model1);
        if(num!=0) {
            String errorTK = tokens.getInputValueByNum(num);
            throw new CmdException.grammarErrorException(errorTK);
        }
    }

    // DELETE FROM table1 WHERE score==100;
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
        int num = checkWithWHERE(model);
        if(num == 0){
            num = checkSameCurrToken(model);
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
            model.setCurrTokenNum(7);
        }
        return num;
    }

}
