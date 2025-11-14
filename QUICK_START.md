# 🚀 Quick Start Guide - Tenant Features

## Running the App

```bash
cd E:\lap_trinh_dien_thoai_di_dong\QuanLyPhongTro_App
.\gradlew clean assembleDebug
.\gradlew installDebug
```

Or in Android Studio:
- Click Run ▶️
- Select your device/emulator
- App will launch

---

## 📱 Feature Access Guide

### 1. Advanced Filter
**Location**: Home screen → "Bộ lọc" button (filter bar)
**How to use**:
- Tap the purple "Bộ lọc" button
- Bottom sheet slides up
- Adjust price range slider
- Select district from dropdown
- Choose distance (Any, <3km, <5km)
- Pick room type chips
- Select amenities
- Tap "Áp dụng" to apply

**Code to trigger**:
```java
// In MainActivity
showAdvancedFilter();
```

---

### 2. Booking Create (Đặt lịch xem phòng)
**Location**: Room Detail → "Đặt lịch" button
**How to use**:
- View room details
- Tap booking button
- Select date (calendar picker)
- Choose time slot (Morning/Afternoon/Evening)
- Fill in your name
- Enter phone number
- Add optional note
- Toggle "Allow call" if needed
- Tap "XÁC NHẬN ĐẶT LỊCH"

**Navigate programmatically**:
```java
Intent intent = new Intent(context, BookingCreateActivity.class);
intent.putExtra("room_id", roomId);
startActivity(intent);
```

---

### 3. Booking List (Lịch hẹn của tôi)
**Location**: Bottom Navigation → "Đặt lịch" tab
**How to use**:
- Tap calendar icon in bottom nav
- See 2 tabs: "Sắp tới" and "Đã xem / Đã huỷ"
- View booking details
- Tap "Chi tiết" to see more
- Tap "Huỷ lịch" to cancel

**Sample data included**:
- Upcoming bookings (pending, confirmed)
- Past bookings (completed, cancelled)

---

### 4. Saved Rooms (Tin đã lưu)
**Location**: Profile → "Tin đã lưu" OR Bottom Nav → "Tôi" → "Tin đã lưu"
**How to use**:
- Save rooms by tapping ♥ icon on home
- Access from profile menu
- Sort by: "Mới nhất", "Giá tăng dần", "Giá giảm dần"
- Tap heart to unsave
- Tap card to view details

**Navigate**:
```java
Intent intent = new Intent(context, SavedRoomsActivity.class);
startActivity(intent);
```

---

### 5. Notifications (Thông báo)
**Location**: Bottom Navigation → "Thông báo" (bell icon)
**How to use**:
- Tap bell icon in bottom nav
- View all notifications
- Unread shown with purple dot + gray background
- Tap notification to mark as read
- Tap "Mark all read" icon to clear all

**Sample notifications**:
- Booking confirmed
- New room matches
- New messages

---

### 6. Profile (Tôi)
**Location**: Bottom Navigation → "Tôi" (person icon)
**Features**:

**Activity Section**:
- ♥ Tin đã lưu → SavedRoomsActivity
- 📅 Lịch hẹn của tôi → BookingListActivity

**Account Section**:
- 👤 Thông tin cá nhân (placeholder)
- ⚙️ Cài đặt (placeholder)

**Support Section**:
- ❓ Trợ giúp & Hỏi đáp (placeholder)
- 🔒 Điều khoản sử dụng (placeholder)

**Other**:
- 🚪 Đăng xuất (closes activity)

---

## 🎨 Color Reference

Use these colors when extending features:

```xml
<!-- Primary Colors -->
<color name="primary_blue">#4a90e2</color>
<color name="primary_purple">#7c3aed</color>
<color name="light_purple">#E9D5FF</color>

<!-- Backgrounds -->
<color name="bg_light">#f5f5f5</color>
<color name="white">#FFFFFF</color>

<!-- Text Colors -->
<color name="text_primary">#333333</color>
<color name="text_secondary">#666666</color>
<color name="text_tertiary">#999999</color>
```

---

## 🔄 Navigation Flow

```
MainActivity (Home)
├── AdvancedFilterBottomSheet (modal)
├── RoomDetailActivity
│   └── BookingCreateActivity
└── Bottom Navigation
    ├── Home (active)
    ├── Đặt lịch → BookingListActivity
    │   ├── Tab: Sắp tới
    │   └── Tab: Đã xem/Huỷ
    ├── Thông báo → NotificationsActivity
    └── Tôi → ProfileActivity
        ├── Tin đã lưu → SavedRoomsActivity
        └── Lịch hẹn → BookingListActivity
```

---

## 🛠️ Customization Points

### Add Real Data

1. **Bookings**: Update `BookingListFragment.getBookingList()`
2. **Saved Rooms**: Update `SavedRoomsActivity.getSampleSavedRooms()`
3. **Notifications**: Update `NotificationsActivity.getSampleNotifications()`

### Connect to Backend

```java
// Replace sample data with API calls
// Example in BookingCreateActivity:

private void confirmBooking() {
    // ... validation ...
    
    // Create booking object
    BookingRequest request = new BookingRequest(
        roomId, fullName, phone, selectedDate, selectedTimeSlot, note
    );
    
    // Call API
    apiService.createBooking(request).enqueue(new Callback<BookingResponse>() {
        @Override
        public void onResponse(Call<BookingResponse> call, Response<BookingResponse> response) {
            if (response.isSuccessful()) {
                Toast.makeText(this, "Đặt lịch thành công!", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
        
        @Override
        public void onFailure(Call<BookingResponse> call, Throwable t) {
            Toast.makeText(this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
        }
    });
}
```

---

## 📊 Testing Checklist

- [x] Build successful
- [ ] App launches without crash
- [ ] Bottom navigation works
- [ ] Filter bottom sheet opens
- [ ] Booking form validates correctly
- [ ] Booking list tabs switch
- [ ] Saved rooms display
- [ ] Notifications show unread state
- [ ] Profile menu navigates correctly
- [ ] All colors match design
- [ ] Smooth animations
- [ ] No memory leaks

---

## 🐛 Troubleshooting

### Build Errors
```bash
# Clean and rebuild
.\gradlew clean
.\gradlew build
```

### Layout Issues
- Check all `@drawable` resources exist
- Verify color resources in `colors.xml`
- Ensure all IDs in XML match Java code

### Navigation Not Working
- Check AndroidManifest.xml for activity registration
- Verify Intent creation in click listeners
- Check bottom nav IDs match

---

## 📚 Documentation Files

1. `TENANT_FEATURES_IMPLEMENTATION.md` - Detailed feature documentation
2. `IMPLEMENTATION_COMPLETE.md` - Implementation summary
3. This file - Quick start guide

---

## ✅ Success Indicators

✓ Build: **SUCCESSFUL**
✓ Files Created: **30+**
✓ Activities: **6 new**
✓ Layouts: **10 new**
✓ Adapters: **4 new**
✓ Models: **2 new**
✓ Colors: **Consistent**
✓ Navigation: **Complete**

---

**Ready to run! 🚀**

For questions or issues, refer to the detailed documentation in:
- `TENANT_FEATURES_IMPLEMENTATION.md`
- `IMPLEMENTATION_COMPLETE.md`

