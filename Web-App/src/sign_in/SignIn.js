import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import styles from './SignIn.module.css';
import Logo from '../logo.png';

const SignIn = ({ validateUser }) => {
  const navigate = useNavigate();
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [errorMessage, setErrorMessage] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const data = await validateUser(username, password);
      if (data) {
        navigate('/main');
      } else {
        setErrorMessage('Invalid username or password');
      }
    } catch (error) {
      setErrorMessage('Invalid username or password');
    }
  };

  return (
    <div className={styles.container}>
      <div className={styles.formWrapper}>
        <form className={styles.form} onSubmit={handleSubmit}>
          <Link to="/main">
            <img src={Logo} alt="Home" className={styles.logo} />
          </Link>
          <div className={styles.inputContainer}>
            <label className={styles.inputLabel} htmlFor="username">Username:</label>
            <input type="text" id="username" name="username" className={styles.input} placeholder="example123" value={username} onChange={(e) => setUsername(e.target.value)} required />
          </div>
          <div className={styles.inputContainer}>
            <label className={styles.inputLabel} htmlFor="password">Password:</label>
            <input type="password" id="password" name="password" className={styles.input} placeholder="********" value={password} onChange={(e) => setPassword(e.target.value)} required />
          </div>
          {errorMessage && <div className={styles.errorMessage}>{errorMessage}</div>}
          <button type="submit" className={styles.button}>Sign In</button>
          <Link to="/sign-up" className={styles.link}>Don't have an account? Sign up here.</Link>
        </form>
      </div>
    </div>
  );
};

export default SignIn;
