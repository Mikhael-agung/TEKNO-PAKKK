# 📱 Service Complaint App - Documentation

## 🎯 Overview
A mobile application for handling customer complaints and technician assignments with real-time communication features.

## 👥 Role-Based Access

### **Authentication & Roles**
- **All Users**: Login, Profile, Complaint Details
- **Customer**: Create complaints, track status, chat with technicians
- **Teknisi**: Attendance, handle complaints, send reports, chat with customers

## 📋 Project Structure

### **Activities & Fragments**

| Halaman / Fragment | Tipe | Role | Description |
|-------------------|------|------|-------------|
| `LoginActivity` | Activity | All | User authentication |
| `RegisterCustomerActivity` | Activity | Customer | New customer registration |
| `MainActivity` | Activity | All | Main customer dashboard |
| `MainActivity` (Teknisi) | Activity | Teknisi | Main technician dashboard |
| `ComplaintDetailActivity` | Activity | Customer | Complaint details with timeline |
| `KomplainDetailActivity` | Activity | Teknisi | Technician complaint details |
| `DashboardCustomerFragment` | Fragment | Customer | Customer dashboard with stats |
| `HistoryComplainFragment` | Fragment | Customer | Complaint history list |
| `CustomerFragment` | Fragment | Customer | Create new complaints |
| `DashboardTeknisiFragment` | Fragment | Teknisi | Technician dashboard |
| `KomplainFragment` | Fragment | Teknisi | Technician complaint list |
| `ProgressFragment` | Fragment | Teknisi | In-progress complaints |
| `CompletedFragment` | Fragment | Teknisi | Completed complaints |
| `DiskusiTeknisiFragment` | Fragment | Teknisi | Technician discussions |
| `ProfilFragment` | Fragment | All | User profile management |

## 🔐 Authentication Flow

### **Login Process**
```java
// LoginActivity
Input: username & password
POST → /login
Response: { userId, role }
Store: SharedPreferences + AuthManager
Navigate → Role-specific MainActivity
```

### **Registration Process**
```java
// RegisterCustomerActivity
Input: username, email, password, full_name, phone
POST → /register
Navigate → LoginActivity
```

### **Role Detection & Navigation**
```java
// MainActivity - Role-Based Dashboard
if (role == "customer") {
    show: DashboardCustomerFragment, CustomerFragment, HistoryComplainFragment, ProfilFragment
} else if (role == "teknisi") {
    show: DashboardTeknisiFragment, KomplainFragment, ProgressFragment, CompletedFragment, ProfilFragment
}
```

## 📱 Customer Flow

### **1. 🏠 Dashboard Customer - `DashboardCustomerFragment`**
```java
// Features:
- Welcome message with user name
- Complaint statistics (Total, Proses, Pending, Selesai)
- Quick access to create new complaint
- Recent complaint history (clickable → ComplaintDetailActivity)
```

### **2. 📝 Buat Komplain - `CustomerFragment`**
```java
// Input Fields:
- Judul (required)
- Deskripsi (required) 
- Kategori (dropdown: AC Rusak, Listrik Mati, Pipa Bocor, Default Rusak)
- Foto (optional, camera/gallery)

// API:
Multipart POST → /complaints
Body: { title, description, category, photo, status: "complaint" }
```

### **3. 📋 Riwayat Komplain - `HistoryComplainFragment`**
```java
// API:
GET → /complaints/{userId}

// Display:
- Grid/list of user's complaints with status badges
- Status badges: MENUNGGU (orange), PROSES (blue), SELESAI (green)
- Click → ComplaintDetailActivity with timeline
```

### **4. 📊 Detail Komplain - `ComplaintDetailActivity`**
```java
// NEW FEATURES:
- Dynamic timeline from /complaints/{id}/history API
- 4 status stages: Complaint → On Progress → Pending → Completed
- Complete description display
- Technician card with name and status
- WhatsApp contact button with fallback phone number
- Status-based outline colors on timeline cards
- Dark/Light mode support
```

## 🔧 Technician Flow

