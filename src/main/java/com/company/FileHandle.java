package com.company;

import com.company.DBcmd.*;
import com.company.Exception.IOException;
import com.company.Exception.TableException;

import java.io.*;
import java.nio.file.Paths;
import java.util.*;

public class FileHandle {

    private final File homeDirectory;
    private DataStore dbStore;
    private String dbNameInOperating;
    private final HashMap<String, Integer> idNumberOfTables;

    public FileHandle(File Directory) {
        homeDirectory = Directory;
        dbStore = new DataStore();
        idNumberOfTables = new HashMap<>();
    }

    public DataStore getDbStore(){ return dbStore; }

    public File transferStringToFile(String formerDir, String LatterDir){
        return Paths.get(formerDir+File.separator+LatterDir).toAbsolutePath().toFile();
    }

    public File getFileDirectByFolder(String targetedFolder){
        String homeString = homeDirectory.toString();
        return transferStringToFile(homeString, targetedFolder);
    }

    public void checkAssignedDatabase() throws IOException {
        if(dbNameInOperating==null){
            throw new IOException.noDatabaseAssignedException();
        }
    }

    public File getFileDirectoryByFile(String targetedFile) throws IOException {
        checkAssignedDatabase();
        String fullNameOfFile = targetedFile+".tab";
        File file1 = transferStringToFile(homeDirectory.toString(), dbNameInOperating);
        return transferStringToFile(file1.toString(), fullNameOfFile);
    }

    public void readHomeDirectory() {
        try{
            dbStore = readFolders(homeDirectory);
        } catch (Exception e){
            System.err.println(e.getMessage());
        }
    }

    public Boolean checkFolderExists(String targetedFolder){
        File newFolder = getFileDirectByFolder(targetedFolder);
        return newFolder.exists();
    }

    public void createNewFolder(String createdFolder) throws IOException {
        File newFolder = getFileDirectByFolder(createdFolder);
        if(!newFolder.mkdir()){
            throw new IOException.failedToCreateFolderException(createdFolder);
        }
    }

    public void deleteExistingFolder(String deletedFolder) throws IOException {
        File tgtFolder = getFileDirectByFolder(deletedFolder);
        if(!tgtFolder.delete()){
            throw new IOException.failedToDeleteFolderException(deletedFolder);
        }
    }

    public DataStore readFolders(File homeDirectory) throws java.io.IOException {
        DataStore newStore = new DataStore();
        File[] folders = homeDirectory.listFiles();
        assert folders != null;
        for(File folder: folders){
            if(folder.isDirectory()){
                String folderName = getFolderName(folder);
                newStore.addDB(folderName, readFiles(folder));
            }
        }
        return newStore;
    }

    public String getFolderName(File folder){
        return folder.getName();
    }

    public Boolean checkFileExists(String targetedFile) throws IOException {
        File newFile = getFileDirectoryByFile(targetedFile);
        return newFile.exists();
    }

    public void createNewFile(String createdFile) throws IOException, java.io.IOException {
        File newFile = getFileDirectoryByFile(createdFile);
        if(!newFile.createNewFile()){
            throw new IOException.failedToCreateFileException(createdFile);
        }
    }

    public void deleteExistingFile(String deletedFile) throws IOException {
        File tgtFile = getFileDirectoryByFile(deletedFile);
        if(!tgtFile.delete()){
            throw new IOException.failedToDeleteFolderException(deletedFile);
        }
    }

    public void storeInUseDBInMemory() throws java.io.IOException {
        dbStore.getDbMap().remove(dbNameInOperating);
        File folderDirectory = getFileDirectByFolder(dbNameInOperating);
        dbStore.getDbMap().put(dbNameInOperating, readFiles(folderDirectory));
    }

