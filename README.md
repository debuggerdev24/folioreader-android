# Folioreader

Folioreader is an open-source Android library designed to provide seamless EPUB reading functionality for your mobile applications. It is lightweight, highly customizable, and easy to integrate, making it the perfect solution for adding EPUB support to your project.

## Features

- 📖 **Seamless EPUB Support**: Effortlessly display EPUB files within your app.
- 🌗 **Customizable Themes**: Offers Light and Dark mode for enhanced readability.
- 🗂️ **Table of Contents**: Navigate chapters with ease.
- ⚡ **Optimized Performance**: Lightweight and fast, ensuring a smooth user experience.
- 🔧 **Developer-Friendly**: Simple APIs for quick integration.

## Installation

### Gradle (Recommended)

1. **Add JitPack Repository**  
   Add the JitPack repository in your project-level `build.gradle` file:

   ```gradle
   allprojects {
       repositories {
           maven { url 'https://jitpack.io' }
       }
   }

2. **Add the Dependency**  
Add the Folioreader dependency in your module-level build.gradle file:

```dependencies {
    implementation 'com.github.debuggerdev24:folioreader-android:v0.1.7'
}

Maven (Alternative)
1. Add JitPack Repository
Add the following repository to your pom.xml:
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

2. Add the Dependency
Include the dependency in your pom.xml file:
<dependency>
    <groupId>com.github.debuggerdev24</groupId>
    <artifactId>folioreader-android</artifactId>
    <version>v0.1.7</version>
</dependency>

Key Features
Handle Table of Contents: Allows navigation through chapters.
Theme Configuration: Toggle between Light and Dark themes.
EPUB Asset Support: Load EPUB files from assets or external sources.



### Notes:
- You can modify the "Contributing" and "License" sections based on your preferences.
- Make sure to create a `LICENSE` file if you mention licensing in the README.
- Feel free to add any additional sections like "Usage" or "Examples" if you want to provide more information to users.
