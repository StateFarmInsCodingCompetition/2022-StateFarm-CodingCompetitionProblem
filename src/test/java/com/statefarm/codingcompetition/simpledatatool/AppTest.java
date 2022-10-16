package com.statefarm.codingcompetition.simpledatatool;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.statefarm.codingcompetition.simpledatatool.controller.SimpleDataToolController;
import com.statefarm.codingcompetition.simpledatatool.model.Claim;
import com.statefarm.codingcompetition.simpledatatool.model.Customer;
import com.statefarm.codingcompetition.simpledatatool.model.Policy;

/**
 * Unit test for simple App.
 */
public class AppTest {

    private final String agentsFilePath = "src/main/resources/agents.csv";
    private final String claimsFilePath = "src/main/resources/claims.csv";
    private final String customersFilePath = "src/main/resources/customers.csv";
    private final String policiesFilePath = "src/main/resources/policies.csv";

    private static SimpleDataToolController controller;

    @Before
    public void initialize() {
        controller = new SimpleDataToolController();
    }

    // #1 Can read whole customer CSV as List<Customer>
    @Test
    public void readCustomerCSV() {
        List<Customer> customers = controller.readCsvFile(customersFilePath, Customer.class);
        System.out.println(customers.size());

        assertEquals(5000, customers.size());

        Customer customer1 = customers.get(19);
        assertEquals("Myles", customer1.getFirstName());
        assertEquals("Sprigings", customer1.getLastName());

        Customer customer2 = customers.get(1874);
        assertEquals("Bing", customer2.getFirstName());
        assertEquals("Vanns", customer2.getLastName());
        assertEquals("MT", customer2.getState());
        assertEquals("", customer2.getSecondaryLanguage());

        Customer customer3 = customers.get(4006);
        assertEquals("Jephthah", customer3.getFirstName());
        assertEquals("Hattersley", customer3.getLastName());
        assertEquals(37, customer3.getAge());
        assertEquals("MN", customer3.getState());
        assertEquals("Midwest", customer3.getRegion());
        assertEquals("English", customer3.getPrimaryLanguage());
        assertEquals("Spanish", customer3.getSecondaryLanguage());
        assertEquals(719, customer3.getAgentId());
    }

    // #2 Can get number of open claims
    @Test
    public void getNumberOfOpenClaims() {
        List<Claim> claims = controller.readCsvFile(claimsFilePath, Claim.class);
        int numberOfClaims = controller.getNumberOfOpenClaims(claims);

        assertEquals(1916, numberOfClaims);
    }

    // #3 Can get the number of customers that an agent has
    @Test
    public void getNumberOfCustomersForAgent() {
        assertEquals(5, controller.getNumberOfCustomersForAgentId(customersFilePath, 67));
        assertEquals(8, controller.getNumberOfCustomersForAgentId(customersFilePath, 472));
        assertEquals(4, controller.getNumberOfCustomersForAgentId(customersFilePath, 763));
    }

    // #4 Can get the number of agents for a specific state
    @Test
    public void getNumberOfAgentsForState() {
        assertEquals(24, controller.getNumberOfAgentsForState(agentsFilePath, "AZ"));
        assertEquals(26, controller.getNumberOfAgentsForState(agentsFilePath, "IL"));
        assertEquals(26, controller.getNumberOfAgentsForState(agentsFilePath, "GA"));
        assertEquals(105, controller.getNumberOfAgentsForState(agentsFilePath, "TX"));
    }

    // #5 For a specific customer id, total their monthly premium for all their
    // policies
    @Test
    public void sumMonthlyPremiumForCustomerId() {
        List<Policy> policies = controller.readCsvFile(policiesFilePath, Policy.class);
        assertEquals(0d, controller.sumMonthlyPremiumForCustomerId(policies, 0), 0.01d);
        assertEquals(0d, controller.sumMonthlyPremiumForCustomerId(policies, 5001), 0.01d);

        assertEquals(274.75d, controller.sumMonthlyPremiumForCustomerId(policies, 372), 0.02d);
    }

    // #6 For a specific customer name, how many claims does the customer have?
    @Test
    public void getNumberOfOpenClaimsForCustomerName() {
        assertEquals(null, controller.getNumberOfOpenClaimsForCustomerName(customersFilePath, policiesFilePath,
                claimsFilePath, "Jake", "StateFarm"));
        assertEquals(Integer.valueOf(4),
                controller.getNumberOfOpenClaimsForCustomerName(customersFilePath, policiesFilePath,
                        claimsFilePath, "Gabbie", "Copin"));
        assertEquals(Integer.valueOf(0),
                controller.getNumberOfOpenClaimsForCustomerName(customersFilePath, policiesFilePath,
                        claimsFilePath, "Nike", "Redd"));
    }

    // #7 What's the most spoken language besides English for all customers in X
    // state?
    @Test
    public void getMostSpokenLanguageForState() {
        assertEquals("Spanish", controller.getMostSpokenLanguageForState(customersFilePath, "TX"));
        assertEquals("Spanish", controller.getMostSpokenLanguageForState(customersFilePath, "AZ"));
    }

    // #8 What customer has the highest policy premium?
    @Test
    public void getCustomerWithHighestTotalPremium() {
        List<Policy> policies = controller.readCsvFile(policiesFilePath, Policy.class);
        Customer actualCustomer = controller.getCustomerWithHighestTotalPremium(customersFilePath, policies);

        assertEquals("Vyky", actualCustomer.getFirstName());
        assertEquals("South", actualCustomer.getRegion());
        assertEquals("German", actualCustomer.getPrimaryLanguage());
    }

    // #9 How many open claims for all customers in a state?
    @Test
    public void getOpenClaimsForState() {
        assertEquals(3, controller.getOpenClaimsForState(customersFilePath, policiesFilePath, claimsFilePath, "MT"));
        assertEquals(0, controller.getOpenClaimsForState(customersFilePath, policiesFilePath, claimsFilePath, "VT"));
        assertEquals(8, controller.getOpenClaimsForState(customersFilePath, policiesFilePath, claimsFilePath, "NJ"));
    }

    // #10 Total amount of premium by agent by state
    @Test
    public void buildMapOfAgentPremiums() {
        Map<Integer, Double> agentToTotalPremiumMap = controller.buildMapOfAgentPremiums(
                customersFilePath, policiesFilePath);
        assertEquals(890.62d, agentToTotalPremiumMap.get(13), 0.02d);
        assertEquals(572.70d, agentToTotalPremiumMap.get(424), 0.02d);
        assertEquals(null, agentToTotalPremiumMap.get(1001));
    }
}
