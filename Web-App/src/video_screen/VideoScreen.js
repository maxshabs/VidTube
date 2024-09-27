import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import './VideoScreen.css';
import SideVideo from './SideVideo/SideVideo';
import CommentSection from './CommentSection/CommentSection';
import ActionBar from './ActionsBar/ActionBar';
import LeftMenu from '../leftMenu/LeftMenu';

const VideoScreen = ({ loggedInUser, comments, setComments, calculateTimeAgo }) => {
  const { id } = useParams(); // Get the video ID from the URL params

  const [currentVideo, setCurrentVideo] = useState(null);
  const [sideVideos, setSideVideos] = useState([]);
  const [isSubscribed, setIsSubscribed] = useState(false);
  const [displayTime, setDisplayTime] = useState('');

  const updateComments = (videoId, newComments) => {
    const updatedComments = comments.map((commentData) =>
      commentData.videoId === videoId
        ? { ...commentData, comments: newComments }
        : commentData
    );
    setComments(updatedComments); // Update the comments in the parent component
  };

  // Fetch the current video data
  useEffect(() => {
    const fetchVideo = async () => {
      try {
        const response = await fetch(`http://localhost:12345/api/videos/${id}`);
        if (!response.ok) {
          throw new Error('Video not found');
        }
        const data = await response.json();
        setCurrentVideo(data);
      } catch (error) {
        console.error('Error fetching video:', error);
      }
    };

    fetchVideo();
  }, [id]);

  // Fetch recommended videos after the current video is loaded
  useEffect(() => {
    const fetchRecommendations = async () => {
      try {
        if (!currentVideo) return;
        
        if (loggedInUser != null){
          // Send a request to the Node.js server for recommendations
          const response = await fetch(`http://localhost:12345/api/videos/${id}`, {
            method: 'POST',
            headers: {
              'Content-Type': 'application/json'
            },
            body: JSON.stringify({ userId: loggedInUser._id, videoId: id })
          });
  
          if (!response.ok) {
            throw new Error('Failed to fetch recommendations');
          }
  
          const recommendedVideos = await response.json();
          setSideVideos(recommendedVideos); // Update the side videos with recommended videos
        } else {
          // Send a request to the Node.js server for recommendations
          const response = await fetch(`http://localhost:12345/api/videos/${id}`, {
            method: 'POST',
            headers: {
              'Content-Type': 'application/json'
            },
            body: JSON.stringify({ userId: null, videoId: id })
          });
  
          if (!response.ok) {
            throw new Error('Failed to fetch recommendations');
          }
  
          const recommendedVideos = await response.json();
          setSideVideos(recommendedVideos); // Update the side videos with recommended videos
        }
      }
      catch (error) {
        console.error('Error fetching recommendations:', error);
      }
    };

    fetchRecommendations();
  }, [currentVideo, id, loggedInUser?._id]);

  // Update display time for the current video
  useEffect(() => {
    if (currentVideo) {
      setDisplayTime(calculateTimeAgo(currentVideo.uploadTime));
      const interval = setInterval(() => {
        setDisplayTime(calculateTimeAgo(currentVideo.uploadTime));
      }, 60000); // Update every minute

      return () => clearInterval(interval);
    }
  }, [currentVideo, calculateTimeAgo]);

  useEffect(() => {
    window.scrollTo(0, 0); // Scroll to the top whenever the video ID changes
  }, [id]);

  if (!currentVideo) {
    return <div>Loading...</div>;
  }

  const sideVideoList = sideVideos.map((video, key) => (
    <SideVideo
      id={video._id}
      title={video.title}
      author={video.author}
      views={video.views}
      img={video.img}
      date={calculateTimeAgo(video.uploadTime)}
      key={key}
    />
  ));

  return (
    <div className="container-fluid">
      <div className="row">
        <div className="col-1 vh-100">
          <LeftMenu />
        </div>
        <div className="col-7">
          <div>
            <video className="thumbnail" controls autoPlay src={currentVideo.video}></video>
            <div id="title">{currentVideo.title}</div>
            <ActionBar
              className="action-bar"
              userName={currentVideo.author}
              img={currentVideo.authorImage}
              likes={currentVideo.likes}
              dislikes={currentVideo.dislikes}
              isSubscribed={isSubscribed}
              setIsSubscribed={setIsSubscribed}
              videoId={currentVideo._id}
              loggedInUser={loggedInUser}
            />
            <div id="description">
              <div id="stats">{currentVideo.views} views - {displayTime}</div>
              <div>{currentVideo.description}</div>
            </div>
            <CommentSection
              videoId={currentVideo._id}
              initialComments={comments}
              updateComments={updateComments}
              loggedInUser={loggedInUser}
              calculateTimeAgo={calculateTimeAgo}
            />
          </div>
        </div>
        <div className="col-3">
          <div>{sideVideoList}</div>
        </div>
      </div>
    </div>
  );
};

export default VideoScreen;
