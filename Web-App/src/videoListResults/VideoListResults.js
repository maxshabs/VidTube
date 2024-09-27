import VideoItem from "../videoItem/VideoItem";

function VideoListResults({ videos, handleDeleteVideo, loggedInUser, calculateTimeAgo }) {

    const videoList = videos.map((video, key) => {
        return <VideoItem {...video} key={key} handleDeleteVideo={handleDeleteVideo} loggedInUser={loggedInUser} videos={videos} calculateTimeAgo={calculateTimeAgo} />
    });

    return (
        <div className="row gx-3">
            {videoList}
        </div>
    );
}

export default VideoListResults;
