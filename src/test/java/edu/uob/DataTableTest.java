package edu.uob;

import edu.uob.Exception.TableException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DataTableTest {

    DataTable tsTable;

    DataCol tsCol1;
    DataRow tsRow1;

    @BeforeEach
    void setup() {
        DataCol tsCol0 = new DataCol("id", DataCol.KeyType.NORMAL);
        tsCol1 = new DataCol("head1", DataCol.KeyType.NORMAL);
        ArrayList<DataCol> indexes = new ArrayList<>(Arrays.asList(tsCol0, tsCol1));

        ArrayList<String> values = new ArrayList<>(List.of("1", "value1"));
        tsRow1 = new DataRow(0, values);
        ArrayList<DataRow> contents = new ArrayList<>(List.of(tsRow1));

        tsTable = new DataTable("test1", indexes, contents);
    }

    @Test
    void testGetName() {
        assertEquals("test1", tsTable.getName());
    }

    @Test
    void testSetName() {
        tsTable.setName("test2");
        assertEquals("test2", tsTable.getName());
    }

    @Test
    void testGetIndexNum() {
        assertEquals(2, tsTable.getIndexNum());
    }

    @Test
    void testAddIndex() {
        DataCol tsCol2 = new DataCol("head2", DataCol.KeyType.NORMAL);
        tsTable.addIndex(tsCol2);
        assertEquals(3, tsTable.getIndexNum());
        DataCol tsCol3 = new DataCol("head3", DataCol.KeyType.NORMAL);
        tsTable.addIndex(tsCol3);
        assertEquals(4, tsTable.getIndexNum());
    }

    @Test
    void testGetIndexByNum() {
        DataCol tsCol2 = new DataCol("head2", DataCol.KeyType.NORMAL);
        tsTable.addIndex(tsCol2);
        assertEquals(tsCol1, tsTable.getIndexByNum(1));
        assertEquals(tsCol2, tsTable.getIndexByNum(2));
    }

    @Test
    void testGetIdxNameByNum() {
        DataCol tsCol2 = new DataCol("head2", DataCol.KeyType.NORMAL);
        tsTable.addIndex(tsCol2);
        assertEquals("id", tsTable.getIdxNameByNum(0));
        assertEquals("head1", tsTable.getIdxNameByNum(1));
        assertEquals("head2", tsTable.getIdxNameByNum(2));
    }


    @Test
    void testGetRowNum() {
        assertEquals(1, tsTable.getRowNum());
    }

    @Test
    void testAddRow() {
        ArrayList<String> value2 = new ArrayList<>(List.of("2","value2"));
        DataRow tsRow2 = new DataRow(1, value2);
        tsTable.addRow(tsRow2);
        assertEquals(2, tsTable.getRowNum());
        ArrayList<String> value3 = new ArrayList<>(List.of("3","value3"));
        DataRow tsRow3 = new DataRow(2, value3);
        tsTable.addRow(tsRow3);
        assertEquals(3, tsTable.getRowNum());
    }

    @Test
    void testGetItemByNum() {
        ArrayList<String> value2 = new ArrayList<>(List.of("2","value2"));
        DataRow tsRow2 = new DataRow(1, value2);
        tsTable.addRow(tsRow2);
        assertEquals(tsRow1, tsTable.getItemByNum(0));
        assertEquals(tsRow2, tsTable.getItemByNum(1));
    }

    @Test
    void testGetValFrom2D() {
        ArrayList<String> value2 = new ArrayList<>(List.of("2","value2"));
        DataRow tsRow2 = new DataRow(1, value2);
        tsTable.addRow(tsRow2);
        assertEquals("value1", tsTable.getValFrom2D(0, 1));
        assertEquals("value2", tsTable.getValFrom2D(1,1));
    }

    @Test
    void testFindMaxId() throws TableException {
        ArrayList<String> value2 = new ArrayList<>(List.of("2","value2"));
        DataRow tsRow2 = new DataRow(1, value2);
        tsTable.addRow(tsRow2);
        assertEquals(2, tsTable.findMaxId());
        ArrayList<String> value5 = new ArrayList<>(List.of("5","value5"));
        DataRow tsRow5 = new DataRow(4, value5);
        tsTable.addRow(tsRow5);
        assertEquals(5, tsTable.findMaxId());
    }

    @Test
    void testGetColNames() {
        ArrayList<String> expectedResult = new ArrayList<>(Arrays.asList("id","head1"));
        assertEquals(expectedResult, tsTable.getColNames());
    }

    @Test
    void testPrintCols() {
        String expectedResult= "id\thead1\n";
        assertEquals(expectedResult, tsTable.printCols());
    }

    @Test
    void testPrintRows() {
        ArrayList<String> value2 = new ArrayList<>(List.of("2","value2"));
        DataRow tsRow2 = new DataRow(1, value2);
        tsTable.addRow(tsRow2);
        ArrayList<String> value5 = new ArrayList<>(List.of("5","value5"));
        DataRow tsRow5 = new DataRow(4, value5);
        tsTable.addRow(tsRow5);
        String expectedResult= """
                1\tvalue1
                2\tvalue2
                5\tvalue5
                """;
        assertEquals(expectedResult, tsTable.printRows());
    }

    @Test
    void testGetRowsWidth() {
        assertEquals(2, tsTable.getRowsWidth());
    }

    @Test
    void testFillInAllBlankRows() {
        ArrayList<String> value2 = new ArrayList<>(List.of("2","value2"));
        DataRow tsRow2 = new DataRow(1, value2);
        tsTable.addRow(tsRow2);
        DataCol tsCol2 = new DataCol("head2", DataCol.KeyType.NORMAL);
        tsTable.addIndex(tsCol2);
        assertEquals(2, tsTable.getRowsWidth());
        tsTable.fillInAllBlankRows();
        assertEquals(3, tsTable.getRowsWidth());
        assertEquals("null", tsTable.getValFrom2D(0,2));
        assertEquals("null", tsTable.getValFrom2D(1,2));
    }

    @Test
    void testDropElementByColNum() {
        ArrayList<String> value2 = new ArrayList<>(List.of("2","value2"));
        DataRow tsRow2 = new DataRow(1, value2);
        tsTable.addRow(tsRow2);

        assertEquals(2, tsTable.getRowsWidth());
        tsTable.dropElementByColNum(1);
        assertEquals(1, tsTable.getRowsWidth());

    }

    @Test
    void testDropRowByRowNum() {
        ArrayList<String> value2 = new ArrayList<>(List.of("2","value2"));
        DataRow tsRow2 = new DataRow(1, value2);
        tsTable.addRow(tsRow2);
        assertEquals(2, tsTable.getRowNum());
        tsTable.dropRowByRowNum(1);
        assertEquals(1, tsTable.getRowNum());
        assertEquals(1, tsTable.getRows().size());

    }


}