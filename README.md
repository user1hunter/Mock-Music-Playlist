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

## v2 version is up
- The v2 version is up!
- Checkout the spotifyCompatible branch to utilize the console ui app and add playlists directly from Spotify!
- Here is the link for the Console UI app! https://github.com/nathanelmer/Mock-Music-Playlist-Console-UI

## 🚀 Getting Started

1. Clone the repo:

2. Run it:

## 🔐 Authentication
- Go to Swagger Page http://localhost:8080/swagger-ui/index.html *Refer to swagger for Request Body
- Register: `/api/auth/register` *Enter any username and password
- Login: `/api/auth/login` *Copy the JWT
- Use JWT token in Header `Authorization: Bearer <token>` for secured endpoints

## 📬 Endpoints

| Method | Endpoint            | Description          |
| ------ | ------------------- | -------------------- |
| POST   | /api/playlists      | Create new playlist  |
| GET    | /api/playlists      | Get all playlists    |
| GET    | /api/playlists/{id} | Get a playlist by ID |
| PUT    | /api/playlists/{id} | Update playlist      |
| DELETE | /api/playlists/{id} | Delete playlist      |

3. H2 Console:
- Use H2 Console to verify data
- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:testdb`


## 🗒️ Notes

- Use Postman or curl to test endpoints.
- Tokens expire after a set period.
