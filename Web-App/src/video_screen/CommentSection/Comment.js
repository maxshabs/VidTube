// src/comment/Comment.js

import React, { useState, useEffect } from 'react';
import './Comment.css';
import { ReactComponent as Like } from '../ActionsBar/like.svg';
import { ReactComponent as Dislike } from '../ActionsBar/dislike.svg';

function Comment({ _id, text, username, displayName, date, img, onUpdate , loggedInUser, calculateTimeAgo, likes , dislikes }) {
  const [liked, setLiked] = useState(false);
  const [disliked, setDisliked] = useState(false);
  const [isEditing, setIsEditing] = useState(false);
  const [editText, setEditText] = useState(text);
  const [displayTime, setDisplayTime] = useState(date);

  // For comment time
  useEffect(() => {
    setDisplayTime(calculateTimeAgo(date));
    const interval = setInterval(() => {
      setDisplayTime(calculateTimeAgo(date));
    }, 60000); // Update every minute
    return () => clearInterval(interval);
  }, [date, calculateTimeAgo]);

  // Reset edit text to original comment text when not editing
  useEffect(() => {
    if (!isEditing) {
      setEditText(text);
    }
  }, [isEditing, text]);

  // Setting the likes/dislikes state
  useEffect(() => {
    if (loggedInUser) {
      setLiked(likes.includes(loggedInUser.displayName));
      setDisliked(dislikes.includes(loggedInUser.displayName));
    }
  }, [loggedInUser, likes, dislikes]);

  // Handling a like click
  const handleLikeClick = async () => {
    if (!loggedInUser) return;
    setLiked(!liked);
    setDisliked(false);

    try {
      const token = localStorage.getItem('jwtToken');
      const response = await fetch(`http://localhost:12345/api/${_id}/like`, {
        method: 'PATCH',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`,
        },
        body: JSON.stringify({ userDisplayName: loggedInUser.displayName }),
      });
      if (response.ok) {
        onUpdate(); // Refresh comments to update likes
      } else {
        throw new Error('Failed to update like');
      }
    } catch (error) {
      console.error('Error updating like:', error.message);
    }
  };

  // Handling a dislike click
  const handleDislikeClick = async () => {
    if (!loggedInUser) return;
    setDisliked(!disliked);
    setLiked(false);

    try {
      const token = localStorage.getItem('jwtToken');
      const response = await fetch(`http://localhost:12345/api/${_id}/dislike`, {
        method: 'PATCH',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`,
        },
        body: JSON.stringify({ userDisplayName: loggedInUser.displayName }),
      });
      if (response.ok) {
        onUpdate(); // Refresh comments to update dislikes
      } else {
        throw new Error('Failed to update dislike');
      }
    } catch (error) {
      console.error('Error updating dislike:', error.message);
    }
  };

  // Handling an edit button click
  const handleEditClick = () => {
    setIsEditing(true);
  };

  // Handling a save button click
  const handleEditSaveClick = async () => {
    try {
      const token = localStorage.getItem('jwtToken');
      const response = await fetch(`http://localhost:12345/api/users/${_id}/comments`, {
        method: 'PATCH',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`,
        },
        
        body: JSON.stringify({ id: _id , text: editText }),
      });
      if (response.ok) {
        onUpdate();
        setIsEditing(false);
      } else {
        throw new Error('Failed to update comment');
      }
    } catch (error) {
      console.error('Error updating comment:', error.message);
    }
  };
  
  // Handling a delete button click
  const handleDeleteClick = async () => {
    try {
      const token = localStorage.getItem('jwtToken');
      const response = await fetch(`http://localhost:12345/api/users/${_id}/comments`, {
        method: 'DELETE',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`,
        },
      });
      if (response.ok) {
        onUpdate();
      } else {
        throw new Error('Failed to delete comment');
      }
    } catch (error) {
      console.error('Error deleting comment:', error.message);
    }
  };
  const handleEditTextChange = (e) => {
    setEditText(e.target.value);
  };

  return (
    <div className='commentContainer'>
      <img src={img} className="userPicture" alt='' />
      <div className="commentText">
        <p>{displayName} - {displayTime}</p>
        {isEditing ? (
          <input
            className='editTextField'
            value={editText}
            onChange={handleEditTextChange}
          />
        ) : (
          <p className='lighter'>{text}</p>
        )}
        <div>
          <button
            _id='liked'
            className={`comment-button ${liked ? 'liked' : ''}`}
            onClick={handleLikeClick}
          >
            <Like />
          </button>
          <button
            _id='disliked'
            className={`comment-button ${disliked ? 'disliked' : ''}`}
            onClick={handleDislikeClick}
          >
            <Dislike />
          </button>
          {/* allow user to perform actions only on his own comments (if comment displayname matches user displayname) */}
          {loggedInUser && loggedInUser.displayName === displayName && (
            <>
              {isEditing ? (
                <button className='comment-button' onClick={handleEditSaveClick}>Save</button>
              ) : (
                <button className='comment-button' onClick={handleEditClick}>Edit</button>
              )}
              <button className='comment-button' onClick={handleDeleteClick}>Delete</button>
            </>
          )}
        </div>
      </div>
    </div>
  );
}

export default Comment;
