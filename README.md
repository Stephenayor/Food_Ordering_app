
📂 Project Structure
- com.example.foodie
- ├── data
- │   ├── model           # Data models (e.g., Product, Order, User)
- │   ├── repository      # Repositories for data handling
- │   └── db              # Room Database and DAOs
- ├── ui
- │   ├── cart            # Cart management screens
- │   ├── order           # Order-related screens
- │   ├── payment         # Payment screens and logic
- │   └── viewmodel       # ViewModels for managing UI-related data
- ├── utils               # Utility classes (e.g., extensions, helpers)
- ├── network             # API service interfaces and Retrofit setup
- └── di                  # Dependency injection setup (Hilt modules)



📱 Features

- Browse Menu: Explore a wide range of food items with detailed descriptions and prices.
- Search Functionality: Quickly find your favorite dishes with the search feature.
- Cart Management: Add, remove, and update items in your cart.
- Order History: View past orders and their details.
- Integrated Payment System: Pay securely using various payment methods (e.g., Paystack, Stripe).
- Push Notifications: Receive updates on your order status in real-time.
- User Authentication: Sign up and log in using email or social media accounts.
- Responsive UI: Optimized for different screen sizes and orientations.
  
  
🛠️ Tech Stack
  
- Language: Kotlin
- Architecture: MVVM (Model-View-ViewModel)
- Dependency Injection: Hilt
- Networking: Retrofit, OkHttp
- Database: Room
- LiveData & ViewModel: For managing UI-related data
- Coroutines & Flow: For asynchronous programming
- Image Loading: Glide
- Payment Integration: Paystack (or Stripe)
- Push Notifications: Firebase Cloud Messaging (FCM)
- Analytics: Firebase Analytics

📸 Screenshots

 Payment Fragment             |         Cart Fragment          
:-------------------------:|:-------------------------:| 
(<img width="247" alt="Screenshot 2024-09-02 at 13 49 50" src="https://github.com/user-attachments/assets/bc3d378e-28cc-45d5-9da6-1cad71d56e3e">)|<img width="247" alt="Screenshot 2024-09-02 at 13 49 43" src="https://github.com/user-attachments/assets/cdfdee56-6192-4c15-ade2-4cfacb74cf8f">