### **1. 🏠 Dashboard Teknisi - `DashboardTeknisiFragment`**
```java
// Features:
- Welcome message
- Complaint statistics
- Quick access to complaint lists
```

### **2. 📥 Daftar Komplain - `KomplainFragment`**
```java
// API:
GET → /complaints?status=complaint

// Display:
- List of new complaints
- Click → KomplainDetailActivity
```

### **3. 🔄 Proses Komplain - `ProgressFragment`**
```java
// API:
GET → /complaints?status=on_progress&teknisiId={id}

// Display:
- List of in-progress complaints assigned to technician
```

### **4. ✅ Selesai Komplain - `CompletedFragment`**
```java
// API:
GET → /complaints?status=completed&teknisiId={id}

// Display:
- List of completed complaints
```

### **5. 🔧 Detail Komplain Teknisi - `KomplainDetailActivity`**
```java
// Features:
- View complaint details + photos
- Update status buttons
- Send work report with photos
- Chat with customer
- Discuss with other technicians
- WhatsApp integration
```

## 🗂️ API Endpoints

### **Authentication**
- `POST /login` - User login
- `POST /register` - Customer registration
- `POST /logout` - User logout

### **Complaints**
- `GET /complaints` - Get all complaints (with filters)
- `GET /complaints/{id}` - Get complaint details
- `GET /complaints/{id}/history` - Get complaint timeline history
- `POST /complaints` - Create new complaint (Multipart)
- `PUT /complaints/{id}` - Update complaint status
- `DELETE /complaints/{id}` - Delete complaint

### **User Management**
- `GET /users/{id}` - Get user details
- `PUT /users/{id}` - Update user profile

### **Attendance** (Planned)
- `POST /attendance` - Technician attendance

### **Chat/Discussion**
- `GET /messages?complaintId={id}` - Get messages
- `POST /messages` - Send new message

## 🎨 UI/UX Features

### **NEW UI IMPROVEMENTS:**
- **Dynamic Timeline**: Shows complaint progression with status-based colors
- **Status Badges**: Color-coded badges (MENUNGGU, PROSES, SELESAI)
- **Outline Colors**: Timeline cards have colored outlines matching status
- **Dark Mode Support**: Full dark/light mode adaptation
- **WhatsApp Integration**: Direct contact with technicians
- **Responsive Layout**: Better spacing and visual hierarchy

### **Navigation**
- Bottom Navigation (role-based)
- Fragment transactions with back stack
- Intent for activity navigation
- Clickable history items → detail activity

### **Design Patterns**
- Material Design components (MaterialCardView)
- RecyclerView with custom adapters
- Multipart file upload for images
- Real-time chat interface
- Role-based UI adaptation
- Status-based color themes

## 🛠️ Technical Stack

- **Language**: Java
- **Architecture**: MVC with Activities & Fragments
- **Network**: Retrofit2 + OkHttp3 + Gson
- **Authentication**: JWT with AuthInterceptor
- **Storage**: SharedPreferences, Multipart File Upload
- **Navigation**: BottomNavigationView + FragmentManager
- **UI Components**: Material Design, RecyclerView, CardView
- **Image Loading**: Native implementation

## 📁 Updated Project Architecture

