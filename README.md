# 🎵 Spring Boot Playlist App

A simple RESTful app built with Spring Boot, using:

- H2 in-memory database
- CRUD for music playlists
- JWT-based authentication
- Maven for build

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

3. Clone and run the Java Console UI that can be found here https://github.com/nathanelmer/Mock-Music-Playlist-Console-UI

## 🔐 Authentication
- Utilizes JWT for user authentication 
- Utilizes Spotify Token for Spotify endpoints

## 📬 Endpoints

| Method | Endpoint               | Description          |
| ------ | -------------------    | -------------------- |
| POST   | /api/playlists/{id}    | Add playlist         |
| GET    | /api/playlists         | Get all playlists    |
| GET    | /api/playlists/spotify | Get a playlist by ID |
| DELETE | /api/playlists/{id}    | Delete playlist      |

3. H2 Console:
- Use H2 Console to verify data
- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:testdb`


## 🗒️ Notes

- Tokens expire after a set period.
