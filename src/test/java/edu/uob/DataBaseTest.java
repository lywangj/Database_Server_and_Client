package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DataBaseTest {

    DataBase tsDB;
    DataTable tsTable1;
    DataCol tsCol1;
    DataRow tsRow1;

    @BeforeEach
    void setup() {
        tsCol1 = new DataCol("head1", DataCol.KeyType.NORMAL);
        ArrayList<DataCol> indexes = new ArrayList<>(List.of(tsCol1));

        ArrayList<String> values = new ArrayList<>(List.of("value1"));
        tsRow1 = new DataRow(0, values);
        ArrayList<DataRow> contents = new ArrayList<>(List.of(tsRow1));

        tsTable1 = new DataTable("table1", indexes, contents);
        ArrayList<DataTable> tables = new ArrayList<>(List.of(tsTable1));

        tsDB = new DataBase("DB1", tables);
    }

    @Test
    void testGetName() {
        assertEquals("DB1", tsDB.getName());
    }

    @Test
    void testSetName() {
        tsDB.setName("DB2");
        assertEquals("DB2", tsDB.getName());
    }

    @Test
    void testGetTBByName() {
        assertEquals(tsTable1, tsDB.getTBByName("table1"));
    }

    @Test
    void testGetTBByNum() {
        assertEquals(tsTable1, tsDB.getTBByNum(0));
    }

    @Test
    void testGetTBNameByNum() {
        assertEquals("table1", tsDB.getTBNameByNum(0));
    }

}