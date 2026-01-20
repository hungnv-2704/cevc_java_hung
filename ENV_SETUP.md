# Tour Booking Service - Environment Setup

## Environment Variables

This project uses environment variables for sensitive configuration. 

### Setup Instructions

1. Copy `.env.example` to `.env`:
   ```bash
   cp .env.example .env
   ```

2. Update the values in `.env` with your actual credentials:
   - **Database Configuration**: PostgreSQL connection details
   - **JWT Configuration**: Secret key for JWT token generation
   - **Google OAuth2**: Client ID and Secret from Google Cloud Console
   - **Server Configuration**: Application port

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `DB_URL` | PostgreSQL database URL | `jdbc:postgresql://localhost:5432/practice_db` |
| `DB_USERNAME` | Database username | `spring_user` |
| `DB_PASSWORD` | Database password | `123456` |
| `JWT_SECRET` | Secret key for JWT tokens | (see .env.example) |
| `JWT_EXPIRATION` | JWT token expiration time (ms) | `86400000` (24 hours) |
| `GOOGLE_CLIENT_ID` | Google OAuth2 Client ID | (your value) |
| `GOOGLE_CLIENT_SECRET` | Google OAuth2 Client Secret | (your value) |
| `SERVER_PORT` | Application server port | `8080` |

### Security Notes

- **Never commit `.env` file to version control**
- The `.env` file is already added to `.gitignore`
- Use `.env.example` as a template for team members
- Keep sensitive credentials secure

### Running the Application

The application will automatically load environment variables from `.env` file. If variables are not set, it will use default values from `application.properties`.

```bash
./mvnw spring-boot:run
```

### Production Deployment

For production, set environment variables through your hosting platform's configuration instead of using `.env` files.
