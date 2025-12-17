
# 🚆 Indian Railways Booking System
A robust, console-based Railway Reservation System built using Java and PostgreSQL. This project implements the DAO (Data Access Object) design pattern to ensure clean separation between the user interface and database logic. It features transaction management, dynamic database querying, and a user-friendly CLI menu.

✨ Features
Interactive Menu System: Easy-to-use command-line interface with clear navigation.

🚄 Dynamic Train Search: Fetches available destinations and trains directly from the database (e.g., searching trains for "Goa" or "Mumbai").

📝 Passenger Profile: Captures full passenger details (Name, Age, Gender, Phone) for every booking.

🍱 Add-ons & Services: Supports optional add-ons like Meals (Veg/Non-Veg), Travel Kits, and Wifi.

💳 Transaction Management: Uses ACID compliant transactions (Commit/Rollback) to ensure that bookings and add-ons are saved together or not at all.

🔍 Search Booking: View complete booking details, including train times and total fare, using a Booking ID.

🗑️ Cancellation System: Allows users to cancel bookings. Automatically handles foreign key constraints to clean up linked add-ons before deleting the ticket.

🛠️ Tech Stack:
Language: Java (JDK 21+)
Database: PostgreSQL
Connectivity: JDBC (Java Database Connectivity)
Architecture: DAO Design Pattern
