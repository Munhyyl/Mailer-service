### 2. Mailer Service README.md

```markdown
# Mailer Service

Имэйл илгээх болон хадгалахад зориулсан RESTful сервис. Энэ сервис нь энгийн текст имэйл эсвэл AWS S3 дээрх зургуудыг агуулсан HTML имэйл илгээдэг.

## Онцлог
- Энгийн текст имэйл илгээх.
- S3 дээрх хавтасны зургуудыг агуулсан HTML имэйл илгээх.
- Илгээсэн имэйлүүдийг өгөгдлийн санд хадгалах.
- Имэйл хайх, жагсаах функц.

## Технологийн Стек
- **Java**: Spring Boot
- **Spring Mail**: Имэйл илгээх
- **JPA**: H2 эсвэл бусад DB интеграци
- **Nginx**: Reverse Proxy (сонголттой)

## Суулгах Заавар

### Шаардлага
- Java 11+ (JDK)
- Maven
- SMTP сервер (жишээ нь Gmail)
- `File Manager Service` API хаяг
- Ubuntu сервер (сонголттой, deploy-д)

### Алхмууд
1. Репозиторийг клон хийнэ:
   ```bash
   git clone https://github.com/[your-username]/mailer-service.git
   cd mailer-service
   ```
2. Тохиргооны файл (`application.properties`) бөглөнө:
   ```properties
   spring.mail.host=smtp.gmail.com
   spring.mail.port=587
   spring.mail.username=your-email@gmail.com
   spring.mail.password=your-app-password
   spring.mail.properties.mail.smtp.auth=true
   spring.mail.properties.mail.smtp.starttls.enable=true
   file.manager.url=http://<file-manager-ip>:8082
   ```
3. Build хийнэ:
   ```bash
   mvn clean package
   ```
4. Сервисийг ажиллуулна:
   ```bash
   java -jar target/mailer-service-0.0.1-SNAPSHOT.jar
   ```

### Nginx Reverse Proxy (Сонголттой)
Nginx-ийг reverse proxy болгон ашиглах бол:
```nginx
server {
    listen 80;
    server_name <your-public-ip-or-domain>;

    location / {
        proxy_pass http://localhost:8081;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```
Тохиргоог идэвхжүүлэх:
```bash
sudo ln -s /etc/nginx/sites-available/mailer-service /etc/nginx/sites-enabled/
sudo systemctl reload nginx
```

## API Endpoint-ууд
- **POST** `/api/emails/send`: Имэйл илгээнэ (зурагтай эсвэл тексттэй).
  - Body жишээ:
    ```json
    {
        "to": "user@example.com",
        "subject": "Test Email",
        "body": "Hello World",
        "folder": "marchDogPhotos"  // Сонголттой
    }
    ```
- **GET** `/api/emails`: Бүх имэйлийг жагсаана.
- **GET** `/api/emails/search?query=<text>`: Имэйл хайна.

### Жишээ Хүсэлт
Зурагтай имэйл илгээх:
```bash
curl -X POST http://<your-ip>:8081/api/emails/send \
-H "Content-Type: application/json" \
-d '{"to":"user@example.com","subject":"March Dog Photos","folder":"marchDogPhotos"}'
```

## Хэрэглээ
Энэ сервисийг `File Manager Service`-тэй хамт ашиглан S3 дээрх зургуудыг имэйлээр илгээж, хэрэглэгчийн интерфэйсээр удирдана.

