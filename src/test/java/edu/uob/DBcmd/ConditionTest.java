package edu.uob.DBcmd;

import edu.uob.Exception.CmdException;
import edu.uob.Tokenizer;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

class ConditionTest {

    Tokenizer tsTKs;
    Condition tsCondition;

    @Test
    void testInitialize000() throws CmdException {
        tsTKs = new Tokenizer("mark==007;");
        tsCondition = new Condition(tsTKs,0);
        assertEquals(Condition.Type.INT, tsCondition.getType());
        assertEquals("INT", tsCondition.getType().name());
        assertEquals("mark", tsCondition.getColName());
        assertEquals("%EQ%", tsCondition.getOperator());
        assertEquals("007", tsCondition.getInputValue());
        assertEquals( 7, tsCondition.getINTValue());
        assertNull(tsCondition.getFloatValue());
        assertNull(tsCondition.getBoolValue());
        assertNull(tsCondition.getStrValue());
    }

    @Test
    void testInitialize001() throws CmdException {
        tsTKs = new Tokenizer("WHERE name LIKE 'Dave')OR");
        tsCondition = new Condition(tsTKs,1);
        assertEquals(Condition.Type.STR, tsCondition.getType());
        assertEquals("STR", tsCondition.getType().name());
        assertEquals("name", tsCondition.getColName());
        assertEquals("LIKE", tsCondition.getOperator());
        assertEquals( "'Dave'", tsCondition.getInputValue());
        assertEquals( "Dave", tsCondition.getStrValue());
    }

    @Test
    void testInitialize002() throws CmdException {
        tsTKs = new Tokenizer("WHERE (email5 LIKE 'Dave')");
        tsCondition = new Condition(tsTKs,2);
        assertEquals(Condition.Type.STR, tsCondition.getType());
        assertEquals("STR", tsCondition.getType().name());
        assertEquals("email5", tsCondition.getColName());
        assertEquals("LIKE", tsCondition.getOperator());
        assertEquals( "'Dave'", tsCondition.getInputValue());
        assertEquals( "Dave", tsCondition.getStrValue());
    }

    @Test
    void testInitialize003() throws CmdException {
        tsTKs = new Tokenizer(" == 'Dave'");
        tsCondition = new Condition(tsTKs,0);
        assertNull(tsCondition.getType());
        assertNull(tsCondition.getColName());
        assertNull(tsCondition.getOperator());
        assertNull(tsCondition.getInputValue());
        assertNull(tsCondition.getINTValue());
        assertNull(tsCondition.getFloatValue());
        assertNull(tsCondition.getBoolValue());
        assertNull(tsCondition.getStrValue());
    }

    @Test
    void testInitialize004() throws CmdException {
        tsTKs = new Tokenizer("AND(mark==70);");
        tsCondition = new Condition(tsTKs,0);
        assertEquals(Condition.Type.AND, tsCondition.getType());
        assertEquals("AND", tsCondition.getType().name());
        assertNull(tsCondition.getColName());
        assertNull(tsCondition.getOperator());
        assertNull(tsCondition.getInputValue());
        assertNull(tsCondition.getINTValue());
        assertNull(tsCondition.getFloatValue());
        assertNull(tsCondition.getBoolValue());
        assertNull(tsCondition.getStrValue());
    }

    @Test
    void testInitialize005() throws CmdException {
        tsTKs = new Tokenizer("OR (mark==NULL)");
        tsCondition = new Condition(tsTKs,2);
        assertEquals(Condition.Type.NULL, tsCondition.getType());
        assertEquals("NULL", tsCondition.getType().name());
        assertEquals("mark", tsCondition.getColName());
        assertEquals("%EQ%", tsCondition.getOperator());
        assertEquals("NULL", tsCondition.getInputValue());
        assertNull(tsCondition.getINTValue());
        assertNull(tsCondition.getFloatValue());
        assertNull(tsCondition.getBoolValue());
        assertNull(tsCondition.getStrValue());
    }

    @Test
    void testInitialize006() throws CmdException {
        tsTKs = new Tokenizer("mark==+70.0)");
        tsCondition = new Condition(tsTKs,0);
        assertNull(tsCondition.getINTValue());
        assertEquals( 70.0f, tsCondition.getFloatValue());
    }

    @Test
    void testInitialize007() throws CmdException {
        tsTKs = new Tokenizer("mark==-99970.01)");
        tsCondition = new Condition(tsTKs,0);
        assertNull(tsCondition.getINTValue());
        assertEquals( -99970.01f, tsCondition.getFloatValue());
    }

    @Test
    void testInitialize008() throws CmdException {
        tsTKs = new Tokenizer("mark==-30)");
        tsCondition = new Condition(tsTKs,0);
        assertEquals( -30, tsCondition.getINTValue());
        assertNull(tsCondition.getFloatValue());
    }

    @Test
    void testInitialize009() throws CmdException {
        tsTKs = new Tokenizer("mark==+99)");
        tsCondition = new Condition(tsTKs,0);
        assertEquals( 99, tsCondition.getINTValue());
        assertNull(tsCondition.getFloatValue());
    }

    @Test
    void testInitialize010() throws CmdException {
        tsTKs = new Tokenizer("mark==69.33)");
        tsCondition = new Condition(tsTKs,0);
        assertNull(tsCondition.getINTValue());
        assertEquals( 69.33f, tsCondition.getFloatValue());
    }

    @Test
    void testInitialize011() throws CmdException {
        tsTKs = new Tokenizer("mark==69.9999)");
        tsCondition = new Condition(tsTKs,0);
        assertNull(tsCondition.getINTValue());
        assertEquals( 69.9999f, tsCondition.getFloatValue());
    }

    @Test
    void testInitialize012() throws CmdException {
        tsTKs = new Tokenizer("mark==True)");
        tsCondition = new Condition(tsTKs,0);
        assertEquals( true, tsCondition.getBoolValue());
    }

    @Test
    void testInitialize013() throws CmdException {
        tsTKs = new Tokenizer("mark==FALSE)");
        tsCondition = new Condition(tsTKs,0);
        assertEquals( false, tsCondition.getBoolValue());
    }

    @Test
    void testInitialize014() throws CmdException {
        tsTKs = new Tokenizer("mark=='High')");
        tsCondition = new Condition(tsTKs,0);
        assertEquals( "High", tsCondition.getStrValue());
    }
}
