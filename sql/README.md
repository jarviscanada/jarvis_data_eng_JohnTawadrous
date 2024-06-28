# Introduction
This project contains SQL queries and related tasks for managing and querying a PostgreSQL database.
The tasks include creating tables, inserting data, and performing various queries to retrieve and manipulate data.

# SQL Queries

###### Table Setup (DDL)
The following SQL Data Definition Language (DDL) statements create the necessary tables for the project.

```sql
-- Create cd.members table
CREATE TABLE cd.members (
  memid INTEGER PRIMARY KEY,
  surname VARCHAR(200),
  firstname VARCHAR(200),
  address VARCHAR(300),
  zipcode INTEGER,
  telephone VARCHAR(20),
  recommendedby INTEGER,
  joindate TIMESTAMP,
  FOREIGN KEY (recommendedby) REFERENCES cd.members(memid)
);

-- Create cd.facilities table
CREATE TABLE cd.facilities (
  facid INTEGER PRIMARY KEY,
  name VARCHAR(200),
  membercost NUMERIC,
  guestcost NUMERIC,
  initialoutlay NUMERIC,
  monthlymaintenance NUMERIC
);

-- Create cd.bookings table
CREATE TABLE cd.bookings (
  bookid INTEGER PRIMARY KEY,
  facid INTEGER,
  memid INTEGER,
  starttime TIMESTAMP,
  slots INTEGER,
  FOREIGN KEY (facid) REFERENCES cd.facilities(facid),
  FOREIGN KEY (memid) REFERENCES cd.members(memid)
);
```
Example to load sample data:
```
psql -U <username> -f clubdata.sql -d postgres -x -q
```


###### Question 1: The club is adding a new facility - a spa. We need to add it into the facilities table. Use the following values: facid: 9, Name: 'Spa', membercost: 20, guestcost: 30, initialoutlay: 100000, monthlymaintenance: 800
```
INSERT INTO cd.facilities (facid, name, membercost, guestcost, initialoutlay, monthlymaintenance)
VALUES (9, 'Spa', 20, 30, 100000, 800);
```

###### Questions 2: Let's try adding the spa to the facilities table again. This time, though, we want to automatically generate the value for the next facid, rather than specifying it as a constant. Use the following values for everything else: Name: 'Spa', membercost: 20, guestcost: 30, initialoutlay: 100000, monthlymaintenance: 800.
```
INSERT INTO cd.facilities (facid, name, membercost, guestcost, initialoutlay, monthlymaintenance)
VALUES ((SELECT MAX(facid) + 1 FROM cd.facilities),'Spa', 20, 30, 100000, 800);
```

###### Question 3: We made a mistake when entering the data for the second tennis court. The initial outlay was 10000 rather than 8000: you need to alter the data to fix the error.
```
UPDATE cd.facilities
SET initialoutlay = 10000
WHERE facid = 1;
```

###### Question 4: We want to alter the price of the second tennis court so that it costs 10% more than the first one. Try to do this without using constant values for the prices, so that we can reuse the statement if we want to.
```
UPDATE cd.facilities AS f2
	SET membercost = (SELECT 1.1 * membercost FROM cd.facilities WHERE facid = 0),
		guestcost = (SELECT 1.1 * guestcost FROM cd.facilities WHERE facid = 0)
WHERE facid = 1;
```

###### Question 5: As part of a clearout of our database, we want to delete all bookings from the cd.bookings table. How can we accomplish this?
```
DELETE FROM cd.bookings;
```

###### Question 6: We want to remove member 37, who has never made a booking, from our database. How can we achieve that?
```
DELETE FROM cd.members
WHERE memid = 37;
```

###### Question 7: How can you produce a list of facilities that charge a fee to members, and that fee is less than 1/50th of the monthly maintenance cost? Return the facid, facility name, member cost, and monthly maintenance of the facilities in question.
```
SELECT facid, name, membercost, monthlymaintenance
FROM cd.facilities
WHERE membercost < monthlymaintenance / 50 AND membercost > 0;
```

###### Question 8: How can you produce a list of all facilities with the word 'Tennis' in their name?
```
SELECT *
FROM cd.facilities
WHERE name LIKE '%Tennis%';
```

###### Question 9: How can you retrieve the details of facilities with ID 1 and 5? Try to do it without using the OR operator.
```
SELECT *
FROM cd.facilities
WHERE facid IN (1, 5);
```

###### Question 10: How can you produce a list of members who joined after the start of September 2012? Return the memid, surname, firstname, and joindate of the members in question.
```
SELECT memid, surname, firstname, joindate
FROM cd.members
WHERE joindate >= '2012-09-01';
```

###### Question 11: You, for some reason, want a combined list of all surnames and all facility names. Yes, this is a contrived example :-). Produce that list!
```
SELECT surname FROM cd.members
UNION
SELECT name FROM cd.facilities;
```

###### Question 12: How can you produce a list of the start times for bookings by members named 'David Farrell'?
```
SELECT b.starttime
FROM cd.bookings AS b
JOIN cd.members AS m ON b.memid = m.memid
WHERE m.firstname = 'David' AND m.surname = 'Farrell';
```

###### Question 13: How can you produce a list of the start times for bookings for tennis courts, for the date '2012-09-21'? Return a list of start time and facility name pairings, ordered by the time.
```
SELECT b.starttime AS start, f.name 
FROM cd.bookings AS b
JOIN cd.facilities AS f ON b.facid = f.facid
WHERE f.name LIKE '%Tennis Court%' AND DATE(b.starttime) = '2012-09-21'
ORDER BY b.starttime;
```

