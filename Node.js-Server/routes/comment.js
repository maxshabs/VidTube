// routes/comments.js

const commentController = require('../controllers/comment');
const { authenticateToken } = require('../middlewares/auth');
const express = require('express');
const router = express.Router();

router.route('/comments')
    .get(commentController.getComments)
    .post(authenticateToken, commentController.createComment);

router.route('/comments/:id')
    .get(commentController.getComment)

router.route('/users/:id/comments')
    .patch(authenticateToken, commentController.updateComment)
    .delete(authenticateToken, commentController.deleteComment);

router.route('/:id/like')
    .patch(authenticateToken, commentController.likeComment);

router.route('/:id/dislike')
    .patch(authenticateToken, commentController.dislikeComment);

module.exports = router;
