package com.statefarm.codingcompetition.simpledatatool.sql;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import com.statefarm.codingcompetition.simpledatatool.controller.SimpleDataToolController;
import com.statefarm.codingcompetition.simpledatatool.model.Agent;
import com.statefarm.codingcompetition.simpledatatool.model.Customer;
import com.statefarm.codingcompetition.simpledatatool.sql.utility.SqlConnector;


/**
 * Class for mangeing Database Processing
 */
public class DataCore {
    
    //Path to DDL for design
    private static String DDLPath = "src/main/resources/DDL.sql";


    //CSV Paths
    private final String agentsFilePath = "src/main/resources/agents.csv";
    private final String claimsFilePath = "src/main/resources/claims.csv";
    private final String customersFilePath = "src/main/resources/customers.csv";
    private final String policiesFilePath = "src/main/resources/policies.csv";

    //SimpleDataTool for CSV Import
    private SimpleDataToolController dataToolController;

    //Template Insert Strings
    private final String agentInsert = "REPLACE INTO agent (id,first_name,last_name,'state',region,pri_lang,sec_lang) VALUES (%d,\"%s\",\"%s\",\"%s\",\"%s\") ";
    private final String customerInsert = "REPLACE INTO customer (id,first_name,last_name,'state',region,agent_id,pri_lang,sec_lang) VALUES (%d,\"%s\",\"%s\",\"%s\",\"%s\",%d,%s,\"%s\")";
    private final String policyInsert = "REPLACE INTO 'policy' (id,customer_id,startDate,premiumPerMonth) VALUES (%d,%d,%d,%f)";
    private final String claimInsert = "REPLACE INTO claim (id,customer_id,startDate,premiumPerMonth) VALUES (%d,%d,%d,%f)";
    //private SimpleDateFormat DateFormat;

    public DataCore(){
        //Create and instance of the Simple Data tool to pull the CSVs
        dataToolController = new SimpleDataToolController();
        //DateFormat = new SimpleDateFormat("MM/DD/YYYY")
    }

    /**Method to use the DDL in Resources to generate the Tables for the database and then call helper methods to to use the insert strings to push CSV data to the DB
     * 
     */
    public void generateDatabase() {
        Connection dbConnection = SqlConnector.createConnection();
        

        try{
            File DDLFile = new File(DDLPath);
            Scanner reader = new Scanner(DDLFile);
            
            Statement statement = dbConnection.createStatement();
            statement.setQueryTimeout(30);
            while(reader.hasNext()){
                statement.addBatch(reader.nextLine());
                statement.executeBatch();
            }
            generateAgentDB(dbConnection);
            generateCustomerDB(dbConnection);

            //Closing to prevent Memory Leaks
            reader.close(); 
            dbConnection.close();

        } catch(Exception e ){
            e.printStackTrace();
        }
    }


    /**
     * Helper Method for generating AgentDB
     * @param dbConnection Active Database connection
     */
    private void generateAgentDB(Connection dbConnection){
        try{
        List<Agent> agents = dataToolController.readCsvFile(agentsFilePath,Agent.class);
        Statement state = dbConnection.createStatement();
        state.setQueryTimeout(30);

        for(Agent agent: agents){
            state.execute(String.format(agentInsert, agent.getId(),agent.getFirstName(),agent.getLastName(),agent.getState(),agent.getRegion(),agent.getPrimaryLanguage(),agent.getSecondaryLanguage()));
        }
        state.close();
    } catch (SQLException e){
        e.printStackTrace();
    }

    }

    /**
     * Helper Method for generating CustomerDB
     * @param dbConnection Active Database connection
     */
    private void generateCustomerDB(Connection dbConnection){
        try{List<Customer> cust = dataToolController.readCsvFile(customersFilePath,Customer.class);
        Statement state = dbConnection.createStatement();
        state.setQueryTimeout(30);
        for(Customer customer: cust){
            state.execute(String.format(customerInsert, customer.getId(),customer.getFirstName(),customer.getLastName(),customer.getState(),customer.getRegion(),customer.getAgentId(),customer.getPrimaryLanguage(),customer.getSecondaryLanguage()));
        }

        state.close();
    } catch (SQLException e){
        e.printStackTrace();
    }

    }
    /* 
    private void generatePolicyDB(Connection dbConnect){
        try{List<Policy> policy = dataToolController.readCsvFile(customersFilePath,Customer.class);
            Statement state = dbConnection.createStatement();
            state.setQueryTimeout(30);
            for(Policy customer: cust){
                state.addBatch(String.format(policyInsert, customer.getId(),customer.getFirstName(),customer.getLastName(),customer.getState(),customer.getRegion(),customer.getAgentId(),customer.getPrimaryLanguage(),customer.getSecondaryLanguage()));
            }
            state.executeBatch();
    
            state.close();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    private generateClaimDB(Connection dbConnection){
        try{List<Claim> cust = dataToolController.readCsvFile(customersFilePath,Customer.class);
            Statement state = dbConnection.createStatement();
            state.setQueryTimeout(30);
            for(Claim claim: cust){
                state.addBatch(String.format(claimInsert, ));
            }
            state.executeBatch();
    
            state.close();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }
    */
}
