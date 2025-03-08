# Use Cases for Transport Management System as per CSCB525 course requirements

## Use Case 1: Manage Transport Companies
**Requirement:** Въвеждане, редактиране и изтриване на транспортна компания, която извършва транспортни услуги и в която са наети служители  
**Purpose:** Allow users to add, edit, and delete transport companies that provide transport services and employ staff.

### Operations
1. **Create:** Add a new transport company.
2. **Update:** Modify existing company details.
3. **Delete:** Remove a company from the system.

### Create a Transport Company
- **Input:**
    - Menu selection: Manage Transport Companies → Add a new company
    - Company name: "SwiftMove Transport"
    - Address: "456 Oak Ave, Townsville"
- **Expected Output:**
    - "Transport company created with ID: 1"
- **Validation:**
    - Company name and address are mandatory fields.
    - If either field is blank, display error: "Name and address are required."

### Update a Transport Company
- **Input:**
    - Menu selection: Manage Transport Companies → Update a company
    - Company ID: 1
    - New name: "SwiftMove Logistics" (address unchanged)
- **Expected Output:**
    - "Company updated successfully."
- **Validation:**
    - Company ID must exist in the system; otherwise, display error: "Company not found."
    - At least one field (name or address) must be provided for update.

### Delete a Transport Company
- **Input:**
    - Menu selection: Manage Transport Companies → Delete a company
    - Company ID: 1
- **Expected Output:**
    - "Company deleted."
- **Validation:**
    - Company ID must exist; otherwise, display error: "Company not found."
    - If company has active employees or vehicles, warn: "Cannot delete company with active dependencies."

---

## Use Case 2: Manage Clients
**Requirement:** Въвеждане, редактиране и изтриване на клиентите на транспортната компания  
**Purpose:** Manage client records for the transport company.

### Operations
1. **Create:** Add a new client.
2. **Update:** Edit client details.
3. **Delete:** Remove a client.

### Create a Client
- **Input:**
    - Menu selection: Manage Clients → Add a new client
    - Name: "John Doe"
    - Telephone: "123-456-7890"
    - Email: "john@example.com"
- **Expected Output:**
    - "Client created with ID: 1"
- **Validation:**
    - Name and email are required.
    - Email must follow format (e.g., user@domain.com); otherwise, display error: "Invalid email format."

### Update a Client
- **Input:**
    - Menu selection: Manage Clients → Update a client
    - Client ID: 1
    - New telephone: "098-765-4321" (name and email unchanged)
- **Expected Output:**
    - "Client updated successfully."
- **Validation:**
    - Client ID must exist; otherwise, display error: "Client not found."

### Delete a Client
- **Input:**
    - Menu selection: Manage Clients → Delete a client
    - Client ID: 1
- **Expected Output:**
    - "Client deleted."
- **Validation:**
    - Client ID must exist; otherwise, display error: "Client not found."

---

## Use Case 3: Manage Vehicles
**Requirement:** Въвеждане, редактиране и изтриване на превозните средства, които са собственост на компанията  
**Purpose:** Manage vehicles owned by the transport company (e.g., buses, trucks).

### Operations
1. **Create:** Add a new vehicle.
2. **Update:** Modify vehicle details.
3. **Delete:** Remove a vehicle.

### Create a Vehicle (Bus)
- **Input:**
    - Menu selection: Manage Vehicles → Manage Buses → Add a new bus
    - Registration plate: "BUS-123"
    - Company ID: 1
    - Capacity: 40 passengers
    - Has restroom: No
    - Luggage capacity: 150.00 kg
- **Expected Output:**
    - "Bus created with ID: 1"
- **Validation:**
    - Registration plate and company ID are required.
    - Company ID must exist; otherwise, display error: "Company not found."

### Update a Vehicle
- **Input:**
    - Menu selection: Manage Vehicles → Manage Buses → Update a bus
    - Vehicle ID: 1
    - New capacity: 50 passengers (other fields unchanged)
