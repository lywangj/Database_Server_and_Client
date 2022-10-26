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

class JoinCMDTest {

    Tokenizer tsTKs;
    DataStore tsDBMap;
    FileHandle handler;
    DBCmd cmd;

    @BeforeEach
    void setup(@TempDir File dbDir) throws IOException, java.io.IOException, TableException {
        handler = new FileHandle(dbDir);
        tsDBMap=handler.getDbStore();
        handler.createNewFolder("testJoinDB");
        handler.setDbNameInOperating("testJoinDB");
        handler.createNewFile("table1");
        String content = """
                id\tclass\thours\tstudent
                1\tMath\t16\t3
                2\tPhysics\t12\t1
                3\tChemistry\t8\t2
                """;
        handler.writeWholeFile(content, "table1");
        handler.createNewFile("table2");
        String content2 = """
                id\tname\tpass\tmark\taverage
                1\tDave\tTRUE\t1\t1
                2\tBob\tFALSE\t1\t3
                3\tnull\tNULL\t2\t4
                """;
        handler.writeWholeFile(content2, "table2");
    }

    @AfterEach
    void close() {
        assertDoesNotThrow(() -> handler.deleteExistingFile("table1"));
        assertDoesNotThrow(() -> handler.deleteExistingFile("table2"));
        assertDoesNotThrow(() -> handler.deleteExistingFolder("testJoinDB"));
    }


    @Test
    void testInitialize000() {
        String comingCommand = "JOIN table1 AND table2 ON student AND id;";
        tsTKs = new Tokenizer(comingCommand);
        cmd = new JoinCMD(tsTKs, handler);
        cmd.initialize();
        assertEquals("JOIN", cmd.getCmdType());
        assertEquals("table1", cmd.getTBNames().get(0));
        assertEquals("table2", cmd.getTBNames().get(1));
        assertEquals("student", cmd.getColNames().get(0));
        assertEquals("id", cmd.getColNames().get(1));
        assertEquals(true, cmd.getNormalRun() );
        String message = cmd.getOutputResult();
        assertEquals("""
                Successfully join table 'table1' with table 'table2'.
                id\tclass\thours\tname\tpass\tmark\taverage
                1\tMath\t16\tnull\tNULL\t2\t4
                2\tPhysics\t12\tDave\tTRUE\t1\t1
                3\tChemistry\t8\tBob\tFALSE\t1\t3
                """, message);
    }

    @Test
    void testInitialize001() {
        String comingCommand = "JOIN table1 OR table2 ON Name AND email;";
        tsTKs = new Tokenizer(comingCommand);
        cmd = new JoinCMD(tsTKs, handler);
        cmd.initialize();
        assertThrows(CmdException.grammarErrorException.class, () -> cmd.checkGrammar() );
        assertEquals(false, cmd.getNormalRun() );
        String message = cmd.getOutputResult();
        assertEquals("Grammar error with at least one unexpected keyword 'OR' is found.", message);
    }

    @Test
    void testInitialize002() {
        String comingCommand = "JOIN 99 AND table2 INTO Name AND email;";
        tsTKs = new Tokenizer(comingCommand);
        cmd = new JoinCMD(tsTKs, handler);
        cmd.initialize();
        assertThrows(CmdException.grammarErrorException.class, () -> cmd.checkGrammar() );
        assertEquals(false, cmd.getNormalRun() );
        String message = cmd.getOutputResult();
        assertEquals("Grammar error with at least one unexpected keyword '99' is found.", message);
    }

    @Test
    void testInitialize003() {
        String comingCommand = "JOIN table1 AND table2 ON from AND email;";
        tsTKs = new Tokenizer(comingCommand);
        cmd = new JoinCMD(tsTKs, handler);
        cmd.initialize();
        assertThrows(CmdException.grammarErrorException.class, () -> cmd.checkGrammar() );
        assertEquals(false, cmd.getNormalRun() );
        String message = cmd.getOutputResult();
        assertEquals("Grammar error with at least one unexpected keyword 'from' is found.", message);
    }

    @Test
    void testInitialize004() {
        String comingCommand = "JOIN table1 AND table2 ON n@me OR email;";
        tsTKs = new Tokenizer(comingCommand);
        cmd = new JoinCMD(tsTKs, handler);
        cmd.initialize();
        assertThrows(CmdException.grammarErrorException.class, () -> cmd.checkGrammar() );
        assertEquals(false, cmd.getNormalRun() );
        String message = cmd.getOutputResult();
        assertEquals("Grammar error with at least one unexpected keyword 'OR' is found.", message);
    }