    public DataBase readFiles(File folderDirectory) throws java.io.IOException {
        String folderName = getFolderName(folderDirectory);
        FileFilter filter = f -> f.getName().endsWith("tab");
        File[] files = folderDirectory.listFiles(filter);
        ArrayList<DataTable> tableList = new ArrayList<>();
        for(File currFile: Objects.requireNonNull(files)){
            tableList.add(readLines(currFile));
        }
        return new DataBase(folderName, tableList);
    }

    public String getFileName(File file){
        String fullName = file.getName() ;
        String[] names = fullName.split(".tab");
        return names[0];
    }

    public DataTable readLines(File fileDirectory) throws java.io.IOException {
        String fileName = getFileName(fileDirectory);
        DataTable newTable;
        FileReader reader = new FileReader(fileDirectory);
        BufferedReader buffReader = new BufferedReader(reader);
        String tmpLine = buffReader.readLine();
        ArrayList<DataCol> newIndexList = readHeadline(tmpLine);
        ArrayList<DataRow> newContents = readLinesOfContents(buffReader);
        buffReader.close();
        newTable = new DataTable(fileName, newIndexList, newContents);
         return newTable;
    }

    public ArrayList<DataRow> readLinesOfContents(BufferedReader buffReader)
            throws java.io.IOException {
        ArrayList<DataRow> newContents = new ArrayList<>();
        int cnt=0;
        String tmpLine;
        do{ tmpLine = buffReader.readLine();
            newContents = readLines(tmpLine, newContents, cnt);
            cnt++;
        }while(tmpLine!=null && tmpLine.length()!=0);

        return newContents;
    }

    public ArrayList<DataRow> readLines(String tmpLine, ArrayList<DataRow> newContents, int cnt){
        if(tmpLine!=null && tmpLine.length()!=0){
            newContents.add(readOneLine(cnt, tmpLine));
        }
        return newContents;
    }

    public ArrayList<DataCol> readHeadline(String headLine) {
        ArrayList<DataCol> newIndexList = new ArrayList<>();
        if(headLine != null){
            String[] headings = headLine.split("\t");
            for (String heading : headings) {
                newIndexList.add(new DataCol(heading, DataCol.KeyType.NORMAL));
            }
        }
        return newIndexList;
    }

    public DataRow readOneLine(int num, String line) {
        String[] contents = line.split("\t");
        ArrayList<String> valueList = new ArrayList<>(Arrays.asList(contents));
        return new DataRow(num, valueList);
    }

    public void writeHeadLine(ArrayList<String> colNames, String tableName)
            throws IOException, java.io.IOException {
        // already check file exists
        File fileToOpen = getFileDirectoryByFile(tableName);
        FileWriter writer = new FileWriter(fileToOpen);
        writer.write(transferColsToString(colNames));
        writer.flush();
        writer.close();
    }

    public String transferColsToString(ArrayList<String> colNames){
        return "id\t"+String.join("\t", colNames)+"\n";
    }

    public void writeOneLine(ArrayList<String> outputValues, String tableName)
            throws IOException, java.io.IOException, TableException {
        // already check file exists
        File fileToOpen = getFileDirectoryByFile(tableName);
        FileWriter writer1 = new FileWriter(fileToOpen,true);
        BufferedWriter writer = new BufferedWriter(writer1);
        writer.append(transferValuesToString(outputValues, tableName));
        writer.flush();
        writer.close();
    }

    public void writeWholeFile(String input, String tableName)
            throws IOException, java.io.IOException, TableException {
        // already check file exists
        File fileToOpen = getFileDirectoryByFile(tableName);
        FileWriter writer1 = new FileWriter(fileToOpen,true);
        BufferedWriter writer = new BufferedWriter(writer1);
        writer.write(input);
        writer.flush();
        writer.close();
    }

