// const fetch = require('node-fetch'); // Native fetch is available in Node 18+

// Helper to log steps
const log = (msg) => console.log(`[TEST] ${msg}`);

const BASE_URL = 'http://localhost:3000/api';

async function testApi() {
    try {
        log('Starting API tests...');

        // 1. Create Option
        log('Creating Option "Test Option"...');
        const createOptRes = await fetch(`${BASE_URL}/opciones`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ texto: 'Test Option' })
        });
        const createdOpt = await createOptRes.json();
        console.log('Created:', createdOpt);

        // 2. List Options
        log('Listing Options...');
        const listOptRes = await fetch(`${BASE_URL}/opciones`);
        const listOpt = await listOptRes.json();
        console.log('Options:', listOpt);

        // 3. Create Result
        log('Creating Result "Winner"...');
        const createResRes = await fetch(`${BASE_URL}/resultados`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ resultado: 'Winner' })
        });
        const createdRes = await createResRes.json();
        console.log('Created Result:', createdRes);

        // 4. List Results
        log('Listing Results...');
        const listResRes = await fetch(`${BASE_URL}/resultados`);
        const listRes = await listResRes.json();
        console.log('Results:', listRes);

        // 5. Delete Option
        if (createdOpt.id) {
            log(`Deleting Option ID ${createdOpt.id}...`);
            const delOptRes = await fetch(`${BASE_URL}/opciones/${createdOpt.id}`, { method: 'DELETE' });
            const delOpt = await delOptRes.json();
            console.log('Delete status:', delOpt);
        }

        log('Tests completed.');

    } catch (error) {
        console.error('Test failed:', error);
    }
}

// Check if fetch is available (Node 18+), otherwise warn
if (!globalThis.fetch) {
    console.log("Native fetch not found. Please run with Node 18+ or install node-fetch.");
} else {
    testApi();
}
