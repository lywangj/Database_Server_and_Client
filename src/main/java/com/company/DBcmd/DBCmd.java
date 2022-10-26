package com.company.DBcmd;

import com.company.*;
import com.company.Exception.CmdException;
import com.company.Exception.IOException;
import com.company.Exception.TableException;

import java.util.ArrayList;
import java.util.Arrays;

public abstract class DBCmd {

    FileHandle handler;
    ArrayList<Condition> conditions;
    ArrayList<String> colNames;
    ArrayList<Token> values;
    ArrayList<String> tableNames;
    String DBName;
    String cmdType;
    String action;
    String outputResult;
    Boolean normalRun;
    Tokenizer tokens;

    /* basic methods */
    public void initialize() {}

    public void query() {}

    public void implement() throws CmdException, IOException, TableException, java.io.IOException {
    }

    public void storeCmdInformation() {
    }

    public void checkGrammar() throws CmdException {
    }

    public void checkFolderExists(String DBName) throws IOException {
        if(!handler.checkFolderExists(DBName.toLowerCase())){
            throw new IOException.absentFolderException(DBName);
        }
    }

    public void checkFileExists(String TBName) throws IOException {
        if(!handler.checkFileExists(TBName.toLowerCase())){
            throw new IOException.absentFileException(TBName);
        }
    }

    /* compare to model */
    public void checkLengthOfCmd(Tokenizer model) throws CmdException {
        if (!checkValidLength(model, tokens)) {
            throw new CmdException.invalidCmdLengthException();
        } else if (!checkRedundantWords(tokens)) {
            throw new CmdException.invalidEndOfCmdException();
        }
    }

    public Boolean checkValidLength(Tokenizer model, Tokenizer input) {
        return model.getLength() <= input.getLength();
    }

    public Boolean checkRedundantWords(Tokenizer input) {
        int numOfSemicolon = input.getValueList().indexOf(";");
        return numOfSemicolon == input.getLength() - 1;
    }

    public int checkSameCurrToken(Tokenizer model){
        if(!model.getCurrTKType().equals(tokens.getCurrTKType())) {
            return model.getCurrNum();
        }
        return 0;
    }

    public Boolean compareTypeList(Tokenizer model, Tokenizer input) {
        return model.getTypeList().equals(input.getTypeList());
    }

    public Integer getNumOfFirstError(Tokenizer model) throws CmdException {
        int num =0;
        while(model.nextTokenByNum() && num==0){
            tokens.nextTokenByNum();
            num = checkSameCurrToken(model);
        }
        return num;
    }

    /* check grammar */
    public void checkConditions() throws CmdException {
        if(tokens.getCurrTKType().equals("LParen")){
            checkConditionStruct();
        } else if(tokens.getCurrTKType().equals("ID")){
            checkBasicCondition();
        }else{
            String errorTK = tokens.getInputValueByNum(tokens.getCurrNum());
            throw new CmdException.grammarErrorException(errorTK);
        }
    }

    // (score==100) AND (score==80)
    // ((score==100) AND (score==90)) AND (score==80)
    public void checkConditionStruct() throws CmdException {
        checkBracketCondition();
        checkValidKeyword("AND","OR");
        tokens.nextTokenByNum();
        checkValidKeyword("LParen");
        checkBracketCondition();
    }

    public void checkBracketCondition() throws CmdException {
        tokens.nextTokenByNum();
        if(tokens.getCurrTKType().equals("LParen")){
            checkConditionStruct();
            checkValidKeyword("RParen");
            tokens.nextTokenByNum();
        } else {
//            checkGrammarInConditionWithinBracketStruct();
            checkBasicCondition();
            checkValidKeyword("RParen");
            tokens.nextTokenByNum();
        }
    }

    // score==100)
//    public void checkGrammarInConditionWithinBracketStruct() throws CmdException {
//        checkBasicCondition();
//        checkValidKeyword("RParen");
//        tokens.nextTokenByNum();
//    }

    // score==100
    public void checkBasicCondition() throws CmdException {
        checkValidKeyword("ID");
        tokens.nextTokenByNum();
        checkValidKeyword("OP");
        tokens.nextTokenByNum();
        checkValidValue();
        tokens.nextTokenByNum();
    }

    public int checkBracketAttributes() throws CmdException {
        if(tokens.getCurrTKType().equals("RParen")){
            return 0;
        }
        checkValidKeyword("ID");
        tokens.nextTokenByNum();
        if(tokens.getCurrTKType().equals("RParen")){
            return 0;
        }
        checkValidKeyword("Comma");
        tokens.nextTokenByNum();
        return checkBracketAttributes();
    }

