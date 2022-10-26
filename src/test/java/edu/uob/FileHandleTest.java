package edu.uob;

import edu.uob.Exception.IOException;
import edu.uob.Exception.TableException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileHandleTest {

    FileHandle tsHandle;
    File testDirectory;
    DataStore tsDBStore;

    @BeforeEach
    void setup(@TempDir File dbDir) throws TableException, IOException, java.io.IOException {
        tsHandle = new FileHandle(dbDir);
        tsHandle.createNewFolder("testDB");
        tsHandle.setDbNameInOperating("testDB");
        tsHandle.createNewFile("table1");
        String content = """
                id\tname\tpass\tmark\taverage
                1\tDave\tTRUE\t10\t-50.4
                2\tBob\tFALSE\t20\t+3.33
                3\tnull\tNULL\t30\t5.00
                """;
        tsHandle.writeWholeFile(content, "table1");
        tsHandle.storeInUseDBInMemory();
    }

    @AfterEach
    void clear() {
        assertDoesNotThrow(() -> tsHandle.deleteExistingFile("table1"));
        assertDoesNotThrow(() -> tsHandle.deleteExistingFolder("testDB"));
    }

    @Test
    void testCheckFolderExists() {
        assertTrue(tsHandle.checkFolderExists("testDB"));
        assertFalse(tsHandle.checkFolderExists("DB999"));
    }

    @Test
    void testCheckFileExists() throws IOException{
        assertTrue(tsHandle.checkFileExists("table1"));
        assertFalse(tsHandle.checkFileExists("notExistingFile"));
    }

    @Test
    void testCreateNewFolder() {
        assertThrows(IOException.failedToCreateFolderException.class,
                () -> tsHandle.createNewFolder("testDB"));
        assertDoesNotThrow(() -> tsHandle.createNewFolder("testDB02"));
        assertTrue(tsHandle.checkFolderExists("testDB02"));
        assertDoesNotThrow(() -> tsHandle.deleteExistingFolder("testDB02"));
    }

    @Test
    void testDeleteExistFolder() {
        assertDoesNotThrow(() -> tsHandle.createNewFolder("testDB03"));
        assertDoesNotThrow(() -> tsHandle.deleteExistingFolder("testDB03"));
        assertFalse(tsHandle.checkFolderExists("testDB03"));
    }

    @Test
    void testCreateNewFile() throws IOException {
        assertFalse(tsHandle.checkFileExists("table999"));
        assertDoesNotThrow(() -> tsHandle.createNewFile("table999"));
        assertTrue(tsHandle.checkFileExists("table999"));
        assertThrows(IOException.failedToCreateFileException.class,
                () -> tsHandle.createNewFile("table999"));
        assertDoesNotThrow(() -> tsHandle.deleteExistingFile("table999"));
    }

    @Test
    void testDeleteExistFile() throws IOException {
        assertDoesNotThrow(() -> tsHandle.createNewFile("table999"));
        assertDoesNotThrow(() -> tsHandle.deleteExistingFile("table999"));
        assertFalse(tsHandle.checkFileExists("table999"));
    }

    @Test
    void testTransferColNamesToString() {
        ArrayList<String> names = new ArrayList<>(Arrays.asList("name", "email", "mark"));
        String ExpectedOutput = "id\tname\temail\tmark\n";
        assertEquals(ExpectedOutput, tsHandle.transferColsToString(names));
    }

    @Test
    void testGetFolderName() {
        String path = "."+File.separator+"testDB";
        testDirectory = Paths.get(path).toAbsolutePath().toFile();
        assertEquals("testDB", tsHandle.getFolderName(testDirectory));
    }

    @Test
    void testGetFileName() {
        String path = "./testDB"+File.separator+"table1.tab";
        testDirectory = Paths.get(path).toAbsolutePath().toFile();
        assertEquals("table1", tsHandle.getFileName(testDirectory));
    }

    @Test
    void testReadHeadline() {
        String tsLine = "id\tName\tAge\tEmail";
        ArrayList<DataCol> heading = tsHandle.readHeadline(tsLine);
        assertEquals("id", heading.get(0).getName());
        assertEquals(DataCol.KeyType.NORMAL, heading.get(0).getKeyType());
        assertEquals("Name", heading.get(1).getName());
        assertEquals("Age", heading.get(2).getName());
        assertEquals("Email", heading.get(3).getName());
    }

    @Test
    void testReadOneLine() {
        String tsLine = "1\tBob\t21\tbob@bob.net";
        DataRow line = tsHandle.readOneLine(1, tsLine);
        assertEquals(1, line.getNum());
        assertEquals("1", line.getValueByNum(0));
        assertEquals("Bob", line.getValueByNum(1));
        assertEquals("21", line.getValueByNum(2));
        assertEquals("bob@bob.net", line.getValueByNum(3));
    }


    @Test
    void testStoreInUseDatabaseInMemory() throws java.io.IOException {
        assertDoesNotThrow(() -> tsHandle.createNewFile("empty"));
        tsHandle.storeInUseDBInMemory();
        tsDBStore = tsHandle.getDbStore();
        assertEquals("empty", tsDBStore.getDBByName("testDB").getTBNameByNum(0));
        assertTrue(tsDBStore.getDBByName("testDB").getTBByNum(0).getCols().isEmpty());
        assertTrue(tsDBStore.getDBByName("testDB").getTBByNum(0).getRows().isEmpty());
        assertEquals(0, tsDBStore.getDBByName("testDB").getTBByName("empty").getRowNum());
        assertEquals(0, tsDBStore.getDBByName("testDB").getTBByName("empty").getIndexNum());
        assertEquals("table1", tsDBStore.getDBByName("testDB").getTBNameByNum(1));
        assertEquals("Bob", tsDBStore.getDBByName("testDB").getTBByNum(1).getValFrom2D(1,1));
        assertDoesNotThrow(() -> tsHandle.deleteExistingFile("empty"));
    }

    @Test
    void testGetMaxIdNumberByTable() throws IOException, TableException, java.io.IOException {
        assertDoesNotThrow(() -> tsHandle.createNewFile("empty"));
        tsHandle.storeInUseDBInMemory();
        tsDBStore = tsHandle.getDbStore();
        tsHandle.updateMaxIdNumber("empty");
        tsHandle.updateMaxIdNumber("table1");
        assertEquals(0, tsHandle.getMaxIdNumber("empty"));
        assertEquals(3, tsHandle.getMaxIdNumber("table1"));
        assertDoesNotThrow(() -> tsHandle.deleteExistingFile("empty"));
    }

    @Test
    void testGetMaxIdNumberByTable2() throws IOException, TableException, java.io.IOException {
        tsHandle.updateMaxIdNumber("table1");
        assertEquals(3, tsHandle.getMaxIdNumber("table1"));
        assertDoesNotThrow(() -> tsHandle.deleteExistingFile("table1"));
        assertDoesNotThrow(() -> tsHandle.createNewFile("table1"));
        assertEquals(3, tsHandle.getMaxIdNumber("table1"));
    }

    @Test
    void testOutputWholeTableToString() throws java.io.IOException {
        tsHandle.storeInUseDBInMemory();
        String ExpectedResult = """
                id\tname\tpass\tmark\taverage
                1\tDave\tTRUE\t10\t-50.4
                2\tBob\tFALSE\t20\t+3.33
                3\tnull\tNULL\t30\t5.00
                """;
        assertEquals(ExpectedResult, tsHandle.outputTableToString("table1"));
    }

    @Test
    void testCheckSameIndexOfInputAndTable() throws java.io.IOException {
        tsHandle.storeInUseDBInMemory();
        tsDBStore = tsHandle.getDbStore();
        DataTable tsTable = tsDBStore.getDBByName("testDB").getTBByName("table1");
        assertThrows(IOException.sameInputIndexInTableException.class,
                () -> tsHandle.checkSameIndex("name", tsTable));
        assertThrows(IOException.absentInputIndexInTableException.class,
                () -> tsHandle.checkNoSameIndex("Dave", tsTable));
        assertThrows(IOException.keyIndexIdException.class,
                () -> tsHandle.checkNotKeyIndexId("id"));
    }

    @Test
    void testAddColsInTable() throws IOException, java.io.IOException {
        tsHandle.storeInUseDBInMemory();
        ArrayList<String> colNames = new ArrayList<>(List.of("email"));
        tsHandle.addColsInTable(colNames,"table1");
        tsDBStore = tsHandle.getDbStore();
        DataTable tsTable = tsDBStore.getDBByName("testDB").getTBByName("table1");
        assertEquals(6, tsTable.getIndexNum());
        assertEquals(6, tsTable.getRowsWidth());
    }

    @Test
    void testJoinRows() {
        ArrayList<String> TB1contents =
                new ArrayList<>(Arrays.asList("1","Math","16","3"));
        ArrayList<String> TB2contents =
                new ArrayList<>(Arrays.asList("3","null","NULL","30","5.00"));
        assertTrue(tsHandle.joinRows(TB1contents, TB2contents,3,0));
    }

    @Test
    void testJoinTwoTables() throws IOException, java.io.IOException, TableException {
        tsHandle.createNewFile("table2");
        String content = """
                id\tclass\thours\tstudent
                1\tMath\t16\t3
                2\tPhysics\t12\t1
                3\tChemistry\t8\t2
                """;
        tsHandle.writeWholeFile(content, "table2");
        tsHandle.storeInUseDBInMemory();
        tsHandle.joinTwoTables("table2","table1","student", "id");
        tsDBStore = tsHandle.getDbStore();
        DataTable tsTable = tsDBStore.getDBByName("testDB").getTBByName("table2");
        assertEquals(7, tsTable.getIndexNum());
        assertEquals(7, tsTable.getRowsWidth());
        String message = tsHandle.outputTableToString("table2");
        assertEquals("""
                id\tclass\thours\tname\tpass\tmark\taverage
                1\tMath\t16\tnull\tNULL\t30\t5.00
                2\tPhysics\t12\tDave\tTRUE\t10\t-50.4
                3\tChemistry\t8\tBob\tFALSE\t20\t+3.33
                """, message);
        assertDoesNotThrow(() -> tsHandle.deleteExistingFile("table2"));
    }

    @Test
    void testUpdateValue() throws java.io.IOException, IOException {
        tsHandle.storeInUseDBInMemory();
        tsDBStore = tsHandle.getDbStore();
        DataTable tsTable = tsDBStore.getDBByName("testDB").getTBByName("table1");
        tsHandle.updateValue(0, "name", "NewName", tsTable);
        assertEquals("NewName", tsTable.getValFrom2D(0,1));
    }

    @Test
    void testReverseList() {
        ArrayList<Integer> list = new ArrayList<>(Arrays.asList(0,1,3,4,5));
        ArrayList<Integer> reversedList = tsHandle.reverseList(list, 8);
        ArrayList<Integer> expectedList = new ArrayList<>(Arrays.asList(2,6,7));
        assertEquals(expectedList, reversedList);
    }

    @Test
    void testImplementAND() {
        ArrayList<Integer> list1 = new ArrayList<>(Arrays.asList(0,1,2,4,5));
        ArrayList<Integer> list2 = new ArrayList<>(Arrays.asList(1,3,4,6));
        ArrayList<Integer> andList = tsHandle.implementAND(list1, list2);
        ArrayList<Integer> expectedList = new ArrayList<>(Arrays.asList(1,4));
        assertEquals(expectedList, andList);
    }

    @Test
    void testImplementOR() {
        ArrayList<Integer> list1 = new ArrayList<>(Arrays.asList(0,1,2,4,5));
        ArrayList<Integer> list2 = new ArrayList<>(Arrays.asList(1,3,4,6));
        ArrayList<Integer> orList = tsHandle.implementOR(list1, list2);
        ArrayList<Integer> expectedList = new ArrayList<>(Arrays.asList(0,1,2,3,4,5,6));
        assertEquals(expectedList, orList);
    }

    @Test
    void testCheckSameNumOfCols() {
        assertThrows(IOException.lessNumOfColsException.class,
                () -> tsHandle.checkSameNumOfCols(3, "table1"));
        assertThrows(IOException.moreNumOfColsException.class,
                () -> tsHandle.checkSameNumOfCols(5, "table1"));
        assertDoesNotThrow(() -> tsHandle.checkSameNumOfCols(4, "table1"));
    }

}