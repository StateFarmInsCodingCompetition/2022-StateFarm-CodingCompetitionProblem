package com.statefarm.codingcompetition.simpledatatool;

import com.statefarm.codingcompetition.simpledatatool.controller.SimpleDataToolController;
import com.statefarm.codingcompetition.simpledatatool.model.Agent;

import java.util.List;

public class Application {
    public static void main(String[] args) {
        System.out.println("Hello World");
        SimpleDataToolController tool = new SimpleDataToolController();
        Agent agent = new Agent();
        tool.readCsvFile("C:Projects\\Java\\StateFarm-Competition\\src\\main\\resources\\agents.csv", Agent);
    }
}
