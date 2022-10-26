package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class TokenTest {

    Token tsTK;
    String input;

    @Test
    void testSeparateToList() {
        input = "Use";
        tsTK = new Token(input);
        assertEquals(Token.KeyType.CT, tsTK.getType());
        assertEquals("CT", tsTK.getTypeName());
        assertEquals("USE", tsTK.getValue());
        assertEquals("Use", tsTK.getInput());
    }

    @Test
    void testInteger1() {
        input = "9340";
        tsTK = new Token(input);
        assertEquals(Token.KeyType.INT, tsTK.getType());
        assertEquals("9340", tsTK.getValue());
    }

    @Test
    void testInteger2() {
        input = "-9340";
        tsTK = new Token(input);
        assertEquals(Token.KeyType.INT, tsTK.getType());
        assertEquals("-9340", tsTK.getValue());
    }

    @Test
    void testInteger3() {
        input = "+9340";
        tsTK = new Token(input);
        assertEquals(Token.KeyType.INT, tsTK.getType());
        assertEquals("+9340", tsTK.getValue());
    }

    @Test
    void testInteger4() {
        input = "09";
        tsTK = new Token(input);
        assertEquals(Token.KeyType.INT, tsTK.getType());
        assertEquals("09", tsTK.getValue());
    }

    @Test
    void testFloat1() {
        input = "5.8";
        tsTK = new Token(input);
        assertEquals(Token.KeyType.FLOAT, tsTK.getType());
        assertEquals("5.8", tsTK.getValue());
    }

    @Test
    void testFloat2() {
        input = "+9.99";
        tsTK = new Token(input);
        assertEquals(Token.KeyType.FLOAT, tsTK.getType());
        assertEquals("+9.99", tsTK.getValue());
    }

    @Test
    void testFloat3() {
        input = "-11.23";
        tsTK = new Token(input);
        assertEquals(Token.KeyType.FLOAT, tsTK.getType());
        assertEquals("-11.23", tsTK.getValue());
    }

    @Test
    void testString() {
        input = "'test56 Test@'";
        tsTK = new Token(input);
        assertEquals(Token.KeyType.STR, tsTK.getType());
        assertEquals("test56 Test@", tsTK.getValue());
    }

    @Test
    void testOperator01() {
        input = ">=";
        tsTK = new Token(input);
        assertEquals(Token.KeyType.OP, tsTK.getType());
        assertEquals(">=", tsTK.getValue());
        assertEquals(">=", tsTK.getInput());
    }
}