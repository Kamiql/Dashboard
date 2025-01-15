import ThemeOption from "./ThemeOption";

const ThemeOptions = () => {
  return (
    <div className="theme-container">
      <ThemeOption theme="light" icon="fas fa-sun" />
      <ThemeOption theme="dark" icon="fas fa-moon" />
      <ThemeOption theme="cool" icon="fas fa-temperature-low" />
      <ThemeOption theme="dark-purple" icon="fas fa-eye" />
      <ThemeOption theme="light-purple" icon="far fa-eye" />
      <ThemeOption theme="dracula" icon="fas fa-skull" />
    </div>
  );
};

export default ThemeOptions;
