package com.company;

import java.util.ArrayList;
import java.util.Arrays;

public class Tokenizer {

    private String command;
    private ArrayList<Token> tokens;
    private Integer currTokenNum;

    public Tokenizer(String comingCommand){
        command = comingCommand;
        tokens = new ArrayList<>();
        convertToTokens();
        currTokenNum =0;
    }

    private void convertToTokens(){
        ArrayList<String> strings = separateToList();
        tokens = new ArrayList<>();
        for(String wrd : strings){
            if(!wrd.equals("")){
                Token newTK = new Token(wrd);
                tokens.add(newTK);
            }
        }
    }

    public ArrayList<String> separateToList(){
        splitPunctuations();
        splitOperators();
        return new ArrayList<>(Arrays.asList(command.split(" ")));
    }

    public void splitPunctuations(){
        command = command.replaceAll(";"," ; ")
                .replaceAll("\\(", " ( ")
                .replaceAll("\\)", " ) ")
                .replaceAll(",", " , ");
    }

    public void splitOperators(){
        command = command.replaceAll("==", "  %EQ% ")
                .replaceAll(">=", "  %LT% ")
                .replaceAll("<=", "  %ST% ")
                .replaceAll("!=", " %NE% ")
                .replaceAll("=", " = ")
                .replaceAll(">", " > ")
                .replaceAll("<", " < ");
    }

    public ArrayList<Token> getList() { return tokens; }

    public ArrayList<String> getInputList(){
        ArrayList<String> strings = new ArrayList<>();
        for(Token TK: tokens){
            strings.add(TK.getInput());
        }
        return strings;
    }

    public ArrayList<String> getValueList(){
        ArrayList<String> strings = new ArrayList<>();
        for(Token TK: tokens){
            strings.add(TK.getValue());
        }
        return strings;
    }

    public ArrayList<Token.KeyType> getTypeList(){
        ArrayList<Token.KeyType> types = new ArrayList<>();
        for(Token TK: tokens){
            types.add(TK.getType());
        }
        return types;
    }

    public Integer getLength(){
        return tokens.size();
    }

    public Integer getCurrNum(){
        return currTokenNum;
    }

    public String getCurrTKInputValue(){
        return tokens.get(currTokenNum).getInput();
    }

    public String getCurrTKValue(){
        return tokens.get(currTokenNum).getValue();
    }

    public String getCurrTKType(){
        return tokens.get(currTokenNum).getType().name();
    }

    public String getInputValueByNum(Integer num){ return tokens.get(num).getInput(); }

    public String getValueByNum(Integer num){
        return tokens.get(num).getValue();
    }

    public String getTypeByNum(Integer num){
        return tokens.get(num).getType().name();
    }

    public Token nextToken(){
        currTokenNum++;
        if(currTokenNum>=tokens.size()){ return null;}
        return tokens.get(currTokenNum);
    }

    public Boolean nextTokenByNum(){
        currTokenNum++;
        return currTokenNum < tokens.size();
    }

    public void setCurrTokenNum(int num){ currTokenNum = num; }

}
