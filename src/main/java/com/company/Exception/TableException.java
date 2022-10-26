package com.company.Exception;


public class TableException extends Exception {

    private static final long serialVersionUID = -3237469957618113527L;

    public TableException(String message) {
        super(message);
    }

    public static class absentIdForFirstColumnException extends TableException {
        private static final long serialVersionUID = -3237469957618113528L;

        public absentIdForFirstColumnException(String tableName) {

            super("Invalid format for 'id' of index is found in table '"+tableName+"'.");
        }
    }

    public static class invalidIdNumberInTableException extends TableException {
        private static final long serialVersionUID = -3237469957618113529L;

        public invalidIdNumberInTableException(String tableName) {

            super("Invalid format for 'id' is found in table '"+tableName+"'.");
        }
    }

    public static class differentNumOfElementsInRowsException extends TableException {
        private static final long serialVersionUID = -3237469957618113530L;

        public differentNumOfElementsInRowsException(String tableName) {

            super("Different numbers of elements in each row are found in table '"+tableName+"'.");
        }
    }

    public static class invalidFormatOfException extends TableException {
        private static final long serialVersionUID = -3237469957618113531L;

        public invalidFormatOfException(String word) {

            super("Invalid format of value '"+word+"' is found in targeted column.");
        }
    }


    public String toString(){
        return getMessage();
    }

}