###### Question 14: How can you output a list of all members, including the individual who recommended them (if any)? Ensure that results are ordered by (surname, firstname).
```
SELECT m.firstname AS memfname,
       m.surname AS memsname,
	   r.firstname AS recfname,
       r.surname AS recsname      
FROM cd.members AS m
LEFT OUTER JOIN cd.members AS r ON m.recommendedby = r.memid
ORDER BY memsname, memfname; 
```

###### Question 15: How can you output a list of all members who have recommended another member? Ensure that there are no duplicates in the list, and that results are ordered by (surname, firstname).
```
SELECT DISTINCT m.firstname,
       m.surname   
FROM cd.members AS m
JOIN cd.members AS r ON m.memid = r.recommendedby
ORDER BY m.surname, m.firstname;
```

###### Question 16: How can you output a list of all members, including the individual who recommended them (if any), without using any joins? Ensure that there are no duplicates in the list, and that each firstname + surname pairing is formatted as a column and ordered.
```
SELECT DISTINCT
    CONCAT(m.firstname, ' ', m.surname) AS member,
    (SELECT CONCAT(r.firstname, ' ', r.surname) 
     FROM cd.members r 
     WHERE r.memid = m.recommendedby) AS recommender
FROM
    cd.members m
ORDER BY
    member;
```

###### Question 17: Produce a count of the number of recommendations each member has made. Order by member ID.
```
SELECT recommendedby, COUNT(*) 
	FROM cd.members
	WHERE recommendedby is not null
	GROUP BY  recommendedby
ORDER BY  recommendedby;   
```

###### Question 18: Produce a list of the total number of slots booked per facility. For now, just produce an output table consisting of facility id and slots, sorted by facility id.
```
SELECT
    facid,
    SUM(slots) AS "Total Slots"
FROM
    cd.bookings
GROUP BY
    facid
ORDER BY
    facid;
```

###### Question 19: Produce a list of the total number of slots booked per facility in the month of September 2012. Produce an output table consisting of facility id and slots, sorted by the number of slots.
```
SELECT
    facid,
    SUM(slots) AS "Total Slots"
FROM
    cd.bookings
WHERE
    starttime >= '2012-09-01' AND starttime < '2012-10-01'
GROUP BY
    facid
```

###### Question 20: Produce a list of the total number of slots booked per facility per month in the year of 2012. Produce an output table consisting of facility id and slots, sorted by the id and month.
```
SELECT
    facid,
    EXTRACT(MONTH FROM starttime) AS month,
    SUM(slots) AS total_slots
FROM
    cd.bookings
WHERE
    EXTRACT(YEAR FROM starttime) = 2012
GROUP BY
    facid, month
ORDER BY
    facid, month;
```

###### Question 21: Find the total number of members (including guests) who have made at least one booking.
```
SELECT
    COUNT(DISTINCT memid) AS count
FROM
    cd.bookings;
```

###### Question 22: Produce a list of each member name, id, and their first booking after September 1st 2012. Order by member ID.
```
SELECT
	m.surname,
	m.firstname,
	m.memid,
	MIN(b.starttime)
FROM
	cd.members AS m
JOIN
	cd.bookings AS b ON m.memid = b.memid
WHERE
	b.starttime > '2012-09-01'
GROUP BY
    m.memid, m.firstname, m.surname
ORDER BY
    m.memid;
```

###### Question 23: Produce a list of member names, with each row containing the total member count. Order by join date, and include guest members.
```
SELECT
	COUNT(*) OVER () AS count,
	m.firstname,
	m.surname
FROM
	cd.members AS m
ORDER BY
	m.joindate;
```

###### Question 24: Produce a monotonically increasing numbered list of members (including guests), ordered by their date of joining. Remember that member IDs are not guaranteed to be sequential.
```
SELECT
    ROW_NUMBER() OVER (ORDER BY joindate) AS row_number,
    firstname,
    surname
FROM
    cd.members
ORDER BY
    joindate;
```

###### Question 25: Output the facility id that has the highest number of slots booked. Ensure that in the event of a tie, all tieing results get output.
```
WITH TotalSlots AS (
    SELECT facid, SUM(slots) AS total_slots
    FROM cd.bookings
    GROUP BY facid
)
SELECT facid, total_slots
FROM TotalSlots
WHERE total_slots = (SELECT MAX(total_slots) FROM TotalSlots);
```

###### Question 26: Output the names of all members, formatted as 'Surname, Firstname'
```
SELECT 
	CONCAT(surname, ', ', firstname) AS name
FROM
	cd.members;
```

###### Question 27: You've noticed that the club's member table has telephone numbers with very inconsistent formatting. You'd like to find all the telephone numbers that contain parentheses, returning the member ID and telephone number sorted by member ID.
```
SELECT memid, telephone
FROM cd.members
WHERE telephone LIKE '%(%'
ORDER BY memid;
```

###### Question 28: You'd like to produce a count of how many members you have whose surname starts with each letter of the alphabet. Sort by the letter, and don't worry about printing out a letter if the count is 0.
```
SELECT LEFT(surname, 1) AS letter, COUNT(*) AS count
FROM cd.members
GROUP BY LEFT(surname, 1)
ORDER BY letter;
```

