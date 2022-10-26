package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

class DataColTest {

    DataCol tsCol;

    @BeforeEach
    void setup() {
        tsCol = new DataCol("test1", DataCol.KeyType.NORMAL);
    }

    @Test
    void testGetName() {
        assertEquals("test1", tsCol.getName());
    }

    @Test
    void testSetName() {
        tsCol.setName("test2");
        assertEquals("test2", tsCol.getName());
        tsCol.setName("");
        assertEquals("", tsCol.getName());
    }

    @Test
    void testGetKeyType() {
        assertEquals(DataCol.KeyType.NORMAL, tsCol.getKeyType());
    }

    @Test
    void testSetKeyType() {
        DataCol.KeyType primary = DataCol.KeyType.PRIMARY;
        DataCol.KeyType foreign = DataCol.KeyType.FOREIGN;
        tsCol.setKeyType(DataCol.KeyType.PRIMARY);
        assertEquals(primary, tsCol.getKeyType());
        tsCol.setKeyType(DataCol.KeyType.FOREIGN);
        assertEquals(foreign, tsCol.getKeyType());
    }
}