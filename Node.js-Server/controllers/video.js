const videoService = require('../services/video');
const { sendMessageToCppServer } = require('../services/cppCommunication');

const createVideo = async (req, res) => {
  const { title, description, author, username, authorImage, uploadTime } = req.body;
  const img = req.files['img'] ? `http://localhost:${process.env.PORT}/uploads/${req.files['img'][0].filename}` : null;
  const video = req.files['video'] ? `http://localhost:${process.env.PORT}/uploads/${req.files['video'][0].filename}` : null;

  try {
      const newVideo = await videoService.createVideo(title, description, author, username, img, video, authorImage, uploadTime);
      res.json(newVideo);
  } catch (error) {
      res.status(500).json({ errors: [error.message] });
  }
};


const getVideos = async (_, res) => {
    res.json(await videoService.getVideos())
};

const getAllVideos = async (_, res) => {
    res.json(await videoService.getAllVideos())
}

const getVideo = async (req, res) => {
    const video = await videoService.getVideoById(req.params.id);
    if (!video) {
        return res.status(404).json({ errors: ['The video could not be found.'] });
    }
    res.json(video);
};

const getVideosByAuthor = async (req, res) => {
    const videos = await videoService.getVideosByAuthor(req.params.id);
    if (!videos) {
        return res.status(404).json({ errors: ['The videos could not be found.'] });
    }
    res.json(videos);
};

const getUserVideoById = async (req, res) => {
    const video = await videoService.getUserVideoById(req.params.pid, req.params.id);
    if (!video) {
      return res.status(404).json({ errors: ['The video could not be found.'] });
    }
    res.json(video);
};

const updateVideo = async (req, res) => {
  const { title, description } = req.body;
  const img = req.files['img'] ? `http://localhost:${process.env.PORT}/uploads/${req.files['img'][0].filename}` : undefined;
  const video = req.files['video'] ? `http://localhost:${process.env.PORT}/uploads/${req.files['video'][0].filename}` : undefined;

  try {
      const updatedVideo = await videoService.updateVideo(req.params.pid, title, description, img, video);
      if (!updatedVideo) {
          return res.status(404).json({ errors: ['The video could not be found.'] });
      }
      res.json(updatedVideo);
  } catch (error) {
      res.status(500).json({ errors: [error.message] });
  }
};
const getRecommendations = async (req, res) => {
    const { id } = req.params;
    const { userId, videoId } = req.body;
  
    try {
        // Prepare the message to send to the C++ server
        const message = `USER:${userId};VIDEO:${videoId};`;
  
        // Fetch all videos from the database (or cache)
        const allVideos = await videoService.getAllVideos();
  
        // Extract only the video IDs
        const videoIds = allVideos.map(video => video._id.toString());
  
        // Send the message and video IDs to the C++ server
        const recommendedVideoIds = await sendMessageToCppServer(message, videoIds);
  
        // Filter out invalid or non-existing video IDs before querying the database
        const validVideoIds = recommendedVideoIds.split(';').filter(id => videoIds.includes(id));
  
        // Convert the list of valid recommended video IDs to actual video objects
        let recommendedVideos = await videoService.getVideosByIds(validVideoIds);
  
        // Convert view counts to integers for comparison
        recommendedVideos.forEach(video => {
            const viewsStr = video.views;
            let viewsInt;
  
            if (viewsStr.endsWith('K')) {
                viewsInt = parseFloat(viewsStr.replace('K', '')) * 1000;
            } else if (viewsStr.endsWith('M')) {
                viewsInt = parseFloat(viewsStr.replace('M', '')) * 1000000;
            } else {
                viewsInt = parseFloat(viewsStr.replace(/\D/g, ''));
            }
  
            video.viewsInt = viewsInt; // Add a temporary property for integer views
        });
  
        // Remove videos with the least views to keep only 6 videos
        while (recommendedVideos.length > 6) {
          let minIndex = 0;
          recommendedVideos.forEach((video, index) => {
              if (video.viewsInt < recommendedVideos[minIndex].viewsInt) {
                  minIndex = index;
              }
          });
          recommendedVideos.splice(minIndex, 1); // Remove the video with the least views
        }
        
        // Clean up the temporary viewsInt property
        recommendedVideos.forEach(video => delete video.viewsInt);
  
        res.json(recommendedVideos);
    } catch (error) {
        console.error('Error fetching recommendations:', error);
        res.status(500).json({ errors: [error.message] });
    }
  };
  


const deleteVideo = async (req, res) => {
    const video = await videoService.deleteVideo(req.params.pid);
    if (!video) {
        return res.status(404).json({ errors: ['The video could not be found.'] })
    }
    res.json(video);
};

// Likes on videos
async function likeVideo(req, res) {
    const { id } = req.params;
    const { userDisplayName } = req.body;
  
    try {
      const updatedVideo = await videoService.likeVideo(id, userDisplayName);
      res.status(200).json(updatedVideo);
    } catch (error) {
      res.status(500).json({ error: error.message });
    }
  }
  
  async function dislikeVideo(req, res) {
    const { id } = req.params;
    const { userDisplayName } = req.body;
  
    try {
      const updatedVideo = await videoService.dislikeVideo(id, userDisplayName);
      res.status(200).json(updatedVideo);
    } catch (error) {
      res.status(500).json({ error: error.message });
    }
  }

module.exports = { createVideo, getVideos, getAllVideos, getVideo, updateVideo, deleteVideo, getVideosByAuthor, likeVideo ,dislikeVideo, getUserVideoById, getRecommendations };
