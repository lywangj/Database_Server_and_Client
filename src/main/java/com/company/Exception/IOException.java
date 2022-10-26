package com.company.Exception;


public class IOException extends Exception {

    private static final long serialVersionUID = -2629325698018774532L;

    public IOException(String message) {
        super(message);
    }

    public static class absentFolderException extends IOException {
        private static final long serialVersionUID = -2629325698018774533L;

        public absentFolderException(String folderName) {

            super("Database '"+folderName+"' cannot be found.");
        }
    }

    public static class absentFileException extends IOException {
        private static final long serialVersionUID = -2629325698018774534L;

        public absentFileException(String fileName) {

            super("Table '"+fileName+"' cannot be found.");
        }
    }

    public static class conflictFolderException extends IOException {
        private static final long serialVersionUID = -2629325698018774535L;

        public conflictFolderException(String folderName) {

            super("Database '"+folderName+"' has already existed.");
        }
    }

    public static class conflictFileException extends IOException {
        private static final long serialVersionUID = -2629325698018774536L;

        public conflictFileException(String fileName) {

            super("Table '"+fileName+"' has already existed.");
        }
    }

    public static class lessNumOfColsException extends IOException {
        private static final long serialVersionUID = -2629325698018774537L;

        public lessNumOfColsException(String tableName) {

            super("Input value gets less element than attributes in table '"+tableName+"'.");
        }
    }

    public static class moreNumOfColsException extends IOException {
        private static final long serialVersionUID = -2629325698018774538L;

        public moreNumOfColsException(String tableName) {

            super("Input value gets more element than attributes in table '"+tableName+"'.");
        }
    }

    public static class failedToCreateFolderException extends IOException {
        private static final long serialVersionUID = -2629325698018774539L;

        public failedToCreateFolderException(String folderName) {

            super("Database '"+folderName+"' cannot be created. Please check directory.");
        }
    }

    public static class failedToCreateFileException extends IOException {
        private static final long serialVersionUID = -2629325698018774540L;

        public failedToCreateFileException(String fileName) {

            super("Table '"+fileName+"' cannot be created. Please check directory.");
        }
    }

    public static class failedToDeleteFolderException extends IOException {
        private static final long serialVersionUID = -2629325698018774541L;

        public failedToDeleteFolderException(String folderName) {

            super("Database '"+folderName+"' cannot be delete. Please check directory.");
        }
    }

    public static class noDatabaseAssignedException extends IOException {
        private static final long serialVersionUID = -2629325698018774542L;

        public noDatabaseAssignedException() {

            super("No database has been assigned. Please use a database first.");
        }
    }

    public static class sameInputIndexInTableException extends IOException {
        private static final long serialVersionUID = -2629325698018774543L;

        public sameInputIndexInTableException(String index, String tableName) {

            super("Same index ["+index+"] has already existed in table '"+tableName+"'.");
        }
    }

    public static class absentInputIndexInTableException extends IOException {
        private static final long serialVersionUID = -2629325698018774544L;

        public absentInputIndexInTableException(String index, String tableName) {
            super("Input index ["+index+"] cannot be found in table '"+tableName+"'.");
        }
    }

    public static class keyIndexIdException extends IOException {
        private static final long serialVersionUID = -2629325698018774545L;

        public keyIndexIdException() {
            super("Key Index 'id' should not be modified.");
        }
    }



    public String toString(){
        return getMessage();
    }

}
