<!-- PROJECT BANNER -->
<p align="center">
  <img src="banner.gif" alt="bideo.tech Banner" width="100%">
</p>

<h1 align="center">🎥 bideo — Scalable Video Streaming Platform</h1>
<p align="center">
  <i>A high-performance video streaming platform built for the modern web</i>
</p>

<p align="center">
  <a href="https://bideo.rajit.cc"><img src="https://img.shields.io/badge/Live%20Demo-bideo-blue?style=flat&logo=google-chrome" alt="Live Demo"></a>
  <a href="LICENSE"><img src="https://img.shields.io/badge/License-MIT-green?style=flat&logo=open-source-initiative" alt="License"></a>
  <img src="https://img.shields.io/badge/Backend-Spring%20Boot-6DB33F?logo=springboot&logoColor=white" alt="Backend">
  <img src="https://img.shields.io/badge/Frontend-React-61DAFB?logo=react&logoColor=black" alt="Frontend">
</p>

---

## 📖 About the Project

**bideo** is a **YouTube-like** video streaming platform that combines **Spring Boot, AWS S3, RabbitMQ, Redis, PostgreSQL, and Firebase** to deliver a seamless streaming experience.

It supports:
- Video upload and processing (**HLS format**)
- Secure user authentication with **Firebase**
- Metadata caching for lightning-fast loading
- Scalable architecture with async processing

---

## 🚀 Features

- 🔐 **Firebase Authentication** — Secure login and user management
- 📦 **Chunked Video Streaming** — HLS format stored on AWS S3
- 📬 **Asynchronous Processing** — RabbitMQ job queue for uploads
- ⚡ **Metadata Caching** — Redis for reduced database hits
- 🗄 **Reliable Database** — PostgreSQL for user & video records
- 📱 **Responsive Playback** — Adaptive streaming for any device

---

## 🛠️ Tech Stack

| Logo | Technology | Purpose |
|------|------------|---------|
| ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6DB33F?logo=springboot&logoColor=white) | **Spring Boot** | REST API backend |
| ![Firebase](https://img.shields.io/badge/Firebase-FFCA28?logo=firebase&logoColor=black) | **Firebase** | Authentication & user management |
| ![PostgreSQL](https://img.shields.io/badge/PostgreSQL-316192?logo=postgresql&logoColor=white) | **PostgreSQL** | User & video metadata |
| ![AWS S3](https://img.shields.io/badge/Amazon%20S3-569A31?logo=amazons3&logoColor=white) | **AWS S3** | Video storage (HLS chunks) |
| ![RabbitMQ](https://img.shields.io/badge/RabbitMQ-FF6600?logo=rabbitmq&logoColor=white) | **RabbitMQ** | Queue for video processing |
| ![Redis](https://img.shields.io/badge/Redis-DC382D?logo=redis&logoColor=white) | **Redis** | Caching metadata |
| ![HLS](https://img.shields.io/badge/HLS-FF0000?logo=youtube&logoColor=white) | **HLS** | Adaptive video streaming |

---

## 📂 Architecture Overview

```plaintext
 User
  │
  ▼
[Frontend] ──▶ [Spring Boot Backend] ──▶ [PostgreSQL]  
                 │           │
                 │           ├──▶ [AWS S3: Stores .m3u8 + TS chunks]
                 │           ├──▶ [Redis: Cache metadata]
                 │           └──▶ [RabbitMQ: Video processing jobs]
```

---

## ⚙️ How It Works

### 1️⃣ Video Upload

1. **User uploads video** from the frontend.
2. Backend stores metadata in **PostgreSQL** and pushes a job to **RabbitMQ**.
3. A background worker processes the video into **HLS format** (`.m3u8` + `.ts` chunks).
4. Processed files are uploaded to **AWS S3**.
5. Video record is updated with the S3 URL.

### 2️⃣ Video Playback

1. **User requests video** → Metadata is fetched from **Redis** (or PostgreSQL if not cached).
2. Video is streamed directly from **AWS S3** using **HLS adaptive streaming**.
3. Redis caches the metadata for future requests.

---

## 💻 Local Development

### Prerequisites

Make sure you have the following installed:

- **Java 17+**
- **PostgreSQL** (for user & video metadata)
- **Redis** (for caching)
- **RabbitMQ** (for job queues)
- **AWS S3 bucket** (or **MinIO** for local testing)

---

### Steps to Run Locally

```bash
# Clone Backend repository
git clone https://github.com/Mist-AA/bideo_api
1. Uncomment line 16 in SupportVariables
2. Uncomment line 30 in logback-spring.xml
3 In edit configurations, Modify Options > Add vm options. Then within VM options field, add this   -Dspring.profiles.active=dev
4. Use application-dev.properties with secret keys/values
5. Use service-account.json in resource folder

# Clone Frontend Rerpository
https://github.com/rajitk13/bideo_ui
In global.ts change https://bideo.tech path to http://localhost:8080
```

---

### Docker Quick Start Commands

```bash
Redis
docker run -d --name my-redis -p 6379:6379 redis

Rabbitmq
docker run -d -p 5672:5672 -p 15672:15672 --name my-rabbit rabbitmq:3-management


```

---

### 🗝️ Environment Variables

Create a `.env` file in the root directory:

```env
FIREBASE_PROJECT_ID=your_project_id
FIREBASE_CLIENT_EMAIL=your_client_email
FIREBASE_PRIVATE_KEY=your_private_key
AWS_ACCESS_KEY_ID=your_access_key
AWS_SECRET_ACCESS_KEY=your_secret
AWS_S3_BUCKET_NAME=your_bucket_name
POSTGRES_URL=jdbc:postgresql://localhost:5432/
POSTGRES_USER=postgres
POSTGRES_PASSWORD=your_password
RABBITMQ_HOST=localhost
REDIS_HOST=localhost
```

---

## 📸 Screenshots

| Home Page                     | Video Player                       |
| ----------------------------- | ---------------------------------- |
| ![Home Page](assets/home.png) | ![Video Player](assets/player.png) |

---

## 🛠 Future Roadmap

- [ ] Add recommendation engine based on viewing history
- [ ] Enable real-time comments & chat
- [ ] Build a creator analytics dashboard
- [ ] Launch mobile app support

---

## 📜 License

Distributed under the **MIT License**. See [`LICENSE`](LICENSE) for details.

---

<p align="center">Creators <a href="https://github.com/rajitk13">Rajit</a> | <a href="https://github.com/Mist-AA">Ayushmaan</a></p>
