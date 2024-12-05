import Header from '../components/Header';
import '../App.css';
import Footer from '../components/Footer';
import './Home.css';

export default function Home(){
    return (
        <div>
            <Header />
            <hr className="divider" />
            <div className="container-home">
                <h1 className="welcome-text">Witaj w ViBank!</h1>
            </div>
            <hr className="divider" />
            <Footer />
        </div>
    )
}
