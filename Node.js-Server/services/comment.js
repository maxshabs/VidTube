const Comment = require('../models/comment');

const createComment = async (text, userName, displayName, date, img, videoId) => {
    const comment = new Comment({ text, userName, displayName, date, img, videoId });
    try {
      const savedComment = await comment.save();
      return savedComment; // Return the saved comment object
    } catch (error) {
      throw new Error(error.message); // Handle any errors that occur during saving
    }
  };
  
const getComments = async () => {
  return await Comment.find({});
};

const getCommentsByVideoId = async (videoId) => {
  return await Comment.find({ videoId });
};

const getCommentById = async (id) => {
  return await Comment.findById(id);
};

const updateComment = async (id, text) => {
  const comment = await getCommentById(id);
  if (!comment) return null;
  comment.text = text;
  await comment.save();
  return comment;
};

const deleteComment = async (id) => {
  const comment = await getCommentById(id);
  if (!comment) return null;
  await comment.deleteOne();
  return comment;
};

// likes on comments
async function likeComment(commentId, userDisplayName) {
    try {
      const comment = await Comment.findById(commentId);
      if (!comment) throw new Error('Comment not found');
  
      // Check if user is already in likes array
      if (comment.likes.includes(userDisplayName)) {
        // Remove user from likes array
        comment.likes = comment.likes.filter(name => name !== userDisplayName);
      } else {
        // Add user to likes array
        comment.likes.push(userDisplayName);
      }
  
      // Ensure user is removed from dislikes array
      comment.dislikes = comment.dislikes.filter(name => name !== userDisplayName);
  
      await comment.save();
      return comment;
    } catch (error) {
      throw error;
    }
  }
  
  
  async function dislikeComment(commentId, userDisplayName) {
    try {
      const comment = await Comment.findById(commentId);
      if (!comment) throw new Error('Comment not found');
  
      // Check if user is already in dislikes array
      if (comment.dislikes.includes(userDisplayName)) {
        // Remove user from dislikes array
        comment.dislikes = comment.dislikes.filter(name => name !== userDisplayName);
      } else {
        // Add user to dislikes array
        comment.dislikes.push(userDisplayName);
      }
  
      // Ensure user is removed from likes array
      comment.likes = comment.likes.filter(name => name !== userDisplayName);
  
      await comment.save();
      return comment;
    } catch (error) {
      throw error;
    }
  }
  
  

module.exports = { createComment, getComments, getCommentById, updateComment, deleteComment, getCommentsByVideoId,  likeComment, dislikeComment };