# ♟️ Cờ Vua Online — Chess Game Web Application

> Ứng dụng cờ vua trực tuyến xây dựng bằng **Spring Boot 4**, hỗ trợ chơi real-time giữa hai người chơi hoặc đấu với AI (Stockfish), kèm hệ thống quản lý người dùng và replay ván cờ.


## 🎯 Giới thiệu

**Cờ Vua Online** là một ứng dụng web J2EE cho phép người dùng đăng ký, đăng nhập và tham gia các ván cờ vua trực tuyến theo thời gian thực (real-time). Ứng dụng hỗ trợ hai chế độ chơi:

- **Người vs Người (PvP):** Hai người dùng kết nối và thi đấu trong cùng một phòng.
- **Người vs AI:** Người chơi đấu với động cơ Stockfish thông qua API.

Sau mỗi ván, người chơi có thể xem lại toàn bộ diễn biến (replay) theo từng nước đi.

---

## ✨ Tính năng

### 👤 Quản lý người dùng
- Đăng ký tài khoản với kiểm tra trùng username/email và độ mạnh mật khẩu
- Đăng nhập / Đăng xuất với session-based authentication
- Trang hồ sơ cá nhân: xem lịch sử ván đấu, số trận thắng/thua
- Trang cài đặt cá nhân

### 🎮 Trò chơi
- **Phòng chơi real-time** qua WebSocket (SockJS + STOMP)
- **Chế độ PvP:** tạo phòng, chia sẻ ID, đối thủ tham gia
- **Chế độ vs AI:** tích hợp Stockfish API với cache nước đi thông minh
- Xác thực nước đi hợp lệ cho từng loại quân (Vua, Hậu, Xe, Tượng, Mã, Tốt)
- Hỗ trợ luật đặc biệt: nhập thành (castling), bắt tốt qua đường (en passant), phong hậu (promotion)
- Phát hiện chiếu hết (Checkmate) và hòa (Stalemate)
- Các hành động trong ván: đề nghị hòa, đầu hàng

### 📼 Replay
- Xem lại toàn bộ ván cờ từng nước một
- Hiển thị bàn cờ tương ứng với từng trạng thái FEN
- Tô sáng nước đi vừa thực hiện

### 🛡️ Quản trị (Admin)
- Xem danh sách toàn bộ người dùng
- Khóa / Mở khóa tài khoản (ban/unban)
- Phân quyền admin qua interceptor

---

## 🛠️ Công nghệ sử dụng

| Thành phần         | Công nghệ                                      |
|--------------------|------------------------------------------------|
| **Backend**        | Spring Boot 4.0.2, Spring MVC, Spring Data JPA |
| **Bảo mật**        | Spring Security, BCrypt                        |
| **Real-time**      | Spring WebSocket, STOMP, SockJS                |
| **Template Engine**| Thymeleaf                                      |
| **Cơ sở dữ liệu**  | MySQL 8+                                       |
| **AI Engine**      | Stockfish Online API (`stockfish.online`)      |
| **Build Tool**     | Maven (Wrapper)                                |
| **Ngôn ngữ**       | Java 21                                        |
| **Testing**        | JUnit, Selenium, WebDriverManager, H2 (in-mem) |
| **Containerize**   | Docker (multi-stage build)                     |
| **Khác**           | Lombok, Jackson Databind                       |

---

## ⚙️ Yêu cầu hệ thống

- **Java 21** trở lên
- **Maven 3.9+** (hoặc dùng `mvnw` đi kèm)
- **MySQL 8+**
- (Tùy chọn) **Docker** & **Docker Compose**

---

## 🚀 Hướng dẫn cài đặt

### 1. Clone repository

```bash
git clone https://github.com/<your-username>/covua_web_J2EE.git
cd covua_web_J2EE
```

### 2. Tạo cơ sở dữ liệu

Tạo schema MySQL và import file SQL có sẵn:

```sql
CREATE DATABASE chessgame CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

```bash
mysql -u root -p chessgame < Database/chessgame.sql
```

### 3. Cấu hình kết nối database

Tạo file `src/main/resources/application-local.properties` (hoặc chỉnh `application.properties`):

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/chessgame?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=your_password
```

### 4. Build và chạy ứng dụng

```bash
# Sử dụng Maven Wrapper (không cần cài Maven riêng)
./mvnw spring-boot:run

# Hoặc build trước rồi chạy JAR
./mvnw clean package -DskipTests
java -jar target/chessgame-0.0.1-SNAPSHOT.jar
```

Ứng dụng sẽ chạy tại: **http://localhost:8080**

---

## 🔧 Cấu hình môi trường

Dự án hỗ trợ cấu hình qua **biến môi trường** (phù hợp khi deploy lên Render, Railway, v.v.):

| Biến môi trường          | Mô tả                              | Mặc định                                          |
|--------------------------|------------------------------------|---------------------------------------------------|
| `PORT`                   | Cổng server                        | `8080`                                            |
| `SPRING_PROFILES_ACTIVE` | Spring profile đang hoạt động      | `default`                                         |
| `JDBC_DATABASE_URL`      | URL kết nối JDBC                   | `jdbc:mysql://localhost:3306/chessgame?...`        |
| `JDBC_DATABASE_USERNAME` | Tên người dùng database            | `root`                                            |
| `JDBC_DATABASE_PASSWORD` | Mật khẩu database                  | *(rỗng)*                                          |

