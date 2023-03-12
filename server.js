const express = require('express');
const mongoose = require('mongoose');

const User = require('./users');
const Activity = require('./activities');

const dbURI = 'mongodb://127.0.0.1:27017/running_buddy_db';

const app = express();

app.use(express.json());

mongoose.connect(dbURI, {
  useNewUrlParser: true,
  useUnifiedTopology: true
}).then(() => {
  console.log('Connected to database');
}).catch((err) => {
  console.log(`Error connecting to database: ${err}`);
});

app.get('/', (req, res) => {
  const db = mongoose.connection; 
  db.on('error', console.error.bind(console, 'MongoDB connection error:'));
  db.once('open', function () {
    console.log('Connected to database!');
  });
});


app.post('/login', async(req, res) => {
  const { username, password } = req.body;
  try {
    const user = await User.findOne({ username, password });
    if (user) {
      res.json({ success: true, message: 'Login successful' }); 
    } else {
      res.json({ success: false, message: 'Incorrect username or password' });
    }
  } catch (err) {
    console.error(err);
    res.status(500).json({ success: false, message: 'Error logging in' });
  }
});

app.post('/register', async(req, res) => {
  const {username, password, firstName, lastName } = req.body;
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

// Route to add a new activity
app.post('/newActivity', (req, res) => {
  // Get the username, distance, and time from the request body
  const { username, distance, time } = req.body;

  // Create a new activity instance with the required fields
  const activity = new Activity({
    username: username,
    distance: distance,
    time: time
  });

  // Save the activity to the database
  activity.save()
    .then(() => {
      res.status(201).send('Activity created');
    })
    .catch((error) => {
      console.error(error);
      res.status(500).send('Error creating activity');
    });
});

app.get('/activities', async (req, res) => {
  const { latitude, longitude, radius } = req.query;
  const activities = await Activity.find({
    location: {
      $near: {
        $geometry: {
          type: "Point",
          coordinates: [longitude, latitude]
        },
        $maxDistance: radius
      }
    }
  });
  res.json(activities);
});

app.get('/users', async (req, res) => {
  const { username } = req.query;

  try {
    const user = await User.findOne({ username });
    if (!user) {
      res.status(404).send(`User with username ${username} not found`);
      return;
    }

    // Extract the user data
    const { firstName, lastName, location, radius } = user;

    res.json([{ username, firstName, lastName, location, radius }]);
  } catch (error) {
    console.error(`Error fetching user with username ${username}: ${error.message}`);
    res.status(500).send(`Error fetching user with username ${username}`);
  }
});


// Start the server
app.listen(3000, () => console.log(`Server started on port 3000`));
module.exports = mongoose;
