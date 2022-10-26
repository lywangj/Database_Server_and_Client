package edu.uob.DBcmd;

import edu.uob.Exception.CmdException;
import edu.uob.DataStore;
import edu.uob.Exception.IOException;
import edu.uob.Exception.TableException;
import edu.uob.FileHandle;
import edu.uob.Tokenizer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class InsertCMDTest {

    Tokenizer tsTKs;
    DataStore tsDBMap;
    FileHandle handler;
    DBCmd cmd;

    @BeforeEach
    void setup(@TempDir File dbDir) throws IOException, java.io.IOException, TableException {
        handler = new FileHandle(dbDir);
        tsDBMap=handler.getDbStore();
        handler.createNewFolder("testInsertDB");
        handler.setDbNameInOperating("testInsertDB");
        handler.createNewFile("table1");
        String content = """
                id\tname\temail\tmark
                1\tDave\tdavE@bristol\t10
                2\tBob\tbob@bob.net\t20
                3\tHarry\tharry@harry.com\t30
                """;
        handler.writeWholeFile(content, "table1");
    }

    @AfterEach
    void close() {
        assertDoesNotThrow(() -> handler.deleteExistingFile("table1"));
        assertDoesNotThrow(() -> handler.deleteExistingFolder("testInsertDB"));
    }

    @Test
    void testInitialize000() {
        String comingCommand = "INSERT INTO table1 VALUES('Chris', 'chris@chris.ac.uk', 40);";
        tsTKs = new Tokenizer(comingCommand);
        cmd = new InsertCMD(tsTKs, handler);
        cmd.initialize();
        assertEquals("INSERT", cmd.getCmdType());
        assertEquals("table1", cmd.getTBNames().get(0));
        assertEquals("Chris", cmd.getValueList().get(0).getValue());
        assertEquals(true, cmd.getNormalRun());
        String message = cmd.getOutputResult();
        assertEquals("Successfully insert ['Chris', 'chris@chris.ac.uk', 40] in table 'table1'.", message);
    }

    @Test
    void testInitialize001() {
        String comingCommand = "INSERT INTO table1 VALUES(100);";
        tsTKs = new Tokenizer(comingCommand);
        cmd = new InsertCMD(tsTKs, handler);
        cmd.initialize();
        assertEquals("INSERT", cmd.getCmdType());
        assertEquals("table1", cmd.getTBNames().get(0));
        assertEquals("100", cmd.getValueList().get(0).getValue());
        assertEquals(false, cmd.getNormalRun());
        String message = cmd.getOutputResult();
        assertEquals("Input value gets less element than attributes in table 'table1'.", message);
    }

    @Test
    void testInitialize002() {
        String comingCommand = "INSERT INTO table1 VALUES('Dave', 55, TRUE, -0.55, NULL);";
        tsTKs = new Tokenizer(comingCommand);
        cmd = new InsertCMD(tsTKs, handler);
        cmd.initialize();
        assertEquals("-0.55", cmd.getValueList().get(3).getValue());
        assertEquals("NULL", cmd.getValueList().get(4).getValue());
        assertEquals(false, cmd.getNormalRun());
        String message = cmd.getOutputResult();
        assertEquals("Input value gets more element than attributes in table 'table1'.", message);
    }

    @Test
    void testInitialize003() {
        String comingCommand = "INSERT INTO table1 VALUES(WHere, 55, TRUE);";
        tsTKs = new Tokenizer(comingCommand);
        cmd = new InsertCMD(tsTKs, handler);
        cmd.initialize();
        assertThrows(CmdException.grammarErrorException.class, () -> cmd.checkGrammar() );
        assertEquals(false, cmd.getNormalRun() );
        String message = cmd.getOutputResult();
        assertEquals("Grammar error with at least one unexpected keyword 'WHere' is found.", message);
    }

    @Test
    void testInitialize004() {
        String comingCommand = "INSERT INTO table1 VALUES 'Dave', 55, TRUE);";
        tsTKs = new Tokenizer(comingCommand);
        cmd = new InsertCMD(tsTKs, handler);
        cmd.initialize();
        assertThrows(CmdException.grammarErrorException.class, () -> cmd.checkGrammar() );
        assertEquals(false, cmd.getNormalRun() );
        String message = cmd.getOutputResult();
        assertEquals("Grammar error with at least one unexpected keyword ''Dave'' is found.", message);
    }

    @Test
    void testInitialize005() {
        String comingCommand = "INSERT INTO table1 VALUES( 'Dave', 55, TRUE;";   //size:11
        tsTKs = new Tokenizer(comingCommand);
        cmd = new InsertCMD(tsTKs, handler);
        cmd.initialize();
        assertEquals(false, cmd.getNormalRun() );
        String message = cmd.getOutputResult();
        assertEquals("Grammar error with at least one unexpected keyword ';' is found.", message);
    }

    @Test
    void testInitialize0051() {
        String comingCommand = "INSERT INTO table1 VALUES( 'Dave', 100, WHERE   ;";
        tsTKs = new Tokenizer(comingCommand);
        cmd = new InsertCMD(tsTKs, handler);
        cmd.initialize();
        assertThrows(CmdException.grammarErrorException.class, () -> cmd.checkGrammar() );
        assertEquals(false, cmd.getNormalRun() );
        String message = cmd.getOutputResult();
        assertEquals("Grammar error with at least one unexpected keyword 'WHERE' is found.", message);
    }

    @Test
    void testInitialize006() {
        String comingCommand = "INSERT INTO table1 VALUES ('Dave', 55, TRUE)";
        tsTKs = new Tokenizer(comingCommand);
        cmd = new InsertCMD(tsTKs, handler);
        cmd.initialize();
        assertThrows(CmdException.invalidEndOfCmdException.class, () -> cmd.checkGrammar() );
        assertEquals(false, cmd.getNormalRun() );
        String message = cmd.getOutputResult();
        assertEquals("Semicolon ';' must be at the end of command.", message);
    }

    @Test
    void testInitialize007() {
        String comingCommand = "INSERT FROM table1 VALUES ('Dave', 55, TRUE);";
        tsTKs = new Tokenizer(comingCommand);
        cmd = new InsertCMD(tsTKs, handler);
        cmd.initialize();
        assertThrows(CmdException.grammarErrorException.class, () -> cmd.checkGrammar() );
        assertEquals(false, cmd.getNormalRun() );
        String message = cmd.getOutputResult();
        assertEquals("Grammar error with at least one unexpected keyword 'FROM' is found.", message);
    }

    @Test
    void testInitialize008() {
        String comingCommand = "INSERT into table1 VALUES(  'Dave', 55 TRUE);";
        tsTKs = new Tokenizer(comingCommand);
        cmd = new InsertCMD(tsTKs, handler);
        cmd.initialize();
        assertThrows(CmdException.grammarErrorException.class, () -> cmd.checkGrammar() );
        assertEquals(false, cmd.getNormalRun() );
        String message = cmd.getOutputResult();
        assertEquals("Grammar error with at least one unexpected keyword 'TRUE' is found.", message);
    }

}