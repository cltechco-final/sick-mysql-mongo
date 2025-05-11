CREATE TABLE IF NOT EXISTS posts
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    title         VARCHAR(255) NOT NULL,
    content       TEXT,
    author        VARCHAR(100),
    -- 비밀번호는 해시화된 값을 저장하도록 컬럼명 변경
    password_hash VARCHAR(60)  NOT NULL
) ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4;

-- 제목 검색 성능 개선을 위한 인덱스
CREATE INDEX idx_posts_title ON posts (title);
