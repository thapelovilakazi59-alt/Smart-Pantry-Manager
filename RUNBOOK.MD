# Smart Pantry Manager — Run Book

## Pre-Requisites
| Requirement | Version / Details |
|-------------|-------------------|
| Android Studio | 2023.1.1 (Hedgehog) or newer |
| Java JDK | 8 or newer |
| Android SDK | API 34 (compileSdk), API 24 minimum |
| Device/Emulator | Android 7.0+ (API 24) |
| Storage | ~50 MB free space |

## Step-by-Step: Run the App

### Step 1: Open Project
1. Launch Android Studio.
2. Click **File → Open**.
3. Navigate to the `SmartPantryManager` folder and click **OK**.
4. Wait for the Gradle sync to finish. You will see "Gradle sync finished" in the status bar.

### Step 2: Configure Emulator (if no physical device)
1. Open **Device Manager** (icon in top-right toolbar or via Tools menu).
2. Click **Create Device**.
3. Select **Phone → Pixel 6** (or any device with API 24+).
4. Download and select a system image (e.g., API 34 x86_64).
5. Finish the wizard and start the emulator.

### Step 3: Run the Application
1. Select your device/emulator from the device dropdown in the toolbar.
2. Click the **green Run button** (▶) or press `Shift + F10`.
3. Android Studio will:
    - Compile the Java sources
    - Package the APK
    - Install on the selected device
    - Launch the MainActivity automatically

### Step 4: First-Time Use
1. The app opens on the **Main Screen** with three buttons: My Pantry, Suggested Recipes, Settings.
2. Tap **My Pantry** → tap the **+ (FAB)** button.
3. Add an ingredient (e.g., Name: "Eggs", Quantity: 3, Unit: piece).
4. Tap **Save**.
5. Repeat to add more ingredients (try adding items from a recipe like "Scrambled Eggs on Toast").
6. Go back and tap **Suggested Recipes**.
7. Only recipes where you have **ALL** ingredients will appear.
8. Tap a recipe card to see full details.

### Step 5: Test CRUD & Persistence
1. **Create**: Add a new ingredient.
2. **Read**: View it in the Pantry list.
3. **Update**: Tap the ingredient card → change quantity → Save.
4. **Delete**: Long-press an ingredient → confirm Delete.
5. **Persistence**: Close the app completely (swipe away from recents). Re-open — data is still there.

### Step 6: Test Strict Matching
1. Add exactly: 3 eggs, 2 bread slices, 10g butter.
2. Go to Suggested Recipes — "Scrambled Eggs on Toast" should appear.
3. Delete the bread from pantry.
4. Return to Suggested Recipes — the recipe should **disappear** (strict match).

## Troubleshooting
| Problem | Solution |
|---------|----------|
| Gradle sync fails | Check internet connection; try **File → Sync Project with Gradle Files** |
| Emulator won't start | Ensure Intel HAXM or AMD Hypervisor is installed; try cold boot |
| App crashes on launch | Check Logcat for SQLite errors; ensure no duplicate table creation |
| No recipes shown | Add ingredients that match pre-seeded recipes (see DatabaseHelper seed data) |
| Dark mode not applying | Restart app after toggling dark mode in Settings |

## Build APK (Optional)
1. **Build → Build Bundle(s) / APK(s) → Build APK(s)**.
2. Locate output at `app/build/outputs/apk/debug/app-debug.apk`.
3. Transfer to any Android device and install.
