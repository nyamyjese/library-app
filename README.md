# Librairie App

Built with Poja.

A Spring Boot library management system for handling books, stock, sales and customers.

Database hosted on Neon.

## Connect to Neon Database (for team members)


1. **Get your connection URL**
    - Go to [Neon Console](https://console.tench)
    - Select **your personal branch** (e.g., `pierre-dev`, `marie-dev`, or `dev`)
    - Click **Dashboard** → **Connection details**
    - Copy the **Java/JDBC** URL (each branch has its own unique URL)

2. **Configure IntelliJ**
    - `Run` → `Edit Configurations` → `PojaApplication`
    - Add to **Environment variables**:
    - Exemple: NEON_DATABASE_URL=jdbc:postgresql://ep-example-123456-pooler.us-east-1.aws.neon.tech/neondb?user=neondb_owner&password=example_password&sslmode=require&channelBinding=require