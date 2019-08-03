-- version 1 of the database UserDictionary words table was
--
-- CREATE TABLE words
-- ( _id INTEGER PRIMARY KEY,
--   word TEXT NOT NULL,
--   frequency INTEGER,
--   following TEXT NOT NULL
-- ); -- end comment (comment lines cannot end with semicolon)

PRAGMA foreign_keys=off;

BEGIN TRANSACTION;

ALTER TABLE words RENAME TO _words_old;

CREATE TABLE words
( _id INTEGER PRIMARY KEY,
  word TEXT NOT NULL UNIQUE,
  frequency INTEGER DEFAULT 1,
  following TEXT NOT NULL DEFAULT ''
);

INSERT OR IGNORE INTO words (word, frequency, following)
  SELECT word, frequency, following
  FROM _words_old;

DROP TABLE IF EXISTS _words_old;

COMMIT;

PRAGMA foreign_keys=on;