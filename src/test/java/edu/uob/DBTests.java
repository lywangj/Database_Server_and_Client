package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertTrue;

// PLEASE READ:
// The tests in this file will fail by default for a template skeleton, your job is to pass them
// and maybe write some more, read up on how to write tests at
// https://junit.org/junit5/docs/current/user-guide/#writing-tests
final class DBTests {

  private DBServer server;

  // we make a new server for every @Test (i.e. this method runs before every @Test test case)
  @BeforeEach
  void setup(@TempDir File dbDir) throws IOException {
    // Notice the @TempDir annotation, this instructs JUnit to create a new temp directory somewhere
    // and proceeds to *delete* that directory when the test finishes.
    // You can read the specifics of this at
    // https://junit.org/junit5/docs/5.4.2/api/org/junit/jupiter/api/io/TempDir.html

    // If you want to inspect the content of the directory during/after a test run for debugging,
    // simply replace `dbDir` here with your own File instance that points to somewhere you know.
    // IMPORTANT: If you do this, make sure you rerun the tests using `dbDir` again to make sure it
    // still works and keep it that way for the submission.

//    String path = "."+File.separator+"";
//    File homeDirectory = Paths.get(path).toAbsolutePath().toFile();
//    server = new DBServer(homeDirectory);
    server = new DBServer(dbDir);
  }

  // Here's a basic test for spawning a new server and sending an invalid command,
  // the spec dictates that the server respond with something that starts with `[ERROR]`
  @Test
  void testInvalidCmdType() {
    assertTrue(server.handleCommand("foo").startsWith("[ERROR]"));
    assertTrue(server.handleCommand("").startsWith("[ERROR]"));
    assertTrue(server.handleCommand("USE $#$;").startsWith("[ERROR]"));
  }

  @Test
  void testInvalidCmdStyle() {
    assertTrue(server.handleCommand("USE markbook").startsWith("[ERROR]"));
    assertTrue(server.handleCommand("USE data;").startsWith("[ERROR]"));
    assertTrue(server.handleCommand("CREATE DATABASE markbook; ;;;").startsWith("[ERROR]"));
    assertTrue(server.handleCommand("DROP markbook;").startsWith("[ERROR]"));
  }

  @Test
  void testTypo() {
    assertTrue(server.handleCommand("CREATE DATABASE markbook;").startsWith("[OK]"));
    assertTrue(server.handleCommand("USE markbook;").startsWith("[OK]"));
    assertTrue(server.handleCommand("CREATE TABLE marks (name, mark, pass);").startsWith("[OK]"));
    assertTrue(server.handleCommand("CREATE TxxxxE marks (name, mark, pass);").startsWith("[ERROR]"));
    assertTrue(server.handleCommand("JOIN coursework AxD marks ON grade AND id;").startsWith("[ERROR]"));
    assertTrue(server.handleCommand("UPDATE marks SxT mark = 38 WHERE name == 'Clive';").startsWith("[ERROR]"));
    assertTrue(server.handleCommand("DELETE FROM marks WHxRE mark<40;").startsWith("[ERROR]"));
    assertTrue(server.handleCommand("SELECT x x FROM marks;").startsWith("[ERROR]"));
    assertTrue(server.handleCommand("INSERT INTO marks VxxxES ('Peter', 33, FALSE);").startsWith("[ERROR]"));
    assertTrue(server.handleCommand("DROP TABLE courseWork x").startsWith("[ERROR]"));
    assertTrue(server.handleCommand("DROP TABLE maxxxxxxks;").startsWith("[ERROR]"));
    assertTrue(server.handleCommand("DROP database marxxxxxook;").startsWith("[ERROR]"));
    assertTrue(server.handleCommand("DROP TABLE marks;").startsWith("[OK]"));
    assertTrue(server.handleCommand("DROP database markBook;").startsWith("[OK]"));
  }

  @Test
  void testWithoutCreate() {
//    assertTrue(server.handleCommand("CREATE DATABASE markbook;").startsWith("[ERROR]"));
    assertTrue(server.handleCommand("USE markbook;").startsWith("[ERROR]"));
    assertTrue(server.handleCommand("CREATE TABLE marks (name, mark, pass);").startsWith("[ERROR]"));
    assertTrue(server.handleCommand("JOIN coursework AND marks ON grade AND id;").startsWith("[ERROR]"));
    assertTrue(server.handleCommand("UPDATE marks SET mark = 38 WHERE name == 'Clive';").startsWith("[ERROR]"));
    assertTrue(server.handleCommand("DELETE FROM marks WHERE mark<40;").startsWith("[ERROR]"));
    assertTrue(server.handleCommand("SELECT * FROM marks;").startsWith("[ERROR]"));
    assertTrue(server.handleCommand("INSERT INTO marks VALUES ('Peter', 33, FALSE);").startsWith("[ERROR]"));
    assertTrue(server.handleCommand("DROP TABLE courseWork;").startsWith("[ERROR]"));
    assertTrue(server.handleCommand("DROP TABLE marks;").startsWith("[ERROR]"));
    assertTrue(server.handleCommand("DROP database markBook;").startsWith("[ERROR]"));
  }

