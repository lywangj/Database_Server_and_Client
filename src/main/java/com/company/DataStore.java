package com.company;

import java.util.*;

public class DataStore {

    private static Hashtable<String, DataBase> dbMap;

    public DataStore(){
        dbMap = new Hashtable<>();
    }

    public Hashtable<String, DataBase> getDbMap(){
        return dbMap;
    }

    public DataBase getDBByName(String dbName){
        return dbMap.get(dbName);
    }

    public void addDB(String dbName, DataBase newDB){
        dbMap.put(dbName, newDB);
    }

    public void deleteDB(String dbName){
        dbMap.remove(dbName, getDBByName(dbName));
    }





}
