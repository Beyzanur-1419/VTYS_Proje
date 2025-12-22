const ngrok = require('ngrok');

(async function () {
    try {
        // Attempt to kill any existing process first
        try { await ngrok.kill(); } catch (e) { }
        try { await ngrok.disconnect(); } catch (e) { }

        const url = await ngrok.connect({ proto: 'http', addr: 3000 });
        console.log(`NGROK_URL=${url}`);

        // Keep alive
        setInterval(() => { }, 1000 * 60 * 60);
    } catch (error) {
        console.error('Error starting ngrok:', error);
        process.exit(1);
    }
})();
