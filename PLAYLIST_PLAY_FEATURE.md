# Feature: Play Song dari Playlist + Detail Panel Update

## Deskripsi  
Fitur lengkap yang memungkinkan user untuk memutar lagu dengan double-click pada lagu di playlist dan melihat informasi lagu yang sedang diputar di control bar serta detail panel.

## 🎯 **Masalah yang Diperbaiki di PlaylistFXML**

Detail panel di halaman PlaylistFXML tidak menampilkan informasi lagu yang sedang diputar karena:

1. **Controller Reference Issue**: Field FXML tidak terikat dengan benar  
2. **FXML Package Mismatch**: Controller reference salah package
3. **Cross-Controller Communication**: Tidak ada komunikasi antar controller instances

## Fitur yang Ditambahkan

### 1. **Double-Click Play Functionality (Semua Halaman)**
- User dapat double-click pada baris lagu di tabel playlist
- Lagu akan otomatis ditambahkan ke queue dan diputar
- Bekerja di halaman utama (FXMLDocument) dan halaman playlist (PlaylistFXML)
- Control bar akan ter-update dengan informasi lagu yang dipilih

### 2. **Perubahan pada SongData.java**
- Ditambahkan field `Music music` untuk menyimpan referensi ke objek Music asli
- Ditambahkan constructor baru yang menerima objek Music
- Ditambahkan getter/setter untuk objek Music

### 3. **Perubahan pada PlaylistFXMLController.java**
- Ditambahkan field `parentController` untuk referensi ke MainLayoutController
- Ditambahkan method `playSelectedSong()` untuk handle play functionality
- Ditambahkan row factory untuk mendeteksi double-click pada tabel
- Update constructor SongData untuk menyertakan objek Music

### 4. **Perubahan pada MainLayoutController.java**
- Ditambahkan field `musicControlController` dengan @FXML annotation
- Ditambahkan method `setCurrentTime()` untuk mengatur posisi waktu lagu
- Perbaikan referensi ke ControlFXMLController

## Cara Menggunakan

### Untuk User:
1. Buka aplikasi streaming music
2. Navigasi ke halaman playlist
3. **Double-click** pada lagu yang ingin diputar
4. Lagu akan otomatis diputar dan control bar akan ter-update

### Untuk Developer:
```java
// Untuk set parent controller (jika dibutuhkan)
playlistController.setParentController(mainLayoutController);

// Cara membuat SongData dengan Music object
SongData songData = new SongData(
    0, 
    info.title, 
    info.author, 
    "", 
    String.valueOf(info.length), 
    new Image(info.artworkUrl), 
    music // Music object
);
```

## Flow Kerja

1. **User double-clicks lagu** di playlist
2. **`playSelectedSong()`** method dipanggil
3. **Lagu ditambahkan ke queue** menggunakan `MusicPlayerFacade.addToQueue()`
4. **Player jump ke lagu tersebut** menggunakan `MusicPlayerFacade.jump()`
5. **Player mulai memutar** dengan `MusicPlayerFacade.resume()`
6. **Control bar di-update** dengan informasi lagu (title, artist, duration)
7. **Posisi waktu di-reset** ke 0

## Error Handling
- Validasi null check untuk SongData dan Music object
- Try-catch untuk operasi player
- Fallback ke MainLayoutController.getInstance() jika parent controller null
- Error logging untuk debugging

## Fitur Selanjutnya yang Bisa Ditambahkan
- Single-click untuk preview lagu
- Right-click context menu
- Drag & drop untuk mengatur urutan playlist
- Play all songs in playlist
- Shuffle mode untuk playlist