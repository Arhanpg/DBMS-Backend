# All 14 preset queries aligned to the actual HK database schema.
# Column names match the CREATE TABLE statements exactly.
# Queries with runtime parameters use %s placeholders (mysql-connector style).

PRESET_QUERIES = [
    {
        "id": 1,
        "label": "(a) Hall manager contacts",
        "description": "Manager name and telephone number for each hall of residence",
        "params": [],
        "sql": """
            SELECT
                h.name          AS hall_name,
                h.manager_name,
                h.phone         AS telephone
            FROM Hall h
            ORDER BY h.name
        """
    },
    {
        "id": 2,
        "label": "(b) Students with lease details",
        "description": "Names and banner numbers of students with their lease agreements",
        "params": [],
        "sql": """
            SELECT
                s.banner_no,
                CONCAT(s.first_name, ' ', s.last_name) AS student_name,
                l.lease_no,
                l.duration,
                l.place_no,
                l.room_no,
                l.start_date,
                l.end_date
            FROM Student s
            JOIN Lease l ON s.banner_no = l.banner_no
            ORDER BY s.last_name
        """
    },
    {
        "id": 3,
        "label": "(c) Summer semester leases",
        "description": "Details of lease agreements that cover the summer semester (Apr-Jul start)",
        "params": [],
        "sql": """
            SELECT
                l.lease_no,
                s.banner_no,
                CONCAT(s.first_name, ' ', s.last_name) AS student_name,
                l.duration,
                l.start_date,
                l.end_date,
                l.place_no,
                l.room_no,
                l.address
            FROM Lease l
            JOIN Student s ON l.banner_no = s.banner_no
            WHERE MONTH(l.start_date) BETWEEN 4 AND 7
            ORDER BY l.start_date
        """
    },
    {
        "id": 4,
        "label": "(d) Total rent paid by a student",
        "description": "Total rent paid by a given student — enter their banner number",
        "params": [
            {"name": "banner_no", "hint": "e.g. S100", "type": "text"}
        ],
        "sql": """
            SELECT
                s.banner_no,
                CONCAT(s.first_name, ' ', s.last_name) AS student_name,
                COUNT(i.invoice_no)                    AS invoices_paid,
                SUM(i.payment_due)                     AS total_rent_paid
            FROM Student s
            JOIN Lease l   ON s.banner_no   = l.banner_no
            JOIN Invoice i ON l.lease_no    = i.lease_no
            WHERE i.payment_date IS NOT NULL
              AND s.banner_no = %s
            GROUP BY s.banner_no, s.first_name, s.last_name
        """
    },
    {
        "id": 5,
        "label": "(e) Unpaid invoices by semester",
        "description": "Students who have not paid their invoices — enter semester (e.g. Sem1, Sem2, Sem3)",
        "params": [
            {"name": "semester", "hint": "e.g. Sem1", "type": "text"}
        ],
        "sql": """
            SELECT
                s.banner_no,
                CONCAT(s.first_name, ' ', s.last_name) AS student_name,
                i.invoice_no,
                i.semester,
                i.payment_due,
                i.payment_date
            FROM Invoice i
            JOIN Lease l   ON i.lease_no  = l.lease_no
            JOIN Student s ON l.banner_no = s.banner_no
            WHERE i.payment_date IS NULL
              AND i.semester = %s
            ORDER BY s.last_name
        """
    },
    {
        "id": 6,
        "label": "(f) Unsatisfactory inspections",
        "description": "Apartment inspections where the property was found to be unsatisfactory",
        "params": [],
        "sql": """
            SELECT
                inspection_id,
                staff_name,
                inspection_date,
                status,
                comments
            FROM Inspection
            WHERE status = 'Fail'
            ORDER BY inspection_date DESC
        """
    },
    {
        "id": 7,
        "label": "(g) Students in a particular hall",
        "description": "Names, banner numbers, room and place numbers for students in a given hall — enter hall name (e.g. Block A)",
        "params": [
            {"name": "hall_name", "hint": "e.g. Block A", "type": "text"}
        ],
        "sql": """
            SELECT
                h.name          AS hall_name,
                s.banner_no,
                CONCAT(s.first_name, ' ', s.last_name) AS student_name,
                hr.room_no,
                hr.place_no
            FROM Student s
            JOIN Lease l     ON s.banner_no  = l.banner_no
            JOIN HallRoom hr ON l.place_no   = hr.place_no
            JOIN Hall h      ON hr.hall_id   = h.hall_id
            WHERE h.name = %s
            ORDER BY s.last_name
        """
    },
    {
        "id": 8,
        "label": "(h) Students on waiting list",
        "description": "All students currently on the waiting list (status = 'waiting')",
        "params": [],
        "sql": """
            SELECT
                banner_no,
                CONCAT(first_name, ' ', last_name) AS student_name,
                email,
                mobile,
                category,
                nationality
            FROM Student
            WHERE status = 'waiting'
            ORDER BY last_name
        """
    },
    {
        "id": 9,
        "label": "(i) Students per category",
        "description": "Total number of students in each student category (UG / PG / NRI / Foreign)",
        "params": [],
        "sql": """
            SELECT
                category,
                COUNT(*) AS total_students
            FROM Student
            GROUP BY category
            ORDER BY total_students DESC
        """
    },
    {
        "id": 10,
        "label": "(j) Students missing next-of-kin",
        "description": "Names and banner numbers for students who have not supplied next-of-kin details",
        "params": [],
        "sql": """
            SELECT
                s.banner_no,
                CONCAT(s.first_name, ' ', s.last_name) AS student_name,
                s.email,
                s.mobile
            FROM Student s
            LEFT JOIN NextOfKin nk ON s.banner_no = nk.banner_no
            WHERE nk.banner_no IS NULL
            ORDER BY s.last_name
        """
    },
    {
        "id": 11,
        "label": "(k) Adviser for a student",
        "description": "Name and phone number of the Adviser for a given student — enter banner number",
        "params": [
            {"name": "banner_no", "hint": "e.g. S100", "type": "text"}
        ],
        "sql": """
            SELECT
                s.banner_no,
                CONCAT(s.first_name, ' ', s.last_name) AS student_name,
                a.full_name     AS adviser_name,
                a.phone         AS adviser_phone,
                a.email         AS adviser_email,
                a.department,
                a.room_no       AS adviser_room
            FROM Student s
            JOIN Adviser a ON s.major = a.department
            WHERE s.banner_no = %s
            LIMIT 1
        """
    },
    {
        "id": 12,
        "label": "(l) Hall room rent statistics",
        "description": "Minimum, maximum and average monthly rent for rooms in residence halls",
        "params": [],
        "sql": """
            SELECT
                MIN(rent)           AS min_rent,
                MAX(rent)           AS max_rent,
                ROUND(AVG(rent), 2) AS avg_rent
            FROM HallRoom
        """
    },
    {
        "id": 13,
        "label": "(m) Total places per hall",
        "description": "Total number of rooms/places in each residence hall",
        "params": [],
        "sql": """
            SELECT
                h.name              AS hall_name,
                COUNT(hr.place_no)  AS total_places
            FROM Hall h
            JOIN HallRoom hr ON h.hall_id = hr.hall_id
            GROUP BY h.hall_id, h.name
            ORDER BY total_places DESC
        """
    },
    {
        "id": 14,
        "label": "(n) Residence staff over 60",
        "description": "Staff ID, name, age and position of all residence staff over 60 years old",
        "params": [],
        "sql": """
            SELECT
                staff_id,
                CONCAT(first_name, ' ', last_name)              AS staff_name,
                TIMESTAMPDIFF(YEAR, dob, CURDATE())             AS age,
                position,
                location
            FROM Staff
            WHERE TIMESTAMPDIFF(YEAR, dob, CURDATE()) > 60
            ORDER BY age DESC
        """
    },
]
