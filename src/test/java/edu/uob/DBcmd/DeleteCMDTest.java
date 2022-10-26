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

class DeleteCMDTest {

    Tokenizer tsTKs;
    DataStore tsDBMap;
    FileHandle handler;
    DBCmd cmd;

    @BeforeEach
    void setup(@TempDir File dbDir) throws IOException, java.io.IOException, TableException {
        handler = new FileHandle(dbDir);
        tsDBMap=handler.getDbStore();
        handler.createNewFolder("testDeleteDB");
        handler.setDbNameInOperating("testDeleteDB");
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
        assertDoesNotThrow(() -> handler.deleteExistingFolder("testDeleteDB"));
    }


    @Test
    void testInitialize000() {
        String comingCommand = "DELETE FROM table1 WHERE name==NULL;";
        tsTKs = new Tokenizer(comingCommand);
        cmd = new DeleteCMD(tsTKs, handler);
        cmd.initialize();
        assertEquals("DELETE", cmd.getCmdType());
        assertNull(cmd.getDBName());
        assertEquals("table1", cmd.getTBNames().get(0));
        assertEquals("name == (NULL) NULL", cmd.getConditions().get(0).printout());
//        assertEquals(true, cmd.getNormalRun());
        String message = cmd.getOutputResult();
        assertEquals("Successfully delete in table 'table1' when [name==NULL].", message);
    }

    @Test
    void testInitialize001() {
        String comingCommand = "DELETE FROM table1 WHERE (mark>20) OR (pass==TRUE);";
        tsTKs = new Tokenizer(comingCommand);
        cmd = new DeleteCMD(tsTKs, handler);
        cmd.initialize();
        assertEquals("DELETE", cmd.getCmdType());
        assertNull(cmd.getDBName());
        assertEquals("table1", cmd.getTBNames().get(0));
        assertEquals("mark > (INT) 20", cmd.getConditions().get(0).printout());
        assertEquals("pass == (BOOL) TRUE", cmd.getConditions().get(1).printout());
        assertEquals("OR", cmd.getConditions().get(2).printout());
        assertEquals(true, cmd.getNormalRun());
        String message = cmd.getOutputResult();
        assertEquals("Successfully delete in table 'table1' when [(mark>20)OR(pass==TRUE)].", message);
    }

    @Test
    void testInitialize002() {
        String comingCommand = "DELETE FROM table1 WHERE ((mark==100) AND ( mark==10)) OR (  (mark==30) OR ( mark==40));";
        tsTKs = new Tokenizer(comingCommand);
        cmd = new DeleteCMD(tsTKs, handler);
        cmd.initialize();
        assertEquals("mark == (INT) 100", cmd.getConditions().get(0).printout());
        assertEquals("mark == (INT) 10", cmd.getConditions().get(1).printout());
        assertEquals("AND", cmd.getConditions().get(2).printout());
        assertEquals("mark == (INT) 30", cmd.getConditions().get(3).printout());
        assertEquals("mark == (INT) 40", cmd.getConditions().get(4).printout());
        assertEquals("OR", cmd.getConditions().get(5).printout());
        assertEquals("OR", cmd.getConditions().get(6).printout());
        assertEquals(true, cmd.getNormalRun());
        String message = cmd.getOutputResult();
        assertEquals("Successfully delete in table 'table1' " +
                "when [((mark==100)AND(mark==10))OR((mark==30)OR(mark==40))].", message);
    }

    @Test
    void testInitialize003() {
        String comingCommand = "DELETE FROM table1 WHERE " +
                "((mark==100) OR (((mark==90) OR ((mark==80) OR ( mark==70))) OR ( mark==60))) OR ( mark==50);";
        tsTKs = new Tokenizer(comingCommand);
        cmd = new DeleteCMD(tsTKs, handler);
        cmd.initialize();
        assertEquals(11, cmd.getConditions().size());
        assertEquals("mark == (INT) 100", cmd.getConditions().get(0).printout());
        assertEquals("mark == (INT) 90", cmd.getConditions().get(1).printout());
        assertEquals("mark == (INT) 80", cmd.getConditions().get(2).printout());
        assertEquals("mark == (INT) 70", cmd.getConditions().get(3).printout());
        assertEquals("mark == (INT) 60", cmd.getConditions().get(6).printout());
        assertEquals("mark == (INT) 50", cmd.getConditions().get(9).printout());
        assertEquals(true, cmd.getNormalRun());
        String message = cmd.getOutputResult();
        assertEquals("Successfully delete in table 'table1' " +
                "when [((mark==100)OR(((mark==90)OR((mark==80)OR(mark==70)))OR(mark==60)))OR(mark==50)].", message);
    }

