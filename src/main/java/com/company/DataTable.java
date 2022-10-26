package com.company;

import com.company.Exception.*;
import com.company.DBcmd.*;

import java.util.ArrayList;
import java.util.Locale;

import static java.lang.Float.*;

public class DataTable {

    private String tableName;
    private ArrayList<DataCol> cols;
    private ArrayList<DataRow> rows;
    private Integer NumOfCol;
    private Integer NumOfRow;
    private Integer maxId;
    private ArrayList<Integer> selectedRows;

    public DataTable(String name, ArrayList<DataCol> indexes, ArrayList<DataRow> contents){
        tableName = name;
        cols = indexes;
        rows = contents;
        NumOfCol = cols.size();
        NumOfRow = rows.size();
    }

    public void resetId(){
        Integer cnt =1 ;
        for(DataRow row: rows){
            row.getContents().set(0, cnt.toString());
            cnt++;
        }
    }

    public void selectByBasicCondition(Condition condition) throws TableException {

        selectedRows = new ArrayList<>();
//        String operator = condition.getOperator();
        int colNum = getColNames().indexOf(condition.getColName());
        if(colNum!=-1){
            switch(condition.getType()) {
                case INT -> {
                    int value = condition.getINTValue();
                    selectWithOperators(colNum, value, condition);
                }
                case FLOAT -> {
                    Float value = condition.getFloatValue();
                    selectWithOperators(colNum, value, condition);
                }
                case BOOL -> {
                    Boolean value = condition.getBoolValue();
                    selectWithOperators(colNum, value, condition);
                }
                case STR -> {
                    String value = condition.getStrValue();
                    selectWithOperators(colNum, value, condition);
                }
                case NULL -> {
                    String value = "NULL";
                    selectWithOperatorsForNull(colNum, value, condition);
                }
            }
        }
    }

    public void updateSelectedRows() {
        // build table by selectedRow
        ArrayList<DataRow> newRows = new ArrayList<>();
        for(Integer num : selectedRows){
            newRows.add(rows.get(num));
        }
        rows = newRows;
        NumOfRow = newRows.size();
    }


    //    "==", ">", "<", ">=", "<=", "!=", "LIKE",
//            "%EQ%", "%LT%", "%ST%", "%NE%" -> Token.KeyType.OP;
    public void selectWithOperatorsForNull(Integer colNum, String value, Condition condition) throws TableException {
        String operator = condition.getOperator();
        switch(operator) {
            case "==","%EQ%" -> selectByEqualOPForNull(colNum, value);
            case "!=","%NE%" -> selectByNotEqualOPForNull(colNum, value);
            // no Like
        }
    }

    public String transferToStringForNull(int colNum, int rowNum) throws TableException {
        String word = getValFrom2D(rowNum,colNum).toUpperCase();
//        get(rowNum).getContents().get(colNum);
//        if(!word.matches("[^0-9]+")){
//            throw new TableException.invalidFormatOfException(word);
//        }
        return word;
    }

    public void selectByNotEqualOPForNull(Integer colNum, String value) throws TableException {
        for(int i=0; i<NumOfRow ; ++i ){
            String colValue = transferToStringForNull(colNum, i);
            if(!colValue.equals(value)){
                selectedRows.add(i);
            }
        }
    }

    public void selectByEqualOPForNull(Integer colNum, String value) throws TableException {
        for(int i=0; i<NumOfRow ; ++i ){
            String colValue = transferToStringForNull(colNum, i);
            if(colValue.equals(value)){
                selectedRows.add(i);
            }
        }
    }


    //    "==", ">", "<", ">=", "<=", "!=", "LIKE",
//            "%EQ%", "%LT%", "%ST%", "%NE%" -> Token.KeyType.OP;
    public void selectWithOperators(Integer colNum, String value, Condition condition) throws TableException {
        String operator = condition.getOperator();
        switch(operator) {
            case "==","%EQ%" -> selectByEqualOP(colNum, value);
            case "!=","%NE%" -> selectByNotEqualOP(colNum, value);
            case "LIKE" -> selectByLikeOP(colNum, value);
        }
    }

    public String transferToString(int colNum, int rowNum) {
        String word = getValFrom2D(rowNum,colNum);
        if(word.equalsIgnoreCase("NULL")){
            return null;
        }
//        get(rowNum).getContents().get(colNum);
//        if(!word.matches("[^0-9]+")){
//            throw new TableException.invalidFormatOfException(word);
//        }
        return word;
    }