    @Test
    void testInitialize005() {
        String comingCommand = "JOIN 'table1' AND table2 ON n@me AND email;";
        tsTKs = new Tokenizer(comingCommand);
        cmd = new JoinCMD(tsTKs, handler);
        cmd.initialize();
//        assertThrows(CmdException.grammarErrorException.class, () -> cmd.checkGrammar() );
        assertEquals(false, cmd.getNormalRun() );
        String message = cmd.getOutputResult();
        assertEquals("Grammar error with at least one unexpected keyword ''table1'' is found.", message);
    }

    @Test
    void testInitialize006() {
        String comingCommand = "JOIN table1 AND table2 ON hours AND id;";
        tsTKs = new Tokenizer(comingCommand);
        cmd = new JoinCMD(tsTKs, handler);
        cmd.initialize();
        assertEquals(true, cmd.getNormalRun() );
        String message = cmd.getOutputResult();
        assertEquals("""
                Successfully join table 'table1' with table 'table2'.
                id\tclass\tstudent\tname\tpass\tmark\taverage
                1\tMath\t3
                2\tPhysics\t1
                3\tChemistry\t2
                """, message);
    }

    @Test
    void testInitialize006b() {
        String comingCommand = "JOIN table1 AND table2 ON id AND id;";
        tsTKs = new Tokenizer(comingCommand);
        cmd = new JoinCMD(tsTKs, handler);
        cmd.initialize();
        assertEquals(true, cmd.getNormalRun() );
        String message = cmd.getOutputResult();
        assertEquals("""
                Successfully join table 'table1' with table 'table2'.
                id\tclass\thours\tstudent\tname\tpass\tmark\taverage
                1\tMath\t16\t3\tDave\tTRUE\t1\t1
                2\tPhysics\t12\t1\tBob\tFALSE\t1\t3
                3\tChemistry\t8\t2\tnull\tNULL\t2\t4
                """, message);
    }

    @Test
    void testInitialize006c() {
        String comingCommand = "JOIN table1 AND table2 ON id AND average;";
        tsTKs = new Tokenizer(comingCommand);
        cmd = new JoinCMD(tsTKs, handler);
        cmd.initialize();
        assertEquals(true, cmd.getNormalRun() );
        String message = cmd.getOutputResult();
        assertEquals("""
                Successfully join table 'table1' with table 'table2'.
                id\tclass\thours\tstudent\tid\tname\tpass\tmark
                1\tMath\t16\t3\t1\tDave\tTRUE\t1
                2\tPhysics\t12\t1
                3\tChemistry\t8\t2\t2\tBob\tFALSE\t1
                """, message);
    }

    @Test
    void testInitialize007() {
        String comingCommand = "JOIN table2 AND table1 ON mark AND id;";
        tsTKs = new Tokenizer(comingCommand);
        cmd = new JoinCMD(tsTKs, handler);
        cmd.initialize();
        assertEquals(true, cmd.getNormalRun() );
        String message = cmd.getOutputResult();
        assertEquals("""
                Successfully join table 'table2' with table 'table1'.
                id\tname\tpass\taverage\tclass\thours\tstudent
                1\tDave\tTRUE\t1\tMath\t16\t3
                2\tBob\tFALSE\t3\tMath\t16\t3
                3\tnull\tNULL\t4\tPhysics\t12\t1
                """, message);
    }

    @Test
    void testInitialize008() {
        String comingCommand = "JOIN table2 AND table1 ON mark AND student;";
        tsTKs = new Tokenizer(comingCommand);
        cmd = new JoinCMD(tsTKs, handler);
        cmd.initialize();
        assertEquals(true, cmd.getNormalRun() );
        String message = cmd.getOutputResult();
        assertEquals("""
                Successfully join table 'table2' with table 'table1'.
                id\tname\tpass\taverage\tid\tclass\thours
                1\tDave\tTRUE\t1\t2\tPhysics\t12
                2\tBob\tFALSE\t3\t2\tPhysics\t12
                3\tnull\tNULL\t4\t3\tChemistry\t8
                """, message);
    }

    @Test
    void testInitialize009() {
        String comingCommand = "JOIN table2 AND table1 ON average AND id;";
        tsTKs = new Tokenizer(comingCommand);
        cmd = new JoinCMD(tsTKs, handler);
        cmd.initialize();
        assertEquals(true, cmd.getNormalRun() );
        String message = cmd.getOutputResult();
        assertEquals("""
                Successfully join table 'table2' with table 'table1'.
                id\tname\tpass\tmark\tclass\thours\tstudent
                1\tDave\tTRUE\t1\tMath\t16\t3
                2\tBob\tFALSE\t1\tChemistry\t8\t2
                3\tnull\tNULL\t2
                """, message);
    }

}