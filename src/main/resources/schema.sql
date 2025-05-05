CREATE TABLE IF NOT EXISTS posts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255),
    content TEXT,
    author VARCHAR(100),
    password VARCHAR(100)  -- 취약점: 평문으로 저장되는 비밀번호
); 