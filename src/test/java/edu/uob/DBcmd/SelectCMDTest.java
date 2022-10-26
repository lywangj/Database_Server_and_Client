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

class SelectCMDTest {

    Tokenizer tsTKs;
    DataStore tsDBMap;
    FileHandle handler;
    DBCmd cmd;

    @BeforeEach
    void setup(@TempDir File dbDir) throws IOException, java.io.IOException, TableException {
        handler = new FileHandle(dbDir);
        tsDBMap=handler.getDbStore();
        handler.createNewFolder("testSelectDB");
        handler.setDbNameInOperating("testSelectDB");
        handler.createNewFile("table1");
        String content = """
                id\tname\tpass\tmark\taverage
                1\tDave\tTRUE\t10\t-50.4
                2\tBob\tFALSE\t20\t+3.33
                3\tnull\tNULL\t30\t5.00
                """;
        handler.writeWholeFile(content, "table1");
    }

    @AfterEach
    void close() {
        assertDoesNotThrow(() -> handler.deleteExistingFile("table1"));
        assertDoesNotThrow(() -> handler.deleteExistingFolder("testSelectDB"));
    }

    @Test
    void testInitialize000() {
        String comingCommand = "SELECT * FROM table1;";
        tsTKs = new Tokenizer(comingCommand);
        cmd = new SelectCMD(tsTKs, handler);
        cmd.initialize();
        assertEquals("SELECT", cmd.getCmdType());
        assertNull(cmd.getDBName());
        assertEquals("table1", cmd.getTBNames().get(0));
        assertEquals(true, cmd.getNormalRun());
        String message = cmd.getOutputResult();
        assertEquals("""
                Successfully select all columns in table 'table1'.
                id\tname\tpass\tmark\taverage
                1\tDave\tTRUE\t10\t-50.4
                2\tBob\tFALSE\t20\t+3.33
                3\tnull\tNULL\t30\t5.00
                """, message);
    }

    @Test
    void testInitialize001() {
        String comingCommand = "SELECT name FROM table1;";
        tsTKs = new Tokenizer(comingCommand);
        cmd = new SelectCMD(tsTKs, handler);
        cmd.initialize();
        assertEquals("SELECT", cmd.getCmdType());
        assertEquals("table1", cmd.getTBNames().get(0));
        assertEquals("name", cmd.getColNames().get(0));
        assertEquals(true, cmd.getNormalRun());
        String message = cmd.getOutputResult();
        assertEquals("""
                Successfully select columns in table 'table1'.
                name
                Dave
                Bob
                null
                """, message);
    }

    @Test
    void testInitialize002() {
        String comingCommand = "SELECT id, pass, average FROM table1;";
        tsTKs = new Tokenizer(comingCommand);
        cmd = new SelectCMD(tsTKs, handler);
        cmd.initialize();
        assertEquals("SELECT", cmd.getCmdType());
        assertEquals("average", cmd.getColNames().get(2));
        assertEquals(3, cmd.getColNames().size());
        assertEquals("select columns", cmd.getAction());
        assertEquals(true, cmd.getNormalRun());
        String message = cmd.getOutputResult();
        assertEquals("""
                Successfully select columns in table 'table1'.
                id\tpass\taverage
                1\tTRUE\t-50.4
                2\tFALSE\t+3.33
                3\tNULL\t5.00
                """, message);
    }

    @Test
    void testInitialize003() {
        String comingCommand = "SELECT * FROM table1 WHERE pass==false;";
        tsTKs = new Tokenizer(comingCommand);
        cmd = new SelectCMD(tsTKs, handler);
        cmd.initialize();
        assertEquals(0, cmd.getColNames().size());
        assertEquals("select all columns", cmd.getAction());
        assertEquals("pass == (BOOL) false", cmd.getConditions().get(0).printout());
        assertEquals(1, cmd.getConditions().size());
        assertEquals(true, cmd.getNormalRun());
        String message = cmd.getOutputResult();
        assertEquals("""
                Successfully select all columns in table 'table1'.
                id\tname\tpass\tmark\taverage
                2\tBob\tFALSE\t20\t+3.33
                """, message);
    }

    @Test
    void testInitialize003b() {
        String comingCommand = "SELECT name FROM table1 WHERE pass==false;";
        tsTKs = new Tokenizer(comingCommand);
        cmd = new SelectCMD(tsTKs, handler);
        cmd.initialize();
        assertEquals(1, cmd.getColNames().size());
        assertEquals("select columns", cmd.getAction());
        assertEquals("pass == (BOOL) false", cmd.getConditions().get(0).printout());
        assertEquals(1, cmd.getConditions().size());
        assertEquals(true, cmd.getNormalRun());
        String message = cmd.getOutputResult();
        assertEquals("""
                Successfully select columns in table 'table1'.
                name
                Bob
                """, message);
    }

    @Test
    void testInitialize003c() {
        String comingCommand = "SELECT pass, mark, name FROM table1 WHERE pass==null;";
        tsTKs = new Tokenizer(comingCommand);
        cmd = new SelectCMD(tsTKs, handler);
        cmd.initialize();
        assertEquals(true, cmd.getNormalRun());
        String message = cmd.getOutputResult();
        assertEquals("""
                Successfully select columns in table 'table1'.
                name\tpass\tmark
                null\tNULL\t30
                """, message);
    }

