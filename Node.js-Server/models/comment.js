const mongoose = require('mongoose')

const Schema = mongoose.Schema

const Comment = new Schema({
    text: {
        type: String,
        required: true
    },
    userName: {
        type: String,
        required: true
    },
    displayName: {
        type: String,
        required: true
    },
    date: {
        type: Date,
        required: true,
        default: Date.now
    },
    img: {
        type: String,
        required: true
    },
    videoId: {
        type: String,
        required: true
    },
    likes: [{ type: String }], // Array to store display names of users who liked the comment
    dislikes: [{ type: String }],
});

module.exports = mongoose.model('Comment', Comment)