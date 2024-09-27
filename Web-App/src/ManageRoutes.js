import React, { useState, useEffect } from 'react';
import { Route, Routes, Navigate } from 'react-router-dom';
import SignUp from './sign_up/SignUp';
import SignIn from './sign_in/SignIn';
import UploadVideo from './upload_video/UploadVideo';
import EditVideo from './edit_video/EditVideo';
import MainPage from './main_page/MainPage';
import LoggedInHeader from './logged_in_header/LoggedInHeader';
import videos from './data/videos.json';
import VideoScreen from './video_screen/VideoScreen';
import ProfilePage from './profile_page/ProfilePage';
import Logo from './favicon.png';
import UserVideoPage from './profile_page/UserVideoPage';

const ManageRoutes = () => {
  const [users, setUsers] = useState([]);
  const [loggedInUser, setLoggedInUser] = useState(null);
  const [allVideos, setAllVideos] = useState([]);
  const [videoList, setVideoList] = useState([]);
  const [theme, setTheme] = useState('light');
  const [comments, setComments] = useState([]);
  const [displayTimes, setDisplayTimes] = useState({});

  useEffect(() => {
    const fetchAllVideos = async () => {
      try {
        const response = await fetch('http://localhost:12345/api/allvideos', {
          method: 'GET',
          headers: {
            'Content-Type': 'application/json',
          },
        });
        const data = await response.json();
        setAllVideos(data);
      } catch (error) {
        console.error('Error fetching videos:', error);
      }
    };

    fetchAllVideos();
  }, []);

  useEffect(() => {
    const fetchVideos = async () => {
      try {
        const response = await fetch('http://localhost:12345/api/videos', {
          method: 'GET',
          headers: {
            'Content-Type': 'application/json',
          },
        });
        const data = await response.json();
        setVideoList(data);
      } catch (error) {
        console.error('Error fetching videos:', error);
      }
    };

    fetchVideos();
  }, []);

  const addUser = async (newUser) => {
    try {
      const response = await fetch('http://localhost:12345/api/users', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(newUser),
      });

      if (response.ok) {
        const user = await response.json();
        setUsers((prevUsers) => [...prevUsers, user]);
        return null; // No error
      } else {
        const errorData = await response.json();
        return errorData.errors ? errorData.errors.join(', ') : 'Failed to add user';
      }
    } catch (error) {
      console.error('Error adding user:', error);
      return 'Failed to add user';
    }
  };

  const validateUser = async (username, password) => {
    try {
      const response = await fetch('http://localhost:12345/api/tokens', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ username, password }),
      });

      if (response.ok) {
        const data = await response.json();
        localStorage.setItem('jwtToken', data.token); // Store the token

        // Fetch the most recent user information
        const user = await fetchUser(username);
        setLoggedInUser(user); // Set the full user object in state

        return data;
      } else {
        const errorData = await response.json();
        throw new Error(errorData.errors ? errorData.errors.join(', ') : 'Invalid username or password');
      }
    } catch (error) {
      console.error('Error validating user:', error);
      return null;
    }
  };

  const signOutUser = () => {
    setLoggedInUser(null);
    localStorage.removeItem('jwtToken'); // Remove the token on sign out
  };

  const fetchUser = async (username) => {
    try {
      const token = localStorage.getItem('jwtToken');
      if (!token) {
        console.error('No token found');
        throw new Error('No token found');
      }

      console.log(`Fetching user with token: ${token}`);
      const response = await fetch(`http://localhost:12345/api/users/${username}`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`,
        },
      });

      if (response.ok) {
        const user = await response.json();
        return user;
      } else {
        const errorData = await response.json();
        console.error('Error response from server:', errorData);
        throw new Error(errorData.errors ? errorData.errors.join(', ') : 'Failed to fetch user');
      }
    } catch (error) {
      console.error('Error fetching user:', error);
      throw error;
    }
  };

  const updateUser = async (updatedUser) => {
    try {
      const token = localStorage.getItem('jwtToken');
      const response = await fetch(`http://localhost:12345/api/users/${loggedInUser.username}`, {
        method: 'PATCH',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`,
        },
        body: JSON.stringify(updatedUser),
      });

      if (response.ok) {
        const user = await response.json();
        setLoggedInUser(user); // Update the logged-in user state
        const fetchUpdatedVideos = async () => {
          const response = await fetch('http://localhost:12345/api/videos', {
            method: 'GET',
            headers: {
              'Content-Type': 'application/json',
            },
          });
          const data = await response.json();
          setAllVideos(data);
          setVideoList(data);
        };
    
        fetchUpdatedVideos();

        const fetchComments = async () => {
          try {
            const response = await fetch('http://localhost:12345/api/comments');
            if (!response.ok) {
              throw new Error('Failed to fetch comments');
            }
            const data = await response.json();
            setComments(data); // Assuming data is an array of comments
          } catch (error) {
            console.error('Error fetching comments:', error);
            // Handle error state or retry logic
          }
        };

        fetchComments();
        return null;
      } else {
        const errorData = await response.json();
        return errorData.errors ? errorData.errors.join(', ') : 'Failed to update user';
      }
    } catch (error) {
      console.error('Error updating user:', error);
      return 'Failed to update user';
    }
  };

  const deleteUser = async () => {
    try {
      const token = localStorage.getItem('jwtToken');
      const response = await fetch(`http://localhost:12345/api/users/${loggedInUser.username}`, {
        method: 'DELETE',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`,
        },
      });

      if (response.ok) {
        setLoggedInUser(null);
        const fetchUpdatedVideos = async () => {
          const response = await fetch('http://localhost:12345/api/videos', {
            method: 'GET',
            headers: {
              'Content-Type': 'application/json',
            },
          });
          const data = await response.json();
          setAllVideos(data);
          setVideoList(data);
        };
    
        fetchUpdatedVideos();

        const fetchComments = async () => {
          try {
            const response = await fetch('http://localhost:12345/api/comments');
            if (!response.ok) {
              throw new Error('Failed to fetch comments');
            }
            const data = await response.json();
            setComments(data); // Assuming data is an array of comments
          } catch (error) {
            console.error('Error fetching comments:', error);
            // Handle error state or retry logic
          }
        };

        fetchComments();
        localStorage.removeItem('jwtToken');
        return null;
      } else {
        const errorData = await response.json();
        return errorData.errors ? errorData.errors.join(', ') : 'Failed to delete user';
      }

    } catch (error) {
      console.error('Error deleting user:', error);
      return 'Failed to delete user';
    }
  };

  const handleUploadVideo = async (formData) => {
    try {
      const token = localStorage.getItem('jwtToken');
      const response = await fetch(`http://localhost:12345/api/users/${loggedInUser.username}/videos`, {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`,
        },
        body: formData,
      });

      if (!response.ok) {
        throw new Error('Failed to upload video');
      }

      // Fetch the updated list of videos from the server
      const fetchUpdatedVideos = async () => {
        const response = await fetch('http://localhost:12345/api/videos', {
          method: 'GET',
          headers: {
            'Content-Type': 'application/json',
          },
        });
        const data = await response.json();
        setAllVideos(data);
        setVideoList(data);
      };

      fetchUpdatedVideos();
    } catch (error) {
      console.error('Error uploading video:', error);
    }
  };

