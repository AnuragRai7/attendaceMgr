AttendanceManager App

A comprehensive Android application built with Kotlin for teachers and instructors to manage student attendance across multiple classes and subjects. The app is built using modern Android development practices, including RoomDB, Coroutines, and a Material 3 design.

📱 App Screenshots

Login Screen

<img width="1080" height="2400" alt="Screenshot_20251121_233502" src="https://github.com/user-attachments/assets/6c600c0e-8dc7-490e-ab20-0b50d2ac8525" />


Main Dashboard

<img width="1080" height="2400" alt="Screenshot_20251121_233536" src="https://github.com/user-attachments/assets/97705a1a-7cdd-4b0e-9c47-0aa2e640c102" />


Mark Attendance

<img width="1080" height="2400" alt="Screenshot_20251121_233740" src="https://github.com/user-attachments/assets/89233eef-f427-4d06-bbef-53f1b5783f6c" />


View Report

<img width="1080" height="2400" alt="Screenshot_20251121_233831" src="https://github.com/user-attachments/assets/cb871e65-7322-40fe-b0e0-e09f11cd282d" />

Manage Subjects

<img width="1080" height="2400" alt="Screenshot_20251121_233921" src="https://github.com/user-attachments/assets/ce9bcfcb-aff5-40a8-8818-ce99bf5d3cec" />



✨ Features

This app is a complete, multi-featured tool that allows for:

**User Authentication:** A full login and sign-up screen. User accounts are saved locally in the database.

**Live Dashboard:** A dynamic home screen showing the current date, total student count, and "Present Today" stats.

**Dashboard Filtering:** The "Present Today" card includes a settings icon to select a "default subject" (e.g., "Homeroom") or "All Subjects" for the live stats. The app remembers your choice.

**Subject Management:** A dedicated screen to Add and Delete class subjects (e.g., "Math", "History", "Science").

**Student Management:** A dedicated screen to Add students, which automatically logs the date they were added.

**Mark Attendance:** A powerful screen with all of the following features:

**Date Picker:** Select any date to mark or edit past attendance.

**Subject Dropdown:** Select a subject to mark attendance for. The student list is loaded after you select a subject.

**Search/Filter:** A search bar to filter the student list by name or roll number.

**Delete Student:** A delete icon for each student with a confirmation dialog.


**Detailed Reporting:**

Filter by "All Subjects" for a combined, overall report.

Filter by Specific Subject to see a report for just one class.

Share/Export: A share icon in the toolbar to export the current report as text to any app (WhatsApp, Gmail, etc.).

Student History: Tap any student in the report to open a new screen showing their complete day-by-day attendance history, including the subject for each entry.


**Modern UI & Theme:**

A professional navy blue (Material 3) theme used across the entire app.

Consistent MaterialToolbar (app bar) on every screen with back-button navigation.

fitsSystemWindows="true" used on all layouts to correctly avoid the camera cutout and status bar.

Responsive layouts that use ConstraintLayout to look good on any screen size.


**🛠 Tech Stack & Key Components**


**Language:** Kotlin

**Architecture:** Built with a modern MVVM-like (Model-View-ViewModel) structure.

**Database:** Room Database (Part of Android Jetpack)

Uses a complex relational schema with 4 tables: User, Student, Subject, and Attendance.

Features composite primary keys and advanced queries (@Upsert, DISTINCT, etc.).

**Asynchronous:** Kotlin Coroutines (using lifecycleScope) to keep all database operations off the main UI thread.


**UI (XML):**

**Android Views (XML Layouts)**

Material Design 3 (MaterialToolbar, MaterialCardView, TextInputLayout)

ConstraintLayout & CoordinatorLayout

RecyclerView (used for all lists)

Local Storage: SharedPreferences (used to save the user's default dashboard subject).

UI Components: DatePickerDialog, AlertDialog, Spinner, SearchView.


🚀 How to Run

Clone the repository:

git clone [your-repo-url-here]


Open in Android Studio:

Open Android Studio.

Select "Open an existing project".

Navigate to the cloned folder and open it.

Build & Run:

Let Gradle sync and download all the dependencies (this may take a minute).

Click the Run (▶️) button to build and install the app on your emulator or physical device.
