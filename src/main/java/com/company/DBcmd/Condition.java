package com.company.DBcmd;

import com.company.Exception.CmdException;
import com.company.Tokenizer;

import java.util.ArrayList;
import java.util.Arrays;


public class Condition {

    private final Tokenizer tokens;
    private Type type;            // INT
    private String colName;       // score
    private String operator;      // >
    private String inputValue;    // "50"
    private Integer INTValue;     // 50
    private Float floatValue;
    private Boolean boolValue;
    private String strValue;

    public enum Type{
        AND, OR, INT, FLOAT, BOOL, STR, NULL
    }

    public Condition(Tokenizer TKs, int num) throws CmdException {
        tokens = TKs;
        tokens.setCurrTokenNum(num);
        String currType = tokens.getCurrTKType();
        if("ID".equals(currType)){
             storeCondition();
             checkValidCondition();
        } else if(currType.equals("AND")){
             storeRelation("AND");
        } else if(currType.equals("OR")) {
            storeRelation("OR");
        }
    }

//    AND, OR, INT, FLOAT, BOOL, STR, NULL
    public void checkValidCondition() throws CmdException {

        switch (type){
            case INT, FLOAT -> checkValidOperatorForINT();
            case BOOL, NULL -> checkValidOperatorForBOOL();
            case STR -> checkValidOperatorForSTR();
        }
    }

    public void checkValidOperatorForINT() throws CmdException {
        if(operator.equals("LIKE")){
            throw new CmdException.invalidConditionException(printout());
        }
    }

    public void checkValidOperatorForBOOL() throws CmdException {
        ArrayList<String> operatorsOfBOOL =
                new ArrayList<>(Arrays.asList("%EQ%", "%NE%"));
        if(!operatorsOfBOOL.contains(operator)){
            throw new CmdException.invalidConditionException(printout());
        }
    }

    public void checkValidOperatorForSTR() throws CmdException {
        ArrayList<String> operatorsOfSTR =
                new ArrayList<>(Arrays.asList("%EQ%", "%NE%", "LIKE"));
        if(!operatorsOfSTR.contains(operator)){
            throw new CmdException.invalidConditionException(printout());
        }
    }

    public void storeCondition(){
        colName = tokens.getCurrTKInputValue();
        tokens.nextTokenByNum();
        operator = tokens.getCurrTKInputValue().toUpperCase();
        tokens.nextTokenByNum();
        String valueType = tokens.getCurrTKType();
        switch (valueType) {
            case "INT" -> {
                type = Type.INT;
                INTValue = storeINTNumber();
                inputValue = tokens.getCurrTKInputValue();
            }
            case "FLOAT" -> {
                type = Type.FLOAT;
                floatValue = storeFLOATNumber();
                inputValue = tokens.getCurrTKInputValue();
            }
            case "BOOL" -> {
                type = Type.BOOL;
                boolValue = storeBool();
                inputValue = tokens.getCurrTKInputValue();
            }
            case "STR" -> {
                type = Type.STR;
                strValue = storeSTR();
                inputValue = tokens.getCurrTKInputValue();
            }
            case "NULL" -> {
                type = Type.NULL;
                INTValue = null;
                floatValue = null;
                boolValue = null;
                strValue = null;
                inputValue = tokens.getCurrTKInputValue();
            }
        }
    }


    public Integer storeINTNumber(){
        String word = tokens.getCurrTKValue();
        return Integer.valueOf(word);
    }

    public Float storeFLOATNumber(){
        // '-', '+'
        String word = tokens.getCurrTKValue();
        return Float.parseFloat(word);
    }

    public Boolean storeBool(){
        return tokens.getCurrTKValue().equals("TRUE");
    }

    public String storeSTR(){
        return tokens.getCurrTKValue();
    }

    public void storeRelation(String s){
        if(s.equals("AND")){
            type = Type.AND;
        } else {
            type = Type.OR;
        }
    }

    public Type getType() {
        return type;
    }

    public String getColName() {
        return colName;
    }

    public String getOperator() {
        return operator;
    }

    public String getInputValue() {
        return inputValue;
    }

    public Integer getINTValue() {
        return INTValue;
    }

    public Float getFloatValue() {
        return floatValue;
    }

    public Boolean getBoolValue() {
        return boolValue;
    }

    public String getStrValue() {
        return strValue;
    }

    public String printout() {
        switch (type){
            case AND -> {
                return "AND";
            }
            case OR -> {
                return "OR";
            }
            default -> {
                return colName+" "+getOriginalOP()+" ("+type.name()+") "+inputValue;
            }
        }

    }

    public String getOriginalOP() {
        return switch (operator) {
            case "%EQ%" -> "==";
            case "%LT%" -> ">=";
            case "%ST%" -> "<=";
            case "%NE%" -> "!=";
            default -> operator;
        };
    }

}
