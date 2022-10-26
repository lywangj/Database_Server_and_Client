package com.company;

import java.util.ArrayList;

public class DataRow {

    private Integer rowNum;
    private ArrayList<String> contents;

    public DataRow(Integer num, ArrayList<String> valueList){
        rowNum = num;
        contents = valueList;
    }

    public void dropElementByColNum(int colNum) {
        contents.remove(colNum);
    }

    public Integer getNum() {
        return rowNum;
    }

    public void setNum(Integer num) {
        rowNum = num;
    }

    public String getValueByNum(Integer num) {
        return contents.get(num);
    }

    public void addElement(String element) {
        getContents().add(element);
    }

    public ArrayList<String> getContents() {
        return contents;
    }

    public String printContents() {
        return String.join("\t", contents)+"\n";
    }


}