    public void selectByLikeOP(Integer colNum, String value) throws TableException {
        for(int i=0; i<NumOfRow ; ++i ){
            String colValue = transferToString(colNum, i);
            if(colValue!=null && colValue.contains(value)){    // case sensitive
                selectedRows.add(i);
            }
        }
    }

    public void selectByNotEqualOP(Integer colNum, String value) throws TableException {
        for(int i=0; i<NumOfRow ; ++i ){
            String colValue = transferToString(colNum, i);
            if(colValue!=null && !colValue.equals(value)){
                selectedRows.add(i);
            }
        }
    }

    public void selectByEqualOP(Integer colNum, String value) throws TableException {
        for(int i=0; i<NumOfRow ; ++i ){
            String colValue = transferToString(colNum, i);
            if(colValue!=null && colValue.equals(value)){
                selectedRows.add(i);
            }
        }
    }


    //    "==", ">", "<", ">=", "<=", "!=", "LIKE",
//            "%EQ%", "%LT%", "%ST%", "%NE%" -> Token.KeyType.OP;
    public void selectWithOperators(Integer colNum, Boolean value, Condition condition) throws TableException {
        String operator = condition.getOperator();
        switch(operator) {
            case "==","%EQ%" -> selectByEqualOP(colNum, value);
            case "!=","%NE%" -> selectByNotEqualOP(colNum, value);
        }
    }

    public Boolean transferToBool(int colNum, int rowNum) throws TableException {
        String word = getValFrom2D(rowNum,colNum).toLowerCase();
//        get(rowNum).getContents().get(colNum);
        if(word.equalsIgnoreCase("NULL")){
            return null;
        }
//        if(!word.equals("true")&&!word.equals("false")){
//            throw new TableException.invalidFormatOfException(word);
//        }
        checkValidBool(word);

        return Boolean.parseBoolean(word.toUpperCase());
    }

    public void checkValidBool(String word) throws TableException {
        if(!word.equals("true")&&!word.equals("false")){
            throw new TableException.invalidFormatOfException(word);
        }
    }

    public void selectByNotEqualOP(Integer colNum, Boolean value) throws TableException {
        for(int i=0; i<NumOfRow ; ++i ){
            Boolean colValue = transferToBool(colNum, i);
            if(colValue!=null && !colValue.equals(value)){
                selectedRows.add(i);
            }
        }
    }

    public void selectByEqualOP(Integer colNum, Boolean value) throws TableException {
        for(int i=0; i<NumOfRow ; ++i ){
            Boolean colValue = transferToBool(colNum, i);
            if(colValue!=null && colValue.equals(value)){
                selectedRows.add(i);
            }
        }
    }


    //    "==", ">", "<", ">=", "<=", "!=", "LIKE",
//            "%EQ%", "%LT%", "%ST%", "%NE%" -> Token.KeyType.OP;
    public void selectWithOperators(Integer colNum, Float value, Condition condition) throws TableException {
        String operator = condition.getOperator();
        switch(operator) {
            case "==","%EQ%" -> selectByEqualOP(colNum, value);
            case "!=","%NE%" -> selectByNotEqualOP(colNum, value);
            case ">" -> selectByLargerOP(colNum, value);
            case "<" -> selectBySmallerOP(colNum, value);
            case ">=","%LT%" -> selectByLargerThanOP(colNum, value);
            case "<=","%ST%" -> selectBySmallerThanOP(colNum, value);
        }
    }

    public Float transferToFloat(int colNum, int rowNum) throws TableException {
        String word = getValFrom2D(rowNum,colNum);
        if(word.equalsIgnoreCase("NULL")){
            return null;
        }
        if(!isFloatNumeric(word)){
            return null;
        }
        return Float.parseFloat(word);
    }

    public static boolean isFloatNumeric(String string) {
        if(string == null || string.equals("")) {
            return false;
        }
        try {
            Float intValue = Float.parseFloat(string);
            return true;
        } catch (NumberFormatException e) {
        }
        return false;
    }

    public void selectBySmallerThanOP(Integer colNum, Float value) throws TableException {
        for(int i=0; i<NumOfRow ; ++i ){
            Float colValue = transferToFloat(colNum, i);
            if(colValue!=null && colValue<=value){
                selectedRows.add(i);
            }
        }
    }

