# RuletAPP Microservice API

This is a simple Node.js/Express API for the RuletAPP Android application.

## Setup

1.  Navigate to the `backend` directory.
2.  Install dependencies:
    ```bash
    npm install
    ```
3.  Start the server:
    ```bash
    npm start
    ```
    The server runs on `http://localhost:3000`.

## API Endpoints

### Opciones (Options)

*   **GET /api/opciones**: Get all options.
*   **POST /api/opciones**: Create a new option.
    *   Body: `{ "texto": "Option Name" }`
*   **DELETE /api/opciones/:id**: Delete an option by ID.

### Resultados (Results)

*   **GET /api/resultados**: Get all results.
*   **POST /api/resultados**: Create a new result.
    *   Body: `{ "resultado": "Result Name" }`
*   **DELETE /api/resultados/:id**: Delete a result by ID.
