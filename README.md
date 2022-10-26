# Database_Server_and_Client
Implement create, use, select, join, alter, drop, delete SQL queries and performed data manipulation.

Operating querues include the commands,

```
<Command>        ::=  <CommandType> ";"
<CommandType>    ::=  <Use> | <Create> | <Drop> | <Alter> | <Insert> | <Select> | <Update> | <Delete> | <Join>
<Use>            ::=  "USE " <DatabaseName>
<Create>         ::=  <CreateDatabase> | <CreateTable>
<CreateDatabase> ::=  "CREATE DATABASE " <DatabaseName>
<CreateTable>    ::=  "CREATE TABLE " <TableName> | "CREATE TABLE " <TableName> "(" <AttributeList> ")"
<Drop>           ::=  "DROP " <Structure> " " <StructureName>
<Structure>      ::=  "DATABASE" | "TABLE"
<Alter>          ::=  "ALTER TABLE " <TableName> " " <AlterationType> " " <AttributeName>
<AlterationType> ::=  "ADD" | "DROP"
<Insert>         ::=  "INSERT INTO " <TableName> " VALUES(" <ValueList> ")"
<ValueList>      ::=  <Value> | <Value> "," <ValueList>
<Value>          ::=  "'" <StringLiteral> "'" | <BooleanLiteral> | <FloatLiteral> | <IntegerLiteral> | "NULL"
<IntegerLiteral> ::=  <DigitSequence> | "-" <DigitSequence> | "+" <DigitSequence> 
<FloatLiteral>   ::=  <DigitSequence> "." <DigitSequence> | "-" <DigitSequence> "." <DigitSequence> | "+" <DigitSequence> "." <DigitSequence>
<BooleanLiteral> ::=  "TRUE" | "FALSE"
<CharLiteral>    ::=  <Space> | <Letter> | <Symbol>
<StringLiteral>  ::=  "" | <CharLiteral> | <CharLiteral> <StringLiteral>
<Select>         ::=  "SELECT " <WildAttribList> " FROM " <TableName> | "SELECT " <WildAttribList> " FROM " <TableName> " WHERE " <Condition> 
<WildAttribList> ::=  <AttributeList> | "*"
<AttributeList>  ::=  <AttributeName> | <AttributeName> "," <AttributeList>
<Condition>      ::=  "(" <Condition> ")AND(" <Condition> ")" | "(" <Condition> ")OR(" <Condition> ")" | <AttributeName> <Operator> <Value>
<Operator>       ::=  "==" | ">" | "<" | ">=" | "<=" | "!=" | " LIKE "
<Update>         ::=  "UPDATE " <TableName> " SET " <NameValueList> " WHERE " <Condition> 
<NameValueList>  ::=  <NameValuePair> | <NameValuePair> "," <NameValueList>
<NameValuePair>  ::=  <AttributeName> "=" <Value>
<Delete>         ::=  "DELETE FROM " <TableName> " WHERE " <Condition>
<Join>           ::=  "JOIN " <TableName> " AND " <TableName> " ON " <AttributeName> " AND " <AttributeName>
<TableName>      ::=  <PlainText>
<ColumnName>     ::=  <PlainText>
<DatabaseName>   ::=  <PlainText>
<AttributeName>  ::=  <PlainText>
<PlainText>      ::=  <Letter> | <Digit> | <Letter> <PlainText> | <Digit> <PlainText>
<DigitSequence>  ::=  <Digit> | <Digit> <DigitSequence>
<Digit>          ::=  "0" | "1" | "2" | "3" | "4" | "5" | "6" | "7" | "8" | "9"
<Uppercase>      ::=  "A" | "B" | "C" | "D" | "E" | "F" | "G" | "H" | "I" | "J" | "K" | "L" | "M" | "N" | "O" | "P" | "Q" | "R" | "S" | "T" | "U" | "V" | "W" | "X" | "Y" | "Z"
<Lowercase>      ::=  "a" | "b" | "c" | "d" | "e" | "f" | "g" | "h" | "i" | "j" | "k" | "l" | "m" | "n" | "o" | "p" | "q" | "r" | "s" | "t" | "u" | "v" | "w" | "x" | "y" | "z"
<Letter>         ::=  <Uppercase> | <Lowercase>
<Symbol>         ::=  "!" | "#" | "$" | "%" | "&" | "(" | ")" | "*" | "+" | "," | "-" | "." | "/" | ":" | ";" | ">" | "=" | "<" | "?" | "@" | "[" | "\" | "]" | "^" | "_" | "`" | "{" | "}" | "~"
<Space>          ::=  " "
```
