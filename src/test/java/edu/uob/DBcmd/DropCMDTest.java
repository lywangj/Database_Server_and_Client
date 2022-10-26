package edu.uob.DBcmd;

import edu.uob.Exception.CmdException;
import edu.uob.DataStore;
import edu.uob.Exception.IOException;
import edu.uob.FileHandle;
import edu.uob.Tokenizer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import static org.junit.jupiter.api.Assertions.*;

class DropCMDTest {

    Tokenizer tsTKs;
    DataStore tsDBMap;
    FileHandle handler;
    DBCmd cmd;

    @BeforeEach
    void setup(@TempDir File dbDir) throws IOException {
        handler = new FileHandle(dbDir);
        tsDBMap=handler.getDbStore();
        handler.createNewFolder("testDropDB");
        handler.setDbNameInOperating("testDropDB");
    }


    @Test
    void testInitialize000() {
        String comingCommand = "drop DATAbase testDropDB;";
        tsTKs = new Tokenizer(comingCommand);
        cmd = new DropCMD(tsTKs, handler);
        cmd.initialize();
        assertEquals("DROP", cmd.getCmdType());
        assertEquals("testDropDB", cmd.getDBName());
        assertNull(cmd.getColNames());
        assertNull(cmd.getTBNames());
        assertEquals(true, cmd.getNormalRun() );
        String message = cmd.getOutputResult();
        assertEquals("Successfully drop database 'testDropDB'.", message);
        assertFalse(handler.checkFolderExists("testDropDB"));
    }

    @Test
    void testInitialize001() throws IOException, java.io.IOException {
        handler.createNewFile("table1");
        String comingCommand = "drOp TABLE table1;";
        tsTKs = new Tokenizer(comingCommand);
        cmd = new DropCMD(tsTKs, handler);
        cmd.initialize();
        assertEquals("DROP", cmd.getCmdType());
        int numOfLastItem = cmd.getTBNames().size()-1;
        assertEquals("table1", cmd.getTBNames().get(numOfLastItem));
        assertNull(cmd.getColNames());
        assertNull(cmd.getDBName());
        assertEquals(true, cmd.getNormalRun() );
        String message = cmd.getOutputResult();
        assertEquals("Successfully drop table 'table1'.", message);
        assertFalse(handler.checkFileExists("table1"));
        assertDoesNotThrow(() -> handler.deleteExistingFolder("testDropDB"));
    }

    @Test
    void testInitialize002() {
        String comingCommand = "drOp table1;";
        tsTKs = new Tokenizer(comingCommand);
        cmd = new DropCMD(tsTKs, handler);
        cmd.initialize();
        assertThrows(CmdException.invalidCmdLengthException.class, () -> cmd.checkGrammar() );
        assertEquals(false, cmd.getNormalRun() );
        String message = cmd.getOutputResult();
        assertEquals("Length of this command is invalid.", message);
        assertDoesNotThrow(() -> handler.deleteExistingFolder("testDropDB"));
    }

    @Test
    void testInitialize003() {
        String comingCommand = "DROP";
        tsTKs = new Tokenizer(comingCommand);
        cmd = new DropCMD(tsTKs, handler);
        cmd.initialize();
        assertThrows(CmdException.invalidCmdLengthException.class, () -> cmd.checkGrammar() );
        assertEquals(false, cmd.getNormalRun() );
        String message = cmd.getOutputResult();
        assertEquals("Length of this command is invalid.", message);
        assertDoesNotThrow(() -> handler.deleteExistingFolder("testDropDB"));
    }
}