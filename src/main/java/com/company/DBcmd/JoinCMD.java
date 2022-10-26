package com.company.DBcmd;

import com.company.*;
import com.company.Exception.CmdException;
import com.company.Exception.IOException;

import java.util.ArrayList;

public class JoinCMD extends DBCmd {

    public JoinCMD(Tokenizer TKs, FileHandle dbHandler){
        handler = dbHandler;
        tokens = TKs;
        normalRun = true;
    }

    public void initialize() {
        try{
            checkGrammar();
            storeCmdInformation();
            implement();
            query();
        } catch (Exception e){
            normalRun = false;
            outputResult = e.toString();
        }
    }

    @Override
    public void query(){
        int size = tableNames.size();
        String tableName1 = tableNames.get(size-2);
        String tableName2 = tableNames.get(size-1);
        String comment="Successfully "+action+" table '"+tableName1+
                "' with table '"+tableName2+"'.";
        outputResult = comment+"\n"+outputResult;
    }

    @Override
    public void implement() throws IOException, java.io.IOException {
        String TBName1 = tableNames.get(0).toLowerCase();
        String TBName2 = tableNames.get(1).toLowerCase();
        checkFileExists(tableNames.get(0));
        checkFileExists(tableNames.get(1));
        handler.storeInUseDBInMemory();
        handler.joinTwoTables(TBName1, TBName2, colNames.get(0),colNames.get(1));
        outputResult = handler.outputTableToString(TBName1);
    }

    @Override
    public void storeCmdInformation(){
        cmdType = tokens.getValueByNum(0);
        action="join";
        tableNames = new ArrayList<>();
        tableNames.add(tokens.getInputValueByNum(1));
        tableNames.add(tokens.getInputValueByNum(3));
        colNames = new ArrayList<>();
        colNames.add(tokens.getInputValueByNum(5));
        colNames.add(tokens.getInputValueByNum(7));
    }

    @Override
    public void checkGrammar() throws CmdException {
        Tokenizer model1 = new Tokenizer(
                "JOIN table1 AND table2 ON Name AND email;");
        checkLengthOfCmd(model1);
        Integer num = getNumOfFirstError(model1);
        if(num!=0){
            String errorTK = tokens.getInputValueByNum(num);
            throw new CmdException.grammarErrorException(errorTK);
        }
    }

    public String getOutputResult() { return outputResult; }

}
