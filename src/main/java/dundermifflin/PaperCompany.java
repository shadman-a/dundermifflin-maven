package dundermifflin;

import dundermifflin.accounting.AccountingDepartment;
import dundermifflin.newyork.NewYorkDistributionCenter;
import dundermifflin.scranton.ScrantonDistributionCenter;
import it.itDepartment;
import thingthatcangowrong.DeliveryRefusedException;
import thingthatcangowrong.DeliveryUnavailableException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PaperCompany<FILE_PATH> {

    Map<String, DistributionCenter> distributionCenters = new HashMap<String, DistributionCenter>();

    itDepartment itDepartment = new itDepartment();

    AccountingDepartment accountingDepartment = new AccountingDepartment();


    List<Order> orders = new ArrayList<Order>();

    Integer orderProcessed = 0;
    Float revenueProcessed = 0F;



    public PaperCompany() {
        distributionCenters.put ("newyork", new NewYorkDistributionCenter());
        distributionCenters.put ("scranton", new ScrantonDistributionCenter());
    }

    private void receiveOrder(Order order) {
        try {
            orders.add(order);
        } catch (Exception e) {
            System.out.println("Unable to receive order. Message: " + e.getMessage());
        }
    }

    public void readFromFile() throws IOException {

        File orderInputFile = new File( "/Users/shadman/eclipse-workspace/dundermifflin-maven/src/main/resources/orders.csv");
        FileInputStream fileInputStream = new FileInputStream(orderInputFile);
        int r = 0;
        StringBuilder stringBuilder = new StringBuilder();
        while((r = fileInputStream.read()) != -1) {
            stringBuilder.append((char)r);
        }
        String orders = stringBuilder.toString();
        System.out.println(orders);
        String[] lines = orders.split("\n");
        for (String line : lines) {
            String[] columns = line.split(",");
            if (columns.length == 4) {
                Order order = new Order(
                        columns[0],
                        columns[1],
                        Integer.parseInt(columns[2]),
                        Float.parseFloat(columns[3])
                );
                receiveOrder(order);
            }

        }
    }

    public void receiveOrders(){
        receiveOrder(new Order("rocket","mars", -4444, 0F));
        receiveOrder(new Order("paperstock","scranton", 200, 40.00F));
        receiveOrder(new Order("kettlebells","newyork", 500, 100.00F));
        receiveOrder(new Order("posters","newyork", 50, 40.00F));
        receiveOrder(new Order("printerpaper","scranton", 5000, 90.00F));
        receiveOrder(new Order("cardboard","newyork", 500, 05.00F));
    }

    public void receiveShipments() {
        distributionCenters.get("newyork").receiveShipment(new DistributionCenter.Product(
                "posters", 500
        ));
        distributionCenters.get("newyork").receiveShipment(new DistributionCenter.Product(
                "cardboard", 500
        ));
        distributionCenters.get("scranton").receiveShipment(new DistributionCenter.Product(
                "paperstock", 500
        ));
        distributionCenters.get("scranton").receiveShipment(new DistributionCenter.Product(
                "printerpaper", 500
        ));
        distributionCenters.get("newyork").receiveShipment(new DistributionCenter.Product(
                "kettlebells", 5
        ));
    }

    public void processOrders() {
        for (Order order : orders) {
            try {
                this.processOrder(order);
            } catch (DeliveryRefusedException e) {
                itDepartment.logIssue("Delivery refused. Message: " + e.getMessage());
                itDepartment.logIssue("Attempting to ship from scranton instead");
                order.closestLocation = "scranton";
                try {
                    this.processOrder(order);
                } catch (Exception error) {
                    System.out.println("Unable to try delivery again after shipping from scranton instead. Message: " + error.getMessage());
                }
            } catch (DeliveryUnavailableException e) {
                itDepartment.logIssue("Delivery unavailable. Message: " + e.getMessage());

                itDepartment.logIssue("Fixing the truck");
                ((ScrantonDistributionCenter) distributionCenters.get("scranton"))
                        .deliveryTruck.setBroken(false);
                try {
                    itDepartment.logIssue("Retrying the order");
                    this.processOrder(order);
                } catch (Exception error) {
                    itDepartment.logIssue("Unable to try delivery again after fixing truck. Message: " + error.getMessage());
                }
            } catch (Exception e) {
                itDepartment.logIssue("Delivery not possible. Message: " + e.getMessage());
            } finally {
                itDepartment.logIssue("========= moving on =========");
            }
        }
    }

    private void processOrder(Order order) throws DeliveryRefusedException, DeliveryUnavailableException {
        this.distributionCenters.get(order.closestLocation).shipProduct(
                new DistributionCenter.Product(
                        order.product,
                        order.size
                )
        );

        /** Order is processed */
        order.setProcessed(true);

        /** Needs to be accurate */
        revenueProcessed += order.price;
        orderProcessed++;

    }

    public void printSummary() {
        System.out.println("Processed " + orderProcessed + " orders");
        System.out.println("Today we had $" + revenueProcessed + " in revenue");
    }

    static class Order {
        public Order(String productName, String closestLocation, int size, float price) {
            this.product = productName;
            this.closestLocation = closestLocation;
            this.size = size;
            this.price = price;
        }

        boolean processed = false;
        public String product;
        public String closestLocation;
        public int size = 0;
        public float price = 0;

        public void setProcessed(boolean processed) {
            this.processed = processed;
        }

        public boolean isProcessed() {
            return processed;
        }
    }



}
