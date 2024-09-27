// src/ActionsBar/ActionBar.js

import React, { useState, useEffect } from 'react';
import './ActionBar.css';
import { ReactComponent as ShareIcon } from './share.svg';
import { ReactComponent as Like } from './like.svg';
import { ReactComponent as Dislike } from './dislike.svg';
import { ReactComponent as Whatsapp } from './whatsapp.svg';
import { ReactComponent as Facebook } from './facebook.svg';
import { ReactComponent as Gmail } from './gmail.svg';
import { Link } from 'react-router-dom';

function ActionBar({ userName, img, likes, dislikes, isSubscribed, setIsSubscribed, videoId, loggedInUser }) {
  const [isLiked, setIsLiked] = useState(false);
  const [isDisliked, setIsDisliked] = useState(false);
  const userProfileLink = loggedInUser && userName === loggedInUser.displayName ? '/profile' : `/profile/${userName}`;

  // Setting the like/dislike state
  useEffect(() => {
    if (loggedInUser) {
      setIsLiked(likes.includes(loggedInUser.displayName));
      setIsDisliked(dislikes.includes(loggedInUser.displayName));
    }
  }, [loggedInUser, likes, dislikes]);


  // Handling a like click on video
  const handleLikeClick = async () => {
    if (!loggedInUser) return;
    setIsLiked(!isLiked);
    setIsDisliked(false);

    try {
      const token = localStorage.getItem('jwtToken');
      const response = await fetch(`http://localhost:12345/api/videos/${videoId}/like`, {
        method: 'PATCH',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`,
        },
        body: JSON.stringify({ userDisplayName: loggedInUser.displayName }),
      });
      if (!response.ok) {
        throw new Error('Failed to update like');
      }
    } catch (error) {
      console.error('Error updating like:', error.message);
    }
  };

  // Handling a dislike click on video
  const handleDislikeClick = async () => {
    if (!loggedInUser) return;
    setIsDisliked(!isDisliked);
    setIsLiked(false);

    try {
      const token = localStorage.getItem('jwtToken');
      const response = await fetch(`http://localhost:12345/api/videos/${videoId}/dislike`, {
        method: 'PATCH',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`,
        },
        body: JSON.stringify({ userDisplayName: loggedInUser.displayName }),
      });
      if (!response.ok) {
        throw new Error('Failed to update dislike');
      }
    } catch (error) {
      console.error('Error updating dislike:', error.message);
    }
  };

  const handleSubscribeClick = () => {
    setIsSubscribed(!isSubscribed);
  };

  return (
    <div className="container">
      <div className="userBar">
        <Link to={userProfileLink} className="Link"><img src={img} className="img-thumbnail" alt=""></img></Link>
        <Link to={userProfileLink} className="Link"><a id="userName">{userName}</a></Link>
        <button
          id='subscribe'
          className={`subscribe-button${isSubscribed ? ' subscribed' : ''}`}
          onClick={handleSubscribeClick}
        >
          {isSubscribed ? 'Unsubscribe' : 'Subscribe'}
        </button>
      </div>
      <div className="actionBar">
        <button
          id='like'
          className={`userBar-button${isLiked ? ' liked' : ''}`}
          onClick={handleLikeClick}
        >
          <Like /> Like
        </button>
        <button
          id='dislike'
          className={`userBar-button${isDisliked ? ' disliked' : ''}`}
          onClick={handleDislikeClick}
        >
          <Dislike /> Dislike
        </button>
        <div class="dropdown">
          <a class="userBar-button dropdown-toggle" href="#" role="button" data-bs-toggle="dropdown" aria-expanded="false">
            Share
          </a>

          <ul class="dropdown-menu">
            <li><a class="dropdown-item" href="#"><Whatsapp></Whatsapp> Whatsapp</a></li>
            <li><a class="dropdown-item" href="#"><Facebook></Facebook> Facebook</a></li>
            <li><a class="dropdown-item" href="#"><Gmail></Gmail> Gmail</a></li>
          </ul>
        </div>
      </div>
    </div>
  );
}

export default ActionBar;