    public void selectByLargerThanOP(Integer colNum, Float value) throws TableException {
        for(int i=0; i<NumOfRow ; ++i ){
            Float colValue = transferToFloat(colNum, i);
            if(colValue!=null && colValue>=value){
                selectedRows.add(i);
            }
        }
    }

    public void selectBySmallerOP(Integer colNum, Float value) throws TableException {
        for(int i=0; i<NumOfRow ; ++i ){
            Float colValue = transferToFloat(colNum, i);
            if(colValue!=null && colValue<value){
                selectedRows.add(i);
            }
        }
    }

    public void selectByLargerOP(Integer colNum, Float value) throws TableException {
        for(int i=0; i<NumOfRow ; ++i ){
            Float colValue = transferToFloat(colNum, i);
            if(colValue!=null && colValue>value){
                selectedRows.add(i);
            }
        }
    }

    public void selectByNotEqualOP(Integer colNum, Float value) throws TableException {
        for(int i=0; i<NumOfRow ; ++i ){
            Float colValue = transferToFloat(colNum, i);
            if(colValue!=null && !colValue.equals(value)){
                selectedRows.add(i);
            }
        }
    }

    public void selectByEqualOP(Integer colNum, Float value) throws TableException {
        for(int i=0; i<NumOfRow ; ++i ){
            Float colValue = transferToFloat(colNum, i);
            if(colValue!=null && colValue.equals(value)){
                selectedRows.add(i);
            }
        }
    }

    //    "==", ">", "<", ">=", "<=", "!=", "LIKE",
//            "%EQ%", "%LT%", "%ST%", "%NE%" -> Token.KeyType.OP;
    public void selectWithOperators(Integer colNum, Integer value, Condition condition) throws TableException {
        String operator = condition.getOperator();
        switch(operator) {
            case "==","%EQ%" -> selectByEqualOP(colNum, value);
            case "!=","%NE%" -> selectByNotEqualOP(colNum, value);
            case ">" -> selectByLargerOP(colNum, value);
            case "<" -> selectBySmallerOP(colNum, value);
            case ">=","%LT%" -> selectByLargerThanOP(colNum, value);
            case "<=","%ST%" -> selectBySmallerThanOP(colNum, value);
        }
    }

    public Integer transferToInteger(int colNum, int rowNum) throws TableException {
        String word = getValFrom2D(rowNum,colNum);
        if(word.equalsIgnoreCase("NULL")){
            return null;
        }
        if(!isNumeric(word)){
            return null;
        }
//        if(!word.matches("[0-9+-]+")){
//            return null;
//        }
        return Integer.parseInt(word);
    }

    public static boolean isNumeric(String string) {
        if(string == null || string.equals("")) {
            return false;
        }
        try {
            int intValue = Integer.parseInt(string);
            return true;
        } catch (NumberFormatException e) {
        }
        return false;
    }

    public void selectBySmallerThanOP(Integer colNum, Integer value) throws TableException {
        for(int i=0; i<NumOfRow ; ++i ){
            Integer colValue = transferToInteger(colNum, i);
            if(colValue!=null && colValue<=value){
                selectedRows.add(i);
            }
        }
    }

    public void selectByLargerThanOP(Integer colNum, Integer value) throws TableException {
        for(int i=0; i<NumOfRow ; ++i ){
            Integer colValue = transferToInteger(colNum, i);
            if(colValue!=null && colValue>=value){
                selectedRows.add(i);
            }
        }
    }

    public void selectBySmallerOP(Integer colNum, Integer value) throws TableException {
        for(int i=0; i<NumOfRow ; ++i ){
            Integer colValue = transferToInteger(colNum, i);
            if(colValue!=null && colValue<value){
                selectedRows.add(i);
            }
        }
    }

    public void selectByLargerOP(Integer colNum, Integer value) throws TableException {
        for(int i=0; i<NumOfRow ; ++i ){
            Integer colValue = transferToInteger(colNum, i);
            if(colValue!=null && colValue>value){
                selectedRows.add(i);
            }
        }
    }

    public void selectByNotEqualOP(Integer colNum, Integer value) throws TableException {
        for(int i=0; i<NumOfRow ; ++i ){
            Integer colValue = transferToInteger(colNum, i);
            if(colValue!=null && !colValue.equals(value)){
                selectedRows.add(i);
            }
        }
    }

