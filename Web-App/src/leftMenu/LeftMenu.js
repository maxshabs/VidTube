import './LeftMenu.css';
import Logo from '../logo.png';
import { Link } from 'react-router-dom';

function LeftMenu() {
    return (
        <div className="menu">
            <button className="menu-btn" data-bs-toggle="offcanvas" href="#offcanvasMenu" role="button" aria-controls="offcanvasMenu">
                <i className="bi bi-list"></i>
            </button>
            <ul className="nav flex-column menu-list">
                <li className="menu-item">
                    <Link to="/main" className="menu-link">
                        <i className="bi bi-house-door-fill"></i>
                    </Link>
                </li>
                <li className="menu-item">
                    <Link to="/main" className="menu-link">
                        <i className="bi bi-arrow-up-right-square-fill"></i>
                        </Link>
                </li>
                <li className="menu-item">
                    <Link to="/main" className="menu-link">
                        <i className="bi bi-collection-play-fill"></i>
                        </Link>
                </li>
                <li className="menu-item">
                    <Link to="/main" className="menu-link">
                        <i className="bi bi-person-fill"></i>
                        </Link>
                </li>
            </ul>
            <div className="offcanvas offcanvas-start" tabIndex="-1" id="offcanvasMenu" aria-labelledby="offcanvasMenuLabel" style={{ width: '200px' }}>
                <div className="offcanvas-header">
                    <h5 className="offcanvas-title" id="offcanvasMenuLabel">
                        <img src={Logo} alt="LogoImg" className="logo-image" />
                    </h5>
                    <button type="button" className="close-btn" data-bs-dismiss="offcanvas" aria-label="Close"><i class="bi bi-x-lg"></i></button>
                </div>
                <div className="offcanvas-body">
                    <ul className="list-group list-group-flush offcanvas-list" id="offcanvasMenu">
                        <li className="list-group-item">
                            <ul className="nav flex-column">
                                <li className="nav-item">
                                    <Link to="/main" className="nav-link ps-0 pe-0">
                                        <i className="bi bi-house-door"></i>
                                        <span>Home</span>
                                    </Link>
                                </li>
                                <li className="nav-item">
                                    <Link to="/main" className="nav-link ps-0 pe-0">
                                        <i className="bi bi-arrow-up-right-square"></i>
                                        <span>Shorts</span>
                                    </Link>
                                </li>
                                <li className="nav-item">
                                    <Link to="/main" className="nav-link ps-0 pe-0">
                                        <i className="bi bi-collection-play"></i>
                                        <span>Subscriptions</span>
                                    </Link>
                                </li>
                                <li className="nav-item">
                                    <Link to="/main" className="nav-link ps-0 pe-0">
                                        <i className="bi bi-person"></i>
                                        <span>You</span>
                                    </Link>
                                </li>
                                <li className="nav-item">
                                    <Link to="/main" className="nav-link ps-0 pe-0">
                                        <i className="bi bi-clock-history"></i>
                                        <span>History</span>
                                    </Link>
                                </li>
                            </ul>
                        </li>
                    </ul>
                    <hr />
                    <ul className="list-group list-group-flush offcanvas-list" id="offcanvasMenu">
                        <li className="list-group-item">
                            <ul className="nav flex-column">
                                <li className="nav-item">
                                    <Link to="/main" className="nav-link ps-0 pe-0">
                                        <i className="bi bi-fire"></i>
                                        <span>Trending</span>
                                    </Link>
                                </li>
                                <li className="nav-item">
                                    <Link to="/main" className="nav-link ps-0 pe-0">
                                        <i className="bi bi-music-note"></i>
                                        <span>Music</span>
                                    </Link>
                                </li>
                                <li className="nav-item">
                                    <Link to="/main" className="nav-link ps-0 pe-0">
                                        <i className="bi bi-controller"></i>
                                        <span>Gaming</span>
                                    </Link>
                                </li>
                                <li className="nav-item">
                                    <Link to="/main" className="nav-link ps-0 pe-0">
                                        <i className="bi bi-newspaper"></i>
                                        <span>News</span>
                                    </Link>
                                </li>
                                <li className="nav-item">
                                    <Link to="/main" className="nav-link ps-0 pe-0">
                                        <i className="bi bi-trophy"></i>
                                        <span>Sports</span>
                                    </Link>
                                </li>
                                <li className="nav-item">
                                    <Link to="/main" className="nav-link ps-0 pe-0">
                                        <i className="bi bi-mic"></i>
                                        <span>Podcasts</span>
                                    </Link>
                                </li>
                            </ul>
                        </li>
                    </ul>
                    <hr />
                    <ul className="list-group list-group-flush offcanvas-list" id="offcanvasMenu">
                        <li className="list-group-item">
                            <ul className="nav flex-column">
                                <li className="nav-item">
                                    <Link to="/main" className="nav-link ps-0 pe-0">
                                        <i class="bi bi-gear"></i>
                                        <span>Settings</span>
                                    </Link>
                                </li>
                                <li className="nav-item">
                                    <Link to="/main" className="nav-link ps-0 pe-0">
                                        <i class="bi bi-question-circle"></i>
                                        <span>Help</span>
                                    </Link>
                                </li>
                                <li className="nav-item">
                                    <Link to="/main" className="nav-link ps-0 pe-0">
                                        <i class="bi bi-chat-dots"></i>
                                        <span>Feedback</span>
                                    </Link>
                                </li>
                            </ul>
                        </li>
                    </ul>
                </div>
            </div>
        </div>
    );
}

export default LeftMenu;
