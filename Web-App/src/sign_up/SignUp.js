import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import styles from './SignUp.module.css';
import Logo from '../logo.png';

const SignUp = ({ addUser }) => {
  const navigate = useNavigate();
  const [username, setUsername] = useState('');
  const [displayName, setDisplayName] = useState('');
  const [profilePicture, setProfilePicture] = useState('');
  const [password, setPassword] = useState('');
  const [verifyPassword, setVerifyPassword] = useState('');
  const [errorMessage, setErrorMessage] = useState('');

  const strongPasswordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*])[A-Za-z0-9!@#$%^&*]{8,}$/;

  const validatePassword = (e) => {
    const newPassword = e.target.value;
    setPassword(newPassword);

    if (!strongPasswordRegex.test(newPassword)) {
      setErrorMessage('Password must be at least 8 characters long and contain at least one capital letter, one small letter, one number, and one special character.');
    } else if (verifyPassword !== '' && verifyPassword !== newPassword) {
      setErrorMessage('Passwords do not match.');
    } else {
      setErrorMessage('');
    }
  };

  const validateVerifyPassword = (e) => {
    const newVerifyPassword = e.target.value;
    setVerifyPassword(newVerifyPassword);

    if (newVerifyPassword !== password) {
      setErrorMessage('Passwords do not match.');
    } else if (!strongPasswordRegex.test(password)) {
      setErrorMessage('Password must be at least 8 characters long and contain at least one capital letter, one small letter, one number, and one special character.');
    } else {
      setErrorMessage('');
    }
  };

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
    if (errorMessage === '') {
      try {
        const error = await addUser({
          username,
          password,
          displayName,
          profilePicture
        });
        if (!error) {
          navigate('/sign-in');
        } else {
          setErrorMessage(error);
        }
      } catch (error) {
        setErrorMessage(error.message);
      }
    }
  };

  const handleInputChange = () => {
    setErrorMessage('');
  };

  return (
    <div className={styles.container}>
      <Link to="/main">
        <img src={Logo} alt="Home" className={styles.logo} />
      </Link>
      <div className={styles.formWrapper}>
        <form className={styles.form} onSubmit={handleSubmit}>
          <div className={styles.inputContainer}>
            <label className={styles.inputLabel} htmlFor="username">Username:</label>
            <input type="text" id="username" name="username" className={styles.input} placeholder="example123" value={username} onChange={(e) => {setUsername(e.target.value); handleInputChange();}} required />
          </div>
          <div className={styles.inputContainer}>
            <label className={styles.inputLabel} htmlFor="password">Password:</label>
            <input type="password" id="password" name="password" className={styles.input} placeholder="********" value={password} onChange={validatePassword} required />
          </div>
          <div className={styles.inputContainer}>
            <label className={styles.inputLabel} htmlFor="verifyPassword">Verify Password:</label>
            <input type="password" id="verifyPassword" name="verifyPassword" className={styles.input} placeholder="********" value={verifyPassword} onChange={validateVerifyPassword} required />
          </div>
          <div className={styles.inputContainer}>
            <label className={styles.inputLabel} htmlFor="displayName">Display Name:</label>
            <input type="text" id="displayName" name="displayName" className={styles.input} placeholder="displayName123" value={displayName} onChange={(e) => {setDisplayName(e.target.value); handleInputChange();}} required />
          </div>
          <div className={styles.inputContainer}>
            <label className={styles.inputLabel} htmlFor="profilePicture">Upload Profile Picture:</label>
            <input type="file" id="profilePicture" name="profilePicture" className={styles.input} onChange={handleProfilePicture} required />
          </div>
          {errorMessage && <div className={styles.errorMessage}>{errorMessage}</div>}
          <button type="submit" className={styles.button}>Sign Up</button>
          <Link to="/sign-in" className={styles.link}>Already have an account? Log in</Link>
        </form>
      </div>
    </div>
  );
};

export default SignUp;
