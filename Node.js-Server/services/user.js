const jwt = require('jsonwebtoken');
const User = require('../models/user');
const Comment = require('../models/comment');
const Video = require('../models/video');

const createUser = async (username, password, displayName, profilePicture) => {
  try {
    const user = new User({ username, password, displayName, profilePicture });
    return await user.save();
  } catch (error) {
    if (error.code === 11000) {
      if (error.keyPattern.username) {
        throw new Error('Username already exists.');
      }
      if (error.keyPattern.displayName) {
        throw new Error('Display Name already exists.');
      }
    } else {
      throw error;
    }
  }
};

const getUsers = async () => {
  return await User.find({});
};

const updateUser = async (id, newUsername, password, displayName, profilePicture) => {
  try {
    const user = await findUserByUsername(id);
    if (!user) {
      throw new Error('User not found.');
    }

    const oldUsername = user.username;
    const oldDisplayName = user.displayName;
    
    if (newUsername) user.username = newUsername;
    if (password) user.password = password;
    if (displayName) user.displayName = displayName;
    if (profilePicture) user.profilePicture = profilePicture;

    await user.save();

    const updateFields = {};
    const commentUpdateFields = {};

    if (newUsername) {
      updateFields.username = newUsername;
      commentUpdateFields.userName = newUsername;
    }
    if (displayName) {
      updateFields.author = displayName;
      commentUpdateFields.displayName = displayName;
    }
    if (profilePicture) {
      updateFields.authorImage = profilePicture;
      commentUpdateFields.img = profilePicture;
    }

    if (Object.keys(updateFields).length > 0) {
      await Video.updateMany({ username: oldUsername }, updateFields);
      await Video.updateMany({ likes: oldDisplayName }, { $set: { "likes.$": displayName } });
      await Video.updateMany({ dislikes: oldDisplayName }, { $set: { "dislikes.$": displayName } });
    }

    if (Object.keys(commentUpdateFields).length > 0) {
      await Comment.updateMany({ userName: oldUsername }, commentUpdateFields);
      await Comment.updateMany({ likes: oldDisplayName }, { $set: { "likes.$": displayName } });
      await Comment.updateMany({ dislikes: oldDisplayName }, { $set: { "dislikes.$": displayName } });
    }

    return user;
  } catch (error) {
    if (error.code === 11000) {
      if (error.keyPattern.username) {
        return { error: 'Username already exists.' };
      }
      if (error.keyPattern.displayName) {
        return { error: 'Display Name already exists.' };
      }
    } else {
      throw error;
    }
  }
};

const deleteUser = async (id) => {
  const user = await findUserByUsername(id);
  if (!user) {
    return null;
  }

  const displayName = user.displayName;
  await user.deleteOne();

  await Comment.deleteMany({ userName: id });
  await Video.deleteMany({ username : id });

  await Video.updateMany({ likes: displayName }, { $pull: { likes: displayName } });
  await Video.updateMany({ dislikes: displayName }, { $pull: { dislikes: displayName } });

  await Comment.updateMany({ likes: displayName }, { $pull: { likes: displayName } });
  await Comment.updateMany({ dislikes: displayName }, { $pull: { dislikes: displayName } });
  return user;
};

const findUserByUsername = async (username) => {
  return await User.findOne({ username });
};

const findUserByUsernameAndPassword = async (username, password) => {
  return await User.findOne({ username, password });
};

const generateToken = (user) => {
  const payload = { username: user.username, displayName: user.displayName };
  return jwt.sign(payload, process.env.JWT_SECRET, { expiresIn: '1h' });
};

module.exports = {
  createUser, getUsers, updateUser, deleteUser, findUserByUsername, findUserByUsernameAndPassword, generateToken
};
