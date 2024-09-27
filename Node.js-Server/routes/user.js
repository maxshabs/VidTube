const express = require('express');
const router = express.Router();
const userController = require('../controllers/user');
const { authenticateToken } = require('../middlewares/auth');

router.route('/users')
  .get(authenticateToken, userController.getUsers)
  .post(userController.createUser);

router.route('/users/signin')
  .post(userController.signInUser);

  router.route('/users/:id')
  .get(authenticateToken, userController.getUser)
  .delete(authenticateToken, userController.deleteUser)
  .patch(authenticateToken, userController.updateUser);


module.exports = router;
