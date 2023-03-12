const mongoose = require('mongoose');

const activitySchema = new mongoose.Schema({
  username: String,
  distance: Number,
  time: Number,
});


const Activity = mongoose.model('Activity', activitySchema);

module.exports = Activity;