CREATE TABLE token (
  id SERIAL PRIMARY KEY,
  token VARCHAR(255) NOT NULL,
  token_type VARCHAR(10) NOT NULL,
  expired BOOLEAN,
  revoked BOOLEAN,
  user_id INTEGER NOT NULL,

  FOREIGN KEY (user_id) REFERENCES users (id)
);