    public void selectColsInTable(ArrayList<String> colNames, String tableName)
            throws IOException {
        // already check table in folder && already store in dbStore
        if(colNames.size()!=0){
            DataTable buff = dbStore.getDBByName(dbNameInOperating).getTBByName(tableName);
            ArrayList<String> indexes = buff.getColNames();
            for(String index: indexes){
                if(!colNames.contains(index)){
                    int num = buff.getNumByIndexName(index);
                    buff.dropElementByColNum(num);
                }
            }
            dbStore.getDBByName(dbNameInOperating).deleteTable(tableName);
            dbStore.getDBByName(dbNameInOperating).addTable(buff);
        }
    }

    public void addColsInTable(ArrayList<String> colNames, String tableName) throws IOException {
        // already check table in folder && already store in dbStore
        DataTable buff = dbStore.getDBByName(dbNameInOperating).getTBByName(tableName);
        for(String index: colNames){
            checkSameIndex(index, buff);
            buff.addIndex(new DataCol(index, DataCol.KeyType.NORMAL));
        }
        buff.fillInAllBlankRows();
        dbStore.getDBByName(dbNameInOperating).deleteTable(tableName);
        dbStore.getDBByName(dbNameInOperating).addTable(buff);
    }

    public void dropColsInTable(ArrayList<String> colNames, String tableName) throws IOException {
        // already check table in folder && already store in dbStore
        DataTable buff = dbStore.getDBByName(dbNameInOperating).getTBByName(tableName);
        for(String index: colNames){
            checkNoSameIndex(index, buff);
            checkNotKeyIndexId(index);
            int colNum = buff.getColNames().indexOf(index);
            buff.dropElementByColNum(colNum);
        }
        dbStore.getDBByName(dbNameInOperating).deleteTable(tableName);
        dbStore.getDBByName(dbNameInOperating).addTable(buff);
    }

    public void checkSameIndex(String index, DataTable dbTable)
            throws IOException {
        String TBName = dbTable.getName();
        if(dbTable.getColNames().contains(index)){
            throw new IOException.sameInputIndexInTableException(index,TBName);
        }
    }

    public void checkNoSameIndex(String index, DataTable dbTable)
            throws IOException {
        String TBName = dbTable.getName();
        if(!dbTable.getColNames().contains(index)){
            throw new IOException.absentInputIndexInTableException(index,TBName);
        }
    }

    public void checkNotKeyIndexId(String index) throws IOException {
        if(index.equals("id")){
            throw new IOException.keyIndexIdException();
        }
    }

    public void joinTwoTables(String TB1, String TB2, String col1, String col2)
            throws IOException {
        // already check table in folder
        // already store in dbStore
        DataTable table1 = dbStore.getDBByName(dbNameInOperating).getTBByName(TB1);
        DataTable table2 = dbStore.getDBByName(dbNameInOperating).getTBByName(TB2);
        Integer num1 = table1.getNumByIndexName(col1);
        Integer num2 = table2.getNumByIndexName(col2);
        Integer num3 = table1.getIndexNum();
        joinRowsAndColumns(table1, table2, num1, num2);
        combineCols(table1,table2);
        table1.dropElementByColNum(num3+num2);
        if(num1!=0){
            table1.dropElementByColNum(num1);
        }
    }

    public void combineCols(DataTable TB1, DataTable TB2){
        int numOfCol1 = TB1.getIndexNum();
        int numOfCol2 = TB2.getIndexNum();
        TB1.getCols().addAll(TB2.getCols());
        TB1.setNumOfCol(numOfCol1+numOfCol2);
        TB1.resetId();
    }

    public void joinRowsAndColumns(DataTable TB1, DataTable TB2,
                                      Integer num1, Integer num2){
        int numOfRow1 = TB1.getRowNum();
        for(int h1=0; h1<numOfRow1; ++h1){
            ArrayList<String> TB1contents = TB1.getRows().get(h1).getContents();
            joinAnotherTable(TB1contents, TB2, num1, num2);
        }
    }

