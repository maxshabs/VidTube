const userService = require('../services/user');

const sessionStore = {}; // Simple in-memory store for sessions

const generateToken = async (req, res) => {
  const user = await userService.findUserByUsernameAndPassword(req.body.username, req.body.password);
  if (!user) {
    return res.status(401).json({ errors: ['Invalid username or password'] });
  }
  const token = userService.generateToken(user);
  
  // Store the user session with token as key
  sessionStore[token] = user;
  
  res.json({ token });
};

module.exports = {
  generateToken,
  sessionStore // Export the session store
};
