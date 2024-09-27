import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useParams } from 'react-router-dom';
import styles from './ProfilePage.module.css';
import SideVideo from '../video_screen/SideVideo/SideVideo';
import VideoItemUserPage from "../videoItem/VideoItemUserPage";


const ProfilePage = ({ loggedInUser, fetchUser, handleDeleteVideo, updateUser, deleteUser, videos, calculateTimeAgo }) => {
  const { clickedDisplayName } = useParams();

  const navigate = useNavigate();
  const [user, setUser] = useState(loggedInUser);
  const [username, setUsername] = useState(loggedInUser.username);
  const [displayName, setDisplayName] = useState(loggedInUser.displayName);
  const [profilePicture, setProfilePicture] = useState(loggedInUser.profilePicture);
  const [password, setPassword] = useState('');
  const [errorMessage, setErrorMessage] = useState('');

  const strongPasswordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*])[A-Za-z0-9!@#$%^&*]{8,}$/;

  useEffect(() => {
    setUser(loggedInUser);
    setUsername(loggedInUser.username);
    setDisplayName(loggedInUser.displayName);
    setProfilePicture(loggedInUser.profilePicture);
  }, [loggedInUser]);

  const handleProfilePicture = (e) => {
    const file = e.target.files[0];
    const reader = new FileReader();
    reader.onloadend = () => {
      setProfilePicture(reader.result);
    };
    reader.readAsDataURL(file);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    // Validate the password if it's provided
    if (password && !strongPasswordRegex.test(password)) {
      setErrorMessage('Password must be at least 8 characters long and contain at least one capital letter, one small letter, one number, and one special character.');
      return;
    }

    // Create an updatedUser object with only the fields that are not empty
    const updatedUser = {
      username: username || undefined,
      password: password || undefined,
      displayName: displayName || undefined,
      profilePicture: profilePicture || undefined,
    };

    try {
      const error = await updateUser(updatedUser);
      if (!error) {
        navigate('/main');
      } else {
        setErrorMessage(error);
      }
    } catch (error) {
      setErrorMessage('Failed to update account. Please try again later.');
    }
  };

  const handleDelete = async () => {
    try {
      const error = await deleteUser();
      if (!error) {
        navigate('/main');
      } else {
        setErrorMessage(error);
      }
    } catch (error) {
      setErrorMessage('Failed to delete account. Please try again later.');
    }
  };

  const handleInputChange = () => {
    setErrorMessage('');
  };

  // filter videos by user display name
  const userVideos = videos.filter((video) => video.author === loggedInUser.displayName);

  const userVideoList = userVideos.map((video, key) => (
    <VideoItemUserPage
      _id={video._id}
      title={video.title}
      author={video.author}
      views={video.views}
      img={video.img}
      uploadTime={video.uploadTime}
      loggedInUser={loggedInUser}
      handleDeleteVideo={handleDeleteVideo}
      calculateTimeAgo={calculateTimeAgo}
      key={key}
    />
  ));

  return (
    <div className={styles.profileContainer}>
      <div className={styles.leftColumn}>
        <div className={styles.profileHeader}>
          <h2 className={styles.profileDisplayName}>{loggedInUser.displayName}</h2>
        </div>
        <h1 className={styles.sectionTitle}>Change your details</h1>
        <form className={styles.form} onSubmit={handleSubmit}>
          <div className={styles.inputContainer}>
            <label className={styles.inputLabel} htmlFor="username">Username:</label>
            <input type="text" id="username" name="username" className={styles.input} value={username} onChange={(e) => { setUsername(e.target.value); handleInputChange() }} />
          </div>
          <div className={styles.inputContainer}>
            <label className={styles.inputLabel} htmlFor="password">Password:</label>
            <input type="password" id="password" name="password" className={styles.input} placeholder="********" value={password} onChange={(e) => { setPassword(e.target.value); handleInputChange() }} />
          </div>
          <div className={styles.inputContainer}>
            <label className={styles.inputLabel} htmlFor="displayName">Display Name:</label>
            <input type="text" id="displayName" name="displayName" className={styles.input} value={displayName} onChange={(e) => { setDisplayName(e.target.value); handleInputChange() }} />
          </div>
          <div className={styles.inputContainer}>
            <label className={styles.inputLabel} htmlFor="profilePicture">Upload Profile Picture:</label>
            <input type="file" id="profilePicture" name="profilePicture" className={styles.input} onChange={handleProfilePicture} />
          </div>
          {errorMessage && <div className={styles.errorMessage}>{errorMessage}</div>}
          <button type="submit" className={styles.button}>Update Profile</button>
          <button type="button" className={styles.deleteButton} onClick={handleDelete}>Delete Account</button>
        </form>
      </div>
      <div className={styles.rightColumn}>
        <h1 className={styles.sectionTitle}>Your Videos</h1>
        <div id="user-video-list">{userVideoList}</div>
      </div>
    </div>
  );
};

export default ProfilePage;