**Sử dụng profile `local` khi phát triển:**

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

---

## 🐳 Chạy bằng Docker

```bash
# Build Docker image
docker build -t chessgame .

# Chạy container (cần truyền biến môi trường database)
docker run -p 8080:8080 \
  -e JDBC_DATABASE_URL="jdbc:mysql://<db-host>:3306/chessgame?useSSL=false&serverTimezone=UTC" \
  -e JDBC_DATABASE_USERNAME="root" \
  -e JDBC_DATABASE_PASSWORD="your_password" \
  chessgame
```

---

## 📁 Cấu trúc dự án

```
covua_web_J2EE/
├── Database/
│   └── chessgame.sql               # Script khởi tạo & dữ liệu mẫu
├── src/
│   ├── main/
│   │   ├── java/com/group18/chessgame/
│   │   │   ├── ChessgameApplication.java   # Entry point
│   │   │   ├── config/                     # Cấu hình Spring
│   │   │   │   ├── SecurityConfig.java     # Bảo mật & BCrypt
│   │   │   │   ├── WebSocketConfig.java    # STOMP & SockJS
│   │   │   │   ├── WebConfig.java          # Interceptors MVC
│   │   │   │   ├── AdminInterceptor.java   # Phân quyền admin
│   │   │   │   ├── BanCheckInterceptor.java# Kiểm tra tài khoản bị khóa
│   │   │   │   ├── ActiveUserListener.java # Theo dõi session người dùng
│   │   │   │   └── DataSeeder.java         # Tạo dữ liệu mẫu ban đầu
│   │   │   ├── controller/                 # Xử lý HTTP & WebSocket
│   │   │   │   ├── AuthController.java     # Đăng ký, đăng nhập, đăng xuất
│   │   │   │   ├── GameLobbyController.java# Tạo/tham gia/hủy phòng & replay
│   │   │   │   ├── GameController.java     # REST API game logic
│   │   │   │   ├── ChatController.java     # Tin nhắn trong phòng (WebSocket)
│   │   │   │   ├── UserController.java     # Profile & cài đặt
│   │   │   │   ├── AdminController.java    # Quản lý người dùng (Admin)
│   │   │   │   └── HomeController.java     # Trang chủ / lobby
│   │   │   ├── service/                    # Business logic
│   │   │   │   ├── GameLogicService.java   # Luật cờ, xác thực nước đi
│   │   │   │   ├── GameLobbyService.java   # Quản lý phòng & lịch sử
│   │   │   │   ├── GameConnectionService.java # Kết nối người chơi
│   │   │   │   ├── GameStateCache.java     # Cache trạng thái game in-memory
│   │   │   │   ├── PlayerService.java      # Xác thực & quản lý người dùng
│   │   │   │   └── StockfishApiService.java# Gọi Stockfish AI API
│   │   │   ├── model/                      # Entity / Domain objects
│   │   │   │   ├── Board.java              # Bàn cờ 8x8
│   │   │   │   ├── Game.java               # Ván cờ
│   │   │   │   ├── Move.java               # Nước đi
│   │   │   │   ├── Player.java             # Người chơi
│   │   │   │   ├── Spot.java               # Ô trên bàn cờ
│   │   │   │   └── piece/                  # Các loại quân cờ
│   │   │   │       ├── Piece.java (abstract)
│   │   │   │       ├── King.java
│   │   │   │       ├── Queen.java
│   │   │   │       ├── Rook.java
│   │   │   │       ├── Bishop.java
│   │   │   │       ├── Knight.java
│   │   │   │       └── Pawn.java
│   │   │   ├── repository/                 # Spring Data JPA repositories
│   │   │   ├── dto/                        # Data Transfer Objects
│   │   │   ├── enums/                      # Enum definitions
│   │   │   │   ├── GameMode.java           # PvP / PvAI
│   │   │   │   ├── GameStatus.java         # WAITING / IN_PROGRESS / FINISHED
│   │   │   │   ├── GameResult.java         # WHITE_WIN / BLACK_WIN / DRAW
│   │   │   │   ├── GameTermination.java    # CHECKMATE / STALEMATE / RESIGN / DRAW
│   │   │   │   ├── PieceColor.java         # WHITE / BLACK
│   │   │   │   └── RegisterResult.java     # SUCCESS / USERNAME_TAKEN / ...
│   │   │   └── utils/                      # Tiện ích (FEN, session, ...)
│   │   └── resources/
│   │       ├── application.properties      # Cấu hình chính
│   │       ├── application-local.properties# Cấu hình local (không commit)
│   │       ├── templates/                  # Thymeleaf HTML templates
│   │       │   ├── index.html              # Trang chủ / lobby
│   │       │   ├── login.html
│   │       │   ├── register.html
│   │       │   ├── game.html               # Phòng chơi
│   │       │   ├── replay.html             # Xem lại ván cờ
│   │       │   ├── profile.html
│   │       │   ├── settings.html
│   │       │   ├── fragments/              # Layout dùng chung
│   │       │   └── admin/
│   │       │       └── users.html
│   │       └── static/                     # CSS, JS, images
│   └── test/                               # Unit & Integration tests
├── Dockerfile
├── pom.xml
└── README.md
```

