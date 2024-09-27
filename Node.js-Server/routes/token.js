const express = require('express');
const router = express.Router();
const tokenController = require('../controllers/token');

router.route('/tokens')
  .post(tokenController.generateToken);

module.exports = router;
