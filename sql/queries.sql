-- MODIFYING DATA
-- 1.
INSERT INTO cd.facilities (facid, name, membercost, guestcost, initialoutlay, monthlymaintenance)
VALUES (9, 'Spa', 20, 30, 100000, 800);

-- 2.
INSERT INTO cd.facilities (facid, name, membercost, guestcost, initialoutlay, monthlymaintenance)
VALUES ((SELECT MAX(facid) + 1 FROM cd.facilities),'Spa', 20, 30, 100000, 800);

-- 3.
UPDATE cd.facilities
SET initialoutlay = 10000
WHERE facid = 1;

-- 4.
UPDATE cd.facilities AS f2
	SET membercost = (SELECT 1.1 * membercost FROM cd.facilities WHERE facid = 0),
		guestcost = (SELECT 1.1 * guestcost FROM cd.facilities WHERE facid = 0)
WHERE facid = 1;

-- 5.
DELETE FROM cd.bookings;

-- 6.
delete from cd.members where memid = 37;

--BASICS
-- 7.
SELECT facid, name, membercost, monthlymaintenance
FROM cd.facilities
WHERE membercost < monthlymaintenance / 50 AND membercost > 0;

-- 8.
SELECT *
FROM cd.facilities
WHERE name LIKE '%Tennis%';

-- 9.
SELECT *
FROM cd.facilities
WHERE facid IN (1, 5);

-- 10.
SELECT memid, surname, firstname, joindate
FROM cd.members
WHERE joindate >= '2012-09-01';

-- 11.
SELECT surname FROM cd.members
UNION
SELECT name FROM cd.facilities;

--JOIN
-- 12.
SELECT b.starttime
FROM cd.bookings AS b
JOIN cd.members AS m ON b.memid = m.memid
WHERE m.firstname = 'David' AND m.surname = 'Farrell';

-- 13.
SELECT b.starttime AS start, f.name
FROM cd.bookings AS b
JOIN cd.facilities AS f ON b.facid = f.facid
WHERE f.name LIKE '%Tennis Court%' AND DATE(b.starttime) = '2012-09-21'
ORDER BY b.starttime;

-- 14.
SELECT m.firstname AS memfname,
       m.surname AS memsname,
	   r.firstname AS recfname,
       r.surname AS recsname
FROM cd.members AS m
LEFT OUTER JOIN cd.members AS r ON m.recommendedby = r.memid
ORDER BY memsname, memfname;

-- 15.
SELECT DISTINCT m.firstname,
       m.surname
FROM cd.members AS m
JOIN cd.members AS r ON m.memid = r.recommendedby
ORDER BY m.surname, m.firstname;

-- 16.
SELECT DISTINCT  mems.firstname || ' ' ||  mems.surname AS member,
	(SELECT recs.firstname || ' ' || recs.surname AS recommender
		FROM cd.members recs
		WHERE recs.memid = mems.recommendedby
	)
	FROM
		cd.members mems
ORDER BY member;

--AGGREGATION
-- 17.
SELECT recommendedby, COUNT(*)
	FROM cd.members
	WHERE recommendedby is not null
	GROUP BY  recommendedby
ORDER BY  recommendedby;

-- 18.
SELECT
    facid,
    SUM(slots) AS "Total Slots"
FROM
    cd.bookings
GROUP BY
    facid
ORDER BY
    facid;

-- 19.
SELECT
    facid,
    SUM(slots) AS "Total Slots"
FROM
    cd.bookings
WHERE
    starttime >= '2012-09-01' AND starttime < '2012-10-01'
GROUP BY
    facid
ORDER BY
    sum(slots);

-- 20.
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

-- 21.
SELECT
    COUNT(DISTINCT memid) AS count
FROM
    cd.bookings;

-- 22.
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

-- 23.
SELECT
	COUNT(*) OVER () AS count,
	m.firstname,
	m.surname
FROM
	cd.members AS m
ORDER BY
	m.joindate;

-- 24.
SELECT
    ROW_NUMBER() OVER (ORDER BY joindate) AS row_number,
    firstname,
    surname
FROM
    cd.members
ORDER BY
    joindate;

-- 25.
WITH TotalSlots AS (
    SELECT facid, SUM(slots) AS total_slots
    FROM cd.bookings
    GROUP BY facid
)
SELECT facid, total_slots
FROM TotalSlots
WHERE total_slots = (SELECT MAX(total_slots) FROM TotalSlots);

--STRING
-- 26.
SELECT
	CONCAT(surname, ', ', firstname) AS name
FROM
	cd.members;

-- 27.
SELECT memid, telephone
FROM cd.members
WHERE telephone LIKE '%(%'
ORDER BY memid;

-- 28.
SELECT LEFT(surname, 1) AS letter, COUNT(*) AS count
FROM cd.members
GROUP BY LEFT(surname, 1)
ORDER BY letter;
