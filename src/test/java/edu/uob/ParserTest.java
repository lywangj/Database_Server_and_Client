package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.uob.DBcmd.*;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class ParserTest {

    Parser parser;
    String comingCommand;
    DataStore tsDBMap;
    FileHandle handler;
    String expectedOutput;

    @BeforeEach
    void setup(@TempDir File dbDir) {
        handler = new FileHandle(dbDir);
        tsDBMap = handler.getDbStore();

    }

    @Test
    void testUse() {
        comingCommand = "Use database1;";
        parser = new Parser(comingCommand, handler);
        expectedOutput = "Database 'database1' cannot be found.";
        assertEquals("CT", parser.getTokens().getTypeByNum(0));
        assertEquals("USE", parser.getTokens().getValueByNum(0));
        assertEquals("Use", parser.getTokens().getInputValueByNum(0));
        assertEquals(expectedOutput, parser.getOutputResult());
        assertEquals(expectedOutput, parser.getCmd().getOutputResult());
    }

    @Test
    void testUseCmdWithError() {
        comingCommand = "Use database1 %;";
        parser = new Parser(comingCommand, handler);
        expectedOutput = "Grammar error with at least one unexpected keyword '%' is found.";
        assertEquals(expectedOutput, parser.getOutputResult());
    }

    @Test
    void testUndefinedCommandType() {
        comingCommand = "Suse database1 %;";
        parser = new Parser(comingCommand, handler);
        expectedOutput = "Undefined command type 'Suse' is found.";
        assertEquals(expectedOutput, parser.getOutputResult());
    }

}