package com.company;

import java.util.ArrayList;


public class DataBase {
    private static String dbName;
    private static ArrayList<DataTable> tables;

    public DataBase(String name, ArrayList<DataTable> files){
        dbName = name;
        tables = files;
    }

    public String getName() { return dbName; }

    public void setName(String name) {
        dbName = name;
    }

    public DataTable getTBByName(String tgtName) {
        for(DataTable table: tables){
            if(table.getName().equals(tgtName)) {
                return table;
            }
        }
        return null;
    }

    public DataTable getTBByNum(Integer num) { return tables.get(num); }

    public String getTBNameByNum(Integer num) { return tables.get(num).getName(); }

    public void addTable(DataTable dbTable){
        tables.add(dbTable);
    }

    public void deleteTable(String tableName){
        DataTable tgtTable = getTBByName(tableName);
        tables.remove(tgtTable);
    }


}
