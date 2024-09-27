const userService = require('../services/user');

const createUser = async (req, res) => {
  try {
    const user = await userService.createUser(req.body.username, req.body.password, req.body.displayName, req.body.profilePicture);
    res.json(user);
  } catch (error) {
    res.status(400).json({ errors: [error.message] });
  }
};

const getUsers = async (req, res) => {
  res.json(await userService.getUsers());
};

const signInUser = async (req, res) => {
  const user = await userService.findUserByUsernameAndPassword(req.body.username, req.body.password);
  if (!user) {
    return res.status(401).json({ errors: ['Invalid username or password'] });
  }
  res.json(user);
};

const getUser = async (req, res) => {
  const user = await userService.findUserByUsername(req.params.id);
  if (!user) {
    return res.status(404).json({ errors: ['User not found'] });
  }
  res.json(user);
};

const updateUser = async (req, res) => {
  const result = await userService.updateUser(req.params.id, req.body.username, req.body.password, req.body.displayName, req.body.profilePicture);
  
  if (result.error) {
    return res.status(400).json({ errors: [result.error] });
  }

  if (!result) {
    return res.status(404).json({ errors: ['User not found'] });
  }

  res.json(result);
};

const deleteUser = async (req, res) => {
  const user = await userService.deleteUser(req.params.id);
  if (!user) {
    return res.status(404).json({ errors: ['User not found'] });
  }
  res.json(user);
};

module.exports = {
  createUser, getUsers, getUser, updateUser, deleteUser, signInUser
};
