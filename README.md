# AutoSync

AutoSync is an Android application that bridges the gap between **auto repair shops** and **their customers**.  
It allows businesses to manage ongoing car repairs while giving customers visibility and convenience in finding trusted mechanics.

AutoSync provides two types of user accounts:
- **Customer Accounts:** Search for nearby mechanic shops and view their available services.
- **Business Accounts:** Add, update, and track vehicles currently being worked on.  

## Overview

When a business adds a new car to their list, they can **enter the VIN**, and the app automatically fetches the vehicle details from the [NHTSA Vehicle API](https://vpic.nhtsa.dot.gov/api/).  
Each car is displayed in a **RecyclerView** using **CardViews**, and the mechanic can track repair progress using a **status spinner** with three options:
- Waiting for Parts
- In Progress
- Completed  
---

## Tech Stack

**Language:** Kotlin  
**Framework:** Android SDK (minSdk 26, targetSdk 35)  
**Architecture:** MVVM  
**Libraries & Tools:**
- **Retrofit + Moshi** — REST API calls and JSON parsing
- **Firebase Auth, Firestore** — user authentication and data storage
- **Coroutines** — asynchronous operations
- **Material Components** — modern UI design
- **Navigation Component** — fragment navigation
- **ViewBinding** — type-safe view access  

---

## Screenshots

### Login Screen
<img src="./screenshots/Login_Screen.png" alt="Login Screen Screenshot" width="400"/>

### Business Dashboard
<img src="./screenshots/Business_Dashboard.png" alt="Business Dashboard Screenshot" width="400"/>

### Add Vehicle GIF
<img src="./screenshots/add_vehicle_gif.gif" alt="Add Vehicle GIF" width="400"/>

### Customer Dashboard
<img src="./screenshots/Customer_Dashboard.png" alt="Customer Dashboard Screenshot" width="400"/>

### Search Business Screen
<img src="./screenshots/Business_Search.png" alt="Business Search Screenshot" width="400"/>


## 🌐 API Integration
The app integrates with the [NHTSA Vehicle API](https://vpic.nhtsa.dot.gov/api/) to automatically populate car details using the VIN.
