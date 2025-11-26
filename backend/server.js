const express = require('express');
const cors = require('cors');
const path = require('path');
const { Pool } = require('pg');
const sqlite3 = require('sqlite3').verbose();

const app = express();
const PORT = process.env.PORT || 3000;

// Middleware
app.use(cors());
app.use(express.json());

// Database Abstraction Layer
const DB_TYPE = process.env.DATABASE_URL ? 'postgres' : 'sqlite';
let db;

console.log(`Using database: ${DB_TYPE}`);

if (DB_TYPE === 'postgres') {
    db = new Pool({
        connectionString: process.env.DATABASE_URL,
        ssl: { rejectUnauthorized: false } // Required for Railway
    });
} else {
    const dbPath = path.resolve(__dirname, 'ruleta.db');
    db = new sqlite3.Database(dbPath, (err) => {
        if (err) console.error('Error opening SQLite database:', err.message);
        else console.log('Connected to SQLite database.');
    });
}

// Helper function to run queries
async function query(sql, params = []) {
    if (DB_TYPE === 'postgres') {
        // Postgres uses $1, $2, etc. instead of ?
        let paramIndex = 1;
        const pgSql = sql.replace(/\?/g, () => `$${paramIndex++}`);
        const result = await db.query(pgSql, params);
        return result;
    } else {
        return new Promise((resolve, reject) => {
            if (sql.trim().toUpperCase().startsWith('SELECT')) {
                db.all(sql, params, (err, rows) => {
                    if (err) reject(err);
                    else resolve({ rows });
                });
            } else {
                db.run(sql, params, function (err) {
                    if (err) reject(err);
                    else resolve({ rows: [], lastID: this.lastID, changes: this.changes });
                });
            }
        });
    }
}

// Initialize Database
async function initializeDatabase() {
    try {
        if (DB_TYPE === 'postgres') {
            await query(`CREATE TABLE IF NOT EXISTS opciones (
                id SERIAL PRIMARY KEY,
                texto TEXT NOT NULL
            )`);
            await query(`CREATE TABLE IF NOT EXISTS resultados (
                id SERIAL PRIMARY KEY,
                resultado TEXT NOT NULL,
                timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )`);
        } else {
            db.serialize(() => {
                db.run(`CREATE TABLE IF NOT EXISTS opciones (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    texto TEXT NOT NULL
                )`);
                db.run(`CREATE TABLE IF NOT EXISTS resultados (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    resultado TEXT NOT NULL,
                    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP
                )`);
            });
        }
        console.log('Database initialized.');
    } catch (err) {
        console.error('Error initializing database:', err);
    }
}

initializeDatabase();

// Routes

// --- Opciones ---

// Get all options
app.get('/api/opciones', async (req, res) => {
    try {
        const result = await query("SELECT * FROM opciones ORDER BY id DESC");
        res.json(result.rows);
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
});

// Create an option
app.post('/api/opciones', async (req, res) => {
    const { texto } = req.body;
    if (!texto) {
        res.status(400).json({ error: "Field 'texto' is required" });
        return;
    }
    try {
        const result = await query("INSERT INTO opciones (texto) VALUES (?) RETURNING id", [texto]);
        // Handle ID return difference
        let newId;
        if (DB_TYPE === 'postgres') {
            newId = result.rows[0].id;
        } else {
            newId = result.lastID;
        }
        res.json({ id: newId, texto });
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
});

// Delete an option
app.delete('/api/opciones/:id', async (req, res) => {
    const { id } = req.params;
    try {
        const result = await query("DELETE FROM opciones WHERE id = ?", [id]);
        res.json({ message: "Deleted", changes: result.changes || result.rowCount });
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
});

// --- Resultados ---

// Get all results
app.get('/api/resultados', async (req, res) => {
    try {
        const result = await query("SELECT * FROM resultados ORDER BY id DESC");
        res.json(result.rows);
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
});

// Create a result
app.post('/api/resultados', async (req, res) => {
    const { resultado } = req.body;
    if (!resultado) {
        res.status(400).json({ error: "Field 'resultado' is required" });
        return;
    }
    try {
        const result = await query("INSERT INTO resultados (resultado) VALUES (?) RETURNING id", [resultado]);
        let newId;
        if (DB_TYPE === 'postgres') {
            newId = result.rows[0].id;
        } else {
            newId = result.lastID;
        }
        res.json({ id: newId, resultado });
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
});

// Delete a result
app.delete('/api/resultados/:id', async (req, res) => {
    const { id } = req.params;
    try {
        const result = await query("DELETE FROM resultados WHERE id = ?", [id]);
        res.json({ message: "Deleted", changes: result.changes || result.rowCount });
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
});

// Start server
app.listen(PORT, () => {
    console.log(`Server is running on http://localhost:${PORT}`);
});
