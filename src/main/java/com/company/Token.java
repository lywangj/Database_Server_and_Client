package com.company;

//import java.util.regex.*;

public class Token {

    private KeyType type;
    private final String value;
    private final String input;

    public Token(String wrd){
        input = wrd;
        type = categorize(wrd);
        if(type==KeyType.ID){
            classifyToNumbers(wrd);
            classifyToString(wrd);
        }
        value = normalizeCase(wrd);
    }

    public enum KeyType{
        CT,       // <CommandType>
        FROM,       // "FROM"
        WHERE,       // "WHERE"
        VALUES,       // "VALUES"
        SET,       // "SET"
        ON,       // "ON"
        INTO,       // "INTO"
        ADD,      // "ADD"
        DROP,     // "DROP"         // can be CT and just DROP
        DB,       // "DATABASE",
        TB,       // "TABLE",
        AND,       // "AND", "OR"
        OR,       // "OR"
        ALL,      // "*"                   // Why to deal with this?
        LParen,   //  "("
        RParen,   //  ")"
        ASSIGN,   //  "="
        Comma,    //  ","
        OP,       // "==" | ">" | "<" | ">=" | "<=" | "!=" | "LIKE"
        INT,      // "10"
        FLOAT,    // "-9.99"
        BOOL,     // "TRUE", "FALSE"
        STR,      // "' ... '"            // should keep ' ' in value?  NO!!!
        NULL,     // "NULL"
        ID,       //
        SemiCO,   // ";"
    }

    private KeyType categorize(String wrd){

        String upperCaseWrd = wrd.toUpperCase();

        return switch (upperCaseWrd) {
            case "USE", "CREATE", "ALTER", "INSERT", "SELECT",
                    "UPDATE", "DELETE", "JOIN" -> KeyType.CT;
            case "FROM" -> KeyType.FROM;
            case "WHERE" -> KeyType.WHERE;
            case "VALUES" -> KeyType.VALUES;
            case "SET" -> KeyType.SET;
            case "ON" -> KeyType.ON;
            case "INTO" -> KeyType.INTO;
            case "ADD" -> KeyType.ADD;
            case "DROP" -> KeyType.DROP;
            case "AND" -> KeyType.AND;
            case "OR" -> KeyType.OR;
            case "DATABASE" -> KeyType.DB;
            case "TABLE" -> KeyType.TB;
            case "*" -> KeyType.ALL;
            case ";" -> KeyType.SemiCO;
            case "(" -> KeyType.LParen;
            case ")" -> KeyType.RParen;
            case "=" -> KeyType.ASSIGN;
            case "," -> KeyType.Comma;
            case "==", ">", "<", ">=", "<=", "!=", "LIKE",
                    "%EQ%", "%LT%", "%ST%", "%NE%" -> KeyType.OP;
            case "NULL" -> KeyType.NULL;
            case "TRUE", "FALSE" -> KeyType.BOOL;
            default -> KeyType.ID;
        };
    }

    private void classifyToNumbers(String wrd){
        if(wrd.matches("[0-9+-]+")){
            type = KeyType.INT;
        } else if(wrd.matches("^[0-9.+-]+$")){
            type = KeyType.FLOAT;
        }
    }

    private void classifyToString(String wrd){
        if(wrd.charAt(0)==39 && wrd.charAt(wrd.length()-1)==39 ){
            type = KeyType.STR;
        }
    }

    private String normalizeCase(String wrd){
        return switch (type) {
            case ID -> wrd;
            case STR -> removeQuotes(wrd);
            default -> wrd.toUpperCase();
        };
    }

    private String removeQuotes(String wrd){
        return wrd.substring(1,wrd.length()-1);
    }

    public KeyType getType(){ return type; }

    public String getValue(){ return value; }

    public String getInput(){ return input; }

    public String getTypeName(){ return type.name(); }

}