- **Expected Output:**
    - "Bus updated successfully."
- **Validation:**
    - Vehicle ID must exist; otherwise, display error: "Vehicle not found."

### Delete a Vehicle
- **Input:**
    - Menu selection: Manage Vehicles → Manage Buses → Delete a bus
    - Vehicle ID: 1
- **Expected Output:**
    - "Bus deleted."
- **Validation:**
    - Vehicle ID must exist; otherwise, display error: "Vehicle not found."

---

## Use Case 4: Manage Employees
**Requirement:** Въвеждане, редактиране и изтриване на служителите на компанията  
**Purpose:** Manage company employees (e.g., drivers, dispatchers).

### Operations
1. **Create:** Add a new employee.
2. **Update:** Edit employee details.
3. **Delete:** Remove an employee.

### Create an Employee (Driver)
- **Input:**
    - Menu selection: Manage Employees → Manage Drivers → Add a new driver
    - First name: "Jane"
    - Last name: "Smith"
    - Salary: 50000
    - Company ID: 1
    - Qualifications: "Heavy Vehicle License"
- **Expected Output:**
    - "Driver created with ID: 1"
- **Validation:**
    - First name, last name, and company ID are required.
    - Company ID must exist; otherwise, display error: "Company not found."

### Update an Employee
- **Input:**
    - Menu selection: Manage Employees → Manage Drivers → Update a driver
    - Employee ID: 1
    - New salary: 55000 (other fields unchanged)
- **Expected Output:**
    - "Driver updated successfully."
- **Validation:**
    - Employee ID must exist; otherwise, display error: "Employee not found."

### Delete an Employee
- **Input:**
    - Menu selection: Manage Employees → Manage Drivers → Delete a driver
    - Employee ID: 1
- **Expected Output:**
    - "Driver deleted."
- **Validation:**
    - Employee ID must exist; otherwise, display error: "Employee not found."

---

## Use Case 5: Record Transport Services
**Requirement:** Възможност за записване на данни за превозите (дестинация, товар, цена и др.)  
**Purpose:** Record details of transport services (e.g., cargo or passenger transports).

### Operation
1. **Create:** Add a new transport service.

### Create a Cargo Service
- **Input:**
    - Menu selection: Manage Transport Services → Manage Cargo Services → Add a new cargo service
    - Destination: "City B"
    - Cargo weight: 1000 kg
    - Price: 500.00
    - Client ID: 1
    - Vehicle ID: 1
    - Driver ID: 1
- **Expected Output:**
    - "Cargo service created with ID: 1"
- **Validation:**
    - Client ID, vehicle ID, and driver ID must exist; otherwise, display error: "Invalid reference ID."
    - Price must be positive; otherwise, display error: "Price must be greater than 0."

---

## Use Case 6: Record Payment Status
**Requirement:** Начин за записване на това, дали клиентът си е платил задълженията  
**Purpose:** Track payment status for transport services.

### Operation
1. **Update:** Mark a service as paid or unpaid.

### Mark a Service as Paid
- **Input:**
    - Menu selection: Manage Transport Services → Update a service
    - Service ID: 1
    - Payment status: Paid (true)
- **Expected Output:**
    - "Service updated successfully."
- **Validation:**
    - Service ID must exist; otherwise, display error: "Service not found."

---

## Use Case 7: Sort and Filter Data
**Requirement:** Сортиране и филтриране на данните по различни критерии  
**Purpose:** Provide sorting and filtering options for companies, employees, and services.

### Operations
1. Sort companies: By name or revenue.
2. Filter/sort employees: By qualification or salary.
3. Sort services: By destination.

### Sort Companies by Revenue
- **Input:**
    - Menu selection: Manage Transport Companies → List all companies → Sort by revenue
- **Expected Output:**
    - List of companies sorted by revenue (e.g., "SwiftMove Logistics: $5000, FastFreight: $3000")
- **Validation:**
    - If no companies exist, display: "No companies available."

