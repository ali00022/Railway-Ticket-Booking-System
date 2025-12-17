import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        TrainDAO dao = new TrainDAO();

        while (true) {
            System.out.println("\n=========================================");
            System.out.println("      INDIAN RAILWAYS MAIN MENU      ");
            System.out.println("=========================================");
            System.out.println("1. Book a New Ticket");
            System.out.println("2. Search Booking Details");
            System.out.println("3. Cancel Reservation");
            System.out.println("4. Exit");
            System.out.print(">> Enter Choice: ");
            
            int choice = sc.nextInt();
            sc.nextLine(); // Fix newline bug

            switch (choice) {
                case 1:
                    bookTicketProcess(dao, sc);
                    break;
                case 2:
                    System.out.print("\nEnter Booking ID to Search: ");
                    int searchId = sc.nextInt();
                    dao.getBookingDetails(searchId);
                    break;
                case 3:
                    System.out.print("\nEnter Booking ID to Cancel: ");
                    int cancelId = sc.nextInt();
                    System.out.print("Are you sure? (yes/no): ");
                    String confirm = sc.next();
                    if (confirm.equalsIgnoreCase("yes")) {
                        dao.cancelBooking(cancelId);
                    } else {
                        System.out.println("Cancellation aborted.");
                    }
                    break;
                case 4:
                    System.out.println("Exiting System. Goodbye!");
                    sc.close();
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid option! Try again.");
            }
        }
    }

    public static void bookTicketProcess(TrainDAO dao, Scanner sc) {
        System.out.println("\n--- ENTER PASSENGER DETAILS ---");
        System.out.print("Full Name: ");
        String name = sc.nextLine();
        System.out.print("Age: ");
        int age = sc.nextInt();
        sc.nextLine(); 
        System.out.print("Gender (M/F): ");
        String gender = sc.nextLine();
        System.out.print("Phone: ");
        String phone = sc.nextLine();

        List<String> cities = dao.getDestinations();
        System.out.println("\n--- SELECT DESTINATION ---");
        for (int i = 0; i < cities.size(); i++) {
            System.out.println((i + 1) + ". " + cities.get(i));
        }
        System.out.print("Enter Choice (Number): ");
        int cityChoice = sc.nextInt();
        if (cityChoice < 1 || cityChoice > cities.size()) {
            System.out.println("[ERROR] Invalid Choice!");
            return;
        }
        String dest = cities.get(cityChoice - 1);

        dao.showTrainsForDestination(dest);
        System.out.print("\nEnter Train ID: ");
        int trainId = sc.nextInt();

        dao.showAddons();
        System.out.print("\nEnter Add-on ID (0 for None): ");
        int addonId = sc.nextInt();

        System.out.println("\n... Processing Payment ...");
        try { Thread.sleep(1000); } catch(Exception e){} 
        
        dao.createBooking(name, age, gender, phone, trainId, addonId);
    }
}