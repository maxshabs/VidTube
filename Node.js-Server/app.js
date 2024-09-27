const express = require('express');
const path = require('path');
const bodyParser = require('body-parser');
const cors = require('cors');
const customEnv = require('custom-env');
const mongoose = require('mongoose');
var app = express();

customEnv.env(process.env.NODE_ENV, './config');
console.log(process.env.CONNECTION_STRING);
console.log(process.env.PORT);

mongoose.connect(process.env.CONNECTION_STRING, {
  useNewUrlParser: true,
  useUnifiedTopology: true,
});

// Increase the payload size limit
app.use(bodyParser.urlencoded({ extended: true, limit: '10mb' }));
app.use(bodyParser.json({ limit: '10mb' }));

app.use(cors());
app.use(express.static('public'));

// Serve static files from the 'public/uploads' directory
app.use('/uploads', express.static(path.join(__dirname, 'public/uploads')));

// Serve static files from the React app
app.use(express.static(path.join(__dirname, 'public')));

// routes
const userRoutes = require('./routes/user');
app.use('/api', userRoutes);
const videos = require('./routes/video');
app.use('/api', videos);
const commentsRouter = require('./routes/comment');
app.use('/api', commentsRouter);
const tokenRoutes = require('./routes/token');
app.use('/api', tokenRoutes);

// The "catchall" handler: for any request that doesn't match an API route,
// send back React's index.html file.
app.get('*', (req, res) => {
  res.sendFile(path.join(__dirname, 'public', 'index.html'));
});

app.listen(process.env.PORT, () => {
  console.log(`Server is running on port ${process.env.PORT}`);
});
