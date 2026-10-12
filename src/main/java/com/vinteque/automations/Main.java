package com.vinteque.automations;

public class Main {
    static void main() {
        LinkedInMessagingAutomation automation = new LinkedInMessagingAutomation();

        try {
            // Configuration
            String email = "vinvincent254@gmail.com";  // REPLACE WITH YOUR EMAIL
            String password = "vX/s3K-^zmy?X!u";         // REPLACE WITH YOUR PASSWORD
            String message = "My name is Vincent Kuria. I lead a team at Vinteque Software Developers, where we help businesses get real results from their websites and mobile apps.\n" +
                    "\n" +
                    "We build websites and mobile apps that are fast, well branded, and serving the main business purpose of improving visibility and conversion rate, not just sitting online.\n" +
                    "\n" +
                    "Would you be open to a 10–15 minute conversation to see if there’s any opportunity to improve or optimize your current digital setup?"; // REPLACE WITH YOUR MESSAGE
            int startPage = 19;
            int maxPages = 45; // Number of pages to process

            // Step 1: Login
//            automation.accessTest();

            // Step 2: Navigate to search results
            automation.navigateToSearchResults(startPage);

            // Step 3: Process all connections
            automation.processAllConnections(message,startPage, maxPages);

        } catch (Exception e) {
            System.err.println("Fatal error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
