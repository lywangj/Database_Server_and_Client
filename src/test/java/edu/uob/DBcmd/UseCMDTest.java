package edu.uob.DBcmd;

import edu.uob.Exception.CmdException;
import edu.uob.Exception.IOException;
import edu.uob.FileHandle;
import edu.uob.Tokenizer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import static org.junit.jupiter.api.Assertions.*;

class UseCMDTest {

    Tokenizer tsTKs;
    FileHandle handler;
    DBCmd cmd;

    @BeforeEach
    void setup(@TempDir File dbDir) throws IOException {
        handler = new FileHandle(dbDir);
        handler.createNewFolder("testUseDB");
    }

    @AfterEach
    void close() {
        assertDoesNotThrow(() -> handler.deleteExistingFolder("testUseDB"));
    }

    @Test
    void testInitialize000() {
        String comingCommand = "Use testUseDB; ";
        tsTKs = new Tokenizer(comingCommand);
        cmd = new UseCMD(tsTKs, handler);
        cmd.initialize();
        assertEquals("USE", cmd.getCmdType());
        assertEquals("testUseDB", cmd.getDBName());
        assertNull(cmd.getColNames());
        assertNull(cmd.getTBNames());
        assertEquals(true, cmd.getNormalRun() );
        String message = cmd.getOutputResult();
        assertEquals("Successfully use database 'testUseDB'.", message);
        assertEquals("testusedb", handler.getDbNameInOperating());
    }

    @Test
    void testInitialize001() {
        String comingCommand = "Use datAB@se1; ";
        tsTKs = new Tokenizer(comingCommand);
        cmd = new UseCMD(tsTKs, handler);
        cmd.initialize();
        assertEquals("USE", cmd.getCmdType());
        assertEquals("datAB@se1", cmd.getDBName());
        assertNull(cmd.getColNames());
        assertNull(cmd.getTBNames());
        assertEquals(false, cmd.getNormalRun() );
        String message = cmd.getOutputResult();
        assertEquals("Database 'datAB@se1' cannot be found.", message);
        assertNull(handler.getDbNameInOperating());
    }

    @Test
    void testInitialize002() {
        String comingCommand = "Use into DB1; ";
        tsTKs = new Tokenizer(comingCommand);
        cmd = new UseCMD(tsTKs, handler);
        cmd.initialize();
        assertEquals(false, cmd.getNormalRun() );
        String message = cmd.getOutputResult();
        assertEquals("Grammar error with at least one unexpected keyword 'into' is found.", message);
        assertNull(handler.getDbNameInOperating());
        comingCommand = "Use testUseDB  ; ";
        Tokenizer tsTKs2 = new Tokenizer(comingCommand);
        DBCmd cmd2 = new UseCMD(tsTKs2, handler);
        cmd2.initialize();
        assertEquals(true, cmd2.getNormalRun() );
        assertEquals("testusedb", handler.getDbNameInOperating());
    }

    @Test
    void testInitialize003() {
        String comingCommand = "Use DB1  ; SELECT ";
        tsTKs = new Tokenizer(comingCommand);
        cmd = new UseCMD(tsTKs, handler);
        cmd.initialize();
        assertThrows(CmdException.invalidEndOfCmdException.class, () -> cmd.checkGrammar() );
        assertEquals(false, cmd.getNormalRun() );
        String message = cmd.getOutputResult();
        assertEquals("Semicolon ';' must be at the end of command.", message);
    }

    @Test
    void testInitialize004() {
        String comingCommand = "Use DB1 . ";
        tsTKs = new Tokenizer(comingCommand);
        cmd = new UseCMD(tsTKs, handler);
        cmd.initialize();
        assertThrows(CmdException.invalidEndOfCmdException.class, () -> cmd.checkGrammar() );
        assertEquals(false, cmd.getNormalRun() );
        String message = cmd.getOutputResult();
        assertEquals("Semicolon ';' must be at the end of command.", message);
    }

}