    public void joinAnotherTable(ArrayList<String> TB1contents,
                                    DataTable TB2, Integer num1, Integer num2){
        int numOfRow2 = TB2.getRowNum();
        for(int h2=0; h2<numOfRow2; ++h2){
            ArrayList<String> TB2contents = TB2.getRows().get(h2).getContents();
            joinRows(TB1contents, TB2contents, num1, num2);
        }
    }

    public boolean joinRows(ArrayList<String> TB1contents,
                            ArrayList<String> TB2contents, Integer num1, Integer num2){
        if(TB1contents.get(num1).equals(TB2contents.get(num2))) {
            TB1contents.addAll(TB2contents);
            return true;
        }
        return false;
    }

    public void updateWithConditions(
            ArrayList<String> colNames, ArrayList<String> valueList,
            ArrayList<Condition> conditions, String tableName) throws TableException, IOException {
        DataTable buff = dbStore.getDBByName(dbNameInOperating).getTBByName(tableName);
        ArrayList<Integer> selectedList = selectRowsByConditions(conditions, tableName);
        for(int i=0; i<colNames.size(); ++i){
            String index = colNames.get(i);
            String value = valueList.get(i);
            for(Integer rowNum: selectedList){
                updateValue(rowNum, index, value, buff);
            }
        }

        dbStore.getDBByName(dbNameInOperating).deleteTable(tableName);
        dbStore.getDBByName(dbNameInOperating).addTable(buff);

    }

    public void updateValue(Integer rowNum, String index,
                            String value, DataTable buff) throws IOException {
        if(buff.getColNames().contains(index)){
            int colNum = buff.getNumByIndexName(index);
            buff.getRows().get(rowNum).getContents().set(colNum,value);
        }
    }

    public void storeRowsByConditions(ArrayList<Condition> conditions,
                                      String tableName) throws TableException {
        // already check table in folder
        // already store in dbStore
        DataTable buff = dbStore.getDBByName(dbNameInOperating).getTBByName(tableName);
        ArrayList<Integer> selectedList = selectRowsByConditions(conditions, tableName);
        buff.setSelectedRows(selectedList);
        buff.updateSelectedRows();
        dbStore.getDBByName(dbNameInOperating).deleteTable(tableName);
        dbStore.getDBByName(dbNameInOperating).addTable(buff);
    }

    public void dropRowsByConditions(ArrayList<Condition> conditions, String tableName) throws TableException {
        // already check table in folder
        // already store in dbStore
        DataTable buff = dbStore.getDBByName(dbNameInOperating).getTBByName(tableName);
        ArrayList<Integer> selectedList = selectRowsByConditions(conditions, tableName);
        selectedList = reverseList(selectedList, buff.getRowNum());
        buff.setSelectedRows(selectedList);
        buff.updateSelectedRows();
        dbStore.getDBByName(dbNameInOperating).deleteTable(tableName);
        dbStore.getDBByName(dbNameInOperating).addTable(buff);
    }

    public ArrayList<Integer> reverseList(ArrayList<Integer> table1, Integer numOfCols) {
        ArrayList<Integer> table = new ArrayList<>();
        for(Integer i=0; i<numOfCols; ++i){
            if(!table1.contains(i)){
                table.add(i);
            }
        }
        Collections.sort(table);
        return table;
    }

    public ArrayList<Integer> selectRowsByConditions
            (ArrayList<Condition> conditions, String tableName) throws TableException {
        // already check table in folder
        // already store in dbStore
        Stack<ArrayList<Integer>> stack = new Stack<>();
        for(Condition con: conditions){
            DataTable buff2 = dbStore.getDBByName(dbNameInOperating).getTBByName(tableName);
            switch (con.getType().name()) {
                case "AND" -> {
                    ArrayList<Integer> table2 = stack.pop();
                    ArrayList<Integer> table1 = stack.pop();
                    ArrayList<Integer> list2 = implementAND(table1, table2);
                    stack.push(list2);
                }
                case "OR" -> {
                    ArrayList<Integer> table2 = stack.pop();
                    ArrayList<Integer> table1 = stack.pop();
                    ArrayList<Integer> list2 = implementOR(table1, table2);
                    stack.push(list2);
                }
                default -> {
                    buff2.selectByBasicCondition(con);
                    ArrayList<Integer> list = buff2.getSelectedRows();
                    stack.push(list);
                }
            }
        }
        return stack.pop();
    }

