package edu.uob;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class TokenizerTest {

    Tokenizer tokenizer;
    String comingCommand;
    ArrayList<String> correctAns;
    ArrayList<String> results;

    @Test
    void testSeparateToList000() {
        comingCommand = "USE     DB1;";
        tokenizer = new Tokenizer(comingCommand);
        String[] ans = {"USE", "DB1", ";"};
        correctAns = new ArrayList<>(Arrays.asList(ans));
        results = tokenizer.getValueList();
        assertEquals(correctAns, results);
        assertEquals(Token.KeyType.CT, tokenizer.getTypeList().get(0));
        assertEquals(Token.KeyType.ID, tokenizer.getTypeList().get(1));
        assertEquals(Token.KeyType.SemiCO, tokenizer.getTypeList().get(2));
    }

    @Test
    void testSeparateToList001() {
        comingCommand = "'strTest'";
        tokenizer = new Tokenizer(comingCommand);
        String[] ans = {"strTest"};
        correctAns = new ArrayList<>(Arrays.asList(ans));
        results = tokenizer.getValueList();
        assertEquals(correctAns, results);
        assertEquals(Token.KeyType.STR, tokenizer.getTypeList().get(0));
    }

    @Test
    void testSeparateToList011() {
        comingCommand = "uSe Db1;Create table peo%ple5";
        tokenizer = new Tokenizer(comingCommand);
        String[] ans = {"USE", "Db1", ";", "CREATE", "TABLE", "peo%ple5"};
        correctAns = new ArrayList<>(Arrays.asList(ans));
        results = tokenizer.getValueList();
        assertEquals(correctAns, results);
        assertEquals(Token.KeyType.CT, tokenizer.getTypeList().get(3));
        assertEquals(Token.KeyType.TB, tokenizer.getTypeList().get(4));
        assertEquals(Token.KeyType.ID, tokenizer.getTypeList().get(5));
    }

    @Test
    void testSeparateToList013() {
        comingCommand = "USE DB1;CREATE TABLE peo%ple5";
        tokenizer = new Tokenizer(comingCommand);
        String[] ans = {"USE", "DB1", ";", "CREATE", "TABLE", "peo%ple5"};
        correctAns = new ArrayList<>(Arrays.asList(ans));
        results = tokenizer.getValueList();
        assertEquals(correctAns, results);
        assertEquals(Token.KeyType.CT, tokenizer.getTypeList().get(3));
        assertEquals(Token.KeyType.TB, tokenizer.getTypeList().get(4));
        assertEquals(Token.KeyType.ID, tokenizer.getTypeList().get(5));
    }

    @Test
    void testSeparateToList014() {
        comingCommand = "SELECT * FROM marks ";
        tokenizer = new Tokenizer(comingCommand);
        String[] ans = {"SELECT", "*", "FROM", "marks"};
        correctAns = new ArrayList<>(Arrays.asList(ans));
        results = tokenizer.getValueList();
        assertEquals(correctAns, results);
        assertEquals(Token.KeyType.CT, tokenizer.getTypeList().get(0));
        assertEquals(Token.KeyType.ALL, tokenizer.getTypeList().get(1));
        assertEquals(Token.KeyType.FROM, tokenizer.getTypeList().get(2));
        assertEquals(Token.KeyType.ID, tokenizer.getTypeList().get(3));
    }

    @Test
    void testSeparateToList015() {
        comingCommand = "UPDATE marks SET Name='David', score= 80";  // 'David Lin' ??????
        tokenizer = new Tokenizer(comingCommand);
        String[] ans = {"UPDATE", "marks", "SET", "Name", "=", "David",",", "score", "=", "80"};
        correctAns = new ArrayList<>(Arrays.asList(ans));
        results = tokenizer.getValueList();
        assertEquals(correctAns, results);
        assertEquals(Token.KeyType.ASSIGN, tokenizer.getTypeList().get(4));
        assertEquals(Token.KeyType.STR, tokenizer.getTypeList().get(5));
        assertEquals(Token.KeyType.SET, tokenizer.getTypeList().get(2));
        assertEquals(Token.KeyType.ID, tokenizer.getTypeList().get(3));
    }

    @Test
    void testSeparateToList017() {
        comingCommand = "INSERT INTO table1 VALUES(100);";
        tokenizer = new Tokenizer(comingCommand);
        String[] ans = {"INSERT", "INTO", "table1", "VALUES", "(", "100", ")", ";"};
        correctAns = new ArrayList<>(Arrays.asList(ans));
        results = tokenizer.getValueList();
        assertEquals(correctAns, results);
        assertEquals(Token.KeyType.INTO, tokenizer.getTypeList().get(1));
        assertEquals(Token.KeyType.VALUES, tokenizer.getTypeList().get(3));
        assertEquals(Token.KeyType.RParen, tokenizer.getTypeList().get(6));
    }

    //   INSERT INTO table1 VALUES('Dave', 55, TRUE);
    @Test
    void testSeparateToList018() {
        comingCommand = "INSERT INTO table1 VALUES('Dave', 55, TRUE);";
        tokenizer = new Tokenizer(comingCommand);
        String[] ans = {"INSERT", "INTO", "table1", "VALUES", "(", "Dave", ",", "55", ",", "TRUE", ")", ";"};
        correctAns = new ArrayList<>(Arrays.asList(ans));
        results = tokenizer.getValueList();
        assertEquals(correctAns, results);
    }

    @Test
    void testSeparateToList101() {
        comingCommand = "WHERE=(Name=='Steve')AND(mark>=80);";
        tokenizer = new Tokenizer(comingCommand);
        String[] ans = {"WHERE","=","(","Name","%EQ%","Steve", ")","AND","(","mark","%LT%","80",")",";"};
        correctAns = new ArrayList<>(Arrays.asList(ans));
        results = tokenizer.getValueList();
        assertEquals("%LT%", tokenizer.getValueList().get(10));
        assertEquals(Token.KeyType.OP, tokenizer.getTypeList().get(10));
        assertEquals(correctAns, results);
    }

    @Test
    void testNextToken() {
        comingCommand = "UPDATE marks";
        tokenizer = new Tokenizer(comingCommand);
        assertEquals(0, tokenizer.getCurrNum());
        tokenizer.nextToken();
        assertEquals(1, tokenizer.getCurrNum());
        assertNull(tokenizer.nextToken());
    }

    @Test
    void testGetCurrTKValueAndType() {
        comingCommand = "marks WHERE (pass == FALSE);";
        tokenizer = new Tokenizer(comingCommand);
        assertEquals("marks", tokenizer.getCurrTKValue());
        tokenizer.nextToken();
        assertEquals("WHERE", tokenizer.getCurrTKType());
        tokenizer.nextToken();
        tokenizer.nextToken();
        assertEquals("pass", tokenizer.getCurrTKValue());
        assertEquals("ID", tokenizer.getCurrTKType());
    }

    @Test
    void testGetValueAndTypeByNum() {
        comingCommand = "AND(mark>=-80.33);";
        tokenizer = new Tokenizer(comingCommand);
        assertEquals("AND", tokenizer.getValueByNum(0));
        assertEquals("AND", tokenizer.getTypeByNum(0));
        assertEquals("-80.33", tokenizer.getValueByNum(4));
        assertEquals("FLOAT", tokenizer.getTypeByNum(4));
    }

}