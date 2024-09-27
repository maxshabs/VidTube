import './ThemeSwitchButton.css';

function ThemeSwitchButton({ toggleTheme, theme }) {
    return (
        <button className="theme-switch-button" onClick={toggleTheme}>
            {theme === 'light' ? <i class="bi bi-moon"></i> : <i class="bi bi-brightness-high"></i>}
        </button>
    );
}

export default ThemeSwitchButton;