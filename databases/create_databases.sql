DROP DATABASE IF EXISTS timesheets_dev;
DROP DATABASE IF EXISTS timesheets_test;
DROP DATABASE IF EXISTS registration_dev;
DROP DATABASE IF EXISTS registration_test;

CREATE DATABASE timesheets_dev;
CREATE DATABASE timesheets_test;
CREATE DATABASE registration_dev;
CREATE DATABASE registration_test;

CREATE USER IF NOT EXISTS 'tracker'@'localhost'
    IDENTIFIED BY '';
GRANT ALL PRIVILEGES ON timesheets_dev.* TO 'tracker' @'localhost';
GRANT ALL PRIVILEGES ON timesheets_test.* TO 'tracker' @'localhost';
GRANT ALL PRIVILEGES ON registration_dev.* TO 'tracker' @'localhost';
GRANT ALL PRIVILEGES ON registration_test.* TO 'tracker' @'localhost';
