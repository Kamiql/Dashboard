
// @ts-ignore
const ThemeOption = ({ theme, icon }) => {
  const setTheme = () => {
    document.querySelector("body")?.setAttribute("data-theme", theme);
    localStorage.setItem("theme", theme);
  };

  return (
    <div onClick={setTheme} className={`theme-option ${theme}`} id={`theme-${theme}`}>
        <i className={icon} />
        <h4>{theme.charAt(0).toUpperCase() + theme.slice(1)}</h4>
    </div>
  );
};

export default ThemeOption;