---

## 🏗️ Kiến trúc hệ thống

```
Browser (Thymeleaf + SockJS)
        │
        ├── HTTP Request ──► Spring MVC Controllers ──► Services ──► JPA Repositories ──► MySQL
        │
        └── WebSocket (STOMP) ──► /ws endpoint ──► SimpMessagingTemplate ──► /topic/game/{id}
                                                                           ──► /topic/lobby
```

### Luồng tạo và chơi ván cờ

1. Người dùng đăng nhập → session được lưu server-side
2. Tạo phòng (`POST /game/create`) → sự kiện `ROOM_CREATED` broadcast qua WebSocket tới lobby
3. Đối thủ tham gia (`POST /game/join`) → sự kiện `PLAYER_JOINED` khởi động ván đấu
4. Mỗi nước đi gửi qua `POST /api/game/{id}/move` → `GameLogicService` xác thực → lưu DB → broadcast kết quả
5. Kết thúc ván → lưu kết quả, người chơi có thể xem `GET /game/replay/{id}`

---

## 🔌 API Endpoints

### Trang & Điều hướng

| Method | URL                   | Mô tả                          |
|--------|-----------------------|--------------------------------|
| GET    | `/`                   | Trang chủ / danh sách phòng   |
| GET    | `/login`              | Trang đăng nhập               |
| POST   | `/login`              | Xử lý đăng nhập               |
| GET    | `/register`           | Trang đăng ký                  |
| POST   | `/register`           | Xử lý đăng ký                 |
| GET    | `/logout`             | Đăng xuất                     |
| GET    | `/profile`            | Trang hồ sơ cá nhân           |
| GET    | `/settings`           | Trang cài đặt                  |

### Phòng chơi

| Method | URL                   | Mô tả                          |
|--------|-----------------------|--------------------------------|
| POST   | `/game/create`        | Tạo phòng PvP                 |
| POST   | `/game/create-bot`    | Tạo phòng vs AI               |
| POST   | `/game/join`          | Tham gia phòng                 |
| POST   | `/game/cancel`        | Hủy phòng đang chờ            |
| GET    | `/game/{id}`          | Vào phòng chơi                |
| GET    | `/game/replay/{id}`   | Xem lại ván cờ                |

### Game REST API

| Method | URL                              | Mô tả                               |
|--------|----------------------------------|-------------------------------------|
| POST   | `/api/game/{id}/move`            | Thực hiện nước đi                   |
| GET    | `/api/game/{id}/valid-moves`     | Lấy danh sách nước đi hợp lệ        |
| GET    | `/api/game/{id}/state`           | Lấy trạng thái ván cờ hiện tại      |
| GET    | `/api/game/{id}/board`           | Lấy trạng thái bàn cờ              |
| POST   | `/api/game/{id}/action`          | Hành động (đề nghị hòa, đầu hàng)  |
| POST   | `/api/game/{id}/bot-move`        | Yêu cầu AI đi nước (PvAI)          |
| GET    | `/api/game/{id}/replay-moves`    | Lấy danh sách nước đi để replay     |

### Admin

| Method | URL                   | Mô tả                          |
|--------|-----------------------|--------------------------------|
| GET    | `/admin/users`        | Danh sách người dùng          |
| POST   | `/admin/users/ban`    | Khóa / mở khóa tài khoản     |

### WebSocket Topics

| Topic                     | Mô tả                                    |
|---------------------------|------------------------------------------|
| `/topic/lobby`            | Sự kiện lobby (tạo/hủy phòng)           |
| `/topic/game/{id}`        | Sự kiện trong phòng (nước đi, kết thúc) |

---

## 🗄️ Cơ sở dữ liệu

File schema và dữ liệu mẫu: [`Database/chessgame.sql`](Database/chessgame.sql)

**Các bảng chính:**

| Bảng     | Mô tả                                              |
|----------|----------------------------------------------------|
| `player` | Thông tin người dùng (username, email, password, role, is_banned) |
| `game`   | Thông tin ván cờ (players, status, result, mode, FEN) |
| `move`   | Lịch sử nước đi (notation, FEN, UCI move, thứ tự)  |

---

## 🧪 Kiểm thử

```bash
# Chạy toàn bộ test
./mvnw test

# Test sử dụng H2 in-memory (không cần MySQL)
./mvnw test -Dspring.profiles.active=test
```

Dự án bao gồm:
- **Unit tests** cho game logic
- **Integration tests** với Thymeleaf và Spring MVC
- **Selenium tests** cho UI end-to-end

---

## 👥 Tác giả

Dự án được phát triển bởi **Group 18** — môn học J2EE / Lập trình Web với Java.

Dự án được phát triển phục vụ mục đích học tập.
