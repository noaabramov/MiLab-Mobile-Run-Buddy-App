const express = require('express');
const router = express.Router();
const User = require('./users');

router.post('/login', async (req, res) => {
  const { username, password } = req.body;

  // Check if username and password are provided
  if (!username || !password) {
    return res.status(400).json({ error: 'Username and password are required' });
  }

  // Check if user exists in the database
  const user = await User.findOne({ username });
  if (!user) {
    return res.status(401).json({ error: 'Invalid username or password' });
  }

  // Check if password matches the hashed password in the database
  const passwordMatches = await user.comparePassword(password);
  if (!passwordMatches) {
    return res.status(401).json({ error: 'Invalid username or password' });
  }

  // Return a success message with the user's ID
  res.status(200).json({ message: 'Login successful', userId: user._id });
});

router.post('/register', async(req, res) => {
  const { firstName, lastName, email } = req.body;
  try {
    const user = new User({
      username,
      password,
      firstName,
      lastName,
    });

    await user.save();
    res.send('User created successfully');
  } catch (err) {
    console.error(err);
    res.status(500).send('Error creating user');
  }
});



module.exports = router;
