const express  = require('express');
const bodyParser = require('body-parser');
const cors  = require('cors');
const mongoose = require('mongoose');
const User = require('./models/User');

const app = express();
const port  = process.env.PORT || 5000;
const server = app.listen(port);

const io = require('socket.io')(server);

app.set('socketio',io);
app.use(bodyParser.urlencoded({ extended: true }));
app.use(cors());

const router = express.Router();

let user = [];

io.on('connection',(socket) => {
   console.log("connected");

});
io.on('success',(success) => {
    if(success){
        console.log("done");
    }
    else console.log("not done");
});

router.get('/', (req, res) => {
    res.json({sucess: true});
});


router.post('/login',(req, res) => {
    const user  = req.body.email;
    const pass = req.body.pass;

    User.findOne({'email': user}, (err, user) => {
        if (err) res.json({success: false,err: "server error"});

        else if (user == null) res.json({success: false,err: "user not found"});
        else {
            if (user.pass === pass) {
                let sites = [];
                user.details.forEach((site, i) => sites.push(site.website));
                res.json({success: true, sites: sites});
            }
            else res.json({success: false,err: "wrong password"});
        }
    });
});

router.post('/user',(req, res) => {

    User.findOne({'email': req.body.email},(err, user) => {
        if (err) res.json({success: false,err: "server error"});

        if (user == null) res.json({success: false,err: "user not found"});
        else {
            if (user.pass === pass) {
                let sites = [];
                user.details.forEach((site, i) => sites.push(site.website));
                res.json({success: true, sites: sites});
            }
            else res.json({success: false,err: "wrong password"});
        }

    } );
});

router.put('/user',(req,res) => {
    const email = req.body.email;
    const detail = {'website': req.body.website,'username': req.body.username, 'pass': req.body.pass};

    User.updateOne({'email': email}, {$push : { details: detail}}, (err, res) => {

    });

});

router.post('/auth', (req,res) => {
    const url = req.body.url;
    const email = req.body.email;
    console.log("aa");
    let io = req.app.get('socketio');
    io.emit('authenticate');

});

mongoose.connect("mongodb://aryan:aryan1@ds031541.mlab.com:31541/rovault", { useNewUrlParser: true,useCreateIndex: true } ,(err) =>{
    if(err) console.log("error occured");
    else console.log("connected");
});

app.use('/api',router);

console.log("app is working");
