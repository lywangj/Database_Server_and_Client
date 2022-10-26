package com.company;

public class DataCol {

    private String colName;
    private KeyType key;

    enum KeyType {
        NORMAL,
        PRIMARY,
        FOREIGN
    }

    public DataCol(String name, KeyType type){
        colName = name;
        key = type;
    }

    public void setName(String name) {
        colName = name;
    }

    public String getName() {
        return colName;
    }

    public void setKeyType(KeyType type) {
        key = type;
    }

    public KeyType getKeyType() {
        return key;
    }

}
