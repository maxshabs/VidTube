import React from 'react';
import { Link } from 'react-router-dom';
import styles from './LoggedInHeader.module.css';
import Search from '../search/Search';
import ThemeSwitchButton from '../themeSwitchButton/ThemeSwitchButton';
import Logo from '../logo.png';

const LoggedInHeader = ({ loggedInUser, doSearch, toggleTheme, theme, signOutUser }) => {
  const onSignOut = () => {
    signOutUser();
  };

  return (
    <header className={styles.loggedInHeader}>
      <Link to="/main" className={styles.logoContainer}>
        <img src={Logo} alt="Home" className={styles.logo} />
      </Link>
      <div className={styles.searchContainer}>
        <Search doSearch={doSearch} />
      </div>
      <div className={styles.buttonsContainer}>
        {loggedInUser ? 
        <> 
          <Link to={"/upload-video"} className={styles.button}><i className="bi bi-plus-circle"></i></Link>
          <ThemeSwitchButton toggleTheme={toggleTheme} theme={theme} className={styles.button}/>
          <Link to={`/profile`} className={styles.profileLink}>
            <img src={loggedInUser.profilePicture} alt="Profile" className={styles.profilePicture} />
            <span>Hello, {loggedInUser.displayName}!</span>
          </Link>
          <Link to="/main" className={styles.button} onClick={onSignOut}><i className="bi bi-box-arrow-right"></i> Sign Out </Link>
        </> :
        <>
          <ThemeSwitchButton toggleTheme={toggleTheme} theme={theme} className={styles.button}/>
          <Link to={"/sign-in"} className={styles.button}><i className="bi bi-person m-1"></i> Sign in </Link>
          <Link to={"/sign-up"} className={styles.button}><i className="bi bi-person-plus"></i> Sign Up </Link>
        </>}
      </div>
    </header>
  );
};

export default LoggedInHeader;