    @Test
    void testInitialize004() {
        String comingCommand = "DELETE INTO table1 WHERE mark==100;";
        tsTKs = new Tokenizer(comingCommand);
        cmd = new DeleteCMD(tsTKs, handler);
        cmd.initialize();
        assertThrows(CmdException.grammarErrorException.class, () -> cmd.checkGrammar() );
        assertEquals(false, cmd.getNormalRun() );
        String message = cmd.getOutputResult();
        assertEquals("Grammar error with at least one unexpected keyword 'INTO' is found.", message);
    }

    @Test
    void testInitialize005() {
        String comingCommand = "DELETE FROM 90 WHERE score==100;";
        tsTKs = new Tokenizer(comingCommand);
        cmd = new DeleteCMD(tsTKs, handler);
        cmd.initialize();
        assertThrows(CmdException.grammarErrorException.class, () -> cmd.checkGrammar() );
        assertEquals(false, cmd.getNormalRun() );
        String message = cmd.getOutputResult();
        assertEquals("Grammar error with at least one unexpected keyword '90' is found.", message);
    }

    @Test
    void testInitialize006() {
        String comingCommand = "DELETE FROM table1 ON score==100;";
        tsTKs = new Tokenizer(comingCommand);
        cmd = new DeleteCMD(tsTKs, handler);
        cmd.initialize();
        assertThrows(CmdException.grammarErrorException.class, () -> cmd.checkGrammar() );
        assertEquals(false, cmd.getNormalRun() );
        String message = cmd.getOutputResult();
        assertEquals("Grammar error with at least one unexpected keyword 'ON' is found.", message);
    }

    @Test
    void testInitialize007() {
        String comingCommand = "DELETE FROM table1 WHERE +100.0==100;";
        tsTKs = new Tokenizer(comingCommand);
        cmd = new DeleteCMD(tsTKs, handler);
        cmd.initialize();
        assertThrows(CmdException.grammarErrorException.class, () -> cmd.checkGrammar() );
        assertEquals(false, cmd.getNormalRun() );
        String message = cmd.getOutputResult();
        assertEquals("Grammar error with at least one unexpected keyword '+100.0' is found.", message);
    }

    @Test
    void testInitialize008() {
        String comingCommand = "DELETE FROM table1 WHERE () AND ( score>79.9);";
        tsTKs = new Tokenizer(comingCommand);
        cmd = new DeleteCMD(tsTKs, handler);
        cmd.initialize();
        assertThrows(CmdException.grammarErrorException.class, () -> cmd.checkGrammar() );
        assertEquals(false, cmd.getNormalRun() );
        String message = cmd.getOutputResult();
        assertEquals("Grammar error with at least one unexpected keyword ')' is found.", message);
    }

    @Test
    void testInitialize009() {
        String comingCommand = "DELETE FROM table1 WHERE (score>79.9) AND ( );";
        tsTKs = new Tokenizer(comingCommand);
        cmd = new DeleteCMD(tsTKs, handler);
        cmd.initialize();
        assertThrows(CmdException.grammarErrorException.class, () -> cmd.checkGrammar() );
        assertEquals(false, cmd.getNormalRun() );
        String message = cmd.getOutputResult();
        assertEquals("Grammar error with at least one unexpected keyword ')' is found.", message);
    }

    @Test
    void testInitialize010() {
        String comingCommand = "DELETE FROM table1 WHERE (score>79.9) INTO (score>79.9);";
        tsTKs = new Tokenizer(comingCommand);
        cmd = new DeleteCMD(tsTKs, handler);
        cmd.initialize();
        assertThrows(CmdException.grammarErrorException.class, () -> cmd.checkGrammar() );
        assertEquals(false, cmd.getNormalRun() );
        String message = cmd.getOutputResult();
        assertEquals("Grammar error with at least one unexpected keyword 'INTO' is found.", message);
    }

    @Test
    void testInitialize011() {
        String comingCommand = "DELETE FROM table1 WHERE (score>where) OR (score>79.9);";
        tsTKs = new Tokenizer(comingCommand);
        cmd = new DeleteCMD(tsTKs, handler);
        cmd.initialize();
        assertThrows(CmdException.grammarErrorException.class, () -> cmd.checkGrammar() );
        assertEquals(false, cmd.getNormalRun() );
        String message = cmd.getOutputResult();
        assertEquals("Grammar error with at least one unexpected keyword 'where' is found.", message);
    }
}