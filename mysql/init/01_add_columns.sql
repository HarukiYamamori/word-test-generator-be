CREATE TABLE words (
                       id INT AUTO_INCREMENT PRIMARY KEY,
                       word_book VARCHAR(255) NOT NULL,
                       num INT NOT NULL,
                       section VARCHAR(255) NOT NULL,
                       word VARCHAR(255) NOT NULL,
                       meaning TEXT NOT NULL
);