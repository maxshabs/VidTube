const net = require('net');

function sendMessageToCppServer(message, videoData) {
    console.log("Attempting to send message to C++ server:", message); // Add this line
    return new Promise((resolve, reject) => {
        const client = new net.Socket();
        client.connect(5555, '192.168.1.173', () => {
            // Combine the message and video data
            const fullMessage = `${message}\n${JSON.stringify(videoData)}`;
            client.write(fullMessage);
        });

        client.on('data', (data) => {
            resolve(data.toString().trim());
            client.destroy();  // Close the connection
        });

        client.on('error', (err) => {
            reject(err);
        });
    });
}

module.exports = { sendMessageToCppServer };