    public ArrayList<Integer> implementAND(ArrayList<Integer> table1,
                                           ArrayList<Integer> table2) {
        ArrayList<Integer> table = new ArrayList<>();
        for(Integer num: table1){
            if(table2.contains(num)){
                table.add(num);
            }
        }
        Collections.sort(table);
        return table;
    }

    public ArrayList<Integer> implementOR(ArrayList<Integer> table1,
                                          ArrayList<Integer> table2) {
        for(Integer num: table2){
            if(!table1.contains(num)){
                table1.add(num);
            }
        }
        Collections.sort(table1);
        return table1;
    }

    public String outputTableToString(String tableName){
        return transferColsToString(tableName)+transferRowsToString(tableName);
    }

    public String transferColsToString(String tableName){
        DataTable buff = dbStore.getDBByName(dbNameInOperating).getTBByName(tableName);
        return buff.printCols();
    }

    public String transferRowsToString(String tableName){
        DataTable buff = dbStore.getDBByName(dbNameInOperating).getTBByName(tableName);
        return buff.printRows();
    }

    public String transferValuesToString(ArrayList<String> values, String tableName)
            throws IOException, TableException, java.io.IOException {
        updateMaxIdNumber(tableName);
        String fullName = dbNameInOperating+"_"+tableName;
        int NumOfId = idNumberOfTables.get(fullName)+1;
        return NumOfId+"\t"+String.join("\t", values)+"\n";
    }

    public void updateMaxIdNumber(String tableName)
            throws IOException, TableException, java.io.IOException {
        checkFileExists(tableName);
        String fullName = dbNameInOperating+"_"+tableName;
        if(!idNumberOfTables.containsKey(fullName)){
            idNumberOfTables.put(fullName, 0);
        }
        storeInUseDBInMemory();
        storeMaxIdNumber(tableName);
//        return idNumberOfTables.get(tableName);
    }

    public void storeMaxIdNumber(String tableName) throws TableException {
        // already check hashmap of tableName exists
        String fullName = dbNameInOperating+"_"+tableName;
        int maxId = idNumberOfTables.get(fullName);
        maxId = Math.max(maxId, getMaxIdNumber(tableName));
        idNumberOfTables.put(fullName, maxId);
    }

    public int getMaxIdNumber(String tableName) throws TableException {
        // already store table to memory
        return dbStore.getDBByName(dbNameInOperating).getTBByName(tableName).findMaxId();
    }

    public void checkSameNumOfCols(int LengthOfInput, String tableName)
            throws IOException, java.io.IOException {
        storeInUseDBInMemory();
        int LengthOfCols = dbStore.getDBByName(dbNameInOperating).getTBByName(tableName).getIndexNum();
        checkLessNumOfCols(LengthOfInput, LengthOfCols, tableName);
        checkMoreNumOfCols(LengthOfInput, LengthOfCols, tableName);
    }

    public void checkLessNumOfCols(int LengthOfInput, int LengthOfCols, String tableName)
            throws IOException {
        if(LengthOfInput<LengthOfCols-1){
            throw new IOException.lessNumOfColsException(tableName);
        }
    }

    public void checkMoreNumOfCols(int LengthOfInput, int LengthOfCols, String tableName)
            throws IOException {
        if(LengthOfInput>LengthOfCols-1){
            throw new IOException.moreNumOfColsException(tableName);
        }
    }

    public String getDbNameInOperating() {
        return dbNameInOperating;
    }

    public void setDbNameInOperating(String dbName) {
        dbNameInOperating = dbName;
    }
}
