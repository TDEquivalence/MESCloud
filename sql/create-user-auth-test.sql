CREATE TABLE users (
  id int GENERATED ALWAYS AS IDENTITY,
  first_name VARCHAR(50),
  last_name VARCHAR(50),
  username VARCHAR(50) NOT NULL,
  email VARCHAR(50),
  password VARCHAR(255) NOT NULL,
  role VARCHAR(50),
  user_authorities TEXT[],
  created_at TIMESTAMP,
  updated_at TIMESTAMP,
  is_active BOOLEAN,
  is_not_locked BOOLEAN,

  PRIMARY KEY(id)
);
