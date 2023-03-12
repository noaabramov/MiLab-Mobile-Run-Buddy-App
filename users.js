const mongoose = require('mongoose');

const userSchema = new mongoose.Schema({
  username: {
    type: String,
    required: true,
    unique: true,
    trim: true
  },
  password: {
    type: String,
    required: true,
    trim: true
  },
  firstName: {
    type: String,
    required: true,
    unique: false
  },
  lastName: { 
    type: String,
    required: true,
    unique: false
  },
  location: {
    type: {
      type: String,
      enum: ['Point'],
      required: false
    },
    coordinates: {
      type: [Number],
      required: false
    }
  },
  radius: {
    type: Number,
    required: false
  }
});

// Add a text index on the 'username' field
userSchema.index({username: 'text'});

const User = mongoose.model('User', userSchema);

module.exports = User;
