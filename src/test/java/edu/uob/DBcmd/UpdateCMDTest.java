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

class UpdateCMDTest {

    Tokenizer tsTKs;
    DataStore tsDBMap;
    FileHandle handler;
    DBCmd cmd;

    @BeforeEach
    void setup(@TempDir File dbDir) throws IOException, java.io.IOException, TableException {
        handler = new FileHandle(dbDir);
        tsDBMap=handler.getDbStore();
        handler.createNewFolder("testUpdateDB");
        handler.setDbNameInOperating("testUpdateDB");
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
        assertDoesNotThrow(() -> handler.deleteExistingFolder("testUpdateDB"));
    }

    @Test
    void testInitialize000() {
        String comingCommand = "UPDATE table1 SET mark=75 WHERE name=='Dave';";
        tsTKs = new Tokenizer(comingCommand);
        cmd = new UpdateCMD(tsTKs, handler);
        cmd.initialize();
        assertEquals("UPDATE", cmd.getCmdType());
        assertEquals("table1", cmd.getTBNames().get(0));
        assertEquals("mark", cmd.getColNames().get(0));
        assertEquals("75", cmd.getValueList().get(0).getValue());
        assertEquals("name == (STR) 'Dave'", cmd.getConditions().get(0).printout());
        assertEquals(true, cmd.getNormalRun());
        String message = cmd.getOutputResult();
        assertEquals("Successfully update [mark=75] in table 'table1' when [name=='Dave'].", message);
    }

    @Test
    void testInitialize001() {
        String comingCommand = "UPDATE table1 SET mark=null, pass=false, average=33 " +
                "WHERE name=='Dave';";
        tsTKs = new Tokenizer(comingCommand);
        cmd = new UpdateCMD(tsTKs, handler);
        cmd.initialize();
        assertEquals("UPDATE", cmd.getCmdType());
        assertEquals(1, cmd.getTBNames().size());
        assertEquals(3, cmd.getColNames().size());
        assertEquals(3, cmd.getValueList().size());
        assertEquals(1, cmd.getConditions().size());
        assertEquals(true, cmd.getNormalRun());
        String message = cmd.getOutputResult();
        assertEquals("Successfully update [mark=null,pass=false,average=33]" +
                " in table 'table1' when [name=='Dave'].", message);
    }

    @Test
    void testInitialize001b() {
        String comingCommand = "UPDATE table1 SET mark=70, mark=80, mark=90 WHERE name=='Dave';";
        tsTKs = new Tokenizer(comingCommand);
        cmd = new UpdateCMD(tsTKs, handler);
        cmd.initialize();
        assertEquals(true, cmd.getNormalRun());
        String message = cmd.getOutputResult();
        assertEquals("Successfully update [mark=70,mark=80,mark=90] in table 'table1'" +
                " when [name=='Dave'].", message);
    }

    @Test
    void testInitialize002() {
        String comingCommand = "UPDATE table1 SET mark=75 " +
                "WHERE (name=='Dave')AND(name=='Dave');";
        tsTKs = new Tokenizer(comingCommand);
        cmd = new UpdateCMD(tsTKs, handler);
        cmd.initialize();
        assertEquals(3, cmd.getConditions().size());
        assertEquals(true, cmd.getNormalRun());
        String message = cmd.getOutputResult();
        assertEquals("Successfully update [mark=75] in table 'table1'" +
                " when [(name=='Dave')AND(name=='Dave')].", message);
    }

    @Test
    void testInitialize003() {
        String comingCommand = "UPDATE table1 SET mark=75, name='Dave' " +
                "WHERE (name=='Dave')AND(name=='Dave');";
        tsTKs = new Tokenizer(comingCommand);
        cmd = new UpdateCMD(tsTKs, handler);
        cmd.initialize();
        assertEquals(true, cmd.getNormalRun());
        String message = cmd.getOutputResult();
        assertEquals("Successfully update [mark=75,name='Dave'] in table 'table1'" +
                " when [(name=='Dave')AND(name=='Dave')].", message);
    }

    @Test
    void testInitialize003b() {
        String comingCommand = "UPDATE table1 SET name='Harry', pass=TRUE WHERE mark>15;";
        tsTKs = new Tokenizer(comingCommand);
        cmd = new UpdateCMD(tsTKs, handler);
        cmd.initialize();
        assertEquals(true, cmd.getNormalRun());
        String message = cmd.getOutputResult();
        assertEquals("Successfully update [name='Harry',pass=TRUE] in table 'table1'" +
                " when [mark>15].", message);
    }

