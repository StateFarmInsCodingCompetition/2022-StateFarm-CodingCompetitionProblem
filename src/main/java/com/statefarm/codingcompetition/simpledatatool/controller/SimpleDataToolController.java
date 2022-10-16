package com.statefarm.codingcompetition.simpledatatool.controller;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.statefarm.codingcompetition.simpledatatool.model.Agent;
import com.statefarm.codingcompetition.simpledatatool.model.Claim;
import com.statefarm.codingcompetition.simpledatatool.model.Customer;
import com.statefarm.codingcompetition.simpledatatool.model.Policy;

public class SimpleDataToolController {
    /*public static void main(String[] args) throws Exception {
        List<Customer> customers = readCsvFile("src/main/resources/customers.csv", Customer.class);
    }*/
    /**
     * Read in a CSV file and return a list of entries in that file
     * 
     * @param <T>
     * @param filePath  Path to the file being read in
     * @param classType Class of entries being read in
     * @return List of entries from CSV file
     */
    public <T> List<T> readCsvFile(String filePath, Class<T> classType){
        try {
            /* The first line of each file is the fields of the given class.
             * Place them into the "fields" array.
             */
            BufferedReader br = new BufferedReader(new FileReader(new File(filePath)));
            String[] fields = br.readLine().split(",");

            // Get the accesor and mutator methods from the provided class.
            Method[] classMethods = classType.getDeclaredMethods();

            // Initialize our input and the final object List
            String input = br.readLine();
            List<T> csvObjects = new ArrayList<>();

            // While there are still lines to read...
            while(input != null)
            {
                // Create an empty object of the classType provided
                T object = classType.getConstructor().newInstance();

                // Split the current line by "," and store them in a String Array
                String[] objectInfo = input.split(",");

                // Create a HashMap that maps each field to its String value in the input file
                HashMap<String, String> fieldToValue = new HashMap<>();

                // For each field, map the field to its corresponding value for the object
                for(int i = 0; i < fields.length; i++)
                {
                    fieldToValue.put(fields[i], objectInfo[i]);
                }

                // Now, for each mutator method (set___), we will invoke it on the empty object to set the value to the input.
                for(Method m: classMethods)
                {
                    if (m.getName().contains("set"))
                    {
                        // get the Class of the field
                        Class a =  m.getParameterTypes()[0];
                        // get the field mame
                        String field = (char)(m.getName().charAt(3)+32) + m.getName().substring(4);
                        // Check what the field type is, and format it for method invocation
                        if(a.getName().equals("int"))
                        {
                            m.invoke(object, Integer.parseInt(fieldToValue.get(field)));
                        }
                        else if (a.getName().equals("float"))
                        {
                            m.invoke(object, Float.parseFloat(fieldToValue.get(field)));
                        }
                        else if(a.getName().equals("boolean"))
                        {
                            m.invoke(object, Boolean.parseBoolean(fieldToValue.get(field)));
                        }
                        else {
                            m.invoke(object, fieldToValue.get(field));
                        }
                    }
                }
                // Add the object to the list of objects
                csvObjects.add(object);
                // Move the reader to the next line
                input = br.readLine();
            }

            // Return the list of objects.
            return csvObjects;
        } catch (Exception e)
        {
            // Catch any errors.
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Gets the number of open claims
     * 
     * @param claims List of all claims
     * @return number of open claims
     */
    public int getNumberOfOpenClaims(List<Claim> claims) {
        int numOpenClaims = 0;
        if(!claims.isEmpty()){ //iterate through every claim in the list
            for(Claim claim : claims){ //save the current claim
                if(claim.getIsClaimOpen()) //if the current claim has an open claim, increase the count
                    numOpenClaims++;
            }
        }
        return numOpenClaims;
    }

    /**
     * Get the number of customer for an agent id
     * 
     * @param filePath File path to the customers CSV
     * @param agentId  Agent id as int
     * @return number of customer for agent
     */
    public int getNumberOfCustomersForAgentId(String filePath, int agentId) {
        String line = "";
        int currId;
        int idCount = 0;
        try{
            BufferedReader in = new BufferedReader(new FileReader(filePath));
            line = in.readLine();
            while( (line = in.readLine()) != null ){
                String[] token = line.split(",");
                currId = Integer.parseInt(token[token.length-1]);
                if(currId == agentId)
                    idCount++;
            }
            in.close();
            return idCount;
        }catch (IOException e){
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Get the number of customer for an agent id
     * 
     * @param filePath File path to the customer CSV
     * @param state    String state
     * @return number of customer for agent
     */
    public int getNumberOfAgentsForState(String filePath, String state) {
        int agentId, currId, agentCount = 0;
        try{
            BufferedReader in = new BufferedReader(new FileReader(filePath));
            HashSet<Integer> set = new HashSet(); //this will be used to compile all the found agent Id's
            String line = in.readLine(); //skip the instructions line
            while( (line = in.readLine()) != null ){ //read each line of the csv
                String[] token = line.split(",");
                if(token[3].equals(state) ){ //if the state is found, then add the agent Id to the list
                    agentId = Integer.parseInt(token[0]);
                    set.add(agentId);
                    //since it's a hashset, it will remove duplicates so we're left with an accurate number
                }
            }

            in.close();
            return set.size(); //returns the # of agent Id's
        }catch (IOException e){
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Sum total premium for a specific customer id
     * 
     * @param policies   List of all policies
     * @param customerId Customer id as int
     * @return float of monthly premium
     */
    public double sumMonthlyPremiumForCustomerId(List<Policy> policies, int customerId) {
        // Initialize the total monthly premium to 0
        float monthlyPremium = 0;
        // For each policy, if the customer of that policy is our desired customer, add the premium to the monthlyPremium variable
        for(Policy p : policies)
        {
            if(p.getCustomerId() == customerId)
            {
                monthlyPremium += p.getPremiumPerMonth();
            }
        }

        // return the total monthly premium
        return monthlyPremium;
    }

    /**
     * For a given customer (by first and last names), return the number of open
     * claims they have
     * 
     * @param filePathToCustomer File path to customers CSV
     * @param filePathToPolicy   File path to policies CSV
     * @param filePathToClaims   File path to claims CSV
     * @param firstName          First name of customer to search for
     * @param lastName           Last name of customer to search for
     * @return Number of open claims for customer or null if customer doesn't exist
     */
    public Integer getNumberOfOpenClaimsForCustomerName(String filePathToCustomer, String filePathToPolicy,
            String filePathToClaims, String firstName, String lastName) {
        // Get the list of all customers, policies, and claims
        List<Customer> customers = readCsvFile(filePathToCustomer, Customer.class);
        List<Policy> policies = readCsvFile(filePathToPolicy, Policy.class);
        List<Claim> claims = readCsvFile(filePathToClaims, Claim.class);

        // Create a hashSet to get the ID of the customer requested
        HashSet<Integer> customerId = new HashSet<>();
        Integer numOpenClaims = 0;
        // Find the customer by checking for each customer, do they have the same first and last names.
        for(Customer c: customers)
        {
            if(c.getFirstName().equals(firstName) && c.getLastName().equals(lastName))
            {
                customerId.add(c.getId());
            }
        }
        // If that persoon does not exist, return null
        if (customerId.isEmpty())
            return null;
        // Create a HashSet to keep track of all policy ids tied to the specified customer
        HashSet<Integer> policyIds = new HashSet<>();
        // For each policy, check if it is the customer's, and if so, add it to the policyIds HashSet
        for(Policy p: policies)
        {
            if(customerId.contains(p.getCustomerId()))
            {
                policyIds.add(p.getId());
            }
        }
        // For each claim, check if it is of the customer's policy ids, and if the claim is open
        for(Claim c: claims)
        {
            if(policyIds.contains(c.getPolicyId()) && c.getIsClaimOpen())
            {
                // Increment the number of open claims
                numOpenClaims++;
            }
        }
        // Return the total number of open claims for the specified customer
        return numOpenClaims;
    }

    /**
     * Returns the most spoken language (besides English) for customers in a given
     * state
     * 
     * @param customersFilePath File path to customers CSV
     * @param state             State abbreviation ex: AZ, TX, IL, etc.
     * @return String of language
     */
    public String getMostSpokenLanguageForState(String customersFilePath, String state) {
        // Get the list of all customers
        List<Customer> customers = readCsvFile(customersFilePath, Customer.class);

        // Create a map that maps the language spoken (first and second) to how many people speak them
        HashMap<String, Integer> languageToFrequency = new HashMap<>();
        // For each customer, check if they are in the state.
        for(Customer c: customers)
        {
            if(c.getState().equals(state))
            {
                // Add 1 to the primary language if it exists, otherwise put in 1 as a new element
                languageToFrequency.put(c.getPrimaryLanguage(), languageToFrequency.getOrDefault(c.getPrimaryLanguage(),0)+1);
                if(c.getSecondaryLanguage().equals(""))
                    continue;
                // Only if the customer has a secondary language, we perform the same operation to their second language
                languageToFrequency.put(c.getSecondaryLanguage(), languageToFrequency.getOrDefault(c.getSecondaryLanguage(),0)+1);
            }
        }
        // We remove english, as it is already the most common
        languageToFrequency.remove("English");
        // Iterate through the Map and find the largest frequency, and store its language until all languages have been iterated.
        int maxVal = 0;
        String maxLang = "";
        for(String k: languageToFrequency.keySet())
        {
            if(languageToFrequency.get(k) > maxVal)
            {
                maxLang = k;
                maxVal = languageToFrequency.get(k);
            }
        }
        // Return the name of the language with the largest number of speakers (not including english)
        return maxLang;
    }

    /**
     * Returns Customer with the highest, total premium
     * 
     * @param customersFilePath File path to customers CSV
     * @param policies          List of all policies
     * @return Customer that has the highest, total premium as Customer object
     */
    public Customer getCustomerWithHighestTotalPremium(String customersFilePath, List<Policy> policies) {
        // Get the list of all Customers
        List<Customer> customers = readCsvFile(customersFilePath, Customer.class);

        // Create a map that maps each customer to their total premium
        HashMap<Customer, Double> customerToPremium = new HashMap<>();
        // Computer the premium using the previously defined sumMonthlyPremiumForCustomerId method
        for(Customer c: customers)
        {
            customerToPremium.put(c, sumMonthlyPremiumForCustomerId(policies, c.getId()));
        }
        // Iterate through all Customers to find the one with the largest total monthly premium
        double maxVal = 0;
        Customer maxCustomer = new Customer();
        for(Customer c: customerToPremium.keySet())
        {
            if(customerToPremium.get(c) > maxVal)
            {
                maxCustomer = c;
                maxVal = customerToPremium.get(c);
            }
        }
        // Return the customer with the largest monthly premium
        return maxCustomer;
    }

    /**
     * Returns the total number of open claims for a given state
     * 
     * @param customersFilePath File path to customers CSV
     * @param policiesFilePath  File path to policies CSV
     * @param claimsFilePath    File path to claims CSV
     * @param state             State abbreviation ex: AZ, TX, IL, etc.
     * @return number of open claims as int
     */
    public int getOpenClaimsForState(String customersFilePath, String policiesFilePath, String claimsFilePath,
            String state) {
        // Get the list of all Customers, Policies, and Claims
        List<Customer> customers = readCsvFile(customersFilePath, Customer.class);
        List<Policy> policies = readCsvFile(policiesFilePath, Policy.class);
        List<Claim> claims = readCsvFile(claimsFilePath, Claim.class);

        // Intialize the total number of open claims for a specific state
        int numOpenClaims = 0;
        // Get the list of all customers in the specified state
        HashSet<Integer> inStateCustomerId = new HashSet<>();
        for(Customer c: customers)
        {
            if(c.getState().equals(state))
            {
                // Add a customer if they are within the specified state
                inStateCustomerId.add(c.getId());
            }
        }
        // Initialize the set of all policies that are of customers within the specified state
        HashSet<Integer> inStatePolicyId = new HashSet<>();
        // For each policy, check if the CustomerId is that of someone in the inStateCustomerId set, and if so, add it.
        for(Policy p: policies)
        {
            if(inStateCustomerId.contains(p.getCustomerId()))
            {
                inStatePolicyId.add(p.getId());
            }
        }

        // For each claim, if it is from an inState Policy, and it is open, increment the number of open claims in the state.
        for(Claim c: claims)
        {
            if(inStatePolicyId.contains(c.getPolicyId()) && c.getIsClaimOpen())
            {
                numOpenClaims++;
            }
        }
        // Return the total number of open claims within a specific state.
        return numOpenClaims;
    }

    /**
     * Builds a dictionary/map of agents and their total premium from their
     * customers
     * 
     * @param customersFilePath File path to customers CSV
     * @param policiesFilePath  File path to policies CSV
     * @return Map of agent id as int to agent's total premium as double
     */
    public Map<Integer, Double> buildMapOfAgentPremiums(
            String customersFilePath, String policiesFilePath) {
        // Get the list of all Customers and Policies
        List<Customer> customers = readCsvFile(customersFilePath, Customer.class);
        List<Policy> policies = readCsvFile(policiesFilePath, Policy.class);
        // Initialize a map from each agent to their total premium amount.
        Map<Integer, Double> agentIdToTotalPremium = new HashMap<>();
        for(Customer c: customers) //iterate through each customer to get their agent's total premium
        {
            //Since we're already able to find the monthly premiums, we can reuse our own method to save the premium
            double customerPremium = sumMonthlyPremiumForCustomerId(policies, c.getId());
            //add the premium to our hashmap, which will remove duplicates
            agentIdToTotalPremium.put(c.getAgentId(), agentIdToTotalPremium.getOrDefault(c.getAgentId(), 0.0) + customerPremium);
        }
        return agentIdToTotalPremium;
    }
}