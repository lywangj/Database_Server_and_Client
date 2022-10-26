package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DataStoreTest {

    DataStore tsDStore;
    DataBase tsDB1;
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

        tsDB1 = new DataBase("DB1", tables);
        tsDStore = new DataStore();
        tsDStore.addDB("DB1", tsDB1);
    }

    @Test
    void testAddDB() {
        assertEquals(1, tsDStore.getDbMap().size());
        assertEquals(tsDB1, tsDStore.getDbMap().get("DB1"));
        tsDStore.addDB("DB2", tsDB1);
        assertEquals(2, tsDStore.getDbMap().size());
    }

    @Test
    void testGetDBByName() {
        assertEquals(tsDB1, tsDStore.getDBByName("DB1"));
    }

    @Test
    void testDeleteDB() {
        tsDStore.deleteDB("DB1");
        assertTrue(tsDStore.getDbMap().isEmpty());
    }

}