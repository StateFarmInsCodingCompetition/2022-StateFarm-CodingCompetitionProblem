package com.statefarm.codingcompetition.simpledatatool.controller;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.statefarm.codingcompetition.simpledatatool.model.Agent;
import com.statefarm.codingcompetition.simpledatatool.model.Claim;
import com.statefarm.codingcompetition.simpledatatool.model.Customer;
import com.statefarm.codingcompetition.simpledatatool.model.Policy;

public class SimpleDataToolController {

    /**
     * Read in a CSV file and return a list of entries in that file
     * 
     * @param <T>
     * @param filePath  Path to the file being read in
     * @param classType Class of entries beng read in
     * @return List of entries from CSV file
     */
    public <T> List<T> readCsvFile(String filePath, Class<T> classType) {
        try{
	        
            File file = new File(filePath);
            //Using the lib that was already in the project
            CsvMapper mapper = new CsvMapper();
            mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
            CsvSchema Schema = CsvSchema.emptySchema().withHeader();
    
            
            MappingIterator<T> it = mapper.readerFor(classType).with(Schema).readValues(file);
            List<T> ans = it.readAll();
            return ans;
            
           
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
    }

    /**
     * Gets the number of open claims
     * 
     * @param claims List of all claims
     * @return number of open claims
     */
    public int getNumberOfOpenClaims(List<Claim> claims) {
        claims.removeIf(c -> !c.getIsClaimOpen());
        return claims.size();
    }

    /**
     * Get the number of customer for an agent id
     * 
     * @param filePath File path to the customers CSV
     * @param agentId  Agent id as int
     * @return number of customer for agent
     */
    public int getNumberOfCustomersForAgentId(String filePath, int agentId) {
        List<Customer> cust = readCsvFile(filePath, Customer.class);
        cust.removeIf(c -> c.getAgentId() != agentId);

        return cust.size();
    }

    /**
     * Get the number of customer for an agent id
     * 
     * @param filePath File path to the customers CSV
     * @param state    Agent id as int
     * @return number of customer for agent
     */
    public int getNumberOfAgentsForState(String filePath, String state) {
        List<Agent> agents = readCsvFile(filePath, Agent.class);
        agents.removeIf(a -> !a.getState().equals(state));

        return agents.size();
    }

    /**
     * Sum total premium for a specific customer id
     * 
     * @param policies   List of all policies
     * @param customerId Customer id as int
     * @return float of monthly premium
     */
    public double sumMonthlyPremiumForCustomerId(List<Policy> policies, int customerId) {
        //Copy the given List as all situations this method is used the original List needs to remain consistent
        List<Policy> policiesCopy = new LinkedList<>(policies);
        policiesCopy.removeIf(p -> p.getCustomerId() != customerId);
        double policyTotal = 0d;
        for(Policy custPolicy: policiesCopy){
            policyTotal += custPolicy.getPremiumPerMonth();
        }
        return policyTotal;
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


        List<Customer> cust = readCsvFile(filePathToCustomer, Customer.class);

        cust.removeIf(c-> !c.getFirstName().equals(firstName));
        cust.removeIf(c -> !c.getLastName().equals(lastName));
        if(cust.size() == 0){
            return null;
        }


        Customer customer = cust.get(0);

        List<Policy> policies = readCsvFile(filePathToPolicy, Policy.class);

        policies.removeIf(p -> p.getCustomerId() != customer.getId());
        List<Integer> policyIds = new LinkedList<>();

        for(Policy p : policies){
            policyIds.add(p.getId());
        }

        List<Claim> claims = readCsvFile(filePathToClaims, Claim.class);

        claims.removeIf(claim -> !policyIds.contains(claim.getPolicyId()));
        claims.removeIf(claim -> !claim.getIsClaimOpen());

        return claims.size();      

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

        List<Customer> cust = readCsvFile(customersFilePath, Customer.class);

        Map<String,Integer> languageSpeakers = new HashMap<>();

        cust.removeIf(c-> !c.getState().equals(state));

        for(Customer customer: cust){
            if(!customer.getPrimaryLanguage().equals("English")){
                languageSpeakersHelper(languageSpeakers, customer.getPrimaryLanguage());
            }

            if(!customer.getSecondaryLanguage().isEmpty() && !customer.getSecondaryLanguage().equals("English")){
                languageSpeakersHelper(languageSpeakers, customer.getSecondaryLanguage());
            }
        }

        List<String> keys = new LinkedList<>(languageSpeakers.keySet());

        keys.sort((k1,k2)-> languageSpeakers.get(k2) - languageSpeakers.get(k1));


        return keys.get(0);
    }

    /**
     * Helper method for getMostSpokenLanguageForState
     * 
     * @param languageSpeakers State abbreviation String
     * @param language  English String for Langauge
     */
    private void languageSpeakersHelper(Map<String,Integer> languageSpeakers,String language){
        if(languageSpeakers.containsKey(language)){
            int speakers = languageSpeakers.get(language);
            speakers++;
            languageSpeakers.put(language,speakers);
            return;
        }
        languageSpeakers.put(language,1);
    }

    /**
     * Returns Customer with the highest, total premium
     * 
     * @param customersFilePath File path to customers CSV
     * @param policies          List of all policies
     * @return Customer that has the highest, total premium as Customer object
     */
    public Customer getCustomerWithHighestTotalPremium(String customersFilePath, List<Policy> policies) {
        List<Customer> cust = readCsvFile(customersFilePath, Customer.class);
        Map<Integer,Double> premiumTotals = new HashMap<>();
        for(Customer customer : cust){
            //Subtract by 1 to fill 0 index to prevent slide
            premiumTotals.put(customer.getId() - 1, sumMonthlyPremiumForCustomerId(policies, customer.getId()));
        }
        List<Integer> keys = new LinkedList<Integer>(premiumTotals.keySet());

        keys.sort((k1,k2) -> (int)(Math.ceil(premiumTotals.get(k2)) - Math.ceil(premiumTotals.get(k1))));

        return cust.get(keys.get(0));
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
                List<Customer> cust = readCsvFile(customersFilePath, Customer.class);
                cust.removeIf(c-> c.getState().equals(state));

                List<Policy> policies = readCsvFile(policiesFilePath, Policy.class);
                for(Customer customer:cust){
                    policies.removeIf(p -> p.getCustomerId() == customer.getId());
                }
                List<Integer> PolicyIds = new LinkedList<>();
                for(Policy custPolicy : policies){
                    PolicyIds.add(custPolicy.getId());
                }

                List<Claim> claims = readCsvFile(claimsFilePath, Claim.class);
                claims.removeIf(c -> !PolicyIds.contains(c.getPolicyId()));
                claims.removeIf(c-> !c.getIsClaimOpen());

        
        return claims.size();
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
                List<Customer> cust = readCsvFile(customersFilePath, Customer.class);
                Map<Integer,Integer> customerAgent = new HashMap<>();
                for(Customer customer:cust){
                    customerAgent.put(customer.getId(),customer.getAgentId());
                }

                List<Policy> polices = readCsvFile(policiesFilePath, Policy.class);
                Map<Integer,Double> agentPremiums = new HashMap<>();
                for(Policy policy: polices){
                    int agentId = customerAgent.get(policy.getCustomerId());
                    if(agentPremiums.containsKey(agentId)){
                        double current = agentPremiums.get(agentId);
                        agentPremiums.put(agentId,current + policy.getPremiumPerMonth());
                    } else {
                        agentPremiums.put(agentId,policy.getPremiumPerMonth());
                    }
                }


        return agentPremiums;
    }
}