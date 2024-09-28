# VidTube
VidTube is a full-stack video-sharing platform with both web and Android clients, powered by a Node.js server and a C++ recommendation algorithm. The project is built across four interconnected **[repositories](#repositories)**, each serving a unique function in the system architecture.


## Project Overview

This project is designed to provide a seamless video-streaming experience with recommendations based on a custom algorithm. The system integrates multiple components to serve both web and Android platforms.

## Repositories

The project is split into four main repositories, each handling a different part of the system:

1. **[Web Client Side](https://github.com/maxshabs/project_web/tree/master-part4)**: 
   - Built with React.
   - Provides a user-friendly interface for video browsing, watching, and uploading.

2. **[Android Client Side](https://github.com/eyalg43/project_android/tree/mainPart4)**: 
   - Developed in Java for Android devices.
   - Delivers a native mobile experience for VidTube, with features like video playback, user authentication, and video uploads.

3. **[Node.js Server (Web & Android)](https://github.com/OCDev1/VidTube-server/tree/main-part4)**: 
   - Powers the backend for both web and Android clients.
   - Handles user management, video uploads, API routing, and serves video data to the clients.

4. **[C++ Recommendation Algorithm Server](https://github.com/maxshabs/vidtube-part4.git)**:
   - A multithreaded C++ server that processes user data to generate personalized video recommendations.
   - Communicates with the Node.js server to suggest videos based on user preferences and popular content.

In addition, all the project files are in this repository as well:

- **[Web App](./Web-App)**
- **[Android App](./Android-App)**
- **[Node.js Server](./Node-js-Server)**
- **[Recommendation Algorithm Server](./Recommendation-Algorithm-Server)**


## Architecture

The system is designed with a **client-server architecture**, where the web and Android clients interact with the Node.js backend. The C++ server is responsible for providing video recommendations, and it communicates with the Node.js server to retrieve and update data.

## Technologies Used

- **Frontend (Web)**: React, CSS, JavaScript
- **Frontend (Android)**: Java, XML
- **Backend (Server)**: Node.js, Express, MongoDB
- **Recommendation Algorithm**: C++ (multithreaded)

## Features

- **Responsive Web Interface**: Fully responsive web client built with React.
- **Native Android Application**: Android client for mobile users, featuring smooth video playback and user-friendly interactions.
- **Node.js API**: Backend API that handles data persistence, user management, and video handling for both web and Android.
- **Recommendation Algorithm**: A custom C++ algorithm to provide personalized video recommendations based on watch history and popular trends.
---

## RESTful API

The Node.js server in the VidTube project provides a RESTful API to handle various actions like video uploads, user management, likes, dislikes, and retrieving data for the web and Android clients. The API follows standard REST principles, using different HTTP methods to interact with resources. Below is a summary of the key API endpoints and their functionality:

### Base URL
- For local development: `http://localhost:<PORT>/api`

### **Authentication**

#### `POST /api/tokens`
Authenticate users and generate a JWT (JSON Web Token) for subsequent requests.

- **Request body:**
  ```json
  {
    "username": "exampleUser",
    "password": "password123"
  }
  ```
- **Response:**
  ```json
  {
    "token": "JWT_TOKEN",
    "user": {
      "username": "exampleUser",
      "displayName": "Example User"
    }
  }
  ```

### **Users**

#### `GET /api/users/:username`
Get details of a specific user.

- **Headers:**
  - `Authorization: Bearer JWT_TOKEN`

- **Response:**
  ```json
  {
    "username": "exampleUser",
    "displayName": "Example User",
    "profilePicture": "http://localhost:12345/uploads/profileImage.jpg"
  }
  ```

#### `POST /api/users`
Register a new user.

- **Request body:**
  ```json
  {
    "username": "newUser",
    "password": "newPassword",
    "displayName": "New User",
    "profilePicture": "http://localhost:12345/uploads/newProfile.jpg"
  }
  ```

#### `PATCH /api/users/:username`
Update user details like username, password, display name, and profile picture.

- **Headers:**
  - `Authorization: Bearer JWT_TOKEN`
  
- **Request body (optional):**
  ```json
  {
    "username": "updatedUsername",
    "password": "updatedPassword",
    "displayName": "Updated Name",
    "profilePicture": "http://localhost:12345/uploads/updatedProfile.jpg"
  }
  ```

#### `DELETE /api/users/:username`
Delete a user and remove all associated videos and comments.

- **Headers:**
  - `Authorization: Bearer JWT_TOKEN`

### **Videos**

#### `GET /api/videos`
Retrieve a list of up to 20 videos: 10 popular videos and 10 random videos.

- **Response:**
  ```json
  [
    {
      "title": "Example Video",
      "description": "An example video description",
      "views": "1.2M",
      "img": "http://localhost:12345/uploads/videoThumbnail.jpg",
      "video": "http://localhost:12345/uploads/exampleVideo.mp4",
      "likes": ["User1", "User2"],
      "dislikes": ["User3"]
    }
  ]
  ```

#### `GET /api/videos/:id`
Retrieve details of a specific video.

- **Response:**
  ```json
  {
    "title": "Example Video",
    "description": "An example video description",
    "views": "1.2M",
    "img": "http://localhost:12345/uploads/videoThumbnail.jpg",
    "video": "http://localhost:12345/uploads/exampleVideo.mp4",
    "likes": ["User1", "User2"],
    "dislikes": ["User3"]
  }
  ```

#### `POST /api/users/:username/videos`
Upload a new video. This requires the video file and thumbnail image to be included.

- **Headers:**
  - `Authorization: Bearer JWT_TOKEN`

- **Request body (multipart form data):**
  - `title`: Video title
  - `description`: Video description
  - `video`: Video file
  - `img`: Thumbnail image

- **Response:**
  ```json
  {
    "title": "New Video",
    "description": "New video description",
    "img": "http://localhost:12345/uploads/newVideoThumbnail.jpg",
    "video": "http://localhost:12345/uploads/newVideo.mp4"
  }
  ```

#### `PATCH /api/users/:username/videos/:id`
Update an existing video.

- **Headers:**
  - `Authorization: Bearer JWT_TOKEN`

- **Request body (optional):**
  ```json
  {
    "title": "Updated Video Title",
    "description": "Updated description",
    "img": "http://localhost:12345/uploads/updatedThumbnail.jpg",
    "video": "http://localhost:12345/uploads/updatedVideo.mp4"
  }
  ```

#### `DELETE /api/users/:username/videos/:id`
Delete a video and remove all associated comments.

- **Headers:**
  - `Authorization: Bearer JWT_TOKEN`

### **Likes & Dislikes**

#### `PATCH /api/videos/:id/like`
Like or unlike a video.

- **Headers:**
  - `Authorization: Bearer JWT_TOKEN`

- **Request body:**
  ```json
  {
    "userDisplayName": "Example User"
  }
  ```

#### `PATCH /api/videos/:id/dislike`
Dislike or remove dislike from a video.

- **Headers:**
  - `Authorization: Bearer JWT_TOKEN`

- **Request body:**
  ```json
  {
    "userDisplayName": "Example User"
  }
  ```

### **Comments**

#### `POST /api/comments`
Post a comment on a video.

- **Headers:**
  - `Authorization: Bearer JWT_TOKEN`

- **Request body:**
  ```json
  {
    "text": "Great video!",
    "userName": "exampleUser",
    "displayName": "Example User",
    "img": "http://localhost:12345/uploads/commenterProfile.jpg",
    "videoId": "60e9f9b7c8e4b8a4c3e2a1a7"
  }
  ```

#### `DELETE /api/comments/:id`
Delete a comment.

- **Headers:**
  - `Authorization: Bearer JWT_TOKEN`

---

This API supports key operations like managing users, videos, comments, likes, and dislikes, all while adhering to RESTful principles.
