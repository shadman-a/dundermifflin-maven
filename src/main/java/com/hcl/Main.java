package com.hcl;

import dundermifflin.PaperCompany;

import java.io.IOException;


public class Main {

	public static void main(String[] args) throws IOException {
		PaperCompany dunderMifflin = new PaperCompany();
		dunderMifflin.receiveShipments();
		dunderMifflin.readFromFile();
		//dunderMifflin.receiveOrders();
		dunderMifflin.processOrders();
		dunderMifflin.printSummary();

	}

}
