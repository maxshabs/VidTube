const videoController = require('../controllers/video');
const express = require('express');
const { authenticateToken } = require('../middlewares/auth');
const multer = require('multer');
const path = require('path');
const fs = require('fs');

const uploadPath = path.join(__dirname, '../public/uploads/');

// Ensure uploads directory exists
if (!fs.existsSync(uploadPath)) {
  fs.mkdirSync(uploadPath, { recursive: true });
}

// Define the storage configuration
const storage = multer.diskStorage({
  destination: function (req, file, cb) {
    cb(null, uploadPath); // Specify the directory to store uploaded files
  },
  filename: function (req, file, cb) {
    cb(null, Date.now() + '-' + file.originalname); // Generate a unique filename
  }
});

const upload = multer({ storage: storage });

const router = express.Router();

router.route('/videos')
  .get(videoController.getVideos);

router.route('/allvideos')
  .get(videoController.getAllVideos);

router.route('/videos/:id')
  .get(videoController.getVideo)
  .post(videoController.getRecommendations);

router.route('/users/:id/videos')
  .get(videoController.getVideosByAuthor)
  .post(authenticateToken, upload.fields([{ name: 'img' }, { name: 'video' }]), videoController.createVideo);

router.route('/users/:id/videos/:pid')
  .get(videoController.getUserVideoById)
  .patch(authenticateToken, upload.fields([{ name: 'img' }, { name: 'video' }]), videoController.updateVideo)
  .delete(authenticateToken, videoController.deleteVideo);

router.route('/videos/:id/like')
  .patch(authenticateToken, videoController.likeVideo);

router.route('/videos/:id/dislike')
  .patch(authenticateToken, videoController.dislikeVideo);

module.exports = router;
