import React, { useEffect, useState } from 'react';
import './VideoItem.css';
import { Link } from 'react-router-dom';

function VideoItem({ _id, title, author, views, img, uploadTime, loggedInUser, handleDeleteVideo, calculateTimeAgo }) {
  const [displayTime, setDisplayTime] = useState('');

  useEffect(() => {
    setDisplayTime(calculateTimeAgo(uploadTime));
    const interval = setInterval(() => {
      setDisplayTime(calculateTimeAgo(uploadTime));
    }, 60000); // Update every minute

    return () => clearInterval(interval); // Cleanup interval on component unmount
  }, [uploadTime, calculateTimeAgo]);

  return (
      <div className="card-for-userpage">
        <Link to={`/videos/${_id}`}><img src={img} className="card-img-top" alt="..."></img></Link>
        <div className="card-body">
          <h5 className="card-title">{title}</h5>
          <p className="card-text">{author}</p>
          <p className="card-text">{views} views - {displayTime}</p>
          {loggedInUser && loggedInUser.displayName === author && (
            <div className="video-actions">
              <button onClick={() => handleDeleteVideo(_id)} className="btn" id="delete-btn"><i className="bi bi-trash3"></i></button>
              <Link to={`/edit-video/${_id}`} className="btn" id="edit-btn"><i className="bi bi-pencil"></i></Link>
            </div>
          )}
        </div>
      </div>
  );
}

export default VideoItem;
