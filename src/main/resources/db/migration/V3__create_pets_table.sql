CREATE TABLE pets (
    id NUMBER(19) GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR2(30) NOT NULL,
    age_category VARCHAR2(50) NOT NULL,
    type_id NUMBER(19) NOT NULL,
    user_id NUMBER(19) NOT NULL,
    last_seen_location VARCHAR2(100),
    last_seen_date TIMESTAMP NOT NULL,
    color VARCHAR2(30),
    description VARCHAR2(255),
    status VARCHAR2(50) NOT NULL,
    CONSTRAINT fk_pets_pet_type FOREIGN KEY (type_id) REFERENCES pet_types(id)
);