### Filter Employees by Qualification
- **Input:**
    - Menu selection: Manage Employees → List all employees → Filter by qualification
    - Qualification: "Heavy Vehicle License"
- **Expected Output:**
    - List of employees with that qualification (e.g., "Jane Smith, ID: 1")
- **Validation:**
    - If no matches, display: "No employees found."

### Sort Services by Destination
- **Input:**
    - Menu selection: Manage Transport Services → List all services → Sort by destination
- **Expected Output:**
    - List of services sorted alphabetically by destination (e.g., "City A, City B")
- **Validation:**
    - If no services exist, display: "No services available."

---

## Use Case 8: Save and Retrieve Transport Data with ServiceSerializer
**Requirement:** Записване на данните за превозите във файл и възможност за извличането и показването на тези данни  
**Purpose:** Export and import transport service data to/from a file, using a ServiceSerializer to structure and serialize the data.

### Operations
1. **Export:** Save transport service data to a file using ServiceSerializer.
2. **Import:** Load transport service data from a file.

### Export Transport Services
- **Input:**
    - Menu selection: Manage Transport Services → Export data
    - File name: "services.json"
- **Process:**
    - The ServiceSerializer converts service objects (e.g., ID, destination, price, payment status) into a JSON format:
      ```json
      [
        {
          "id": 1,
          "destination": "City B",
          "cargo_weight": 1000,
          "price": 500.00,
          "client_id": 1,
          "vehicle_id": 1,
          "driver_id": 1,
          "paid": true
        }
      ]
      ```
    - The serialized data is written to "services.json".
- **Expected Output:**
    - "Data exported to services.json"
- **Validation:**
    - If no services exist, display: "No data to export."
    - If file write fails, display: "Error writing to file."

### Import Transport Services
- **Input:**
    - Menu selection: Manage Transport Services → Import data
    - File name: "services.json"
- **Process:**
    - The ServiceSerializer reads and deserializes the JSON file, reconstructing service objects.
    - Each service is validated and added to the system.
- **Expected Output:**
    - "Data imported successfully. 1 service(s) added."
- **Validation:**
    - If file doesn’t exist, display: "File not found."
    - If data is invalid (e.g., missing required fields), display: "Invalid data format."

#### ServiceSerializer Implementation Note
The ServiceSerializer is a utility class responsible for converting transport service objects to and from a structured format (e.g., JSON). It ensures all required fields (e.g., ID, destination, price) are included during serialization and validates them during deserialization. This makes data export/import consistent and reliable.

---

## Use Case 9: Generate Reports
**Requirement:** Показване на справки за общ брой извършени превози, обща сума на извършените превози, списък с шофьорите и колко превоза е осъществил всеки от тях, приходите на компанията за определен период от време, колко точно е приходът от всеки от шофьорите и т.н.  
**Purpose:** Provide detailed reports on transport activities and finances.

### Operations
1. Total transports: Count all completed services.
2. Total revenue: Sum of all service prices.
3. Driver activity: Number of transports per driver.
4. Company revenue: Revenue within a date range.
5. Driver revenue: Revenue generated by each driver.

### View Total Revenue
- **Input:**
    - Menu selection: Reports → Total revenue
- **Expected Output:**
    - "Total revenue: $500.00"
- **Validation:**
    - If no services exist, display: "No revenue data available."

### List Drivers and Their Transports
- **Input:**
    - Menu selection: Reports → Driver activity
- **Expected Output:**
    - "Jane Smith: 1 transport(s)"
- **Validation:**
    - If no drivers or transports exist, display: "No driver activity data."

### View Company Revenue for a Period
- **Input:**
    - Menu selection: Reports → Company revenue by period
    - Start date: "2023-01-01"
    - End date: "2023-12-31"
- **Expected Output:**
    - "Revenue from 2023-01-01 to 2023-12-31: $500.00"
- **Validation:**
    - Dates must be valid; otherwise, display: "Invalid date range."