    @Test
    void testInitialize004() {
        String comingCommand = "SELECT name, email, group FROM table1 WHERE (score==100) AND ( (score==100) AND ( (score==100) AND ( score==80)) );";
        tsTKs = new Tokenizer(comingCommand);
        cmd = new SelectCMD(tsTKs, handler);
        cmd.initialize();
        assertEquals(3, cmd.getColNames().size());
        assertEquals("select columns", cmd.getAction());
        assertEquals("score == (INT) 80", cmd.getConditions().get(3).printout());
        assertEquals(7, cmd.getConditions().size());
        assertEquals(true, cmd.getNormalRun());
        String message = cmd.getOutputResult();
        assertEquals("""
                Successfully select columns in table 'table1'.
                name
                """, message);
    }

    @Test
    void testInitialize005() {
        String comingCommand = "SELECT * FROM table1 WHERE score == 20;";
        tsTKs = new Tokenizer(comingCommand);
        cmd = new SelectCMD(tsTKs, handler);
        cmd.initialize();
        assertEquals(true, cmd.getNormalRun());
        String message = cmd.getOutputResult();
        assertEquals("""
                Successfully select all columns in table 'table1'.
                id\tname\tpass\tmark\taverage
                """, message);
    }

    @Test
    void testInitialize006() {
        String comingCommand = "SELECT * FROM table1";
        tsTKs = new Tokenizer(comingCommand);
        cmd = new SelectCMD(tsTKs, handler);
        cmd.initialize();
        assertThrows(CmdException.invalidCmdLengthException.class, () -> cmd.checkGrammar() );
        assertEquals(false, cmd.getNormalRun() );
        String message = cmd.getOutputResult();
        assertEquals("Length of this command is invalid.", message);
    }

    @Test
    void testInitialize007() {
        String comingCommand = "SELECT name, email FROM table1";
        tsTKs = new Tokenizer(comingCommand);
        cmd = new SelectCMD(tsTKs, handler);
        cmd.initialize();
        assertThrows(CmdException.invalidEndOfCmdException.class, () -> cmd.checkGrammar() );
        assertEquals(false, cmd.getNormalRun() );
        String message = cmd.getOutputResult();
        assertEquals("Semicolon ';' must be at the end of command.", message);
    }

    @Test
    void testInitialize008() {
        String comingCommand = "SELECT name email FROM table1;";
        tsTKs = new Tokenizer(comingCommand);
        cmd = new SelectCMD(tsTKs, handler);
        cmd.initialize();
        assertEquals(false, cmd.getNormalRun() );
        String message = cmd.getOutputResult();
        assertEquals("Grammar error with at least one unexpected keyword 'email' is found.", message);
    }

    @Test
    void testInitialize009() {
        String comingCommand = "SELECT name FROM table1 WHERE   ;";
        tsTKs = new Tokenizer(comingCommand);
        cmd = new SelectCMD(tsTKs, handler);
        cmd.initialize();
        assertEquals(false, cmd.getNormalRun() );
        String message = cmd.getOutputResult();
        assertEquals("Grammar error with at least one unexpected keyword ';' is found.", message);
    }

    @Test
    void testInitialize010() {
        String comingCommand = "SELECT * FROM table1 from   ;";
        tsTKs = new Tokenizer(comingCommand);
        cmd = new SelectCMD(tsTKs, handler);
        cmd.initialize();
        assertEquals(false, cmd.getNormalRun() );
        String message = cmd.getOutputResult();
        assertEquals("Grammar error with at least one unexpected keyword 'from' is found.", message);
    }

    @Test
    void testInitialize011() {
        String comingCommand = "SELECT * table1 WHERE   ;";
        tsTKs = new Tokenizer(comingCommand);
        cmd = new SelectCMD(tsTKs, handler);
        cmd.initialize();
        assertThrows(CmdException.grammarErrorException.class, () -> cmd.checkGrammar() );
        assertEquals(false, cmd.getNormalRun() );
        String message = cmd.getOutputResult();
        assertEquals("Grammar error with at least one unexpected keyword 'table1' is found.", message);
    }

    @Test
    void testInitialize012() {
        String comingCommand = "SELECT * from WHeRE   ;";
        tsTKs = new Tokenizer(comingCommand);
        cmd = new SelectCMD(tsTKs, handler);
        cmd.initialize();
        assertEquals(false, cmd.getNormalRun() );
        String message = cmd.getOutputResult();
        assertEquals("Grammar error with at least one unexpected keyword 'WHeRE' is found.", message);
    }

    @Test
    void testInitialize013() {
        String comingCommand = "SELECT * from tb1 WHeRE  98 == mark ;";
        tsTKs = new Tokenizer(comingCommand);
        cmd = new SelectCMD(tsTKs, handler);
        cmd.initialize();
        assertThrows(CmdException.grammarErrorException.class, () -> cmd.checkGrammar() );
        assertEquals(false, cmd.getNormalRun() );
        String message = cmd.getOutputResult();
        assertEquals("Grammar error with at least one unexpected keyword '98' is found.", message);
    }

    @Test
    void testInitialize014() {
        String comingCommand = "SELECT * from tb1 WHeRE  mark=100 ;";
        tsTKs = new Tokenizer(comingCommand);
        cmd = new SelectCMD(tsTKs, handler);
        cmd.initialize();
        assertThrows(CmdException.grammarErrorException.class, () -> cmd.checkGrammar() );
        assertEquals(false, cmd.getNormalRun() );
        String message = cmd.getOutputResult();
        assertEquals("Grammar error with at least one unexpected keyword '=' is found.", message);
    }

}