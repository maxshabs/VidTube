const Video = require('../models/video');
const Comment = require('../models/comment');

const createVideo = async (title, description, author, username, img, video, authorImage, uploadTime, views) => {
  try {
      const newVideo = new Video({ title, description, author, username, img, video, authorImage, uploadTime, views });
      return await newVideo.save();
  } catch (error) {
      throw error;
  }
};

const getVideos = async () => {
  try {
    const allVideos = await Video.find({});
    
    // Convert view counts to integers
    allVideos.forEach(video => {
      const viewsStr = video.views;
      let viewsInt;

      if (viewsStr.endsWith('K')) {
        viewsInt = parseFloat(viewsStr.replace('K', '')) * 1000;
      } else if (viewsStr.endsWith('M')) {
        viewsInt = parseFloat(viewsStr.replace('M', '')) * 1000000;
      } else {
        viewsInt = parseFloat(viewsStr.replace(/\D/g, ''));
      }

      video.views = viewsInt;
    });

    // Sort videos by view count in descending order
    const mostViewedVideos = allVideos.sort((a, b) => b.views - a.views).slice(0, 10);

    // Shuffle the remaining videos and pick 10 random ones
    const shuffledVideos = allVideos.sort(() => 0.5 - Math.random());
    const randomVideos = shuffledVideos.filter(video => !mostViewedVideos.includes(video)).slice(0, 10);

    // Combine the two sets of videos
    const selectedVideos = [...mostViewedVideos, ...randomVideos].sort(() => Math.random() - 0.5);

    // Convert the views back to formatted strings
    selectedVideos.forEach(video => {
      if (video.views >= 1000000) {
        video.views = (video.views / 1000000).toFixed(1).replace(/\.0$/, '') + 'M';
      } else if (video.views >= 1000) {
        video.views = (video.views / 1000).toFixed(1).replace(/\.0$/, '') + 'K';
      } else {
        video.views = video.views.toString();
      }
    });

    return selectedVideos;
  } catch (error) {
    throw error;
  }
};


const getAllVideos = async () => { 
  try {
    return await Video.find({});
  } catch (error) {
    throw error;
  }
};

const getVideoById = async (id) => {
  try {
    return await Video.findById(id);
  } catch (error) {
    throw error;
  }
};

const getUserVideoById = async (pid, username) => {
  try {
    return await Video.findOne({ _id: pid, username });
  } catch (error) {
    throw error;
  }
};

const getVideosByAuthor = async (id) => {
  try {
    return await Video.find({ username: id });
  } catch (error) {
    throw error;
  }
}

const getVideosByIds = async (ids) => {
  try {
    const videos = [];
    for (const id of ids) {
      const video = await getVideoById(id);
      if (video) {
        videos.push(video);
      }
    }
    return videos;
  } catch (error) {
    throw error;
  }
};


const updateVideo = async (id, title, description, img, video) => {
  try {
      const cur_video = await getVideoById(id);
      if (!cur_video) {
          return null;
      }
      cur_video.title = title;
      cur_video.description = description;
      if (img) cur_video.img = img;
      if (video) cur_video.video = video;
      return await cur_video.save();
  } catch (error) {
      throw error;
  }
};

const deleteVideo = async (id) => {
  try {
    const video = await getVideoById(id);
    if (!video) {
      return null;
    }
    await video.deleteOne();
    await Comment.deleteMany({ videoId: id });
    return video;
  } catch (error) {
    throw error;
  }
}


// likes on Videos
async function likeVideo(videoId, userDisplayName) {
  try {
    const video = await Video.findById(videoId);
    if (!video) throw new Error('Video not found');

    // Check if user is already in likes array
    if (video.likes.includes(userDisplayName)) {
      // Remove user from likes array
      video.likes = video.likes.filter(name => name !== userDisplayName);
    } else {
      // Add user to likes array
      video.likes.push(userDisplayName);
    }

    // Ensure user is removed from dislikes array
    video.dislikes = video.dislikes.filter(name => name !== userDisplayName);

    await video.save();
    return video;
  } catch (error) {
    throw error;
  }
}


async function dislikeVideo(videoId, userDisplayName) {
  try {
    const video = await Video.findById(videoId);
    if (!video) throw new Error('Video not found');

    // Check if user is already in dislikes array
    if (video.dislikes.includes(userDisplayName)) {
      // Remove user from dislikes array
      video.dislikes = video.dislikes.filter(name => name !== userDisplayName);
    } else {
      // Add user to dislikes array
      video.dislikes.push(userDisplayName);
    }

    // Ensure user is removed from likes array
    video.likes = video.likes.filter(name => name !== userDisplayName);

    await video.save();
    return video;
  } catch (error) {
    throw error;
  }
}

module.exports = { createVideo, getVideos, getAllVideos, getVideoById, updateVideo, deleteVideo , getVideosByAuthor, likeVideo, dislikeVideo, getUserVideoById, getVideosByIds };