    @Test
    void testInitialize004() {
        String comingCommand = "UPDATE table1 mark=75, name='Dave' " +
                "WHERE (name=='Dave')AND(name=='Dave');";
        tsTKs = new Tokenizer(comingCommand);
        cmd = new UpdateCMD(tsTKs, handler);
        cmd.initialize();
        assertThrows(CmdException.grammarErrorException.class, () -> cmd.checkGrammar() );
        assertEquals(false, cmd.getNormalRun() );
        String message = cmd.getOutputResult();
        assertEquals("Grammar error with at least one unexpected keyword 'mark' is found.", message);
    }

    @Test
    void testInitialize005() {
        String comingCommand = "UPDATE 70 SET mark=75, name='Dave' " +
                "WHERE (name=='Dave')AND(name=='Dave');";
        tsTKs = new Tokenizer(comingCommand);
        cmd = new UpdateCMD(tsTKs, handler);
        cmd.initialize();
        assertThrows(CmdException.grammarErrorException.class, () -> cmd.checkGrammar() );
        assertEquals(false, cmd.getNormalRun() );
        String message = cmd.getOutputResult();
        assertEquals("Grammar error with at least one unexpected keyword '70' is found.", message);
    }

    @Test
    void testInitialize006() {
        String comingCommand = "UPDATE table1 SET SET=75, name='Dave' " +
                "WHERE (name=='Dave')AND(name=='Dave');";
        tsTKs = new Tokenizer(comingCommand);
        cmd = new UpdateCMD(tsTKs, handler);
        cmd.initialize();
        assertThrows(CmdException.grammarErrorException.class, () -> cmd.checkGrammar() );
        assertEquals(false, cmd.getNormalRun() );
        String message = cmd.getOutputResult();
        assertEquals("Grammar error with at least one unexpected keyword 'SET' is found.", message);
    }

    @Test
    void testInitialize007() {
        String comingCommand = "UPDATE table1 SET mark=75 name='Dave' " +
                "WHERE (name=='Dave')AND(name=='Dave');";
        tsTKs = new Tokenizer(comingCommand);
        cmd = new UpdateCMD(tsTKs, handler);
        cmd.initialize();
        assertThrows(CmdException.grammarErrorException.class, () -> cmd.checkGrammar() );
        assertEquals(false, cmd.getNormalRun() );
        String message = cmd.getOutputResult();
        assertEquals("Grammar error with at least one unexpected keyword 'name' is found.", message);
    }

    @Test
    void testInitialize008() {
        String comingCommand = "UPDATE table1 SET mark=75, name='Dave' to " +
                "WHERE (name=='Dave')AND(name=='Dave');";
        tsTKs = new Tokenizer(comingCommand);
        cmd = new UpdateCMD(tsTKs, handler);
        cmd.initialize();
        assertThrows(CmdException.grammarErrorException.class, () -> cmd.checkGrammar() );
        assertEquals(false, cmd.getNormalRun() );
        String message = cmd.getOutputResult();
        assertEquals("Grammar error with at least one unexpected keyword 'to' is found.", message);
    }

    @Test
    void testInitialize009() {
        String comingCommand = "UPDATE table1 SET mark=75, name='Dave' " +
                "WHERE ((name=='Dave')AND(name=='Dave');";
        tsTKs = new Tokenizer(comingCommand);
        cmd = new UpdateCMD(tsTKs, handler);
        cmd.initialize();
        assertEquals(false, cmd.getNormalRun() );
        String message = cmd.getOutputResult();
        assertEquals("Grammar error with at least one unexpected keyword ';' is found.", message);
    }

    @Test
    void testInitialize010() {
        String comingCommand = "UPDATE table1 SET mark=75, name='Dave' " +
                "WHERE name (name=='Dave')AND(name=='Dave');";
        tsTKs = new Tokenizer(comingCommand);
        cmd = new UpdateCMD(tsTKs, handler);
        cmd.initialize();
        assertThrows(CmdException.grammarErrorException.class, () -> cmd.checkGrammar() );
        assertEquals(false, cmd.getNormalRun() );
        String message = cmd.getOutputResult();
        assertEquals("Grammar error with at least one unexpected keyword '(' is found.", message);
    }

    @Test
    void testInitialize011() {
        String comingCommand = "UPDATE table1 SET mark=75, name='Dave' " +
                "WHERE WHERE (name=='Dave')AND(name=='Dave');";
        tsTKs = new Tokenizer(comingCommand);
        cmd = new UpdateCMD(tsTKs, handler);
        cmd.initialize();
        assertThrows(CmdException.grammarErrorException.class, () -> cmd.checkGrammar() );
        assertEquals(false, cmd.getNormalRun() );
        String message = cmd.getOutputResult();
        assertEquals("Grammar error with at least one unexpected keyword 'WHERE' is found.", message);
    }
}