import java.util.ArrayList;
import java.util.Scanner;

public class BloodBankSystem {

    // ---------- Donor class ----------
    static class Donor {
        String name;
        int age;
        String bloodGroup;
        String contact;

        Donor(String name, int age, String bloodGroup, String contact) {
            this.name = name;
            this.age = age;
            this.bloodGroup = bloodGroup;
            this.contact = contact;
        }

        void display() {
            System.out.println("Name: " + name + " | Age: " + age +
                    " | Blood Group: " + bloodGroup + " | Contact: " + contact);
        }
    }

    // ---------- Data storage ----------
    static ArrayList<Donor> donors = new ArrayList<>();

    // Blood groups supported
    static String[] bloodGroups = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
    static int[] inventory = new int[bloodGroups.length]; // units available per group

    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        int choice;

        do {
            printMenu();
            choice = sc.nextInt();
            sc.nextLine(); // consume leftover newline

            switch (choice) {
                case 1:
                    addDonor();
                    break;
                case 2:
                    viewDonors();
                    break;
                case 3:
                    searchDonorsByGroup();
                    break;
                case 4:
                    addBloodStock();
                    break;
                case 5:
                    issueBlood();
                    break;
                case 6:
                    viewInventory();
                    break;
                case 7:
                    System.out.println("Exiting program. Thank you!");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
            System.out.println();
        } while (choice != 7);

        sc.close();
    }

    static void printMenu() {
        System.out.println("===== BLOOD BANK INVENTORY & DONOR MATCHER =====");
        System.out.println("1. Register a Donor");
        System.out.println("2. View All Donors");
        System.out.println("3. Find Donors by Blood Group");
        System.out.println("4. Add Blood Stock to Inventory");
        System.out.println("5. Issue Blood (Check Availability)");
        System.out.println("6. View Full Inventory");
        System.out.println("7. Exit");
        System.out.print("Enter your choice: ");
    }

    // ---------- Donor management ----------
    static void addDonor() {
        System.out.print("Enter donor name: ");
        String name = sc.nextLine();

        System.out.print("Enter donor age: ");
        int age = sc.nextInt();
        sc.nextLine();

        String bg = readValidBloodGroup();

        System.out.print("Enter contact number: ");
        String contact = sc.nextLine();

        if (age < 18 || age > 65) {
            System.out.println("Donor not eligible (age must be between 18 and 65). Not registered.");
            return;
        }

        donors.add(new Donor(name, age, bg, contact));
        System.out.println("Donor registered successfully!");
    }

    static void viewDonors() {
        if (donors.isEmpty()) {
            System.out.println("No donors registered yet.");
            return;
        }
        System.out.println("---- All Registered Donors ----");
        for (Donor d : donors) {
            d.display();
        }
    }

    static void searchDonorsByGroup() {
        String bg = readValidBloodGroup();
        boolean found = false;

        System.out.println("---- Donors with Blood Group " + bg + " ----");
        for (Donor d : donors) {
            if (d.bloodGroup.equalsIgnoreCase(bg)) {
                d.display();
                found = true;
            }
        }
        if (!found) {
            System.out.println("No donors found with blood group " + bg);
        }
    }

    // ---------- Inventory management ----------
    static void addBloodStock() {
        String bg = readValidBloodGroup();
        System.out.print("Enter units to add: ");
        int units = sc.nextInt();

        int index = getGroupIndex(bg);
        inventory[index] += units;
        System.out.println(units + " units of " + bg + " added. New total: " + inventory[index]);
    }

    static void issueBlood() {
        System.out.print("Enter blood group needed by patient: ");
        String requiredGroup = sc.nextLine().trim().toUpperCase();

        int index = getGroupIndex(requiredGroup);
        if (index == -1) {
            System.out.println("Invalid blood group entered.");
            return;
        }

        System.out.print("Enter units required: ");
        int unitsNeeded = sc.nextInt();

        // Step 1: Check direct stock availability
        if (inventory[index] >= unitsNeeded) {
            inventory[index] -= unitsNeeded;
            System.out.println(unitsNeeded + " units of " + requiredGroup + " issued from inventory.");
            System.out.println("Remaining stock: " + inventory[index]);
            return;
        }

        // Step 2: Not enough stock -> find compatible donors instead
        System.out.println("Insufficient stock (" + inventory[index] + " units available).");
        System.out.println("Searching for compatible donors...");

        String[] compatibleGroups = getCompatibleDonorGroups(requiredGroup);
        boolean matchFound = false;

        for (Donor d : donors) {
            for (String cg : compatibleGroups) {
                if (d.bloodGroup.equalsIgnoreCase(cg)) {
                    d.display();
                    matchFound = true;
                }
            }
        }

        if (!matchFound) {
            System.out.println("No compatible donors available at this time.");
        }
    }

    static void viewInventory() {
        System.out.println("---- Current Blood Inventory ----");
        for (int i = 0; i < bloodGroups.length; i++) {
            System.out.println(bloodGroups[i] + " : " + inventory[i] + " units");
        }
    }

    // ---------- Helper methods ----------
    static String readValidBloodGroup() {
        String bg;
        int index;
        do {
            System.out.print("Enter blood group (A+, A-, B+, B-, AB+, AB-, O+, O-): ");
            bg = sc.nextLine().trim().toUpperCase();
            index = getGroupIndex(bg);
            if (index == -1) {
                System.out.println("Invalid blood group. Please try again.");
            }
        } while (index == -1);
        return bg;
    }

    static int getGroupIndex(String bg) {
        for (int i = 0; i < bloodGroups.length; i++) {
            if (bloodGroups[i].equalsIgnoreCase(bg)) {
                return i;
            }
        }
        return -1;
    }

    // Standard blood donation compatibility rules:
    // returns which donor blood groups CAN donate to the given patient group
    static String[] getCompatibleDonorGroups(String patientGroup) {
        switch (patientGroup) {
            case "A+":  return new String[]{"A+", "A-", "O+", "O-"};
            case "A-":  return new String[]{"A-", "O-"};
            case "B+":  return new String[]{"B+", "B-", "O+", "O-"};
            case "B-":  return new String[]{"B-", "O-"};
            case "AB+": return new String[]{"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"}; // universal recipient
            case "AB-": return new String[]{"A-", "B-", "AB-", "O-"};
            case "O+":  return new String[]{"O+", "O-"};
            case "O-":  return new String[]{"O-"}; // O- can only receive from O-
            default:    return new String[]{};
        }
    }
}