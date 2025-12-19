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
| `DashboardActivity` | Activity | All | Role-based main dashboard |
| `KomplainFormFragment` | Fragment | Customer | Create new complaints |
| `StatusKomplainFragment` | Fragment | Customer | Track complaint status |
| `KomplainListFragment` | Fragment | Teknisi | List of assigned complaints |
| `KomplainDetailFragment` | Fragment | All | Complaint details & discussion |
| `AbsenFragment` | Fragment | Teknisi | Daily attendance |
| `ProfilFragment` | Fragment | All | User profile management |
| `DiskusiTeknisiFragment` | Fragment | Teknisi | Technician discussions |

## 🔐 Authentication Flow

### **Login Process**
```java
// LoginActivity
Input: username & password
POST → /login
Response: { userId, role }
Store: SharedPreferences
Navigate → DashboardActivity
```

### **Role Detection & Navigation**
```java
// DashboardActivity - Role-Based Menu
if (role == "customer") {
    showMenu: Komplain Baru, Status Komplain, Profil
} else if (role == "teknisi") {
    showMenu: Absen, Daftar Komplain, Profil
}
```

## 📱 Customer Flow

### **1. 📸 Komplain Baru - `KomplainFormFragment`**
```java
// Input Fields:
- Judul (required)
- Deskripsi (required) 
- Kategori (dropdown)
- Foto barang rusak (optional)

// API:
Multipart POST → /complains
Body: { judul, deskripsi, kategori, foto, status: "open" }
```

### **2. 📋 Status Komplain - `StatusKomplainFragment`**
```java
// API:
GET → /complains?userId={userId}

// Display:
- List of user's complaints
- Click → KomplainDetailFragment
```

### **3. 💬 Diskusi Komplain - `KomplainDetailFragment`**
```java
// Features:
- Chat with assigned technician
- View complaint details
- Cannot change status or send reports
```

## 🔧 Technician Flow

### **1. ✅ Absen Harian - `AbsenFragment`**
```java
// API:
POST → /attendance
Body: { teknisiId, timestamp }
```

### **2. 📥 Daftar Komplain - `KomplainListFragment`**
```java
// API:
GET → /complains?status=open

// Display:
- List of open complaints
- Click → KomplainDetailFragment
```

### **3. 🔧 Detail Komplain - `KomplainDetailFragment`**
```java
// Features:
- View complaint details + photos
- Update status: open → in_progress → done
- Send work report: description + result photos
- Chat with customer
- Discuss with other technicians (all technicians can view)
```

## 🗂️ API Endpoints

### **Authentication**
- `POST /login` - User login
- `POST /logout` - User logout

### **Complaints**
- `GET /complains?userId={id}` - Get user complaints
- `GET /complains?status={status}` - Get complaints by status
- `POST /complains` - Create new complaint (Multipart)
- `PUT /complains/{id}` - Update complaint status
- `GET /complains/{id}` - Get complaint details

### **Attendance**
- `POST /attendance` - Technician attendance

### **Chat/Discussion**
- `GET /messages?complaintId={id}` - Get messages
- `POST /messages` - Send new message

## 🎨 UI/UX Features

### **Navigation**
- Bottom Navigation (role-based)
- Fragment transactions with back stack
- Intent for activity navigation

### **Design Patterns**
- Material Design components
- Multipart file upload for images
- Real-time chat interface
- Role-based UI adaptation

## 🛠️ Technical Stack

- **Language**: Java
- **Architecture**: MVC with Fragments
- **Network**: Retrofit + OkHttp
- **Storage**: SharedPreferences, Multipart File Upload
- **Navigation**: BottomNavigationView + FragmentManager

## 📁 Project Architecture

```
app/
├── src/main/java/com/example/project_uts/
│   ├── activity/
│   │   ├── LoginActivity.java
│   │   └── DashboardActivity.java
│   ├── fragment/
│   │   ├── KomplainFormFragment.java
│   │   ├── StatusKomplainFragment.java
│   │   ├── KomplainListFragment.java
│   │   ├── KomplainDetailFragment.java
│   │   ├── AbsenFragment.java
│   │   ├── ProfilFragment.java
│   │   └── DiskusiTeknisiFragment.java
│   ├── adapter/
│   │   ├── KomplainAdapter.java
│   │   └── ChatAdapter.java
│   ├── model/
│   │   ├── User.java
│   │   ├── Komplain.java
│   │   └── ChatMessage.java
│   └── api/
│       └── ApiClient.java
```

## 🔄 Workflow Summary

### **Customer Journey**
```
Login → Dashboard → Buat Komplain → Lihat Status → Chat dengan Teknisi
```

### **Technician Journey**  
```
Login → Dashboard → Absen → Lihat Daftar Komplain → Proses Komplain → Kirim Laporan → Chat dengan Customer
```

## 🚀 Getting Started

1. **Clone repository**
2. **Configure API endpoints in `ApiClient.java`**
3. **Build and run on Android Studio**
4. **Test with different user roles**

## 📞 Support

For technical issues or feature requests, contact the development team.

---

**Version**: 1.0  
**Last Updated**: 2025  
**Developed By**: Dicky Pratama and Mikhael Agung
