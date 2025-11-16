# 📱 Service Complaint App

## 🎯 Overview
A mobile application for handling customer service complaints and technician assignments with real-time communication features. Built with Java and Android Studio.

## 👥 Role-Based System

### **User Roles:**
- **Customer**: Create complaints, track status, chat with technicians
- **Teknisi**: Handle complaints, daily attendance, send work reports, chat with customers

## 📱 Features

### **Customer Features:**
- 📝 Create new complaints with photos
- 📋 Track complaint status  
- 💬 Real-time chat with technicians
- 📊 View complaint history
- 👤 User profile management

### **Technician Features:**
- ✅ Daily attendance system
- 📥 View assigned complaints
- 🔧 Update complaint status
- 📋 Send work reports with photos
- 💬 Chat with customers
- 👤 Technician profile

## 🏗️ Project Structure

```
app/src/main/
├── java/com/example/project_uts/
│   ├── activity/
│   │   ├── MainActivity.java          # Main activity with role-based navigation
│   │   └── LoginActivity.java         # User authentication
│   ├── fragment/
│   │   ├── CustomerFragment.java      # Complaint creation form
│   │   ├── DashboardFragment.java     # Role-based dashboard
│   │   ├── KomplainListFragment.java  # Complaint list for technicians
│   │   ├── DiskusiTeknisiFragment.java # Discussion/chat interface
│   │   ├── ProfilFragment.java        # User profile management
│   │   └── HistoryComplainFragment.java # Complaint history
│   ├── adapter/
│   │   ├── KomplainAdapter.java       # Complaint list adapter
│   │   ├── ChatAdapter.java           # Chat message adapter
│   │   └── HistoryAdapter.java        # History list adapter
│   ├── model/
│   │   ├── Komplain.java              # Complaint data model
│   │   ├── ChatMessage.java           # Chat message model
│   │   └── Complaint.java             # History complaint model
│   └── api/
│       └── ApiClient.java             # API service client
└── res/
    ├── layout/                         # UI layout files
    ├── drawable/                       # Icons and shapes
    └── menu/                           # Navigation menus
```

## 🔄 Workflow

### **Customer Journey:**
```
Login → Dashboard → Create Complaint → Track Status → Chat with Technician
```

### **Technician Journey:**
```
Login → Attendance → View Complaints → Process Complaint → Send Report → Chat with Customer
```

## 🎨 UI/UX Features

- **Material Design 3** components
- **Role-based navigation** with BottomNavigationView
- **Responsive layouts** with ConstraintLayout
- **Image upload** with preview functionality
- **Real-time chat** interface
- **Form validation** and error handling

## 🛠️ Technical Stack

- **Language**: Java
- **Minimum SDK**: API 21 (Android 5.0)
- **Architecture**: MVC with Fragments
- **UI**: Material Components, ConstraintLayout
- **Navigation**: BottomNavigationView, FragmentManager

## 📋 API Integration

### **Planned Endpoints:**
- `POST /login` - User authentication
- `POST /complaints` - Create new complaint
- `GET /complaints` - Get complaints list
- `PUT /complaints/{id}` - Update complaint status
- `POST /messages` - Send chat messages
- `POST /attendance` - Technician attendance

## 🚀 Getting Started

### **Prerequisites:**
- Android Studio Arctic Fox or later
- Java JDK 11+
- Android SDK API 21+

### **Installation:**
1. Clone the repository
2. Open in Android Studio
3. Sync project with Gradle files
4. Build and run on emulator or device

### **Build Instructions:**
```bash
./gradlew assembleDebug
```

## 🔧 Development

### **Code Style:**
- Follow Android Java style guide
- Use meaningful variable names
- Add comments for complex logic
- Maintain consistent formatting

### **Branch Strategy:**
- `main` - Production ready code
- `staging` - Testing and integration
- `feature/*` - Feature development
- `bugfix/*` - Bug fixes

## 📝 TODO / Upcoming Features

- [ ] Firebase integration for real-time data
- [ ] Push notifications
- [ ] Image compression for uploads
- [ ] Offline support
- [ ] Payment integration
- [ ] Rating system for technicians

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details.

## 📞 Support

For support and questions:
- Create an issue in the repository
- Contact the development team
- Check the project documentation

---

**Version**: 1.0.0  
**Last Updated**: 2025  
**Developed By**: Dicky Pratama and Mikhael Agung
