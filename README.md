
# Triptale - A Travel Diary App

**Triptale** is a mobile application designed to help travelers explore, save, and share their journeys. The app provides features like place searching, saving locations, writing stories, and sharing with a community.

---

## Features

- **Search for Places**: Integrated with the Google Places API for accurate place searches.
- **Save Places**: Save favorite places locally (Room Database) and sync to Firebase for cross-device access.
- **Interactive Map**: View saved places on a map with markers and explore nearby attractions.
- **Share Stories**: Write stories tied to saved places and share them with the community.
- **Community Feed**: Explore stories shared by other users and interact with their content.

---

## Technologies Used

- **Jetpack Compose**: For building the UI declaratively.
- **Retrofit**: For API interactions (Google Places and Directions APIs).
- **Kotlin Coroutines**: For handling asynchronous operations.
- **Room Database**: For local storage and offline capability.
- **Firebase**: For cloud data synchronization.
- **Google Maps API**: For map views and markers.

---

## Installation and Setup

### Prerequisites
- **Android Studio** 
- A **Google API key** with access to Google Maps and Places APIs

### Steps to Clone and Run the Project

1. **Clone the Repository**:
   ```bash
   git clone <repository-url>
   cd Triptale
   ```

2. **Open in Android Studio**:
   - Open Android Studio.
   - Select "Open an existing project."
   - Navigate to the cloned repository and select it.

3. **Add Your Google API Key**:
   - Open the `gradle.properties` file located in the project directory.
   - Replace the placeholder with your **Google API key**:
     ```properties
     GOOGLE_MAPS_API_KEY=your_api_key
     ```

4. **Sync Gradle**:
   - Allow Android Studio to sync Gradle and download the required dependencies.

5. **Run the App**:
   - Connect an Android device or start an emulator.
   - Click the "Run" button in Android Studio to build and launch the app.

---

## Usage

### Exploring Features
- **Search**: Use the search bar to find cities or attractions.
- **Save**: Save a location by clicking on the save button.
- **Map**: Navigate to the map to view your saved locations and nearby attractions.
- **Stories**: Write and share a story linked to a saved place. Explore the community feed for inspiration.

---

## Contributing

I welcome contributions! To contribute:
1. Fork the repository.
2. Create a feature branch.
3. Submit a pull request with a description of the changes.

---

## License

This project is licensed under the [MIT License](LICENSE).

---

## Contact

For questions or feedback, feel free to reach out:
- **Email**: giahaonguyen2207@gmail.com

