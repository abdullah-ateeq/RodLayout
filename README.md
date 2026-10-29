# 🛠️ RodLayout

**Professional Workshop & Fabrication Marking Layout Utility for Android**

[![Platform](https://img.shields.io/badge/Platform-Android-green.svg?style=flat&logo=android)](https://www.android.com)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin-purple.svg?style=flat&logo=kotlin)](https://kotlinlang.org/)
[![UI](https://img.shields.io/badge/UI-Jetpack%20Compose-blue.svg?style=flat&logo=jetpackcompose)](https://developer.android.com/jetpack/compose)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Build Status](https://img.shields.io/badge/Build-Passing-brightgreen.svg)]()

---

## 📌 Overview

**RodLayout** is a modern, high-precision Android application designed for machinists, woodworkers, metal fabricators, railing installers, cabinetmakers, and engineers. It simplifies and automates marking and hole-spacing calculations along rods, pipes, tubes, bars, gables, and strut channels.

Calculating layouts manually with tape measures or calipers often leads to **cumulative rounding errors**. RodLayout solves this by calculating mark positions algebraically from fixed origins, delivering exact measurement coordinates in both Metric and Imperial units.

---

## ✨ Key Features

- 📏 **High-Precision Calculation Engine**: Evaluates marking positions algebraically to prevent floating-point drift and cumulative error buildup.
- 📐 **Multiple Layout Modes**:
  - **Fixed Spacing**: Place marks at exact intervals along usable spans.
  - **Mark Count**: Automatically divide usable spans into equal increments based on a target count.
- 🎯 **Boundary & Alignment Styles**:
  - **At Boundaries**: First and last marks sit precisely at start/end offset boundaries.
  - **Centered**: Automatically centers mark arrays within usable spans with balanced end margins.
  - **Equal Spaces (Gaps)**: Divides usable spans into equal spaces on both ends and between marks.
- 🔄 **Direction & Reference Origins**:
  - **Layout Directions**: Normal (Left-to-Right / Start-to-End) and Reversed (Right-to-Left / End-to-Start).
  - **Measurement Origins**: Measure relative to Start Edge or Centerline Midpoint.
- 🌍 **Multi-Unit & Imperial Fraction Support**:
  - Units: Millimeters (`mm`), Centimeters (`cm`), Meters (`m`), Inches (`in`), and Feet (`ft`).
  - Imperial fraction representation (e.g., `12 3/8"`, `5/16"`) down to 1/64" precision settings.
- 📊 **Interactive Canvas Diagram**: Visualizes rod length, start/end offsets, margins, mark lines, and spacing intervals dynamically with touch controls.
- 📋 **Export & Sharing**:
  - Output results to formatted plain text or CSV files.
  - Copy layout tables directly to clipboard or share via native Android share sheet.
  - In-app search and filtering for individual mark coordinates.
- 🎨 **Industrial Theme & Offline First**:
  - Material 3 design with dark mode, light mode, and high-contrast workshop styling.
  - **100% Offline & Private**: Zero runtime permissions, zero internet access, zero tracking.

---

## ⚙️ How It Works (Calculation Logic)

$$\text{Usable Span} = L_{\text{total}} - S_{\text{offset}} - E_{\text{offset}} - (2 \times \text{Margin})$$

1. **Usable Marking Span**: Subtracts start offsets, end offsets, and safety margins from total rod length to determine active layout zone.
2. **Interval Calculation**:
   - *At Boundaries*: $\text{Spacing} = \frac{\text{Usable Span}}{N - 1}$
   - *Equal Spaces*: $\text{Spacing} = \frac{\text{Usable Span}}{N + 1}$
3. **Cumulative Error Prevention**: Calculates absolute position $P(i) = S_{\text{offset}} + (i \times \text{Spacing})$ for mark index $i$, rounding only for presentation.
4. **Centerline Reference**: Calculates $X_{\text{center}} = P(i) - \frac{L_{\text{total}}}{2}$ for symmetrical mounting and balanced drill hole references.

---

## 🛠️ Practical Applications

- 🏗️ **Deck Railing & Balusters**: Calculate baluster spacing to adhere strictly to building safety codes (< 4" gap rules).
- 🪵 **Cabinetry & Woodworking**: Layout 32mm system shelf pin holes or dowels along cabinet gables.
- ⚡ **Metal Fabrication & Structural Steel**: Punch marks for picket holes in square tubing, angle iron, or flat bar.
- 🔧 **Pipe, Conduit & Strut Supports**: Layout strut channels and pipe hangers evenly along overhead runs.

---

## 📱 Screenshots & UI Structure

The app consists of five primary navigation screens:

1. **Home / Layout Screen**: Input fields for total length, offsets, margins, target spacing, unit selection, and layout mode toggles with live preview diagram and summary card.
2. **Results Screen**: Complete list of calculated marks with absolute coordinates from start/origin/center, step distances, search filter, and export buttons (CSV / Text / Copy / Share).
3. **Interactive Diagram Screen**: Expanded high-resolution vector canvas visualization of the physical rod with layout annotations.
4. **Settings Screen**: Theme selection (Light, Dark, System Default), unit defaults, and imperial fraction precision settings (1/16", 1/32", 1/64").
5. **About Screen**: Information on calculation formulas, workshop applications, and app specs.

---

## 🗂️ Project Architecture

```
RodLayout/
├── app/
│   ├── build.gradle.kts
│   └── src/
│       ├── main/
│       │   ├── java/com/example/rodlayout/
│       │   │   ├── MainActivity.kt
│       │   │   ├── calculator/
│       │   │   │   ├── ExportFormatter.kt
│       │   │   │   └── RodCalculator.kt
│       │   │   ├── model/
│       │   │   │   ├── LayoutEnums.kt
│       │   │   │   ├── MarkItem.kt
│       │   │   │   ├── MeasurementUnit.kt
│       │   │   │   └── RodLayoutConfig.kt
│       │   │   ├── ui/
│       │   │   │   ├── components/
│       │   │   │   │   ├── NumericInputField.kt
│       │   │   │   │   ├── RodDiagram.kt
│       │   │   │   │   ├── StatCards.kt
│       │   │   │   │   └── UnitSelectorChips.kt
│       │   │   │   ├── screens/
│       │   │   │   │   ├── AboutScreen.kt
│       │   │   │   │   ├── HomeScreen.kt
│       │   │   │   │   ├── MainScreen.kt
│       │   │   │   │   ├── ResultsScreen.kt
│       │   │   │   │   ├── SettingsScreen.kt
│       │   │   │   │   └── SplashScreen.kt
│       │   │   │   ├── theme/
│       │   │   │   │   ├── Color.kt
│       │   │   │   │   ├── Theme.kt
│       │   │   │   │   └── Type.kt
│       │   │   │   └── viewmodel/
│       │   │   │       ├── RodViewModel.kt
│       │   │   │       └── SettingsViewModel.kt
│       │   └── AndroidManifest.xml
│       └── test/
│           └── java/com/example/rodlayout/
│               ├── RodCalculatorTest.kt
│               └── RodViewModelTest.kt
├── build.gradle.kts
├── gradle.properties
├── settings.gradle.kts
└── README.md
```

---

## 💻 Tech Stack & Requirements

- **Language**: [Kotlin](https://kotlinlang.org/) (100% Jetpack Compose)
- **Minimum SDK**: API Level 24 (Android 7.0 Nougat)
- **Target SDK**: API Level 35
- **Architecture**: MVVM with Kotlin Coroutines & StateFlow
- **UI Toolkit**: Material 3 Design Components & Jetpack Compose Canvas
- **Testing**: JUnit 4 for unit tests on layout engine and viewmodels

---

## 🚀 Getting Started & Building

### Prerequisites

- [Android Studio Ladybug (2024.2.1)](https://developer.android.com/studio) or newer
- JDK 17 or higher
- Android SDK 35

### Building from Source

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/abdullah-ateeq/RodLayout.git
   cd RodLayout
   ```

2. **Run Unit Tests**:
   ```bash
   ./gradlew test
   ```

3. **Build Debug APK**:
   ```bash
   ./gradlew assembleDebug
   ```

4. **Install on Connected Device**:
   ```bash
   ./gradlew installDebug
   ```

---

## 🧪 Testing

The core calculation logic and ViewModel state handling are fully unit tested. Run tests using:

```bash
./gradlew test --info
```

Test coverage includes:
- Metric and Imperial unit conversions & cross-conversions.
- Fractional inch formatting & rounding checks.
- Fixed spacing calculation edge cases (Boundary, Centered, Equal Spaces).
- Mark count calculations and zero/single-mark handling.
- Realigned layout direction (Normal vs. Reversed) and center origin positions.
- Validation bounds and overlap warning triggers.

---

## 📄 License

This project is licensed under the [MIT License](LICENSE).

---

Developed with ❤️ for woodworkers, fabricators, and engineers by **Abdullah Ateeq**.
