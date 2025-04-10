# 🎵 Spring Boot Playlist App

A simple RESTful app built with Spring Boot, using:

- H2 in-memory database
- CRUD for music playlists
- JWT-based authentication
- Maven for build
- Logging via SLF4J

## 🔧 Tech Stack

- Java 17+
- Spring Boot 3.x
- Spring Security + JWT
- Spring Data JPA
- H2 Database
- Maven

## 🚀 Getting Started

1. Clone the repo:

2. Run it:

3. H2 Console:

- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:testdb`

## 🔐 Authentication

- Login: `/api/auth/login`
- Register: `/api/auth/register`
- Use JWT token in `Authorization: Bearer <token>` for secured endpoints

## 📬 Endpoints

| Method | Endpoint            | Description          |
| ------ | ------------------- | -------------------- |
| POST   | /api/playlists      | Create new playlist  |
| GET    | /api/playlists      | Get all playlists    |
| GET    | /api/playlists/{id} | Get a playlist by ID |
| PUT    | /api/playlists/{id} | Update playlist      |
| DELETE | /api/playlists/{id} | Delete playlist      |

## 🗒️ Notes

- Use Postman or curl to test endpoints.
- Tokens expire after a set period.