```
app/
├── java/com/example/project_uts/
│   ├── MainActivity.java (Customer)
│   ├── LoginActivity.java
│   ├── RegisterCustomerActivity.java
│   ├── ComplaintDetailActivity.java (NEW)
│   ├── MyApplication.java
│   ├── ThemeTransitionHelper.java
│   │
│   ├── adapter/
│   │   ├── ComplaintGridAdapter.java
│   │   ├── HistoryAdapter.java
│   │   └── CustomerTimelineAdapter.java (NEW)
│   │
│   ├── fragment/
│   │   ├── ComplaintDetailActivity.java (Legacy)
│   │   ├── CustomerFragment.java
│   │   ├── DashboardCustomerFragment.java
│   │   ├── HistoryComplainFragment.java
│   │   └── ProfilFragment.java
│   │
│   ├── models/
│   │   ├── Complaint.java
│   │   ├── ComplaintResponse.java
│   │   ├── ApiResponse.java
│   │   ├── LoginResponse.java
│   │   └── User.java
│   │
│   ├── network/
│   │   ├── ApiClient.java
│   │   ├── ApiService.java
│   │   ├── AuthInterceptor.java
│   │   ├── AuthManager.java
│   │   └── AppConfig.java
│   │
│   └── Teknisi/
│       ├── Activity/
│       │   ├── MainActivity.java
│       │   └── KomplainDetailActivity.java
│       │
│       ├── Adapter/
│       │   ├── KomplainAdapter.java
│       │   ├── ProgressAdapter.java
│       │   ├── CompletedAdapter.java
│       │   └── DiskusiAdapter.java
│       │
│       ├── Fragment/
│       │   ├── DashboardTeknisiFragment.java
│       │   ├── KomplainFragment.java
│       │   ├── ProgressFragment.java
│       │   ├── CompletedFragment.java
│       │   ├── DiskusiTeknisiFragment.java
│       │   └── ProfilFragment.java
│       │
│       ├── Model/
│       │   ├── Komplain.java
│       │   ├── HistoryTeknisi.java
│       │   └── TeknisiComplaintsResponse.java
│       │
│       └── Utils/
│           └── WhatsAppHelper.java
```

## 🔄 Workflow Summary

### **Customer Journey**
```
Login/Register → Dashboard → Buat Komplain → Lihat History → Detail dengan Timeline → Chat via WhatsApp
```

### **Technician Journey**  
```
Login → Dashboard → Lihat Komplain Baru → Proses Komplain → Update Status → Kirim Laporan → Chat dengan Customer
```

## 🚀 Recent Improvements

### **Fixed Issues:**
1. **Timeline Static → Dinamis**: Now shows 4 status stages from API history
2. **Deskripsi NULL**: Fixed empty description display
3. **Teknisi Tidak Muncul**: Technician card now visible with status
4. **No Telepon Teknisi**: WhatsApp button works with fallback phone
5. **Klik History Dashboard**: History items now navigate to detail
6. **Status Badge Warna**: Color consistency for all status badges
7. **Timeline Dark Mode**: Full dark/light mode support
8. **Outline Tidak Muncul**: Cards now have colored outlines
9. **Spacing Terlalu Mepet**: Better spacing for readability

### **New Features:**
- Dynamic complaint timeline with status-based colors
- WhatsApp integration for direct technician contact
- Dark mode support across all UI components
- Improved visual hierarchy and spacing
- Better error handling and fallback mechanisms

## 📦 Dependencies

```gradle
dependencies {
    implementation 'androidx.appcompat:appcompat:1.6.1'
    implementation 'com.google.android.material:material:1.9.0'
    implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
    implementation 'androidx.recyclerview:recyclerview:1.3.0'
    implementation 'androidx.cardview:cardview:1.0.0'
    
    // Networking
    implementation 'com.squareup.retrofit2:retrofit:2.9.0'
    implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
    implementation 'com.squareup.okhttp3:okhttp:4.11.0'
    implementation 'com.squareup.okhttp3:logging-interceptor:4.11.0'
    
    // Image Loading
    implementation 'com.github.bumptech.glide:glide:4.15.1'
}
```

## 🚀 Getting Started

1. **Clone repository**
2. **Configure API endpoints in `AppConfig.java`**
3. **Update base URL in `ApiClient.java`**
4. **Build and run on Android Studio**
5. **Test with different user roles**

### **Testing Credentials:**
- **Customer**: Username/Password from registration
- **Teknisi**: Pre-configured in database

## 📞 Support

For technical issues or feature requests, contact the development team.

---

**Version**: 2.0 (Updated)  
**Last Updated**: January 2025  
**Developed By**: Dicky Pratama and Mikhael Agung  
**Recent Updates**: Timeline feature, Dark mode, WhatsApp integration, UI improvements
