const commentService = require('../services/comment');

const createComment = async (req, res) => {
  res.json(await commentService.createComment(req.body.text, req.body.userName, req.body.userDisplayName, req.body.date, req.body.img, req.body.videoId));
};

const updateComment = async (req, res) => {
    const comment = await commentService.updateComment(req.body.id, req.body.text);
    if (!comment) {
      return res.status(404).json({ errors: ['Comment not found'] });
    }
    res.json(comment);
  };

const getComments = async (req, res) => {
  res.json(await commentService.getComments());
};

const getComment = async (req, res) => {
  const comment = await commentService.getCommentById(req.params.id);
  if (!comment) {
    return res.status(404).json({ errors: ['Comment not found'] });
  }
  res.json(comment);
};

const getCommentsByVideoId = async (req, res) => {
  const comments = await commentService.getCommentsByVideoId(req.params.videoId);
  res.json(comments);
};

const deleteComment = async (req, res) => {
  const comment = await commentService.deleteComment(req.params.id);
  if (!comment) {
    return res.status(404).json({ errors: ['Comment not found'] });
  }
  res.json(comment);
};

async function likeComment(req, res) {
    const { id } = req.params;
    const { userDisplayName } = req.body;
  
    try {
      const updatedComment = await commentService.likeComment(id, userDisplayName);
      res.status(200).json(updatedComment);
    } catch (error) {
      res.status(500).json({ error: error.message });
    }
  }
  
  async function dislikeComment(req, res) {
    const { id } = req.params;
    const { userDisplayName } = req.body;
  
    try {
      const updatedComment = await commentService.dislikeComment(id, userDisplayName);
      res.status(200).json(updatedComment);
    } catch (error) {
      res.status(500).json({ error: error.message });
    }
  }

module.exports = { createComment, getComments, updateComment, getComment, deleteComment, getCommentsByVideoId,  likeComment, dislikeComment };