import '../../../css/components/appearance.css';
import ThemeOptions from '../../util/ThemeOptions';

function Appearance() {
    return (
        <div className="card">
            <h3>Appearance Settings</h3>
            <p>Change the appearance of the app.</p>
            <ThemeOptions />
        </div>
    );
}

export default Appearance;
