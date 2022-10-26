package edu.uob;

import edu.uob.DBcmd.Condition;
import edu.uob.Exception.CmdException;
import edu.uob.Exception.IOException;
import edu.uob.Exception.TableException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class FileWithConditionTest {

    FileHandle tsHandle;
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
    void testDropColsInTable00() throws TableException, CmdException {
        Tokenizer tsTKs = new Tokenizer("OR (mark>15)");
        Condition tsCondition = new Condition(tsTKs,2);
        ArrayList<Condition> tsConditions = new ArrayList<>();
        tsConditions.add(tsCondition);

        tsHandle.storeRowsByConditions(tsConditions,"table1");
        tsDBStore = tsHandle.getDbStore();
        DataTable tsTable = tsDBStore.getDBByName("testDB").getTBByName("table1");
        assertEquals(2, tsTable.getRowNum());
    }

    @Test
    void testDropColsInTable00b() throws TableException, CmdException {
        Tokenizer tsTKs = new Tokenizer("OR (mark>15)");
        Condition tsCondition = new Condition(tsTKs,2);
        ArrayList<Condition> tsConditions = new ArrayList<>();
        tsConditions.add(tsCondition);

        tsHandle.dropRowsByConditions(tsConditions,"table1");
        tsDBStore = tsHandle.getDbStore();
        DataTable tsTable = tsDBStore.getDBByName("testDB").getTBByName("table1");
        assertEquals(1, tsTable.getRowNum());
    }

    @Test
    void testDropColsInTable01() throws TableException, CmdException {
        Tokenizer tsTKs = new Tokenizer("OR (average<=-0.11)");
        Condition tsCondition = new Condition(tsTKs,2);
        ArrayList<Condition> tsConditions = new ArrayList<>();
        tsConditions.add(tsCondition);

        tsHandle.storeRowsByConditions(tsConditions,"table1");
        tsDBStore = tsHandle.getDbStore();
        DataTable tsTable = tsDBStore.getDBByName("testDB").getTBByName("table1");
        assertEquals(1, tsTable.getRowNum());
    }

    @Test
    void testDropColsInTable02() throws TableException, CmdException {
        Tokenizer tsTKs = new Tokenizer("OR (average>+0.11)");
        Condition tsCondition = new Condition(tsTKs,2);
        ArrayList<Condition> tsConditions = new ArrayList<>();
        tsConditions.add(tsCondition);

        tsHandle.storeRowsByConditions(tsConditions,"table1");
        tsDBStore = tsHandle.getDbStore();
        DataTable tsTable = tsDBStore.getDBByName("testDB").getTBByName("table1");
        assertEquals(2, tsTable.getRowNum());
    }

    @Test
    void testDropColsInTable03() throws TableException, CmdException {
        Tokenizer tsTKs = new Tokenizer("OR (name=='Bob')");
        Condition tsCondition = new Condition(tsTKs,2);
        ArrayList<Condition> tsConditions = new ArrayList<>();
        tsConditions.add(tsCondition);

        tsHandle.storeRowsByConditions(tsConditions,"table1");
        tsDBStore = tsHandle.getDbStore();
        DataTable tsTable = tsDBStore.getDBByName("testDB").getTBByName("table1");
        assertEquals(1, tsTable.getRowNum());
    }

    @Test
    void testDropColsInTable04() throws TableException, CmdException {
        Tokenizer tsTKs = new Tokenizer("OR (name Like 'B')");
        Condition tsCondition = new Condition(tsTKs,2);
        ArrayList<Condition> tsConditions = new ArrayList<>();
        tsConditions.add(tsCondition);

        tsHandle.storeRowsByConditions(tsConditions,"table1");
        tsDBStore = tsHandle.getDbStore();
        DataTable tsTable = tsDBStore.getDBByName("testDB").getTBByName("table1");
        assertEquals(1, tsTable.getRowNum());
    }

    @Test
    void testDropColsInTable05() throws TableException, CmdException {
        Tokenizer tsTKs = new Tokenizer("OR (name==NULL)");
        Condition tsCondition = new Condition(tsTKs,2);
        ArrayList<Condition> tsConditions = new ArrayList<>();
        tsConditions.add(tsCondition);

        tsHandle.storeRowsByConditions(tsConditions,"table1");
        tsDBStore = tsHandle.getDbStore();
        DataTable tsTable = tsDBStore.getDBByName("testDB").getTBByName("table1");
        assertEquals(1, tsTable.getRowNum());
    }

    @Test
    void testDropColsInTable06() throws TableException, CmdException {
        Tokenizer tsTKs = new Tokenizer("OR (pass==NULL)");
        Condition tsCondition = new Condition(tsTKs,2);
        ArrayList<Condition> tsConditions = new ArrayList<>();
        tsConditions.add(tsCondition);

        tsHandle.storeRowsByConditions(tsConditions,"table1");
        tsDBStore = tsHandle.getDbStore();
        DataTable tsTable = tsDBStore.getDBByName("testDB").getTBByName("table1");
        assertEquals(1, tsTable.getRowNum());
    }

    @Test
    void testDropColsInTable07() throws TableException, CmdException {
        Tokenizer tsTKs = new Tokenizer("OR (mark != NULL)");
        Condition tsCondition = new Condition(tsTKs,2);
        ArrayList<Condition> tsConditions = new ArrayList<>();
        tsConditions.add(tsCondition);

        tsHandle.storeRowsByConditions(tsConditions,"table1");
        tsDBStore = tsHandle.getDbStore();
        DataTable tsTable = tsDBStore.getDBByName("testDB").getTBByName("table1");
        assertEquals(3, tsTable.getRowNum());
    }

    @Test
    void testDropColsInTable08() throws TableException, CmdException {
        Tokenizer tsTKs = new Tokenizer("OR (average== null)");
        Condition tsCondition = new Condition(tsTKs,2);
        ArrayList<Condition> tsConditions = new ArrayList<>();
        tsConditions.add(tsCondition);

        tsHandle.storeRowsByConditions(tsConditions,"table1");
        tsDBStore = tsHandle.getDbStore();
        DataTable tsTable = tsDBStore.getDBByName("testDB").getTBByName("table1");
        assertEquals(0, tsTable.getRowNum());
    }

    @Test
    void testDropColsInTable09() throws TableException, CmdException {
        Tokenizer tsTKs = new Tokenizer("(mark <= 20 ) AND (average > 0.1)");
        Condition tsCond1 = new Condition(tsTKs,1);
        Condition tsCond3 = new Condition(tsTKs,5);
        Condition tsCond2 = new Condition(tsTKs,7);
        ArrayList<Condition> tsConditions = new ArrayList<>(Arrays.asList(tsCond1,tsCond2,tsCond3));

        tsHandle.storeRowsByConditions(tsConditions,"table1");
        tsDBStore = tsHandle.getDbStore();
        DataTable tsTable = tsDBStore.getDBByName("testDB").getTBByName("table1");
        assertEquals(1, tsTable.getRowNum());
    }

    @Test
    void testDropColsInTable10() throws TableException, CmdException {
        Tokenizer tsTKs = new Tokenizer("(mark < 20 ) OR (average > +3.9)");
        Condition tsCond1 = new Condition(tsTKs,1);
        Condition tsCond3 = new Condition(tsTKs,5);
        Condition tsCond2 = new Condition(tsTKs,7);
        ArrayList<Condition> tsConditions = new ArrayList<>(Arrays.asList(tsCond1,tsCond2,tsCond3));

        tsHandle.storeRowsByConditions(tsConditions,"table1");
        tsDBStore = tsHandle.getDbStore();
        DataTable tsTable = tsDBStore.getDBByName("testDB").getTBByName("table1");
        assertEquals(2, tsTable.getRowNum());
    }

    @Test
    void testDropColsInTable10b() throws TableException, CmdException {
        Tokenizer tsTKs = new Tokenizer("(mark < 20 ) OR (average > +3.9)");
        Condition tsCond1 = new Condition(tsTKs,1);
        Condition tsCond3 = new Condition(tsTKs,5);
        Condition tsCond2 = new Condition(tsTKs,7);
        ArrayList<Condition> tsConditions = new ArrayList<>(Arrays.asList(tsCond1,tsCond2,tsCond3));

        tsHandle.dropRowsByConditions(tsConditions,"table1");
        tsDBStore = tsHandle.getDbStore();
        DataTable tsTable = tsDBStore.getDBByName("testDB").getTBByName("table1");
        assertEquals(1, tsTable.getRowNum());
    }
}