    public void selectByEqualOP(Integer colNum, Integer value) throws TableException {
        for(int i=0; i<NumOfRow ; ++i ){
            Integer colValue = transferToInteger(colNum, i);
            if(colValue!=null && colValue.equals(value)){
                selectedRows.add(i);
            }
        }
    }

    public void dropRowByRowNum(Integer rowNum){
        DataRow tgt = rows.get(rowNum);
        rows.remove(tgt);
        NumOfRow--;
    }


    public void dropElementByColNum(int colNum) {
        // already not allowed drop id
        cols.remove(colNum);
        for(DataRow row: rows){
            if(colNum<row.getContents().size()){
                row.dropElementByColNum(colNum);
            }
        }
        NumOfCol--;
    }

    public int getRowsWidth() {
        if(NumOfRow==0){ return 0; }
        return rows.get(0).getContents().size();
    }

    public void fillInAllBlankRows() {
        if(NumOfRow!=0 && NumOfCol!=0){
            int bias = NumOfCol - getRowsWidth();
            addNullWithMultiTimes(bias);
        }
    }

    public void addNullWithMultiTimes(int times){
        for(int i=0; i<times; ++i){
            for(DataRow row: rows){
                row.addElement("null");    // actually is null
            }
        }
    }

    public String printCols(){
        ArrayList<String> list = getColNames();
        return String.join("\t", list)+"\n";
    }

    public String printRows(){
        ArrayList<String> list = new ArrayList<>();
        for(DataRow row: rows){
            list.add(row.printContents());
        }
        return String.join("", list);
    }


    public Integer findMaxId() throws TableException{
        setMaxId();
        return maxId;
    }

    public void setMaxId() throws TableException {
        checkTableFormat();
        int maxNum = 0;
        for (DataRow row : rows){
            maxNum = Math.max(maxNum, Integer.parseInt(row.getContents().get(0)));
        }
        maxId = maxNum;
    }

    public void checkIdInColumn() throws TableException {
        if(!cols.isEmpty()){
            if(!cols.get(0).getName().equals("id")){
                throw new TableException.absentIdForFirstColumnException(tableName);
            }
            checkIdInEveryRow();
        }
    }

    public void checkIdInEveryRow() throws TableException {
        for (DataRow row : rows){
            if(!row.getContents().get(0).matches("[0-9]+")){
                throw new TableException.invalidIdNumberInTableException(tableName);
            }
        }
    }

    public void checkSameNumberOfColsAndElementOfEachRow() throws TableException {
        for (DataRow row : rows){
            if(row.getContents().size()!=NumOfCol){
                throw new TableException.differentNumOfElementsInRowsException(tableName);
            }
        }
    }

    public void checkTableFormat() throws TableException {
        checkIdInColumn();
//        checkIdInEveryRow();
        checkSameNumberOfColsAndElementOfEachRow();
    }

    public String getName() { return tableName; }

    public void setName(String name) {
        tableName = name;
    }

    public Integer getIndexNum() {
        return NumOfCol;
    }

    public void addIndex(DataCol col) {
        cols.add(col);
        NumOfCol++;   // need test
    }

    public DataCol getIndexByNum(Integer num) { return cols.get(num); }

    public String getIdxNameByNum(Integer num) {
        return cols.get(num).getName();
    }

    public int getRowNum() {
        return NumOfRow;
    }

    public void addRow(DataRow row) {
        rows.add(row);
        NumOfRow++;   // need test
    }

    public DataRow getItemByNum(Integer num) {
        return rows.get(num);
    }


    public String getValFrom2D(Integer h, Integer w) {
        return rows.get(h).getValueByNum(w);
    }

    public Integer getNumByIndexName(String index) throws IOException {
        if(!getColNames().contains(index)){
            throw new IOException.absentInputIndexInTableException(index,tableName);
        }
        return getColNames().indexOf(index);
    }

    public ArrayList<String> getColNames() {
        ArrayList<String> list = new ArrayList<>();
        for(DataCol col: cols){
            list.add(col.getName());
        }
        return list;
    }

    public ArrayList<DataCol> getCols() {
        return cols;
    }

    public ArrayList<DataRow> getRows() {
        return rows;
    }

    public ArrayList<Integer> getSelectedRows() {
        return selectedRows;
    }

    public void setSelectedRows(ArrayList<Integer> Rows) {
        selectedRows = Rows;
    }


    public void setNumOfCol(Integer newNumOfCol) {
        NumOfCol = newNumOfCol;
    }

}
