import './MainPage.css';
import '../theme.css';
import LeftMenu from '../leftMenu/LeftMenu';
import VideoListResults from '../videoListResults/VideoListResults';
import CategoryButton from '../categoryButton/CategoryButton';

function MainPage({ videos, handleDeleteVideo, loggedInUser, calculateTimeAgo }) {

    return (
        <div className="container-fluid">
            <div className="row">
                    <div className="col-1 menu">
                        <LeftMenu />
                    </div>
                    <div className="col-11 main-content">
                        <div className="categories row d-flex justify-content-center">
                            <row>
                                <CategoryButton category="All" />
                                <CategoryButton category="Music" />
                                <CategoryButton category="Sports" />
                                <CategoryButton category="Gaming" />
                                <CategoryButton category="News" />
                                <CategoryButton category="Movies" />
                                <CategoryButton category="Fashion" />
                                <CategoryButton category="Learning" />
                                <CategoryButton category="Live" />
                            </row>
                        </div>
                        <VideoListResults videos={videos} handleDeleteVideo={handleDeleteVideo} loggedInUser={loggedInUser} calculateTimeAgo={calculateTimeAgo} />
                    </div>
            </div>
        </div>
    );
}

export default MainPage;
