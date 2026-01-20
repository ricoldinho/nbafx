CREATE TABLE IF NOT EXISTS jugadores (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    dorsal INT NOT NULL,
    equipo VARCHAR(100) NOT NULL,
    posicion VARCHAR(50) NOT NULL,
    numero_anillos INT DEFAULT 0,
    altura DOUBLE NOT NULL,
    peso DOUBLE NOT NULL,
    image_url VARCHAR(500)
);

INSERT INTO jugadores (nombre, dorsal, equipo, posicion, numero_anillos, altura, peso, image_url) VALUES
('LeBron James', 23, 'Los Angeles Lakers', 'ALERO', 4, 2.06, 113.0, 'https://cdn.nba.com/headshots/nba/latest/1040x760/2544.png'),
('Stephen Curry', 30, 'Golden State Warriors', 'BASE', 4, 1.88, 84.0, 'https://cdn.nba.com/headshots/nba/latest/1040x760/201939.png'),
('Luka Doncic', 77, 'Dallas Mavericks', 'BASE', 0, 2.01, 104.0, 'https://cdn.nba.com/headshots/nba/latest/1040x760/1629029.png'),
('Giannis Antetokounmpo', 34, 'Milwaukee Bucks', 'ALA_PIVOT', 1, 2.11, 110.0, 'https://cdn.nba.com/headshots/nba/latest/1040x760/203507.png'),
('Nikola Jokic', 15, 'Denver Nuggets', 'PIVOT', 1, 2.11, 129.0, 'https://cdn.nba.com/headshots/nba/latest/1040x760/203999.png');