SET @sql = (SELECT IF(
    (SELECT COUNT(*)
        FROM INFORMATION_SCHEMA.COLUMNS WHERE
          table_schema=DATABASE() 
          AND table_name='CourseClass' 
          AND column_name='enrollmentExpiryDays'
    ) > 0,
    "SELECT 0",
    "alter table CourseClass add enrollmentExpiryDays smallint unsigned not null default 0;"
));
PREPARE stmt FROM @sql;
EXECUTE stmt;



SET @sql = (SELECT
  IF(
    (SELECT COUNT(*)
        FROM INFORMATION_SCHEMA.COLUMNS WHERE
          table_schema=DATABASE()
          AND table_name = 'Enrollment'
          AND column_name = 'end_date'
    ) > 0,
    "ALTER TABLE Enrollment CHANGE end_date endDate datetime;",
    "SELECT 0"
  )
);
PREPARE stmt FROM @sql;
EXECUTE stmt;



SET @sql = (SELECT
  IF(
    (SELECT COUNT(*)
        FROM INFORMATION_SCHEMA.COLUMNS WHERE
          table_schema=DATABASE()
          AND table_name = 'Enrollment'
          AND column_name = 'start_date'
    ) > 0,
    "ALTER TABLE Enrollment CHANGE start_date startDate datetime;",
    "SELECT 0"
  )
);
PREPARE stmt FROM @sql;
EXECUTE stmt;