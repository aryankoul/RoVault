const mongoose = require('mongoose');

const UserSchema = mongoose.Schema({
   email: {
       type: String,
       required: true,
       unique: true
   },
   pass: {
       type: String,
       required: true
   },
    details: [{
       website: {
           type: String,
           required: true
       },
        username: {
           type: String,
            required: true
        },
        password: {
           type: String,
            required: true
        }

    }]

});

module.exports = User =  mongoose.model('User', UserSchema);

