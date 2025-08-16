# Xpense -Smart Expense Tracker Android App

A modern, minimal, and aesthetic expense tracking Android application built with Kotlin and following MVVM architecture patterns.

## 📱 App Overview

Xpense is a comprehensive expense management application that helps users track their daily expenses, categorize spending, generate reports, and export data in PDF format. The app features a clean, modern UI with both light and dark theme support.

## ✨ Features

### Core Features
- **Expense Entry**: Add expenses with title, amount, category, date, notes, and receipt images
- **Expense List**: View and filter expenses by category and date range
- **Expense Reports**: View daily and category-wise expense reports
- **PDF Export**: Export expense reports as PDF files with open/share options
- **Receipt Management**: Add and view receipt images for expenses
- **Real-time Totals**: View today's total expenses in real-time

### UI/UX Features
- **Modern Design**: Material Design 3 with custom color palette
- **Theme Support**: Light and dark theme with automatic switching
- **Responsive Layout**: Optimized for different screen sizes
- **Smooth Animations**: Interactive animations and transitions
- **Gradient App Bar**: Beautiful gradient background for the app bar

### Data Management
- **Local Storage**: Room database for offline data persistence
- **Category Management**: Predefined categories (Staff, Travel, Food, Utility)
- **Date Filtering**: Filter expenses by specific dates or date ranges

## 🛠 Technologies Used

### Core Technologies
- **Kotlin**
- **Android SDK**
- **Gradle**

### Architecture & Patterns
- **MVVM (Model-View-ViewModel)**
- **Repository Pattern**
- **Dependency Injection**
- **LiveData**
- **ViewBinding**

### UI Framework
- **Material Design 3**
- **Navigation Component**
- **RecyclerView**
- **ConstraintLayout**
- **Custom Views**

### Data Layer
- **Room Database**
- **Room DAO**
- **Room Entities**
- **Repository Pattern**

### Image Loading
- **Coil**
- **Photo Picker**
- **File Provider**

### PDF Generation
- **PdfDocument**
- **FileProvider**
- **Intent Actions**


## 📁 Project Structure

```
com.example.xpense/
├── core/                    # Core application components
│   ├── di/                  # Dependency injection modules
│   ├── utils/               # Utility classes (DateUtils, Format, BarChartView)
│   └── constants/           # App constants
├── data/                    # Data layer
│   ├── local/               # Local data sources
│   │   ├── dao/            # Data Access Objects
│   │   ├── entity/         # Database entities
│   │   └── database/       # Database configuration
│   ├── repository/          # Repository implementations
│   └── model/              # Data models
├── domain/                  # Domain layer (business logic)
│   ├── model/              # Domain models
│   ├── repository/         # Repository interfaces
│   └── usecase/            # Use cases
├── presentation/            # Presentation layer
│   ├── ui/                 # UI components
│   │   ├── entry/          # Expense entry screen
│   │   ├── list/           # Expense list screen
│   │   ├── report/         # Expense report screen
│   │   ├── adapter/        # RecyclerView adapters
│   │   └── components/     # Reusable UI components
│   └── viewmodel/          # ViewModels
└── MyApp.kt                # Application class
```

## 🎨 UI Components

### Screens
1. **Expense Entry**: Form to add new expenses with receipt upload
2. **Expense List**: List view with filtering and search capabilities
3. **Expense Report**: Charts and reports with PDF export functionality

### Custom Components
- **TotalBarView**: Custom view for displaying total amounts
- **BarChartView**: Custom chart component for expense visualization
- **Modern Cards**: Custom card styles with shadows and gradients
- **Gradient Buttons**: Custom button styles with gradients

## 🔧 Setup & Installation

### Prerequisites
- Android Studio Arctic Fox or later
- Android SDK 24+
- Kotlin 1.9+
- Gradle 8.0+

### Build Instructions
1. Clone the repository
2. Open project in Android Studio
3. Sync Gradle files
4. Build and run on device/emulator

### Dependencies
Key dependencies are managed through version catalogs in `gradle/libs.versions.toml`:
- AndroidX Core KTX
- Material Design Components
- Room Database
- Hilt Dependency Injection
- Navigation Component
- Coil Image Loading

## 📊 Database Schema

### ExpenseEntity
- `id`: Primary key (auto-generated)
- `title`: Expense title
- `amount`: Expense amount (Double)
- `category`: Expense category
- `timestamp`: Date and time
- `notes`: Optional notes
- `imageUri`: Optional receipt image URI

### Views
- `DailyTotal`: Daily expense totals
- `CategoryTotal`: Category-wise expense totals

## 🔐 Permissions

- **READ_MEDIA_IMAGES**: For Android 13+ (photo picker)
- **READ_EXTERNAL_STORAGE**: For Android < 13 (legacy)
- **WRITE_EXTERNAL_STORAGE**: For file operations (max SDK 28)

## 🎯 Key Features Implementation

### PDF Export
- Generates comprehensive expense reports
- Includes daily and category-wise totals
- User choice dialog for open/share
- Proper file provider configuration

### Image Handling
- Secure photo picker integration
- Image persistence in internal storage
- Automatic cleanup of temporary files
- Error handling for failed loads

### Theme System
- Material 3 color system
- Custom color palette (Indigo/Violet)
- Dark mode support
- Custom component styles

## 🚀 Performance Optimizations

- **Efficient List Rendering**: RecyclerView with proper view recycling
- **Image Caching**: Coil library for efficient image loading
- **Database Optimization**: Room with proper indexing
- **Memory Management**: Automatic cleanup of resources

## 📱 Screenshots

*[Screenshots would be added here]*

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🙏 Acknowledgments

- Material Design 3 guidelines
- Android Jetpack libraries
- Room database documentation
- Coil image loading library

---

**Version**: 1.0.0  
**Last Updated**: August 2025  
**Developer**: [Your Name]
