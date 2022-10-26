package com.company.DBcmd;

import com.company.*;
import com.company.Exception.CmdException;
import com.company.Exception.IOException;
import com.company.Exception.TableException;

import java.util.ArrayList;

public class AlterCMD extends DBCmd {

    public AlterCMD(Tokenizer TKs, FileHandle dbHandler){
        handler = dbHandler;
        tokens = TKs;
        normalRun = true;
    }

    public void initialize() {
        try{
            checkGrammar();
            storeCmdInformation();
            implementReadWrite();
            query();
        } catch (Exception e){
            normalRun = false;
            outputResult = e.toString();
        }
    }

    @Override
    public void query(){
        int size = tableNames.size();
        String tableName = tableNames.get(size-1);
        String colName = colNames.get(size-1);
        outputResult = "Successfully "+action+" a column '"+colName+"', in table '"+tableName+"'.";
    }

    public void implementReadWrite() throws IOException, TableException, java.io.IOException {
        String TBName = tableNames.get(0).toLowerCase();
        checkFileExists(tableNames.get(0));
        handler.storeInUseDBInMemory();
        handler.deleteExistingFile(TBName);
        if(action.equals("add")){
            handler.addColsInTable(colNames, TBName);
        } else {
            handler.dropColsInTable(colNames, TBName);
        }
        String wholeContent = handler.outputTableToString(TBName);
        handler.createNewFile(TBName);
        handler.writeWholeFile(wholeContent, TBName);
    }

    @Override
    public void storeCmdInformation(){
        cmdType = tokens.getValueByNum(0);
        tableNames = new ArrayList<>();
        tableNames.add(tokens.getInputValueByNum(2));
        colNames = new ArrayList<>();
        colNames.add(tokens.getInputValueByNum(4));
    }

    // override
    public void checkGrammar() throws CmdException {
        Tokenizer model1 = new Tokenizer("ALTER TABLE table1 ADD score;");
        Tokenizer model2 = new Tokenizer("ALTER TABLE table1 DROP score;");
        checkLengthOfCmd(model1);
        if(compareTypeList(model1, tokens)){
            action = "add";
        } else if (compareTypeList(model2, tokens)){
            action = "drop";
        } else {
            Integer num = getNumOfFirstError(model1);
            String errorTK = tokens.getInputValueByNum(num);
            throw new CmdException.grammarErrorException(errorTK);
        }
    }

    public String getOutputResult() { return outputResult; }

    public String query(DBServer s){
        return outputResult;
    }
}
