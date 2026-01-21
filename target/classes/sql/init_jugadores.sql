-- Tabla de Usuarios
CREATE TABLE IF NOT EXISTS usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL, -- En producción, usar hash!
    rol VARCHAR(20) NOT NULL,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Usuario de prueba inicial
INSERT INTO usuarios (nombre, password, rol) VALUES ('admin', 'admin123', 'ADMIN');

-- Tabla de Jugadores
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
('Nikola Jokic', 15, 'Denver Nuggets', 'PIVOT', 1, 2.11, 129.0, 'https://cdn.nba.com/headshots/nba/latest/1040x760/203999.png'),
('Kevin Durant', 35, 'Phoenix Suns', 'ALERO', 2, 2.11, 109.0, 'https://cdn.nba.com/headshots/nba/latest/1040x760/201142.png'),
('Joel Embiid', 21, 'Philadelphia 76ers', 'PIVOT', 0, 2.13, 127.0, 'https://cdn.nba.com/headshots/nba/latest/1040x760/203954.png'),
('Jayson Tatum', 0, 'Boston Celtics', 'ALERO', 0, 2.03, 95.0, 'https://cdn.nba.com/headshots/nba/latest/1040x760/1628369.png'),
('Jimmy Butler', 22, 'Miami Heat', 'ALERO', 0, 2.01, 104.0, 'https://cdn.nba.com/headshots/nba/latest/1040x760/202710.png'),
('Devin Booker', 1, 'Phoenix Suns', 'ESCOLTA', 0, 1.96, 93.0, 'https://cdn.nba.com/headshots/nba/latest/1040x760/1626164.png'),
('Anthony Davis', 3, 'Los Angeles Lakers', 'ALA_PIVOT', 1, 2.08, 115.0, 'https://cdn.nba.com/headshots/nba/latest/1040x760/203076.png'),
('Kawhi Leonard', 2, 'Los Angeles Clippers', 'ALERO', 2, 2.01, 102.0, 'https://cdn.nba.com/headshots/nba/latest/1040x760/202695.png'),
('Damian Lillard', 0, 'Milwaukee Bucks', 'BASE', 0, 1.88, 88.0, 'https://cdn.nba.com/headshots/nba/latest/1040x760/203081.png'),
('Shai Gilgeous-Alexander', 2, 'Oklahoma City Thunder', 'BASE', 0, 1.98, 88.0, 'https://cdn.nba.com/headshots/nba/latest/1040x760/1628983.png'),
('Victor Wembanyama', 1, 'San Antonio Spurs', 'PIVOT', 0, 2.24, 95.0, 'https://cdn.nba.com/headshots/nba/latest/1040x760/1641705.png');

-- Tabla para gestionar los quintetos ideales (Relación N:M entre Usuarios y Jugadores)
CREATE TABLE IF NOT EXISTS quintetos (
    usuario_id INT,
    jugador_id INT,
    fecha_seleccion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (usuario_id, jugador_id),
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    FOREIGN KEY (jugador_id) REFERENCES jugadores(id) ON DELETE CASCADE
);