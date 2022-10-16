package com.statefarm.codingcompetition.simpledatatool;

import java.util.LinkedList;
import java.util.List;

import com.statefarm.codingcompetition.simpledatatool.sql.DataCore;
import com.statefarm.codingcompetition.simpledatatool.sql.utility.SqlConnector;

public class Application {
    public static void main(String[] args) {
        List<String> cleanArgs = new LinkedList<String>();
        for(String arg: args){
            cleanArgs.add(arg);
        }
        DataCore dataManager = new DataCore();
        SqlConnector.setConnectionString("jdbc:sqlite:statefarm.db");

        if(cleanArgs.contains("-genDB")||true){
            dataManager.generateDatabase();
        };
        
        
        
    }
}