  @Test
  void testWithoutUse() {
    assertTrue(server.handleCommand("CREATE DATABASE markbook;").startsWith("[OK]"));
//    assertTrue(server.handleCommand("USE markbook;").startsWith("[ERROR]"));
    assertTrue(server.handleCommand("CREATE TABLE marks (name, mark, pass);").startsWith("[ERROR]"));
    assertTrue(server.handleCommand("JOIN coursework AND marks ON grade AND id;").startsWith("[ERROR]"));
    assertTrue(server.handleCommand("UPDATE marks SET mark = 38 WHERE name == 'Clive';").startsWith("[ERROR]"));
    assertTrue(server.handleCommand("DELETE FROM marks WHERE mark<40;").startsWith("[ERROR]"));
    assertTrue(server.handleCommand("SELECT * FROM marks;").startsWith("[ERROR]"));
    assertTrue(server.handleCommand("INSERT INTO marks VALUES ('Peter', 33, FALSE);").startsWith("[ERROR]"));
    assertTrue(server.handleCommand("DROP TABLE courseWork;").startsWith("[ERROR]"));
    assertTrue(server.handleCommand("DROP TABLE marks;").startsWith("[ERROR]"));
    assertTrue(server.handleCommand("DROP database markBook;").startsWith("[OK]"));
  }

  @Test
  void testExample() {
    assertTrue(server.handleCommand("CREATE DATABASE markbooK;").startsWith("[OK]"));
    assertTrue(server.handleCommand("USE markBook;").startsWith("[OK]"));
    assertTrue(server.handleCommand("CREATE TABLE maRks (name, mark, pass);").startsWith("[OK]"));
    assertTrue(server.handleCommand("INSERT INTO Marks VALUES ('Steve', 65, TRUE);").startsWith("[OK]"));
    assertTrue(server.handleCommand("INSERT INTO marks VALUES ('Dave', 55, TRUE);").startsWith("[OK]"));
    assertTrue(server.handleCommand("INSERT INTO marKs VALUES ('Bob', 35, FALSE);").startsWith("[OK]"));
    assertTrue(server.handleCommand("INSERT INTO mArks VALUES ('Clive', 20, FALSE);").startsWith("[OK]"));
    assertTrue(server.handleCommand("SELECT * FROM maRks;").startsWith("[OK]"));
    assertTrue(server.handleCommand("SELECT *   FROM marKs where name    != 'Dave';").startsWith("[OK]"));
    assertTrue(server.handleCommand("SELECT * from Marks WHERE pass == TRUE;").startsWith("[OK]"));
    assertTrue(server.handleCommand("Create Table Coursework (task,grade   )  ;").startsWith("[OK]"));
    assertTrue(server.handleCommand("INSERT INTO couRsework VALUES ('OXO',3);").startsWith("[OK]"));
    assertTrue(server.handleCommand("INSERT INTO courseWork ValueS ('DB',1);").startsWith("[OK]"));
    assertTrue(server.handleCommand("insert INTO coursewoRk VALUES ('OXO',4);").startsWith("[OK]"));
    assertTrue(server.handleCommand("INSERT into coUrsework VALUES ('STAG',2);").startsWith("[OK]"));

    assertTrue(server.handleCommand("JOIN courseWork AND MARKS ON grade AND id;").startsWith("[OK]"));

    assertTrue(server.handleCommand("UPDATE marks SET mark = 38 WHERE name == 'Clive';").startsWith("[OK]"));
    assertTrue(server.handleCommand("UPDATE marKS SET mark = 70 WHERE mark >= 60 ;").startsWith("[OK]"));

    assertTrue(server.handleCommand("SELECT * FROM marks WHERE name == 'Clive';").startsWith("[OK]"));
    assertTrue(server.handleCommand("DELETE FROM maRKs WHERE name == 'Dave';").startsWith("[OK]"));
    assertTrue(server.handleCommand("SELECT * FROM marks;").startsWith("[OK]"));
    assertTrue(server.handleCommand("SELECT * FROM marks WHERE (pass == FALSE) AND (mark > 35);").startsWith("[OK]"));
    assertTrue(server.handleCommand("SELECT * FROM marks WHERE name LIKE 've';").startsWith("[OK]"));
    assertTrue(server.handleCommand("SELECT id FROM marks WHERE pass == FALSE;").startsWith("[OK]"));
    assertTrue(server.handleCommand("SELECT name FROM marks WHERE mark>60;").startsWith("[OK]"));

    assertTrue(server.handleCommand("DELETE FROM mARks WHERE mark<40;").startsWith("[OK]"));
    assertTrue(server.handleCommand("SELECT * FROM marks;").startsWith("[OK]"));
    assertTrue(server.handleCommand("INSERT INTO MArks VALUES ('Peter', 33, FALSE);").startsWith("[OK]"));

    assertTrue(server.handleCommand("JOIN MarKs AND cOUrsework ON id AND id;").startsWith("[OK]"));

    assertTrue(server.handleCommand("alter table maRKS add tmp;").startsWith("[OK]"));
    assertTrue(server.handleCommand("DELETE FROM marks WHERE name == 'Dave';").startsWith("[OK]"));
    assertTrue(server.handleCommand("alter table MARks drop pass;").startsWith("[OK]"));

    assertTrue(server.handleCommand("DROP TABLE courseWORK;").startsWith("[OK]"));
    assertTrue(server.handleCommand("DROP TAbLE mARKs;").startsWith("[OK]"));
    assertTrue(server.handleCommand("DROP database mARKBook;").startsWith("[OK]"));

  }

  // Add more unit tests or integration tests here.
  // Unit tests would test individual methods or classes whereas integration tests are geared
  // towards a specific usecase (i.e. creating a table and inserting rows and asserting whether the
  // rows are actually inserted)

}
