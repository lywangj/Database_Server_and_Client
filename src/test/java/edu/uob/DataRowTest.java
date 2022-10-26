package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class DataRowTest {

    DataRow tsRow;

    @BeforeEach
    void setup() {
        ArrayList<String> values = new ArrayList<>();
        tsRow = new DataRow(0, values);
    }

    @Test
    void testGetNum() {
        assertEquals(0, tsRow.getNum());
    }

    @Test
    void testSetNum() {
        tsRow.setNum(1);
        assertEquals(1, tsRow.getNum());
    }

    @Test
    void testPrintContents() {
        ArrayList<String> values = new ArrayList<>(Arrays.asList("1","Steve", "65", "TRUE"));
        tsRow = new DataRow(0, values);
        String expectedResult= "1\tSteve\t65\tTRUE\n";
        assertEquals(expectedResult, tsRow.printContents());
    }

    @Test
    void testDropElementByColNum() {
        ArrayList<String> values = new ArrayList<>(Arrays.asList("1","Steve", "65", "TRUE"));
        tsRow = new DataRow(0, values);
        tsRow.dropElementByColNum(2);
        String expectedResult= "1\tSteve\tTRUE\n";
        assertEquals(expectedResult, tsRow.printContents());
    }
}