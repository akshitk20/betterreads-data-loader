-- Create the keyspace if it doesn't exist
CREATE KEYSPACE IF NOT EXISTS main WITH replication = {'class': 'SimpleStrategy', 'replication_factor': '1'};

-- Use the keyspace
USE my_keyspace;

-- Create the author_by_id table
CREATE TABLE IF NOT EXISTS author_by_id (
    author_id UUID PRIMARY KEY,
    author_name TEXT,
    personal_name TEXT
);

CREATE TABLE IF NOT EXISTS book_by_id (
    book_id TEXT PRIMARY KEY,
    book_name TEXT,
    book_description TEXT,
    published_date DATE,
    cover_ids LIST<TEXT>,
    author_names LIST<TEXT>,
    author_ids LIST<TEXT>
);