    public int checkAttributes() throws CmdException {
        if(tokens.getCurrTKType().equals("FROM")){ return 0; }
        checkValidKeyword("ID");
        tokens.nextTokenByNum();
        if(tokens.getCurrTKType().equals("FROM")){ return 0; }
        checkValidKeyword("Comma");
        tokens.nextTokenByNum();
        return checkAttributes();
    }

    /* check keywords */
    public void checkValidKeyword(String word1, String word2) throws CmdException {
        if(!tokens.getCurrTKType().equals(word1)&&!tokens.getCurrTKType().equals(word2)){
            String errorTK = tokens.getInputValueByNum(tokens.getCurrNum());
            throw new CmdException.grammarErrorException(errorTK);
        }
    }

    public void checkValidKeyword(String word) throws CmdException {
        if(!tokens.getCurrTKType().equals(word)){
            String errorTK = tokens.getInputValueByNum(tokens.getCurrNum());
            throw new CmdException.grammarErrorException(errorTK);
        }
    }

    public void checkValidValue() throws CmdException {
        ArrayList<String> elementsOfValue =
                new ArrayList<>(Arrays.asList("INT", "FLOAT", "BOOL", "STR", "NULL"));
        if(!elementsOfValue.contains(tokens.getCurrTKType())){
            String errorTK = tokens.getInputValueByNum(tokens.getCurrNum());
            throw new CmdException.grammarErrorException(errorTK);
        }
    }

    public Boolean checkValidAttribute(String type){
        return type.equals("ID");
    }

    public Boolean checkValidValue(String type){
        ArrayList<String> elementsOfValue =
                new ArrayList<>(Arrays.asList("INT", "FLOAT", "BOOL", "STR", "NULL"));
        return elementsOfValue.contains(type);
    }

    /* store Cmd information */
    public void storeConditionList(int num) throws CmdException {
        tokens.setCurrTokenNum(num+1);
        if(tokens.getCurrTKType().equals("LParen")){
            storeConditionStruct();
        } else if(tokens.getCurrTKType().equals("ID")){
            storeBasicCondition();
        }
    }

    // (score==100) AND (score==80)
    // ((score==100) AND (score==90)) AND (score==80)
    public void storeConditionStruct() throws CmdException {
        Condition relationSymbol = null;
        storeBracketCondition();
        if(tokens.getCurrTKType().equals("AND") || tokens.getCurrTKType().equals("OR")){
            int cnt = tokens.getCurrNum();
            relationSymbol = new Condition(tokens, cnt);
        }
        tokens.nextTokenByNum();
        storeBracketCondition();
        conditions.add(relationSymbol);
    }

    public void storeBracketCondition() throws CmdException {
        tokens.nextTokenByNum();
        if(tokens.getCurrTKType().equals("LParen")){
            storeConditionStruct();
            tokens.nextTokenByNum();
        } else {
//            storeBracketCondition();
            storeBasicCondition();
            tokens.nextTokenByNum();
        }
    }

    // score==100)
//    public void storeBracketCondition() throws CmdException {
//        storeBasicCondition();
//        tokens.nextTokenByNum();
//    }

    // score==100
    public void storeBasicCondition() throws CmdException {
        if(checkValidAttribute(tokens.getCurrTKType())){
            int cnt = tokens.getCurrNum();
            conditions.add(new Condition(tokens, cnt));
            tokens.nextTokenByNum();
        }
    }

    public void storeConditions() throws CmdException{
        int cnt =0;
        for(Token element: tokens.getList()){
            if("WHERE".equals(element.getTypeName())){
                tokens.nextTokenByNum();
                storeConditionList(cnt);
            }
            cnt++;
        }
    }

    public void storeTableNameByNum(int num){
        tableNames.add(tokens.getInputValueByNum(num));
    }

    public void storeValueList(){
        for(Token element: tokens.getList()){
            if(checkValidValue(element.getTypeName())){
                values.add(element);
            }
        }
    }

    public void storeAttributeList(){
        boolean turningOnToAction = false;
        for(Token element: tokens.getList()){
            if("LParen".equals(element.getTypeName())){
                turningOnToAction = true;
            }
            if(turningOnToAction && checkValidAttribute(element.getTypeName())){
                colNames.add(element.getInput());
            }
        }
    }

    /* get attributes */
    public ArrayList<Condition> getConditions() {
        return conditions;
    }

    public ArrayList<Token> getValues() {
        return values;
    }

    public ArrayList<String> getColNames() {
        return colNames;
    }

    public ArrayList<Token> getValueList() {
        return values;
    }

    public ArrayList<String> getTBNames() {
        return tableNames;
    }

    public String getDBName() {
        return DBName;
    }

    public String getCmdType() {
        return cmdType;
    }

    public String getOutputResult() {
        return outputResult;
    }

    public Boolean getNormalRun() {
        return normalRun;
    }

    public String getAction() {
        return action;
    }
}
