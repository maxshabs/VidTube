import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useParams } from 'react-router-dom';
import styles from './ProfilePage.module.css';
import SideVideo from '../video_screen/SideVideo/SideVideo';

const UserVideoPage = ({ videos, calculateTimeAgo }) => {
    const { clickedDisplayName } = useParams();

    // filter videos by user display name
  const userVideos = videos.filter((video) => video.author === clickedDisplayName);

  const userVideoList = userVideos.map((video, key) => (
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
    <div className={styles.profileContainer}>
        <h1 className={styles.sectionTitle}>{clickedDisplayName}'s Videos:</h1>
        <div>{userVideoList}</div>
    </div>
  );

}

export default UserVideoPage;