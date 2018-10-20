const express  = require('express');
const bodyParser = require('body-parser');
const cors  = require('cors');
const mongoose = require('mongoose');
const User = require('./models/User');

const app = express();

app.use(bodyParser.urlencoded({ extended: true }));
app.use(cors());

const port  = process.env.PORT || 5000;

const router = express.Router();




mongoose.connect("mongodb://aryan:aryan1@ds031541.mlab.com:31541/rovault", { useNewUrlParser: true,useCreateIndex: true } ,(err) =>{
    if(err) console.log("error occured");
    else console.log("connected");
});

app.use('/api',router);

app.listen(port);
console.log("app is working");
