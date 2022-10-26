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

class AlterCMDTest {

    Tokenizer tsTKs;
    DataStore tsDBMap;
    FileHandle handler;
    DBCmd cmd;

    @BeforeEach
    void setup(@TempDir File dbDir) throws IOException, java.io.IOException, TableException {
        handler = new FileHandle(dbDir);
        tsDBMap=handler.getDbStore();
        handler.createNewFolder("testAlterDB");
        handler.setDbNameInOperating("testAlterDB");
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
        assertDoesNotThrow(() -> handler.deleteExistingFolder("testAlterDB"));
    }

    @Test
    void testInitialize000() {
        String comingCommand = "ALTER TABLE table1 ADD pass;";
        tsTKs = new Tokenizer(comingCommand);
        cmd = new AlterCMD(tsTKs, handler);
        cmd.initialize();
        assertEquals("ALTER", cmd.getCmdType());
        assertEquals("table1", cmd.getTBNames().get(0));
        assertEquals("pass", cmd.getColNames().get(0));
        assertEquals(true, cmd.getNormalRun() );
        String message = cmd.getOutputResult();
        assertEquals("Successfully add a column 'pass', in table 'table1'.", message);
    }

    @Test
    void testInitialize001() {
        String comingCommand = "ALTER TABLE table1 drop email;";
        tsTKs = new Tokenizer(comingCommand);
        cmd = new AlterCMD(tsTKs, handler);
        cmd.initialize();
        assertEquals(true, cmd.getNormalRun() );
        String message = cmd.getOutputResult();
        assertEquals("Successfully drop a column 'email', in table 'table1'.", message);
    }

    @Test
    void testInitialize002() {
        String comingCommand = "ALTER TABLE table1 score;";
        tsTKs = new Tokenizer(comingCommand);
        cmd = new AlterCMD(tsTKs, handler);
        cmd.initialize();
        assertThrows(CmdException.invalidCmdLengthException.class, () -> cmd.checkGrammar() );
        assertEquals(false, cmd.getNormalRun() );
        String message = cmd.getOutputResult();
        assertEquals("Length of this command is invalid.", message);
    }

    @Test
    void testInitialize003() {
        String comingCommand = "ALTER TABLE table1 AND score;";
        tsTKs = new Tokenizer(comingCommand);
        cmd = new AlterCMD(tsTKs, handler);
        cmd.initialize();
        assertThrows(CmdException.grammarErrorException.class, () -> cmd.checkGrammar() );
        assertEquals(false, cmd.getNormalRun() );
        String message = cmd.getOutputResult();
        assertEquals("Grammar error with at least one unexpected keyword 'AND' is found.", message);
    }

    @Test
    void testInitialize004() {
        String comingCommand = "ALTER TABLE table1 drop score; SELECT";
        tsTKs = new Tokenizer(comingCommand);
        cmd = new AlterCMD(tsTKs, handler);
        cmd.initialize();
        assertEquals(false, cmd.getNormalRun() );
        String message = cmd.getOutputResult();
        assertEquals("Semicolon ';' must be at the end of command.", message);
    }

}