const handleEditVideo = async (id, formData) => {
    try {
      const token = localStorage.getItem('jwtToken');
      const response = await fetch(`http://localhost:12345/api/users/${loggedInUser.username}/videos/${id}`, {
        method: 'PATCH',
        headers: {
          'Authorization': `Bearer ${token}`,
        },
        body: formData,
      });

      if (!response.ok) {
        throw new Error('Failed to edit video');
      }

      // Fetch the updated list of videos from the server
      const fetchUpdatedVideos = async () => {
        const response = await fetch('http://localhost:12345/api/videos', {
          method: 'GET',
          headers: {
            'Content-Type': 'application/json',
          },
        });
        const data = await response.json();
        setAllVideos(data);
        setVideoList(data);
      };

      fetchUpdatedVideos();
    } catch (error) {
      console.error('Error editing video:', error);
    }
  };


  const handleDeleteVideo = async (id) => {
    try {
      const token = localStorage.getItem('jwtToken');
      const response = await fetch(`http://localhost:12345/api/users/${loggedInUser.username}/videos/${id}`, {
        method: 'DELETE',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`,
        },
      });

      if (!response.ok) {
        throw new Error('Failed to delete video');
      }

      // Fetch the updated list of videos from the server
      const fetchUpdatedVideos = async () => {
        const response = await fetch('http://localhost:12345/api/videos', {
          method: 'GET',
          headers: {
            'Content-Type': 'application/json',
          },
        });
        const data = await response.json();
        setAllVideos(data);
        setVideoList(data);
      };

      fetchUpdatedVideos();
    } catch (error) {
      console.error('Error deleting video:', error);
    }
  };

  const calculateTimeAgo = (uploadTime) => {
    const now = new Date();
    const uploadDate = new Date(uploadTime);
    const differenceInSeconds = Math.floor((now - uploadDate) / 1000);
    let timeAgo = '';

    if (differenceInSeconds < 60) {
      timeAgo = `${differenceInSeconds} seconds ago`;
    } else if (differenceInSeconds < 3600) {
      timeAgo = `${Math.floor(differenceInSeconds / 60)} minutes ago`;
    } else if (differenceInSeconds < 86400) {
      timeAgo = `${Math.floor(differenceInSeconds / 3600)} hours ago`;
    } else if (differenceInSeconds < 2592000) {
      timeAgo = `${Math.floor(differenceInSeconds / 86400)} days ago`;
    } else if (differenceInSeconds < 31536000) {
      timeAgo = `${Math.floor(differenceInSeconds / 2592000)} months ago`;
    } else {
      timeAgo = `${Math.floor(differenceInSeconds / 31536000)} years ago`;
    }

    return timeAgo;
  };

  useEffect(() => {
    const interval = setInterval(() => {
      const updatedDisplayTimes = {};
      allVideos.forEach((video) => {
        updatedDisplayTimes[video.id] = calculateTimeAgo(video.uploadTime);
      });
      setDisplayTimes(updatedDisplayTimes);
    }, 60000); // Update every minute

    return () => clearInterval(interval); // Cleanup interval on component unmount
  }, [allVideos]);

  useEffect(() => {
    const updatedDisplayTimes = {};
    allVideos.forEach((video) => {
      updatedDisplayTimes[video.id] = calculateTimeAgo(video.uploadTime);
    });
    setDisplayTimes(updatedDisplayTimes);
  }, [allVideos]);

  useEffect(() => {
    document.documentElement.classList.toggle('dark', theme === 'dark');
  }, [theme]);

  const doSearch = (query) => {
    setVideoList(allVideos.filter((video) => video.title.toLowerCase().includes(query.toLowerCase())));
  };

  const toggleTheme = () => {
    setTheme(theme === 'light' ? 'dark' : 'light');
  };

  // Function to fetch all comments from the server
  const fetchComments = async () => {
    try {
      const response = await fetch('/api/comments');
      if (!response.ok) {
        throw new Error('Failed to fetch comments');
      }
      const data = await response.json();
      setComments(data); // Assuming data is an array of comments
    } catch (error) {
      console.error('Error fetching comments:', error);
      // Handle error state or retry logic
    }
  };

  useEffect(() => {
    fetchComments(); // Fetch comments when component mounts
  }, []);

  return (
    <Routes>
      <Route path="/sign-up" element={<SignUp addUser={addUser} />} />
      <Route path="/sign-in" element={<SignIn validateUser={validateUser} />} />
      <Route
        path="/main"
        element={
          <>
            <LoggedInHeader loggedInUser={loggedInUser} doSearch={doSearch} toggleTheme={toggleTheme} theme={theme} signOutUser={signOutUser} />
            <MainPage videos={videoList} handleDeleteVideo={handleDeleteVideo} loggedInUser={loggedInUser} calculateTimeAgo={calculateTimeAgo} displayTimes={displayTimes} />
          </>
        }
      />
      <Route
        path="/upload-video"
        element={
          loggedInUser && (
            <>
              <LoggedInHeader loggedInUser={loggedInUser} doSearch={doSearch} toggleTheme={toggleTheme} theme={theme} signOutUser={signOutUser} />
              <UploadVideo handleUploadVideo={handleUploadVideo} loggedInUser={loggedInUser} videos={allVideos} />
            </>
          )
        }
      />
      <Route
        path="/edit-video/:id"
        element={
          loggedInUser && (
            <>
              <LoggedInHeader loggedInUser={loggedInUser} doSearch={doSearch} toggleTheme={toggleTheme} theme={theme} signOutUser={signOutUser} />
              <EditVideo handleEditVideo={handleEditVideo} videos={allVideos} />
            </>
          )
        }
      />
      <Route
        path="/videos/:id"
        element={
          <>
            <LoggedInHeader loggedInUser={loggedInUser} doSearch={doSearch} toggleTheme={toggleTheme} theme={theme} signOutUser={signOutUser} />
            <VideoScreen loggedInUser={loggedInUser} videos={allVideos} comments={comments} setComments={setComments} calculateTimeAgo={calculateTimeAgo} displayTimes={displayTimes} />
          </>
        }
      />
      <Route
        path="/profile"
        element={
          loggedInUser && (
            <>
              <LoggedInHeader loggedInUser={loggedInUser} doSearch={doSearch} toggleTheme={toggleTheme} theme={theme} signOutUser={signOutUser} />
              <ProfilePage loggedInUser={loggedInUser} fetchUser={fetchUser} handleDeleteVideo={handleDeleteVideo} updateUser={updateUser} deleteUser={deleteUser} videos={allVideos} calculateTimeAgo={calculateTimeAgo} />
            </>
          )
        }
      />
      <Route
        path="/profile/:clickedDisplayName"
        element={
          <>
            <LoggedInHeader loggedInUser={loggedInUser} doSearch={doSearch} toggleTheme={toggleTheme} theme={theme} signOutUser={signOutUser} />
            {/* comments ={comments} passes all comments in database, then in watch video we filter only the comments for current video by videoID */}
            <UserVideoPage loggedInUser={loggedInUser} videos={allVideos} comments={comments} setComments={setComments} calculateTimeAgo={calculateTimeAgo} displayTimes={displayTimes} />
          </>
        }
      />
      <Route path="/" element={<Navigate to="/main" />} />
    </Routes>
  );
};

export default ManageRoutes;
