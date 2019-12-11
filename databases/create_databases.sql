DROP DATABASE IF EXISTS timesheets_dev;
DROP DATABASE IF EXISTS timesheets_test;

CREATE DATABASE timesheets_dev;
CREATE DATABASE timesheets_test;

CREATE USER IF NOT EXISTS 'tracker'@'localhost'
    IDENTIFIED BY '';
GRANT ALL PRIVILEGES ON timesheets_dev.* TO 'tracker' @'localhost';
GRANT ALL PRIVILEGES ON timesheets_test.* TO 'tracker' @'localhost';
