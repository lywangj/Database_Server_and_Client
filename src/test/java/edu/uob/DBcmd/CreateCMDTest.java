package edu.uob.DBcmd;

import edu.uob.Exception.CmdException;
import edu.uob.DataStore;
import edu.uob.Exception.IOException;
import edu.uob.FileHandle;
import edu.uob.Tokenizer;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class CreateCMDTest {

    Tokenizer tsTKs;
    DataStore tsDBMap;
    FileHandle handler;
    DBCmd cmd;

    @BeforeEach
    void setup(@TempDir File dbDir) throws IOException {
        handler = new FileHandle(dbDir);
        tsDBMap=handler.getDbStore();
        handler.createNewFolder("testCreateDB");
        handler.setDbNameInOperating("testCreateDB");
    }

    @AfterEach
    void close() {
        assertDoesNotThrow(() -> handler.deleteExistingFolder("testCreateDB"));
    }

    @Test
    void testInitialize000() {
        String comingCommand = "CREATE DATABASE dataBase1;";
        tsTKs = new Tokenizer(comingCommand);
        cmd = new CreateCMD(tsTKs, handler);
        cmd.initialize();
        assertEquals("CREATE", cmd.getCmdType());
        assertEquals("dataBase1", cmd.getDBName());
        assertNull(cmd.getTBNames());
        assertEquals(true, cmd.getNormalRun());
        String message = cmd.getOutputResult();
        assertEquals("Successfully create database 'dataBase1'.", message);
        assertTrue(handler.checkFolderExists("dataBase1"));
        assertDoesNotThrow(() -> handler.deleteExistingFolder("dataBase1"));
    }

    @Test
    void testInitialize001() throws IOException {
        String comingCommand = "CREATE TABLE table1;";
        tsTKs = new Tokenizer(comingCommand);
        cmd = new CreateCMD(tsTKs, handler);
        cmd.initialize();
        assertEquals("CREATE", cmd.getCmdType());
        assertEquals("table1", cmd.getTBNames().get(0));
        assertEquals(true, cmd.getNormalRun());
        String message = cmd.getOutputResult();
        assertEquals("Successfully create table 'table1'.", message);
        assertTrue(handler.checkFileExists("table1"));
        assertDoesNotThrow(() -> handler.deleteExistingFile("table1"));
    }

    @Test
    void testInitialize002() throws IOException {
        String comingCommand = "CREATE TABLE table2 ( email)  ;";
        tsTKs = new Tokenizer(comingCommand);
        cmd = new CreateCMD(tsTKs, handler);
        cmd.initialize();
        assertEquals("CREATE", cmd.getCmdType());
        assertEquals("table2", cmd.getTBNames().get(0));
        assertEquals("email", cmd.getColNames().get(0));
        assertEquals(true, cmd.getNormalRun());
        String message = cmd.getOutputResult();
        assertEquals("Successfully create table 'table2' with [email].", message);
        assertTrue(handler.checkFileExists("table2"));
        assertDoesNotThrow(() -> handler.deleteExistingFile("table2"));
    }

    @Test
    void testInitialize003() throws IOException {
        String comingCommand = "CREATE TABLE table3 ( name1, number, email)  ;";
        tsTKs = new Tokenizer(comingCommand);
        cmd = new CreateCMD(tsTKs, handler);
        cmd.initialize();
        assertEquals("email", cmd.getColNames().get(2));
        assertEquals(true, cmd.getNormalRun());
        String message = cmd.getOutputResult();
        assertEquals("Successfully create table 'table3' with [name1, number, email].", message);
        assertTrue(handler.checkFileExists("table3"));
        assertDoesNotThrow(() -> handler.deleteExistingFile("table3"));
    }

    @Test
    void testInitialize004() {
        String comingCommand = "CREATE TABLE table1  name1, number, email)  ;";
        tsTKs = new Tokenizer(comingCommand);
        cmd = new CreateCMD(tsTKs, handler);
        cmd.initialize();
        assertThrows(CmdException.grammarErrorException.class, () -> cmd.checkGrammar() );
        assertEquals(false, cmd.getNormalRun() );
        String message = cmd.getOutputResult();
        assertEquals("Grammar error with at least one unexpected keyword 'name1' is found.", message);
    }

    @Test
    void testInitialize005() {
        String comingCommand = "CREATE TABLE table1 ( name1, number email)  ;";
        tsTKs = new Tokenizer(comingCommand);
        cmd = new CreateCMD(tsTKs, handler);
        cmd.initialize();
        assertThrows(CmdException.grammarErrorException.class, () -> cmd.checkGrammar() );
        assertEquals(false, cmd.getNormalRun() );
        String message = cmd.getOutputResult();
        assertEquals("Grammar error with at least one unexpected keyword 'email' is found.", message);
    }

    @Test
    void testInitialize006() {
        String comingCommand = "CREATE TABLE table1 ( name1, number, email;";
        tsTKs = new Tokenizer(comingCommand);
        cmd = new CreateCMD(tsTKs, handler);
        cmd.initialize();
//        assertThrows(CmdException.grammarErrorException.class, () -> cmd.checkGrammar() );
        assertEquals(false, cmd.getNormalRun() );
        String message = cmd.getOutputResult();
        assertEquals("Grammar error with at least one unexpected keyword ';' is found.", message);
    }

    @Test
    void testInitialize007() {
        String comingCommand = "CREATE TABLE table1 ?;";
        tsTKs = new Tokenizer(comingCommand);
        cmd = new CreateCMD(tsTKs, handler);
        cmd.initialize();
        assertThrows(CmdException.grammarErrorException.class, () -> cmd.checkGrammar() );
        assertEquals(false, cmd.getNormalRun() );
        String message = cmd.getOutputResult();
        assertEquals("Grammar error with at least one unexpected keyword '?' is found.", message);
    }

    @Test
    void testInitialize008() {
        String comingCommand = "CREATE TABLE table1 ?";
        tsTKs = new Tokenizer(comingCommand);
        cmd = new CreateCMD(tsTKs, handler);
        cmd.initialize();
        assertThrows(CmdException.invalidEndOfCmdException.class, () -> cmd.checkGrammar() );
        assertEquals(false, cmd.getNormalRun() );
        String message = cmd.getOutputResult();
        assertEquals("Semicolon ';' must be at the end of command.", message);
    }

    @Test
    void testInitialize009() {
        String comingCommand = "CREATE Database;";
        tsTKs = new Tokenizer(comingCommand);
        cmd = new CreateCMD(tsTKs, handler);
        cmd.initialize();
        assertThrows(CmdException.invalidCmdLengthException.class, () -> cmd.checkGrammar() );
        assertEquals(false, cmd.getNormalRun() );
        String message = cmd.getOutputResult();
        assertEquals("Length of this command is invalid.", message);
    }

    @Test
    void testInitialize010() {
        String comingCommand = "CREATE Database testCreateDB;";
        tsTKs = new Tokenizer(comingCommand);
        cmd = new CreateCMD(tsTKs, handler);
        cmd.initialize();
        assertThrows(IOException.conflictFolderException.class, () -> cmd.implement() );
        assertEquals(false, cmd.getNormalRun() );
        String message = cmd.getOutputResult();
        assertEquals("Database 'testCreateDB' has already existed.", message);
    }
}